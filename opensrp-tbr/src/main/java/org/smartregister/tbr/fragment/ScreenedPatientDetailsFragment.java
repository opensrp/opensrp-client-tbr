package org.smartregister.tbr.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.tbr.R;
import org.smartregister.tbr.util.Constants;

import java.util.Map;

/**
 * Created by samuelgithengi on 2/1/18.
 */

public class ScreenedPatientDetailsFragment extends BasePatientDetailsFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_screened_patient_detail, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(Constants.SCREENED_PATIENT);
        setupViews(rootView);
        return rootView;
    }

    @Override
    protected void processViewConfigurations(View rootView) {
        renderDefaultLayout(rootView);
    }

    @Override
    protected void renderDefaultLayout(View rootView) {
        renderDemographicsView(rootView, patientDetails);
        renderServiceHistoryView(rootView, patientDetails);
    }

    @Override
    public void setPatientDetails(Map<String, String> patientDetails) {
        this.patientDetails = patientDetails;
    }

    @Override
    protected String getViewConfigurationIdentifier() {
        return "";
    }
}
