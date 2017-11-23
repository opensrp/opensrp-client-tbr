package org.smartregister.tbr.jsonspec;

import android.view.View;

import com.avocarrot.json2view.DynamicViewId;

/**
 * Created by samuelgithengi on 11/23/17.
 */

public class RegisterViewHolder {
    @DynamicViewId(id = "patient_header")
    public View patientHeader;

    @DynamicViewId(id = "results_header")
    public View resultsHeader;

    @DynamicViewId(id = "diagnose_header")
    public View diagnoseHeader;

    @DynamicViewId(id = "encounter_header")
    public View encounterHeader;

    @DynamicViewId(id = "xpert_results_header")
    public View xpertResultsHeader;

    @DynamicViewId(id = "register_headers")
    public View registerHeaders;

    public RegisterViewHolder() {
    }
}
