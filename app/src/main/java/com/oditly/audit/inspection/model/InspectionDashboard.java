package com.oditly.audit.inspection.model;

public class InspectionDashboard {
    private int status_id;
    private String status_name;
    private int status_count;

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public int getStatus_count() {
        return status_count;
    }

    public void setStatus_count(int status_count) {
        this.status_count = status_count;
    }
    /* "status_id": 1,
             "status_name": "Pending Approval",
             "status_count": 15*/
}

