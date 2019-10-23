package com.cs528.hw3.database;

import java.sql.Time;

public class Action {
    private Long time;
    private int action;

    public Action(Long time, int action) {
        this.time = time;
        this.action = action;
    }

    public Long getTime() {
        return time;
    }

    public int getAction() {
        return action;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setAction(int action) {
        this.action = action;
    }

}
