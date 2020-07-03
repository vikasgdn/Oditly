package com.oditly.audit.inspection.localDB.executiveSummary;


import com.oditly.audit.inspection.model.audit.ExecutiveSummary.ExecutiveSummaryInfo;

public class ExecutiveSummaryRoot {

    String auditId;
    ExecutiveSummaryInfo sections;

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public ExecutiveSummaryInfo getSections() {
        return sections;
    }

    public void setSections(ExecutiveSummaryInfo sections) {
        this.sections = sections;
    }
}
