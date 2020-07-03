package com.oditly.audit.inspection.model.audit.DetailedSummary;


public class DetailedSummaryInfo {

    private int audit_id = 0;
    private String audit_date = "";
    private int detailed_sum_status = 0;
    private String reviewer_detailed_sum_comment = "";
    private int section_id = 0;
    private String section_title = "";
    private int is_na = 0;
    private String date= "";
    private String time= "";
    private String date_time = "";
    private String staff_name = "";
    private int file_count = 0;
    private int max_mark = 0;
    private int option_mark = 0;
    private String summary = "";
    private String key_positive = "";
    private String key_negative = "";
    private String recommendation = "";
    private int section_group_id = 0;
    private String section_group_title = "";
    boolean isExpand = false;

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

    public int getDetailed_sum_status() {
        return detailed_sum_status;
    }

    public void setDetailed_sum_status(int detailed_sum_status) {
        this.detailed_sum_status = detailed_sum_status;
    }

    public String getReviewer_detailed_sum_comment() {
        return reviewer_detailed_sum_comment;
    }

    public void setReviewer_detailed_sum_comment(String reviewer_detailed_sum_comment) {
        this.reviewer_detailed_sum_comment = reviewer_detailed_sum_comment;
    }

    public int getSection_id() {
        return section_id;
    }

    public void setSection_id(int section_id) {
        this.section_id = section_id;
    }

    public String getSection_title() {
        return section_title;
    }

    public void setSection_title(String section_title) {
        this.section_title = section_title;
    }

    public int getIs_na() {
        return is_na;
    }

    public void setIs_na(int is_na) {
        this.is_na = is_na;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getStaff_name() {
        return staff_name;
    }

    public void setStaff_name(String staff_name) {
        this.staff_name = staff_name;
    }

    public int getFile_count() {
        return file_count;
    }

    public void setFile_count(int file_count) {
        this.file_count = file_count;
    }

    public int getMax_mark() {
        return max_mark;
    }

    public void setMax_mark(int max_mark) {
        this.max_mark = max_mark;
    }

    public int getOption_mark() {
        return option_mark;
    }

    public void setOption_mark(int option_mark) {
        this.option_mark = option_mark;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getKey_positive() {
        return key_positive;
    }

    public void setKey_positive(String key_positive) {
        this.key_positive = key_positive;
    }

    public String getKey_negative() {
        return key_negative;
    }

    public void setKey_negative(String key_negative) {
        this.key_negative = key_negative;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public int getSection_group_id() {
        return section_group_id;
    }

    public void setSection_group_id(int section_group_id) {
        this.section_group_id = section_group_id;
    }

    public String getSection_group_title() {
        return section_group_title;
    }

    public void setSection_group_title(String section_group_title) {
        this.section_group_title = section_group_title;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
