package org.smartregister.tbr.model;

import java.util.List;

/**
 * Created by ndegwamartin on 26/01/2018.
 */

public class BMIRecordWrapper {

    private List<BMIRecord> bmiRecords;
    private String recordType;

    public BMIRecordWrapper(String recordType, List<BMIRecord> bmiRecords) {
        this.recordType = recordType;
        this.bmiRecords = bmiRecords;
    }

    public List<BMIRecord> getBmiRecords() {
        return bmiRecords;
    }

    public void setBmiRecords(List<BMIRecord> bmiRecords) {
        this.bmiRecords = bmiRecords;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }


    public static class BMIRecordsTYPE {
        public static String BMIS = "bmis";
        public static String WEIGHTS = "weights";
    }

}
