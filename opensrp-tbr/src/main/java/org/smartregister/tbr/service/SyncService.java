package org.smartregister.tbr.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.LoginActivity;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.event.SyncEvent;
import org.smartregister.tbr.sync.ECSyncHelper;
import org.smartregister.tbr.sync.TbrClientProcessor;
import org.smartregister.util.Utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import util.NetworkUtils;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static org.smartregister.util.Log.logInfo;

/**
 * Created by samuelgithengi on 12/18/17.
 */

public class SyncService extends Service {

    private static final Object EVENTS_SYNC_PATH = "/rest/event/add";
    private static final int EVENT_PUSH_LIMIT = 25;
    public static final int EVENT_PULL_LIMIT = 25;
    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;
    private Context context;
    private HTTPAgent httpAgent;
    private List<Observable<?>> observables;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandlerThread = new HandlerThread("SyncService.HandlerThread", THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();

        mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());

        context = getBaseContext();
        httpAgent = TbrApplication.getInstance().getContext().getHttpAgent();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mHandlerThread.quit();
    }

    private void handleSync() {
        if (TbrApplication.getInstance().getContext().IsUserLoggedOut()) {
            logInfo("Not updating from server as user is not logged in.");
            return;
        }
        sendSyncStatusBroadcastMessage(FetchStatus.fetchStarted);
        if (!NetworkUtils.isNetworkAvailable()) {
            sendSyncStatusBroadcastMessage(FetchStatus.noConnection);
            return;
        }

        try {
            pushECToServer();
            pullECFromServer();

        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
            sendSyncStatusBroadcastMessage(FetchStatus.fetchedFailed);
        }

    }

    private void pushECToServer() {
        EventClientRepository db = TbrApplication.getInstance().getEventClientRepository();
        boolean keepSyncing = true;

        while (keepSyncing) {
            try {
                Map<String, Object> pendingEvents = db.getUnSyncedEvents(EVENT_PUSH_LIMIT);

                if (pendingEvents.isEmpty()) {
                    return;
                }

                String baseUrl = TbrApplication.getInstance().getContext().configuration().dristhiBaseURL();
                if (baseUrl.endsWith(context.getString(R.string.url_separator))) {
                    baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(context.getString(R.string.url_separator)));
                }
                // create request body
                JSONObject request = new JSONObject();
                if (pendingEvents.containsKey(context.getString(R.string.clients_key))) {
                    request.put(context.getString(R.string.clients_key), pendingEvents.get(context.getString(R.string.clients_key)));
                }
                if (pendingEvents.containsKey(context.getString(R.string.events_key))) {
                    request.put(context.getString(R.string.events_key), pendingEvents.get(context.getString(R.string.events_key)));
                }
                String jsonPayload = request.toString();
                Response<String> response = httpAgent.post(
                        MessageFormat.format("{0}/{1}",
                                baseUrl,
                                EVENTS_SYNC_PATH),
                        jsonPayload);
                if (response.isFailure()) {
                    Log.e(getClass().getName(), "Events sync failed.");
                    return;
                }
                db.markEventsAsSynced(pendingEvents);
                Log.i(getClass().getName(), "Events synced successfully.");
            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage());
            }
        }
    }

    private void pullECFromServer() {
        final ECSyncHelper ecSyncHelper = ECSyncHelper.getInstance(context);
        // Fetch locations
        String locations = Utils.getPreference(context, LoginActivity.PREF_TEAM_LOCATIONS, "");
        if (StringUtils.isBlank(locations)) {
            sendSyncStatusBroadcastMessage(FetchStatus.fetchedFailed);
            stopSelf();
            return;
        }

        Observable.just(locations)
                .observeOn(AndroidSchedulers.from(mHandlerThread.getLooper()))
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull String locations) throws Exception {

                        JSONObject jsonObject = fetchRetry(locations, 0);
                        if (jsonObject == null) {
                            return Observable.just(FetchStatus.fetchedFailed);
                        } else {
                            final String NO_OF_EVENTS = "no_of_events";
                            int eCount = jsonObject.has(NO_OF_EVENTS) ? jsonObject.getInt(NO_OF_EVENTS) : 0;
                            if (eCount < 0) {
                                return Observable.just(FetchStatus.fetchedFailed);
                            } else if (eCount == 0) {
                                return Observable.just(FetchStatus.nothingFetched);
                            } else {
                                Pair<Long, Long> serverVersionPair = ecSyncHelper.getMinMaxServerVersions(jsonObject);
                                long lastServerVersion = serverVersionPair.second - 1;
                                if (eCount < EVENT_PULL_LIMIT) {
                                    lastServerVersion = serverVersionPair.second;
                                }

                                ecSyncHelper.updateLastSyncTimeStamp(lastServerVersion);
                                return Observable.just(new ResponseParcel(jsonObject, serverVersionPair));
                            }
                        }
                    }
                })
                .subscribe(new Consumer<Object>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o != null) {
                            if (o instanceof ResponseParcel) {
                                ResponseParcel responseParcel = (ResponseParcel) o;
                                saveResponseParcel(responseParcel);
                            } else if (o instanceof FetchStatus) {
                                final FetchStatus fetchStatus = (FetchStatus) o;
                                if (observables != null && !observables.isEmpty()) {
                                    Observable.zip(observables, new Function<Object[], Object>() {
                                        @Override
                                        public Object apply(@NonNull Object[] objects) throws Exception {
                                            return FetchStatus.fetched;
                                        }
                                    }).subscribe(new Consumer<Object>() {
                                        @Override
                                        public void accept(Object o) throws Exception {
                                            complete(fetchStatus);
                                        }
                                    });
                                } else {
                                    complete(fetchStatus);
                                }

                            }
                        }
                    }
                });

    }

    private void saveResponseParcel(final ResponseParcel responseParcel) {
        final ECSyncHelper ecSyncHelper = ECSyncHelper.getInstance(context);
        final Observable<FetchStatus> observable = Observable.just(responseParcel)
                .observeOn(AndroidSchedulers.from(mHandlerThread.getLooper()))
                .subscribeOn(Schedulers.io()).
                        flatMap(new Function<ResponseParcel, ObservableSource<FetchStatus>>() {
                            @Override
                            public ObservableSource<FetchStatus> apply(@NonNull ResponseParcel responseParcel) throws Exception {
                                JSONObject jsonObject = responseParcel.getJsonObject();
                                ecSyncHelper.saveAllClientsAndEvents(jsonObject);
                                return Observable.
                                        just(responseParcel.getServerVersionPair())
                                        .observeOn(AndroidSchedulers.from(mHandlerThread.getLooper()))
                                        .subscribeOn(Schedulers.io())
                                        .map(new Function<Pair<Long, Long>, FetchStatus>() {
                                            @Override
                                            public FetchStatus apply(@NonNull Pair<Long, Long> serverVersionPair) throws Exception {
                                                TbrClientProcessor.getInstance(context).processClient(ecSyncHelper.allEvents(serverVersionPair.first - 1, serverVersionPair.second));
                                                return FetchStatus.fetched;
                                            }
                                        });

                            }
                        });

        observable.subscribe(new Consumer<FetchStatus>() {
            @Override
            public void accept(FetchStatus fetchStatus) throws Exception {
                sendSyncStatusBroadcastMessage(FetchStatus.fetched);
                observables.remove(observable);
            }
        });

        observables.add(observable);

        pullECFromServer();

    }

    private JSONObject fetchRetry(String locations, int count) throws Exception {
        // Request spacing
        try {
            final int ONE_SECOND = 1000;
            Thread.sleep(ONE_SECOND);
        } catch (InterruptedException ie) {
            Log.e(getClass().getName(), ie.getMessage());
        }

        try {
            return ECSyncHelper.getInstance(context).fetchAsJsonObject(AllConstants.SyncFilters.FILTER_LOCATION_ID, locations);

        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage(), e);
            if (count >= 2) {
                //TODO Remove
                stopSelf();
                return null;
            } else {
                int newCount = count + 1;
                return fetchRetry(locations, newCount);
            }

        }
    }

    private void complete(FetchStatus fetchStatus) {
        ECSyncHelper.getInstance(context).updateLastCheckTimeStamp(Calendar.getInstance().getTimeInMillis());
        sendSyncStatusBroadcastMessage(fetchStatus);
        stopSelf();
    }

    private void sendSyncStatusBroadcastMessage(FetchStatus fetchStatus) {
        EventBus.getDefault().post(new SyncEvent(fetchStatus));
    }

    // inner classes
    private final class ServiceHandler extends Handler {
        private ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message message) {
            observables = new ArrayList<>();
            handleSync();
        }
    }

    private class ResponseParcel {
        private JSONObject jsonObject;
        private Pair<Long, Long> serverVersionPair;

        private ResponseParcel(JSONObject jsonObject, Pair<Long, Long> serverVersionPair) {
            this.jsonObject = jsonObject;
            this.serverVersionPair = serverVersionPair;
        }

        private JSONObject getJsonObject() {
            return jsonObject;
        }

        private Pair<Long, Long> getServerVersionPair() {
            return serverVersionPair;
        }
    }

}
