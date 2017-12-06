package org.smartregister.tbr.provider;

import android.app.Activity;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.tbr.BaseUnitTest;
import org.smartregister.tbr.R;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import util.TbrConstants;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.TbrConstants.KEY.BASE_ENTITY_ID_COLUMN;
import static util.TbrConstants.KEY.DOB;
import static util.TbrConstants.KEY.FIRST_NAME;
import static util.TbrConstants.KEY.GENDER;
import static util.TbrConstants.KEY.LAST_NAME;
import static util.TbrConstants.KEY.TBREACH_ID;
import static util.TbrConstants.REGISTER_COLUMNS.DIAGNOSE;
import static util.TbrConstants.REGISTER_COLUMNS.DIAGNOSIS;
import static util.TbrConstants.REGISTER_COLUMNS.DROPDOWN;
import static util.TbrConstants.REGISTER_COLUMNS.ENCOUNTER;
import static util.TbrConstants.REGISTER_COLUMNS.PATIENT;
import static util.TbrConstants.REGISTER_COLUMNS.RESULTS;
import static util.TbrConstants.REGISTER_COLUMNS.TREAT;
import static util.TbrConstants.REGISTER_COLUMNS.XPERT_RESULTS;
import static util.TbrConstants.RESULT.MTB_RESULT;
import static util.TbrConstants.RESULT.RIF_RESULT;

/**
 * Created by samuelgithengi on 12/4/17.
 */

