package com.csymicc.poster;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Micc on 7/26/2017.
 */

public class PosterDataBaseHelper extends SQLiteOpenHelper {

    public static final int DataBaseVersion = 7;

    private final String CREATE_USER = "create table USER (" +
            "UID integer," +
            "EMAIL text primary key," +
            "USERNAME text)";

    private final String CREATE_FOLLOW = "create table FOLLOW(" +
            "UID1 integer," +
            "UID2 integer," +
            "primary key (UID1, UID2))";

    private final String CREATE_POST = "create table POST(" +
            "PID integer primary key," +
            "UID integer," +
            "USERNAME text," +
            "TIME text," +
            "TEXT text)";

    private final String CREATE_IMAGES = "create table IMAGES(" +
            "PID integer," +
            "IMAGE integer," +
            "primary key (PID, IMAGE))";

    private final String CREATE_INDEX_FOLLOW = "create index IF on FOLLOW(UID1)";

    private final String CREATE_INDEX_POST = "create index IP on POST(TIME DESC)";

    private final String CREATE_INDEX_IMAGES = "create index II on IMAGES(PID)";

    public PosterDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_FOLLOW);
        db.execSQL(CREATE_POST);
        db.execSQL(CREATE_IMAGES);
        Log.d("PosterDataBase: ", "Create succeeds");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table USER");
        db.execSQL("drop table FOLLOW");
        db.execSQL("drop table POST");
        db.execSQL("drop table IMAGES");
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_FOLLOW);
        db.execSQL(CREATE_POST);
        db.execSQL(CREATE_IMAGES);
        Log.d("PosterDataBase: ", "Update succeeds");
    }

}
