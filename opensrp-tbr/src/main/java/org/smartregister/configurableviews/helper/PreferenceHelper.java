package org.smartregister.configurableviews.helper;

import android.content.Context;

import org.smartregister.configurableviews.util.Constants;
import org.smartregister.util.Utils;

import static org.smartregister.configurableviews.util.Constants.LAST_VIEWS_SYNC_TIMESTAMP;
import static org.smartregister.configurableviews.util.Constants.VIEW_CONFIGURATION_PREFIX;
import static org.smartregister.util.Utils.getPreference;

/**
 * Created by ndegwamartin on 21/02/2018.
 */

public class PreferenceHelper implements PrefsHelper {

    private final Context context;

    private static PreferenceHelper instance;

    public static PreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceHelper(context);
        }
        return instance;
    }

    private PreferenceHelper(Context context) {
        this.context = context;
    }

    public long getLastViewsSyncTimeStamp() {
        return Long.parseLong(getPreference(context, LAST_VIEWS_SYNC_TIMESTAMP, "0"));
    }

    public void updateLastViewsSyncTimeStamp(long lastSyncTimeStamp) {
        Utils.writePreference(context, LAST_VIEWS_SYNC_TIMESTAMP, lastSyncTimeStamp + "");
    }


    public void updateLoginConfigurableViewPreference(String loginJson) {
        Utils.writePreference(context, VIEW_CONFIGURATION_PREFIX + Constants.CONFIGURATION.LOGIN, loginJson);
    }


}
