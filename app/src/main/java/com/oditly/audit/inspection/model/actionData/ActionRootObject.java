package com.oditly.audit.inspection.model.actionData;

import java.util.ArrayList;

public class ActionRootObject {

    boolean error;
    ArrayList<ActionInfo> data;
    String message = "";

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public ArrayList<ActionInfo> getData() {
        return data;
    }

    public void setData(ArrayList<ActionInfo> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
