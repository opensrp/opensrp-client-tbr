package org.smartregister.tbr.jsonspec.model;

public abstract class BaseConfiguration {

    private String language;

    private String applicationName;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

}
