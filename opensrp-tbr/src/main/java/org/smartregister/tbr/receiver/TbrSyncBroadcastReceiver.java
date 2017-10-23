package org.smartregister.tbr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.smartregister.sync.SyncProgressIndicator;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.sync.TbrSyncActionsTask;

/**
 * Created by SGithengi on 10/23/17.
 */
public class TbrSyncBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent alarmIntent) {
        TbrSyncActionsTask pathUpdateActionsTask = new TbrSyncActionsTask(
                context,
                TbrApplication.getInstance().getContext().actionService(),
                new SyncProgressIndicator());

        pathUpdateActionsTask.syncFromServer();

    }


}
