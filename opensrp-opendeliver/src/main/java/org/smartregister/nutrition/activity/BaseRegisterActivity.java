package org.smartregister.nutrition.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.domain.FetchStatus;
import org.smartregister.enketo.adapter.pager.EnketoRegisterPagerAdapter;
import org.smartregister.enketo.listener.DisplayFormListener;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.nutrition.R;
import org.smartregister.nutrition.application.OpenDeliverApplication;
import org.smartregister.nutrition.event.EnketoFormSaveCompleteEvent;
import org.smartregister.nutrition.event.ShowProgressDialogEvent;
import org.smartregister.nutrition.event.SyncEvent;
import org.smartregister.nutrition.fragment.BaseRegisterFragment;
import org.smartregister.nutrition.util.Constants;
import org.smartregister.nutrition.util.FilterEnum;
import org.smartregister.nutrition.util.OtherFiltersEnum;
import org.smartregister.nutrition.util.ResultsFilterEnum;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import util.EnketoFormUtils;
import util.TbrConstants;

import static util.TbrConstants.ENKETO_FORMS.CHEST_XRAY;
import static util.TbrConstants.ENKETO_FORMS.CULTURE;
import static util.TbrConstants.ENKETO_FORMS.DIAGNOSIS;
import static util.TbrConstants.ENKETO_FORMS.NUTRITION_ENROLLMENT;
import static util.TbrConstants.ENKETO_FORMS.GENE_XPERT;
import static util.TbrConstants.ENKETO_FORMS.NUTRITION_FOLLOWUP;
import static util.TbrConstants.ENKETO_FORMS.NUTRITION_CASECLOSING;
import static util.TbrConstants.ENKETO_FORMS.SMEAR;


/**
 * Created by samuelgithengi on 10/30/17.
 */

public abstract class BaseRegisterActivity extends SecuredNativeSmartRegisterActivity implements DisplayFormListener, CompoundButton.OnCheckedChangeListener,  AdapterView.OnItemSelectedListener  {

    public static final String TAG = "BaseRegisterActivity";

    public static String TOOLBAR_TITLE = "org.smartregister.nutrition.activity.toolbarTitle";

    private ProgressDialog progressDialog;

    @Bind(R.id.view_pager)
    protected OpenSRPViewPager mPager;
    protected FragmentPagerAdapter mPagerAdapter;
    protected int currentPage;

