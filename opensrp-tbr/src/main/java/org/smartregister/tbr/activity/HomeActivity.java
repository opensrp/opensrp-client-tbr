package org.smartregister.tbr.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.smartregister.tbr.R;
import org.smartregister.tbr.fragment.RegisterFragment;
import org.smartregister.tbr.util.Utils;

import java.util.Calendar;

/**
 * Created by ndegwamartin on 09/10/2017.
 */

public class HomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.round_white_background);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //set custom title
        TextView title = (TextView) toolbar.findViewById(R.id.custom_toolbar_logo_text);
        title.setText("BA");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.registers_container, new RegisterFragment())
                    .commit();
        }


    }

    //
    public void manualSync(View view) {
        Utils.showToast(this, "Manual Syncing ...");

        TextView textView = (TextView) view.getRootView().findViewById(R.id.registerLastSyncTime);
        textView.setText("Last sync: "+Utils.formatDate(Calendar.getInstance().getTime(), "MMM d H:m"));
    }
}
