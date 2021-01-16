package com.oditly.audit.inspection.model.audit.BrandStandard;


import android.os.Parcel;
import android.os.Parcelable;

public class BrandStandardSlider implements Parcelable {

    int from = 0;
    int to = 0;
    int step = 0;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }





    protected BrandStandardSlider(Parcel in) {
        from = in.readInt();
        to = in.readInt();
        step = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(from);
        dest.writeInt(to);
        dest.writeInt(step);
    }

    @SuppressWarnings("unused")
    public static final Creator<BrandStandardSlider> CREATOR = new Creator<BrandStandardSlider>() {
        @Override
        public BrandStandardSlider createFromParcel(Parcel in) {
            return new BrandStandardSlider(in);
        }

        @Override
        public BrandStandardSlider[] newArray(int size) {
            return new BrandStandardSlider[size];
        }
    };


}