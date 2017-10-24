package org.smartregister.tbr.model;

import android.util.Log;

import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.jsonspec.model.View;

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

    public Register(View view, int totalPatients, int totalPatientsWithDueOverdue) {

        Map<String, String> en = getLanguageFile("en");
        String label = en != null && en.size() > 0 ? en.get(view.getIdentifier()) : view.getLabel();

        this.title = label != null && !label.isEmpty() ? label : view.getLabel();
        this.titleToken = view.getIdentifier();
        this.totalPatients = totalPatients;
        this.totalPatientsWithDueOverdue = totalPatientsWithDueOverdue;
        this.position = view.getResidence().getPosition();

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

    private Map<String, String> getLanguageFile(String languageCode) {
        try {
            return TbrApplication.getJsonSpecHelper().getLanguageFile(languageCode);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }
}
