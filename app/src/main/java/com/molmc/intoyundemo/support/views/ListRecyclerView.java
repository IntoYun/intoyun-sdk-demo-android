package com.molmc.intoyundemo.support.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * features: recycler list view
 * Author：  hhe on 16-8-03 16:00
 * Email：   hhe@molmc.com
 */

public class ListRecyclerView extends RecyclerView {

    public ListRecyclerView(Context context) {
        this(context, null, 0);
    }

    public ListRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setItemAnimator(new DefaultItemAnimator());// Fix update progress falsh.
        addItemDecoration(new ListRecyclerItemDecoration());
        setLayoutManager(new GridLayoutManager(getContext(), 2));
//        setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
