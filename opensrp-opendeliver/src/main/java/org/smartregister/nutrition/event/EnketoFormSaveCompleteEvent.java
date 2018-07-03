package org.smartregister.nutrition.event;

/**
 * Created by ndegwamartin on 09/11/2017.
 */

public class EnketoFormSaveCompleteEvent extends BaseEvent {
    private String formName;

    public EnketoFormSaveCompleteEvent(String formName) {
        this.formName = formName;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

}
