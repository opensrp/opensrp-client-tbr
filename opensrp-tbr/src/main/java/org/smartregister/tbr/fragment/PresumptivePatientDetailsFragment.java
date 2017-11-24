package org.smartregister.tbr.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.helper.view.RenderPatientDemographicCardHelper;
import org.smartregister.tbr.helper.view.RenderPositiveResultsCardHelper;
import org.smartregister.tbr.helper.view.RenderServiceHistoryCardHelper;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

import java.util.HashMap;
import java.util.Map;

import util.TbrConstants;

import static org.smartregister.tbr.util.Constants.INTENT_KEY.REGISTER_TITLE;

/**
 * Created by ndegwamartin on 24/11/2017.
 */


public class PresumptivePatientDetailsFragment extends BaseRegisterFragment {
    private Map<String, String> patientDetails;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_presumptive_patient_detail, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(activity.getIntent().getStringExtra(REGISTER_TITLE));
        setupViews(view);
        return view;
    }


    @Override
    public void setupViews(View view) {
        processViews(view);
        processViewConfigurations();
    }

    public void setPatientDetails(Map<String, String> patientDetails) {
        this.patientDetails = patientDetails;
    }

    ;

    private void processViewConfigurations() {


    }

    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            initializeQueries();
        }
        processViewConfigurations();
    }


    private void initializeQueries() {
    }



    @Override
    protected String getMainCondition() {
        return null;
    }


    private void processViews(View view) {

        renderDemographicsView(view);
        renderPositiveResultsView(view);
        renderServiceHistoryView(view);

        //Remove patient button
        Button removePatientButton = (Button) view.findViewById(R.id.removePatientButton);
        removePatientButton.setTag(R.id.CLIENT_ID, patientDetails.get(Constants.KEY._ID));

        //Record Results
        TextView recordResults = (TextView) view.findViewById(R.id.recordResultsTextView);
        recordResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showToast(getActivity(), "Recording patient results ...");
            }
        });
    }

    private void renderPositiveResultsView(View view) {
        RenderPositiveResultsCardHelper renderPositiveResultsHelper = new RenderPositiveResultsCardHelper(getActivity(), TbrApplication.getInstance().getResultDetailsRepository());
        Map<String, String> extra = new HashMap<>();
        extra.put(Constants.KEY.LAST_INTERACTED_WITH, patientDetails.get(Constants.KEY.LAST_INTERACTED_WITH));
        renderPositiveResultsHelper.renderView(patientDetails.get(Constants.KEY._ID), view.findViewById(R.id.clientPositiveResultsCardView), extra);
    }

    private void renderServiceHistoryView(View view) {
        RenderServiceHistoryCardHelper renderServiceHistoryHelper = new RenderServiceHistoryCardHelper(getActivity(), TbrApplication.getInstance().getResultDetailsRepository());
        renderServiceHistoryHelper.renderView(patientDetails.get(Constants.KEY._ID), view.findViewById(R.id.clientServiceHistoryCardView));
    }

    private void renderDemographicsView(View view) {

        RenderPatientDemographicCardHelper renderPatientDemographicCardHelper = new RenderPatientDemographicCardHelper(getActivity(), TbrApplication.getInstance().getResultDetailsRepository());
        renderPatientDemographicCardHelper.renderView(patientDetails.get(Constants.KEY._ID), view.findViewById(R.id.clientDetailsCardView), patientDetails);

    }
}
