package org.smartregister.tbr.provider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.database.Cursor;
import android.os.Build;
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
import org.smartregister.tbr.repository.ResultsRepository;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.tbr.repository.ResultsRepository.DATE;
import static util.TbrConstants.KEY;
import static util.TbrConstants.KEY.BASE_ENTITY_ID_COLUMN;
import static util.TbrConstants.KEY.DOB;
import static util.TbrConstants.KEY.FIRST_NAME;
import static util.TbrConstants.KEY.GENDER;
import static util.TbrConstants.KEY.LAST_NAME;
import static util.TbrConstants.KEY.PARTICIPANT_ID;
import static util.TbrConstants.REGISTER_COLUMNS.BASELINE;
import static util.TbrConstants.REGISTER_COLUMNS.DIAGNOSE;
import static util.TbrConstants.REGISTER_COLUMNS.DIAGNOSIS;
import static util.TbrConstants.REGISTER_COLUMNS.FOLLOWUP;
import static util.TbrConstants.REGISTER_COLUMNS.FOLLOWUP_SCHEDULE;
import static util.TbrConstants.REGISTER_COLUMNS.INTREATMENT_RESULTS;
import static util.TbrConstants.REGISTER_COLUMNS.PATIENT;
import static util.TbrConstants.REGISTER_COLUMNS.RESULTS;
import static util.TbrConstants.REGISTER_COLUMNS.TREAT;
import static util.TbrConstants.REGISTER_COLUMNS.TREATMENT;
import static util.TbrConstants.REGISTER_COLUMNS.XPERT_RESULTS;
import static util.TbrConstants.RESULT.CULTURE_RESULT;
import static util.TbrConstants.RESULT.MTB_RESULT;
import static util.TbrConstants.RESULT.RIF_RESULT;
import static util.TbrConstants.RESULT.TEST_RESULT;
import static util.TbrConstants.RESULT.XRAY_RESULT;

/**
 * Created by samuelgithengi on 12/4/17.
 */

