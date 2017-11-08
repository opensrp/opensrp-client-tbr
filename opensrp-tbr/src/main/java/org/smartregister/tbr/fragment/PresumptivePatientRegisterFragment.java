package org.smartregister.tbr.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.smartregister.cursoradapter.CursorSortOption;
import org.smartregister.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.tbr.PatientRegisterProvider;
import org.smartregister.tbr.R;

import util.TbrConstants;

import static org.smartregister.tbr.activity.BaseRegisterActivity.TOOLBAR_TITLE;

/**
 * Created by samuelgithengi on 11/6/17.
 */

public class PresumptivePatientRegisterFragment extends BaseRegisterFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_activity, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.register_toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(activity.getIntent().getStringExtra(TOOLBAR_TITLE));
        setupViews(view);
        return view;

    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
        view.findViewById(R.id.sorted_by_bar).setVisibility(View.GONE);
        initializeQueries();
        populateClientListHeaderView(view);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            initializeQueries();
        }
    }

    private void initializeQueries() {
        String tableName = TbrConstants.PATIENT_TABLE_NAME;

        PatientRegisterProvider hhscp = new PatientRegisterProvider(getActivity());
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, hhscp, context().commonrepository(tableName));
        clientsView.setAdapter(clientAdapter);

        setTablename(tableName);
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
        countqueryBUilder.SelectInitiateMainTableCounts(tableName);
        mainCondition = " tbreach_id is not NULL ";
        countSelect = countqueryBUilder.mainCondition(mainCondition);
        super.CountExecute();

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, new String[]{
                tableName + ".relationalid",
                tableName + "." + TbrConstants.KEY.FIRST_NAME,
                tableName + "." + TbrConstants.KEY.LAST_NAME,
                tableName + "." + TbrConstants.KEY.TBREACH_ID,
                tableName + "." + TbrConstants.KEY.GENDER,
                tableName + "." + TbrConstants.KEY.DOB
        });
        mainSelect = queryBUilder.mainCondition(mainCondition);
        Sortqueries = ((CursorSortOption) getDefaultOptionsProvider().sortOption()).sort();

        currentlimit = 20;
        currentoffset = 0;

        super.filterandSortInInitializeQueries();

        refresh();
    }


    private void populateClientListHeaderView(View view) {
        LinearLayout clientsHeaderLayout = (LinearLayout) view.findViewById(org.smartregister.R.id.clients_header_layout);
        clientsHeaderLayout.setVisibility(View.GONE);

        LinearLayout headerLayout = (LinearLayout) getLayoutInflater(null).inflate(R.layout.register_list_header, null);
        clientsView.addHeaderView(headerLayout);
        clientsView.setEmptyView(getActivity().findViewById(R.id.empty_view));

    }
}
