package com.cs528.hw3.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cs528.hw3.database.ActivityDbSchema.ActivityTable;

public class ActivityBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "ActivityBaseHelper";
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "activity.db";

    public ActivityBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + ActivityTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                ActivityTable.Cols.TIME + ", " +
                ActivityTable.Cols.ACTION +
                ")"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
