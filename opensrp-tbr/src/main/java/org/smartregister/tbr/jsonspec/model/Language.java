package org.smartregister.tbr.jsonspec.model;

import java.util.Map;

/**
 * Created by ndegwamartin on 13/10/2017.
 */

public class Language {
    private Map<String, String> labels;

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }
}
