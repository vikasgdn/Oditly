package com.oditly.audit.inspection.model.audit.BrandStandard;

public class BrandStandardActionPlan {
    private String action_plan_id;
    private String action_plan_status;
    private String can_delete;
    private String can_update;

    public String getAction_plan_id() {
        return this.action_plan_id;
    }

    public void setAction_plan_id(String action_plan_id2) {
        this.action_plan_id = action_plan_id2;
    }

    public String getAction_plan_status() {
        return this.action_plan_status;
    }

    public void setAction_plan_status(String action_plan_status2) {
        this.action_plan_status = action_plan_status2;
    }

    public String getCan_update() {
        return this.can_update;
    }

    public void setCan_update(String can_update2) {
        this.can_update = can_update2;
    }

    public String getCan_delete() {
        return this.can_delete;
    }

    public void setCan_delete(String can_delete2) {
        this.can_delete = can_delete2;
    }
}
