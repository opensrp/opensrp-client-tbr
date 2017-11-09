package org.smartregister.tbr.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONObject;
import org.smartregister.enketo.adapter.pager.EnketoRegisterPagerAdapter;
import org.smartregister.enketo.listener.DisplayFormListener;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.tbr.R;
import org.smartregister.tbr.fragment.PresumptivePatientRegisterFragment;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import util.EnketoFormUtils;

import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;

/**
 * Created by samuelgithengi on 10/30/17.
 */

public class PresumptivePatientRegisterActivity extends BaseRegisterActivity implements DisplayFormListener {

    @Bind(R.id.view_pager)
    OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private int currentPage;

    private String[] formNames = new String[]{};
    private android.support.v4.app.Fragment mBaseFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_register);
        ButterKnife.bind(this);
        formNames = this.buildFormNameList();
        mBaseFragment = new PresumptivePatientRegisterFragment();

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new EnketoRegisterPagerAdapter(getSupportFragmentManager(), formNames, mBaseFragment);
        mPager.setOffscreenPageLimit(formNames.length);
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }
        });
        initializeProgressDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addNewPatient:
                String entityId = generateRandomUUIDString();
                startFormActivity("new_patient_registration", entityId, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void saveFormSubmission(String formSubmision, String id, String formName, JSONObject fieldOverrides) {
        Toast.makeText(this, formName + " submitted", Toast.LENGTH_SHORT).show();
        try {
            EnketoFormUtils enketoFormUtils = EnketoFormUtils.getInstance(this);
            enketoFormUtils.generateFormSubmisionFromXMLString(id, formSubmision, formName, fieldOverrides);
        } catch (Exception e) {
            e.printStackTrace();
        }
        switchToBaseFragment(formSubmision);

    }

    @Override
    public void savePartialFormData(String formData, String id, String formName, JSONObject fieldOverrides) {
        Toast.makeText(this, formName + " partially submitted", Toast.LENGTH_SHORT).show();
    }

    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            int formIndex = getIndexForFormName(formName, formNames) + 1; // add the offset
            if (entityId != null || metaData != null) {
                String data = null;

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
            e.printStackTrace();
        }

    }

    private String[] buildFormNameList() {
        List<String> formNames = new ArrayList<String>();
        formNames.add("new_patient_registration");
        return formNames.toArray(new String[formNames.size()]);
    }

    private void switchToBaseFragment(final String data) {
        final int prevPageIndex = currentPage;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPager.setCurrentItem(0, false);
                //hack reset the form
                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(prevPageIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.hideTranslucentProgressDialog();
                    displayFormFragment.setFormData(null);
                }

                displayFormFragment.setRecordId(null);
                refreshList(data);
            }
        });

    }

    @Override
    protected android.support.v4.app.Fragment findFragmentByPosition(int position) {
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + mPagerAdapter.getItemId(position));
    }

    private DisplayFormFragment getDisplayFormFragmentAtIndex(int index) {
        return (DisplayFormFragment) findFragmentByPosition(index);
    }

    private int getIndexForFormName(String formName, String[] formNames) {
        for (int i = 0; i < formNames.length; i++) {
            if (formName.equalsIgnoreCase(formNames[i])) {
                return i;
            }
        }

        return -1;
    }


}
