package com.app.download.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "download.db";
    private static final int DATABASE_VERSION = 1;

    //数据库表
    public static final String TABLE_DOWNLOAD = "t_download";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String sql = "create table if not exists " + TABLE_DOWNLOAD + " (id Integer primary key autoincrement,thread_id Integer,start_position Long,end_position Long,progress_position Long,download_url varchar)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
