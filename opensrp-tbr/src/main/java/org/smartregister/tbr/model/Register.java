package org.smartregister.tbr.model;

import android.content.Context;

import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.tbr.util.Utils;

import java.util.Map;

/**
 * Created by ndegwamartin on 11/10/2017.
 */

public class Register {

    public static final String PRESUMPTIVE_PATIENTS = "presumptive_patients";
    public static final String POSITIVE_PATIENTS = "positive_patients";
    public static final String IN_TREATMENT_PATIENTS = "in_treatment_patients";
    public static final String TAG = Register.class.getCanonicalName();

    private String title;
    private String titleToken;
    private int totalPatients;
    private int totalPatientsWithDueOverdue;
    private int position;

    public Register(Context context, View view, RegisterCount registerCount) {
        ViewConfiguration config = TbrApplication.getJsonSpecHelper().getLanguage(Utils.getLanguage());
        String label = getRegisterLabel(context, view, config);
        this.title = label != null && !label.isEmpty() ? label : context.getString(Utils.getTokenStringResourceId(context, view.getLabel()));
        this.titleToken = view.getIdentifier();
        if (registerCount != null) {
            this.totalPatients = registerCount.getTotal();
            this.totalPatientsWithDueOverdue = registerCount.getTotalOverdue();
        }
        this.position = view.getResidence().getPosition();

    }

    private String getRegisterLabel(Context context, View view, ViewConfiguration config) {

        Map<String, String> langMap = config == null ? null : config.getLabels();
        return langMap != null && !langMap.isEmpty() ? langMap.get(view.getIdentifier()) : getStringResource(context, view);
    }

    public String getStringResource(Context context, View view) {
        return context.getString(Utils.getTokenStringResourceId(context, view.getLabel()));
    }

    public String getTitleToken() {
        return titleToken;
    }

    public void setTitleToken(String titleToken) {
        this.titleToken = titleToken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(int totalPatients) {
        this.totalPatients = totalPatients;
    }

    public int getTotalPatientsWithDueOverdue() {
        return totalPatientsWithDueOverdue;
    }

    public void setTotalPatientsWithDueOverdue(int totalPatientsWithDueOverdue) {
        this.totalPatientsWithDueOverdue = totalPatientsWithDueOverdue;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


}
