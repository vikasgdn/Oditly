package com.oditly.audit.inspection.model.teamData;




public class TeamInfo {
    private int user_id = 0;
    private int  parent_user_id=0;
    private int  role_id=0;
    private int  custom_role_id=0;

    private String name = "";

    private String username="";
    private String email = "";
    private String phone = "";
    private String dob = "";
    private String gender= "";
    private String address= "";
    private int user_status = 0;
    private String created_on = "";
    private String role_name = "";




    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getParent_user_id() {
        return parent_user_id;
    }

    public void setParent_user_id(int parent_user_id) {
        this.parent_user_id = parent_user_id;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public int getCustom_role_id() {
        return custom_role_id;
    }

    public void setCustom_role_id(int custom_role_id) {
        this.custom_role_id = custom_role_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getUser_status() {
        return user_status;
    }

    public void setUser_status(int user_status) {
        this.user_status = user_status;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }
}
