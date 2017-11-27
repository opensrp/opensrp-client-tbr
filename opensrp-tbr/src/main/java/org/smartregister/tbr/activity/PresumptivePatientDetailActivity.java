package org.smartregister.tbr.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.smartregister.tbr.R;
import org.smartregister.tbr.fragment.PresumptivePatientDetailsFragment;
import org.smartregister.tbr.util.Constants;

import java.util.HashMap;
import java.util.Map;

import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;

/**
 * Created by ndegwamartin on 09/10/2017.
 */

public class PresumptivePatientDetailActivity extends BasePatientDetailActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PresumptivePatientDetailsFragment mBaseFragment = new PresumptivePatientDetailsFragment();
        Map<String, String> patientDetails = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.PATIENT_DETAIL_MAP);
        mBaseFragment.setPatientDetails(patientDetails);
        initViewByFragmentType(mBaseFragment);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient_detail_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tbDiagnosisForm:
                String entityId = generateRandomUUIDString();
                startFormActivity(Constants.FORM.DIAGNOSIS, entityId, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
