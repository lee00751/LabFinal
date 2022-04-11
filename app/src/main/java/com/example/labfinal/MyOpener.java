package com.example.labfinal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpener extends SQLiteOpenHelper {

    protected static final String DATABASE_NAME = "FinalDB";
    protected static final int VERSION_NUM = 1;

    public static final String TABLE_NAME = "Favorites";

    public static final String COL_ID = "id";
    public static final String COL_SECTION_NAME = "sectionName";
    public static final String COL_WEB_TITLE = "webTitle";
    public static final String COL_WEB_URL = "webUrl";
    public static final String COL_MS = "ms";                   // Add to Favorites Date(TimeMillis)

    public MyOpener(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table

        // Favorites
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COL_ID + " TEXT PRIMARY KEY, "
                + COL_SECTION_NAME + " TEXT, "
                + COL_WEB_TITLE + " TEXT, "
                + COL_WEB_URL + " TEXT, "
                + COL_MS + " INTEGER);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // delete table
        db.execSQL("DROP TABLE IF EXISTS "  + TABLE_NAME);

        // to delete and recreate a table
        onCreate(db);
    }
}