package util;

import org.smartregister.AllConstants;
import org.smartregister.tbr.BuildConfig;

/**
 * Created by SGithengi on 09/10/17.
 */
public class
TbrConstants extends AllConstants {

    public static final long MAX_SERVER_TIME_DIFFERENCE = BuildConfig.MAX_SERVER_TIME_DIFFERENCE;
    public static final boolean TIME_CHECK = BuildConfig.TIME_CHECK;
    public static final String LAST_SYNC_TIMESTAMP = "LAST_SYNC_TIMESTAMP";

    public static final String PATIENT_TABLE_NAME = "ec_patient";

    public static final class KEY {
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String DOB = "dob";
        public static final String TBREACH_ID = "tbreach_id";
        public static final String GENDER = "gender";
    }
}