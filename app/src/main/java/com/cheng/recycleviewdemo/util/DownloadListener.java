package com.cheng.recycleviewdemo.util;

/**
 * Created by asus on 2017-04-23.
 * 下载回调接口，用于对下载过程中的各种状态的监听和回调
 */

public interface DownloadListener {
    void onProgress(int progress);
    void onSuccess();
    void onFailed();
    void onPaused();
    void onCanceled();
}
