package com.molmc.intoyundemo.support.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * features:
 * Author：  hhe on 16-8-3 16:01
 * Email：   hhe@molmc.com
 */

public class ListRecyclerItemDecoration extends RecyclerView.ItemDecoration {

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		outRect.set(5, 5, 5, 5);
	}
}
