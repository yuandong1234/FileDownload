package com.app.download.file;

import android.content.Context;
import android.os.Environment;

import com.app.download.utils.Md5Util;

import java.io.File;
import java.io.IOException;

public class FileStorageManager {

    /**
     * 检查是否存SD卡
     */
    public static boolean isSupportSdcard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED)
                || !Environment.isExternalStorageRemovable();
    }

    /**
     * 获得文件储存根路径
     */
    public static String getRootPath(Context context) {
        String path = "";
        if (isSupportSdcard()) {
            //外部存储手机应用私有目录
            path = context.getExternalFilesDir(null).getPath();// /storage/emulated/0/Android/data/包命/files
        } else {
            //注意：内部存储没有root的手机不能打开该文件夹的
            //内部存储应用私有目录
            path = context.getFilesDir().getPath();//  /data/user/0/包命/files
        }
        return path;
    }


    /**
     * 创建文件
     *
     * @param context
     * @param fileName 文件名
     * @param delete   是否删除
     * @return
     */
    public static File crateFile(Context context, String fileName, boolean delete) {
        File file = null;
        try {
            file = new File(getRootPath(context), fileName);
            if (!file.exists()) {
                //如果文件不存在，先创建文件夹，再创建文件
                file.getParentFile().mkdirs();
                file.createNewFile();
            } else {
                if (delete) {
                    file.delete();
                    file.createNewFile();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File getFileByName(Context context, String url) {
        String fileName = Md5Util.generateCode(url);

        File file = new File(getRootPath(context), fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void deleteFileByName(Context context, String url) {
        String fileName = Md5Util.generateCode(url);
        File file = new File(getRootPath(context), fileName);
        if (file.exists()) {
            file.delete();
        }
    }
}
