package com.oditly.audit.inspection.model.audit.createaudit;


public class ActionFilterRootObject {

    private boolean error;
    private ActionFilterData data;
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


    public ActionFilterData getData() {
        return data;
    }

    public void setData(ActionFilterData data) {
        this.data = data;
    }
}
