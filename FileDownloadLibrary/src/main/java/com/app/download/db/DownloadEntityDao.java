package com.app.download.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.download.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class DownloadEntityDao {

    private static DownloadEntityDao downloadEntityDao;
    private DatabaseManager mManager;

    private DownloadEntityDao(Context context) {
        mManager = DatabaseManager.getInstance(context);
    }

    public static DownloadEntityDao getInstance(Context context) {
        if (null == downloadEntityDao) {
            synchronized (DownloadEntityDao.class) {
                if (null == downloadEntityDao) {
                    downloadEntityDao = new DownloadEntityDao(context.getApplicationContext());
                }
            }
        }
        return downloadEntityDao;
    }

    public void insert(DownloadEntity entity) {
        System.out.println("insert data................");
        SQLiteDatabase db = mManager.open();
        String sql = "insert into " + DatabaseHelper.TABLE_DOWNLOAD + " (thread_id,start_position,end_position,progress_position,download_url)values(?,?,?,?,?)";
        db.execSQL(sql, new Object[]{entity.getThread_id(),
                entity.getStart_position(),
                entity.getEnd_position(),
                entity.getProgress_position(),
                entity.getDownload_url()});
        mManager.close();
    }

    public void update(DownloadEntity entity) {
        System.out.println("update data................");
        SQLiteDatabase db = mManager.open();
        String sql = "update " + DatabaseHelper.TABLE_DOWNLOAD + " set progress_position = ? where thread_id = ? and download_url = ?";
        db.execSQL(sql, new Object[]{entity.getProgress_position(), entity.getThread_id(), entity.getDownload_url()});
        mManager.close();
    }

    public static void delete() {

    }

    public List<DownloadEntity> query(String url) {
        List<DownloadEntity> list = null;
        SQLiteDatabase db = mManager.open();
        String sql = "select * from " + DatabaseHelper.TABLE_DOWNLOAD + " where download_url = ? order by thread_id asc";
        Cursor cursor = db.rawQuery(sql, new String[]{url});
        while (cursor.moveToNext()) {
            DownloadEntity entity = new DownloadEntity();
            entity.setThread_id(cursor.getInt(cursor.getColumnIndex("thread_id")));
            entity.setStart_position(cursor.getLong(cursor.getColumnIndex("start_position")));
            entity.setEnd_position(cursor.getLong(cursor.getColumnIndex("end_position")));
            entity.setProgress_position(cursor.getLong(cursor.getColumnIndex("progress_position")));
            entity.setDownload_url(cursor.getString(cursor.getColumnIndex("download_url")));
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(entity);
        }
        Logger.info("yuong", "list : " + (list == null ? "null" : list.toString()));
        cursor.close();
        mManager.close();
        return list;
    }

}
