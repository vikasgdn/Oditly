package com.oditly.audit.inspection.network.apirequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.oditly.audit.inspection.network.NetworkConstant;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class AddAdHocActionPlan {

    private String location_id;
    private String section_id;
    private String priority_id;
    private String title;
    private String planned_date;
    private String action_details;
    private String media_count;
    private String cc_emails;
    private JSONArray assigned_user_id;


    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public String getPriority_id() {
        return priority_id;
    }

    public void setPriority_id(String priority_id) {
        this.priority_id = priority_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlanned_date() {
        return planned_date;
    }

    public void setPlanned_date(String planned_date) {
        this.planned_date = planned_date;
    }

    public String getAction_details() {
        return action_details;
    }

    public void setAction_details(String action_details) {
        this.action_details = action_details;
    }

    public String getMedia_count() {
        return media_count;
    }

    public void setMedia_count(String media_count) {
        this.media_count = media_count;
    }

    public String getCc_emails() {
        return cc_emails;
    }

    public void setCc_emails(String cc_emails) {
        this.cc_emails = cc_emails;
    }

    public JSONArray getAssigned_user_id() {
        return assigned_user_id;
    }

    public void setAssigned_user_id(JSONArray assigned_user_id) {
        this.assigned_user_id = assigned_user_id;
    }


}
