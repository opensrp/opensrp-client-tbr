package org.smartregister.tbr.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import org.smartregister.domain.FetchStatus;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.tbr.R;
import org.smartregister.tbr.fragment.BaseRegisterFragment;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

/**
 * Created by samuelgithengi on 10/30/17.
 */

public abstract class BaseRegisterActivity extends SecuredNativeSmartRegisterActivity {

    private ProgressDialog progressDialog;

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

    protected abstract android.support.v4.app.Fragment findFragmentByPosition(int position);

    public void refreshList(final FetchStatus fetchStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            BaseRegisterFragment registerFragment = (BaseRegisterFragment) findFragmentByPosition(0);
            if (registerFragment != null && fetchStatus.equals(FetchStatus.fetched)) {
                registerFragment.refreshListView();
            }
        } else {
            Handler handler = new android.os.Handler(Looper.getMainLooper());
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

    protected void refreshList(String data) {
        BaseRegisterFragment registerFragment = (BaseRegisterFragment) findFragmentByPosition(0);
        if (registerFragment != null && data != null) {
            registerFragment.refreshListView();
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
