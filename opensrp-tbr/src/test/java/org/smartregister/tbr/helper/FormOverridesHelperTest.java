package org.smartregister.tbr.helper;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.domain.form.FieldOverrides;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.TbrConstants;

/**
 * Created by ndegwamartin on 20/03/2018.
 */

public class FormOverridesHelperTest {

    private FormOverridesHelper formOverridesHelper;

    @Before
    public void setUp() {

        Map<String, String> patientDetails = new HashMap<>();

        patientDetails.put(TbrConstants.KEY.PARTICIPANT_ID, "5345");
        patientDetails.put(TbrConstants.KEY.FIRST_NAME, "William");
        patientDetails.put(TbrConstants.KEY.LAST_NAME, "Tell");
        patientDetails.put(TbrConstants.KEY.PROGRAM_ID, "7730");
        patientDetails.put(TbrConstants.KEY.TREATMENT_INITIATION_DATE, "2017-03-20");

        formOverridesHelper = new FormOverridesHelper(patientDetails);
    }


    @Test
    public void getFieldOverridesReturnsCorrectInstance() {
        Assert.assertNotNull(formOverridesHelper.getFieldOverrides());
        Assert.assertNotNull("{\"fieldOverrides\":\"{\\\"program_id\\\":\\\"7730\\\",\\\"last_name\\\":\\\"Tell\\\",\\\"first_name\\\":\\\"William\\\"}\"}", formOverridesHelper.getFieldOverrides().getJSONString());


    }

    @Test
    public void getFieldOverridesReturnsCorrectInstanceForSetDetails() {
        FormOverridesHelper formOverridesHelper = new FormOverridesHelper(null);
        Map<String, String> patientDetails = new HashMap<>();

        patientDetails.put(TbrConstants.KEY.PARTICIPANT_ID, "1099");
        patientDetails.put(TbrConstants.KEY.FIRST_NAME, "Speedy");
        patientDetails.put(TbrConstants.KEY.LAST_NAME, "Gonzalez");
        patientDetails.put(TbrConstants.KEY.PROGRAM_ID, "77555");

        formOverridesHelper.setPatientDetails(patientDetails);
        Assert.assertNotNull("{\"fieldOverrides\":\"{\\\"program_id\\\":\\\"77555\\\",\\\"last_name\\\":\\\"Gonzalez\\\",\\\"first_name\\\":\\\"Speedy\\\"}\"}", formOverridesHelper.getFieldOverrides().getJSONString());

    }

    @Test
    public void getFollowupFieldOverridesReturnsCorrectJsonString() {
        FieldOverrides fieldOverrides = formOverridesHelper.getFollowUpFieldOverrides();
        Assert.assertNotNull(fieldOverrides);
        Assert.assertEquals("{\"fieldOverrides\":\"{\\\"participant_id\\\":\\\"5345\\\",\\\"program_id\\\":\\\"7730\\\",\\\"last_name\\\":\\\"Tell\\\",\\\"treatment_initiation_date\\\":\\\"2017-03-20\\\",\\\"first_name\\\":\\\"William\\\"}\"}", fieldOverrides.getJSONString());

    }

    @Test
    public void getContactScreeningFieldOverridesReturnsCorrectJsonString() {
        FieldOverrides fieldOverrides = formOverridesHelper.getContactScreeningFieldOverrides();
        Assert.assertNotNull("{\"fieldOverrides\":\"{\\\"program_id\\\":\\\"7730\\\",\\\"last_name\\\":\\\"Tell\\\",\\\"first_name\\\":\\\"William\\\"}\"}", fieldOverrides.getJSONString());

    }

    @Test
    public void getTreatmentFieldOverridesReturnsCorrectJsonString() {
        FieldOverrides fieldOverrides = formOverridesHelper.getTreatmentFieldOverrides();
        Assert.assertNotNull(fieldOverrides);
        Assert.assertEquals("{\"fieldOverrides\":\"{\\\"participant_id\\\":\\\"5345\\\",\\\"program_id\\\":\\\"7730\\\",\\\"last_name\\\":\\\"Tell\\\",\\\"first_name\\\":\\\"William\\\",\\\"age\\\":\\\"\\\"}\"}", fieldOverrides.getJSONString());

    }

    @Test
    public void getTreatmentFieldOverridesWithDOBReturnsCorrectJsonString() {
        FormOverridesHelper formOverridesHelper = new FormOverridesHelper(null);
        Map<String, String> patientDetails = new HashMap<>();

        patientDetails.put(TbrConstants.KEY.PARTICIPANT_ID, "5345");
        patientDetails.put(TbrConstants.KEY.FIRST_NAME, "William");
        patientDetails.put(TbrConstants.KEY.LAST_NAME, "Tell");
        patientDetails.put(TbrConstants.KEY.PROGRAM_ID, "7730");

        long twentyOneYearsAgo = System.currentTimeMillis() - (((21L * 365L) + 3L) * 24L * 60L * 60L * 1000L);

        patientDetails.put(TbrConstants.KEY.DOB, getDate(twentyOneYearsAgo));

        formOverridesHelper.setPatientDetails(patientDetails);

        FieldOverrides fieldOverrides = formOverridesHelper.getTreatmentFieldOverrides();
        Assert.assertNotNull(fieldOverrides);
        Assert.assertEquals("{\"fieldOverrides\":\"{\\\"participant_id\\\":\\\"5345\\\",\\\"program_id\\\":\\\"7730\\\",\\\"last_name\\\":\\\"Tell\\\",\\\"first_name\\\":\\\"William\\\",\\\"age\\\":\\\"21\\\"}\"}", fieldOverrides.getJSONString());

    }

    @Test
    public void getTreatmentFieldOverridesWithIncorrectDOBReturnsCorrectJsonString() {
        FormOverridesHelper formOverridesHelper = new FormOverridesHelper(null);
        Map<String, String> patientDetails = new HashMap<>();

        patientDetails.put(TbrConstants.KEY.PARTICIPANT_ID, "5345");
        patientDetails.put(TbrConstants.KEY.FIRST_NAME, "William");
        patientDetails.put(TbrConstants.KEY.LAST_NAME, "Tell");
        patientDetails.put(TbrConstants.KEY.PROGRAM_ID, "7730");
        patientDetails.put(TbrConstants.KEY.DOB, "alpha");

        formOverridesHelper.setPatientDetails(patientDetails);

        FieldOverrides fieldOverrides = formOverridesHelper.getTreatmentFieldOverrides();
        Assert.assertNotNull(fieldOverrides);
        Assert.assertEquals("{\"fieldOverrides\":\"{\\\"participant_id\\\":\\\"5345\\\",\\\"program_id\\\":\\\"7730\\\",\\\"last_name\\\":\\\"Tell\\\",\\\"first_name\\\":\\\"William\\\",\\\"age\\\":\\\"\\\"}\"}", fieldOverrides.getJSONString());

    }

    /**
     * Returns the time in the format yyyy-mm-dd eg. 2018-09-23 when given the time in milliseconds
     *
     * @param millis
     * @return
     */
    private String getDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(millis);
        return sdf.format(date);
    }
}
