package org.smartregister.nutrition.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.nutrition.R;
import org.smartregister.tbr.fragment.AdvSearchFormFragment;
import org.smartregister.tbr.fragment.AdvancedSearchResultsFragment;

import java.util.HashMap;

import butterknife.ButterKnife;

/**
 * Created by Imran-PC on 23-Apr-18.
 */

public class AdvancedSearchActivity extends BaseActivity implements View.OnClickListener{

    private AdvSearchFormFragment advSearchFragment;
    private HashMap<String,Object> model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_advanced_search);

        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Advanced Search");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        advSearchFragment = new AdvSearchFormFragment();
        this.loadFragment(advSearchFragment);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id) {
            case android.R.id.home:
                onBackPressed();
                return;
            case R.id.btn_adv_search:
                if(StringUtils.isEmpty(advSearchFragment.participantId.getText().toString()) && StringUtils.isEmpty(advSearchFragment.phoneNumber.getText().toString())) {
                    if (statusEmpty()) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Check at least one status to search", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (!oneOtherFieldNotEmpty()) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "First Name must be filled in", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (isGenderEmpty()) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Select Gender please", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (isAgeGroupEmpty()) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Select Age Group please", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                    else {
                        prepareData();
                        this.loadFragment(new AdvancedSearchResultsFragment());
                    }
                }
                else{
                    if (!StringUtils.isEmpty(advSearchFragment.phoneNumber.getText().toString()) && phoneLessThanTenDigits()) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Phone number should be greater than 10 digits", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                    else {
                        prepareData();
                        this.loadFragment(new AdvancedSearchResultsFragment());
                    }
                }
        }

    }

    private void prepareData(){
        model = new HashMap<>();
        if(advSearchFragment.chkPresumptive.isChecked())
            model.put("presumptive",true);
        if(advSearchFragment.chkPositive.isChecked())
            model.put("positive",true);
        if(advSearchFragment.chkTreatment.isChecked())
            model.put("inTreatment",true);
        if(!advSearchFragment.participantId.getText().toString().isEmpty())
            model.put("participantId",advSearchFragment.participantId.getText().toString().trim());
        if(!advSearchFragment.firstName.getText().toString().isEmpty())
            model.put("firstName", advSearchFragment.firstName.getText().toString().trim());
        if(!advSearchFragment.lastName.getText().toString().isEmpty())
            model.put("lastName", advSearchFragment.lastName.getText().toString().trim());
        if(!advSearchFragment.phoneNumber.getText().toString().isEmpty())
            model.put("phoneNumber", advSearchFragment.phoneNumber.getText().toString().trim());
        if(advSearchFragment.spGender.getSelectedItemPosition() > 0)
            model.put("gender", advSearchFragment.spGender.getSelectedItem().toString());
        if(advSearchFragment.spAgeGroup.getSelectedItemPosition() > 0)
            model.put("ageGroup", advSearchFragment.spAgeGroup.getSelectedItem().toString());
    }

    private boolean oneOtherFieldNotEmpty() {
        if(!advSearchFragment.firstName.getText().toString().isEmpty())
            return true;
        else return false;
    }

    private boolean isGenderEmpty(){
        if(advSearchFragment.spGender.getSelectedItem().toString().equalsIgnoreCase(getResources().getString(R.string.gender_Select_hint)))
            return true;
        else return false;
    }

    private boolean isAgeGroupEmpty(){
        if(advSearchFragment.spAgeGroup.getSelectedItem().toString().equalsIgnoreCase(getResources().getString(R.string.age_group_select_hint)))
            return true;
        else return false;
    }


    private boolean statusEmpty() {
        if(!advSearchFragment.chkPresumptive.isChecked() && !advSearchFragment.chkPositive.isChecked() && !advSearchFragment.chkTreatment.isChecked())
            return true;
        else
            return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean phoneLessThanTenDigits() {
        if(advSearchFragment.phoneNumber.getText().toString().length() < 10)
            return true;
        else return false;
    }

    private void loadFragment(Fragment fragment) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("model",model);
        fragment.setArguments(bundle);
        // create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.frameLayout, fragment).addToBackStack(null);
        fragmentTransaction.commit(); // save the changes
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        int n = getSupportFragmentManager().getBackStackEntryCount();
        if(n <= 1)
            this.finish();
        else getSupportFragmentManager().popBackStack();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
