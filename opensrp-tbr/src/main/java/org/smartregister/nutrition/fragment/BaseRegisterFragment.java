package org.smartregister.nutrition.fragment;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.helper.ConfigurableViewsHelper;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.TestResultsConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.cursoradapter.CursorCommonObjectFilterOption;
import org.smartregister.cursoradapter.CursorCommonObjectSort;
import org.smartregister.cursoradapter.CursorSortOption;
import org.smartregister.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.smartregister.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.nutrition.R;
import org.smartregister.nutrition.activity.BaseRegisterActivity;
import org.smartregister.nutrition.activity.InTreatmentPatientDetailActivity;
import org.smartregister.nutrition.activity.InTreatmentPatientRegisterActivity;
import org.smartregister.nutrition.activity.PositivePatientDetailActivity;
import org.smartregister.nutrition.activity.PositivePatientRegisterActivity;
import org.smartregister.nutrition.activity.PresumptivePatientDetailActivity;
import org.smartregister.nutrition.activity.PresumptivePatientRegisterActivity;
import org.smartregister.nutrition.application.TbrApplication;
import org.smartregister.nutrition.helper.FormOverridesHelper;
import org.smartregister.nutrition.provider.PatientRegisterProvider;
import org.smartregister.nutrition.servicemode.TbrServiceModeOption;
import org.smartregister.nutrition.util.Constants;
import org.smartregister.nutrition.util.FilterEnum;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import util.TbrConstants;
import util.TbrConstants.KEY;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.smartregister.nutrition.activity.BaseRegisterActivity.TOOLBAR_TITLE;
import static util.TbrConstants.ENKETO_FORMS.CHEST_XRAY;
import static util.TbrConstants.ENKETO_FORMS.CULTURE;
import static util.TbrConstants.ENKETO_FORMS.DIAGNOSIS;
import static util.TbrConstants.ENKETO_FORMS.FOLLOWUP_VISIT;
import static util.TbrConstants.ENKETO_FORMS.GENE_XPERT;
import static util.TbrConstants.ENKETO_FORMS.KUNJUNGAN_GIZI;
import static util.TbrConstants.ENKETO_FORMS.SMEAR;
import static util.TbrConstants.ENKETO_FORMS.TREATMENT_INITIATION;
import static util.TbrConstants.REGISTER_COLUMNS.BASELINE;
import static util.TbrConstants.REGISTER_COLUMNS.DIAGNOSE;
import static util.TbrConstants.REGISTER_COLUMNS.FOLLOWUP;
import static util.TbrConstants.REGISTER_COLUMNS.FOLLOWUP_SCHEDULE;
import static util.TbrConstants.REGISTER_COLUMNS.INTREATMENT_RESULTS;
import static util.TbrConstants.REGISTER_COLUMNS.PATIENT;
import static util.TbrConstants.REGISTER_COLUMNS.RESULTS;
import static util.TbrConstants.REGISTER_COLUMNS.SMEAR_RESULTS;
import static util.TbrConstants.REGISTER_COLUMNS.SMEAR_SCHEDULE;
import static util.TbrConstants.REGISTER_COLUMNS.TREAT;
import static util.TbrConstants.REGISTER_COLUMNS.TREATMENT;
import static util.TbrConstants.REGISTER_COLUMNS.XPERT_RESULTS;
import static util.TbrConstants.VIEW_CONFIGS.COMMON_REGISTER_HEADER;

//import org.smartregister.tbr.jsonspec.model.ViewConfiguration;

/**
 * Created by samuelgithengi on 11/8/17.
 */

public abstract class BaseRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {

    protected Set<org.smartregister.configurableviews.model.View> visibleColumns = new TreeSet<>();
    protected ResultMenuListener resultMenuListener = new ResultMenuListener();
    protected CommonPersonObjectClient patient;
    protected RegisterActionHandler registerActionHandler = new RegisterActionHandler();

    private String viewConfigurationIdentifier;

