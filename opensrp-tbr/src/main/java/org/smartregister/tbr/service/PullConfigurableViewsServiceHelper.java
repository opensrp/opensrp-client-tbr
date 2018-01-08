package org.smartregister.tbr.service;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.domain.Response;
import org.smartregister.service.HTTPAgent;
import org.smartregister.tbr.repository.ConfigurableViewsRepository;
import org.smartregister.tbr.sync.ECSyncHelper;

import static org.smartregister.tbr.service.PullConfigurableViewsIntentService.VIEWS_URL;
import static org.smartregister.util.Log.logError;

/**
 * Created by samuelgithengi on 10/27/17.
 */

public class PullConfigurableViewsServiceHelper {
    private static final String TAG = PullConfigurableViewsServiceHelper.class.getCanonicalName();

    private Context applicationContext;
    private ConfigurableViewsRepository configurableViewsRepository;
    private HTTPAgent httpAgent;
    private String baseUrl;
    private ECSyncHelper syncHelper;

    public PullConfigurableViewsServiceHelper(Context applicationContext, ConfigurableViewsRepository configurableViewsRepository, HTTPAgent httpAgent, String baseUrl, ECSyncHelper syncHelper) {
        this.applicationContext = applicationContext;
        this.configurableViewsRepository = configurableViewsRepository;
        this.httpAgent = httpAgent;
        this.baseUrl = baseUrl;
        this.syncHelper = syncHelper;
    }

    protected int processIntent() throws Exception {
        JSONArray views = fetchConfigurableViews();
        if (views != null && views.length() > 0) {
            long lastSyncTimeStamp = configurableViewsRepository.saveConfigurableViews(views);
            syncHelper.updateLastViewsSyncTimeStamp(lastSyncTimeStamp);
        }
        return views == null ? 0 : views.length();
    }

    private JSONArray fetchConfigurableViews() throws JSONException {
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = baseUrl + VIEWS_URL + "?serverVersion=" + ECSyncHelper.getInstance(applicationContext).getLastViewsSyncTimeStamp();
        Log.i(TAG, "URL: " + url);

        if (httpAgent == null) {
            logError(url + " http agent is null");
            return null;
        }

        Response resp = httpAgent.fetch(url);

        if (resp.isFailure()) {
            logError(url + " not returned data");
            return null;
        }
        return new JSONArray((String) resp.payload());
    }

}
