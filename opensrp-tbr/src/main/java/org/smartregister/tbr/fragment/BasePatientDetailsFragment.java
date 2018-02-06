package org.smartregister.tbr.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.domain.FetchStatus;
import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.BasePatientDetailActivity;
import org.smartregister.tbr.activity.HomeActivity;
import org.smartregister.tbr.activity.InTreatmentPatientRegisterActivity;
import org.smartregister.tbr.activity.PositivePatientRegisterActivity;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.event.EnketoFormSaveCompleteEvent;
import org.smartregister.tbr.event.SyncEvent;
import org.smartregister.tbr.helper.FormOverridesHelper;
import org.smartregister.tbr.helper.view.RenderBMIHeightChartCardHelper;
import org.smartregister.tbr.helper.view.RenderContactScreeningCardHelper;
import org.smartregister.tbr.helper.view.RenderPatientDemographicCardHelper;
import org.smartregister.tbr.helper.view.RenderPatientFollowupCardHelper;
import org.smartregister.tbr.helper.view.RenderPositiveResultsCardHelper;
import org.smartregister.tbr.helper.view.RenderServiceHistoryCardHelper;
import org.smartregister.tbr.jsonspec.ConfigurableViewsHelper;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.tbr.model.Register;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;
import org.smartregister.view.fragment.SecuredFragment;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import util.TbrConstants;

import static org.smartregister.tbr.activity.BaseRegisterActivity.TOOLBAR_TITLE;
import static util.TbrConstants.ENKETO_FORMS;

/**
 * Created by ndegwamartin on 06/12/2017.
 */

public abstract class BasePatientDetailsFragment extends SecuredFragment implements View.OnClickListener {

    protected Map<String, String> patientDetails;
    protected ResultMenuListener resultMenuListener;
    protected Map<String, String> languageTranslations;
    private static String TAG = BasePatientDetailsFragment.class.getCanonicalName();
    private FormOverridesHelper formOverridesHelper;

    protected abstract void setPatientDetails(Map<String, String> patientDetails);

    protected abstract String getViewConfigurationIdentifier();

    protected void renderPositiveResultsView(View view, Map<String, String> patientDetails) {
        RenderPositiveResultsCardHelper renderPositiveResultsHelper = new RenderPositiveResultsCardHelper(getActivity(), TbrApplication.getInstance().getResultsRepository());
        renderPositiveResultsHelper.renderView(view, patientDetails);
    }

    protected void renderDemographicsView(View view, Map<String, String> patientDetails) {

        RenderPatientDemographicCardHelper renderPatientDemographicCardHelper = new RenderPatientDemographicCardHelper(getActivity(), TbrApplication.getInstance().getResultDetailsRepository());
        renderPatientDemographicCardHelper.renderView(view, patientDetails);

    }

    protected void renderFollowUpView(View view, Map<String, String> patientDetails) {

        RenderPatientFollowupCardHelper renderPatientFollowupCardHelper = new RenderPatientFollowupCardHelper(getActivity(), TbrApplication.getInstance().getResultDetailsRepository());
        renderPatientFollowupCardHelper.renderView(view, patientDetails);

    }

    protected void renderServiceHistoryView(View view, Map<String, String> patientDetails) {
        RenderServiceHistoryCardHelper renderServiceHistoryHelper = new RenderServiceHistoryCardHelper(getActivity(), TbrApplication.getInstance().getResultDetailsRepository());
        renderServiceHistoryHelper.renderView(view, patientDetails);
    }


    protected void renderContactScreeningView(View view, Map<String, String> patientDetails) {
        RenderContactScreeningCardHelper renderContactScreeningHelper = new RenderContactScreeningCardHelper(getActivity(), TbrApplication.getInstance().getResultsRepository());
        renderContactScreeningHelper.renderView(view, patientDetails);
    }

