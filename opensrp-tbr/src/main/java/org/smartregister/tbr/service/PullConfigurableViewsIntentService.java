package org.smartregister.tbr.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.smartregister.domain.Response;
import org.smartregister.service.HTTPAgent;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.repository.ConfigurableViewsRepository;

/**
 * Created by SGithengi on 19/10/2017.
 * An {@link IntentService} subclass for handling asynchronous tasks for fetching the user configurable views
 * <p>
 */
public class PullConfigurableViewsIntentService extends IntentService {
    public static final String VIEWS_URL = "/rest/viewconfiguration/list";

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
                configurableViewsRepository.saveConfigurableViews(views);
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

        String url = baseUrl + VIEWS_URL;
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        configurableViewsRepository = TbrApplication.getInstance().getConfigurableViewsRepository();
        return super.onStartCommand(intent, flags, startId);
    }

}
