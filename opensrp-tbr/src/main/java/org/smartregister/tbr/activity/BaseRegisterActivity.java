package org.smartregister.tbr.activity;

import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.tbr.R;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

/**
 * Created by samuelgithengi on 10/30/17.
 */

public abstract class BaseRegisterActivity extends SecuredNativeSmartRegisterActivity {

    public static String TOOLBAR_TITLE = "org.smartregister.tbr.activity.toolbarTitle";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void setupViews() {
    }


    @Override
    protected void onResumption() {
    }

    @Override
    protected void onInitialization() {

    }

    @Override
    public void startRegistration() {

    }
}
