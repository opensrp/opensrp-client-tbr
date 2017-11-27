package org.smartregister.tbr.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.PresumptivePatientDetailActivity;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.repository.ResultsRepository;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

import java.util.HashMap;
import java.util.Map;

import static org.smartregister.tbr.R.id.FORM_SUBMISSION_ID;

/**
 * Created by ndegwamartin on 20/11/2017.
 */

public class ServiceHistoryAdapter extends CursorAdapter implements View.OnClickListener {

    private Context mContext;
    private LayoutInflater inflater;

    public ServiceHistoryAdapter(Context context, net.sqlcipher.Cursor cursor, int flags) {
        super(context, cursor, flags);
        this.mContext = context;

        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public void onClick(View view) {
        View formView = view.findViewById(R.id.formNameTextView);
        Utils.showToastShort(mContext, "Opening the " + formView.getTag(R.id.FORM_NAME) + " form");

        int formIdentifier = getFormIdentifierFromName(formView.getTag(R.id.FORM_NAME).toString());

        String formSubmissionId = formView.getTag(FORM_SUBMISSION_ID).toString();

        switch (formIdentifier) {
            case R.id.result_gene_xpert:
                ((PresumptivePatientDetailActivity) mContext).startFormActivity(Constants.FORM.RESULT_GENE_EXPERT, formView.getTag(R.id.BASE_ENTITY_ID).toString(), getSmearResultsFormFieldOverrides(formSubmissionId).getJSONString());
                break;
            case R.id.result_smear:
                ((PresumptivePatientDetailActivity) mContext).startFormActivity(Constants.FORM.RESULT_SMEAR, formView.getTag(R.id.BASE_ENTITY_ID).toString(), getSmearResultsFormFieldOverrides(formSubmissionId).getJSONString());
                break;
            case R.id.result_chest_xray:
                ((PresumptivePatientDetailActivity) mContext).startFormActivity(Constants.FORM.RESULT_CHEST_XRAY, formView.getTag(R.id.BASE_ENTITY_ID).toString(), getSmearResultsFormFieldOverrides(formSubmissionId).getJSONString());

                break;
            case R.id.result_culture:
                ((PresumptivePatientDetailActivity) mContext).startFormActivity(Constants.FORM.RESULT_CULTURE, formView.getTag(R.id.BASE_ENTITY_ID).toString(), getSmearResultsFormFieldOverrides(formSubmissionId).getJSONString());
                break;
            case R.id.addNewPatient:
                ((PresumptivePatientDetailActivity) mContext).startFormActivity(Constants.FORM.NEW_PATIENT_REGISTRATION, formView.getTag(R.id.BASE_ENTITY_ID).toString(), getSmearResultsFormFieldOverrides(formSubmissionId).getJSONString());
                break;
            case R.id.tbDiagnosisForm:
                ((PresumptivePatientDetailActivity) mContext).startFormActivity(Constants.FORM.DIAGNOSIS, formView.getTag(R.id.BASE_ENTITY_ID).toString(), getSmearResultsFormFieldOverrides(formSubmissionId).getJSONString());
                break;
            default:
                break;
        }

    }

    private int getFormIdentifierFromName(String formName) {
        if (StringUtils.containsIgnoreCase(formName, "gene")) {
            return R.id.result_gene_xpert;
        } else if (StringUtils.containsIgnoreCase(formName, "smear")) {
            return R.id.result_smear;
        } else if (StringUtils.containsIgnoreCase(formName, "x-ray")) {
            return R.id.result_chest_xray;
        } else if (StringUtils.containsIgnoreCase(formName, "culture")) {
            return R.id.result_culture;
        } else if (StringUtils.containsIgnoreCase(formName, "registration")) {
            return R.id.addNewPatient;
        } else if (StringUtils.containsIgnoreCase(formName, "diagnosis")) {
            return R.id.tbDiagnosisForm;
        } else {
            return 0;
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.row_service_history, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView date = (TextView) view.findViewById(R.id.formfillDateTextView);
        date.setText(Utils.formatDate(org.smartregister.util.Utils.toDate(cursor.getString(cursor.getColumnIndex(Constants.KEY.DATE.toString())), true), "dd MMM yyyy"));
        TextView formName = (TextView) view.findViewById(R.id.formNameTextView);
        formName.setText(cursor.getString(cursor.getColumnIndex(ResultsRepository.TYPE)));
        formName.setOnClickListener(this);
        formName.setTag(cursor.getString(cursor.getColumnIndex(ResultsRepository.ID)));
        formName.setTag(R.id.FORM_NAME, cursor.getString(cursor.getColumnIndex(ResultsRepository.TYPE)));
        formName.setTag(R.id.FORM_SUBMITTED_DATE, cursor.getString(cursor.getColumnIndex(ResultsRepository.DATE)));
        formName.setTag(R.id.FORM_SUBMISSION_ID, cursor.getString(cursor.getColumnIndex(ResultsRepository.FORMSUBMISSION_ID)));
        formName.setTag(R.id.BASE_ENTITY_ID, cursor.getString(cursor.getColumnIndex(ResultsRepository.BASE_ENTITY_ID)));

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_from_top);
        view.startAnimation(animation);
    }

    private FieldOverrides getFieldOverrides(String tbreachId) {
        FieldOverrides fieldOverrides = null;
        Map fields = new HashMap();
        fields.put(Constants.KEY.PARTICIPANT_ID, tbreachId);
        JSONObject fieldOverridesJson = new JSONObject(fields);
        fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }

    private FieldOverrides getSmearResultsFormFieldOverrides(String formId) {
        FieldOverrides fieldOverrides = null;
        Map db = TbrApplication.getInstance().getResultDetailsRepository().getFormResultDetails(formId);

        Map fields = new HashMap();
        fields.put("afb_culture.sample_id", db.get("sample_id"));
        JSONObject fieldOverridesJson = new JSONObject(fields);
        fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }
}