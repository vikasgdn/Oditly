package com.oditly.audit.inspection.model.audit.AddAttachment;

import android.os.Parcel;
import android.os.Parcelable;

public class AddAttachmentInfo implements Parcelable {

    int brand_std_status = 0;
    String created_on = "";
    String file_url = "";
    String thumb_url = "";
    int audit_section_file_id = 0;
    String client_file_name = "";
    String file_name = "";
    String description = "";
    String file_type = "";
    int is_critical = 0;
    int audit_question_file_id = 0;




    protected AddAttachmentInfo(Parcel in) {
        brand_std_status = in.readInt();
        file_url = in.readString();
        thumb_url = in.readString();
        audit_section_file_id = in.readInt();
        client_file_name = in.readString();
        file_name = in.readString();
        description = in.readString();
        file_type = in.readString();
        created_on = in.readString();
        is_critical = in.readInt();
        audit_question_file_id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(brand_std_status);
        dest.writeString(file_url);
        dest.writeString(thumb_url);
        dest.writeInt(audit_section_file_id);
        dest.writeString(client_file_name);
        dest.writeString(file_name);
        dest.writeString(description);
        dest.writeString(file_type);
        dest.writeString(created_on);
        dest.writeInt(is_critical);
        dest.writeInt(audit_question_file_id);
    }

    @SuppressWarnings("unused")
    public static final Creator<AddAttachmentInfo> CREATOR = new Creator<AddAttachmentInfo>() {
        @Override
        public AddAttachmentInfo createFromParcel(Parcel in) {
            return new AddAttachmentInfo(in);
        }

        @Override
        public AddAttachmentInfo[] newArray(int size) {
            return new AddAttachmentInfo[size];
        }
    };

    public int getAudit_question_file_id() {
        return audit_question_file_id;
    }

    public void setAudit_question_file_id(int audit_question_file_id) {
        this.audit_question_file_id = audit_question_file_id;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public String getClient_file_name() {
        return client_file_name;
    }

    public void setClient_file_name(String client_file_name) {
        this.client_file_name = client_file_name;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public int getBrand_std_status() {
        return brand_std_status;
    }

    public void setBrand_std_status(int brand_std_status) {
        this.brand_std_status = brand_std_status;
    }

    public int getAudit_section_file_id() {
        return audit_section_file_id;
    }

    public void setAudit_section_file_id(int audit_section_file_id) {
        this.audit_section_file_id = audit_section_file_id;
    }

    public int getIs_critical() {
        return is_critical;
    }

    public void setIs_critical(int is_critical) {
        this.is_critical = is_critical;
    }
}
