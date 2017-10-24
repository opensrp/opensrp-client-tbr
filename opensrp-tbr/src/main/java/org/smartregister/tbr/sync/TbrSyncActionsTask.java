package org.smartregister.tbr.sync;

import android.content.Context;
import android.content.Intent;

import org.smartregister.tbr.service.PullConfigurableViewsIntentService;

import static org.smartregister.util.Log.logInfo;

/**
 * Created by SGithengi on 10/19/17.
 */

public class TbrSyncActionsTask {

    private final Context context;

    public TbrSyncActionsTask(Context context) {
        this.context = context;
    }

    public void syncFromServer() {
        logInfo("starting syncing From Server");
        startPullConfigurableViewsIntentService();
    }

    private void startPullConfigurableViewsIntentService() {
        Intent intent = new Intent(context, PullConfigurableViewsIntentService.class);
        context.startService(intent);
    }
}
