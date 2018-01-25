package org.smartregister.tbr.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.smartregister.domain.FetchStatus;
import org.smartregister.enketo.adapter.pager.EnketoRegisterPagerAdapter;
import org.smartregister.enketo.listener.DisplayFormListener;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.event.EnketoFormSaveCompleteEvent;
import org.smartregister.tbr.event.ShowProgressDialogEvent;
import org.smartregister.tbr.event.SyncEvent;
import org.smartregister.tbr.fragment.BaseRegisterFragment;
import org.smartregister.tbr.jsonspec.model.RegisterConfiguration;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import util.EnketoFormUtils;

import static util.TbrConstants.ENKETO_FORMS.CHEST_XRAY;
import static util.TbrConstants.ENKETO_FORMS.CULTURE;
import static util.TbrConstants.ENKETO_FORMS.DIAGNOSIS;
import static util.TbrConstants.ENKETO_FORMS.GENE_XPERT;
import static util.TbrConstants.ENKETO_FORMS.SMEAR;


/**
 * Created by samuelgithengi on 10/30/17.
 */

public abstract class BaseRegisterActivity extends SecuredNativeSmartRegisterActivity implements DisplayFormListener {

    public static final String TAG = "BaseRegisterActivity";

    public static String TOOLBAR_TITLE = "org.smartregister.tbr.activity.toolbarTitle";

    private ProgressDialog progressDialog;

    @Bind(R.id.view_pager)
    protected OpenSRPViewPager mPager;
    protected FragmentPagerAdapter mPagerAdapter;
    protected int currentPage;

    protected List<String> formNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_register);
        ButterKnife.bind(this);
        formNames = this.buildFormNameList();
        Fragment mBaseFragment = getRegisterFragment();

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new EnketoRegisterPagerAdapter(getSupportFragmentManager(), formNames.toArray(new String[formNames.size()]), mBaseFragment);
        mPager.setOffscreenPageLimit(formNames.size());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }
        });
    }

    protected abstract Fragment getRegisterFragment();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        processMenuConfigurations(menu);
        return true;
    }

    private void processMenuConfigurations(Menu menu) {
        if (getViewIdentifiers().isEmpty())
            return;
        ViewConfiguration viewConfiguration = TbrApplication.getInstance()
                .getConfigurableViewsHelper().getViewConfiguration(getViewIdentifiers().get(0));
        if (viewConfiguration == null)
            return;
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
        TbrApplication.getInstance().getConfigurableViewsHelper().registerViewConfigurations(getViewIdentifiers());
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onInitialization() {//Implement Abstract Method
    }

    @Override
    public void startRegistration() {//Implement Abstract Method
    }

    private Fragment findFragmentByPosition(int position) {
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + mPagerAdapter.getItemId(position));
    }

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showProgressDialog(ShowProgressDialogEvent showProgressDialogEvent) {
        if (showProgressDialogEvent != null)
            showProgressDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveFormSubmissionComplete(EnketoFormSaveCompleteEvent enketoFormSaveCompleteEvent) {
        if (enketoFormSaveCompleteEvent != null) {
            refreshList(FetchStatus.fetched);
            hideProgressDialog();
            switchToBaseFragment();
        }
    }

    @Override
    public void onFormClosed(String recordId, String formName) {
        Toast.makeText(this, formName + " closed", Toast.LENGTH_SHORT).show();
        switchToBaseFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshList(SyncEvent syncEvent) {
        if (syncEvent != null && syncEvent.getFetchStatus().equals(FetchStatus.fetched))
            refreshList(FetchStatus.fetched);
    }

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.saving_dialog_title));
        progressDialog.setMessage(getString(R.string.please_wait_message));
        if (!isFinishing())
            progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            int formIndex = formNames.indexOf(formName) + 1; // add the offset
            if (entityId != null || metaData != null) {
                String data = EnketoFormUtils.getInstance(getApplicationContext()).generateXMLInputForFormWithEntityId(entityId, formName, metaData);
                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.setFormData(data);
                    displayFormFragment.setRecordId(entityId);
                    displayFormFragment.setFieldOverides(metaData);
                    displayFormFragment.setListener(this);
                    displayFormFragment.setResize(false);
                }
            }

            mPager.setCurrentItem(formIndex, false); //Don't animate the view on orientation change the view disapears

        } catch (Exception e) {
            Log.e(TAG, "startFormActivity: ", e);
        }

    }

    private DisplayFormFragment getDisplayFormFragmentAtIndex(int index) {
        return (DisplayFormFragment) findFragmentByPosition(index);
    }

    protected List<String> buildFormNameList() {
        List<String> formNames = new ArrayList<String>();
        formNames.add(GENE_XPERT);
        formNames.add(SMEAR);
        formNames.add(CHEST_XRAY);
        formNames.add(CULTURE);
        formNames.add(DIAGNOSIS);
        return formNames;
    }

    public void switchToBaseFragment() {
        final int prevPageIndex = currentPage;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPager.setCurrentItem(0, false);
                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(prevPageIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.hideTranslucentProgressDialog();
                    displayFormFragment.setFormData(null);
                    displayFormFragment.setRecordId(null);
                }
            }
        });

    }

    @Override
    public void saveFormSubmission(String formSubmision, String id, String formName, JSONObject fieldOverrides) {
        try {
            EnketoFormUtils enketoFormUtils = EnketoFormUtils.getInstance(getApplicationContext());
            enketoFormUtils.generateFormSubmisionFromXMLString(id, formSubmision, formName, fieldOverrides);
        } catch (Exception e) {
            Log.i(TAG, "saveFormSubmission: ", e);
            switchToBaseFragment();
        }
        //TODO Remove once the dialog in enketo library is dismissed
        //
    }

    @Override
    protected void onStop() {
        super.onStop();
        TbrApplication.getInstance().getConfigurableViewsHelper().unregisterViewConfiguration(getViewIdentifiers());
    }

    public abstract List<String> getViewIdentifiers();

    @Override
    public void onBackPressed() {
        if (currentPage != 0) {
            new AlertDialog.Builder(this, R.style.TbrAlertDialog)
                    .setMessage(R.string.form_back_confirm_dialog_message)
                    .setTitle(R.string.form_back_confirm_dialog_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.no_button_label,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //Do nothing, remain on Enketo Form Fragment
                                }
                            })
                    .setNegativeButton(R.string.yes_button_label,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    switchToBaseFragment();
                                }
                            })
                    .show();
        } else {
            super.onBackPressed(); // allow back key only if we are
        }
    }

}
