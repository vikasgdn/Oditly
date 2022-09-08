package com.oditly.audit.inspection.model.actionData;

public class ActionCompleteRequestBean {
    private String action_plan_id;
    private String audit_id;
    private String complete_comment;
    private String issue_fix_comment;
    private String mobile;
    private String cc_emails;

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile2) {
        this.mobile = mobile2;
    }

    public String getAudit_id() {
        return this.audit_id;
    }

    public void setAudit_id(String audit_id2) {
        this.audit_id = audit_id2;
    }

    public String getAction_plan_id() {
        return this.action_plan_id;
    }

    public void setAction_plan_id(String action_plan_id2) {
        this.action_plan_id = action_plan_id2;
    }

    public String getComplete_comment() {
        return this.complete_comment;
    }

    public void setComplete_comment(String complete_comment2) {
        this.complete_comment = complete_comment2;
    }

    public String getIssue_fix_comment() {
        return issue_fix_comment;
    }

    public void setIssue_fix_comment(String issue_fix_comment) {
        this.issue_fix_comment = issue_fix_comment;
    }

    public String getCc_emails() {
        return cc_emails;
    }

    public void setCc_emails(String cc_emails) {
        this.cc_emails = cc_emails;
    }
}
