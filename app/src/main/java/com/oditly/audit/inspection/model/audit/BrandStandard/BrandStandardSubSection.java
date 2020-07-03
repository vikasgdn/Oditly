package com.oditly.audit.inspection.model.audit.BrandStandard;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class BrandStandardSubSection implements Parcelable {

    private int sub_section_id = 0;
    private String sub_section_title = "";
    ArrayList<BrandStandardQuestion> questions;



    protected BrandStandardSubSection(Parcel in) {
        sub_section_id = in.readInt();
        sub_section_title = in.readString();
        if (in.readByte() == 0x01) {
            questions = new ArrayList<BrandStandardQuestion>();
            in.readList(questions, BrandStandardQuestion.class.getClassLoader());
        } else {
            questions = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sub_section_id);
        dest.writeString(sub_section_title);
        if (questions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(questions);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BrandStandardSubSection> CREATOR = new Parcelable.Creator<BrandStandardSubSection>() {
        @Override
        public BrandStandardSubSection createFromParcel(Parcel in) {
            return new BrandStandardSubSection(in);
        }

        @Override
        public BrandStandardSubSection[] newArray(int size) {
            return new BrandStandardSubSection[size];
        }
    };

    public int getSub_section_id() {
        return sub_section_id;
    }

    public void setSub_section_id(int sub_section_id) {
        this.sub_section_id = sub_section_id;
    }

    public String getSub_section_title() {
        return sub_section_title;
    }

    public void setSub_section_title(String sub_section_title) {
        this.sub_section_title = sub_section_title;
    }

    public ArrayList<BrandStandardQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<BrandStandardQuestion> questions) {
        this.questions = questions;
    }
}
