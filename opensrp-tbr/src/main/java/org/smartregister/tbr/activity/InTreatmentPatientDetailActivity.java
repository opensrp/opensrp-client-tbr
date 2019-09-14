package org.smartregister.tbr.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.event.LanguageConfigurationEvent;
import org.smartregister.tbr.fragment.InTreatmentPatientDetailsFragment;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ndegwamartin on 09/10/2017.
 */

public class InTreatmentPatientDetailActivity extends BasePatientDetailActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(TbrApplication.getInstance().getApplicationContext()));
        Utils.setLocale(new Locale(allSharedPreferences.getPreference("locale")));
        org.smartregister.tbr.util.Utils.postEvent(new LanguageConfigurationEvent(false));
    }

    @Override
    protected Fragment getDetailFragment() {

        InTreatmentPatientDetailsFragment fragment = new InTreatmentPatientDetailsFragment();
        Map<String, String> patientDetails = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.PATIENT_DETAIL_MAP);
        fragment.setPatientDetails(patientDetails);
        return fragment;
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient_detail_settings, menu);
                return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tbDiagnosisForm:
                patientDetails = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.PATIENT_DETAIL_MAP);
                formOverridesHelper.setPatientDetails(patientDetails);
                startFormActivity(Constants.FORM.DIAGNOSIS, patientDetails.get(Constants.KEY._ID), formOverridesHelper.getFieldOverrides().getJSONString());
                return true;
            case R.id.registerHealthIndicators:
                patientDetails = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.PATIENT_DETAIL_MAP);
                formOverridesHelper.setPatientDetails(patientDetails);
                startFormActivity(Constants.FORM.REGISTER_HEALTH_INDICATORS, patientDetails.get(Constants.KEY._ID), formOverridesHelper.getFieldOverrides().getJSONString());
                return true;
            case R.id.reports:
                Intent intent = new Intent(this,ReportActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
}
