package com.oditly.audit.inspection.model.actionData;

public class AssignedUserData
{
    private int assigned_user_id=0;
    private String assigned_user_name = "";
    private String assigned_user_email = "";


    public int getAssigned_user_id() {
        return assigned_user_id;
    }

    public void setAssigned_user_id(int assigned_user_id) {
        this.assigned_user_id = assigned_user_id;
    }

    public String getAssigned_user_name() {
        return assigned_user_name;
    }

    public void setAssigned_user_name(String assigned_user_name) {
        this.assigned_user_name = assigned_user_name;
    }

    public String getAssigned_user_email() {
        return assigned_user_email;
    }

    public void setAssigned_user_email(String assigned_user_email) {
        this.assigned_user_email = assigned_user_email;
    }
}
