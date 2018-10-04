package org.smartregister.tbr.helper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by ndegwamartin on 20/03/2018.
 */

public class DBQueryHelperTest {

    @Test
    public void getPresumptivePatientRegisterConditionReturnsCorrectSqlFilter() {
        String expectedFilter = " presumptive =\"yes\" AND confirmed_tb IS NULL AND date_removed =\"\" ";
        DBQueryHelper dbQueryHelper = new DBQueryHelper();
        assertEquals(expectedFilter, dbQueryHelper.getPresumptivePatientRegisterCondition());
    }

    @Test
    public void getPositivePatientRegisterConditionReturnsCorrectSqlFilter() {
        String expectedFilter = " confirmed_tb = \"yes\" AND treatment_initiation_date IS NULL AND date_removed =\"\" ";
        DBQueryHelper dbQueryHelper = new DBQueryHelper();
        assertEquals(expectedFilter, dbQueryHelper.getPositivePatientRegisterCondition());
    }

    @Test
    public void getIntreatmentPatientRegisterConditionReturnsCorrectSqlFilter() {
        String expectedFilter = "treatment_initiation_date IS NOT NULL AND date_removed =\"\" ";
        DBQueryHelper dbQueryHelper = new DBQueryHelper();
        assertEquals(expectedFilter, dbQueryHelper.getIntreatmentPatientRegisterCondition());
    }


}
