package org.smartregister.tbr.fragment;


import android.view.View;

import org.smartregister.tbr.R;

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
        return " presumptive =\"yes\" AND confirmed_tb IS NULL";
    }

<<<<<<< HEAD
=======
    @Override
    protected String[] getAdditionalColumns(String tableName) {
        return new String[]{};
    }
>>>>>>> master

}
