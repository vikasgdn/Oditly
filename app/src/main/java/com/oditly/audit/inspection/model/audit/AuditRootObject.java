package com.oditly.audit.inspection.model.audit;

import java.util.ArrayList;

public class AuditRootObject {

    boolean error;
    ArrayList<AuditInfo> data;
    String message = "";

    private int rows;
     private int limit;
    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public ArrayList<AuditInfo> getData() {
        return data;
    }

    public void setData(ArrayList<AuditInfo> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
