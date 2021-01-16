package com.oditly.audit.inspection.model.audit;


public class AuditInfo {


    private int overdue_days=0;
    private int audit_id = 0;
    private String audit_name = "";
    private String score_text = "";
    private String creator_name="";
    private String audit_due_date = "";
    private int brand_std_status = 0;
    private int audit_status = 0;
    private String audit_date = "";
    private String auditor_fname = "";
    private String auditor_lname = "";
    private String location_title = "";
    private String  questionnaire_title;
    private String brand_name = "";
    private int audit_type_id = 0;
    private String audit_type = "";
    private String is_passed = "";
    private String start_date;
    private String end_date;
    private float completion_percent;
    private String completed_late_text;


    public int getAudit_id() {
        return audit_id;
    }

    public void setAudit_id(int audit_id) {
        this.audit_id = audit_id;
    }

    public String getAudit_name() {
        return audit_name;
    }

    public String getAudit_due_date() {
        return audit_due_date;
    }

    public int getBrand_std_status() {
        return brand_std_status;
    }


    public int getAudit_status() {
        return audit_status;
    }


    public String getAudit_date() {
        return audit_date;
    }

    public String getAuditor_fname() {
        return auditor_fname;
    }


    public String getAuditor_lname() {
        return auditor_lname;
    }

    public String getLocation_title() {
        return location_title;
    }



    public String getBrand_name() {
        return brand_name;
    }

    public int getAudit_type_id() {
        return audit_type_id;
    }

    public void setAudit_type_id(int audit_type_id) {
        this.audit_type_id = audit_type_id;
    }

    public String getAudit_type() {
        return audit_type;
    }

    public int getOverdue_days() {
        return overdue_days;
    }


    public String getScore_text() {
        return score_text;
    }


    public String getQuestionnaire_title() {
        return questionnaire_title;
    }

    public String getCreator_name() {
        return creator_name;
    }


    public String getIs_passed() {
        return is_passed;
    }


    public String getStart_date() {
        return start_date;
    }


    public String getEnd_date() {
        return end_date;
    }

    public float getCompletion_percent() {
        return completion_percent;
    }

    public String getCompleted_late_key() {
        return completed_late_text;
    }

    public void setCompleted_late_key(String completed_late_key) {
        this.completed_late_text = completed_late_key;
    }
}
