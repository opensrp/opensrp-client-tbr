package org.smartregister.configurableviews.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.smartregister.Context;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.PreferenceHelper;
import org.smartregister.configurableviews.util.Constants;
import org.smartregister.configurableviews.util.Utils;

import java.util.Calendar;

import static org.smartregister.util.Log.logError;

/**
 * Created by SGithengi on 19/10/2017.
 * An {@link IntentService} subclass for handling asynchronous tasks for fetching the user configurable views
 * <p>
 */
public class PullConfigurableViewsIntentService extends IntentService {
    public static final String VIEWS_URL = "/rest/viewconfiguration/sync";

    private static final String TAG = PullConfigurableViewsIntentService.class.getCanonicalName();

    protected PullConfigurableViewsServiceHelper pullConfigurableViewsServiceHelper;

    public static final String EVENT_SYNC_COMPLETE = "event_sync_complete";

    public PullConfigurableViewsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {


                Intent broadCastIntent = new Intent(PullConfigurableViewsIntentService.EVENT_SYNC_COMPLETE); //broadcast useful meta data

                int count = pullConfigurableViewsServiceHelper.processIntent();
                if (count > 0) {
                    intent.putExtra(Constants.INTENT_KEY.SYNC_TOTAL_RECORDS, count);
                }

                //Broadcast view configuration Sync event

                //update last sync time
                String lastSyncTime = Utils.formatDate(Calendar.getInstance().getTime(), "MMM dd HH:mm");
                Utils.writePrefString(this, Constants.INTENT_KEY.LAST_SYNC_TIME_STRING, lastSyncTime);

                broadCastIntent.putExtra(Constants.INTENT_KEY.LAST_SYNC_TIME_STRING, lastSyncTime);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadCastIntent);

            } catch (Exception e) {
                logError(TAG + " Error fetching configurable Views");
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = ConfigurableViewsLibrary.getInstance().getContext();
        pullConfigurableViewsServiceHelper = new PullConfigurableViewsServiceHelper(getApplicationContext(),
                ConfigurableViewsLibrary.getInstance().getConfigurableViewsRepository(), context.getHttpAgent(),
                context.configuration().dristhiBaseURL(), PreferenceHelper.getInstance(getApplicationContext()),
                ConfigurableViewsLibrary.getInstance().getPassword() != null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
