package org.smartregister.tbr.util;

/**
 * Created by ndegwamartin on 13/10/2017.
 */

public class Constants {
    public static final String PTB = "ptb";
    public static final String EPTB = "eptb";
    public static final String PULMONARY = "Pulmonary";
    public static final String EXTRA_PULMONARY = "Extra Pulmonary";

    public static class INTENT_KEY {
        public static final String FULL_NAME = "full_name";
        public static final String IS_REMOTE_LOGIN = "is_remote_login";
        public static final String REGISTER_TITLE = "register_title";
        public static final String PATIENT_DETAIL_MAP = "patient_detail_map";
        public static final String LAST_SYNC_TIME_STRING = "last_manual_sync_time_string";
        public static final String TB_REACH_ID = "tb_reach_id";
    }

    public static class CHAR {
        public static final String SPACE = " ";
        public static final String HASH = "#";
        public static final String NO_CHAR = "";
        public static final String COLON = ":";
        public static final String UNDERSCORE = "_";
    }

    public static class CONFIGURATION {
        public static final String LOGIN = "login";
        public static final String HOME = "home";
        public static final String MAIN = "main";
        public static final String LANG = "lang";
        public static final String PRESUMPTIVE_PATIENT_DETAILS = "presumptive_patient_details";
        public static final String POSITIVE_PATIENT_DETAILS = "positive_patient_details";
        public static final String INTREATMENT_PATIENT_DETAILS = "intreatment_patient_details";

        public static class COMPONENTS {
            public static final String PATIENT_DETAILS_DEMOGRAPHICS = "component_patient_details_demographics";
            public static final String PATIENT_DETAILS_POSITIVE = "component_patient_details_positive";
            public static final String PATIENT_DETAILS_SERVICE_HISTORY = "component_patient_details_service_history";
            public static final String PATIENT_DETAILS_CONTACT_SCREENING = "component_patient_details_contact_screening";
            public static final String PATIENT_DETAILS_FOLLOWUP = "component_patient_details_followup";
        }
    }

    public static final class GENDER {

        public static final String MALE = "male";
        public static final String FEMALE = "female";
        public static final String TRANSGENDER = "transgender";
    }

    public static final class KEY {
        public static final String _ID = "_id";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String FIRST_ENCOUNTER = "first_encounter";
        public static final String DATE = "date";
        public static final String TBREACH_ID = "tbreach_id";
        public static final String DOB = "dob";
        public static final String GENDER = "gender";
        public static final String PARTICIPANT_ID = "participant_id";
        public static final String NEXT_VISIT_DATE = "next_visit_date";
        public static final String PATIENT_TYPE = "patient_type";
        public static final String SITE_OF_DISEASE = "site_of_disease";
        public static final String TREATMENT_INITIATION_DATE = "treatment_initiation_date";
    }

    public static final class FORM {
        public static final String NEW_PATIENT_REGISTRATION = "add_presumptive_patient";
        public static final String RESULT_SMEAR = "result_smear";
        public static final String RESULT_CHEST_XRAY = "result_chest_xray";
        public static final String RESULT_CULTURE = "result_culture";
        public static final String DIAGNOSIS = "diagnosis";
        public static final String RESULT_GENE_EXPERT = "result_gene_xpert";
        public static final String CONTACT_SCREENING = "contact_screening";
        public static final String REMOVE_PATIENT = "remove_patient";
        public static final String TREATMENT_OUTCOME = "treatment_outcome";
    }

    public enum ScreenStage {
        NOT_SCREENED, PRESUMPTIVE, SCREENED, POSITIVE, IN_TREATMENT;
    }

    public static final class TEST_RESULT {
        public static final class XPERT {
            public static final String DETECTED = "detected";
            public static final String NOT_DETECTED = "not_detected";
            public static final String INDETERMINATE = "indeterminate";
            public static final String ERROR = "error";
            public static final String NO_RESULT = "no_result";
        }
    }

    public static final class RESULT {

        public static final String ERROR_CODE = "error_code";
        public static final String POSITIVE = "positive";
    }
}
