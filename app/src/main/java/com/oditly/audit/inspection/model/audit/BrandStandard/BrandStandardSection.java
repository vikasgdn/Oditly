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
    public static final Parcelable.Creator<BrandStandardSection> CREATOR = new Parcelable.Creator<BrandStandardSection>() {
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
}
