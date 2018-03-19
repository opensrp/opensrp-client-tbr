package org.smartregister.tbr.fragment;


import android.database.Cursor;
import android.util.Log;
import android.view.View;

import org.apache.commons.lang3.ArrayUtils;
import org.smartregister.cursoradapter.CursorSortOption;
import org.smartregister.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.tbr.R;
import org.smartregister.tbr.helper.DBQueryHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.TbrConstants;

import static util.TbrConstants.VIEW_CONFIGS.PRESUMPTIVE_REGISTER_HEADER;

/**
 * Created by samuelgithengi on 11/6/17.
 */

public class PresumptivePatientRegisterFragment extends BaseRegisterFragment {


    @Override
    protected void populateClientListHeaderView(View view) {
        View headerLayout = getLayoutInflater(null).inflate(R.layout.register_presumptive_list_header, null);
        populateClientListHeaderView(view, headerLayout, PRESUMPTIVE_REGISTER_HEADER);
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getPresumptivePatientRegisterCondition();
    }

    @Override
    protected String[] getAdditionalColumns(String tableName) {
        return new String[]{};
    }

    /*@Override
    public void testmethod(List<String> filterOptions){
     *//*   String query = "SELECT julianday('now') - julianday(first_encounter) FROM  ec_patient where" + getMainCondition();
        Cursor cursor = super.commonRepository().rawCustomQueryForAdapter(query);

        int index=0;
        Map<String,Object> map = new HashMap<String,Object>();
        if(cursor.getCount()!=0) {
            if (cursor.moveToFirst()) {
                do {
                    for(int i=0; i<cursor.getColumnCount();i++)
                    {
                        map.put(cursor.getColumnName(i), cursor.getString(i));
                    }
                } while (cursor.moveToNext());
            }
        }*//*


        String tableName = TbrConstants.PATIENT_TABLE_NAME;
        SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
        countQueryBuilder.setSelectquery("Select COUNT(DISTINCT ec_patient.base_entity_id) from ec_patient");
        countQueryBuilder.customJoin("inner join results on results.base_entity_id = ec_patient.base_entity_id");
        mainCondition = getMainCondition();
        countSelect = countQueryBuilder.mainCondition(mainCondition);
        countSelect = countQueryBuilder.addCondition(getAdditionalCondition(filterOptions));
        filters = "and 1=1";
        super.CountExecute();

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        String[] columns = new String[]{
                tableName + ".relationalid",
                tableName + "." + TbrConstants.KEY.LAST_INTERACTED_WITH,
                tableName + "." + TbrConstants.KEY.FIRST_ENCOUNTER,
                tableName + "." + TbrConstants.KEY.BASE_ENTITY_ID,
                tableName + "." + TbrConstants.KEY.FIRST_NAME,
                tableName + "." + TbrConstants.KEY.LAST_NAME,
                tableName + "." + TbrConstants.KEY.PARTICIPANT_ID,
                tableName + "." + TbrConstants.KEY.PROGRAM_ID,
                tableName + "." + TbrConstants.KEY.GENDER,
                tableName + "." + TbrConstants.KEY.DOB};
        String[] allColumns = ArrayUtils.addAll(columns, getAdditionalColumns(tableName));
        mainSelect = queryBUilder.SelectInitiateMainTable(tableName, allColumns);
        mainSelect = queryBUilder.mainCondition(mainCondition);
        mainSelect = queryBUilder.addCondition("AND ec_patient.base_entity_id in (Select base_entity_id from results where 1=1  "+ getAdditionalCondition(filterOptions) + ")");
        filters = "and 1=1";
        Sortqueries = ((CursorSortOption) getDefaultOptionsProvider().sortOption()).sort();

        currentlimit = 20;
        currentoffset = 0;

        super.filterandSortInInitializeQueries();

        refresh();
    }

    private String getAdditionalCondition(List<String> conditions){
        StringBuilder sb = new StringBuilder();
        if(!conditions.isEmpty()) {
            sb.append(" AND (");
            for (String conditon : conditions) {
                sb.append("(");
                sb.append(conditon).append(") ");
                sb.append(" OR ");
            }
            sb.delete(sb.length()-3,sb.length()).append(") ");
        }
        return sb.toString();
    }*/

    @Override
    public String getAggregateCondition(boolean b) {
        return "";
    }
}
