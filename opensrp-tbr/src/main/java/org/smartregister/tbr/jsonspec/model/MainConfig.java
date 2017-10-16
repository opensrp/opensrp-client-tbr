package org.smartregister.tbr.jsonspec.model;


import com.google.gson.annotations.SerializedName;

/**
 * Created by ndegwamartin on 12/10/2017.
 */

public class MainConfig {

    @SerializedName("server_version")
    private String serverVersion;
    private String language;
    @SerializedName("app_name")
    private String appName;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
