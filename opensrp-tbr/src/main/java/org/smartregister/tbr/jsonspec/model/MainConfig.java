package org.smartregister.tbr.jsonspec.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ndegwamartin on 12/10/2017.
 */

public class MainConfig {

    @SerializedName("server_version")
    private String serverVersion;
    private String language;

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
