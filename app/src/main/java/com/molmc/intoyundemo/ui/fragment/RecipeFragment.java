package com.molmc.intoyundemo.ui.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.molmc.intoyunsdk.bean.RecipeBean;
import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.support.adapter.RecipeAdapter;
import com.molmc.intoyundemo.support.eventbus.UpdateRecipe;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehui on 17/3/21.
 */

public class RecipeFragment extends BaseRefreshFragment implements SwipeRefreshLayout.OnRefreshListener, IntoYunListener<List<RecipeBean>> {

    public static final String TAG = "RecipeFragment";


    public static RecipeFragment newInstance() {
        RecipeFragment fragment = new RecipeFragment();
        return fragment;
    }

    private RecipeAdapter recipeAdapter;
    private List<RecipeBean> recipes = new ArrayList<>();


    @Override
    protected void doURV(UltimateRecyclerView urv) {
        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);

        recipeAdapter = new RecipeAdapter(getActivity(), recipes);

        configGridLayoutManager(ultimateRecyclerView, recipeAdapter);
        ultimateRecyclerView.setHasFixedSize(true);
        ultimateRecyclerView.setDefaultOnRefreshListener(this);
        enableEmptyViewPolicy();
        ultimateRecyclerView.setAdapter(recipeAdapter);
        requestData();
    }

    private void requestData() {
        IntoYunSdk.getRecipes(this);
    }

    @Override
    protected void onLoadmore() {

    }

    @Override
    protected void onFireRefresh() {

    }

    @Override
    public void onRefresh() {
        requestData();
    }

    @Override
    public void onSuccess(List<RecipeBean> result) {
        recipes = result;
        recipeAdapter.setData(recipes);
    }

    @Override
    public void onFail(NetError error) {
        Logger.i(error.getMessage());
        showToast(error.getMessage());
        finishRefresh();
    }

    @Subscribe
    public void onEventMainThread(UpdateRecipe event) {
        if ("refresh".equals(event.getType())) {
            onRefresh();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add){
            SelectTriggerFragment.launch(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
