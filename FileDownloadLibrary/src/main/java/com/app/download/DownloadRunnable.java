package com.app.download;

import android.content.Context;

import com.app.download.db.DownloadEntity;
import com.app.download.db.DownloadEntityDao;
import com.app.download.file.FileStorageManager;
import com.app.download.http.DownloadCallback;
import com.app.download.http.HttpManager;
import com.app.download.utils.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;

public class DownloadRunnable implements Runnable {
    private Context mContext;

    private long mStart;

    private long mEnd;

    private String mUrl;

    private DownloadCallback mCallback;

    private DownloadEntity mEntity;

    private RecordTask mRecordRunTask;

    public DownloadRunnable(Context context, long mStart, long mEnd, String mUrl, DownloadCallback mCallback, DownloadEntity mEntity) {
        this.mContext = context;
        this.mStart = mStart;
        this.mEnd = mEnd;
        this.mUrl = mUrl;
        this.mCallback = mCallback;
        this.mEntity = mEntity;
        this.mRecordRunTask = new RecordTask();
    }


    @Override
    public void run() {

        HttpURLConnection connection = HttpManager.getInstance().requestByRange(mUrl, mStart, mEnd);
        if (connection == null || !isSuccessful(connection)) {
            if (mCallback != null) {
                mCallback.fail(HttpManager.NETWORK_ERROR_CODE, "network quest fail!");
            }
            return;
        }

        // Start a thread to record download progress
        new Thread(mRecordRunTask).start();

        File file = FileStorageManager.getFileByName(mContext, mUrl);

        long finshProgress = mEntity.getProgress_position() == null ? 0 : mEntity.getProgress_position();
        long progress = 0;
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(mStart);
            byte[] buffer = new byte[1024 * 1024];
            int len;
            InputStream inStream = connection.getInputStream();
            while ((len = inStream.read(buffer, 0, buffer.length)) != -1) {
                randomAccessFile.write(buffer, 0, len);
                progress += len;
                mEntity.setProgress_position(progress + finshProgress);
                Logger.info("yuong", "time : "+System.currentTimeMillis()+" thread : " + mEntity.getThread_id() + "  progress  ----->" + progress);
            }
            //mEntity.setProgress_position(mEntity.getProgress_position() + finshProgress);
            randomAccessFile.close();
            mRecordRunTask.setRun(false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mRecordRunTask.setRun(false);
            DownloadEntityDao.getInstance(mContext).update(mEntity);
        }
    }

    private boolean isSuccessful(HttpURLConnection httpURLConnection) {
        int response = 0;
        if (httpURLConnection != null) {
            try {
                response = httpURLConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return (response / 100) == 2;
    }

    private class RecordTask implements Runnable {
        private boolean isRunning = true;

        @Override
        public void run() {
            while (isRunning) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DownloadEntityDao.getInstance(mContext).update(mEntity);
            }

        }

        public void setRun(boolean isRunning) {
            this.isRunning = isRunning;
        }
    }
}
