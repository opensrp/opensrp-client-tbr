package org.smartregister.tbr.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.smartregister.tbr.R;
import org.smartregister.tbr.util.Utils;

/**
 * Created by ndegwamartin on 09/10/2017.
 */

public class HomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title2 = (TextView) toolbar.findViewById(R.id.custom_toolbar_logo_text);
        title2.setText("CG");
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.round_white_background);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //set custom title
        TextView title = (TextView) toolbar.findViewById(R.id.custom_toolbar_logo_text);
        title.setText("BA");

        ImageButton button = (ImageButton) findViewById(R.id.refreshSyncButton);

    }

    public void manualSync(View view) {
        Utils.showToast(this, "Manual Syncing ...");
    }
}
