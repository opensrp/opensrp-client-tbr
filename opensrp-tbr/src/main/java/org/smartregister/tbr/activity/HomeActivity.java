package org.smartregister.tbr.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.smartregister.tbr.R;

/**
 * Created by ndegwamartin on 09/10/2017.
 */

public class HomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("TB Reach");
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.round_white_background);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }
}
