package com.cs528.hw3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cs528.hw3.database.Action;
import com.cs528.hw3.database.ActivityBaseHelper;
import com.cs528.hw3.database.ActivityCursorWrapper;
import com.cs528.hw3.database.ActivityDbSchema.ActivityTable;

import java.util.Date;

public class ActionBL {
    private SQLiteDatabase mDatabase;



    public ActionBL(Context mContext) {

        mDatabase = new ActivityBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addAction(Action aciton) {
        ContentValues values = getContentValues(aciton);

        mDatabase.insert(ActivityTable.NAME, null, values);
    }

    public Action getAction(){

        Cursor cursor = mDatabase.rawQuery("select * from "+ActivityTable.NAME,null);
        cursor.moveToLast();
        ActivityCursorWrapper tcursor = new ActivityCursorWrapper(cursor);
        try {
            if (tcursor.getCount() == 0) {
                return null;
            }
            return tcursor.getAction();
        } finally {
            tcursor.close();
            cursor.close();
        }
    }


    private static ContentValues getContentValues(Action action) {
        ContentValues values = new ContentValues();
        values.put(ActivityTable.Cols.TIME, action.getTime());
        values.put(ActivityTable.Cols.ACTION, action.getAction());

        return values;
    }

    public String calculateDuringTime(Date now, Date past){
        long l=now.getTime()-past.getTime();
        long day=l/(24*60*60*1000);
        long hour=(l/(60*60*1000)-day*24);
        long min=((l/(60*1000))-day*24*60-hour*60);
        long s=(l/1000-day*24*60*60-hour*60*60-min*60);
        return  " "+hour+" hour "+min+" min "+s+" seconds.";
    }
}
