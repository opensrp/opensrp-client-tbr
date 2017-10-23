package org.smartregister.tbr.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.smartregister.domain.Response;
import org.smartregister.service.HTTPAgent;
import org.smartregister.tbr.activity.LoginActivity;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.repository.ConfigurableViewsRepository;
import org.smartregister.util.Utils;

import static util.TbrConstants.LAST_SYNC_TIMESTAMP;

/**
 * Created by SGithengi on 19/10/2017.
 * An {@link IntentService} subclass for handling asynchronous tasks for fetching the user configurable views
 * <p>
 */
public class PullConfigurableViewsIntentService extends IntentService {
    public static final String VIEWS_URL = "/rest/viewconfiguration/sync";

    private static final String TAG = PullConfigurableViewsIntentService.class.getCanonicalName();

    private ConfigurableViewsRepository configurableViewsRepository;

    public PullConfigurableViewsIntentService() {
        super("PullConfigurableViewsIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                JSONArray views = fetchConfigurableViews();
                long lastSyncTimeStamp = configurableViewsRepository.saveConfigurableViews(views);
                updateLastSyncTimeStamp(lastSyncTimeStamp);
                Intent refreshLoginIntentFilter = new Intent();
                refreshLoginIntentFilter.setAction(LoginActivity.REFRESH_LOGIN_ACTION);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(refreshLoginIntentFilter);
            } catch (Exception e1) {
                Log.e(TAG, "Error fetching configurable views from server", e1);
            }

        }
    }

    private JSONArray fetchConfigurableViews() throws Exception {
        HTTPAgent httpAgent = TbrApplication.getInstance().getContext().getHttpAgent();
        String baseUrl = TbrApplication.getInstance().getContext().
                configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = baseUrl + VIEWS_URL + "?serverVersion=" + getLastSyncTimeStamp();
        Log.i(TAG, "URL: " + url);

        if (httpAgent == null) {
            throw new Exception(url + " http agent is null");
        }

        Response resp = httpAgent.fetch(url);

        if (resp.isFailure()) {
            throw new Exception(url + " not returned data");
        }
        return new JSONArray((String) resp.payload());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        configurableViewsRepository = TbrApplication.getInstance().getConfigurableViewsRepository();
    }

    public long getLastSyncTimeStamp() {
        return Long.parseLong(Utils.getPreference(getApplicationContext(), LAST_SYNC_TIMESTAMP, "0"));
    }

    private void updateLastSyncTimeStamp(long lastSyncTimeStamp) {
        Utils.writePreference(getApplicationContext(), LAST_SYNC_TIMESTAMP, lastSyncTimeStamp + "");
    }

}
