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
import org.smartregister.util.DateUtil;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.util.Map;

import util.TbrConstants;
import util.TbrSpannableStringBuilder;

import static org.smartregister.util.Utils.fillValue;
import static org.smartregister.util.Utils.getName;
import static org.smartregister.util.Utils.getValue;

/**
 * Created by samuelgithengi on 11/8/17.
 */

public class PatientRegisterProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private final LayoutInflater inflater;
    private Context context;
    private View.OnClickListener onClickListener;
    private DetailsRepository detailsRepository;

    private static final String DETECTED = "detected";

    public PatientRegisterProvider(Context context, View.OnClickListener onClickListener, DetailsRepository detailsRepository) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.onClickListener = onClickListener;
        this.detailsRepository = detailsRepository;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, View convertView) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        String firstName = getValue(pc.getColumnmaps(), TbrConstants.KEY.FIRST_NAME, true);
        String lastName = getValue(pc.getColumnmaps(), TbrConstants.KEY.LAST_NAME, true);
        String patientName = getName(firstName, lastName);

        fillValue((TextView) convertView.findViewById(R.id.patient_name), patientName);

        fillValue((TextView) convertView.findViewById(R.id.participant_id), getValue(pc.getColumnmaps(), TbrConstants.KEY.TBREACH_ID, false));


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
        fillValue((TextView) convertView.findViewById(R.id.age_gender), ageAndGender);

        View result = convertView.findViewById(R.id.result_lnk);
        result.setOnClickListener(onClickListener);
        result.setTag(client);


        TextView results = (TextView) convertView.findViewById(R.id.result_details);
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
        return inflater().inflate(R.layout.register_list_row, null);
    }
}
