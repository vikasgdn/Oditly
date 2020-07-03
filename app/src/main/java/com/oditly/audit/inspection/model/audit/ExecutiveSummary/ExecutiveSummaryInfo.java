package com.oditly.audit.inspection.model.audit.ExecutiveSummary;


public class ExecutiveSummaryInfo {

    private int audit_id = 0;
    private String audit_date = "";
    private int exec_sum_status = 0;
    private String executive_summary = "";
    private String reviewer_exec_sum_comment = "";
    private int file_count = 0;
    private int is_na = 0;
    private int total_obtained_mark = 0;
    private int total_max_mark = 0;

    public int getAudit_id() {
        return audit_id;
    }

    public void setAudit_id(int audit_id) {
        this.audit_id = audit_id;
    }

    public String getAudit_date() {
        return audit_date;
    }

    public void setAudit_date(String audit_date) {
        this.audit_date = audit_date;
    }

    public int getExec_sum_status() {
        return exec_sum_status;
    }

    public void setExec_sum_status(int exec_sum_status) {
        this.exec_sum_status = exec_sum_status;
    }

    public String getExecutive_summary() {
        return executive_summary;
    }

    public void setExecutive_summary(String executive_summary) {
        this.executive_summary = executive_summary;
    }

    public String getReviewer_exec_sum_comment() {
        return reviewer_exec_sum_comment;
    }

    public void setReviewer_exec_sum_comment(String reviewer_exec_sum_comment) {
        this.reviewer_exec_sum_comment = reviewer_exec_sum_comment;
    }

    public int getFile_count() {
        return file_count;
    }

    public void setFile_count(int file_count) {
        this.file_count = file_count;
    }

    public int getIs_na() {
        return is_na;
    }

    public void setIs_na(int is_na) {
        this.is_na = is_na;
    }

    public int getTotal_obtained_mark() {
        return total_obtained_mark;
    }

    public void setTotal_obtained_mark(int total_obtained_mark) {
        this.total_obtained_mark = total_obtained_mark;
    }

    public int getTotal_max_mark() {
        return total_max_mark;
    }

    public void setTotal_max_mark(int total_max_mark) {
        this.total_max_mark = total_max_mark;
    }
}
