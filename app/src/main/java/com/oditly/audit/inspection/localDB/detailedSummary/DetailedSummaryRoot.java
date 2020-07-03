package com.oditly.audit.inspection.localDB.detailedSummary;


import com.oditly.audit.inspection.model.audit.DetailedSummary.DetailedSummaryInfo;

import java.util.ArrayList;

public class DetailedSummaryRoot {

    String auditId;
    ArrayList<DetailedSummaryInfo> sections;

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public ArrayList<DetailedSummaryInfo> getSections() {
        return sections;
    }

    public void setSections(ArrayList<DetailedSummaryInfo> sections) {
        this.sections = sections;
    }
}
