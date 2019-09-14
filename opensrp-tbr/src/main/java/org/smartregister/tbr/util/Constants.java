package org.smartregister.tbr.util;

import org.smartregister.tbr.R;

import java.util.ArrayList;
import java.util.HashMap;

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
        public static final String TEST_RESULTS = "test_results_forms_config";
        public static final String PATIENT_DETAILS_PRESUMPTIVE = "patient_details_presumptive";
        public static final String PATIENT_DETAILS_POSITIVE = "patient_details_positive";
        public static final String PATIENT_DETAILS_INTREATMENT = "patient_details_intreatment";
        public static final String INTREATMENT_REGISTER = "intreatment_register";

        public static class COMPONENTS {
            public static final String PATIENT_DETAILS_DEMOGRAPHICS = "component_patient_details_demographics";
            public static final String PATIENT_DETAILS_RESULTS = "component_patient_details_results";
            public static final String PATIENT_DETAILS_SERVICE_HISTORY = "component_patient_details_service_history";
            public static final String PATIENT_DETAILS_CONTACT_SCREENING = "component_patient_details_contact_screening";
            public static final String PATIENT_DETAILS_FOLLOWUP = "component_patient_details_followup";
            public static final String PATIENT_DETAILS_BMI = "component_patient_details_bmi";
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
        public static final String CLIENT = "client";
        public static final String MOTHER_NAME = "mother_name";
        public static final String DEVELOPMENTAL_DISABILITY = "developmental_disability";

        public static final String DEPARTMENT = "department";
        public static final String PROVINCE = "province";
        public static final String DISTRICT = "district";
        public static final String CITY = "city";
        public static final String FULL_ADDRESS = "full_address";

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
        public static final String REGISTER_HEALTH_INDICATORS = "register_health_indicators";
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

    public static final class EVENT {

        public static final String REMOVE_PATIENT = "Remove Patient";
        public static final String TREATMENT_OUTCOME = "Treatment Outcome";
        public static final String SCREENING = "screening";
        public static final String POSITIVE_TB_PATIENT = "positive TB patient";
        public static final String CONTACT_SCREENING = "Contact Screening";
    }

    private static final Integer [] month0 = {R.drawable.infographic_24_1,R.drawable.infographic_24_2,R.drawable.infographic_24_3,R.drawable.infographic_37_1,R.drawable.infographic_37_2,R.drawable.infographic_37_3,R.drawable.infographic_40_1,R.drawable.infographic_40_2,R.drawable.infographic_40_3,R.raw.video_6};
    private static final Integer [] month1 = {R.drawable.infographic_8_1,R.drawable.infographic_8_2,R.drawable.infographic_8_3,R.drawable.infographic_36_1,R.drawable.infographic_36_2,R.drawable.infographic_36_3,R.drawable.infographic_39_1,R.drawable.infographic_39_2,R.drawable.infographic_39_3,R.raw.video_2};
    private static final Integer [] month2 = {R.drawable.infographic_16_1,R.drawable.infographic_16_2,R.drawable.infographic_16_3,R.drawable.infographic_37_1,R.drawable.infographic_37_2,R.drawable.infographic_37_3,R.drawable.infographic_40_1,R.drawable.infographic_40_2,R.drawable.infographic_40_3,R.raw.video_6};
    private static final Integer [] month3 = {R.drawable.infographic_36_1,R.drawable.infographic_36_2,R.drawable.infographic_36_3,R.drawable.infographic_37_1,R.drawable.infographic_37_2,R.drawable.infographic_37_3,R.drawable.infographic_41_1,R.drawable.infographic_41_2,R.drawable.infographic_41_3,R.raw.video_1};
    private static final Integer [] month4 = {R.drawable.infographic_11_1,R.drawable.infographic_11_2,R.drawable.infographic_11_3,R.drawable.infographic_10_1,R.drawable.infographic_10_2,R.drawable.infographic_10_3,R.drawable.infographic_42_1,R.drawable.infographic_42_2,R.drawable.infographic_42_3,R.raw.video_4};
    private static final Integer [] month5 = {R.drawable.infographic_17_1,R.drawable.infographic_17_2,R.drawable.infographic_26_1,R.drawable.infographic_26_2,R.drawable.infographic_26_3,R.drawable.infographic_43_1,R.drawable.infographic_43_2,R.drawable.infographic_43_3,R.raw.video_3};
    private static final Integer [] month6 = {R.drawable.infographic_13_1,R.drawable.infographic_13_2,R.drawable.infographic_13_3,R.drawable.infographic_27_1,R.drawable.infographic_27_2,R.drawable.infographic_44_1,R.drawable.infographic_44_2,R.drawable.infographic_44_3,R.raw.video_2};
    private static final Integer [] month7 = {R.drawable.infographic_32_1,R.drawable.infographic_32_2,R.drawable.infographic_32_3,R.drawable.infographic_33_1,R.drawable.infographic_33_2,R.drawable.infographic_45_1,R.drawable.infographic_45_2,R.drawable.infographic_45_3,R.raw.video_5};
    private static final Integer [] month8 = {R.drawable.infographic_37_1,R.drawable.infographic_37_2,R.drawable.infographic_37_3,R.drawable.infographic_34_1,R.drawable.infographic_34_2,R.drawable.infographic_34_3,R.drawable.infographic_46_1,R.drawable.infographic_46_2,R.drawable.infographic_46_3,R.raw.video_7};
    private static final Integer [] month9 = {R.drawable.infographic_31_1,R.drawable.infographic_31_2,R.drawable.infographic_31_3,R.drawable.infographic_28_1,R.drawable.infographic_28_2,R.drawable.infographic_28_3,R.drawable.infographic_47_1,R.drawable.infographic_47_2,R.drawable.infographic_47_3,R.raw.video_3};
    private static final Integer [] month10 = {R.drawable.infographic_12_1,R.drawable.infographic_12_2,R.drawable.infographic_12_3,R.drawable.infographic_35_1,R.drawable.infographic_35_2,R.drawable.infographic_35_3,R.drawable.infographic_48_1,R.drawable.infographic_48_2,R.drawable.infographic_48_3,R.raw.video_6};
    private static final Integer [] month11 = {R.drawable.infographic_9_1,R.drawable.infographic_9_2,R.drawable.infographic_9_3,R.drawable.infographic_37_1,R.drawable.infographic_37_2,R.drawable.infographic_37_3,R.drawable.infographic_49_1,R.drawable.infographic_49_2,R.drawable.infographic_49_3,R.raw.video_1};
    private static final Integer [] month12 = {R.drawable.infographic_32_1,R.drawable.infographic_32_2,R.drawable.infographic_32_3,R.drawable.infographic_29_1,R.drawable.infographic_29_2,R.drawable.infographic_29_3,R.drawable.infographic_50_1,R.drawable.infographic_50_2,R.drawable.infographic_50_3,R.raw.video_7};
    private static final Integer [] month13 = {R.drawable.infographic_14_1,R.drawable.infographic_14_2,R.drawable.infographic_14_3,R.drawable.infographic_34_1,R.drawable.infographic_34_2,R.drawable.infographic_34_3,R.drawable.infographic_51_1,R.drawable.infographic_51_2,R.raw.video_5};
    private static final Integer [] month14 = {R.drawable.infographic_11_1,R.drawable.infographic_11_2,R.drawable.infographic_11_3,R.drawable.infographic_10_1,R.drawable.infographic_10_2,R.drawable.infographic_10_3,R.drawable.infographic_52_1,R.drawable.infographic_52_2,R.drawable.infographic_52_3,R.raw.video_4};
    private static final Integer [] month15 = {R.drawable.infographic_15_1,R.drawable.infographic_15_2,R.drawable.infographic_20_1,R.drawable.infographic_20_2,R.drawable.infographic_53_1,R.drawable.infographic_53_2,R.drawable.infographic_53_3,R.raw.video_3};
    private static final Integer [] month16 = {R.drawable.infographic_31_1,R.drawable.infographic_31_2,R.drawable.infographic_31_3,R.drawable.infographic_37_1,R.drawable.infographic_37_2,R.drawable.infographic_37_3,R.drawable.infographic_54_1,R.drawable.infographic_54_2,R.drawable.infographic_54_3,R.raw.video_2};
    private static final Integer [] month17 = {R.drawable.infographic_19_1,R.drawable.infographic_19_2,R.drawable.infographic_19_3,R.drawable.infographic_29_1,R.drawable.infographic_29_2,R.drawable.infographic_29_3,R.drawable.infographic_55_1,R.drawable.infographic_55_2,R.raw.video_6};
    private static final Integer [] month18 = {R.drawable.infographic_32_1,R.drawable.infographic_32_2,R.drawable.infographic_32_3,R.drawable.infographic_33_1,R.drawable.infographic_33_2,R.drawable.infographic_56_1,R.drawable.infographic_56_2,R.drawable.infographic_56_3,R.raw.video_7};
    private static final Integer [] month19 = {R.drawable.infographic_14_1,R.drawable.infographic_14_2,R.drawable.infographic_14_3,R.drawable.infographic_13_1,R.drawable.infographic_13_2,R.drawable.infographic_13_3,R.drawable.infographic_57_1,R.drawable.infographic_57_2,R.raw.video_1};
    private static final Integer [] month20 = {R.drawable.infographic_16_1,R.drawable.infographic_16_2,R.drawable.infographic_16_3,R.drawable.infographic_17_1,R.drawable.infographic_17_2,R.drawable.infographic_58_1,R.drawable.infographic_58_2,R.drawable.infographic_58_3,R.raw.video_2};
    private static final Integer [] month21 = {R.drawable.infographic_21_1,R.drawable.infographic_21_2,R.drawable.infographic_21_3,R.drawable.infographic_30_1,R.drawable.infographic_30_2,R.drawable.infographic_59_1,R.drawable.infographic_59_2,R.drawable.infographic_59_3,R.raw.video_5};
    private static final Integer [] month22 = {R.drawable.infographic_31_1,R.drawable.infographic_31_2,R.drawable.infographic_31_3,R.drawable.infographic_34_1,R.drawable.infographic_34_2,R.drawable.infographic_34_3,R.drawable.infographic_60_1,R.drawable.infographic_60_2,R.drawable.infographic_60_3,R.raw.video_3};
    private static final Integer [] month23 = {R.drawable.infographic_18_1,R.drawable.infographic_18_2,R.drawable.infographic_18_3,R.drawable.infographic_35_1,R.drawable.infographic_35_2,R.drawable.infographic_35_3,R.drawable.infographic_61_1,R.drawable.infographic_61_2,R.drawable.infographic_61_3,R.raw.video_7};
    private static final Integer [] month24 = {R.drawable.infographic_20_1,R.drawable.infographic_20_2,R.drawable.infographic_9_1,R.drawable.infographic_9_2,R.drawable.infographic_9_3,R.drawable.infographic_62_1,R.drawable.infographic_62_2,R.drawable.infographic_62_3,R.raw.video_4};
    private static final Integer [] month25 = {R.drawable.infographic_11_1,R.drawable.infographic_11_2,R.drawable.infographic_11_3,R.drawable.infographic_13_1,R.drawable.infographic_13_2,R.drawable.infographic_13_3,R.drawable.infographic_63_1,R.drawable.infographic_63_2,R.drawable.infographic_63_3,R.raw.video_1};
    private static final Integer [] month26 = {R.drawable.infographic_12_1,R.drawable.infographic_12_2,R.drawable.infographic_12_3,R.drawable.infographic_30_1,R.drawable.infographic_30_2,R.drawable.infographic_64_1,R.drawable.infographic_64_2,R.drawable.infographic_64_3,R.raw.video_6};
    private static final Integer [] month27 = {R.drawable.infographic_10_1,R.drawable.infographic_10_2,R.drawable.infographic_10_3,R.drawable.infographic_19_1,R.drawable.infographic_19_2,R.drawable.infographic_19_3,R.drawable.infographic_65_1,R.drawable.infographic_65_2,R.drawable.infographic_65_3,R.raw.video_5};
    private static final Integer [] month28 = {R.drawable.infographic_15_1,R.drawable.infographic_15_2,R.drawable.infographic_34_1,R.drawable.infographic_34_2,R.drawable.infographic_34_3,R.drawable.infographic_66_1,R.drawable.infographic_66_2,R.drawable.infographic_66_3,R.raw.video_3};
    private static final Integer [] month29 = {R.drawable.infographic_30_1,R.drawable.infographic_30_2,R.drawable.infographic_31_1,R.drawable.infographic_31_2,R.drawable.infographic_31_3,R.drawable.infographic_67_1,R.drawable.infographic_67_2,R.drawable.infographic_67_3,R.raw.video_4};
    private static final Integer [] month30 = {R.drawable.infographic_14_1,R.drawable.infographic_14_2,R.drawable.infographic_14_3,R.drawable.infographic_20_1,R.drawable.infographic_20_2,R.drawable.infographic_68_1,R.drawable.infographic_68_2,R.drawable.infographic_68_3,R.raw.video_7};
    private static final Integer [] month31 = {R.drawable.infographic_18_1,R.drawable.infographic_18_2,R.drawable.infographic_21_1,R.drawable.infographic_21_2,R.drawable.infographic_21_3,R.drawable.infographic_69_1,R.drawable.infographic_69_2,R.drawable.infographic_69_3,R.raw.video_1};
    private static final Integer [] month32 = {R.drawable.infographic_15_1,R.drawable.infographic_15_2,R.drawable.infographic_35_1,R.drawable.infographic_35_2,R.drawable.infographic_35_3,R.drawable.infographic_70_1,R.drawable.infographic_70_2,R.raw.video_6};
    private static final Integer [] month33 = {R.drawable.infographic_9_1,R.drawable.infographic_9_2,R.drawable.infographic_9_3,R.drawable.infographic_30_1,R.drawable.infographic_30_2,R.drawable.infographic_71_1,R.drawable.infographic_71_2,R.drawable.infographic_71_3,R.raw.video_5};
    private static final Integer [] month34 = {R.drawable.infographic_11_1,R.drawable.infographic_11_2,R.drawable.infographic_11_3,R.drawable.infographic_10_1,R.drawable.infographic_10_2,R.drawable.infographic_10_3,R.drawable.infographic_72_1,R.drawable.infographic_72_2,R.drawable.infographic_72_3,R.raw.video_4};
    private static final Integer [] month35 = {R.drawable.infographic_13_1,R.drawable.infographic_13_2,R.drawable.infographic_13_3,R.drawable.infographic_34_1,R.drawable.infographic_34_2,R.drawable.infographic_34_3,R.drawable.infographic_73_1,R.drawable.infographic_73_2,R.drawable.infographic_73_3,R.raw.video_3};
    private static final Integer [] month36 = {R.drawable.infographic_12_1,R.drawable.infographic_12_2,R.drawable.infographic_12_3,R.drawable.infographic_31_1,R.drawable.infographic_31_2,R.drawable.infographic_31_3,R.drawable.infographic_74_1,R.drawable.infographic_74_2,R.drawable.infographic_74_3,R.raw.video_7};


    public static HashMap<Integer, Integer[]> getMonthsImageList(){

        HashMap<Integer, Integer[]> monthsImages = new HashMap<>();

        monthsImages.put(0,month0);
        monthsImages.put(1,month1);
        monthsImages.put(2,month2);
        monthsImages.put(3,month3);
        monthsImages.put(4,month4);
        monthsImages.put(5,month5);
        monthsImages.put(6,month6);
        monthsImages.put(7,month7);
        monthsImages.put(8,month8);
        monthsImages.put(9,month9);
        monthsImages.put(10,month10);
        monthsImages.put(11,month11);
        monthsImages.put(12,month12);
        monthsImages.put(13,month13);
        monthsImages.put(14,month14);
        monthsImages.put(15,month15);
        monthsImages.put(16,month16);
        monthsImages.put(17,month17);
        monthsImages.put(18,month18);
        monthsImages.put(19,month19);
        monthsImages.put(20,month20);
        monthsImages.put(21,month21);
        monthsImages.put(22,month22);
        monthsImages.put(23,month23);
        monthsImages.put(24,month24);
        monthsImages.put(25,month25);
        monthsImages.put(26,month26);
        monthsImages.put(27,month27);
        monthsImages.put(28,month28);
        monthsImages.put(29,month29);
        monthsImages.put(30,month30);
        monthsImages.put(31,month31);
        monthsImages.put(32,month32);
        monthsImages.put(33,month33);
        monthsImages.put(34,month34);
        monthsImages.put(35,month35);
        monthsImages.put(36,month36);

        return monthsImages;


    }


}