public class PatientRegisterProviderTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private ResultsRepository resultsRepository;

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
        patientRegisterProvider = new PatientRegisterProvider(RuntimeEnvironment.application, visibleColumns, registerActionHandler, resultsRepository, detailsRepository);
        patientRegisterProvider.getView(cursor, smartRegisterClient, view);
    }

    @Test
    public void testPopulatePatientColumn() {

        columnMap.put(FIRST_NAME, "Ali");
        columnMap.put(LAST_NAME, "Lango");
        columnMap.put(PARTICIPANT_ID, "45355435435");
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
        when(resultsRepository.getLatestResults("255c9df9-42ba-424d-a235-bd4ea5da77ae")).thenReturn(results);

        initProvider(RESULTS);

        assertEquals(View.GONE, view.findViewById(R.id.result_details).getVisibility());

    }

    @Test
    public void testPopulateResultsColumnWithResults() {
        Map results = new HashMap();
        TextView details = ((TextView) view.findViewById(R.id.result_details));
        results.put(MTB_RESULT, "detected");
        when(resultsRepository.getLatestResults("255c9df9-42ba-424d-a235-bd4ea5da77ae")).thenReturn(results);

        initProvider(RESULTS);
        assertEquals(View.VISIBLE, details.getVisibility());

        String expected = "Xpe +ve";
        assertEquals(expected, details.getText().toString());
        details.setText("");

        results.put(RIF_RESULT, "indeterminate");
        initProvider(RESULTS);
        expected = "Xpe +ve/ ?";
        assertEquals(expected, details.getText().toString());

    }


    @Test
    public void testPopulateXpertResultsColumn() {
        Map results = new HashMap();
        results.put(MTB_RESULT, "detected");
        when(resultsRepository.getLatestResults("255c9df9-42ba-424d-a235-bd4ea5da77ae")).thenReturn(results);

        initProvider(XPERT_RESULTS);
        assertEquals(View.VISIBLE, view.findViewById(R.id.xpert_result_details).getVisibility());

        String expected = "MTB +ve";
        assertEquals(expected, ((TextView) view.findViewById(R.id.xpert_result_details)).getText().toString());

        results.put(RIF_RESULT, "indeterminate");
        initProvider(XPERT_RESULTS);
        expected = "MTB +ve\nRIF ?";
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
    public void testPopulateDiagnosisColumn() {
        view = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.register_positive_list_row, null);
        String firstEncounter = "2017-11-20T02:40:15.600-0500";
        columnMap.put(KEY.DIAGNOSIS_DATE, firstEncounter);
        initProvider(DIAGNOSIS);
        assertEquals(patientRegisterProvider.formatDate(firstEncounter), ((TextView) view.findViewById(R.id.diagnosis)).getText());

    }

    @Test
    public void testGetDuration() {
        patientRegisterProvider = new PatientRegisterProvider(RuntimeEnvironment.application, visibleColumns, registerActionHandler, resultsRepository, detailsRepository);
        Calendar calendar = Calendar.getInstance();
        assertEquals("0d", patientRegisterProvider.getDuration(new DateTime(calendar.getTimeInMillis()).toString()));
        calendar.add(Calendar.DATE, -14);
        assertEquals("2w", patientRegisterProvider.getDuration(new DateTime(calendar.getTimeInMillis()).toString()));
        calendar.add(Calendar.MONTH, -6);
        assertEquals("6m 2w", patientRegisterProvider.getDuration(new DateTime(calendar.getTimeInMillis()).toString()));
        calendar.add(Calendar.YEAR, -30);
        assertEquals("30y 6m", patientRegisterProvider.getDuration(new DateTime(calendar.getTimeInMillis()).toString()));
        assertEquals("", patientRegisterProvider.getDuration(calendar.toString()));


    }

    @Test
    public void testPopulateIntreatmentResultsColumnWithMissingBaseline() {
        view = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.register_intreatment_list_row, null);
        String treatment = "2017-11-20T02:40:15.600-0500";
        columnMap.put(KEY.TREATMENT_INITIATION_DATE, treatment);
        initProvider(INTREATMENT_RESULTS);
        assertTrue(((TextView) view.findViewById(R.id.intreatment_details)).getText().toString().isEmpty());
    }

    @Test
    public void testPopulateIntreatmentResultsColumn() {
        view = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.register_intreatment_list_row, null);
        initProvider(INTREATMENT_RESULTS);
        assertEquals("", ((TextView) view.findViewById(R.id.intreatment_details)).getText().toString());

        Map results = new HashMap();
        results.put(TEST_RESULT, "three_plus");
        results.put(DATE, DateTime.now().getMillis() + "");
        when(resultsRepository.getLatestResult("255c9df9-42ba-424d-a235-bd4ea5da77ae")).thenReturn(results);
        initProvider(INTREATMENT_RESULTS);
        String expected = DateTime.now().toString("dd/MM/yyyy") + "\nSmr 3+";
        assertEquals(expected, ((TextView) view.findViewById(R.id.intreatment_details)).getText().toString());

    }

    @Test
    public void testPopulateFollowupColumn() {
        view = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.register_intreatment_list_row, null);
        String baseEntityID = UUID.randomUUID().toString();
        View component = testClickOnlyColumn(FOLLOWUP, R.id.followup_lnk, baseEntityID);
        assertEquals(((SmartRegisterClient) component.getTag()).entityId(), baseEntityID);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testPopulateFollowupScheduleColumn() {
        view = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.register_intreatment_list_row, null);
        String baseEntityID = UUID.randomUUID().toString();
        View component = testClickOnlyColumn(FOLLOWUP_SCHEDULE, R.id.followup, baseEntityID);
        assertEquals(((SmartRegisterClient) component.getTag()).entityId(), baseEntityID);

        DateTime nextVisitDate = new DateTime();
        columnMap.put(KEY.NEXT_VISIT_DATE, nextVisitDate.toString());
        initProvider(FOLLOWUP_SCHEDULE);
        assertEquals("Followup\n due " + nextVisitDate.toString("dd/MM/yy"), ((TextView) view.findViewById(R.id.followup_text)).getText());
        Activity context = Robolectric.buildActivity(Activity.class).create().get();
        assertEquals(context.getDrawable(R.drawable.due_vaccine_blue_bg), view.findViewById(R.id.followup).getBackground());


        nextVisitDate = nextVisitDate.plusDays(1);
        columnMap.put(KEY.NEXT_VISIT_DATE, nextVisitDate.toString());
        initProvider(FOLLOWUP_SCHEDULE);
        assertEquals(context.getDrawable(R.drawable.due_vaccine_na_bg), view.findViewById(R.id.followup).getBackground());


        nextVisitDate = nextVisitDate.plusDays(-3);
        columnMap.put(KEY.NEXT_VISIT_DATE, nextVisitDate.toString());
        initProvider(FOLLOWUP_SCHEDULE);
        assertEquals(context.getDrawable(R.drawable.due_vaccine_na_bg), view.findViewById(R.id.followup).getBackground());

    }

    @Test
    public void testPopulateBaselineColumn() {
         view = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.register_intreatment_list_row, null);
        TextView details = ((TextView) view.findViewById(R.id.baseline_details));
        long baseline = 1513341747;
        columnMap.put(KEY.BASELINE, String.valueOf(baseline));
        initProvider(BASELINE);
        assertEquals("", details.getText().toString());


        Map results = new HashMap();
        results.put(TEST_RESULT, "one_plus");
        results.put(CULTURE_RESULT, "positive");
        results.put(XRAY_RESULT, "indicative");
        when(resultsRepository.getLatestResults("255c9df9-42ba-424d-a235-bd4ea5da77ae", false, baseline)).thenReturn(results);
        initProvider(BASELINE);
        String expected = "Smr 1+\nCul Pos, CXR Ind";
        assertEquals(expected, details.getText().toString());
        details.setText("");

        results.put(XRAY_RESULT, "not indicative");
        when(resultsRepository.getLatestResults("255c9df9-42ba-424d-a235-bd4ea5da77ae", false, baseline)).thenReturn(results);
        initProvider(BASELINE);
        expected = "Smr 1+\nCul Pos, CXR NonI";
        assertEquals(expected, details.getText().toString());


    }


    @Test
    public void testPopulateTreatmentColumn() {
        view = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.register_intreatment_list_row, null);
        Map results = new HashMap();
        results.put(KEY.TREATMENT_PHASE, "Intensive");
        results.put(KEY.TREATMENT_REGIMEN1, "2HRZE/HR");
        results.put(KEY.TREATMENT_MONTH, "6");
        when(detailsRepository.getAllDetailsForClient("255c9df9-42ba-424d-a235-bd4ea5da77ae")).thenReturn(results);
        initProvider(TREATMENT);
        assertEquals("Month " + 6, ((TextView) view.findViewById(R.id.treatment_started)).getText().toString());
        assertEquals("Intensive", ((TextView) view.findViewById(R.id.treatment_phase)).getText().toString());
        assertEquals("2HRZE/HR", ((TextView) view.findViewById(R.id.regimen)).getText().toString());

    }


    @Test
    public void testPopulateSmear() {
        TextView details = ((TextView) view.findViewById(R.id.result_details));
        Map results = new HashMap();
        results.put(TEST_RESULT, "two_plus");
        when(resultsRepository.getLatestResults("255c9df9-42ba-424d-a235-bd4ea5da77ae")).thenReturn(results);
        initProvider(RESULTS);
        String expected = "Smr 2+";
        assertEquals(expected, details.getText().toString());
        details.setText("");

        results.put(TEST_RESULT, "three_plus");
        when(resultsRepository.getLatestResults("255c9df9-42ba-424d-a235-bd4ea5da77ae")).thenReturn(results);
        initProvider(RESULTS);
        expected = "Smr 3+";
        assertEquals(expected, details.getText().toString());
        details.setText("");

        results.put(TEST_RESULT, "scanty");
        when(resultsRepository.getLatestResults("255c9df9-42ba-424d-a235-bd4ea5da77ae")).thenReturn(results);
        initProvider(RESULTS);
        expected = "Smr Sty";
        assertEquals(expected, details.getText().toString());
        details.setText("");

        results.put(TEST_RESULT, "negative");
        when(resultsRepository.getLatestResults("255c9df9-42ba-424d-a235-bd4ea5da77ae")).thenReturn(results);
        initProvider(RESULTS);
        expected = "Smr Neg";
        assertEquals(expected, details.getText().toString());
        details.setText("");

    }


    private class RegisterActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {//Onclick
        }
    }
}