    protected List<String> formNames;
    private List<FilterEnum> filter_result = new ArrayList<>();
    private List<FilterEnum> filter_other_result = new ArrayList<>();
    private String sortOption;
    private List<FilterEnum> previousFilterList = new ArrayList<>();
    private List<FilterEnum> previousOtherFilterList = new ArrayList<>();

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
        initializeEnketoFormFragment(formNames.get(0), null, null, false);
        //mPager.setCurrentItem(0, false);
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
        ViewConfiguration viewConfiguration = OpenDeliverApplication.getInstance()
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
            case R.id.sortList:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.layout_dialog_sort_register);
                dialog.setTitle("Sort");
                setRadioChecked(dialog);
                Button btnOk = (Button) dialog.findViewById(R.id.dialog_sort_btnOK);
                Button btnCancel = (Button) dialog.findViewById(R.id.dialog_sort_btnCancel);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RadioGroup radioSortGroup = (RadioGroup) dialog.findViewById(R.id.radioSort);
                        int id = radioSortGroup.getCheckedRadioButtonId();
                        RadioButton radioButton = (RadioButton) dialog.findViewById(id);
                        sortOption = radioButton.getText().toString();

                        BaseRegisterFragment frag = (BaseRegisterFragment)findFragmentByPosition(0);
                        frag.filterAndSortRegisterContent(getFilterResult(),getFilterOtherResult(),sortOption);
                        dialog.dismiss();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
               return true;
            case R.id.advancedSearch:
                Intent i = new Intent(this, AdvancedSearchActivity.class);
                startActivity(i);
                return true;
            default:
                Toast.makeText(this,"Hey",Toast.LENGTH_LONG).show();
                return false;
        }
    }

    private void setRadioChecked(Dialog dialog){
        if( sortOption==null || (sortOption != null && sortOption.equalsIgnoreCase("Name (A-Z)")) ) {
            RadioButton rb = (RadioButton) dialog.findViewById(R.id.radioName);
            rb.setChecked(true);
            sortOption = getResources().getString(R.string.sort_by_name);
        }
        else if(sortOption!=null && sortOption.equalsIgnoreCase("Last updated")) {
            RadioButton rb = (RadioButton) dialog.findViewById(R.id.radioLastUpdate);
            rb.setChecked(true);
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
        OpenDeliverApplication.getInstance().getConfigurableViewsHelper().registerViewConfigurations(getViewIdentifiers());
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

    protected Fragment findFragmentByPosition(int position) {
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
        hideProgressDialog();
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
        initializeEnketoFormFragment(formName, entityId, metaData, true);
    }


    public void initializeEnketoFormFragment(String formName, String entityId, String metaData, boolean displayForm) {
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

            if (displayForm)
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
        formNames.add(NUTRITION_FOLLOWUP);
        formNames.add(NUTRITION_ENROLLMENT);
        formNames.add(NUTRITION_CASECLOSING);
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        OpenDeliverApplication.getInstance().getConfigurableViewsHelper().unregisterViewConfiguration(getViewIdentifiers());
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

    protected void setCommonHandlers(Dialog dialog, Context context){
        CheckBox checkBoxSmear = (CheckBox) dialog.findViewById(R.id.chk_smear_pos);
        CheckBox checkBoxXpert = (CheckBox) dialog.findViewById(R.id.chk_xpert_pos);
        CheckBox checkBoxRif = (CheckBox) dialog.findViewById(R.id.chk_rif_pos);
        CheckBox checkBoxCulture = (CheckBox) dialog.findViewById(R.id.chk_cul_pos);
        CheckBox checkBoxXray = (CheckBox) dialog.findViewById(R.id.chk_xray_pos);

        checkBoxSmear.setOnCheckedChangeListener(this);
        checkBoxXpert.setOnCheckedChangeListener(this);
        checkBoxRif.setOnCheckedChangeListener(this);
        checkBoxCulture.setOnCheckedChangeListener(this);
        checkBoxXray.setOnCheckedChangeListener(this);


        checkBoxSmear.setTag(ResultsFilterEnum.SMEAR_POSITIVE);
        checkBoxXpert.setTag(ResultsFilterEnum.XPERT_POSITIVE);
        checkBoxRif.setTag(ResultsFilterEnum.RIF_POSITIVE);
        checkBoxCulture.setTag(ResultsFilterEnum.CULTURE_POSITIVE);
        checkBoxXray.setTag(ResultsFilterEnum.XRAY_INDICATIVE);

        Iterator<FilterEnum> it = filter_result.iterator();
        while(it.hasNext()){
            FilterEnum e = it.next();
            if(e == ResultsFilterEnum.SMEAR_POSITIVE) {
                checkBoxSmear.setOnCheckedChangeListener(null);
                checkBoxSmear.setChecked(true);
                checkBoxSmear.setOnCheckedChangeListener(this);
            }
            else if(e == ResultsFilterEnum.XPERT_POSITIVE) {
                checkBoxXpert.setOnCheckedChangeListener(null);
                checkBoxXpert.setChecked(true);
                checkBoxXpert.setOnCheckedChangeListener(this);
            }
            else if(e == ResultsFilterEnum.RIF_POSITIVE) {
                checkBoxRif.setOnCheckedChangeListener(null);
                checkBoxRif.setChecked(true);
                checkBoxRif.setOnCheckedChangeListener(this);
            }
            else if(e == ResultsFilterEnum.CULTURE_POSITIVE) {
                checkBoxCulture.setOnCheckedChangeListener(null);
                checkBoxCulture.setChecked(true);
                checkBoxCulture.setOnCheckedChangeListener(this);
            }
            else if(e == ResultsFilterEnum.XRAY_INDICATIVE) {
                checkBoxXray.setOnCheckedChangeListener(null);
                checkBoxXray.setChecked(true);
                checkBoxXray.setOnCheckedChangeListener(this);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b){
            if(((FilterEnum)compoundButton.getTag()) instanceof ResultsFilterEnum)
                filter_result.add( ((FilterEnum)compoundButton.getTag()));
            else if(((FilterEnum)compoundButton.getTag()) instanceof OtherFiltersEnum)
                filter_other_result.add( ((FilterEnum)compoundButton.getTag()));
        }
        else{
            if(((FilterEnum)compoundButton.getTag()) instanceof ResultsFilterEnum)
                filter_result.remove( ((FilterEnum)compoundButton.getTag()));
            else if(((FilterEnum)compoundButton.getTag()) instanceof OtherFiltersEnum)
                filter_other_result.remove( ((FilterEnum)compoundButton.getTag()));
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
         sortOption = (String)parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    public List<FilterEnum> getFilterResult(){
        return filter_result;
    }

    public void setFilterResult(List<FilterEnum> list){
        this.filter_result = list;
    }

    public List<FilterEnum> getFilterOtherResult(){
        return filter_other_result;
    }

    public String getSortOption(){
        return this.sortOption;
    }

    public void setFilterOtherResult(List<FilterEnum> list){
        this.filter_other_result = list;
    }

    protected Dialog getDialog(int dialogLayout){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(dialogLayout);
        dialog.setTitle("Filter");
        Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogBtnOk);
        Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialogBtnCancel);
        // if button is clicked, close the custom dialog
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousFilterList = new ArrayList<FilterEnum>(filter_result);
                previousOtherFilterList = new ArrayList<FilterEnum>(filter_other_result);
                BaseRegisterFragment frag = (BaseRegisterFragment)findFragmentByPosition(0);

                frag.filterAndSortRegisterContent(getFilterResult(),getFilterOtherResult(),getSortOption()==null ? getDefaultSort() : getSortOption());
                dialog.dismiss();
            }
        });

        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter_result = new ArrayList<FilterEnum>(previousFilterList);
                filter_other_result = new ArrayList<FilterEnum>(previousOtherFilterList);
                dialog.dismiss();
            }
        });
        return dialog;
    }

    private String getDefaultSort(){
        return "Name (A-Z)";
    }
}
