package com.app.download;

import com.app.download.http.DownloadCallback;

import java.util.Objects;

public class DownloadTask {
    private String mUrl;
    private DownloadCallback mCallback;

    public DownloadTask(String mUrl, DownloadCallback mCallback) {
        this.mUrl = mUrl;
        this.mCallback = mCallback;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public DownloadCallback getCallback() {
        return mCallback;
    }

    public void setCallback(DownloadCallback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownloadTask task = (DownloadTask) o;

        if (mUrl != null ? !mUrl.equals(task.mUrl) : task.mUrl != null) return false;
        return !(mCallback != null ? !mCallback.equals(task.mCallback) : task.mCallback != null);

    }

    @Override
    public int hashCode() {
        int result = mUrl != null ? mUrl.hashCode() : 0;
        result = 31 * result + (mCallback != null ? mCallback.hashCode() : 0);
        return result;
    }
}
