package com.oditly.audit.inspection.model.audit.BrandStandard;


import android.os.Parcel;
import android.os.Parcelable;

public class BrandStandardQuestionsOption implements Parcelable {

    int option_id = 0;
    String option_text = "";
    float option_mark;
    int selected = 0;
    int checked = 0;


    protected BrandStandardQuestionsOption(Parcel in) {
        option_id = in.readInt();
        option_text = in.readString();
        option_mark = in.readFloat();
        selected = in.readInt();
        checked = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(option_id);
        dest.writeString(option_text);
        dest.writeFloat(option_mark);
        dest.writeInt(selected);
        dest.writeInt(checked);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BrandStandardQuestionsOption> CREATOR = new Parcelable.Creator<BrandStandardQuestionsOption>() {
        @Override
        public BrandStandardQuestionsOption createFromParcel(Parcel in) {
            return new BrandStandardQuestionsOption(in);
        }

        @Override
        public BrandStandardQuestionsOption[] newArray(int size) {
            return new BrandStandardQuestionsOption[size];
        }
    };

    public int getOption_id() {
        return option_id;
    }

    public void setOption_id(int option_id) {
        this.option_id = option_id;
    }

    public String getOption_text() {
        return option_text;
    }

    public void setOption_text(String option_text) {
        this.option_text = option_text;
    }

    public float getOption_mark() {
        return option_mark;
    }

    public void setOption_mark(int option_mark) {
        this.option_mark = option_mark;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }
}