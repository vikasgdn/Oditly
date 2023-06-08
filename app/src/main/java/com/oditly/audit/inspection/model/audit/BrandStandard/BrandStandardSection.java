package com.oditly.audit.inspection.model.audit.BrandStandard;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class BrandStandardSection implements Parcelable {

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
    ArrayList<BrandStandardQuestion> questions;
    ArrayList<BrandStandardSubSection> sub_sections;

    public BrandStandardSection() {
    }

    protected BrandStandardSection(Parcel in) {
        section_id = in.readInt();
        section_title = in.readString();
        section_group_id = in.readInt();
        section_group_title = in.readString();
        audit_section_file_cnt = in.readInt();
        answered_question_count = in.readInt();
        question_count = in.readInt();
        if (in.readByte() == 0x01) {
            questions = new ArrayList<BrandStandardQuestion>();
            in.readList(questions, BrandStandardQuestion.class.getClassLoader());
        } else {
            questions = null;
        }
        if (in.readByte() == 0x01) {
            sub_sections = new ArrayList<BrandStandardSubSection>();
            in.readList(sub_sections, BrandStandardSubSection.class.getClassLoader());
        } else {
            sub_sections = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(section_id);
        dest.writeString(section_title);
        dest.writeInt(section_group_id);
        dest.writeString(section_group_title);
        dest.writeInt(audit_section_file_cnt);
        dest.writeInt(answered_question_count);
        dest.writeInt(question_count);
        if (questions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(questions);
        }
        if (sub_sections == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(sub_sections);
        }
    }


    @SuppressWarnings("unused")
    public static final Creator<BrandStandardSection> CREATOR = new Creator<BrandStandardSection>() {
        @Override
        public BrandStandardSection createFromParcel(Parcel in) {
            return new BrandStandardSection(in);
        }

        @Override
        public BrandStandardSection[] newArray(int size) {
            return new BrandStandardSection[size];
        }
    };

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

    public ArrayList<BrandStandardQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<BrandStandardQuestion> questions) {
        this.questions = questions;
    }

    public ArrayList<BrandStandardSubSection> getSub_sections() {
        return sub_sections;
    }

    public void setSub_sections(ArrayList<BrandStandardSubSection> sub_sections) {
        this.sub_sections = sub_sections;
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
