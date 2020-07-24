package com.oditly.audit.inspection.model.audit.createaudit;

public class AditorReviewBean
{

    private int role_id;
    private int user_id;
    private String name;

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    user_id : 3
//    parent_user_id : null
//    role_id : 200
//    custom_role_id : null
//    fname : "Test"
//    lname : "Client"
//    name : "Test Client"
}
