package org.smartregister.tbr.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.smartregister.tbr.R;
import org.smartregister.tbr.fragment.InTreatmentPatientDetailsFragment;
import org.smartregister.tbr.util.Constants;

import java.util.HashMap;
import java.util.Map;

import util.TbrConstants;

import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;

/**
 * Created by ndegwamartin on 09/10/2017.
 */

public class InTreatmentPatientDetailActivity extends BasePatientDetailActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        renderFragmentView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient_detail_positive_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.treatmentInitiationForm:
                String entityId = generateRandomUUIDString();
                startFormActivity(TbrConstants.ENKETO_FORMS.TREATMENT_INITIATION, entityId, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void renderFragmentView() {
        InTreatmentPatientDetailsFragment mBaseFragment = new InTreatmentPatientDetailsFragment();
        Map<String, String> patientDetails = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.PATIENT_DETAIL_MAP);
        mBaseFragment.setPatientDetails(patientDetails);
        initViewByFragmentType(mBaseFragment);
    }

}
