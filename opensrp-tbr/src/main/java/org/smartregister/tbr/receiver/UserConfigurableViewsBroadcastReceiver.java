package org.smartregister.tbr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.smartregister.tbr.sync.UserConfigurableViewsSyncTask;

/**
 * Created by SGithengi on 10/23/17.
 */
public class UserConfigurableViewsBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent alarmIntent) {
        UserConfigurableViewsSyncTask pathUpdateActionsTask = new UserConfigurableViewsSyncTask(context);

        pathUpdateActionsTask.syncFromServer();

    }


}
