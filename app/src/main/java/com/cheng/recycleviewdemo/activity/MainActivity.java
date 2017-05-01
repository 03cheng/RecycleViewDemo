package com.cheng.recycleviewdemo.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cheng.recycleviewdemo.R;
import com.cheng.recycleviewdemo.fragment.BlogFragment;
import com.cheng.recycleviewdemo.fragment.MeiziListFragment;
import com.cheng.recycleviewdemo.util.BlurImageUtils;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private boolean backPressed;
    private int mCurrentUIIndex = 0;
    private static final int INDEX_HOME = 0;
    private static final int INDEX_COLLECTION = 1;
    private static final int INDEX_BLOG = 2;
    Fragment mHomeFragment;
    Fragment mBlogFragment;
    Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        final View mHeadViewContainer = mNavigationView.getHeaderView(0);//获取侧滑栏的头部
        final CircleImageView mheaderimage = (CircleImageView) mHeadViewContainer.findViewById(R.id.headerImage);
        Glide.with(this)
                .load("http://03cheng.github.io/images/avatar.jpg")
                .asBitmap()
                .centerCrop()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mheaderimage.setImageBitmap(resource);
                        Bitmap overlay = BlurImageUtils.blur(mheaderimage, 3, 3);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mHeadViewContainer.setBackground(new BitmapDrawable(getResources(), overlay));
                        }
                    }
                });

        final TextView mUri = (TextView) mHeadViewContainer.findViewById(R.id.tv_uri);
        mUri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUri.getText().toString()));
                startActivity(intent);
            }
        });
        updateUI();
    }

    private void updateUI() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        switch (mCurrentUIIndex) {
            case INDEX_HOME:
                if (mHomeFragment == null) {
                    mHomeFragment = new MeiziListFragment();
                }
                switchFragment(mHomeFragment);
                break;
            case INDEX_BLOG:
                if (mBlogFragment == null) {
                    mBlogFragment = new BlogFragment();
                }
                switchFragment(mBlogFragment);
                break;
            case INDEX_COLLECTION:

            default:
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.nav_home:
                mCurrentUIIndex = INDEX_HOME;
                break;
            case R.id.nav_collect:
                mCurrentUIIndex = INDEX_COLLECTION;
                break;
            case R.id.nav_blog:
                mCurrentUIIndex = INDEX_BLOG;
                break;
            default:
                break;
        }
        updateUI();
        return false;
    }

    /**
     * 切换Fragment
     */
    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentFragment != null) {
            fragmentTransaction.hide(mCurrentFragment);
        }
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.add(R.id.content, fragment);
        }
        fragmentTransaction.commit();
        mCurrentFragment = fragment;
    }

    /**
     * 监听返回键
     * 判断是否为WebView，保证网页之间的正常跳转
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (mCurrentFragment == mBlogFragment) {
            WebView webView = (WebView) mCurrentFragment.getView().findViewById(R.id.web_view);
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                doublePressBackToQuit();
            }
        } else {
            doublePressBackToQuit();
        }
    }

    /**
     * 双击返回键退出应用
     */
    private void doublePressBackToQuit() {
        if (backPressed) {
            super.onBackPressed();
        }
        backPressed = true;
        CoordinatorLayout rootLayout = (CoordinatorLayout) mCurrentFragment.getView().findViewById(R.id.coordinatorLayout);
        Snackbar.make(rootLayout, "确定要离开？", Snackbar.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressed = false;
            }
        }, 2000);
    }

}
