package org.smartregister.nutrition.event;

/**
 * Created by ndegwamartin on 09/11/2017.
 */

public class RefreshPatientDetailsEvent extends BaseEvent {
    private String viewIdentifier;

    public String getViewIdentifier() {
        return viewIdentifier;
    }

    public void setViewIdentifier(String viewIdentifier) {
        this.viewIdentifier = viewIdentifier;
    }

    public RefreshPatientDetailsEvent(String viewIdentifier) {
        this.viewIdentifier = viewIdentifier;

    }
}
