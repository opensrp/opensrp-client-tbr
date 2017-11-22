package org.smartregister.tbr.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.CursorSortOption;
import org.smartregister.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.PresumptivePatientDetailActivity;
import org.smartregister.tbr.activity.PresumptivePatientRegisterActivity;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.jsonspec.model.RegisterConfiguration;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.tbr.provider.PatientRegisterProvider;
import org.smartregister.tbr.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import util.TbrConstants;

import static org.smartregister.tbr.activity.BaseRegisterActivity.TOOLBAR_TITLE;
import static util.TbrConstants.REGISTER_COLUMNS.DIAGNOSE;
import static util.TbrConstants.REGISTER_COLUMNS.ENCOUNTER;
import static util.TbrConstants.REGISTER_COLUMNS.PATIENT;
import static util.TbrConstants.REGISTER_COLUMNS.RESULTS;
import static util.TbrConstants.REGISTER_COLUMNS.XPERT_RESULTS;

/**
 * Created by samuelgithengi on 11/6/17.
 */

public class PresumptivePatientRegisterFragment extends BaseRegisterFragment {

    private RegisterActionHandler registerActionHandler = new RegisterActionHandler();
    private ResultMenuListener resultMenuListener = new ResultMenuListener();
    private CommonPersonObjectClient patient;
    private Set<org.smartregister.tbr.jsonspec.model.View> visibleColumns;
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
        viewConfigurationIdentifier = ((PresumptivePatientRegisterActivity) getActivity()).getViewIdentifier();
        setupViews(view);
        return view;
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
        view.findViewById(R.id.sorted_by_bar).setVisibility(View.GONE);
        processViewConfigurations();
        initializeQueries();
        updateSearchView();
        populateClientListHeaderView(view);
    }

    private void processViewConfigurations() {
        ViewConfiguration viewConfiguration = TbrApplication.getInstance().getConfigurableViewsHelper().getViewConfiguration(viewConfigurationIdentifier);
        RegisterConfiguration config = (RegisterConfiguration) viewConfiguration.getMetadata();
        if (config.getSearchBarText() != null && getView() != null)
            ((EditText) getView().findViewById(R.id.edt_search)).setHint(config.getSearchBarText());
        visibleColumns = TbrApplication.getInstance().getConfigurableViewsHelper().getRegisterActiveColumns(viewConfigurationIdentifier);

    }

    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            initializeQueries();
        }
        updateSearchView();
        processViewConfigurations();
    }

    public void showResultMenu(View view) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        popup.inflate(R.menu.menu_register_result);
        popup.setOnMenuItemClickListener(resultMenuListener);
        MenuItem item = popup.getMenu().getItem(0);
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, item.getTitle().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        item.setTitle(s);
        popup.show();
    }

    private void initializeQueries() {
        String tableName = TbrConstants.PATIENT_TABLE_NAME;

        PatientRegisterProvider hhscp = new PatientRegisterProvider(getActivity(), visibleColumns, registerActionHandler, context().detailsRepository());
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, hhscp, context().commonrepository(tableName));
        clientsView.setAdapter(clientAdapter);

        setTablename(tableName);
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
        countqueryBUilder.SelectInitiateMainTableCounts(tableName);
        mainCondition = getMainCondition();
        countSelect = countqueryBUilder.mainCondition(mainCondition);
        super.CountExecute();

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, new String[]{
                tableName + ".relationalid",
                tableName + ".last_interacted_with",
                tableName + "." + TbrConstants.KEY.BASE_ENTITY_ID_COLUMN,
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
        Map<String, Integer> mapping = new HashMap();
        mapping.put(PATIENT, R.id.patient_header);
        mapping.put(RESULTS, R.id.results_header);
        mapping.put(DIAGNOSE, R.id.diagnose_header);
        mapping.put(ENCOUNTER, R.id.encounter_header);
        mapping.put(XPERT_RESULTS, R.id.xpert_results_header);
        TbrApplication.getInstance().getConfigurableViewsHelper().processRegisterColumns(mapping, headerLayout, visibleColumns, R.id.register_headers);
        clientsView.addHeaderView(headerLayout);
        clientsView.setEmptyView(getActivity().findViewById(R.id.empty_view));

    }

    private void updateSearchView() {
        getSearchView().removeTextChangedListener(textWatcher);
        getSearchView().addTextChangedListener(textWatcher);
    }

    private FieldOverrides getFieldOverrides() {
        FieldOverrides fieldOverrides = null;
        Map fields = new HashMap();
        fields.put("participant_id", patient.getDetails().get(TbrConstants.KEY.TBREACH_ID));
        JSONObject fieldOverridesJson = new JSONObject(fields);
        fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }

    @Override
    protected String getMainCondition() {
        return " presumptive =\"yes\" AND confirmed_tb IS NULL";
    }

    class RegisterActionHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            PresumptivePatientRegisterActivity registerActivity = (PresumptivePatientRegisterActivity) getActivity();
            if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
                patient = (CommonPersonObjectClient) view.getTag();
            }
            switch (view.getId()) {
                case R.id.dropdown_btn:
                case R.id.result_lnk:
                    showResultMenu(view);
                    break;
                case R.id.diagnose_lnk:
                    registerActivity.startFormActivity("diagnosis", patient.getDetails().get("_id"), null);
                    break;
                case R.id.xpert_result_lnk:
                    registerActivity.startFormActivity("result_gene_xpert", patient.getDetails().get("_id"), getFieldOverrides().getJSONString());
                    break;
                case R.id.patient_column:
                    Intent intent = new Intent(registerActivity, PresumptivePatientDetailActivity.class);
                    intent.putExtra(Constants.INTENT_KEY.REGISTER_TITLE, registerActivity.getIntent().getStringExtra(TOOLBAR_TITLE));
                    startActivity(intent);


                    break;
                default:
                    break;
            }
        }
    }

    class ResultMenuListener implements PopupMenu.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            PresumptivePatientRegisterActivity registerActivity = (PresumptivePatientRegisterActivity) getActivity();
            switch (item.getItemId()) {
                case R.id.result_gene_xpert:
                    registerActivity.startFormActivity("result_gene_xpert", patient.getDetails().get("_id"), getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_smear:
                    registerActivity.startFormActivity("result_smear", patient.getDetails().get("_id"), getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_chest_xray:
                    registerActivity.startFormActivity("result_chest_xray", patient.getDetails().get("_id"), getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_culture:
                    registerActivity.startFormActivity("result_culture", patient.getDetails().get("_id"), getFieldOverrides().getJSONString());
                    return true;
                default:
                    return false;
            }
        }
    }
}
