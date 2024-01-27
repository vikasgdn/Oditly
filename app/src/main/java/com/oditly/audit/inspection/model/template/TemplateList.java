package com.oditly.audit.inspection.model.template;

public class TemplateList {

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getQuestionnaire_id() {
        return questionnaire_id;
    }

    public void setQuestionnaire_id(int questionnaire_id) {
        this.questionnaire_id = questionnaire_id;
    }

    public String getQuestionnaire_title() {
        return questionnaire_title;
    }

    public void setQuestionnaire_title(String questionnaire_title) {
        this.questionnaire_title = questionnaire_title;
    }

    public String getCreated_by_name() {
        return created_by_name;
    }

    public void setCreated_by_name(String created_by_name) {
        this.created_by_name = created_by_name;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getQuestionnaire_status_name() {
        return questionnaire_status_name;
    }

    public void setQuestionnaire_status_name(String questionnaire_status_name) {
        this.questionnaire_status_name = questionnaire_status_name;
    }

    public String getStatus_text_color() {
        return status_text_color;
    }

    public void setStatus_text_color(String status_text_color) {
        this.status_text_color = status_text_color;
    }

    public String getStatus_bg_color() {
        return status_bg_color;
    }

    public void setStatus_bg_color(String status_bg_color) {
        this.status_bg_color = status_bg_color;
    }

    private int client_id;
private int questionnaire_id;
private String questionnaire_title;
private String created_by_name;
private String updated_on;
private String brand_name;
private String questionnaire_status_name;
private String status_text_color;
private String status_bg_color;

}
