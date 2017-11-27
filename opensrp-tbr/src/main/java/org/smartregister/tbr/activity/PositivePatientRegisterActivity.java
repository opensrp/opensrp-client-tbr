package org.smartregister.tbr.activity;

import android.support.v4.app.Fragment;
import android.view.MenuItem;

import org.smartregister.tbr.R;
import org.smartregister.tbr.fragment.PositivePatientRegisterFragment;

import java.util.Arrays;
import java.util.List;

import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;
import static util.TbrConstants.ENKETO_FORMS.ADD_POSITIVE_PATIENT;
import static util.TbrConstants.ENKETO_FORMS.TREATMENT;
import static util.TbrConstants.VIEW_CONFIGS.POSITIVE_REGISTER;
import static util.TbrConstants.VIEW_CONFIGS.POSITIVE_REGISTER_HEADER;
import static util.TbrConstants.VIEW_CONFIGS.POSITIVE_REGISTER_ROW;

/**
 * Created by samuelgithengi on 11/27/17.
 */

public class PositivePatientRegisterActivity extends BaseRegisterActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addNewPatient:
                String entityId = generateRandomUUIDString();
                startFormActivity(ADD_POSITIVE_PATIENT, entityId, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Fragment getRegisterFragment() {
        return new PositivePatientRegisterFragment();
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(POSITIVE_REGISTER, POSITIVE_REGISTER_HEADER, POSITIVE_REGISTER_ROW);
    }

    @Override
    protected List<String> buildFormNameList() {
        formNames = super.buildFormNameList();
        formNames.add(0, ADD_POSITIVE_PATIENT);
        formNames.add(TREATMENT);
        return formNames;
    }
}
