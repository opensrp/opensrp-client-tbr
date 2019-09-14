package org.smartregister.configurableviews.model;

import java.util.List;

/**
 * Created by ndegwamartin on 12/10/2017.
 */

public class HomeConfig extends BaseConfiguration {
    private List<View> views;

    public List<View> getViews() {
        return views;
    }

    public void setViews(List<View> views) {
        this.views = views;
    }
}
