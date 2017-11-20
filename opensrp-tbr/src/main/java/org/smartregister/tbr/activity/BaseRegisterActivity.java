package org.smartregister.tbr.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import org.smartregister.domain.FetchStatus;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.fragment.BaseRegisterFragment;
import org.smartregister.tbr.jsonspec.model.RegisterConfiguration;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

/**
 * Created by samuelgithengi on 10/30/17.
 */

public abstract class BaseRegisterActivity extends SecuredNativeSmartRegisterActivity {

    public static final String TAG = "BaseRegisterActivity";

    private ProgressDialog progressDialog;

    public static String TOOLBAR_TITLE = "org.smartregister.tbr.activity.toolbarTitle";

    public ViewConfiguration viewConfiguration;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        processMenuConfigurations(menu);
        return true;
    }

    private void processMenuConfigurations(Menu menu) {
        RegisterConfiguration metadata = (RegisterConfiguration) viewConfiguration.getMetadata();
        menu.findItem(R.id.advancedSearch).setVisible(metadata.isEnableAdvancedSearch());
        menu.findItem(R.id.sortList).setVisible(metadata.isEnableSortList());
        menu.findItem(R.id.filterList).setVisible(metadata.isEnableFilterList());
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
    protected void setupViews() {//Implement Abstract Method
    }

    @Override
    protected void onResumption() {
        String configFile = "presumptive_register";
        String jsonString = TbrApplication.getInstance().getConfigurableViewsRepository().getConfigurableViewJson(configFile);
        if (jsonString == null) return;
        viewConfiguration = TbrApplication.getJsonSpecHelper().getConfigurableView(jsonString);
    }

    @Override
    protected void onInitialization() {//Implement Abstract Method

    }

    @Override
    public void startRegistration() {//Implement Abstract Method

    }

    protected abstract Fragment findFragmentByPosition(int position);

    public void refreshList(final FetchStatus fetchStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            BaseRegisterFragment registerFragment = (BaseRegisterFragment) findFragmentByPosition(0);
            if (registerFragment != null && fetchStatus.equals(FetchStatus.fetched)) {
                registerFragment.refreshListView();
            }
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    BaseRegisterFragment registerFragment = (BaseRegisterFragment) findFragmentByPosition(0);
                    if (registerFragment != null && fetchStatus.equals(FetchStatus.fetched)) {
                        registerFragment.refreshListView();
                    }
                }
            });
        }

    }

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.saving_dialog_title));
        progressDialog.setMessage(getString(R.string.please_wait_message));
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
