package org.smartregister.tbr.model;

/**
 * Created by ndegwamartin on 09/01/2018.
 */

public class RegisterCount {
    public static String REGISTER_COUNT = "RegisterCount";
    public static String OVERDUE_COUNT = "OverdueCount";
    private int total;
    private int totalOverdue;

    public RegisterCount(int total, int totalOverdue) {
        this.total = total;
        this.totalOverdue = totalOverdue;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalOverdue() {
        return totalOverdue;
    }

    public void setTotalOverdue(int totalOverdue) {
        this.totalOverdue = totalOverdue;
    }

}
