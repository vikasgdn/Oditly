package com.oditly.audit.inspection.model.filterData;

public class AuditorBean {
    private int auditor_id=0;
    private String auditor_name;
    private String auditor_email;


    public int getAuditor_id() {
        return auditor_id;
    }

    public void setAuditor_id(int auditor_id) {
        this.auditor_id = auditor_id;
    }

    public String getAuditor_name() {
        return auditor_name;
    }

    public void setAuditor_name(String auditor_name) {
        this.auditor_name = auditor_name;
    }

    public String getAuditor_email() {
        return auditor_email;
    }

    public void setAuditor_email(String auditor_email) {
        this.auditor_email = auditor_email;
    }
}
