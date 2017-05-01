package com.cheng.recycleviewdemo.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;

import com.cheng.recycleviewdemo.R;
import com.cheng.recycleviewdemo.adapter.MyPageAdapter;
import com.cheng.recycleviewdemo.data.Meizi;
import com.cheng.recycleviewdemo.service.DownloadService;
import com.cheng.recycleviewdemo.view.MyViewPager;

import java.util.List;

import static com.cheng.recycleviewdemo.util.ConstUtil.POSITION;

public class PhotoViewActivity extends AppCompatActivity implements View.OnClickListener {
    private CoordinatorLayout rootLayout;
    private MyViewPager mViewPager;
    private ImageButton collect;
    private ImageButton download;
    List<Meizi> meizis;

    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview);

        rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
        mViewPager = (MyViewPager) findViewById(R.id.view_pager);
        collect = (ImageButton) findViewById(R.id.btn_collect);
        collect.setOnClickListener(this);
        download = (ImageButton) findViewById(R.id.btn_download);
        download.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            int position = bundle.getInt(POSITION, 0);
            meizis = intent.getParcelableArrayListExtra("Meizis");
            int page = position / 11;
            for (int i = 0; i < meizis.size(); i++) {
                if (TextUtils.isEmpty(meizis.get(i).getUrl())) {
                    meizis.remove(i);
                }
            }
            MyPageAdapter adapter = new MyPageAdapter(meizis);
            mViewPager.setAdapter(adapter);
            adapter.setOnPhotoViewItemClickListener(new MyPageAdapter.OnPhotoViewItemClickListener() {
                @Override
                public void onItemClick(View view) {
                    Intent i = new Intent();
                    i.putExtra("position", mViewPager.getCurrentItem() / 10 + mViewPager.getCurrentItem());
                    setResult(RESULT_OK, i);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
            mViewPager.setCurrentItem(position - page);

            Intent serviceIntent = new Intent(this, DownloadService.class);
            startService(serviceIntent);//启动服务
            bindService(serviceIntent, connection, BIND_AUTO_CREATE);//绑定服务
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
    }

    @Override
    public void onClick(View v) {
        if (downloadBinder == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_collect:
                Snackbar.make(rootLayout, "收藏成功" + mViewPager.getCurrentItem(), Snackbar.LENGTH_SHORT).setAction("撤销", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(rootLayout, "已取消收藏", Snackbar.LENGTH_SHORT).show();
                    }
                }).show();
                break;
            case R.id.btn_download:
                if (ContextCompat.checkSelfPermission(PhotoViewActivity.this, Manifest.
                        permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PhotoViewActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    String url = meizis.get(mViewPager.getCurrentItem()).getUrl();
                    downloadBinder.startDownload(url);
                    Snackbar.make(rootLayout, "图片已保存至" + mViewPager.getCurrentItem(), Snackbar.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(rootLayout, "无法获取存储权限", Snackbar.LENGTH_SHORT).show();
                } else {
                    String url = meizis.get(mViewPager.getCurrentItem()).getUrl();
                    downloadBinder.startDownload(url);
                    Snackbar.make(rootLayout, "图片已保存至" + mViewPager.getCurrentItem(), Snackbar.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
