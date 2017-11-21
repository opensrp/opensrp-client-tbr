package org.smartregister.tbr.provider;

import android.content.Context;
import android.database.Cursor;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.DateTime;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
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
import util.TbrSpannableStringBuilder;

import static org.smartregister.util.Utils.fillValue;
import static org.smartregister.util.Utils.getName;
import static org.smartregister.util.Utils.getValue;
import static util.TbrConstants.REGISTER_COLUMNS.DIAGNOSE;
import static util.TbrConstants.REGISTER_COLUMNS.PATIENT;
import static util.TbrConstants.REGISTER_COLUMNS.RESULTS;

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

    public PatientRegisterProvider(Context context, Set visibleColumns, View.OnClickListener onClickListener, DetailsRepository detailsRepository) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.visibleColumns = visibleColumns;
        this.onClickListener = onClickListener;
        this.detailsRepository = detailsRepository;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, View convertView) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty() || visibleColumns.size() > 3) {
            populatePatientColumn(pc, convertView);
            populateDiagnoseColumn(client, convertView);
            populateResultsColumn(pc, client, convertView);
            return;
        }
        for (org.smartregister.tbr.jsonspec.model.View columnView : visibleColumns) {
            switch (columnView.getIdentifier()) {
                case PATIENT:
                    populatePatientColumn(pc, convertView);
                    break;
                case RESULTS:
                    populateResultsColumn(pc, client, convertView);
                    break;
                case DIAGNOSE:
                    populateDiagnoseColumn(client, convertView);
                    break;
            }
        }
        Map<String, Integer> mapping = new HashMap();
        mapping.put(PATIENT, R.id.patient_column);
        mapping.put(RESULTS, R.id.results_column);
        mapping.put(DIAGNOSE, R.id.diagnose_column);
        TbrApplication.getInstance().getConfigurableViewsHelper().processRegisterColumns(mapping, convertView, visibleColumns, R.id.register_columns);

    }

    private View populatePatientColumn(CommonPersonObjectClient pc, View view) {

        String firstName = getValue(pc.getColumnmaps(), TbrConstants.KEY.FIRST_NAME, true);
        String lastName = getValue(pc.getColumnmaps(), TbrConstants.KEY.LAST_NAME, true);
        String patientName = getName(firstName, lastName);

        fillValue((TextView) view.findViewById(R.id.patient_name), patientName);

        fillValue((TextView) view.findViewById(R.id.participant_id), getValue(pc.getColumnmaps(), TbrConstants.KEY.TBREACH_ID, false));


        String gender = getValue(pc.getColumnmaps(), TbrConstants.KEY.GENDER, true);

        DateTime birthDateTime;
        String dobString = getValue(pc.getColumnmaps(), TbrConstants.KEY.DOB, false);
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
        String ageAndGender = String.format("%s, %s", age, gender);
        fillValue((TextView) view.findViewById(R.id.age_gender), ageAndGender);
        return view.findViewById(R.id.patient_column);
    }

    private View populateResultsColumn(CommonPersonObjectClient pc, SmartRegisterClient client, View view) {
        View result = view.findViewById(R.id.result_lnk);
        result.setOnClickListener(onClickListener);
        result.setTag(client);

        TextView results = (TextView) view.findViewById(R.id.result_details);
        result.setTag(client);
        Map<String, String> testResults = detailsRepository.getAllDetailsForClient(getValue(pc.getColumnmaps(), TbrConstants.KEY.BASE_ENTITY_ID_COLUMN, false));

        ForegroundColorSpan redForegroundColorSpan = new ForegroundColorSpan(
                context.getResources().getColor(android.R.color.holo_red_dark));
        ForegroundColorSpan blackForegroundColorSpan = new ForegroundColorSpan(
                context.getResources().getColor(android.R.color.black));
        TbrSpannableStringBuilder stringBuilder = new TbrSpannableStringBuilder();
        if (testResults.containsKey(TbrConstants.RESULT.MTB_RESULT)) {
            stringBuilder.append("Xpe ");
            if (testResults.get(TbrConstants.RESULT.MTB_RESULT).equals(DETECTED))
                stringBuilder.append("+ve", redForegroundColorSpan);
            else
                stringBuilder.append("-ve", redForegroundColorSpan);
            stringBuilder.append("/");
            if (testResults.containsKey(TbrConstants.RESULT.RIF_RESULT) && testResults.get(TbrConstants.RESULT.RIF_RESULT).equals(DETECTED))
                stringBuilder.append("+ve", redForegroundColorSpan);
            else
                stringBuilder.append("-ve", redForegroundColorSpan);
        }
        if (testResults.containsKey(TbrConstants.RESULT.TEST_RESULT)) {
            stringBuilder.append(",\nSmr ");
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
                default:
                    stringBuilder.append(WordUtils.capitalize(testResults.get(TbrConstants.RESULT.TEST_RESULT).substring(0, 2)), redForegroundColorSpan);
                    break;
            }
        }

        if (testResults.containsKey(TbrConstants.RESULT.CULTURE_RESULT)) {
            stringBuilder.append(", Cul ");
            stringBuilder.append(WordUtils.capitalizeFully(testResults.get(TbrConstants.RESULT.CULTURE_RESULT).substring(0, 3)), blackForegroundColorSpan);
        }
        if (testResults.containsKey(TbrConstants.RESULT.XRAY_RESULT)) {
            stringBuilder.append(",\nCXR ");
            if (testResults.get(TbrConstants.RESULT.XRAY_RESULT).equals("indicative"))
                stringBuilder.append("Ind", blackForegroundColorSpan);
            else
                stringBuilder.append("Non", blackForegroundColorSpan);

        }
        if (stringBuilder.length() > 0) {
            results.setVisibility(View.VISIBLE);
            results.setText(stringBuilder);
        }
        return view.findViewById(R.id.results_column);
    }

    private View populateDiagnoseColumn(SmartRegisterClient client, View view) {
        View diagnose = view.findViewById(R.id.diagnose_lnk);
        diagnose.setOnClickListener(onClickListener);
        diagnose.setTag(client);
        return view.findViewById(R.id.diagnose_column);
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
        return inflater.inflate(R.layout.register_list_row, null);
    }
}
