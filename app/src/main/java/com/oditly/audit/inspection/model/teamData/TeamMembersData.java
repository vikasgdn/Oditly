package com.oditly.audit.inspection.model.teamData;


import java.util.ArrayList;

public class TeamMembersData {
    private int team_id = 0;
    private int  team_users_count=0;
    private String team_name = "";
    private String created_on = "";
    private String custom_role_name = "";
    private ArrayList<TeamMembers> users;

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public int getTeam_users_count() {
        return team_users_count;
    }

    public void setTeam_users_count(int team_users_count) {
        this.team_users_count = team_users_count;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getCustom_role_name() {
        return custom_role_name;
    }

    public void setCustom_role_name(String custom_role_name) {
        this.custom_role_name = custom_role_name;
    }

    public ArrayList<TeamMembers> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<TeamMembers> users) {
        this.users = users;
    }

//"team_id":6,"team_name":"Test","created_on":"2020-06-23 11:43:36","team_users_count":3,"users"
  //  {"team_id":6,"user_id":6,"name":"CTO","email":"cto@oditly.com","custom_role_id":9,"custom_role_name":"CTO"}
}
