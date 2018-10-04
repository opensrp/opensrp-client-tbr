package org.smartregister.nutrition.fragment;

import android.view.View;

import org.smartregister.nutrition.R;
import org.smartregister.nutrition.helper.DBQueryHelper;

import static util.TbrConstants.KEY.DIAGNOSIS_DATE;
import static util.TbrConstants.VIEW_CONFIGS.POSITIVE_REGISTER_HEADER;

/**
 * Created by samuelgithengi on 11/27/17.
 */

public class PositivePatientRegisterFragment extends BaseRegisterFragment {

    @Override
    protected void populateClientListHeaderView(View view) {
        View headerLayout = getLayoutInflater(null).inflate(R.layout.register_positive_list_header, null);
        populateClientListHeaderView(view, headerLayout, POSITIVE_REGISTER_HEADER);
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getPositivePatientRegisterCondition();
    }

    @Override
    protected String[] getAdditionalColumns(String tableName) {
        return new String[]{
                tableName + "." + DIAGNOSIS_DATE};
    }

    @Override
    public String getAggregateCondition(boolean isEmpty) {
        if(!isEmpty)
            return " GROUP BY Type HAVING MAX(date||created_at)";
        else return "";
    }
}
