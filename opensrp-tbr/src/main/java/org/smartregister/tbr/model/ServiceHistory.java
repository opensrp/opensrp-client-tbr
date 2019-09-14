package org.smartregister.tbr.model;

/**
 * Created by ndegwamartin on 20/11/2017.
 */

public class ServiceHistory {
    private String date;
    private String formName;

    public ServiceHistory(String date, String formName) {
        this.date = date;
        this.formName = formName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }
}
