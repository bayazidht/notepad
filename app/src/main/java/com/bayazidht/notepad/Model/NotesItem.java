package com.bayazidht.notepad.Model;

public class NotesItem {

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NotesItem(String title, String desc, String date, String id) {
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.id = id;
    }

    public String title;
    public String desc;
    public String date;
    public String id;
}