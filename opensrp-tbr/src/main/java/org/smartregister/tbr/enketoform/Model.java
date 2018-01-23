package org.smartregister.tbr.enketoform;

/**
 * Created by samuelgithengi on 1/23/18.
 */

public class Model {

    private String tag;

    private String openMRSEntity;

    private String openMRSEntityId;

    public String getTag() {
        return tag;
    }

    public Model(String tag, String openMRSEntity, String openMRSEntityId) {
        this.tag = tag;
        this.openMRSEntity = openMRSEntity;
        this.openMRSEntityId = openMRSEntityId;
    }

    public String getOpenMRSEntity() {
        return openMRSEntity;
    }

    public String getOpenMRSEntityId() {
        return openMRSEntityId;
    }
}
