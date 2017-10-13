package org.smartregister.tbr.jsonspec.model;

import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 13/10/2017.
 */

public class ViewConfiguration {

    private String organization;

    private String type;

    private String identifier;

    private Map<String, String> metadata;

    private List<View> views;

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public List<View> getViews() {
        return views;
    }

    public void setViews(List<View> views) {
        this.views = views;
    }
}
