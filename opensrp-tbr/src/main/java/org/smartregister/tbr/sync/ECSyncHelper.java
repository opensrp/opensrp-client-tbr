package org.smartregister.tbr.sync;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.domain.Response;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.service.SyncService;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.smartregister.util.Utils.getPreference;
import static util.TbrConstants.LAST_CHECK_TIMESTAMP;
import static util.TbrConstants.LAST_SYNC_TIMESTAMP;

/**
 * Created by samuelgithengi on 12/19/17.
 */

public class ECSyncHelper {

    public static final String SEARCH_URL = "/rest/event/sync";

    private final EventClientRepository eventClientRepository;
    private final Context context;

    private static ECSyncHelper instance;

    public static ECSyncHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ECSyncHelper(context, TbrApplication.getInstance().getEventClientRepository());
        }
        return instance;
    }

    private ECSyncHelper(Context context, EventClientRepository eventClientRepository) {
        this.context = context;
        this.eventClientRepository = eventClientRepository;
    }

    public JSONObject fetchAsJsonObject(String filter, String filterValue) throws Exception {
        try {
            HTTPAgent httpAgent = TbrApplication.getInstance().getContext().getHttpAgent();
            String baseUrl = TbrApplication.getInstance().getContext().
                    configuration().dristhiBaseURL();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
            }

            Long lastSyncDatetime = getLastSyncTimeStamp();
            Log.i(ECSyncHelper.class.getName(), "LAST SYNC DT :" + new DateTime(lastSyncDatetime));

            String url = baseUrl + SEARCH_URL + "?" + filter + "=" + filterValue + "&serverVersion=" + lastSyncDatetime + "&limit=" + SyncService.EVENT_PULL_LIMIT;
            Log.i(ECSyncHelper.class.getName(), "URL: " + url);

            if (httpAgent == null) {
                throw new SyncException(url + " http agent is null");
            }

            Response resp = httpAgent.fetch(url);
            if (resp.isFailure()) {
                throw new SyncException(url + " not returned data");
            }

            return new JSONObject((String) resp.payload());
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
            throw new SyncException(SEARCH_URL + " threw exception", e);
        }
    }

    public boolean saveAllClientsAndEvents(JSONObject jsonObject) {
        try {
            if (jsonObject == null) {
                return false;
            }

            JSONArray events = jsonObject.has("events") ? jsonObject.getJSONArray("events") : new JSONArray();
            JSONArray clients = jsonObject.has("clients") ? jsonObject.getJSONArray("clients") : new JSONArray();

            batchSave(events, clients);


            return true;
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
            return false;
        }
    }

    public List<JSONObject> allEvents(long startSyncTimeStamp, long lastSyncTimeStamp) {
        try {
            return eventClientRepository.getEvents(startSyncTimeStamp, lastSyncTimeStamp);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return new ArrayList<>();
    }

    public Pair<Long, Long> getMinMaxServerVersions(JSONObject jsonObject) {
        final String EVENTS = "events";
        final String SERVER_VERSION = "serverVersion";
        try {
            if (jsonObject != null && jsonObject.has(EVENTS)) {
                JSONArray events = jsonObject.getJSONArray(EVENTS);

                long maxServerVersion = Long.MIN_VALUE;
                long minServerVersion = Long.MAX_VALUE;

                for (int i = 0; i < events.length(); i++) {
                    Object o = events.get(i);
                    if (o instanceof JSONObject) {
                        JSONObject jo = (JSONObject) o;
                        if (jo.has(SERVER_VERSION)) {
                            long serverVersion = jo.getLong(SERVER_VERSION);
                            if (serverVersion > maxServerVersion) {
                                maxServerVersion = serverVersion;
                            }

                            if (serverVersion < minServerVersion) {
                                minServerVersion = serverVersion;
                            }
                        }
                    }
                }
                return Pair.create(minServerVersion, maxServerVersion);
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
        }
        return Pair.create(0L, 0L);
    }

    public long getLastSyncTimeStamp() {
        return Long.parseLong(getPreference(context, LAST_SYNC_TIMESTAMP, "0"));
    }

    public void updateLastSyncTimeStamp(long lastSyncTimeStamp) {
        Utils.writePreference(context, LAST_SYNC_TIMESTAMP, lastSyncTimeStamp + "");
    }

    public long getLastCheckTimeStamp() {
        return Long.parseLong(getPreference(context, LAST_CHECK_TIMESTAMP, "0"));
    }

    public void updateLastCheckTimeStamp(long lastSyncTimeStamp) {
        Utils.writePreference(context, LAST_CHECK_TIMESTAMP, lastSyncTimeStamp + "");
    }

    public void batchSave(JSONArray events, JSONArray clients) throws Exception {
        eventClientRepository.batchInsertClients(clients);
        eventClientRepository.batchInsertEvents(events, getLastSyncTimeStamp());
    }

    public static void main(String[] args) {
        String[] locationIds = "".split(",");
        System.out.println("locations:" + locationIds[0]);
    }

    private class SyncException extends Exception {
        public SyncException(String s) {
            Log.e(getClass().getName(), s);
        }

        public SyncException(String s, Throwable e) {
            Log.e(getClass().getName(), "SyncException: " + s, e);
        }
    }
}
