package com.oditly.audit.inspection.model.audit.DetailedSummary;


import java.util.ArrayList;

public class DetailedSummaryRootObject {

    boolean error;
    ArrayList<DetailedSummaryInfo> data;
    String message = "";

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public ArrayList<DetailedSummaryInfo> getData() {
        return data;
    }

    public void setData(ArrayList<DetailedSummaryInfo> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
