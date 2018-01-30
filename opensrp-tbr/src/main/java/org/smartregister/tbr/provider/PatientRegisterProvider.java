package org.smartregister.tbr.provider;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.InTreatmentPatientRegisterActivity;
import org.smartregister.tbr.activity.PositivePatientRegisterActivity;
import org.smartregister.tbr.activity.PresumptivePatientRegisterActivity;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.jsonspec.ConfigurableViewsHelper;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.tbr.repository.ResultsRepository;
import org.smartregister.util.DateUtil;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.TbrConstants;
import util.TbrConstants.KEY;
import util.TbrSpannableStringBuilder;

import static org.smartregister.tbr.R.id.diagnose_lnk;
import static org.smartregister.tbr.repository.ResultsRepository.DATE;
import static org.smartregister.util.Utils.getName;
import static org.smartregister.util.Utils.getValue;
import static util.TbrConstants.REGISTER_COLUMNS.BASELINE;
import static util.TbrConstants.REGISTER_COLUMNS.DIAGNOSE;
import static util.TbrConstants.REGISTER_COLUMNS.DIAGNOSIS;
import static util.TbrConstants.REGISTER_COLUMNS.FOLLOWUP;
import static util.TbrConstants.REGISTER_COLUMNS.FOLLOWUP_SCHEDULE;
import static util.TbrConstants.REGISTER_COLUMNS.INTREATMENT_RESULTS;
import static util.TbrConstants.REGISTER_COLUMNS.PATIENT;
import static util.TbrConstants.REGISTER_COLUMNS.RESULTS;
import static util.TbrConstants.REGISTER_COLUMNS.SMEAR_RESULTS;
import static util.TbrConstants.REGISTER_COLUMNS.SMEAR_SCHEDULE;
import static util.TbrConstants.REGISTER_COLUMNS.TREAT;
import static util.TbrConstants.REGISTER_COLUMNS.TREATMENT;
import static util.TbrConstants.REGISTER_COLUMNS.XPERT_RESULTS;
import static util.TbrConstants.VIEW_CONFIGS.COMMON_REGISTER_ROW;
import static util.TbrConstants.VIEW_CONFIGS.INTREATMENT_REGISTER_ROW;
import static util.TbrConstants.VIEW_CONFIGS.POSITIVE_REGISTER_ROW;
import static util.TbrConstants.VIEW_CONFIGS.PRESUMPTIVE_REGISTER_ROW;

/**
 * Created by samuelgithengi on 11/8/17.
 */

