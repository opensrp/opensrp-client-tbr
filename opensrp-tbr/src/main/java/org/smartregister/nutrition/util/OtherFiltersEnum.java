package org.smartregister.nutrition.util;

/**
 * Created by Imran-PC on 12-Mar-18.
 */

public enum OtherFiltersEnum implements FilterEnum {
    NOT_DIAGNOSED_1PLUS_WEEKS("Not Diagnosed for 1+ weeks", "julianday('now') - julianday(first_encounter) > 7"),
    NOT_STARTED_TREATMENT_1PLUS_WEEKS("Not started treatment for 1+ weeks", "julianday('now') - julianday(diagnosis_date) > 7"),
    OVERDUE_FOLLOWUP("Overdue Followup", "julianday('now') > julianday(next_visit_date)"),
    OVERDUE_SMEAR("Overdue Smear", "julianday('now') > julianday(smear_due_date)");

    private String filterString;
    private String condition;

    @Override
    public String getCondition(){
        return this.condition;
    }

    public String getFilterString(){
        return this.filterString;
    }

    OtherFiltersEnum(String filterString, String condition){
        this.filterString = filterString;
        this.condition = condition;
    }

}
