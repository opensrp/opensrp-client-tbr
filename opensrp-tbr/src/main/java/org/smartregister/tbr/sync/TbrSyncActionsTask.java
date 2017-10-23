package org.smartregister.tbr.sync;

import android.content.Context;
import android.content.Intent;

import org.smartregister.service.ActionService;
import org.smartregister.service.HTTPAgent;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.service.PullConfigurableViewsIntentService;
import org.smartregister.view.LockingBackgroundTask;
import org.smartregister.view.ProgressIndicator;

import static org.smartregister.util.Log.logInfo;

/**
 * Created by SGithengi on 10/19/17.
 */

public class TbrSyncActionsTask {

    private final LockingBackgroundTask task;
    private final ActionService actionService;
    private final Context context;
    private final HTTPAgent httpAgent;

    public TbrSyncActionsTask(Context context, ActionService actionService, ProgressIndicator progressIndicator) {
        this.actionService = actionService;
        this.context = context;
        task = new LockingBackgroundTask(progressIndicator);
        httpAgent = TbrApplication.getInstance().getContext().getHttpAgent();
    }

    public void syncFromServer() {
        logInfo("starting syncing From Server");
        startPullConfigurableViewsIntentService(context);
    }

    private void startPullConfigurableViewsIntentService(android.content.Context context) {
        Intent intent = new Intent(context, PullConfigurableViewsIntentService.class);
        context.startService(intent);
    }
}
