package com.app.download.http;

import android.content.Context;

import com.app.download.file.FileStorageManager;
import com.app.download.utils.Md5Util;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpManager {
    public static final int NETWORK_ERROR_CODE = 1;
    public static final int CONTENT_LENGTH_ERROR_CODE = 2;
    public static final int TASK_RUNNING_ERROR_CODE = 3;

    private HttpManager() {
    }

    private static class Holder {
        private static final HttpManager sManager = new HttpManager();
    }

    public static HttpManager getInstance() {
        return Holder.sManager;
    }

//    public HttpURLConnection request(Context context, String fileurl) {
//        System.out.println("start download........");
//        HttpURLConnection mConnection = null;
//        try {
//            URL url = new URL(fileurl);
//            mConnection = (HttpURLConnection) url.openConnection();
//            mConnection.setConnectTimeout(5000);
//            mConnection.setReadTimeout(5000);
//            mConnection.setDoInput(true);
//            mConnection.setRequestMethod("GET");
//            mConnection.connect();
//
//            if (mConnection.getResponseCode() == 200) {
//                System.out.println("文件大小 ： " + mConnection.getContentLength());
//                File file = FileStorageManager.crateFile(context, Md5Util.generateCode(fileurl), true);
//                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
//                // randomAccessFile.seek(mStart);
//                byte[] buffer = new byte[1024 * 500];
//                int len;
//                InputStream inStream = mConnection.getInputStream();
//                while ((len = inStream.read(buffer, 0, buffer.length)) != -1) {
//                    randomAccessFile.write(buffer, 0, len);
//                }
//            }
//            mConnection.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return mConnection;
//    }

    public long getFileSize(String downloadUrl, DownloadCallback callback) {
        long mLength = 0;
        try {
            URL url = new URL(downloadUrl);
            HttpURLConnection mConnection = (HttpURLConnection) url.openConnection();
            mConnection.setConnectTimeout(5000);
            mConnection.setReadTimeout(5000);
            mConnection.setDoInput(true);
            mConnection.setRequestMethod("GET");
            mConnection.connect();

            if (mConnection.getResponseCode() == 200) {
                System.out.println("file size ： " + mConnection.getContentLength());
                mLength = mConnection.getContentLength();
            }
            mConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.fail(NETWORK_ERROR_CODE, e.getMessage());
            }
        }
        return mLength;
    }

//    public void requestByRange(Context context, String fileurl, long start, long end) {
//        System.out.println("start download........");
//        HttpURLConnection mConnection = null;
//        try {
//            URL url = new URL(fileurl);
//            mConnection = (HttpURLConnection) url.openConnection();
//            mConnection.setConnectTimeout(5000);
//            mConnection.setReadTimeout(5000);
//            mConnection.setDoInput(true);
//            mConnection.setRequestMethod("GET");
//            mConnection.addRequestProperty("range", "bytes=" + start + "-" + end);
//            mConnection.connect();
//
//            System.out.println(mConnection.getResponseCode());
//            if (mConnection.getResponseCode() / 100 == 2) {
//                System.out.println("文件大小 ： " + mConnection.getContentLength());
//                File file = FileStorageManager.crateFile(context, Md5Util.generateCode(fileurl), true);
//                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
//                // randomAccessFile.seek(mStart);
//                byte[] buffer = new byte[1024 * 500];
//                int len;
//                InputStream inStream = mConnection.getInputStream();
//                while ((len = inStream.read(buffer, 0, buffer.length)) != -1) {
//                    randomAccessFile.write(buffer, 0, len);
//                }
//            }
//            mConnection.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public HttpURLConnection requestByRange(String fileurl, long start, long end) {
        System.out.println("start download  ------> start : " + start + " end : " + end);
        HttpURLConnection mConnection = null;
        try {
            URL url = new URL(fileurl);
            mConnection = (HttpURLConnection) url.openConnection();
            mConnection.setConnectTimeout(5000);
            mConnection.setReadTimeout(5000);
            mConnection.setDoInput(true);
            mConnection.setRequestMethod("GET");
            mConnection.addRequestProperty("range", "bytes=" + start + "-" + end);
            mConnection.connect();
        } catch (Exception e) {
            e.printStackTrace();
            if (mConnection != null) {
                mConnection.disconnect();
            }
            return null;
        }
        return mConnection;
    }
}
