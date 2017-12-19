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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.event.SyncEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Map;

import util.NetworkUtils;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static org.smartregister.util.Log.logInfo;

/**
 * Created by samuelgithengi on 12/18/17.
 */

public class SyncService extends Service {

    private static final int EVENT_PUSH_LIMIT = 50;
    private static final Object EVENTS_SYNC_PATH = "/rest/event/add";
    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;
    private Context context;
    private HTTPAgent httpAgent;
    private ArrayList<Object> observables;

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
        EventBus.getDefault().post(new SyncEvent(FetchStatus.fetchStarted));

        if (!NetworkUtils.isNetworkAvailable()) {
            EventBus.getDefault().post(new SyncEvent(FetchStatus.noConnection));
            return;
        }

        try {
            pushECToServer();
            pullECFromServer();
            //TODO Remove this
            stopSelf();


        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
            EventBus.getDefault().post(new SyncEvent(FetchStatus.fetchedFailed));
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
