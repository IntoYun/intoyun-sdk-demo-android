package com.molmc.intoyundemo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.support.db.DataPointDataBase;
import com.molmc.intoyundemo.ui.fragment.BaseFragment;
import com.molmc.intoyundemo.ui.fragment.DeviceListFragment;
import com.molmc.intoyundemo.ui.fragment.MessageFragment;
import com.molmc.intoyundemo.ui.fragment.MineFragment;
import com.molmc.intoyundemo.ui.fragment.RecipeFragment;
import com.molmc.intoyundemo.utils.AppSharedPref;
import com.molmc.intoyunsdk.bean.BoardInfoBean;
import com.molmc.intoyunsdk.bean.DataPointBean;
import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.network.ReceiveMessageListener;
import com.molmc.intoyunsdk.network.model.response.UserResult;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.molmc.intoyunsdk.utils.IntoYunSharedPrefs;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * features: 主界面
 * Author：  hhe on 16-7-29 22:27
 * Email：   hhe@molmc.com
 */

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {

    public static void launch(Activity from) {
        Intent intent = new Intent(from, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
    }

    @Bind(R.id.frameContent)
    FrameLayout frameContent;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.nav_tab)
    BottomNavigationBar bottomNavigationBar;

    private boolean canFinish = false;


    private int lastSelectedPosition = 0;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private int messageBadge = 0;

    private Map<String, BoardInfoBean> boardInfoMap;
    private BadgeItem numberBadgeItem;
    private UserResult userInfo;

    private BaseFragment[] fragments = {DeviceListFragment.newInstance(), MessageFragment.newInstance(), RecipeFragment.newInstance(), MineFragment.newInstance()};
    private String[] fragmentTags = {"DeviceListFragment", "MessageFragment", "RecipeFragment", "MineFragment"};
    private String[] titles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        titles = getResources().getStringArray(R.array.nav_bar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle(titles[0]);
        userInfo = IntoYunSharedPrefs.getUserInfo(this);
        initBottomBar();
        // 开启一个Fragment事务
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameContent, fragments[0], fragmentTags[0]).commit();
        getBoardInfo();
        registerReceiveMessage();
    }

    private void initBottomBar() {
        messageBadge = AppSharedPref.getInstance(this).getMessageBadge(userInfo.getUid());
        bottomNavigationBar.setTabSelectedListener(this);
        numberBadgeItem = new BadgeItem()
                .setBorderWidth(4)
                .setText(String.valueOf(messageBadge))
                .setBackgroundColorResource(R.color.color_red)
                .setHideOnSelect(true);
        if (messageBadge > 0) {
            numberBadgeItem.show();
        } else {
            numberBadgeItem.hide();
        }
        bottomNavigationBar
                .setMode(BottomNavigationBar.MODE_FIXED)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .addItem(new BottomNavigationItem(R.mipmap.ic_device_selected, titles[0]).setInactiveIconResource(R.mipmap.ic_device).setActiveColorResource(R.color.color_tab))
                .addItem(new BottomNavigationItem(R.mipmap.ic_message_selected, titles[1]).setInactiveIconResource(R.mipmap.ic_message).setActiveColorResource(R.color.color_tab).setBadgeItem(numberBadgeItem))
                .addItem(new BottomNavigationItem(R.mipmap.ic_recipe_selected, titles[2]).setInactiveIconResource(R.mipmap.ic_recipe).setActiveColorResource(R.color.color_tab))
                .addItem(new BottomNavigationItem(R.mipmap.ic_mine_selected, titles[3]).setInactiveIconResource(R.mipmap.ic_mine).setActiveColorResource(R.color.color_tab))
                .initialise();
    }


    private void getProducts() {
        IntoYunSdk.getProducts(new IntoYunListener<Map<String, List<DataPointBean>>>() {
            @Override
            public void onSuccess(Map<String, List<DataPointBean>> result) {
                DataPointDataBase.getInstance(MainActivity.this).saveDataPoints(result);
            }

            @Override
            public void onFail(NetError error) {
                Logger.e(error.getMessage());
                showToast(error.getMessage());
            }
        });
    }


    private void getBoardInfo(){
        IntoYunSdk.getBoardInfo(new IntoYunListener<Map<String, BoardInfoBean>>() {
            @Override
            public void onSuccess(Map<String, BoardInfoBean> result) {
                Logger.i("getBoardInfo");
                boardInfoMap = result;
                AppSharedPref.getInstance(MainActivity.this).saveBoardInfo(result);
                getProducts();
            }

            @Override
            public void onFail(NetError error) {
                Logger.e(error.getMessage());
                showToast(error.getMessage());
            }
        });
    }

    private void registerReceiveMessage() {
        IntoYunSdk.subscribeMessages(new ReceiveMessageListener() {
            @Override
            public void onReceived(String message) {
                Logger.i(message);
                messageBadge++;
                numberBadgeItem.setText(String.valueOf(messageBadge));
                numberBadgeItem.show();
                AppSharedPref.getInstance(MainActivity.this).saveMessageBadge(userInfo.getUid(), messageBadge);
            }
        });
    }


    @Override
    public boolean onBackClick() {
        if (!canFinish) {
            canFinish = true;
            showToast(R.string.comm_hint_exit);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    canFinish = false;
                }

            }, 1500);
            return true;
        }
        super.onBackClick();
        System.exit(0);
        return true;
    }

    @Override
    protected void onDestroy() {
        Logger.i("main activity onDestroy");
        super.onDestroy();
    }

    @Override
    public void onTabSelected(int position) {
        lastSelectedPosition = position;
        showFragment(position);
        if (numberBadgeItem != null) {
            if (position == 1) {
                messageBadge = 0;
                numberBadgeItem.setText(Integer.toString(0));
                numberBadgeItem.hide();
                AppSharedPref.getInstance(this).saveMessageBadge(userInfo.getUid(), messageBadge);
            } else {
                if (messageBadge == 0) {
                    numberBadgeItem.hide();
                }
            }
        }
    }

    public Fragment getCurrentFragment() {
        return fragmentManager.findFragmentByTag(fragmentTags[lastSelectedPosition]);
    }

    private void showFragment(int position) {
        fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager.findFragmentByTag(fragmentTags[position]) == null) {
            fragmentTransaction.add(R.id.frameContent, fragments[position], fragmentTags[position]);
        }
        for (int i = 0; i < fragments.length; i++) {
            if (i == position) {
                fragmentTransaction.show(fragments[position]);
            } else {
                fragmentTransaction.hide(fragments[i]);
            }
        }
        getSupportActionBar().setTitle(titles[position]);
        fragmentTransaction.commit();
        invalidateOptionsMenu();
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }
}
