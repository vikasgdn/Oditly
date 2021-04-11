package com.oditly.audit.inspection.model.audit.createaudit;

public class PriorityBean
{

    private int priority_id;
    private String priority_name;
    private String priority_color;

    public int getPriority_id() {
        return priority_id;
    }

    public void setPriority_id(int priority_id) {
        this.priority_id = priority_id;
    }

    public String getPriority_name() {
        return priority_name;
    }

    public void setPriority_name(String priority_name) {
        this.priority_name = priority_name;
    }

    public String getPriority_color() {
        return priority_color;
    }

    public void setPriority_color(String priority_color) {
        this.priority_color = priority_color;
    }


/* "priority_id": 1,
         "priority_name": "Low",
         "priority_color": "#f1c40f"*/
}
