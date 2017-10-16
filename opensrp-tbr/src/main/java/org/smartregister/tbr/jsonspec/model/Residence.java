package org.smartregister.tbr.jsonspec.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ndegwamartin on 13/10/2017.
 */

public class Residence {

    private String parent;

    private int position;

    @SerializedName("layout_weight")
    private String layoutWeight;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
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
}
