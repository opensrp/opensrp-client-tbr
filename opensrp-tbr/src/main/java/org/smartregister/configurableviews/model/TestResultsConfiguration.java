package org.smartregister.configurableviews.model;

/**
 * Created by Imran-PC on 17-May-18.
 */

public class TestResultsConfiguration extends BaseConfiguration{
    private Object resultsConfig;

    private Integer followupOverduePeriod;

    private Integer smearOverduePeriod;

    public Object getResultsConfig() {
        return resultsConfig;
    }

    public void setResultsConfig(Object resultsConfig) {
        this.resultsConfig = resultsConfig;
    }

    public Integer getFollowupOverduePeriod() {
        return followupOverduePeriod;
    }

    public void setFollowupOverduePeriod(Integer followupOverduePeriod) {
        this.followupOverduePeriod = followupOverduePeriod;
    }

    public Integer getSmearOverduePeriod() {
        return smearOverduePeriod;
    }

    public void setSmearOverduePeriod(Integer smearOverduePeriod) {
        this.smearOverduePeriod = smearOverduePeriod;
    }
}