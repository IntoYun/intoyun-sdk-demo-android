package com.molmc.intoyundemo.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.ObservableScrollState;
import com.marshalchen.ultimaterecyclerview.ObservableScrollViewCallbacks;
import com.marshalchen.ultimaterecyclerview.URLogs;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.layoutmanagers.ClassicSpanGridLayoutManager;
import com.marshalchen.ultimaterecyclerview.layoutmanagers.ScrollSmoothLineaerLayoutManager;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.molmc.intoyundemo.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehui on 17/3/22.
 */

public abstract class BaseRefreshFragment extends BaseFragment {


    protected boolean isDrag = true;
    protected boolean isEnableAutoLoadMore = true;
    protected boolean status_progress = false;

    @Bind(R.id.recycler_view)
    protected UltimateRecyclerView ultimateRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lay_base_refresh, container, false);
        ButterKnife.bind(this, view);
        doURV(ultimateRecyclerView);
        return view;
    }

    protected abstract void doURV(UltimateRecyclerView urv);

    protected abstract void onLoadmore();

    protected abstract void onFireRefresh();

    protected void enableRefresh() {

    }

    protected void enableLoadMore() {
        ultimateRecyclerView.setLoadMoreView(R.layout.custom_bottom_progressbar);

        ultimateRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
                status_progress = true;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        onLoadmore();
                        status_progress = false;
                    }
                }, 500);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    protected final void configStaggerLayoutManager(UltimateRecyclerView rv, easyRegularAdapter ad) {
        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(gaggeredGridLayoutManager);
    }

    protected final void configGridLayoutManager(UltimateRecyclerView rv, easyRegularAdapter ad) {
        final ClassicSpanGridLayoutManager mgm = new ClassicSpanGridLayoutManager(getActivity(), 2, ad);
        rv.setLayoutManager(mgm);
    }

    protected final void configLinearLayoutManager(UltimateRecyclerView rv) {
        final ScrollSmoothLineaerLayoutManager mgm = new ScrollSmoothLineaerLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false, 300);
        rv.setLayoutManager(mgm);
    }

    protected void enableEmptyViewPolicy() {
        ultimateRecyclerView.setEmptyView(R.layout.lay_empty, UltimateRecyclerView.EMPTY_CLEAR_ALL);
    }
    protected final void enableScrollControl() {
        ultimateRecyclerView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
                URLogs.d("onScrollChanged: " + dragging);
            }

            @Override
            public void onDownMotionEvent() {

            }

            @Override
            public void onUpOrCancelMotionEvent(ObservableScrollState observableScrollState) {
                URLogs.d("onUpOrCancelMotionEvent");
            }
        });

        ultimateRecyclerView.showFloatingButtonView();
    }

    protected void enableSwipe() {

    }

    protected void finishRefresh(){
        ultimateRecyclerView.setRefreshing(false);
    }

}
