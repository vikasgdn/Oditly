package com.oditly.audit.inspection.model.teamData;


import java.util.List;

public class TeamMemberRootObject {

    boolean error;
   private List<TeamMembersData> data;
    String message = "";

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


    public List<TeamMembersData> getData() {
        return data;
    }

    public void setData(List<TeamMembersData> data) {
        this.data = data;
    }
}
