package com.cheng.recycleviewdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.cheng.recycleviewdemo.R;

/**
 * Created by asus on 2017-04-22.
 */

public class BlogFragment extends Fragment {
    private static final String BLOG = "https://03cheng.github.io";
    private View mContainView;
    private ProgressBar mProgressBar;
    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainView = inflater.inflate(R.layout.fragment_blog, container, false);

        mWebView = (WebView) mContainView.findViewById(R.id.web_view);
        mProgressBar = (ProgressBar) mContainView.findViewById(R.id.progress_bar);
        initWebView();
        mWebView.loadUrl(BLOG);
        return mContainView;
    }

    private void initWebView() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.GONE);
                        mProgressBar.setProgress(0);
                    }
                } else {
                    // 加载中
                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mProgressBar.setProgress(newProgress);
                    }
                }
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDisplayZoomControls(true);
    }
}
