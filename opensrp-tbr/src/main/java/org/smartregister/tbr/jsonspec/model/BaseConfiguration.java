package org.smartregister.tbr.jsonspec.model;

public abstract class BaseConfiguration {

    private String language;

    private String applicationName;

    private boolean enableJsonViews;

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

    public boolean isEnableJsonViews() {
        return enableJsonViews;
    }

    public void setEnableJsonViews(boolean enableJsonViews) {
        this.enableJsonViews = enableJsonViews;
    }

}
