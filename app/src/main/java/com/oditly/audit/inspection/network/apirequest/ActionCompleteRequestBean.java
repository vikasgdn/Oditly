package com.oditly.audit.inspection.network.apirequest;

public class ActionCompleteRequestBean {
    private String action_plan_id;
    private String audit_id;
    private String complete_comment;
    private String mobile;

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
}
