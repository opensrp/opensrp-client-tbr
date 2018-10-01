package org.smartregister.configurableviews.helper;

/**
 * Created by ndegwamartin on 09/03/2018.
 */

public interface PrefsHelper {

    long getLastViewsSyncTimeStamp();

    void updateLastViewsSyncTimeStamp(long lastSyncTimeStamp);

    void updateLoginConfigurableViewPreference(String loginJson);
}
