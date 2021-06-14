package com.oditly.audit.inspection.model.audit.BrandStandard;

import android.net.Uri;
import com.oditly.audit.inspection.adapter.BrandStandardAuditAdapter;
import java.util.ArrayList;
import java.util.List;

public class BrandStandardQuestion {
    private BrandStandardActionPlan action_plan;
    private String audit_answer = "";
    private int audit_answer_na = 0;
    private String audit_comment = "";
    ArrayList<Integer> audit_option_id;
    private int audit_question_file_cnt = 0;
    private boolean can_create_action_plan;
    private int comment_req_type_id;
    private int has_comment = 0;
    private int is_required = 0;
    private int mClickPosition = 0;
    List<Uri> mImageList;
    private int media_count = 0;
    private int media_req_type_id;
    private String obtained_mark;
    ArrayList<BrandStandardQuestionsOption> options;
    private String question_hint = "";
    private int question_id = 0;
    private String question_title = "";
    private String question_type = "";
    private int question_type_id = 0;
    private BrandStandardRefrence ref_file;
    private BrandStandardSlider slider;
    private BrandStandardAuditAdapter standardAuditAdapter;
    private BrandStandardUnit unit;
    private String max_mark;

    public int getQuestion_id() {
        return this.question_id;
    }

    public void setQuestion_id(int question_id2) {
        this.question_id = question_id2;
    }

    public String getQuestion_title() {
        return this.question_title;
    }

    public void setQuestion_title(String question_title2) {
        this.question_title = question_title2;
    }

    public int getQuestion_type_id() {
        return this.question_type_id;
    }

    public void setQuestion_type_id(int question_type_id2) {
        this.question_type_id = question_type_id2;
    }

    public String getQuestion_type() {
        return this.question_type;
    }

    public void setQuestion_type(String question_type2) {
        this.question_type = question_type2;
    }

    public String getHint() {
        return this.question_hint;
    }

    public void setHint(String hint) {
        this.question_hint = hint;
    }

    public int getIs_required() {
        return this.is_required;
    }

    public void setIs_required(int is_required2) {
        this.is_required = is_required2;
    }

    public int getHas_comment() {
        return this.has_comment;
    }

    public String getAudit_answer() {
        return this.audit_answer;
    }

    public void setAudit_answer(String audit_answer2) {
        this.audit_answer = audit_answer2;
    }

    public int getAudit_answer_na() {
        return this.audit_answer_na;
    }

    public void setAudit_answer_na(int audit_answer_na2) {
        this.audit_answer_na = audit_answer_na2;
    }

    public String getAudit_comment() {
        return this.audit_comment;
    }

    public void setAudit_comment(String audit_comment2) {
        this.audit_comment = audit_comment2;
    }

    public int getAudit_question_file_cnt() {
        return this.audit_question_file_cnt;
    }

    public void setAudit_question_file_cnt(int audit_question_file_cnt2) {
        this.audit_question_file_cnt = audit_question_file_cnt2;
    }

    public ArrayList<BrandStandardQuestionsOption> getOptions() {
        return this.options;
    }

    public void setOptions(ArrayList<BrandStandardQuestionsOption> options2) {
        this.options = options2;
    }

    public ArrayList<Integer> getAudit_option_id() {
        return this.audit_option_id;
    }

    public void setAudit_option_id(ArrayList<Integer> audit_option_id2) {
        this.audit_option_id = audit_option_id2;
    }

    public List<Uri> getmImageList() {
        return this.mImageList;
    }

    public void setmImageList(List<Uri> mImageList2) {
        this.mImageList = mImageList2;
    }

    public BrandStandardSlider getSlider() {
        return this.slider;
    }

    public void setSlider(BrandStandardSlider slider2) {
        this.slider = slider2;
    }

    public int getMedia_count() {
        return this.media_count;
    }

    public void setMedia_count(int media_count2) {
        this.media_count = media_count2;
    }

    public BrandStandardRefrence getRef_file() {
        return this.ref_file;
    }

    public void setRef_file(BrandStandardRefrence ref_file2) {
        this.ref_file = ref_file2;
    }

    public BrandStandardUnit getUnit() {
        return this.unit;
    }

    public void setUnit(BrandStandardUnit unit2) {
        this.unit = unit2;
    }

    public int getComment_req_type_id() {
        return this.comment_req_type_id;
    }

    public void setComment_req_type_id(int comment_req_type_id2) {
        this.comment_req_type_id = comment_req_type_id2;
    }

    public int getMedia_req_type_id() {
        return this.media_req_type_id;
    }

    public void setMedia_req_type_id(int media_req_type_id2) {
        this.media_req_type_id = media_req_type_id2;
    }

    public String getObtainMarksForQuestion() {
        return this.obtained_mark;
    }

    public void setObtainMarksForQuestion(String obtainMarksForQuestion) {
        this.obtained_mark = obtainMarksForQuestion;
    }

    public boolean isCan_create_action_plan() {
        return this.can_create_action_plan;
    }

    public void setCan_create_action_plan(boolean can_create_action_plan2) {
        this.can_create_action_plan = can_create_action_plan2;
    }

    public BrandStandardActionPlan getAction_plan() {
        return this.action_plan;
    }

    public void setAction_plan(BrandStandardActionPlan action_plan2) {
        this.action_plan = action_plan2;
    }

    public int getmClickPosition() {
        return this.mClickPosition;
    }

    public void setmClickPosition(int mClickPosition2) {
        this.mClickPosition = mClickPosition2;
    }

    public BrandStandardAuditAdapter getStandardAuditAdapter() {
        return this.standardAuditAdapter;
    }

    public void setStandardAuditAdapter(BrandStandardAuditAdapter standardAuditAdapter2) {
        this.standardAuditAdapter = standardAuditAdapter2;
    }

    public String getMax_mark() {
        return max_mark;
    }

    public void setMax_mark(String max_mark) {
        this.max_mark = max_mark;
    }
}
