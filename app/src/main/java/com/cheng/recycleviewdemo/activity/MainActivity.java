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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private CoordinatorLayout rootLayout;
    private NavigationView mNavigationView;
    private boolean backPressed;
    private int mCurrentUIIndex = 0;
    private static final int INDEX_HOME = 0;
    private static final int INDEX_BLOG = 1;
    private static final int INDEX_COLLECTION = 2;
    Fragment mHomeFragment;
    Fragment mBlogFragment;
    Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(MainActivity.this,
                mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);//自动为toolbar添加图标
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();//实现箭头和三条杠图案切换和抽屉拉合的同步

        rootLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        final View mHeadViewContainer = mNavigationView.getHeaderView(0);
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
        getSupportActionBar().show();
        switch (mCurrentUIIndex) {
            case INDEX_HOME:
                if (mHomeFragment == null) {
                    mHomeFragment = new MeiziListFragment();
                }
                getSupportActionBar().setTitle("妹子");
                switchFragment(mHomeFragment);
                break;
            case INDEX_BLOG:
                if (mBlogFragment == null) {
                    mBlogFragment = new BlogFragment();
                }
                getSupportActionBar().hide();
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
            case R.id.nav_blog:
                mCurrentUIIndex = INDEX_BLOG;
                break;
            case R.id.nav_collect:
                mCurrentUIIndex = INDEX_COLLECTION;
                break;
            default:
                break;
        }
        updateUI();
        return false;
    }

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
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragment == mBlogFragment) {
            WebView webView = (WebView) fragment.getView().findViewById(R.id.web_view);
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
        Snackbar.make(rootLayout, "确定要离开？", Snackbar.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressed = false;
            }
        }, 2000);
    }
}
