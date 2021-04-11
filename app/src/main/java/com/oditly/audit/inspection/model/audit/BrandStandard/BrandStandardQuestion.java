package com.oditly.audit.inspection.model.audit.BrandStandard;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class BrandStandardQuestion {

    private int question_id = 0;
    private String question_title = "";
    private int question_type_id = 0;
    private String question_type = "";
    private String question_hint = "";
    private int is_required = 0;
    private int has_comment = 0;
    private boolean can_view_create_action_plan;
    private String audit_answer = "";
    private int audit_answer_na = 0;
    private String audit_comment = "";
   // private int answer_status = 0;
    private int audit_question_file_cnt = 0;
    ArrayList<BrandStandardQuestionsOption> options;
    ArrayList<Integer> audit_option_id;
    List<Uri> mImageList;
    private BrandStandardSlider slider;
    private int media_count = 0;
    private BrandStandardRefrence ref_file;
    private BrandStandardUnit unit;


    protected BrandStandardQuestion(Parcel in) {
        question_id = in.readInt();
        question_title = in.readString();
        question_type_id = in.readInt();
        question_type = in.readString();
        question_hint = in.readString();
        is_required = in.readInt();
        has_comment = in.readInt();
     //   is_numbered = in.readInt();
      //  max_mark = in.readInt();
        audit_answer = in.readString();
        audit_answer_na = in.readInt();
        audit_comment = in.readString();
       // answer_status = in.readInt();
        audit_question_file_cnt = in.readInt();
        if (in.readByte() == 0x01) {
            options = new ArrayList<BrandStandardQuestionsOption>();
            in.readList(options, BrandStandardQuestionsOption.class.getClassLoader());
        } else {
            options = null;
        }
        if (in.readByte() == 0x01) {
            audit_option_id = new ArrayList<Integer>();
            in.readList(audit_option_id, Integer.class.getClassLoader());
        } else {
            audit_option_id = null;
        }
        if (in.readByte() == 0x01) {
            mImageList = new ArrayList<Uri>();
            in.readList(mImageList, String.class.getClassLoader());
        } else {
            mImageList = null;
        }
        this.slider= in.readParcelable(BrandStandardSlider.class.getClassLoader()); //retrieving from parcel
        this.media_count=in.readInt();
        this.ref_file= in.readParcelable(BrandStandardRefrence.class.getClassLoader()); //retrieving from parcel
        this.unit= in.readParcelable(BrandStandardUnit.class.getClassLoader()); //retrieving from parcel


    }


    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public String getQuestion_title() {
        return question_title;
    }

    public void setQuestion_title(String question_title) {
        this.question_title = question_title;
    }

    public int getQuestion_type_id() {
        return question_type_id;
    }

    public void setQuestion_type_id(int question_type_id) {
        this.question_type_id = question_type_id;
    }

    public String getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(String question_type) {
        this.question_type = question_type;
    }

    public String getHint() {
        return question_hint;
    }

    public void setHint(String hint) {
        this.question_hint = hint;
    }

    public int getIs_required() {
        return is_required;
    }

    public void setIs_required(int is_required) {
        this.is_required = is_required;
    }


    public int getHas_comment() {
        return has_comment;
    }


    public String getAudit_answer() {
        return audit_answer;
    }

    public void setAudit_answer(String audit_answer) {
        this.audit_answer = audit_answer;
    }

    public int getAudit_answer_na() {
        return audit_answer_na;
    }

    public void setAudit_answer_na(int audit_answer_na) {
        this.audit_answer_na = audit_answer_na;
    }

    public String getAudit_comment() {
        return audit_comment;
    }

    public void setAudit_comment(String audit_comment) {
        this.audit_comment = audit_comment;
    }

    public int getAudit_question_file_cnt() {
        return audit_question_file_cnt;
    }

    public void setAudit_question_file_cnt(int audit_question_file_cnt) {
        this.audit_question_file_cnt = audit_question_file_cnt;
    }

    public ArrayList<BrandStandardQuestionsOption> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<BrandStandardQuestionsOption> options) {
        this.options = options;
    }

    public ArrayList<Integer> getAudit_option_id() {
        return audit_option_id;
    }

    public void setAudit_option_id(ArrayList<Integer> audit_option_id) {
        this.audit_option_id = audit_option_id;
    }

    public List<Uri> getmImageList() {
        return mImageList;
    }

    public void setmImageList(List<Uri> mImageList) {
        this.mImageList = mImageList;
    }

    public BrandStandardSlider getSlider() {
        return slider;
    }

    public void setSlider(BrandStandardSlider slider) {
        this.slider = slider;
    }

    public int getMedia_count() {
        return media_count;
    }

    public void setMedia_count(int media_count) {
        this.media_count = media_count;
    }

    public BrandStandardRefrence getRef_file() {
        return ref_file;
    }

    public void setRef_file(BrandStandardRefrence ref_file) {
        this.ref_file = ref_file;
    }


    public BrandStandardUnit getUnit() {
        return unit;
    }

    public void setUnit(BrandStandardUnit unit) {
        this.unit = unit;
    }

    public boolean isCan_view_create_action_plan() {
        return can_view_create_action_plan;
    }

    public void setCan_view_create_action_plan(boolean can_view_create_action_plan) {
        this.can_view_create_action_plan = can_view_create_action_plan;
    }
}
