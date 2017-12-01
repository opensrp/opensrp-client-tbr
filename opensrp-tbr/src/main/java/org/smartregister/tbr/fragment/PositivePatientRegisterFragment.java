package org.smartregister.tbr.fragment;

import android.view.View;

import org.smartregister.tbr.R;

import static util.TbrConstants.VIEW_CONFIGS.POSITIVE_REGISTER_HEADER;

/**
 * Created by samuelgithengi on 11/27/17.
 */

public class PositivePatientRegisterFragment extends BaseRegisterFragment {

    @Override
    protected void populateClientListHeaderView(View view) {
        populateClientListHeaderView(view, R.layout.register_positive_list_header, POSITIVE_REGISTER_HEADER);
    }

    @Override
    protected String getMainCondition() {
        return " confirmed_tb = \"yes\" ";
    }

}
