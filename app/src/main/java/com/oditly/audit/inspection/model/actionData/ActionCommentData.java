package com.oditly.audit.inspection.model.actionData;


import java.util.ArrayList;

public class ActionCommentData {

    public int getAction_plan_id() {
        return action_plan_id;
    }

    public void setAction_plan_id(int action_plan_id) {
        this.action_plan_id = action_plan_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getCreated_by_id() {
        return created_by_id;
    }

    public void setCreated_by_id(int created_by_id) {
        this.created_by_id = created_by_id;
    }

    public String getCreated_by_name() {
        return created_by_name;
    }

    public void setCreated_by_name(String created_by_name) {
        this.created_by_name = created_by_name;
    }

    public String getCreated_by_email() {
        return created_by_email;
    }

    public void setCreated_by_email(String created_by_email) {
        this.created_by_email = created_by_email;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public int getFiles_count() {
        return files_count;
    }

    public void setFiles_count(int files_count) {
        this.files_count = files_count;
    }

    int action_plan_id;
    String comment;
    int created_by_id;
    String created_by_name;
    String created_by_email;
    String created_on;
    int files_count;


}


/*
"id": 2,
        "action_plan_id": 68,
        "comment": "vikas",
        "created_by_id": 199,
        "created_by_name": "CC HM",
        "created_by_email": "hm@oditly.com",
        "created_on": "2021-02-28 11:12:46",
        "files": [

        ],
        "files_count": 0*/
