package org.smartregister.tbr.helper;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.repository.ResultsRepository;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;
import org.smartregister.util.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.TbrConstants;

/**
 * Created by ndegwamartin on 27/01/2018.
 */

public class FormOverridesHelper {

    private String TAG = FormOverridesHelper.class.getCanonicalName();

    private Map<String, String> patientDetails;

    public FormOverridesHelper(Map<String, String> patientDetails) {
        this.patientDetails = patientDetails;
    }

    public void setPatientDetails(Map<String, String> patientDetails) {
        this.patientDetails = patientDetails;
    }

    public FieldOverrides getFieldOverrideForChildID() {

        Date date = new Date();
        String timestamp = Long.valueOf(date.getTime()).toString();
// Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        int dni = Integer.parseInt(timestamp.toString().substring(timestamp.length()-9,timestamp.length()-1));
        Map fields = new HashMap();
        fields.put(TbrConstants.KEY.CHILD_ID, dni);

        JSONObject fieldOverridesJson = new JSONObject(fields);
        FieldOverrides fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }

    public Map populateFieldOverrides() {
        Map fields = new HashMap();
        fields.put(TbrConstants.KEY.PARTICIPANT_ID, patientDetails.get(TbrConstants.KEY.PARTICIPANT_ID));
        fields.put(TbrConstants.KEY.FIRST_NAME, patientDetails.get(TbrConstants.KEY.FIRST_NAME));
        fields.put(TbrConstants.KEY.LAST_NAME, patientDetails.get(TbrConstants.KEY.LAST_NAME));
        //fields.put(TbrConstants.KEY.PROGRAM_ID, patientDetails.get(TbrConstants.KEY.PROGRAM_ID));
        fields.put(TbrConstants.KEY.PROGRAM_ID, patientDetails.get(TbrConstants.KEY.PARTICIPANT_ID));

        String dobString = patientDetails.get(TbrConstants.KEY.DOB);
        DateTime dateTime = new DateTime(dobString);
        Date dob = dateTime.toDate();
        String age = "";
        int months = (int) Math.round(getAgeInMonths(dob, new Date()));
        /*if (StringUtils.isNotBlank(dobString)) {
            try {
                DateTime birthDateTime = new DateTime(dobString);
                String duration = DateUtil.getDuration(birthDateTime);
                if(duration.contains("y")){
                    months += Integer.parseInt(duration.substring(0, duration.indexOf("y"))) * 12;
                    if(duration.contains("m")){
                        months += Integer.parseInt(duration.substring(3, duration.indexOf("m")));
                    }
                }
                else if(duration.contains("m")){
                    months += Integer.parseInt(duration.substring(0, duration.indexOf("m")));
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }*/
        fields.put(TbrConstants.KEY.AGE, months);
        Map<String, String> latestResult = TbrApplication.getInstance().getResultsRepository().getLatestResult(patientDetails.get(TbrConstants.KEY.BASE_ENTITY_ID));

        fields.put(ResultsRepository.ZERO_TUBERCULOSIS, latestResult.get(ResultsRepository.ZERO_TUBERCULOSIS) == null ? "no" : latestResult.get(ResultsRepository.ZERO_TUBERCULOSIS));
        fields.put(ResultsRepository.ZERO_ANTIHERPATITIS, latestResult.get(ResultsRepository.ZERO_ANTIHERPATITIS) == null ? "no" : latestResult.get(ResultsRepository.ZERO_ANTIHERPATITIS));
        fields.put(ResultsRepository.TWO_ROTAVIRUS, latestResult.get(ResultsRepository.TWO_ROTAVIRUS) == null ? "no" : latestResult.get(ResultsRepository.TWO_ROTAVIRUS));
        fields.put(ResultsRepository.TWO_PENTAVALENTE, latestResult.get(ResultsRepository.TWO_PENTAVALENTE) == null ? "no" : latestResult.get(ResultsRepository.TWO_PENTAVALENTE));
        fields.put(ResultsRepository.TWO_NEUMOCOCO, latestResult.get(ResultsRepository.TWO_NEUMOCOCO) == null ? "no" : latestResult.get(ResultsRepository.TWO_NEUMOCOCO));
        fields.put(ResultsRepository.TWO_ANTIPOLIO, latestResult.get(ResultsRepository.TWO_ANTIPOLIO) == null ? "no" : latestResult.get(ResultsRepository.TWO_ANTIPOLIO));
        fields.put(ResultsRepository.FOUR_ROTAVIRUS, latestResult.get(ResultsRepository.FOUR_ROTAVIRUS) == null ? "no" : latestResult.get(ResultsRepository.FOUR_ROTAVIRUS));
        fields.put(ResultsRepository.FOUR_PENTAVALENTE, latestResult.get(ResultsRepository.FOUR_PENTAVALENTE) == null ? "no" : latestResult.get(ResultsRepository.FOUR_PENTAVALENTE));
        fields.put(ResultsRepository.FOUR_NEUMOCOCO, latestResult.get(ResultsRepository.FOUR_NEUMOCOCO) == null ? "no" : latestResult.get(ResultsRepository.FOUR_NEUMOCOCO));
        fields.put(ResultsRepository.FOUR_ANTIPOLIO, latestResult.get(ResultsRepository.FOUR_ANTIPOLIO) == null ? "no" : latestResult.get(ResultsRepository.FOUR_ANTIPOLIO));
        fields.put(ResultsRepository.SIX_PENTAVALENTE, latestResult.get(ResultsRepository.SIX_PENTAVALENTE) == null ? "no" : latestResult.get(ResultsRepository.SIX_PENTAVALENTE));
        fields.put(ResultsRepository.SIX_ANTIPOLIO, latestResult.get(ResultsRepository.SIX_ANTIPOLIO) == null ? "no" : latestResult.get(ResultsRepository.SIX_ANTIPOLIO));
        fields.put(ResultsRepository.TWELVE_SARAMPION, latestResult.get(ResultsRepository.TWELVE_SARAMPION) == null ? "no" : latestResult.get(ResultsRepository.TWELVE_SARAMPION));
        fields.put(ResultsRepository.TWELVE_NEUMOCOCO, latestResult.get(ResultsRepository.TWELVE_NEUMOCOCO) == null ? "no" : latestResult.get(ResultsRepository.TWELVE_NEUMOCOCO));
        fields.put(ResultsRepository.FIFTEEN_ANTIAMARILICA, latestResult.get(ResultsRepository.FIFTEEN_ANTIAMARILICA) == null ? "no" : latestResult.get(ResultsRepository.FIFTEEN_ANTIAMARILICA));
        return fields;
    }

