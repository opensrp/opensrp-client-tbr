package org.smartregister.tbr.model;

/**
 * Created by ndegwamartin on 22/12/2017.
 */

public class ScreenContact {

    private String stage;
    private String name;
    private String gender;
    private String tbreachId;
    private boolean isNegative = false;

    public boolean isNegative() {
        return isNegative;
    }

    public void setNegative(boolean negative) {
        isNegative = negative;
    }

    public ScreenContact(String tbreachId, String name, String gender, String stage, boolean isNegative) {

        this.stage = stage;
        this.name = name;
        this.gender = gender;
        this.tbreachId = tbreachId;
        this.isNegative = isNegative;

    }

    public String getTbreachId() {
        return tbreachId;
    }

    public void setTbreachId(String tbreachId) {
        this.tbreachId = tbreachId;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
