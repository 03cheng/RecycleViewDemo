package com.cheng.recycleviewdemo;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by asus on 2017-04-19.
 * 实现大图滑动切换
 */

public class MyPageAdapter extends PagerAdapter implements View.OnClickListener {
    private List<Meizi> meizis;

    public interface OnPhotoViewItemClickListener {
        void onItemClick(View view);
    }

    private OnPhotoViewItemClickListener onPhotoViewItemClickListener = null;

    public void setOnPhotoViewItemClickListener(OnPhotoViewItemClickListener onPhotoViewItemClickListener){
        this.onPhotoViewItemClickListener = onPhotoViewItemClickListener;
    }

    public MyPageAdapter(List<Meizi> meizis) {
        this.meizis = meizis;
    }

    @Override
    public int getCount() {
        return meizis.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        final PhotoView photoView = new PhotoView(container.getContext());
        Glide.with(container.getContext()).load(meizis.get(position).getUrl()).into(photoView);
        photoView.enable();
        photoView.setOnClickListener(this);
        photoView.setAdjustViewBounds(true);
        //photoView.animaFrom();//动画
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    @Override
    public void onClick(View v) {
        if (onPhotoViewItemClickListener != null){
            onPhotoViewItemClickListener.onItemClick(v);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
