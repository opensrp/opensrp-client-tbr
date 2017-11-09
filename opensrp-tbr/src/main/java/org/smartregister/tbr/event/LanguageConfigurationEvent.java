package org.smartregister.tbr.event;

/**
 * Created by ndegwamartin on 09/11/2017.
 */

public class LanguageConfigurationEvent {
    private boolean isFromServer = false;

    public LanguageConfigurationEvent(boolean isFromServer) {
        this.isFromServer = isFromServer;
    }

    public boolean isFromServer() {
        return isFromServer;
    }

    public void setFromServer(boolean fromServer) {
        isFromServer = fromServer;
    }
}
