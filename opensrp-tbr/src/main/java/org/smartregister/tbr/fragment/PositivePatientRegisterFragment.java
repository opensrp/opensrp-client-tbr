package org.smartregister.tbr.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.avocarrot.json2view.DynamicView;

import org.json.JSONObject;
import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.BaseRegisterActivity;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;

import java.util.HashMap;
import java.util.Map;

import static org.smartregister.tbr.activity.BaseRegisterActivity.TOOLBAR_TITLE;
import static util.TbrConstants.REGISTER_COLUMNS.DIAGNOSE;
import static util.TbrConstants.REGISTER_COLUMNS.DROPDOWN;
import static util.TbrConstants.REGISTER_COLUMNS.ENCOUNTER;
import static util.TbrConstants.REGISTER_COLUMNS.PATIENT;
import static util.TbrConstants.REGISTER_COLUMNS.RESULTS;
import static util.TbrConstants.REGISTER_COLUMNS.XPERT_RESULTS;
import static util.TbrConstants.VIEW_CONFIGS.POSITIVE_REGISTER_HEADER;

/**
 * Created by samuelgithengi on 11/27/17.
 */

public class PositivePatientRegisterFragment extends BaseRegisterFragment {

    private String viewConfigurationIdentifier;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_activity, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.register_toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(activity.getIntent().getStringExtra(TOOLBAR_TITLE));
        viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        setupViews(view);
        return view;
    }

    @Override
    protected void populateClientListHeaderView(View view) {
        LinearLayout clientsHeaderLayout = (LinearLayout) view.findViewById(org.smartregister.R.id.clients_header_layout);
        clientsHeaderLayout.setVisibility(View.GONE);
        View headerLayout;
        ViewConfiguration viewConfiguration = TbrApplication.getInstance().getConfigurableViewsHelper().getViewConfiguration(POSITIVE_REGISTER_HEADER);
        if (viewConfiguration == null) {
            headerLayout = getLayoutInflater(null).inflate(R.layout.register_positive_list_header, null);
        } else {
            JSONObject jsonView = new JSONObject(viewConfiguration.getJsonView());
            headerLayout = DynamicView.createView(getActivity().getApplicationContext(), jsonView);
            headerLayout.setLayoutParams(
                    new AbsListView.LayoutParams(
                            AbsListView.LayoutParams.MATCH_PARENT,
                            AbsListView.LayoutParams.MATCH_PARENT));
            Map<String, Integer> mapping = new HashMap();
            mapping.put(PATIENT, R.id.patient_header);
            mapping.put(RESULTS, R.id.results_header);
            mapping.put(DIAGNOSE, R.id.diagnose_header);
            mapping.put(ENCOUNTER, R.id.encounter_header);
            mapping.put(XPERT_RESULTS, R.id.xpert_results_header);
            mapping.put(DROPDOWN, R.id.dropdown_header);

            TbrApplication.getInstance().getConfigurableViewsHelper().processRegisterColumns(mapping, headerLayout, visibleColumns, R.id.register_headers);
        }
        clientsView.addHeaderView(headerLayout);
        clientsView.setEmptyView(getActivity().findViewById(R.id.empty_view));

    }

    @Override
    protected String getMainCondition() {
        return " confirmed_tb = \"yes\"";
    }

    @Override
    protected String getViewConfigurationIdentifier() {
        return viewConfigurationIdentifier;
    }
}
