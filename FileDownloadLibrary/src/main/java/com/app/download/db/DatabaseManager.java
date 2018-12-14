package com.app.download.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseManager {

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;
    private static DatabaseManager manager;
    private AtomicInteger mOpenCounter = new AtomicInteger();

    public DatabaseManager(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
    }


    public static DatabaseManager getInstance(Context context) {
        if (null == manager) {
            synchronized (DatabaseManager.class) {
                if (null == manager) {
                    manager = new DatabaseManager(context.getApplicationContext());
                }
            }
        }
        return manager;
    }

    public synchronized SQLiteDatabase open() {
        if (mOpenCounter.incrementAndGet() == 1) {
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void close() {
        if (mOpenCounter.decrementAndGet() == 0) {
            mDatabase.close();
        }
    }
}
