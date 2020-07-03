package com.oditly.audit.inspection.model.teamData;

public class TeamList {

private int team_id;
private int team_users_count;
private String team_name;
private String created_on;

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


    // {"team_id":5,"team_name":"Test","created_on":"2020-06-23 11:43:07","team_users_count":2}
}
