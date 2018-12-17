package com.yuong.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.app.download.DownloadManager;
import com.app.download.file.FileStorageManager;
import com.app.download.http.DownloadCallback;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    //private static String DOWN_LOAD_URL = "http://img.pconline.com.cn/images/photoblog/5/9/2/0/592000/20067/19/1153275823279.jpg";
    private static String DOWN_LOAD_URL = "https://alissl.ucdl.pp.uc.cn/fs08/2018/12/06/1/110_9bffb871854efeadec8a27ef005d9f76.apk?appid=7758886&packageid=700466624&md5=83d297ed314ae8d2403f8071d3cdfa04&apprd=7758886&pkg=com.adot.mining&vcode=400&fname=挖客&iconUrl=http%3A%2F%2Fandroid-artworks%2E25pp%2Ecom%2Ffs08%2F2018%2F12%2F11%2F5%2F110_48d5a88d89bfb65fe32808790e1f5006_con%2Epng&pos=wdj_web%2Fdetail_normal_dl%2F0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String path = FileStorageManager.getRootPath(this);
        System.out.println("path :  " + path);
        findViewById(R.id.bt_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DownloadManager.getInstance().download(MainActivity.this, DOWN_LOAD_URL, new DownloadCallback() {
                            @Override
                            public void success(File file) {
                                Log.e("yuandong", "file ： " + file.getAbsolutePath() + "  size :" + file.length());
                            }

                            @Override
                            public void fail(int errorCode, String errorMessage) {

                            }

                            @Override
                            public void progress(double progress) {
                                Log.e("yuandong", "下载进度 ： " + progress);
                            }
                        });

                        // HttpManager.getInstance().request(MainActivity.this, DOWN_LOAD_URL);
                        //HttpManager.getInstance().requestByRange(MainActivity.this, DOWN_LOAD_URL,0,100*1024);
                    }
                }).start();
            }
        });
    }


//    private void test(){
//        while(){
//
//        }
//    }
}
