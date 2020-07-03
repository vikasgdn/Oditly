package com.oditly.audit.inspection.model.teamData;


import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardInfo;
import com.oditly.audit.inspection.model.signin.SignInModel;

import java.util.ArrayList;

public class TeamRootObject {

    boolean error;
    TeamData data;
    String message = "";

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public TeamData getData() {
        return data;
    }

    public void setData(TeamData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