public class PatientRegisterProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private final LayoutInflater inflater;
    private Context context;
    private Set<org.smartregister.tbr.jsonspec.model.View> visibleColumns;
    private View.OnClickListener onClickListener;
    private ResultsRepository resultsRepository;

    private static final String DETECTED = "detected";
    private static final String NOT_DETECTED = "not_detected";
    private static final String INDETERMINATE = "indeterminate";
    private static final String ERROR = "error";
    private static final String NO_RESULT = "no_result";

    private ForegroundColorSpan redForegroundColorSpan;
    private ForegroundColorSpan blackForegroundColorSpan;
    private DetailsRepository detailsRepository;


    private static final String TAG = PatientRegisterProvider.class.getCanonicalName();


    public PatientRegisterProvider(Context context, Set visibleColumns, View.OnClickListener onClickListener, ResultsRepository resultsRepository, DetailsRepository detailsRepository) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.visibleColumns = visibleColumns;
        this.onClickListener = onClickListener;
        this.resultsRepository = resultsRepository;
        redForegroundColorSpan = new ForegroundColorSpan(
                context.getResources().getColor(android.R.color.holo_red_dark));
        blackForegroundColorSpan = new ForegroundColorSpan(
                context.getResources().getColor(android.R.color.black));
        this.detailsRepository = detailsRepository;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, View convertView) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, client, convertView);
            if (context instanceof PresumptivePatientRegisterActivity) {
                populateResultsColumn(pc, client, convertView);
                populateDiagnoseColumn(pc, client, convertView);
            } else if (context instanceof PositivePatientRegisterActivity) {
                populateResultsColumn(pc, client, convertView);
                populateTreatColumn(client, convertView);
            } else if (context instanceof InTreatmentPatientRegisterActivity) {
                populateIntreatmentResultsColumn(pc, client, convertView);
                populateFollowupScheduleColumn(pc, client, convertView);
            }
            return;
        }
        for (org.smartregister.tbr.jsonspec.model.View columnView : visibleColumns) {
            switch (columnView.getIdentifier()) {
                case PATIENT:
                    populatePatientColumn(pc, client, convertView);
                    break;
                case RESULTS:
                    populateResultsColumn(pc, client, convertView);
                    break;
                case DIAGNOSE:
                    populateDiagnoseColumn(pc, client, convertView);
                    break;
                case XPERT_RESULTS:
                    populateXpertResultsColumn(pc, client, convertView);
                    break;
                case SMEAR_RESULTS:
                    populateSmearResultsColumn(pc, client, convertView);
                    break;
                case TREAT:
                    populateTreatColumn(client, convertView);
                    break;
                case DIAGNOSIS:
                    populateDiagnosisColumn(pc, convertView);
                    break;
                case INTREATMENT_RESULTS:
                    populateIntreatmentResultsColumn(pc, client, convertView);
                    break;
                case FOLLOWUP_SCHEDULE:
                    populateFollowupScheduleColumn(pc, client, convertView);
                    break;
                case SMEAR_SCHEDULE:
                    populateSmearScheduleColumn(pc, client, convertView);
                    break;
                case FOLLOWUP:
                    populateFollowupColumn(convertView, client);
                    break;
                case TREATMENT:
                    populateTreatmentColumn(pc, convertView);
                    break;
                case BASELINE:
                    populateBaselineColumn(pc, client, convertView);
                    break;
            }
        }

        Map<String, Integer> mapping = new HashMap();
        mapping.put(PATIENT, R.id.patient_column);
        mapping.put(RESULTS, R.id.results_column);
        mapping.put(DIAGNOSE, R.id.diagnose_column);
        mapping.put(XPERT_RESULTS, R.id.xpert_results_column);
        mapping.put(SMEAR_RESULTS, R.id.smr_results_column);
        mapping.put(TREAT, R.id.treat_column);
        mapping.put(DIAGNOSIS, R.id.diagnosis_column);
        mapping.put(INTREATMENT_RESULTS, R.id.intreatment_results_column);
        mapping.put(FOLLOWUP, R.id.followup_column);
        mapping.put(FOLLOWUP_SCHEDULE, R.id.followup_schedule_column);
        mapping.put(BASELINE, R.id.baseline_column);
        mapping.put(TREATMENT, R.id.treatment_column);
        mapping.put(SMEAR_SCHEDULE, R.id.smr_schedule_column);
        TbrApplication.getInstance().getConfigurableViewsHelper().processRegisterColumns(mapping, convertView, visibleColumns, R.id.register_columns);
    }

    private void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, View view) {

        String firstName = getValue(pc.getColumnmaps(), KEY.FIRST_NAME, true);
        String lastName = getValue(pc.getColumnmaps(), KEY.LAST_NAME, true);
        String patientName = getName(firstName, lastName);

        fillValue((TextView) view.findViewById(R.id.patient_name), patientName);

        fillValue((TextView) view.findViewById(R.id.participant_id), "#" + getValue(pc.getColumnmaps(), KEY.PARTICIPANT_ID, false));

        String gender = getValue(pc.getColumnmaps(), KEY.GENDER, true);

        fillValue((TextView) view.findViewById(R.id.gender), gender);

        String dobString = getDuration(getValue(pc.getColumnmaps(), KEY.DOB, false));

        fillValue((TextView) view.findViewById(R.id.age), dobString.substring(0, dobString.indexOf("y")));

        View patient = view.findViewById(R.id.patient_column);
        attachOnclickListener(patient, client);
    }

    private boolean populateXpertResult(Map<String, String> testResults, TbrSpannableStringBuilder stringBuilder, boolean withOtherResults) {
        if (testResults.containsKey(TbrConstants.RESULT.MTB_RESULT)) {
            stringBuilder.append(withOtherResults ? "Xpe " : "MTB ");
            String mtbResult = testResults.get(TbrConstants.RESULT.MTB_RESULT);
            processXpertResult(mtbResult, stringBuilder);
            if (testResults.containsKey(TbrConstants.RESULT.ERROR_CODE)) {
                stringBuilder.append(" ");
                stringBuilder.append(testResults.get(TbrConstants.RESULT.ERROR_CODE), blackForegroundColorSpan);
            } else if (testResults.containsKey(TbrConstants.RESULT.RIF_RESULT)) {
                stringBuilder.append(withOtherResults ? "/ " : "\nRIF ");
                processXpertResult(testResults.get(TbrConstants.RESULT.RIF_RESULT), stringBuilder);
            }
            return true;
        }
        return false;
    }

    private void processXpertResult(String result, TbrSpannableStringBuilder stringBuilder) {
        if (result == null)
            return;
        switch (result) {
            case DETECTED:
                stringBuilder.append("+ve", redForegroundColorSpan);
                break;
            case NOT_DETECTED:
                stringBuilder.append("-ve", blackForegroundColorSpan);
                break;
            case INDETERMINATE:
                stringBuilder.append("?", blackForegroundColorSpan);
                break;
            case ERROR:
                stringBuilder.append("err", blackForegroundColorSpan);
                break;
            case NO_RESULT:
                stringBuilder.append("No result", blackForegroundColorSpan);
                break;
            default:
                break;
        }
    }

    private void populateResultsColumn(CommonPersonObjectClient pc, SmartRegisterClient client, View view) {
        View button = view.findViewById(R.id.result_lnk);
        TextView details = (TextView) view.findViewById(R.id.result_details);
        details.setText(new String());
        populateResultsColumn(pc, client, new TbrSpannableStringBuilder(), false, null, button, details);
    }

    private void populateResultsColumn(CommonPersonObjectClient pc, SmartRegisterClient client, TbrSpannableStringBuilder stringBuilder, boolean singleResult, Long baseline, View button, TextView details) {
        if (button != null)
            attachOnclickListener(button, client);
        attachOnclickListener(details, client);
        String baseEntityId = getValue(pc.getColumnmaps(), KEY.BASE_ENTITY_ID_COLUMN, false);
        Map<String, String> testResults;
        if (baseline != null)
            testResults = resultsRepository.getLatestResults(baseEntityId, false, baseline);
        else if (singleResult) {
            testResults = resultsRepository.getLatestResult(baseEntityId);
            String results = testResults.get(DATE);
            if (StringUtils.isNotEmpty(results))
                stringBuilder.append(new DateTime(Long.valueOf(results)).toString("dd/MM/yyyy") + "\n");
        } else
            testResults = resultsRepository.getLatestResults(baseEntityId);
        boolean hasXpert = populateXpertResult(testResults, stringBuilder, true);
        populateSmearResult(stringBuilder, testResults.get(TbrConstants.RESULT.TEST_RESULT), hasXpert, false);
        populateCultureResults(stringBuilder, testResults.get(TbrConstants.RESULT.CULTURE_RESULT));
        populateXrayResults(stringBuilder, testResults.get(TbrConstants.RESULT.XRAY_RESULT));
        if (stringBuilder.length() > 0) {
            details.setVisibility(View.VISIBLE);
            details.append(stringBuilder);
            if (button != null)
                adjustLayoutParams(button);
        } else
            details.setVisibility(View.GONE);
    }

    private void populateSmearResult(TbrSpannableStringBuilder stringBuilder, String result, boolean hasXpert, boolean smearOnlyColumn) {
        if (result == null) return;
        else if (hasXpert && !smearOnlyColumn)
            stringBuilder.append(",\n");
        if (!smearOnlyColumn)
            stringBuilder.append("Smr ");
        switch (result) {
            case "one_plus":
                stringBuilder.append("1+", redForegroundColorSpan);
                break;
            case "two_plus":
                stringBuilder.append("2+", redForegroundColorSpan);
                break;
            case "three_plus":
                stringBuilder.append("3+", redForegroundColorSpan);
                break;
            case "scanty":
                stringBuilder.append(smearOnlyColumn ? "Scanty" : "Sty", redForegroundColorSpan);
                break;
            case "negative":
                stringBuilder.append(smearOnlyColumn ? "Negative" : "Neg", blackForegroundColorSpan);
                break;
            default:
        }
    }

    private void populateXrayResults(TbrSpannableStringBuilder stringBuilder, String result) {
        if (result == null)
            return;
        else if (stringBuilder.length() > 0)
            stringBuilder.append(", ");
        stringBuilder.append("CXR ");
        if ("indicative".equals(result))
            stringBuilder.append("Ind", blackForegroundColorSpan);
        else
            stringBuilder.append("NonI", blackForegroundColorSpan);

    }

    private void populateCultureResults(TbrSpannableStringBuilder stringBuilder, String result) {
        if (result == null)
            return;
        else if (stringBuilder.length() > 0)
            stringBuilder.append("\n");
        stringBuilder.append("Cul ");
        stringBuilder.append(WordUtils.capitalizeFully(result).substring(0, 3), blackForegroundColorSpan);
    }

    private void populateDiagnoseColumn(CommonPersonObjectClient pc, SmartRegisterClient client, View view) {
        String firstEncounter = getValue(pc.getColumnmaps(), KEY.FIRST_ENCOUNTER, false);
        fillValue((TextView) view.findViewById(R.id.encounter), "Scr Date:\n" + formatDate(firstEncounter));
        attachOnclickListener(view.findViewById(diagnose_lnk), client);
    }

    private void populateTreatColumn(SmartRegisterClient client, View view) {
        attachOnclickListener(view.findViewById(R.id.treat_lnk), client);
    }

    private void populateSmearResultsColumn(CommonPersonObjectClient pc, SmartRegisterClient client, View view) {
        View result = view.findViewById(R.id.smr_result_lnk);
        attachOnclickListener(result, client);

        TextView results = (TextView) view.findViewById(R.id.smr_result_details);
        attachOnclickListener(results, client);

        Map<String, String> testResults = resultsRepository.getLatestResults(getValue(pc.getColumnmaps(), KEY.BASE_ENTITY_ID_COLUMN, false));

        TbrSpannableStringBuilder stringBuilder = new TbrSpannableStringBuilder();
        populateSmearResult(stringBuilder, testResults.get(TbrConstants.RESULT.TEST_RESULT), false, true);
        if (stringBuilder.length() > 0) {
            adjustLayoutParams(result);
            results.setVisibility(View.VISIBLE);
            results.setText(stringBuilder);
        }
    }

    private void populateDiagnosisColumn(CommonPersonObjectClient pc, View view) {
        String diagnosis = getValue(pc.getColumnmaps(), KEY.DIAGNOSIS_DATE, false);
        if (!diagnosis.isEmpty())
            fillValue((TextView) view.findViewById(R.id.diagnosis), formatDate(diagnosis));
    }

    public String formatDate(String date) {
        return StringUtils.isNotEmpty(date) ? new DateTime(date).toString("dd/MM/yyyy") : date;
    }

    public String getDuration(String date) {
        DateTime duration;
        if (StringUtils.isNotBlank(date)) {
            try {
                duration = new DateTime(date);
                return DateUtil.getDuration(duration);
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
        return "";
    }

    private void adjustLayoutParams(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        view.setLayoutParams(params);
    }

    private void attachOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
    }

    private void populateXpertResultsColumn(CommonPersonObjectClient pc, SmartRegisterClient client, View view) {
        View result = view.findViewById(R.id.xpert_result_lnk);
        attachOnclickListener(result, client);

        TextView results = (TextView) view.findViewById(R.id.xpert_result_details);
        attachOnclickListener(results, client);

        Map<String, String> testResults = resultsRepository.getLatestResults(getValue(pc.getColumnmaps(), KEY.BASE_ENTITY_ID_COLUMN, false));

        TbrSpannableStringBuilder stringBuilder = new TbrSpannableStringBuilder();
        populateXpertResult(testResults, stringBuilder, false);

        if (stringBuilder.length() > 0) {
            adjustLayoutParams(result);
            results.setVisibility(View.VISIBLE);
            results.setText(stringBuilder);
        }

    }

    private void populateIntreatmentResultsColumn(CommonPersonObjectClient pc, SmartRegisterClient client, View view) {
        TbrSpannableStringBuilder stringBuilder = new TbrSpannableStringBuilder();
        TextView results = (TextView) view.findViewById(R.id.intreatment_details);
        View button = view.findViewById(R.id.intreatment_lnk);
        results.setVisibility(View.VISIBLE);
        results.setText(stringBuilder);
        populateResultsColumn(pc, client, stringBuilder, true, null, button, results);
    }

    private void populateFollowupScheduleColumn(CommonPersonObjectClient pc, SmartRegisterClient client, View view) {
        View followup = view.findViewById(R.id.followup);
        TextView followupText = (TextView) view.findViewById(R.id.followup_text);
        attachOnclickListener(followup, client);
        String nextVisit = getValue(pc.getColumnmaps(), KEY.NEXT_VISIT_DATE, false);
        if (!nextVisit.isEmpty()) {
            DateTime treatmentStartDate = DateTime.parse(nextVisit);
            fillValue(followupText, "Followup\n due " + treatmentStartDate.toString("dd/MM/yy"));
            int due = Days.daysBetween(new DateTime(), treatmentStartDate).getDays();
            if (due < 0) {
                followup.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                followupText.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            } else if (due == 0) {
                followup.setBackgroundResource(R.drawable.due_vaccine_blue_bg);
                followupText.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            } else {
                followupText.setTextColor(context.getResources().getColor(R.color.client_list_grey));
                followup.setBackgroundResource(R.drawable.due_vaccine_na_bg);
            }
        } else {
            followup.setBackgroundResource(R.drawable.due_vaccine_na_bg);
            followupText.setText(R.string.followup);
        }
    }

    private void populateSmearScheduleColumn(CommonPersonObjectClient pc, SmartRegisterClient client, View view) {
        View followup = view.findViewById(R.id.smr_schedule);
        TextView followupText = (TextView) view.findViewById(R.id.smr_schedule_text);
        attachOnclickListener(followup, client);
        String nextVisit = getValue(pc.getColumnmaps(), KEY.SMR_NEXT_VISIT_DATE, false);
        int due = 1;
        if (!nextVisit.isEmpty()) {
            try {
                DateTime treatmentStartDate = DateTime.parse(nextVisit);
                fillValue(followupText, "Smear\n due " + treatmentStartDate.toString("dd/MM/yy"));
                due = Days.daysBetween(new DateTime(), treatmentStartDate).getDays();

            } catch (IllegalArgumentException e) {
                Log.w(TAG, "populateSmearScheduleColumn: " + e.getMessage());
                fillValue(followupText, "Smear\n not due ");
            }
            if (due < 0) {
                followup.setBackgroundResource(R.drawable.due_vaccine_red_bg);
                followupText.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            } else if (due == 0) {
                followup.setBackgroundResource(R.drawable.due_vaccine_blue_bg);
                followupText.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            } else {
                followupText.setTextColor(context.getResources().getColor(R.color.client_list_grey));
                followup.setBackgroundResource(R.drawable.due_vaccine_na_bg);
            }
        } else {
            followup.setBackgroundResource(R.drawable.due_vaccine_na_bg);
            followupText.setText("Smear \nnot due");
        }
    }


    private void populateTreatmentColumn(CommonPersonObjectClient pc, View view) {
        String baseEntityId = getValue(pc.getColumnmaps(), KEY.BASE_ENTITY_ID_COLUMN, false);
        Map<String, String> details = detailsRepository.getAllDetailsForClient(baseEntityId);
        int months = 0;
        if (details.containsKey(KEY.TREATMENT_MONTH))
            months = Integer.valueOf(details.get(KEY.TREATMENT_MONTH));
        ((TextView) view.findViewById(R.id.treatment_started)).setText("Month " + months);
        ((TextView) view.findViewById(R.id.treatment_phase)).setText(StringUtils.capitalize(details.get(KEY.TREATMENT_PHASE)));
        List<String> regimen = new ArrayList();
        if (details.containsKey(KEY.TREATMENT_REGIMEN))
            regimen.add(details.get(KEY.TREATMENT_REGIMEN).toUpperCase());
        if (details.containsKey(KEY.OTHER_REGIMEN))
            regimen.add(details.get(KEY.OTHER_REGIMEN).toUpperCase());
        ((TextView) view.findViewById(R.id.regimen)).setText(TextUtils.join(" ", regimen));

    }

    private void populateBaselineColumn(CommonPersonObjectClient pc, SmartRegisterClient client, View view) {
        TbrSpannableStringBuilder stringBuilder = new TbrSpannableStringBuilder();
        String baselineStr = getValue(pc.getColumnmaps(), KEY.BASELINE, false);
        TextView results = (TextView) view.findViewById(R.id.baseline_details);
        long baseline = 0l;
        if (!baselineStr.isEmpty())
            baseline = Long.valueOf(baselineStr);
        populateResultsColumn(pc, client, stringBuilder, false, baseline, null, results);
    }

    private void populateFollowupColumn(View view, SmartRegisterClient client) {
        attachOnclickListener(view.findViewById(R.id.followup_lnk), client);

    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {//Implement Abstract Method
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        String viewIdentifier;
        View view;
        if (context instanceof PresumptivePatientRegisterActivity) {
            viewIdentifier = PRESUMPTIVE_REGISTER_ROW;
            view = inflater.inflate(R.layout.register_presumptive_list_row, null);
        } else if (context instanceof PositivePatientRegisterActivity) {
            viewIdentifier = POSITIVE_REGISTER_ROW;
            view = inflater.inflate(R.layout.register_positive_list_row, null);
        } else {
            viewIdentifier = INTREATMENT_REGISTER_ROW;
            view = inflater.inflate(R.layout.register_intreatment_list_row, null);
        }
        ConfigurableViewsHelper helper = TbrApplication.getInstance().getConfigurableViewsHelper();
        if (helper.isJsonViewsEnabled()) {
            ViewConfiguration viewConfiguration = helper.getViewConfiguration(viewIdentifier);
            ViewConfiguration commonConfiguration = helper.getViewConfiguration(COMMON_REGISTER_ROW);
            if (viewConfiguration != null) {
                return helper.inflateDynamicView(viewConfiguration, commonConfiguration, view, R.id.register_columns, false);
            }
        }
        return view;
    }

    public static void fillValue(TextView v, String value) {
        if (v != null)
            v.setText(value);

    }

}
