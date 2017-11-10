package org.smartregister.tbr.event;

/**
 * Created by ndegwamartin on 09/11/2017.
 */

public class TriggerViewConfigurationSyncEvent {
    private boolean isManualSync = false;

    public boolean isManualSync() {
        return isManualSync;
    }

    public void setManualSync(boolean manualSync) {
        isManualSync = manualSync;
    }
}
