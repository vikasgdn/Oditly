package com.oditly.audit.inspection.model.filterData;


import com.oditly.audit.inspection.model.teamData.TeamInfo;

import java.util.ArrayList;

public class FilterData {

    private ArrayList<AuditType> audit_types;
    private ArrayList<LocationBean> locations;
    private ArrayList<AuditorBean> auditors;
    private ArrayList<DesignationBean> custom_roles;
    private ArrayList<TemplateBean> questionnaires;

    public ArrayList<AuditType> getAudit_types() {
        return audit_types;
    }

    public void setAudit_types(ArrayList<AuditType> audit_types) {
        this.audit_types = audit_types;
    }

    public ArrayList<LocationBean> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<LocationBean> locations) {
        this.locations = locations;
    }

    public ArrayList<AuditorBean> getAuditors() {
        return auditors;
    }

    public void setAuditors(ArrayList<AuditorBean> auditors) {
        this.auditors = auditors;
    }

    public ArrayList<DesignationBean> getCustom_roles() {
        return custom_roles;
    }

    public void setCustom_roles(ArrayList<DesignationBean> custom_roles) {
        this.custom_roles = custom_roles;
    }

    public ArrayList<TemplateBean> getQuestionnaires() {
        return questionnaires;
    }

    public void setQuestionnaires(ArrayList<TemplateBean> questionnaires) {
        this.questionnaires = questionnaires;
    }
}
