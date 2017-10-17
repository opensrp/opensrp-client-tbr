package org.smartregister.tbr.activity.mocks;

import android.os.Bundle;

import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.HomeActivity;

/**
 * Created by ndegwamartin on 17/10/2017.
 */

public class HomeActivityTestVersion extends HomeActivity {
    @Override
    public void onCreate(Bundle bundle) {
        setTheme(R.style.AppTheme); //we need this here
        super.onCreate(bundle);
    }
}
