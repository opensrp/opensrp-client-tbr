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
import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.BasePatientDetailActivity;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.event.EnketoFormSaveCompleteEvent;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

import java.util.Collections;
import java.util.Comparator;
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
    private Map<String, String> languageTranslations;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_presumptive_patient_detail, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(activity.getIntent().getStringExtra(REGISTER_TITLE));
        setupViews(rootView);
        return rootView;
    }


    @Override
    public void setupViews(View rootView) {

        //Load Language Token Map
        ViewConfiguration config = TbrApplication.getJsonSpecHelper().getLanguage(Utils.getLanguage());
        languageTranslations = config == null ? null : config.getLabels();

        processViewConfigurations(rootView);
        processViews(rootView);

        //Remove patient button
        Button removePatientButton = (Button) rootView.findViewById(R.id.remove_patient);
        removePatientButton.setTag(R.id.CLIENT_ID, patientDetails.get(Constants.KEY._ID));
    }

    public void setPatientDetails(Map<String, String> patientDetails) {
        this.patientDetails = patientDetails;
    }

    protected void processViewConfigurations(View rootView) {

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

            LinearLayout viewParent = (LinearLayout) rootView.findViewById(R.id.patient_detail_container);
            for (org.smartregister.tbr.jsonspec.model.View componentView : views) {

                try {
                    if (componentView.getResidence().getParent() == null) {
                        componentView.getResidence().setParent(detailsView.getIdentifier());
                    }

                    String jsonComponentString = TbrApplication.getInstance().getConfigurableViewsRepository().getConfigurableViewJson(componentView.getIdentifier());
                    ViewConfiguration componentViewConfiguration = TbrApplication.getJsonSpecHelper().getConfigurableView(jsonComponentString);
                    if (componentViewConfiguration != null) {
                        JSONObject jsonViewObject = new JSONObject(componentViewConfiguration.getJsonView());
                        View sampleView = DynamicView.createView(getActivity().getApplicationContext(), jsonViewObject, viewParent);

                        View view = viewParent.findViewById(sampleView.getId());
                        if (view != null) {
                            viewParent.removeView(view);
                        }
                        viewParent.addView(sampleView);
                        processViews(sampleView);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        if (detailsView != null) {
            processLanguageTokens(detailsView.getLabels(), languageTranslations, rootView);
        }

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
    }

    @Override
    protected void populateClientListHeaderView(View view) {

    }

    @Override
    protected String getMainCondition() {
        return null;
    }

    @Override
    protected String getViewConfigurationIdentifier() {
        return null;
    }

    private void processViews(View view) {

        if (view.getId() == R.id.clientDetailsCardView) {
            renderDemographicsView(view, patientDetails);
        } else if (view.getId() == R.id.clientServiceHistoryCardView) {
            renderServiceHistoryView(view, patientDetails);

        } else if (view.getId() == R.id.clientPositiveResultsCardView) {
            renderPositiveResultsView(view, patientDetails);
            //Record Results
            TextView recordResults = (TextView) view.findViewById(R.id.record_results);
            recordResults.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showResultMenu(view);

                }
            });

        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshView(EnketoFormSaveCompleteEvent enketoFormSaveCompleteEvent) {
        if (enketoFormSaveCompleteEvent != null) {
            processViews(getView());
        }

    }

    class ResultMenuListener implements PopupMenu.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            BasePatientDetailActivity registerActivity = (BasePatientDetailActivity) getActivity();
            switch (item.getItemId()) {
                case R.id.result_gene_xpert:
                    registerActivity.startFormActivity(TbrConstants.ENKETO_FORMS.GENE_XPERT, patientDetails.get(Constants.KEY._ID), getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_smear:
                    registerActivity.startFormActivity(TbrConstants.ENKETO_FORMS.SMEAR, patientDetails.get(Constants.KEY._ID), getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_chest_xray:
                    registerActivity.startFormActivity(TbrConstants.ENKETO_FORMS.CHEST_XRAY, patientDetails.get(Constants.KEY._ID), getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_culture:
                    registerActivity.startFormActivity(TbrConstants.ENKETO_FORMS.CULTURE, patientDetails.get(Constants.KEY._ID), getFieldOverrides().getJSONString());
                    return true;
                default:
                    return false;
            }
        }
    }
}
