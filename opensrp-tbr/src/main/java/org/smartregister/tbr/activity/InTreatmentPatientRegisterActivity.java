package org.smartregister.tbr.activity;

import android.support.v4.app.Fragment;
import android.view.Menu;

import org.smartregister.tbr.R;
import org.smartregister.tbr.fragment.InTreatmentPatientRegisterFragment;

import java.util.Arrays;
import java.util.List;

import static util.TbrConstants.VIEW_CONFIGS.INTREATMENT_REGISTER;

/**
 * Created by samuelgithengi on 12/5/17.
 */

public class InTreatmentPatientRegisterActivity extends BaseRegisterActivity {
    @Override
    protected Fragment getRegisterFragment() {
        return new InTreatmentPatientRegisterFragment();
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(INTREATMENT_REGISTER);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.addNewPatient).setVisible(false);
        return true;
    }
}
