package com.cheng.recycleviewdemo.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.cheng.recycleviewdemo.R;
import com.cheng.recycleviewdemo.activity.PhotoViewActivity;
import com.cheng.recycleviewdemo.adapter.GridAdapter;
import com.cheng.recycleviewdemo.data.Meizi;
import com.cheng.recycleviewdemo.util.MyOkhttp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.cheng.recycleviewdemo.util.ConstUtil.MEIZI_BASE_URL;
import static com.cheng.recycleviewdemo.util.ConstUtil.POSITION;

/**
 * Created by asus on 2017-04-22.
 */

public class MeiziListFragment extends Fragment {
    private View mContainView;
    private RecyclerView recyclerView;
    private GridAdapter mAdapter;
    private List<Meizi> meizis;
    private GridLayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int page = 1;
    private int lastVisibleItem;
    private ItemTouchHelper itemTouchHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainView = inflater.inflate(R.layout.fragment_meizilist, container, false);
        initView();
        setListener();
        new GetData().execute(MEIZI_BASE_URL + page);
        return mContainView;
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) mContainView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(getActivity(),
                mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);//自动为toolbar添加图标
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();//实现箭头和三条杠图案切换和抽屉拉合的同步
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("妹子");

        recyclerView = (RecyclerView) mContainView.findViewById(R.id.recyclerView);
        mLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayout.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) mContainView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        Resources resources = getResources();
        int[] SCHEME_COLORS = {resources.getColor(R.color.blue_dark),
                resources.getColor(R.color.red_dark),
                resources.getColor(R.color.yellow_dark),
                resources.getColor(R.color.green_dark)};
        swipeRefreshLayout.setColorSchemeColors(SCHEME_COLORS);
    }

    private void setListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                meizis = null;
                mAdapter = null;
                page = 1;
                new GetData().execute(MEIZI_BASE_URL + page);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //0：当前屏幕停止滚动；1时：屏幕在滚动 且 用户仍在触碰或手指还在屏幕上；2时：随用户的操作，屏幕上产生的惯性滑动；
                // 滑动状态停止并且剩余少于两个item时，自动加载下一页
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 2 >= mLayoutManager.getItemCount()) {
                    new GetData().execute(MEIZI_BASE_URL + (++page));
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //获取加载的最后一个可见视图在适配器的位置。
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });

        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = 0;
                if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager || recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                }
                return makeMovementFlags(dragFlags, 0);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                Meizi moveItem = meizis.get(from);
                meizis.remove(from);
                meizis.add(to, moveItem);
                mAdapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        });
    }

    /**
     * 根据查看大图的position滚动RecycleView至指定位置
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == 100) {
                    int position = data.getIntExtra(POSITION, 0);
                    recyclerView.scrollToPosition(position + 2);
                }
                break;
        }
    }

    private class GetData extends AsyncTask<String, Integer, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            //设置swipeRefreshLayout为刷新状态
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            return MyOkhttp.get(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!TextUtils.isEmpty(result)) {

                JSONObject jsonObject;
                Gson gson = new Gson();
                String jsonData = null;

                try {
                    jsonObject = new JSONObject(result);
                    jsonData = jsonObject.getString("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (meizis == null || meizis.size() == 0) {
                    meizis = gson.fromJson(jsonData, new TypeToken<List<Meizi>>() {
                    }.getType());

                    Meizi pages = new Meizi();
                    pages.setPage(page);
                    meizis.add(pages);//在数据链表中加入一个用于显示页数的item
                } else {
                    List<Meizi> more = gson.fromJson(jsonData, new TypeToken<List<Meizi>>() {
                    }.getType());
                    meizis.addAll(more);

                    Meizi pages = new Meizi();
                    pages.setPage(page);
                    meizis.add(pages);//在数据链表中加入一个用于显示页数的item
                }

                if (mAdapter == null) {
                    recyclerView.setAdapter(mAdapter = new GridAdapter(getActivity(), meizis));//recyclerview设置适配器

                    //实现适配器自定义的点击监听
                    mAdapter.setOnItemClickListener(new GridAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View view) {
                            int position = recyclerView.getChildAdapterPosition(view);
                            Intent intent = new Intent(getActivity(), PhotoViewActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("Meizis", (ArrayList<Meizi>) meizis);
                            bundle.putInt(POSITION, position);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, 100);
                            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }

                        @Override
                        public void onItemLongClick(View view) {
                            itemTouchHelper.startDrag(recyclerView.getChildViewHolder(view));
                        }
                    });
                    itemTouchHelper.attachToRecyclerView(recyclerView);
                } else {
                    //让适配器刷新数据
                    mAdapter.notifyDataSetChanged();
                }
            }
            //停止swipeRefreshLayout加载动画
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
