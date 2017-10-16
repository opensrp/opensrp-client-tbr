package org.smartregister.tbr.model;

/**
 * Created by ndegwamartin on 11/10/2017.
 */

public class Register {

    public static final String PRESUMPTIVE_PATIENTS = "presumptive_patients";
    public static final String POSITIVE_PATIENTS = "positive_patients";
    public static final String IN_TREATMENT_PATIENTS = "in_treatment_patients";

    private String title;
    private String titleToken;
    private int totalPatients;
    private int totalPatientsWithDueOverdue;

    public Register(String title, String titleToken, int totalPatients, int totalPatientsWithDueOverdue) {
        this.title = title;
        this.titleToken = titleToken;
        this.totalPatients = totalPatients;
        this.totalPatientsWithDueOverdue = totalPatientsWithDueOverdue;

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
}
