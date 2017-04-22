package com.cheng.recycleviewdemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import java.util.List;

public class PhotoViewActivity extends AppCompatActivity {
    private MyViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview);

        mViewPager = (MyViewPager) findViewById(R.id.view_pager);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            int position = bundle.getInt("position", 0);
            List<Meizi> meizis = intent.getParcelableArrayListExtra("Meizis");
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
                    finish();
                }
            });
            mViewPager.setCurrentItem(position - page);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
