package org.smartregister.tbr.helper;

import util.TbrConstants;

/**
 * Created by ndegwamartin on 28/01/2018.
 */

public class DBQueryHelper {

    public static final String getPresumptivePatientRegisterCondition() {
        return " " + TbrConstants.KEY.PRESUMPTIVE + " =\"yes\" AND " + TbrConstants.KEY.CONFIRMED_TB + " IS NULL AND " + TbrConstants.KEY.DATE_REMOVED + " =\"\" ";
    }

    public static final String getPositivePatientRegisterCondition() {
        return " " + TbrConstants.KEY.CONFIRMED_TB + " = \"yes\" AND " + TbrConstants.KEY.TREATMENT_INITIATION_DATE + " IS NULL AND " + TbrConstants.KEY.DATE_REMOVED + " =\"\" ";
    }

    public static final String getIntreatmentPatientRegisterCondition() {
        return TbrConstants.KEY.TREATMENT_INITIATION_DATE + " IS NOT NULL AND " + TbrConstants.KEY.DATE_REMOVED + " =\"\" ";
    }
}
