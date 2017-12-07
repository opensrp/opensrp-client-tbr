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
        populateClientListHeaderView(view, R.layout.register_presumptive_list_header, PRESUMPTIVE_REGISTER_HEADER);
    }

    @Override
    protected String getMainCondition() {
        return " presumptive =\"yes\" AND confirmed_tb IS NULL";
    }

}
