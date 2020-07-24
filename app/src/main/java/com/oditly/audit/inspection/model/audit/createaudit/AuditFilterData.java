package com.oditly.audit.inspection.model.audit.createaudit;


import com.oditly.audit.inspection.model.filterData.AuditType;
import com.oditly.audit.inspection.model.filterData.AuditorBean;
import com.oditly.audit.inspection.model.filterData.DesignationBean;
import com.oditly.audit.inspection.model.filterData.LocationBean;
import com.oditly.audit.inspection.model.filterData.TemplateBean;

import java.util.ArrayList;

public class AuditFilterData {

    private ArrayList<AuditType> audit_types;
    private ArrayList<AditorReviewBean> reviewers;
    private ArrayList<AditorReviewBean> auditors;
    private ArrayList<DesignationBean> custom_roles;
    private ArrayList<TemplateBean> questionnaires;

    public ArrayList<AuditType> getAudit_types() {
        return audit_types;
    }

    public void setAudit_types(ArrayList<AuditType> audit_types) {
        this.audit_types = audit_types;
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

    public ArrayList<AditorReviewBean> getReviewers() {
        return reviewers;
    }

    public void setReviewers(ArrayList<AditorReviewBean> reviewers) {
        this.reviewers = reviewers;
    }

    public ArrayList<AditorReviewBean> getAuditors() {
        return auditors;
    }

    public void setAuditors(ArrayList<AditorReviewBean> auditors) {
        this.auditors = auditors;
    }
}
