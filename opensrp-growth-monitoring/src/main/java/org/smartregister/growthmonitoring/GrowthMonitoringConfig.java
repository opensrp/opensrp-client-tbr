package org.smartregister.growthmonitoring;

/**
 * Created by ndegwamartin on 2019-05-28.
 */
public class GrowthMonitoringConfig {

    private String maleWeightAgeZScoreFile;
    private String femaleWeightZScoreFile;
    private String maleHeightAgeZScoreFile;
    private String femaleHeightZScoreFile;
    private String maleWeightForHeightZScoreFile;
    private String femaleWeightForHeightZScoreFile;
    private String genderNeutralZScoreFile;

    public String getMaleWeightForAgeZScoreFile() {
        if (maleWeightAgeZScoreFile != null) {
            return maleWeightAgeZScoreFile;
        } else {
            return "zscores_male_weight_age.csv";
        }
    }

    public void setMaleWeightForAgeZScoreFile(String maleZScoreFile) {
        this.maleWeightAgeZScoreFile = maleZScoreFile;
    }

    public String getFemaleWeightForAgeZScoreFile() {
        if (femaleWeightZScoreFile != null) {
            return femaleWeightZScoreFile;
        } else {
            return "zscores_female_weight_age.csv";
        }
    }

    public void setFemaleWeightForAgeZScoreFile(String femaleZScoreFile) {
        this.femaleWeightZScoreFile = femaleZScoreFile;
    }

    public String getMaleHeightForAgeZScoreFile() {
        if (maleHeightAgeZScoreFile != null) {
            return maleHeightAgeZScoreFile;
        } else {
            return "zscores_male_height_age.csv";
        }
    }

    public void setMaleHeightForAgeZScoreFile(String maleZScoreFile) {
        this.maleHeightAgeZScoreFile = maleZScoreFile;
    }

    public String getFemaleHeightForAgeZScoreFile() {
        if (femaleHeightZScoreFile != null) {
            return femaleHeightZScoreFile;
        } else {
            return "zscores_female_height_age.csv";
        }
    }

    public void setFemaleHeightForAgeZScoreFile(String femaleZScoreFile) {
        this.femaleHeightZScoreFile = femaleZScoreFile;
    }

    public String getMaleWeightForHeightZScoreFile() {
        if (maleWeightForHeightZScoreFile != null) {
            return maleWeightForHeightZScoreFile;
        } else {
            return "zscores_male_weight_height.csv";
        }
    }

    public void setMaleWeightForHeightZScoreFile(String maleZScoreFile) {
        this.maleWeightForHeightZScoreFile = maleZScoreFile;
    }

    public String getFemaleWeightForHeightZScoreFile() {
        if (femaleWeightForHeightZScoreFile != null) {
            return femaleWeightForHeightZScoreFile;
        } else {
            return "zscores_female_weight_height.csv";
        }
    }

    public void setFemaleWeightForHeightZScoreFile(String femaleZScoreFile) {
        this.femaleWeightForHeightZScoreFile = femaleZScoreFile;
    }



    public String getGenderNeutralZScoreFile() {
        return genderNeutralZScoreFile;
    }

    public void setGenderNeutralZScoreFile(String genderNeutralZScoreFile) {
        this.genderNeutralZScoreFile = genderNeutralZScoreFile;
    }
}
