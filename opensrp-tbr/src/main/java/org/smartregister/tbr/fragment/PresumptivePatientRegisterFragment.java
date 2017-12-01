package org.smartregister.tbr.fragment;


import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.avocarrot.json2view.DynamicView;

import org.json.JSONObject;
import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;

import java.util.HashMap;
import java.util.Map;

import static util.TbrConstants.REGISTER_COLUMNS.DIAGNOSE;
import static util.TbrConstants.REGISTER_COLUMNS.DROPDOWN;
import static util.TbrConstants.REGISTER_COLUMNS.ENCOUNTER;
import static util.TbrConstants.REGISTER_COLUMNS.PATIENT;
import static util.TbrConstants.REGISTER_COLUMNS.RESULTS;
import static util.TbrConstants.REGISTER_COLUMNS.XPERT_RESULTS;
import static util.TbrConstants.VIEW_CONFIGS.PRESUMPTIVE_REGISTER_HEADER;

/**
 * Created by samuelgithengi on 11/6/17.
 */

public class PresumptivePatientRegisterFragment extends BaseRegisterFragment {


    @Override
    protected void populateClientListHeaderView(View view) {
        LinearLayout clientsHeaderLayout = (LinearLayout) view.findViewById(org.smartregister.R.id.clients_header_layout);
        clientsHeaderLayout.setVisibility(View.GONE);
        View headerLayout;
        ViewConfiguration viewConfiguration = TbrApplication.getInstance().getConfigurableViewsHelper().getViewConfiguration(PRESUMPTIVE_REGISTER_HEADER);
        if (viewConfiguration == null) {
            headerLayout = getLayoutInflater(null).inflate(R.layout.register_list_header, null);
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
        return " presumptive =\"yes\" AND confirmed_tb IS NULL";
    }


}
