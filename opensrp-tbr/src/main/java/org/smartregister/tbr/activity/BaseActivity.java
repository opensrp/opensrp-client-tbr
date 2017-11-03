package org.smartregister.tbr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.smartregister.Context;
import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;

/**
 * Created by ndegwamartin on 09/10/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_toolbar_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_language) {
            Utils.showToast(this, "Changing Languages");
            return true;
        } else if (id == R.id.action_logout) {
            logOutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logOutUser() {

        DrishtiApplication application = (DrishtiApplication) getApplication();
        application.logoutCurrentUser();
        finish();
    }

    public Context getOpenSRPContext() {
        return TbrApplication.getInstance().getContext();
    }

}
