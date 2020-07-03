package com.oditly.audit.inspection.model.audit.BrandStandard;

public class BrandStandardRootObject {

    boolean error;
    BrandStandardInfo data;
    String message = "";

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public BrandStandardInfo getData() {
        return data;
    }

    public void setData(BrandStandardInfo data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
