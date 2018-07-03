package org.smartregister.configurableviews.model;

import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 13/10/2017.
 */

public class ViewConfiguration {

    private String identifier;

    private BaseConfiguration metadata;

    private List<View> views;

    private Map<String, String> labels;

    private Map jsonView;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public BaseConfiguration getMetadata() {
        return metadata;
    }

    public void setMetadata(BaseConfiguration metadata) {
        this.metadata = metadata;
    }

    public List<View> getViews() {
        return views;
    }

    public void setViews(List<View> views) {
        this.views = views;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public Map getJsonView() {
        return jsonView;
    }

    public void setJsonView(Map jsonView) {
        this.jsonView = jsonView;
    }


}
