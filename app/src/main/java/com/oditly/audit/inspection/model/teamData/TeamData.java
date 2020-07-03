package com.oditly.audit.inspection.model.teamData;


import java.util.ArrayList;

public class TeamData {

    private ArrayList<TeamInfo> users;
    private ArrayList<TeamInfo> team;

    public ArrayList<TeamInfo> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<TeamInfo> users) {
        this.users = users;
    }

    public ArrayList<TeamInfo> getTeam() {
        return team;
    }

    public void setTeam(ArrayList<TeamInfo> team) {
        this.team = team;
    }
}
