package org.smartregister.tbr.activity;

import android.os.Bundle;
import android.view.View;

import org.smartregister.tbr.util.Utils;

/**
 * Created by ndegwamartin on 17/11/2017.
 */

public abstract class BasePatientDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    //remove patient
    public void removePatient(View view) {
        Utils.showToast(this, "Removing patient");
    }

}
