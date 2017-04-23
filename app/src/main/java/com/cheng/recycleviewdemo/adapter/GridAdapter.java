package com.cheng.recycleviewdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cheng.recycleviewdemo.data.Meizi;
import com.cheng.recycleviewdemo.R;

import java.util.List;

/**
 * Created by asus on 2017-03-09.
 */

public class GridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private Context context;
    private List<Meizi> datas;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view);
        void onItemLongClick(View view);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public GridAdapter(Context context, List<Meizi> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getItemViewType(int position) {
        //判断item类别，是图还是显示页数（图片有URL）
        if (!TextUtils.isEmpty(datas.get(position).getUrl())) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据item类别加载不同ViewHolder
        if (viewType == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.meizi_item, parent, false);
            MyViewHolder holder = new MyViewHolder(view);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return holder;
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.page_item, parent, false);
            MyViewHolder2 holder = new MyViewHolder2(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //将数据与item视图进行绑定，如果是MyViewHolder就加载网络图片，如果是MyViewHolder2就显示页数
        if (holder instanceof MyViewHolder) {
            Glide.with(context).load(datas.get(position).getUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).into(((MyViewHolder) holder).imageButton);
        } else if(holder instanceof MyViewHolder2){
            ((MyViewHolder2) holder).textView.setText(datas.get(position).getPage() + "页");
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemClickListener!= null) {
            mOnItemClickListener.onItemLongClick(v);
        }
        return false;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageButton imageButton;

        public MyViewHolder(View view) {
            super(view);
            imageButton = (ImageButton) view.findViewById(R.id.imageButton);
        }
    }

    private class MyViewHolder2 extends RecyclerView.ViewHolder {
        private TextView textView;

        public MyViewHolder2(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textView);
        }
    }
}
