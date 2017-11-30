package org.smartregister.tbr.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avocarrot.json2view.DynamicView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.BasePatientDetailActivity;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.event.EnketoFormSaveCompleteEvent;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.tbr.util.Constants;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.TbrConstants;

import static org.smartregister.tbr.util.Constants.INTENT_KEY.REGISTER_TITLE;

/**
 * Created by ndegwamartin on 24/11/2017.
 */


public class PresumptivePatientDetailsFragment extends BaseRegisterFragment {
    private Map<String, String> patientDetails;
    private static final String TAG = PresumptivePatientDetailsFragment.class.getCanonicalName();
    private ResultMenuListener resultMenuListener = new ResultMenuListener();
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_presumptive_patient_detail, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(activity.getIntent().getStringExtra(REGISTER_TITLE));
        setupViews(rootView);
        return rootView;
    }


    @Override
    public void setupViews(View view) {
        processViewConfigurations();
        processViews(view);
    }

    public void setPatientDetails(Map<String, String> patientDetails) {
        this.patientDetails = patientDetails;
    }

    private void processViewConfigurations() {

        String jsonString = TbrApplication.getInstance().getConfigurableViewsRepository().getConfigurableViewJson(Constants.CONFIGURATION.PRESUMPTIVE_PATIENT_DETAILS);
        if (jsonString == null) return;
        ViewConfiguration detailsView = TbrApplication.getJsonSpecHelper().getConfigurableView(jsonString);
        List<org.smartregister.tbr.jsonspec.model.View> views = detailsView.getViews();
        if (!views.isEmpty()) {
            Collections.sort(views, new Comparator<org.smartregister.tbr.jsonspec.model.View>() {
                @Override
                public int compare(org.smartregister.tbr.jsonspec.model.View registerA, org.smartregister.tbr.jsonspec.model.View registerB) {
                    return registerA.getResidence().getPosition() - registerB.getResidence().getPosition();
                }
            });

            for (org.smartregister.tbr.jsonspec.model.View componentView : views) {
                if (componentView.getResidence().getParent() == null) {
                    componentView.getResidence().setParent(detailsView.getIdentifier());
                }

                LinearLayout viewParent = (LinearLayout) rootView.findViewById(R.id.patient_detail_container);
                String jsonComponentString = TbrApplication.getInstance().getConfigurableViewsRepository().getConfigurableViewJson(componentView.getIdentifier());
                ViewConfiguration componentViewConfiguration = TbrApplication.getJsonSpecHelper().getConfigurableView(jsonComponentString);
                if (componentViewConfiguration != null) {
                    JSONObject jsonViewObject = new JSONObject(componentViewConfiguration.getJsonView());
                    View sampleView = DynamicView.createView(getActivity().getApplicationContext(), jsonViewObject, viewParent);
                    sampleView.toString();
                }
            }
        }
        Log.d(TAG, String.valueOf(views.size()));

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();
        processViewConfigurations();
    }

    @Override
    protected String getMainCondition() {
        return null;
    }

    private void processViews(View view) {

        renderDemographicsView(view, patientDetails);
        renderPositiveResultsView(view, patientDetails);
        renderServiceHistoryView(view, patientDetails);

        //Remove patient button
        Button removePatientButton = (Button) view.findViewById(R.id.removePatientButton);
        removePatientButton.setTag(R.id.CLIENT_ID, patientDetails.get(Constants.KEY._ID));

        //Record Results
        TextView recordResults = (TextView) view.findViewById(R.id.recordResultsTextView);
        recordResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResultMenu(view);

            }
        });
    }

    public void showResultMenu(View view) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        popup.inflate(R.menu.menu_register_result);
        popup.setOnMenuItemClickListener(resultMenuListener);
        MenuItem item = popup.getMenu().getItem(0);
        String firstName = getActivity().getString(R.string.add_result_for) + Constants.CHAR.SPACE + patientDetails.get(Constants.KEY.FIRST_NAME);
        SpannableString s = new SpannableString(firstName);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, firstName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        item.setTitle(s);
        popup.show();
    }

    private FieldOverrides getFieldOverrides() {
        FieldOverrides fieldOverrides = null;
        Map fields = new HashMap();
        fields.put("participant_id", patientDetails.get(TbrConstants.KEY.TBREACH_ID));
        JSONObject fieldOverridesJson = new JSONObject(fields);
        fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshView(EnketoFormSaveCompleteEvent enketoFormSaveCompleteEvent) {
        if (enketoFormSaveCompleteEvent != null) {
            processViews(rootView);
        }

    }

    class ResultMenuListener implements PopupMenu.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            BasePatientDetailActivity registerActivity = (BasePatientDetailActivity) getActivity();
            switch (item.getItemId()) {
                case R.id.result_gene_xpert:
                    registerActivity.startFormActivity("result_gene_xpert", patientDetails.get(Constants.KEY._ID), getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_smear:
                    registerActivity.startFormActivity("result_smear", patientDetails.get("_id"), getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_chest_xray:
                    registerActivity.startFormActivity("result_chest_xray", patientDetails.get("_id"), getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_culture:
                    registerActivity.startFormActivity("result_culture", patientDetails.get("_id"), getFieldOverrides().getJSONString());
                    return true;
                default:
                    return false;
            }
        }
    }
}