    private FormOverridesHelper formOverridesHelper;
    private String customMainCondition;
    private Snackbar snackbar;

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {


            @Override
            public ServiceModeOption serviceMode() {
                return new TbrServiceModeOption(null, "Linda Clinic", new int[]{
                        R.string.patient_name, R.string.participant_id, R.string.mobile_phone_number
                }, new int[]{5, 3, 2});
            }

            @Override
            public FilterOption villageFilter() {
                return new CursorCommonObjectFilterOption("no village filter", "");
            }

            @Override
            public SortOption sortOption() {
                return new CursorCommonObjectSort(getResources().getString(R.string.alphabetical_sort), "first_name asc");
            }

            @Override
            public String nameInShortFormForTitle() {
                return context().getStringResource(R.string.tbreach);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
                        new CursorCommonObjectSort(getResources().getString(R.string.alphabetical_sort), KEY.FIRST_NAME),
                        new CursorCommonObjectSort(getResources().getString(R.string.participant_id), KEY.PARTICIPANT_ID)
                };
            }

            @Override
            public String searchHint() {
                return context().getStringResource(R.string.str_search_hint);
            }
        };
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
        viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        setupViews(view);
        return view;
    }

    protected void processViewConfigurations() {
        ViewConfiguration viewConfiguration = TbrApplication.getInstance().getConfigurableViewsHelper().getViewConfiguration(getViewConfigurationIdentifier());
        if (viewConfiguration == null)
            return;
        RegisterConfiguration config = (RegisterConfiguration) viewConfiguration.getMetadata();
        if (config.getSearchBarText() != null && getView() != null)
            ((EditText) getView().findViewById(R.id.edt_search)).setHint(config.getSearchBarText());
        visibleColumns = TbrApplication.getInstance().getConfigurableViewsHelper().getRegisterActiveColumns(getViewConfigurationIdentifier());

    }

    private PopupMenu initializePopup(View view){
        PopupMenu popup = new PopupMenu(getActivity(), view);
        popup.inflate(R.menu.menu_register_result);
        popup.setOnMenuItemClickListener(resultMenuListener);
        MenuItem item = popup.getMenu().getItem(0);
        String patientName = patient.getColumnmaps().get(KEY.FIRST_NAME) + SPACE + patient.getColumnmaps().get(KEY.LAST_NAME);
        item.setTitle(item.getTitle() + SPACE + patientName);
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, item.getTitle().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        item.setTitle(s);
        return popup;
    }

    public void showResultMenu(View view) {
        PopupMenu popup = initializePopup(view);
        popup.show();
    }

    public void showResultMenu(View view, BaseRegisterActivity activity){
        String string = "";
        if(activity instanceof PresumptivePatientRegisterActivity)
            string = "presumptive";
        else if(activity instanceof PositivePatientRegisterActivity)
            string = "positive";
        else if(activity instanceof InTreatmentPatientRegisterActivity)
            string = "intreatment";
        String jsonString = TbrApplication.getInstance().getConfigurableViewsRepository().getConfigurableViewJson(Constants.CONFIGURATION.TEST_RESULTS);
        ViewConfiguration testResultsConfig = jsonString == null ? null : TbrApplication.getJsonSpecHelper().getConfigurableView(jsonString);
        if(testResultsConfig != null){
            TestResultsConfiguration trc = (TestResultsConfiguration) testResultsConfig.getMetadata();
            LinkedTreeMap map = (LinkedTreeMap) trc.getResultsConfig();
            LinkedTreeMap map1 = (LinkedTreeMap) map.get(string);
            PopupMenu popup = initializePopup(view);
            for(int i=1; i < popup.getMenu().size(); i++){
                try {
                    if (!(boolean) map1.get(popup.getMenu().getItem(i).getTitle().toString()))
                        popup.getMenu().getItem(i).setVisible(false);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            popup.show();
        }
        else
            showResultMenu(view);
    }

    protected void updateSearchView() {
        if (getSearchView() != null) {
            getSearchView().removeTextChangedListener(textWatcher);
            getSearchView().addTextChangedListener(textWatcher);
        }
    }

    protected void filter(String filterString, String joinTableString, String mainConditionString) {
        filters = filterString;
        joinTable = joinTableString;
        mainCondition = mainConditionString;
        getSearchCancelView().setVisibility(isEmpty(filterString) ? INVISIBLE : VISIBLE);
        CountExecute();
        filterandSortExecute();
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        clientsView.setVisibility(VISIBLE);
        clientsProgressView.setVisibility(INVISIBLE);
        view.findViewById(R.id.sorted_by_bar).setVisibility(GONE);
        processViewConfigurations();
        initializeQueriesCustom();
        updateSearchView();
        populateClientListHeaderView(view);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            initializeQueriesCustom();
//            initializeQueries();
        }
        updateSearchView();
        processViewConfigurations();
    }

    /******************************************************
     * Customizing queries for nutrition app
     ******************************************************/
    protected void initializeQueriesCustom(){
        String tableName = TbrConstants.PATIENT_TABLE_NAME;
        PatientRegisterProvider hhscp = new PatientRegisterProvider(getActivity(), visibleColumns, registerActionHandler, TbrApplication.getInstance().getResultsRepository(), TbrApplication.getInstance().getContext().detailsRepository());
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, hhscp, context().commonrepository(tableName));
        clientsView.setAdapter(clientAdapter);

        setTablename(tableName);

        String sql = "Select * from event";
        Cursor c = this.commonRepository().rawCustomQueryForAdapter(sql);
        ArrayList<HashMap<String, String>> maplist = new ArrayList<HashMap<String, String>>();
        if(c.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < c.getColumnCount(); i++) {
                    map.put(c.getColumnName(i), c.getString(i));
                }

                maplist.add(map);
            } while (c.moveToNext());
        }
        SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
        countQueryBuilder.setSelectquery("Select COUNT(*) from client where 1=1");
        countSelect = countQueryBuilder.getSelectquery();
