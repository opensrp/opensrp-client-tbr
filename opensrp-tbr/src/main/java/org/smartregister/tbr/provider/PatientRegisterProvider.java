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
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.PositivePatientRegisterActivity;
import org.smartregister.tbr.activity.PresumptivePatientRegisterActivity;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
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
import static org.smartregister.util.Utils.fillValue;
import static org.smartregister.util.Utils.getName;
import static org.smartregister.util.Utils.getValue;
import static util.TbrConstants.REGISTER_COLUMNS.DIAGNOSE;
import static util.TbrConstants.REGISTER_COLUMNS.DIAGNOSIS;
import static util.TbrConstants.REGISTER_COLUMNS.DROPDOWN;
import static util.TbrConstants.REGISTER_COLUMNS.ENCOUNTER;
import static util.TbrConstants.REGISTER_COLUMNS.PATIENT;
import static util.TbrConstants.REGISTER_COLUMNS.RESULTS;
import static util.TbrConstants.REGISTER_COLUMNS.TREAT;
import static util.TbrConstants.REGISTER_COLUMNS.XPERT_RESULTS;
import static util.TbrConstants.VIEW_CONFIGS.COMMON_REGISTER_ROW;
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
    private DetailsRepository detailsRepository;

    private static final String DETECTED = "detected";
    private static final String NOT_DETECTED = "not_detected";
    private static final String INDETERMINATE = "indeterminate";

    private ForegroundColorSpan redForegroundColorSpan;
    private ForegroundColorSpan blackForegroundColorSpan;

    private View dynamicRow;

    public PatientRegisterProvider(Context context, Set visibleColumns, View.OnClickListener onClickListener, DetailsRepository detailsRepository) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.visibleColumns = visibleColumns;
        this.onClickListener = onClickListener;
        this.detailsRepository = detailsRepository;
        redForegroundColorSpan = new ForegroundColorSpan(
                context.getResources().getColor(android.R.color.holo_red_dark));
        blackForegroundColorSpan = new ForegroundColorSpan(
                context.getResources().getColor(android.R.color.black));
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, View convertView) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty() || visibleColumns.size() > 3) {
            populatePatientColumn(pc, client, convertView);
            populateResultsColumn(pc, client, convertView);
            if (context instanceof PresumptivePatientRegisterActivity)
                populateDiagnoseColumn(client, convertView);
            else if (context instanceof PositivePatientRegisterActivity)
                populateTreatColumn(client, convertView);
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

        DateTime birthDateTime;
        String dobString = getValue(pc.getColumnmaps(), KEY.DOB, false);
        String age = "";
        if (StringUtils.isNotBlank(dobString)) {
            try {
                birthDateTime = new DateTime(dobString);
                String duration = DateUtil.getDuration(birthDateTime);
                if (duration != null) {
                    age = duration.substring(0, duration.length() - 1);
                }
            } catch (Exception e) {
                Log.e(getClass().getName(), e.toString(), e);
            }
        }
        fillValue((TextView) view.findViewById(R.id.age), age);

        View patient = view.findViewById(R.id.patient_column);
        attachOnclickListener(patient, client);
        return patient;
    }

    private TbrSpannableStringBuilder populateXpertResult(Map<String, String> testResults, boolean withOtherResults) {
        TbrSpannableStringBuilder stringBuilder = new TbrSpannableStringBuilder();
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
            result = NOT_DETECTED;
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
        View result = view.findViewById(R.id.result_lnk);
        attachOnclickListener(result, client);
        TextView results = (TextView) view.findViewById(R.id.result_details);
        attachOnclickListener(results, client);

        Map<String, String> testResults = detailsRepository.getAllDetailsForClient(getValue(pc.getColumnmaps(), KEY.BASE_ENTITY_ID_COLUMN, false));
        TbrSpannableStringBuilder stringBuilder = populateXpertResult(testResults, true);
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
        DateTime encounterTime;
        String lastEncounter = getValue(pc.getColumnmaps(), KEY.FIRST_ENCOUNTER, false);
        String duration = "";
        if (StringUtils.isNotBlank(lastEncounter)) {
            try {
                encounterTime = new DateTime(lastEncounter);
                duration = DateUtil.getDuration(encounterTime);
            } catch (Exception e) {
                Log.e(getClass().getName(), e.toString(), e);
            }
        }
        fillValue((TextView) view.findViewById(R.id.encounter), duration + " ago");
        return view.findViewById(R.id.encounter_column);
    }

    private View populateDiagnosisColumn(CommonPersonObjectClient pc, View view) {
        DateTime diagnosisTime;
        String diagnosis = getValue(pc.getColumnmaps(), KEY.FIRST_ENCOUNTER, false);
        String duration = "";
        if (StringUtils.isNotBlank(diagnosis)) {
            try {
                diagnosisTime = new DateTime(diagnosis);
                duration = DateUtil.getDuration(diagnosisTime);
            } catch (Exception e) {
                Log.e(getClass().getName(), e.toString(), e);
            }
        }
        fillValue((TextView) view.findViewById(R.id.diagnosis), duration + " ago");
        return view.findViewById(R.id.diagnosis_column);
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

        Map<String, String> testResults = detailsRepository.getAllDetailsForClient(getValue(pc.getColumnmaps(), KEY.BASE_ENTITY_ID_COLUMN, false));

        TbrSpannableStringBuilder stringBuilder = populateXpertResult(testResults, false);

        if (stringBuilder.length() > 0) {
            adjustLayoutParams(result);
            results.setVisibility(View.VISIBLE);
            results.setText(stringBuilder);
        }

        return view.findViewById(R.id.xpert_results_column);
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
        int viewResourceId;
        if (context instanceof PresumptivePatientRegisterActivity) {
            viewIdentifier = PRESUMPTIVE_REGISTER_ROW;
            viewResourceId = R.layout.register_presumptive_list_row;
        } else {
            viewIdentifier = POSITIVE_REGISTER_ROW;
            viewResourceId = R.layout.register_positive_list_row;
        }
        ViewConfiguration viewConfiguration = TbrApplication.getInstance().getConfigurableViewsHelper().getViewConfiguration(viewIdentifier);
        ViewConfiguration commonConfiguration = TbrApplication.getInstance().getConfigurableViewsHelper().getViewConfiguration(COMMON_REGISTER_ROW);
        View view = inflater.inflate(viewResourceId, null);
        if (viewConfiguration == null) {
            return view;
        } else {
            return getDynamicRowView(viewConfiguration, view, commonConfiguration);
        }
    }

    private View getDynamicRowView(ViewConfiguration viewConfiguration, View view, ViewConfiguration commonConfiguration) {
        if (dynamicRow == null) {
            dynamicRow = TbrApplication.getInstance().getConfigurableViewsHelper().inflateDynamicView(viewConfiguration, commonConfiguration, R.id.register_columns,false);
        }
        ViewGroup insertPoint = (ViewGroup) view.findViewById(R.id.register_columns);
        insertPoint.addView(dynamicRow);
        return insertPoint;
    }
}
