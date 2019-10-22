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
        Time time = Time.valueOf(getString(getColumnIndex(ActivityTable.Cols.TIME)));
        String act = getString(getColumnIndex(ActivityTable.Cols.ACTION));

        Action acti = new Action(time,act);

        return acti;
    }
}
