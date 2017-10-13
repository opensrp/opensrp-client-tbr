package org.smartregister.tbr.jsonspec.model;

import java.util.Map;

/**
 * Created by ndegwamartin on 13/10/2017.
 */

public class View {


    private String parent;

    private String type;

    private String orientation;

    private int position;

    private String layoutWeight;

    private boolean visible;

    private String label;

    private Map<String, String> metadata;

    private String identifier;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getLayoutWeight() {
        return layoutWeight;
    }

    public void setLayoutWeight(String layoutWeight) {
        this.layoutWeight = layoutWeight;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}