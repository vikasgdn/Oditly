package com.oditly.audit.inspection.model.filterData;


import com.oditly.audit.inspection.model.teamData.TeamInfo;

import java.util.ArrayList;

public class LocationBean {

    private  int location_id=0;
    private String location_title;

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public String getLocation_title() {
        return location_title;
    }

    public void setLocation_title(String location_title) {
        this.location_title = location_title;
    }
}
