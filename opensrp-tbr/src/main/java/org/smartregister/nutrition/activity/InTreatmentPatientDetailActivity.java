package org.smartregister.nutrition.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;

import org.smartregister.nutrition.fragment.InTreatmentPatientDetailsFragment;
import org.smartregister.nutrition.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ndegwamartin on 09/10/2017.
 */

public class InTreatmentPatientDetailActivity extends BasePatientDetailActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment getDetailFragment() {

        InTreatmentPatientDetailsFragment fragment = new InTreatmentPatientDetailsFragment();
        Map<String, String> patientDetails = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.PATIENT_DETAIL_MAP);
        fragment.setPatientDetails(patientDetails);
        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }



}