//        mainCondition = getMainCondition();
//        countSelect = countQueryBuilder.mainCondition(mainCondition);
        filters = "and 1=1";
        super.CountExecute();

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        String[] columns = new String[]{
                tableName + ".relationalid",
                tableName + "." + KEY.LAST_INTERACTED_WITH,
                tableName + "." + KEY.FIRST_ENCOUNTER,
                tableName + "." + KEY.BASE_ENTITY_ID,
                tableName + "." + KEY.FIRST_NAME,
                tableName + "." + KEY.LAST_NAME,
                tableName + "." + KEY.PARTICIPANT_ID,
                tableName + "." + KEY.PROGRAM_ID,
                tableName + "." + KEY.GENDER,
                tableName + "." + KEY.DOB};
        String[] allColumns = ArrayUtils.addAll(columns, getAdditionalColumns(tableName));
//        mainSelect = queryBUilder.SelectInitiateMainTable(tableName, allColumns);
        mainSelect = "Select rowid _id, '0' as relationalid,* from client where 1=1";

        filters = "and 1=1";

        currentlimit = 20;
        currentoffset = 0;

        super.filterandSortInInitializeQueries();

        refresh();
    }

    protected void initializeQueries() {
        String tableName = TbrConstants.PATIENT_TABLE_NAME;

        PatientRegisterProvider hhscp = new PatientRegisterProvider(getActivity(), visibleColumns, registerActionHandler, TbrApplication.getInstance().getResultsRepository(), TbrApplication.getInstance().getContext().detailsRepository());
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, hhscp, context().commonrepository(tableName));
        clientsView.setAdapter(clientAdapter);

        setTablename(tableName);
        SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
        countQueryBuilder.SelectInitiateMainTableCounts(tableName);
        mainCondition = getMainCondition();
        countSelect = countQueryBuilder.mainCondition(mainCondition);
        super.CountExecute();

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        String[] columns = new String[]{
                tableName + ".relationalid",
                tableName + "." + KEY.LAST_INTERACTED_WITH,
                tableName + "." + KEY.FIRST_ENCOUNTER,
                tableName + "." + KEY.BASE_ENTITY_ID,
                tableName + "." + KEY.FIRST_NAME,
                tableName + "." + KEY.LAST_NAME,
                tableName + "." + KEY.PARTICIPANT_ID,
                tableName + "." + KEY.PROGRAM_ID,
                tableName + "." + KEY.GENDER,
                tableName + "." + KEY.DOB};
        String[] allColumns = ArrayUtils.addAll(columns, getAdditionalColumns(tableName));
        queryBUilder.SelectInitiateMainTable(tableName, allColumns);
        mainSelect = queryBUilder.mainCondition(mainCondition);
        Sortqueries = ((CursorSortOption) getDefaultOptionsProvider().sortOption()).sort();

        currentlimit = 20;
        currentoffset = 0;

        super.filterandSortInInitializeQueries();

        refresh();

    }

    protected abstract void populateClientListHeaderView(View view);

    protected void populateClientListHeaderView(View view, View headerLayout, String viewConfigurationIdentifier) {

        LinearLayout clientsHeaderLayout = (LinearLayout) view.findViewById(org.smartregister.R.id.clients_header_layout);
        clientsHeaderLayout.setVisibility(GONE);

        ConfigurableViewsHelper helper = TbrApplication.getInstance().getConfigurableViewsHelper();
        if (helper.isJsonViewsEnabled()) {
            ViewConfiguration viewConfiguration = helper.getViewConfiguration(viewConfigurationIdentifier);
            ViewConfiguration commonConfiguration = helper.getViewConfiguration(COMMON_REGISTER_HEADER);
            if (viewConfiguration != null)
                headerLayout = helper.inflateDynamicView(viewConfiguration, commonConfiguration, headerLayout, R.id.register_headers, true);
        }
        if (!visibleColumns.isEmpty()) {
            Map<String, Integer> mapping = new HashMap();
            mapping.put(PATIENT, R.id.patient_header);
            mapping.put(RESULTS, R.id.results_header);
            mapping.put(DIAGNOSE, R.id.diagnose_header);
            mapping.put(XPERT_RESULTS, R.id.xpert_results_header);
            mapping.put(SMEAR_RESULTS, R.id.smr_results_header);
            mapping.put(TREAT, R.id.treat_header);
            mapping.put(DIAGNOSIS, R.id.diagnosis_header);
            mapping.put(INTREATMENT_RESULTS, R.id.intreatment_results_header);
            mapping.put(FOLLOWUP, R.id.followup_header);
            mapping.put(FOLLOWUP_SCHEDULE, R.id.followup_schedule_header);
            mapping.put(TREATMENT, R.id.treatment_header);
            mapping.put(BASELINE, R.id.baseline_header);
            mapping.put(SMEAR_SCHEDULE, R.id.smr_schedule_header);
            mapping.put("next_visit_date", R.id.next_visit_header);
            helper.processRegisterColumns(mapping, headerLayout, visibleColumns, R.id.register_headers);
        }

        clientsView.addHeaderView(headerLayout);
        clientsView.setEmptyView(getActivity().findViewById(R.id.empty_view));

    }

    protected final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(final CharSequence cs, int start, int before, int count) {
            if((customMainCondition!=null && !customMainCondition.equals("")) || (Sortqueries != null && !Sortqueries.equals(""))){
                filter(" AND (ec_patient.id like '%"+cs.toString()+"%' or ec_patient.first_name like '%"+cs.toString()+"%' or ec_patient.last_name like '%"+cs.toString()+"%')","",customMainCondition);
            }
            else
                filter(cs.toString(), "", getMainCondition());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {//Implement Abstract Method
    }

    @Override
    protected void startRegistration() {//Implement Abstract Method
    }

    @Override
    protected void onCreation() {//Implement Abstract Method
    }

    protected abstract String getMainCondition();


    protected abstract String[] getAdditionalColumns(String tableName);

    protected String getViewConfigurationIdentifier() {
        return viewConfigurationIdentifier;
    }

    private void goToPatientDetailActivity(String viewConfigurationIdentifier) {
        Intent intent = null;
        switch (viewConfigurationIdentifier) {
            case TbrConstants.VIEW_CONFIGS.PRESUMPTIVE_REGISTER:
                intent = new Intent(getActivity(), PresumptivePatientDetailActivity.class);
                break;
            case TbrConstants.VIEW_CONFIGS.POSITIVE_REGISTER:
                intent = new Intent(getActivity(), PositivePatientDetailActivity.class);
                break;
            case TbrConstants.VIEW_CONFIGS.INTREATMENT_REGISTER:
                intent = new Intent(getActivity(), InTreatmentPatientDetailActivity.class);
                break;
            default:
                break;

        }

        intent.putExtra(Constants.INTENT_KEY.REGISTER_TITLE, getActivity().getIntent().getStringExtra(TOOLBAR_TITLE));
        intent.putExtra(Constants.INTENT_KEY.PATIENT_DETAIL_MAP, (HashMap) patient.getDetails());
        intent.putExtra(Constants.KEY.TBREACH_ID, patient.getDetails().get(Constants.KEY.TBREACH_ID));
        startActivity(intent);

    }

    class ResultMenuListener implements PopupMenu.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            BaseRegisterActivity registerActivity = (BaseRegisterActivity) getActivity();
            switch (item.getItemId()) {
                case R.id.result_gene_xpert:
                    registerActivity.startFormActivity(GENE_XPERT, patient.getDetails().get("_id"), formOverridesHelper.getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_smear:
                    registerActivity.startFormActivity(SMEAR, patient.getDetails().get("_id"), formOverridesHelper.getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_chest_xray:
                    registerActivity.startFormActivity(CHEST_XRAY, patient.getDetails().get("_id"), formOverridesHelper.getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_culture:
                    registerActivity.startFormActivity(CULTURE, patient.getDetails().get("_id"), formOverridesHelper.getFieldOverrides().getJSONString());
                    return true;
                default:
                    return false;
            }
        }
    }

    class RegisterActionHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            BaseRegisterActivity registerActivity = (BaseRegisterActivity) getActivity();
            if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
                patient = (CommonPersonObjectClient) view.getTag();
                formOverridesHelper = new FormOverridesHelper(patient.getDetails());
            }
            switch (view.getId()) {
                case R.id.result_lnk:
                case R.id.intreatment_lnk:
                    showResultMenu(view, (BaseRegisterActivity) getActivity());
                    break;
                case R.id.diagnose_lnk:
                    registerActivity.startFormActivity(DIAGNOSIS, patient.getDetails().get(Constants.KEY._ID), formOverridesHelper.getFieldOverrides().getJSONString());
                    break;
                case R.id.xpert_result_lnk:
                    registerActivity.startFormActivity(GENE_XPERT, patient.getDetails().get(Constants.KEY._ID), formOverridesHelper.getFieldOverrides().getJSONString());
                    break;
                case R.id.smr_schedule:
                case R.id.smr_result_lnk:
                    registerActivity.startFormActivity(SMEAR, patient.getDetails().get(Constants.KEY._ID), formOverridesHelper.getFieldOverrides().getJSONString());
                    break;
                case R.id.patient_column:
                    goToPatientDetailActivity(getViewConfigurationIdentifier());
                    break;
                case R.id.treat_lnk:
                    registerActivity.startFormActivity(TREATMENT_INITIATION, patient.getDetails().get(Constants.KEY._ID), formOverridesHelper.getTreatmentFieldOverrides().getJSONString());
                    break;
                case R.id.followup_lnk:
                case R.id.followup:
                    registerActivity.startFormActivity(FOLLOWUP_VISIT, patient.getDetails().get(Constants.KEY._ID), formOverridesHelper.getFollowUpFieldOverrides().getJSONString());
                    break;
                case R.id.result_details:
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.layout_dialog_show_results);
                    TextView textView = (TextView) dialog.findViewById(R.id.tv_dialog_results);
                    textView.append((SpannableStringBuilder)view.getTag());
                    TextView closeBtn = (TextView) dialog.findViewById(R.id.dialog_show_results_button_close);
                    closeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    break;
                case R.id.child_followup:
                    try {
//                        Uri uri = Uri.parse("https://oppia-pakistan.opendeliver.org/view?digest=4f335de353b9c3d3c9a59c4705dbdec4384");
                        Uri uri = Uri.parse("https://oppia-pakistan.opendeliver.org/view?digest=4f335de353b9c3d3c9a59c4705dbdec4384");

                        final Intent intentDeviceTest = new Intent("org.digitalcampus.oppia.activity.ViewDigestActivity");
                        intentDeviceTest.setData(uri);
//                        intentDeviceTest.setComponent(new ComponentName("org.digitalcampus.oppia.activity","org.digitalcampus.oppia.activity.ViewDigestActivity"));
                        startActivity(intentDeviceTest);

                        List<ResolveInfo> list =
                                getActivity().getPackageManager().queryIntentActivities(intentDeviceTest,
                                        getActivity().getPackageManager().MATCH_DEFAULT_ONLY);
//                        startActivity(intentDeviceTest);

                      /*  Intent intent = new Intent("org.digitalcampus.oppia.activity.ViewDigestActivity");
                        intent.setData(uri);
                        startActivity(intent);*/

//                        Intent intent = new Intent("org.digitalcampus.oppia.activity.ViewDigestActivity");
//                        intent.setAction(Intent.ACTION_VIEW);
//                        intent.setData(uri);
//                        intent.setComponent(new ComponentName("org.digitalcampus.oppia.activity","org.digitalcampus.oppia.activity.ViewDigestActivity"));
//                        intent.putExtra("digest","7bcb63672b3d18df3bc40d6441de39fb404");
                        startActivity(intentDeviceTest);
                        break;

//                        registerActivity.startFormActivity(KUNJUNGAN_GIZI, new JSONObject(patient.getDetails().get("json")).getString("baseEntityId"), formOverridesHelper.getChildFollowupFieldOverrides().getJSONString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void filterAndSortRegisterContent(List<FilterEnum> filterResults, List<FilterEnum> filterOtherResults, String sortOption){
 /*     Cursor cursor = super.commonRepository().rawCustomQueryForAdapter(query);*/

        String sql = "Select * from client";
        Cursor c = this.commonRepository().rawCustomQueryForAdapter(sql);
        ArrayList<HashMap<String, String>> maplist = new ArrayList<HashMap<String, String>>();
        if(c.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < c.getColumnCount(); i++) {
                    map.put(c.getColumnName(i), c.getString(i));
                }

                maplist.add(map);
            } while (c.moveToNext());
        }
        prepareAndShowSnackBar(getView(),filterResults,filterOtherResults,sortOption);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, -28);
        Date dateBefore30Days = cal.getTime();
        System.out.print(dateBefore30Days);

        String tableName = TbrConstants.PATIENT_TABLE_NAME;
        SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
        countQueryBuilder.setSelectquery("Select COUNT(DISTINCT ec_patient.base_entity_id) from ec_patient");
        mainCondition = getMainCondition();
        countSelect = countQueryBuilder.mainCondition(mainCondition);
        countSelect = countQueryBuilder.addCondition(getOtherCndition(filterOtherResults));
        if(!filterResults.isEmpty()){
            countSelect = countQueryBuilder.addCondition(" AND ec_patient.base_entity_id in (Select base_entity_id from (SELECT * FROM results res where res.base_entity_id = ec_patient.base_entity_id GROUP BY type HAVING MAX(date||created_at)) AS 'results' where 1=1  " + getSubQueryCondition(filterResults) + ")");
        }
        filters = "and 1=1";
        super.CountExecute();

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        String[] columns = new String[]{
                tableName + ".relationalid",
                tableName + "." + KEY.LAST_INTERACTED_WITH,
                tableName + "." + KEY.FIRST_ENCOUNTER,
                tableName + "." + KEY.BASE_ENTITY_ID,
                tableName + "." + KEY.FIRST_NAME,
                tableName + "." + KEY.LAST_NAME,
                tableName + "." + KEY.PARTICIPANT_ID,
                tableName + "." + KEY.PROGRAM_ID,
                tableName + "." + KEY.GENDER,
                tableName + "." + KEY.DOB};
        String[] allColumns = ArrayUtils.addAll(columns, getAdditionalColumns(tableName));
        mainSelect = queryBUilder.SelectInitiateMainTable(tableName, allColumns);
        customMainCondition = " WHERE ";
        customMainCondition += mainCondition + " " + getOtherCndition(filterOtherResults);
        customMainCondition += getOtherCndition(filterOtherResults);
        if(!filterResults.isEmpty()){
            customMainCondition += getSubQueryClause(filterResults);
        }
        mainSelect = queryBUilder.addCondition(customMainCondition);
        filters = "and 1=1";
        Sortqueries = getSortQuery(sortOption);

        currentlimit = 20;
        currentoffset = 0;

        super.filterandSortInInitializeQueries();

        refresh();
    }

    private String getSubQueryCondition(List<FilterEnum> filterResults){
        StringBuilder sb = new StringBuilder();
        if(!filterResults.isEmpty()) {
            sb.append(" AND (");
            for (FilterEnum conditonEnum : filterResults) {
                sb.append("(");
                sb.append(conditonEnum.getCondition()).append(") ");
                sb.append(" OR ");
            }
            sb.delete(sb.length()-3,sb.length()).append(") ");
        }
        return sb.toString();
    }

    private String getOtherCndition(List<FilterEnum> filterOtherResults) {
        StringBuilder sb = new StringBuilder();
        if(!filterOtherResults.isEmpty()) {
            sb.append(" AND (");
            for (FilterEnum conditonEnum : filterOtherResults) {
                sb.append("(");
                sb.append(conditonEnum.getCondition()).append(") ");
                sb.append(" OR ");
            }
            sb.delete(sb.length()-3,sb.length()).append(") ");
        }
        return sb.toString();
    }

    private String getSortQuery(String sort){
        if(sort!=null && !sort.isEmpty()) {
            if (sort.equalsIgnoreCase("name (A-Z)"))
                return "first_name asc";
            else return "last_interacted_with desc";
        }
        else return "";
    }

    private String getSubQueryClause(List<FilterEnum> filterResults){
        return " AND base_entity_id in (Select base_entity_id from (SELECT * FROM results res where res.base_entity_id = ec_patient.base_entity_id GROUP BY type HAVING MAX(date||created_at)) AS 'results' where 1=1  " + getSubQueryCondition(filterResults) + ")";
    }

    public void prepareAndShowSnackBar(View view,List<FilterEnum> filterResults, List<FilterEnum> filterOtherResults, String sortOption){
        StringBuilder toastString = new StringBuilder();
        getToastString(filterOtherResults, toastString);
        getToastString(filterResults, toastString);
        if(toastString != null && toastString.length() > 0)
            toastString.replace(toastString.length()-2,toastString.length(),"");
        appendSortStringToToast(sortOption,toastString);
        dismissSnackbar(snackbar);
        showSnackBar(toastString,view);
    }

    private void showSnackBar(StringBuilder toastString, View view){
        if(toastString != null && !toastString.toString().isEmpty()) {
            snackbar = Snackbar.make(view,toastString.toString(),Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }

    }

    private void getToastString(List<FilterEnum> resultsList, StringBuilder toastString){
        if(resultsList != null && !resultsList.isEmpty()){
            toastString.append("Filters: ");
            for(FilterEnum fe : resultsList){
                toastString.append(fe.getFilterString()).append(", ");
            }
        }
    }
    private void appendSortStringToToast(String sortOption, StringBuilder toastString){
        if(sortOption != null && !sortOption.isEmpty()){
            toastString.append(toastString != null ? "\n" : "");
            toastString.append("Sort: ").append(sortOption);
        }
    }

    private void dismissSnackbar(Snackbar snackbar){
        if(snackbar != null)
            snackbar.dismiss();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String sortOption="";
        if(isEmpty(Sortqueries)|| Sortqueries.equalsIgnoreCase("first_name asc"))
            sortOption = "Name (A-Z)";
        else if(Sortqueries.equalsIgnoreCase("last_interacted_with desc"))
            sortOption = "Last updated";
        prepareAndShowSnackBar(getView(),null,null,sortOption);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public abstract String getAggregateCondition(boolean b);

}
