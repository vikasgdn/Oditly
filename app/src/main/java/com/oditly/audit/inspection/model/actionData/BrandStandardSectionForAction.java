package com.oditly.audit.inspection.model.actionData;


import android.os.Parcel;
import android.os.Parcelable;

import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardQuestion;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardSubSection;

import java.util.ArrayList;

public class BrandStandardSectionForAction
{

    private int section_id = 0;
    private String section_title = "";
    private int section_group_id = 0;
    private String section_group_title = "";
    private int audit_section_file_cnt = 0;
    private int answered_question_count = 0;
    private int question_count;
    private float total_obtained_mark;
    private float  total_max_mark;
    private String section_weightage="1";
    ArrayList<BrandStandardQuestionForAction> questions;


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

    public int getAudit_section_file_cnt() {
        return audit_section_file_cnt;
    }

    public void setAudit_section_file_cnt(int audit_section_file_cnt) {
        this.audit_section_file_cnt = audit_section_file_cnt;
    }

    public ArrayList<BrandStandardQuestionForAction> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<BrandStandardQuestionForAction> questions) {
        this.questions = questions;
    }


    public int getAnswered_question_count() {
        return answered_question_count;
    }

    public void setAnswered_question_count(int answered_question_count) {
        this.answered_question_count = answered_question_count;
    }

    public String getSection_weightage() {
        return section_weightage;
    }

    public void setSection_weightage(String section_weightage) {
        this.section_weightage = section_weightage;
    }
    public int getQuestion_count() {
        return question_count;
    }

    public void setQuestion_count(int question_count) {
        this.question_count = question_count;
    }

    public float getTotal_obtained_mark() {
        return total_obtained_mark;
    }

    public void setTotal_obtained_mark(float total_obtained_mark) {
        this.total_obtained_mark = total_obtained_mark;
    }

    public float getTotal_max_mark() {
        return total_max_mark;
    }

    public void setTotal_max_mark(float total_max_mark) {
        this.total_max_mark = total_max_mark;
    }
}
