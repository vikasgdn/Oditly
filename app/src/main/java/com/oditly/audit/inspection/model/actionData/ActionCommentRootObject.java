package com.oditly.audit.inspection.model.actionData;

import java.util.ArrayList;

public class ActionCommentRootObject {

    boolean error;
    ArrayList<ActionCommentData> data;
    String message = "";

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public ArrayList<ActionCommentData> getData() {
        return data;
    }

    public void setData(ArrayList<ActionCommentData> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }




}
