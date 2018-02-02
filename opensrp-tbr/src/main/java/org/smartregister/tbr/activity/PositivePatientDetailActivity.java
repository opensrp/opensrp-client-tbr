package org.smartregister.tbr.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import org.smartregister.tbr.R;
import org.smartregister.tbr.fragment.PositivePatientDetailsFragment;
import org.smartregister.tbr.util.Constants;

import java.util.HashMap;
import java.util.Map;

import util.TbrConstants;

/**
 * Created by ndegwamartin on 09/10/2017.
 */

public class PositivePatientDetailActivity extends BasePatientDetailActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient_detail_positive_settings, menu);
        return true;
    }

    @Override
    protected Fragment getDetailFragment() {

        PositivePatientDetailsFragment fragment = new PositivePatientDetailsFragment();
        Map<String, String> patientDetails = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.PATIENT_DETAIL_MAP);
        fragment.setPatientDetails(patientDetails);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.treatmentInitiationForm:
                Map<String, String> patientDetails = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.PATIENT_DETAIL_MAP);
                startFormActivity(TbrConstants.ENKETO_FORMS.TREATMENT_INITIATION, patientDetails.get(Constants.KEY._ID), formOverridesHelper.getTreatmentFieldOverrides().getJSONString());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