public class PatientRegisterProviderTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private DetailsRepository detailsRepository;

    @Mock
    private Cursor cursor;

    private Set<org.smartregister.tbr.jsonspec.model.View> visibleColumns = new HashSet<>();

    @Mock
    private RegisterActionHandler registerActionHandler;

    private View view;

    private PatientRegisterProvider patientRegisterProvider;

    private Map<String, String> columnMap = new HashMap<>();

    private CommonPersonObjectClient smartRegisterClient = new CommonPersonObjectClient(null, null, null);

    @Before
    public void setupTest() {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        view = LayoutInflater.from(activity).inflate(R.layout.register_presumptive_list_row, null);
        columnMap.put(BASE_ENTITY_ID_COLUMN, "255c9df9-42ba-424d-a235-bd4ea5da77ae");
        smartRegisterClient.setColumnmaps(columnMap);
    }

    private void initProvider(String columnIdentifier) {
        org.smartregister.tbr.jsonspec.model.View column = new org.smartregister.tbr.jsonspec.model.View();
        column.setIdentifier(columnIdentifier);
        visibleColumns.add(column);
        patientRegisterProvider = new PatientRegisterProvider(RuntimeEnvironment.application, visibleColumns, registerActionHandler, detailsRepository);
        patientRegisterProvider.getView(cursor, smartRegisterClient, view);
    }

    @Test
    public void testPopulatePatientColumn() {

        columnMap.put(FIRST_NAME, "Ali");
        columnMap.put(LAST_NAME, "Lango");
        columnMap.put(TBREACH_ID, "45355435435");
        columnMap.put(DOB, "2016-11-30T00:00:00.000-0500");
        columnMap.put(GENDER, "male");

        initProvider(PATIENT);

        assertEquals("Ali Lango", ((TextView) view.findViewById(R.id.patient_name)).getText());
        assertEquals("#45355435435", ((TextView) view.findViewById(R.id.participant_id)).getText());
        assertEquals("Male", ((TextView) view.findViewById(R.id.gender)).getText());
        String age = patientRegisterProvider.getDuration(columnMap.get(DOB));
        assertEquals(age.substring(0, age.indexOf("y")), ((TextView) view.findViewById(R.id.age)).getText());
    }

    @Test
    public void testPopulateResultsColumnNoResults() {
        Map results = new HashMap();
        when(detailsRepository.getAllDetailsForClient("255c9df9-42ba-424d-a235-bd4ea5da77ae")).thenReturn(results);

        initProvider(RESULTS);

        assertEquals(View.GONE, view.findViewById(R.id.result_details).getVisibility());

    }

    @Test
    public void testPopulateResultsColumnWithResults() {
        Map results = new HashMap();
        results.put(MTB_RESULT, "detected");
        when(detailsRepository.getAllDetailsForClient("255c9df9-42ba-424d-a235-bd4ea5da77ae")).thenReturn(results);

        initProvider(RESULTS);
        assertEquals(View.VISIBLE, view.findViewById(R.id.result_details).getVisibility());

        String expected = "Xpe +ve/ -ve";
        assertEquals(expected, ((TextView) view.findViewById(R.id.result_details)).getText().toString());

        results.put(RIF_RESULT, "indeterminate");
        initProvider(RESULTS);
        expected = "Xpe +ve/ ?";
        assertEquals(expected, ((TextView) view.findViewById(R.id.result_details)).getText().toString());

    }


    @Test
    public void testPopulateXpertResultsColumn() {
        Map results = new HashMap();
        results.put(MTB_RESULT, "detected");
        when(detailsRepository.getAllDetailsForClient("255c9df9-42ba-424d-a235-bd4ea5da77ae")).thenReturn(results);

        initProvider(XPERT_RESULTS);
        assertEquals(View.VISIBLE, view.findViewById(R.id.xpert_result_details).getVisibility());

        String expected = "MTB +ve RIF -ve";
        assertEquals(expected, ((TextView) view.findViewById(R.id.xpert_result_details)).getText().toString());

        results.put(RIF_RESULT, "indeterminate");
        initProvider(XPERT_RESULTS);
        expected = "MTB +ve RIF ?";
        assertEquals(expected, ((TextView) view.findViewById(R.id.xpert_result_details)).getText().toString());

    }

    private View testClickOnlyColumn(String columnIdentifier, int buttonViewId, String baseEntityID) {
        smartRegisterClient.setCaseId(baseEntityID);
        initProvider(columnIdentifier);
        View component = view.findViewById(buttonViewId);
        component.performClick();
        assertEquals(smartRegisterClient, component.getTag());
        verify(registerActionHandler).onClick(component);
        return component;
    }


    @Test
    public void testPopulateDiagnoseColumn() {
        String baseEntityID = UUID.randomUUID().toString();
        View component = testClickOnlyColumn(DIAGNOSE, R.id.diagnose_lnk, baseEntityID);
        assertEquals(((SmartRegisterClient) component.getTag()).entityId(), baseEntityID);
    }

    @Test
    public void testPopulateTreatColumn() {
        String baseEntityID = UUID.randomUUID().toString();
        view = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.register_positive_list_row, null);
        View component = testClickOnlyColumn(TREAT, R.id.treat_lnk, baseEntityID);
        assertEquals(((SmartRegisterClient) component.getTag()).entityId(), baseEntityID);
    }

    @Test
    public void testPopulateDropdownColumn() {
        String baseEntityID = UUID.randomUUID().toString();
        View component = testClickOnlyColumn(DROPDOWN, R.id.dropdown_btn, baseEntityID);
        assertEquals(((SmartRegisterClient) component.getTag()).entityId(), baseEntityID);
    }

    @Test
    public void testPopulateEncounterColumn() {
        String firstEncounter = "2017-11-30T02:40:15.600-0500";
        columnMap.put(TbrConstants.KEY.FIRST_ENCOUNTER, firstEncounter);
        initProvider(ENCOUNTER);
        assertEquals(patientRegisterProvider.getDuration(firstEncounter) + " ago", ((TextView) view.findViewById(R.id.encounter)).getText());

    }

    @Test
    public void testPopulateDiagnosisColumn() {
        view = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.register_positive_list_row, null);
        String firstEncounter = "2017-11-20T02:40:15.600-0500";
        columnMap.put(TbrConstants.KEY.DIAGNOSIS_DATE, firstEncounter);
        initProvider(DIAGNOSIS);
        assertEquals(patientRegisterProvider.getDuration(firstEncounter) + " ago", ((TextView) view.findViewById(R.id.diagnosis)).getText());

    }

    @Test
    public void testGetDuration() {
        patientRegisterProvider = new PatientRegisterProvider(RuntimeEnvironment.application, visibleColumns, registerActionHandler, detailsRepository);
        Calendar calendar = Calendar.getInstance();
        assertEquals("0d", patientRegisterProvider.getDuration(new DateTime(calendar.getTimeInMillis()).toString()));
        calendar.add(Calendar.DATE, -10);
        assertEquals("10d", patientRegisterProvider.getDuration(new DateTime(calendar.getTimeInMillis()).toString()));
        calendar.add(Calendar.MONTH, -6);
        assertEquals("6m 2w", patientRegisterProvider.getDuration(new DateTime(calendar.getTimeInMillis()).toString()));
        calendar.add(Calendar.YEAR, -30);
        assertEquals("30y 6m", patientRegisterProvider.getDuration(new DateTime(calendar.getTimeInMillis()).toString()));

    }

    private class RegisterActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {//Onclick
        }
    }
}
