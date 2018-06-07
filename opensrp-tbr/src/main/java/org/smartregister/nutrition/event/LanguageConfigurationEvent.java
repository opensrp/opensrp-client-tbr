package org.smartregister.nutrition.event;

/**
 * Created by ndegwamartin on 09/11/2017.
 */

public class LanguageConfigurationEvent extends BaseEvent {

    private boolean isFromServer = false;
    private boolean hasMainConfigUpdate = false;

    public boolean isHasMainConfigUpdate() {
        return hasMainConfigUpdate;
    }

    public void setHasMainConfigUpdate(boolean hasMainConfigUpdate) {
        this.hasMainConfigUpdate = hasMainConfigUpdate;
    }

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
