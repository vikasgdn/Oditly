package com.oditly.audit.inspection.model.audit.AddAttachment;

import java.util.ArrayList;

public class AddAttachmentRootObject {

    boolean error;
    ArrayList<AddAttachmentInfo> data;
    String message = "";

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public ArrayList<AddAttachmentInfo> getData() {
        return data;
    }

    public void setData(ArrayList<AddAttachmentInfo> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
