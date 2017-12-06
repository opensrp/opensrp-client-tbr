package org.smartregister.tbr.fragment;

import android.view.View;

import org.smartregister.tbr.R;

import static util.TbrConstants.VIEW_CONFIGS.POSITIVE_REGISTER_HEADER;

/**
 * Created by samuelgithengi on 11/27/17.
 */

public class InTreatmentPatientRegisterFragment extends BaseRegisterFragment {

    @Override
    protected void populateClientListHeaderView(View view) {
        populateClientListHeaderView(view, R.layout.register_positive_list_header, POSITIVE_REGISTER_HEADER);
    }

    @Override
    protected String getMainCondition() {
        return " treatment_initiation_date IS NOT NULL";
    }

}