    protected void processLanguageTokens(Map<String, String> viewLabelsMap, View parentView) {
        try {
            //Process token translations
            if (!viewLabelsMap.isEmpty()) {
                for (Map.Entry<String, String> entry : viewLabelsMap.entrySet()) {
                    String uniqueIdentifier = entry.getKey();
                    View view = parentView.findViewById(Utils.getLayoutIdentifierResourceId(getActivity(), uniqueIdentifier));
                    if (view instanceof TextView) {
                        TextView textView = (TextView) view;
                        if (textView != null) {
                            String translated = getTranslatedToken(entry.getKey(), textView.getText().toString());
                            textView.setText(translated);

                        }
                    } else {
                        Log.w(TAG, " IDentifier for Language Token '" + uniqueIdentifier + "' clashes with a non TextView");
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private String getTranslatedToken(String token) {
        return getTranslatedToken(token, token);
    }

    private String getTranslatedToken(String token, String defaultReturn) {
        if (languageTranslations != null && !languageTranslations.isEmpty() && languageTranslations.containsKey(token)) {
            return languageTranslations.get(token);
        } else return defaultReturn;
    }

    protected void renderBMIHeightChartView(View view, Map<String, String> patientDetails) {

        RenderBMIHeightChartCardHelper renderBMIHeightChartCardHelper = new RenderBMIHeightChartCardHelper(getActivity(), TbrApplication.getInstance().getResultDetailsRepository());
        renderBMIHeightChartCardHelper.renderView(view, patientDetails);

    }

    @Override
    protected void onCreation() {

        resultMenuListener = new ResultMenuListener(patientDetails.get(Constants.KEY._ID));
        formOverridesHelper = new FormOverridesHelper(patientDetails);

    }

    @Override
    protected void onResumption() {
        //Overrides
    }


    class ResultMenuListener implements PopupMenu.OnMenuItemClickListener {

        private String clientIdentifier;

        ResultMenuListener(String clientIdentifier) {
            this.clientIdentifier = clientIdentifier;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            BasePatientDetailActivity registerActivity = (BasePatientDetailActivity) getActivity();
            switch (item.getItemId()) {
                case R.id.result_gene_xpert:
                    registerActivity.startFormActivity(ENKETO_FORMS.GENE_XPERT, clientIdentifier, formOverridesHelper.getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_smear:
                    registerActivity.startFormActivity(ENKETO_FORMS.SMEAR, clientIdentifier, formOverridesHelper.getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_chest_xray:
                    registerActivity.startFormActivity(ENKETO_FORMS.CHEST_XRAY, clientIdentifier, formOverridesHelper.getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_culture:
                    registerActivity.startFormActivity(ENKETO_FORMS.CULTURE, clientIdentifier, formOverridesHelper.getFieldOverrides().getJSONString());
                    return true;
                default:
                    return false;
            }
        }
    }


    public void showResultMenu(View view) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        popup.inflate(R.menu.menu_register_result);
        popup.setOnMenuItemClickListener(resultMenuListener);
        MenuItem item = popup.getMenu().getItem(0);
        String name = getActivity().getString(R.string.add_result_for) + Constants.CHAR.SPACE + patientDetails.get(Constants.KEY.FIRST_NAME) + Constants.CHAR.SPACE + patientDetails.get(Constants.KEY.LAST_NAME);
        SpannableString s = new SpannableString(name);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        item.setTitle(s);
        popup.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshView(EnketoFormSaveCompleteEvent enketoFormSaveCompleteEvent) {
        if (enketoFormSaveCompleteEvent != null) {
            if (enketoFormSaveCompleteEvent.getFormName().equals(Constants.FORM.DIAGNOSIS)) {
                initializeRegister(new Intent(getActivity(), PositivePatientRegisterActivity.class), getTranslatedToken(Register.POSITIVE_PATIENTS, getString(R.string.positive_patients)));

            } else if (enketoFormSaveCompleteEvent.getFormName().equals(ENKETO_FORMS.TREATMENT_INITIATION)) {
                initializeRegister(new Intent(getActivity(), InTreatmentPatientRegisterActivity.class), getTranslatedToken(Register.IN_TREATMENT_PATIENTS, getString(R.string.in_treatment_patients)));

            } else if (enketoFormSaveCompleteEvent.getFormName().equals(Constants.FORM.REMOVE_PATIENT) || enketoFormSaveCompleteEvent.getFormName().equals(Constants.FORM.TREATMENT_OUTCOME)) {
                startActivity(new Intent(getActivity(), HomeActivity.class));
            } else {
                processViewConfigurations(getView());
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshView(SyncEvent syncEvent) {
        if (syncEvent != null && syncEvent.getFetchStatus().equals(FetchStatus.fetched)) {
            processViewConfigurations(getView());
        }
    }

    private void initializeRegister(Intent intent, String registerTitle) {
        intent.putExtra(TOOLBAR_TITLE, registerTitle);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        formOverridesHelper.setPatientDetails(patientDetails);
        switch (view.getId()) {
            case R.id.add_contact:
                ((BasePatientDetailActivity) getActivity()).startFormActivity(ENKETO_FORMS.ADD_TB_CONTACT, patientDetails.get(TbrConstants.KEY.BASE_ENTITY_ID), formOverridesHelper.getAddContactFieldOverrides().getJSONString());
                break;
            case R.id.follow_up_button:
                ((BasePatientDetailActivity) getActivity()).startFormActivity(ENKETO_FORMS.FOLLOWUP_VISIT, view.getTag(R.id.CLIENT_ID).toString(), formOverridesHelper.getFollowUpFieldOverrides().getJSONString());
                break;
            case R.id.record_results:
                showResultMenu(view);
                break;
            case R.id.remove_patient:
                ((BasePatientDetailActivity) getActivity()).startFormActivity(Constants.FORM.REMOVE_PATIENT, view.getTag(R.id.CLIENT_ID).toString(), formOverridesHelper.getFieldOverrides().getJSONString());
                break;
            case R.id.record_outcome:
                ((BasePatientDetailActivity) getActivity()).startFormActivity(Constants.FORM.TREATMENT_OUTCOME, view.getTag(R.id.CLIENT_ID).toString(), formOverridesHelper.getFieldOverrides().getJSONString());
                break;
            default:
                break;

        }
    }

    protected void setupViews(View rootView) {

        //Load Language Token Map
        ViewConfiguration config = TbrApplication.getJsonSpecHelper().getLanguage(Utils.getLanguage());
        languageTranslations = config == null ? null : config.getLabels();

        setUpButtons(rootView);

        //Record Results click handler
        TextView recordResults = (TextView) rootView.findViewById(R.id.record_results);
        if (recordResults != null) {
            recordResults.setOnClickListener(this);
        }
    }

    private void setUpButtons(View rootView) {

        if (patientDetails != null) {

            Button removePatientButton = (Button) rootView.findViewById(R.id.remove_patient);
            if (removePatientButton != null) {
                removePatientButton.setTag(R.id.CLIENT_ID, patientDetails.get(Constants.KEY._ID));
                removePatientButton.setOnClickListener(this);
            }

            Button recordOutcomeButton = (Button) rootView.findViewById(R.id.record_outcome);
            if (recordOutcomeButton != null && getViewConfigurationIdentifier().equals(Constants.CONFIGURATION.PATIENT_DETAILS_INTREATMENT)) {
                recordOutcomeButton.setTag(R.id.CLIENT_ID, patientDetails.get(Constants.KEY._ID));
                recordOutcomeButton.setVisibility(View.VISIBLE);
                recordOutcomeButton.setOnClickListener(this);
            }

            Button followUpButton = (Button) rootView.findViewById(R.id.follow_up_button);
            if (followUpButton != null) {
                followUpButton.setTag(R.id.CLIENT_ID, patientDetails.get(Constants.KEY._ID));
                followUpButton.setOnClickListener(this);
            }

            TextView addContactView = (TextView) rootView.findViewById(R.id.add_contact);
            if (addContactView != null) {
                addContactView.setTag(R.id.CLIENT_ID, patientDetails.get(Constants.KEY._ID));
                addContactView.setOnClickListener(this);
            }
        }
    }

    protected int getCardViewIdentifierByConfiguration(String viewConfigurationIdentifier) {

        int res = 0;
        switch (viewConfigurationIdentifier) {
            case Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_DEMOGRAPHICS:
                res = R.id.clientDetailsCardView;
                break;
            case Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_POSITIVE:
                res = R.id.clientPositiveResultsCardView;
                break;
            case Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_SERVICE_HISTORY:
                res = R.id.clientServiceHistoryCardView;
                break;
            case Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_BMI:
                res = R.id.clientBMIHeightChartCardView;
                break;
            case Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_CONTACT_SCREENING:
                res = R.id.clientContactScreeningCardView;
                break;
            case Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_FOLLOWUP:
                res = R.id.clientFollowupCardView;
                break;

            default:
                break;
        }
        return res;
    }

    protected void processViewConfigurations(View rootView) {
        try {
            String jsonString = TbrApplication.getInstance().getConfigurableViewsRepository().getConfigurableViewJson(getViewConfigurationIdentifier());
            if (jsonString == null) {
                renderDefaultLayout(rootView);

            } else {
                ViewConfiguration detailsView = TbrApplication.getJsonSpecHelper().getConfigurableView(jsonString);
                List<org.smartregister.tbr.jsonspec.model.View> views = detailsView.getViews();
                if (!views.isEmpty()) {
                    Collections.sort(views, new Comparator<org.smartregister.tbr.jsonspec.model.View>() {
                        @Override
                        public int compare(org.smartregister.tbr.jsonspec.model.View registerA, org.smartregister.tbr.jsonspec.model.View registerB) {
                            return registerA.getResidence().getPosition() - registerB.getResidence().getPosition();
                        }
                    });

                    LinearLayout viewParent = (LinearLayout) rootView.findViewById(getContainerViewId());
                    for (org.smartregister.tbr.jsonspec.model.View componentView : views) {

                        try {
                            if (componentView.getResidence().getParent() == null) {
                                componentView.getResidence().setParent(detailsView.getIdentifier());
                            }

                            String jsonComponentString = TbrApplication.getInstance().getConfigurableViewsRepository().getConfigurableViewJson(componentView.getIdentifier());
                            ViewConfiguration componentViewConfiguration = TbrApplication.getJsonSpecHelper().getConfigurableView(jsonComponentString);
                            if (componentViewConfiguration != null) {

                                ConfigurableViewsHelper configurableViewsHelper = TbrApplication.getInstance().getConfigurableViewsHelper();

                                View fallbackView = viewParent.findViewById(getCardViewIdentifierByConfiguration(componentViewConfiguration.getIdentifier()));
                                View json2View = TbrApplication.getJsonSpecHelper().isEnableJsonViews() ? configurableViewsHelper.inflateDynamicView(componentViewConfiguration, viewParent, fallbackView, componentView.isVisible()) : fallbackView;
                                if (componentView.isVisible()) {

                                    if (componentViewConfiguration.getIdentifier().equals(Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_DEMOGRAPHICS)) {
                                        json2View.setTag(R.id.VIEW_CONFIGURATION_ID, getViewConfigurationIdentifier());
                                        renderDemographicsView(json2View, patientDetails);

                                    } else if (componentViewConfiguration.getIdentifier().equals(Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_POSITIVE)) {
                                        renderPositiveResultsView(json2View, patientDetails);
                                        //Record Results click handler
                                        TextView recordResults = (TextView) json2View.findViewById(R.id.record_results);
                                        recordResults.setOnClickListener(this);

                                    } else if (componentViewConfiguration.getIdentifier().equals(Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_FOLLOWUP)) {

                                        renderFollowUpView(json2View, patientDetails);
                                        Button followUpButton = (Button) json2View.findViewById(R.id.follow_up_button);
                                        followUpButton.setTag(R.id.CLIENT_ID, patientDetails.get(Constants.KEY._ID));
                                        followUpButton.setOnClickListener(this);
                                    } else if (componentViewConfiguration.getIdentifier().equals(Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_BMI)) {
                                        json2View.setTag(R.id.BASE_ENTITY_ID, patientDetails.get(Constants.KEY._ID));
                                        renderBMIHeightChartView(json2View, patientDetails);

                                    } else if (componentViewConfiguration.getIdentifier().equals(Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_CONTACT_SCREENING)) {
                                        renderContactScreeningView(json2View, patientDetails);

                                        TextView addContactView = (TextView) json2View.findViewById(R.id.add_contact);
                                        addContactView.setTag(R.id.CLIENT_ID, patientDetails.get(Constants.KEY._ID));
                                        addContactView.setOnClickListener(this);

                                    } else if (componentViewConfiguration.getIdentifier().equals(Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_SERVICE_HISTORY)) {

                                        renderServiceHistoryView(json2View, patientDetails);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                } else {
                    renderDefaultLayout(rootView);
                }

                if (detailsView != null) {
                    processLanguageTokens(detailsView.getLabels(), rootView);
                }
            }
        } catch (Exception e) {

            Log.e(TAG, e.getMessage());
        }

    }

    protected void renderDefaultLayout(View rootView) {

        switch (getViewConfigurationIdentifier()) {
            case Constants.CONFIGURATION.PATIENT_DETAILS_INTREATMENT:
                renderDemographicsView(rootView, patientDetails);
                renderPositiveResultsView(rootView, patientDetails);
                renderContactScreeningView(rootView, patientDetails);
                renderFollowUpView(rootView, patientDetails);
                renderBMIHeightChartView(rootView, patientDetails);
                break;
            case Constants.CONFIGURATION.PATIENT_DETAILS_POSITIVE:
                renderDemographicsView(rootView, patientDetails);
                renderPositiveResultsView(rootView, patientDetails);
                renderContactScreeningView(rootView, patientDetails);
                renderServiceHistoryView(rootView, patientDetails);
                break;
            case Constants.CONFIGURATION.PATIENT_DETAILS_PRESUMPTIVE:
                renderDemographicsView(rootView, patientDetails);
                renderPositiveResultsView(rootView, patientDetails);
                renderServiceHistoryView(rootView, patientDetails);
                break;
            default:
                break;
        }

    }

    private int getContainerViewId() {
        int id = 0;
        switch (getViewConfigurationIdentifier()) {
            case Constants.CONFIGURATION.PATIENT_DETAILS_INTREATMENT:
                id = R.id.content_intreatment_patient_detail_container;
                break;
            case Constants.CONFIGURATION.PATIENT_DETAILS_POSITIVE:
                id = R.id.content_positive_patient_detail_container;
                break;
            case Constants.CONFIGURATION.PATIENT_DETAILS_PRESUMPTIVE:
                id = R.id.content_presumptive_patient_detail_container;
                break;
            default:
                break;
        }

        return id;

    }

}
