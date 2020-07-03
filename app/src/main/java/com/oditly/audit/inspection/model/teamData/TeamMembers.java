package com.oditly.audit.inspection.model.teamData;




public class TeamMembers {
    private int user_id = 0;
    private int  team_id=0;
    private int  custom_role_id=0;
    private String name = "";
    private String email = "";
    private String custom_role_name = "";

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustom_role_name() {
        return custom_role_name;
    }

    public void setCustom_role_name(String custom_role_name) {
        this.custom_role_name = custom_role_name;
    }


    //  {"team_id":6,"user_id":6,"name":"CTO","email":"cto@oditly.com","custom_role_id":9,"custom_role_name":"CTO"}
}
