package com.oditly.audit.inspection.model.audit.BrandStandard;


import java.util.ArrayList;

public class BrandStandardInfo {

    private int audit_type_id = 0;
    private int questionnaire_id = 0;
    private String questionnaire_title = "";
    private int brand_std_status = 0;
    private String audit_date = "";
   // private String audit_due_date = "";
    private int location_id = 0;
    private String location_title = "";

    private int auditTimer= 0;
    private int has_gallery_access = 1;
    private int audit_questionnaire_file_cnt = 0;
    ArrayList<BrandStandardSection> sections;
    private String score = "";


    public int getQuestionnaire_id() {
        return questionnaire_id;
    }

    public void setQuestionnaire_id(int questionnaire_id) {
        this.questionnaire_id = questionnaire_id;
    }

    public String getQuestionnaire_title() {
        return questionnaire_title;
    }

    public void setQuestionnaire_title(String questionnaire_title) {
        this.questionnaire_title = questionnaire_title;
    }

    public int getBrand_std_status() {
        return brand_std_status;
    }

    public void setBrand_std_status(int brand_std_status) {
        this.brand_std_status = brand_std_status;
    }

    public String getAudit_date() {
        return audit_date;
    }

    public void setAudit_date(String audit_date) {
        this.audit_date = audit_date;
    }

  /*  public String getAudit_due_date() {
        return audit_due_date;
    }

    public void setAudit_due_date(String audit_due_date) {
        this.audit_due_date = audit_due_date;
    }
*/
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

    public int getAudit_questionnaire_file_cnt() {
        return audit_questionnaire_file_cnt;
    }

    public void setAudit_questionnaire_file_cnt(int audit_questionnaire_file_cnt) {
        this.audit_questionnaire_file_cnt = audit_questionnaire_file_cnt;
    }

    public ArrayList<BrandStandardSection> getSections() {
        return sections;
    }

    public void setSections(ArrayList<BrandStandardSection> sections) {
        this.sections = sections;
    }


    public int isGalleryDisable() {
        return has_gallery_access;
    }

    public void setGalleryDisable(int galleryDisable) {
        has_gallery_access = galleryDisable;
    }

    public int getAuditTimer() {
        return auditTimer;
    }

    public void setAuditTimer(int auditTimer) {
        this.auditTimer = auditTimer;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