    public FieldOverrides getFieldOverrides() {
        Map fields = populateFieldOverrides();
        JSONObject fieldOverridesJson = new JSONObject(fields);
        FieldOverrides fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }

    public FieldOverrides getFollowUpFieldOverrides() {
        Map fields = populateFieldOverrides();
        fields.put(TbrConstants.KEY.TREATMENT_INITIATION_DATE, patientDetails.get(TbrConstants.KEY.TREATMENT_INITIATION_DATE));
        JSONObject fieldOverridesJson = new JSONObject(fields);
        FieldOverrides fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }

    public FieldOverrides getTreatmentFieldOverrides() {
        Map fields = populateFieldOverrides();
        fields.put(TbrConstants.KEY.GENDER, patientDetails.get(TbrConstants.KEY.GENDER));
        String dobString = patientDetails.get(TbrConstants.KEY.DOB);
        String age = "";
        if (StringUtils.isNotBlank(dobString)) {
            try {
                DateTime birthDateTime = new DateTime(dobString);
                String duration = DateUtil.getDuration(birthDateTime);
                if (duration != null) {
                    age = duration.substring(0, duration.length() - 1);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
        fields.put(TbrConstants.KEY.AGE, age);
        JSONObject fieldOverridesJson = new JSONObject(fields);
        FieldOverrides fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }

    public FieldOverrides getAddContactFieldOverrides() {
        Map fields = new HashMap();
        fields.put(TbrConstants.KEY.PARTICIPANT_ID, patientDetails.get(TbrConstants.KEY.PARTICIPANT_ID));
        fields.put(TbrConstants.KEY.PARENT_ENTITY_ID, patientDetails.get(Constants.KEY._ID));
        return new FieldOverrides(new JSONObject(fields).toString());
    }

    public FieldOverrides getContactScreeningFieldOverrides() {
        Map fields = populateFieldOverrides();
        fields.remove(TbrConstants.KEY.PARTICIPANT_ID);
        return new FieldOverrides(new JSONObject(fields).toString());
    }

    public static double getAgeInMonths(Date dateOfBirth, Date weighingDate) {
        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.setTime(dateOfBirth);
        standardiseCalendarDate(dobCalendar);

        Calendar weighingCalendar = Calendar.getInstance();
        weighingCalendar.setTime(weighingDate);
        standardiseCalendarDate(weighingCalendar);

        double result = 0;
        if (dobCalendar.getTimeInMillis() <= weighingCalendar.getTimeInMillis()) {
            result = ((double) (weighingCalendar.getTimeInMillis() - dobCalendar.getTimeInMillis())) / 2629746000l;
        }

        return result;
    }

    private static void standardiseCalendarDate(Calendar calendarDate) {
        calendarDate.set(Calendar.HOUR_OF_DAY, 0);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);
    }

}
