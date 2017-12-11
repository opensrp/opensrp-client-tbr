package org.smartregister.tbr.provider;

import android.content.Context;
import android.database.Cursor;
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
import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.InTreatmentPatientRegisterActivity;
import org.smartregister.tbr.activity.PositivePatientRegisterActivity;
import org.smartregister.tbr.activity.PresumptivePatientRegisterActivity;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.tbr.repository.ResultsRepository;
import org.smartregister.util.DateUtil;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import util.TbrConstants;
import util.TbrConstants.KEY;
import util.TbrSpannableStringBuilder;

import static org.smartregister.tbr.R.id.diagnose_lnk;
import static org.smartregister.util.Utils.getName;
import static org.smartregister.util.Utils.getValue;
import static util.TbrConstants.REGISTER_COLUMNS.DIAGNOSE;
import static util.TbrConstants.REGISTER_COLUMNS.DIAGNOSIS;
import static util.TbrConstants.REGISTER_COLUMNS.DROPDOWN;
import static util.TbrConstants.REGISTER_COLUMNS.ENCOUNTER;
import static util.TbrConstants.REGISTER_COLUMNS.FOLLOWUP;
import static util.TbrConstants.REGISTER_COLUMNS.INTREATMENT_RESULTS;
import static util.TbrConstants.REGISTER_COLUMNS.PATIENT;
import static util.TbrConstants.REGISTER_COLUMNS.RESULTS;
import static util.TbrConstants.REGISTER_COLUMNS.TREAT;
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

    private ForegroundColorSpan redForegroundColorSpan;
    private ForegroundColorSpan blackForegroundColorSpan;

    public PatientRegisterProvider(Context context, Set visibleColumns, View.OnClickListener onClickListener, ResultsRepository resultsRepository) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.visibleColumns = visibleColumns;
        this.onClickListener = onClickListener;
        this.resultsRepository = resultsRepository;
        redForegroundColorSpan = new ForegroundColorSpan(
                context.getResources().getColor(android.R.color.holo_red_dark));
        blackForegroundColorSpan = new ForegroundColorSpan(
                context.getResources().getColor(android.R.color.black));
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, View convertView) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, client, convertView);
            if (context instanceof PresumptivePatientRegisterActivity) {
                populateResultsColumn(pc, client, convertView);
                populateDiagnoseColumn(client, convertView);
            } else if (context instanceof PositivePatientRegisterActivity) {
                populateResultsColumn(pc, client, convertView);
                populateTreatColumn(client, convertView);
            } else if (context instanceof InTreatmentPatientRegisterActivity) {
                populateIntreatmentResultsColumn(pc, client, convertView);
                populateFollowupColumn(pc, client, convertView);
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
                    populateDiagnoseColumn(client, convertView);
                    break;
                case ENCOUNTER:
                    populateEncounterColumn(pc, convertView);
                    break;
                case XPERT_RESULTS:
                    populateXpertResultsColumn(pc, client, convertView);
                    break;
                case DROPDOWN:
                    populateDropdownColumn(client, convertView);
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
                case FOLLOWUP:
                    populateFollowupColumn(pc, client, convertView);
                    break;
            }
        }
        Map<String, Integer> mapping = new HashMap();
        mapping.put(PATIENT, R.id.patient_column);
        mapping.put(RESULTS, R.id.results_column);
        mapping.put(DIAGNOSE, R.id.diagnose_column);
        mapping.put(ENCOUNTER, R.id.encounter_column);
        mapping.put(XPERT_RESULTS, R.id.xpert_results_column);
        mapping.put(DROPDOWN, R.id.dropdown_column);
        mapping.put(TREAT, R.id.treat_column);
        mapping.put(DIAGNOSIS, R.id.diagnosis_column);
        mapping.put(INTREATMENT_RESULTS, R.id.results_column);
        mapping.put(FOLLOWUP, R.id.followup_column);
        TbrApplication.getInstance().getConfigurableViewsHelper().processRegisterColumns(mapping, convertView, visibleColumns, R.id.register_columns);

    }

    private View populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, View view) {

        String firstName = getValue(pc.getColumnmaps(), KEY.FIRST_NAME, true);
        String lastName = getValue(pc.getColumnmaps(), KEY.LAST_NAME, true);
        String patientName = getName(firstName, lastName);

        fillValue((TextView) view.findViewById(R.id.patient_name), patientName);

        fillValue((TextView) view.findViewById(R.id.participant_id), "#" + getValue(pc.getColumnmaps(), KEY.TBREACH_ID, false));

        String gender = getValue(pc.getColumnmaps(), KEY.GENDER, true);

        fillValue((TextView) view.findViewById(R.id.gender), gender);

        String dobString = getDuration(getValue(pc.getColumnmaps(), KEY.DOB, false));

        fillValue((TextView) view.findViewById(R.id.age), dobString.substring(0, dobString.indexOf("y")));

        View patient = view.findViewById(R.id.patient_column);
        attachOnclickListener(patient, client);
        return patient;
    }

    private TbrSpannableStringBuilder populateXpertResult(Map<String, String> testResults, TbrSpannableStringBuilder stringBuilder, boolean withOtherResults) {
        if (testResults.containsKey(TbrConstants.RESULT.MTB_RESULT)) {
            ForegroundColorSpan colorSpan = withOtherResults ? redForegroundColorSpan : blackForegroundColorSpan;
            stringBuilder.append(withOtherResults ? "Xpe " : "MTB ");
            stringBuilder.append(processXpertResult(testResults.get(TbrConstants.RESULT.MTB_RESULT)), redForegroundColorSpan);
            stringBuilder.append(withOtherResults ? "/ " : " RIF ");
            stringBuilder.append(processXpertResult(testResults.get(TbrConstants.RESULT.RIF_RESULT)), colorSpan);
        }
        return stringBuilder;
    }

    private String processXpertResult(String result) {
        if (result == null)
            return "-ve";
        switch (result) {
            case DETECTED:
                return "+ve";
            case NOT_DETECTED:
                return "-ve";
            case INDETERMINATE:
                return "?";
            default:
                return result;
        }
    }

    private View populateResultsColumn(CommonPersonObjectClient pc, SmartRegisterClient client, View view) {
        return populateResultsColumn(pc, client, view, new TbrSpannableStringBuilder(), false);
    }

    private View populateResultsColumn(CommonPersonObjectClient pc, SmartRegisterClient client, View view, TbrSpannableStringBuilder stringBuilder, boolean afterBaseline) {
        View result = view.findViewById(R.id.result_lnk);
        attachOnclickListener(result, client);
        TextView results = (TextView) view.findViewById(R.id.result_details);
        attachOnclickListener(results, client);
        String baseEntityId = getValue(pc.getColumnmaps(), KEY.BASE_ENTITY_ID_COLUMN, false);
        Map<String, String> testResults;
        if (afterBaseline) {
            long baseline = Long.valueOf(getValue(pc.getColumnmaps(), KEY.BASELINE, false));
            testResults = resultsRepository.getLatestResults(baseEntityId, afterBaseline, baseline);
        } else
            testResults = resultsRepository.getLatestResults(baseEntityId);
        stringBuilder = populateXpertResult(testResults, stringBuilder, true);
        if (testResults.containsKey(TbrConstants.RESULT.TEST_RESULT)) {
            if (stringBuilder.length() > 0)
                stringBuilder.append(",\n");
            stringBuilder.append("Smr ");
            switch (testResults.get(TbrConstants.RESULT.TEST_RESULT)) {
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
                    stringBuilder.append("Scty", redForegroundColorSpan);
                    break;
                case "negative":
                    stringBuilder.append("Neg", redForegroundColorSpan);
                    break;
                default:
            }
        }

        if (testResults.containsKey(TbrConstants.RESULT.CULTURE_RESULT)) {
            if (stringBuilder.length() > 0)
                stringBuilder.append(", ");
            stringBuilder.append("Cul ");
            stringBuilder.append(WordUtils.capitalizeFully(testResults.get(TbrConstants.RESULT.CULTURE_RESULT).substring(0, 3)), blackForegroundColorSpan);
        }
        if (testResults.containsKey(TbrConstants.RESULT.XRAY_RESULT)) {
            if (stringBuilder.length() > 0)
                stringBuilder.append(",\n");
            stringBuilder.append("CXR ");
            if (testResults.get(TbrConstants.RESULT.XRAY_RESULT).equals("indicative"))
                stringBuilder.append("Ind", blackForegroundColorSpan);
            else
                stringBuilder.append("NonI", blackForegroundColorSpan);

        }
        if (stringBuilder.length() > 0) {
            results.setVisibility(View.VISIBLE);
            results.setText(stringBuilder);
            adjustLayoutParams(result);
        } else
            results.setVisibility(View.GONE);
        return view.findViewById(R.id.results_column);
    }

    private View populateDiagnoseColumn(SmartRegisterClient client, View view) {
        attachOnclickListener(view.findViewById(diagnose_lnk), client);
        return view.findViewById(R.id.diagnose_column);
    }

    private View populateTreatColumn(SmartRegisterClient client, View view) {
        attachOnclickListener(view.findViewById(R.id.treat_lnk), client);
        return view.findViewById(R.id.treat_column);
    }

    private View populateDropdownColumn(SmartRegisterClient client, View view) {
        attachOnclickListener(view.findViewById(R.id.dropdown_btn), client);
        return view.findViewById(R.id.dropdown_column);
    }

    private View populateEncounterColumn(CommonPersonObjectClient pc, View view) {
        String firstEncounter = getValue(pc.getColumnmaps(), KEY.FIRST_ENCOUNTER, false);
        fillValue((TextView) view.findViewById(R.id.encounter), getDuration(firstEncounter) + " ago");
        return view.findViewById(R.id.encounter_column);
    }

    private View populateDiagnosisColumn(CommonPersonObjectClient pc, View view) {
        String diagnosis = getValue(pc.getColumnmaps(), KEY.DIAGNOSIS_DATE, false);
        if (!diagnosis.isEmpty())
            fillValue((TextView) view.findViewById(R.id.diagnosis), getDuration(diagnosis) + " ago");
        return view.findViewById(R.id.diagnosis_column);
    }

    public String getDuration(String date) {
        DateTime duration;
        if (StringUtils.isNotBlank(date)) {
            try {
                duration = new DateTime(date);
                return DateUtil.getDuration(duration);
            } catch (Exception e) {
                Log.e(getClass().getName(), e.toString(), e);
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

    private View populateXpertResultsColumn(CommonPersonObjectClient pc, SmartRegisterClient client, View view) {
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

        return view.findViewById(R.id.xpert_results_column);
    }

    private View populateIntreatmentResultsColumn(CommonPersonObjectClient pc, SmartRegisterClient client, View view) {
        String treatmentStartDate = getValue(pc.getColumnmaps(), KEY.TREATMENT_INITIATION_DATE, false);
        TbrSpannableStringBuilder stringBuilder = new TbrSpannableStringBuilder();
        if (!treatmentStartDate.isEmpty()) {
            TextView results = (TextView) view.findViewById(R.id.result_details);
            results.setVisibility(View.VISIBLE);
            stringBuilder.append(getDuration(treatmentStartDate) + " ago\n");
            populateResultsColumn(pc, client, view, stringBuilder, true);
        }
        return view;
    }

    private View populateFollowupColumn(CommonPersonObjectClient pc, SmartRegisterClient client, View view) {
        attachOnclickListener(view.findViewById(R.id.followup), client);
        String treatmentStart = getValue(pc.getColumnmaps(), KEY.NEXT_VISIT_DATE, false);
        if (!treatmentStart.isEmpty()) {
            DateTime treatmentStartDate = DateTime.parse(treatmentStart);
            fillValue((TextView) view.findViewById(R.id.followup_text), "Followup\n due " + treatmentStartDate.toString("dd/MM"));
            int due = Days.daysBetween(new DateTime(), treatmentStartDate).getDays();
            if (due < 0)
                view.findViewById(R.id.followup).setBackgroundResource(R.drawable.due_vaccine_red_bg);
            else if (due == 0)
                view.findViewById(R.id.followup).setBackgroundResource(R.drawable.due_vaccine_blue_bg);
            else {
                ((TextView) view.findViewById(R.id.followup_text)).setTextColor(context.getResources().getColor(R.color.client_list_grey));
                view.findViewById(R.id.followup).setBackgroundResource(R.drawable.due_vaccine_na_bg);
            }

        }
        return view;
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
        ViewConfiguration viewConfiguration = TbrApplication.getInstance().getConfigurableViewsHelper().getViewConfiguration(viewIdentifier);
        ViewConfiguration commonConfiguration = TbrApplication.getInstance().getConfigurableViewsHelper().getViewConfiguration(COMMON_REGISTER_ROW);
        if (viewConfiguration == null) {
            return view;
        } else {
            return TbrApplication.getInstance().getConfigurableViewsHelper().inflateDynamicView(viewConfiguration, commonConfiguration, view, R.id.register_columns, false);
        }
    }

    public static void fillValue(TextView v, String value) {
        if (v != null)
            v.setText(value);
    }

}
