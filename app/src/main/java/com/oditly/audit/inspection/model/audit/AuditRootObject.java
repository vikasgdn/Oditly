package com.oditly.audit.inspection.model.audit;

import java.util.ArrayList;

public class AuditRootObject {

    boolean error;
    ArrayList<AuditInfo> data;
    String message = "";

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
}
