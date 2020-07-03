package com.oditly.audit.inspection.model.signin;

public class SignInRootObject {

    boolean error;
    SignInModel data;
    String message = "";

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public SignInModel getData() {
        return data;
    }

    public void setData(SignInModel data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
