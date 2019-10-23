package com.cs528.hw3.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.cs528.hw3.database.ActivityDbSchema.ActivityTable;

import java.sql.Time;


public class ActivityCursorWrapper extends CursorWrapper {
    public ActivityCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Action getAction() {

        Long time = getLong(getColumnIndex(ActivityTable.Cols.TIME));
        int act = getInt(getColumnIndex(ActivityTable.Cols.ACTION));
        Action acti = new Action(time,act);

        return acti;
    }
}
