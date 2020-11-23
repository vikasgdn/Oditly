package com.oditly.audit.inspection.model.audit.BrandStandard;


import android.os.Parcel;
import android.os.Parcelable;

public class BrandStandardRefrence implements Parcelable {

    private String  file_name = "";
    private String  client_file_name = "";
    private String  file_ext = "";
    private String  file_type = "";
    private String  file_url = "";
    private String  file_thumb_url = "";




    protected BrandStandardRefrence(Parcel in) {
        file_name = in.readString();
        client_file_name = in.readString();
        file_ext = in.readString();
        file_type = in.readString();
        file_url = in.readString();
        file_thumb_url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(file_name);
        dest.writeString(client_file_name);
        dest.writeString(file_ext);
        dest.writeString(file_type);
        dest.writeString(file_url);
        dest.writeString(file_thumb_url);
    }

    @SuppressWarnings("unused")
    public static final Creator<BrandStandardRefrence> CREATOR = new Creator<BrandStandardRefrence>() {
        @Override
        public BrandStandardRefrence createFromParcel(Parcel in) {
            return new BrandStandardRefrence(in);
        }

        @Override
        public BrandStandardRefrence[] newArray(int size) {
            return new BrandStandardRefrence[size];
        }
    };
    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getClient_file_name() {
        return client_file_name;
    }

    public void setClient_file_name(String client_file_name) {
        this.client_file_name = client_file_name;
    }

    public String getFile_ext() {
        return file_ext;
    }

    public void setFile_ext(String file_ext) {
        this.file_ext = file_ext;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getFile_thumb_url() {
        return file_thumb_url;
    }

    public void setFile_thumb_url(String file_thumb_url) {
        this.file_thumb_url = file_thumb_url;
    }


}