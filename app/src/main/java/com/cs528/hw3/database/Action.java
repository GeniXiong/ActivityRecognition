package com.cs528.hw3.database;

import java.sql.Time;

public class Action {
    private Time time;
    private String action;

    public Action(Time time, String action) {
        this.time = time;
        this.action = action;
    }

    public Time getTime() {
        return time;
    }

    public String getAction() {
        return action;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
