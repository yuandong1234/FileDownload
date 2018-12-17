package com.app.download;

import android.content.Context;

import com.app.download.db.DownloadEntity;
import com.app.download.db.DownloadEntityDao;
import com.app.download.file.FileStorageManager;
import com.app.download.http.DownloadCallback;
import com.app.download.http.HttpManager;
import com.app.download.utils.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadManager {
    public final static int MAX_THREAD = 3;
    public final static int LOCAL_PROGRESS_SIZE = 1;

    private static ExecutorService sLocalProgressPool = Executors.newFixedThreadPool(LOCAL_PROGRESS_SIZE);
    private static ThreadPoolExecutor sThreadPool = new ThreadPoolExecutor(MAX_THREAD, MAX_THREAD, 60, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(), new ThreadFactory() {

        private AtomicInteger mInteger = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "download thread #" + mInteger.getAndIncrement());
            return thread;
        }
    });

    private HashSet<DownloadTask> mHashSet = new HashSet<>();
    private long mLength;

    private DownloadManager() {

    }

    private static class Holder {
        private static DownloadManager sManager = new DownloadManager();
    }

    public static DownloadManager getInstance() {
        return Holder.sManager;
    }

    private void finish(DownloadTask task) {
        mHashSet.remove(task);
    }

    public void download(final Context context, final String url, final DownloadCallback callback) {
        final DownloadTask task = new DownloadTask(url, callback);
        if (mHashSet.contains(task)) {
            if (callback != null) {
                callback.fail(HttpManager.TASK_RUNNING_ERROR_CODE, "The task has already been running!");
            }
            return;
        }
        mHashSet.add(task);

        //查询数据库，是否之前已下载
        List<DownloadEntity> mCache = DownloadEntityDao.getInstance(context).query(url);

        if (mCache == null || mCache.size() == 0) {
            mLength = HttpManager.getInstance().getFileSize(url, callback);
            if (mLength == 0) {
                finish(task);
                return;
            }
            if (mLength == -1) {
                callback.fail(HttpManager.CONTENT_LENGTH_ERROR_CODE, "content length -1");
                finish(task);
                return;
            }
            FileStorageManager.deleteFileByName(context, url);//In case a file with the same name exists
            processDownload(context, url, mLength, callback);
        } else {
            for (int i = 0; i < mCache.size(); i++) {
                DownloadEntity entity = mCache.get(i);
                if (i == mCache.size() - 1) {
                    mLength = entity.getEnd_position() + 1;
                }
                long startSize = entity.getStart_position() + entity.getProgress_position();
                long endSize = entity.getEnd_position();
                sThreadPool.execute(new DownloadRunnable(context, startSize, endSize, url, callback, entity));
            }
        }

        sLocalProgressPool.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(200);
                        File file = FileStorageManager.getFileByName(context, url);
                        long fileSize = file.length();
                        double progress = fileSize * 100d / mLength;
                        if (progress >= 100) {
                            Logger.info("yuong","结束时间：" + System.currentTimeMillis());
                            callback.progress(progress);
                            return;
                        }
                        callback.progress(progress);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void processDownload(Context context, String url, long length, DownloadCallback callback) {
        // 100   2  50  0-49  50-99
        System.out.println("开始时间：" + System.currentTimeMillis());
        long threadDownloadSize = length / MAX_THREAD;
        for (int i = 0; i < MAX_THREAD; i++) {
            DownloadEntity entity = new DownloadEntity();
            long startSize = i * threadDownloadSize;
            long endSize = 0;
            if (i == MAX_THREAD - 1) {
                endSize = length - 1;
            } else {
                endSize = (i + 1) * threadDownloadSize - 1;
            }

            entity.setDownload_url(url);
            entity.setStart_position(startSize);
            entity.setEnd_position(endSize);
            entity.setThread_id(i + 1);
            DownloadEntityDao.getInstance(context).insert(entity);//insert a record
            sThreadPool.execute(new DownloadRunnable(context, startSize, endSize, url, callback, entity));
        }

    }
}
