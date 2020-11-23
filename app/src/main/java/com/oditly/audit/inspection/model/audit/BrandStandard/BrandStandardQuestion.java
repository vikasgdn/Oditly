package com.oditly.audit.inspection.model.audit.BrandStandard;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class BrandStandardQuestion implements Parcelable {

    private int question_id = 0;
    private String question_title = "";
    private int question_type_id = 0;
    private String question_type = "";
    private String question_hint = "";
    private int is_required = 0;
    private int is_visible = 0;
    private int has_na = 0;
    private int has_comment = 0;
    private int is_numbered = 0;
    private int max_mark = 0;
    private String audit_answer = "";
    private int audit_answer_na = 0;
    private String audit_comment = "";
    private int answer_status = 0;
    private String reviewer_answer_comment = "";
    private int obtained_mark = 0;
    private int audit_question_file_cnt = 0;
  //  private String ref_image_name = "";
    //private String ref_image_url = "";
   // private String ref_image_thumb = "";
    ArrayList<BrandStandardQuestionsOption> options;
    ArrayList<Integer> audit_option_id;
    List<Uri> mImageList;
    private BrandStandardSlider slider;
    private int media_count = 0;
    private BrandStandardRefrence ref_file;



    protected BrandStandardQuestion(Parcel in) {
        question_id = in.readInt();
        question_title = in.readString();
        question_type_id = in.readInt();
        question_type = in.readString();
        question_hint = in.readString();
        is_required = in.readInt();
        is_visible = in.readInt();
        has_na = in.readInt();
        has_comment = in.readInt();
        is_numbered = in.readInt();
        max_mark = in.readInt();
        audit_answer = in.readString();
        audit_answer_na = in.readInt();
        audit_comment = in.readString();
        answer_status = in.readInt();
        reviewer_answer_comment = in.readString();
        obtained_mark = in.readInt();
        audit_question_file_cnt = in.readInt();
       // ref_image_name = in.readString();
       // ref_image_url = in.readString();
       // ref_image_thumb = in.readString();
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


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(question_id);
        dest.writeString(question_title);
        dest.writeInt(question_type_id);
        dest.writeString(question_type);
        dest.writeString(question_hint);
        dest.writeInt(is_required);
        dest.writeInt(is_visible);
        dest.writeInt(has_na);
        dest.writeInt(has_comment);
        dest.writeInt(is_numbered);
        dest.writeInt(max_mark);
        dest.writeString(audit_answer);
        dest.writeInt(audit_answer_na);
        dest.writeString(audit_comment);
        dest.writeInt(answer_status);
        dest.writeString(reviewer_answer_comment);
        dest.writeInt(obtained_mark);
        dest.writeInt(audit_question_file_cnt);
      //  dest.writeString(ref_image_name);
      //  dest.writeString(ref_image_url);
      //  dest.writeString(ref_image_thumb);
        if (options == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(options);
        }
        if (audit_option_id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(audit_option_id);
        }
        if (mImageList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mImageList);
        }
        dest.writeParcelable(slider, flags); // saving object
        dest.writeInt(media_count);
        dest.writeParcelable(ref_file, flags); // saving object

    }

    @SuppressWarnings("unused")
    public static final Creator<BrandStandardQuestion> CREATOR = new Creator<BrandStandardQuestion>() {
        @Override
        public BrandStandardQuestion createFromParcel(Parcel in) {
            return new BrandStandardQuestion(in);
        }

        @Override
        public BrandStandardQuestion[] newArray(int size) {
            return new BrandStandardQuestion[size];
        }
    };

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

    public int getIs_visible() {
        return is_visible;
    }

    public void setIs_visible(int is_visible) {
        this.is_visible = is_visible;
    }

    public int getHas_na() {
        return has_na;
    }

    public void setHas_na(int has_na) {
        this.has_na = has_na;
    }

    public int getHas_comment() {
        return has_comment;
    }

    public void setHas_comment(int has_comment) {
        this.has_comment = has_comment;
    }

    public int getIs_numbered() {
        return is_numbered;
    }

    public void setIs_numbered(int is_numbered) {
        this.is_numbered = is_numbered;
    }

    public int getMax_mark() {
        return max_mark;
    }

    public void setMax_mark(int max_mark) {
        this.max_mark = max_mark;
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

    public int getAnswer_status() {
        return answer_status;
    }

    public void setAnswer_status(int answer_status) {
        this.answer_status = answer_status;
    }

    public String getReviewer_answer_comment() {
        return reviewer_answer_comment;
    }

    public void setReviewer_answer_comment(String reviewer_answer_comment) {
        this.reviewer_answer_comment = reviewer_answer_comment;
    }

    public int getObtained_mark() {
        return obtained_mark;
    }

    public void setObtained_mark(int obtained_mark) {
        this.obtained_mark = obtained_mark;
    }

    public int getAudit_question_file_cnt() {
        return audit_question_file_cnt;
    }

    public void setAudit_question_file_cnt(int audit_question_file_cnt) {
        this.audit_question_file_cnt = audit_question_file_cnt;
    }

  /*  public String getRef_image_name() {
        return ref_image_name;
    }

    public void setRef_image_name(String ref_image_name) {
        this.ref_image_name = ref_image_name;
    }

    public String getRef_image_url() {
        return ref_image_url;
    }

    public void setRef_image_url(String ref_image_url) {
        this.ref_image_url = ref_image_url;
    }

    public String getRef_image_thumb() {
        return ref_image_thumb;
    }

    public void setRef_image_thumb(String ref_image_thumb) {
        this.ref_image_thumb = ref_image_thumb;
    }*/

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


}
