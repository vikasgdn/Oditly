package com.oditly.audit.inspection.model.audit.createaudit;


import com.oditly.audit.inspection.model.filterData.FilterData;

public class AuditFilterRootObject {

    private boolean error;
    private AuditFilterData data;
    private String message = "";

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public AuditFilterData getData() {
        return data;
    }

    public void setData(AuditFilterData data) {
        this.data = data;
    }
}
