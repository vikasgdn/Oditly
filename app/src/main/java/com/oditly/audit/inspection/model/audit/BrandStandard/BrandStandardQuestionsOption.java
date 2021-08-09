package com.oditly.audit.inspection.model.audit.BrandStandard;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class BrandStandardQuestionsOption  {

    int option_id = 0;
    String option_text = "";
    float option_mark;
    int selected = 0;
    int checked = 0;
    String option_color = "";
    String option_text_color = "";
    int comment_count;
    int media_count;
    private ArrayList<BrandStandardQuestion> questions;

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

    public String getOption_color() {
        return option_color;
    }

    public void setOption_color(String option_color) {
        this.option_color = option_color;
    }

    public String getOption_text_color() {
        return option_text_color;
    }

    public void setOption_text_color(String option_text_color) {
        this.option_text_color = option_text_color;
    }

    @Override
    public String toString() {
        return getOption_text(); // You can add anything else like maybe getDrinkType()
    }

    public int getCommentCount() {
        return comment_count;
    }

    public void setCommentCount(int comment) {
        this.comment_count = comment;
    }

    public int getMedia_count() {
        return media_count;
    }

    public void setMedia_count(int media_count) {
        this.media_count = media_count;
    }

    public ArrayList<BrandStandardQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<BrandStandardQuestion> questions) {
        this.questions = questions;
    }
}