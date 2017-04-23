package com.molmc.intoyundemo.support.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by hehui on 17/3/27.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mListViews;


    public ViewPagerAdapter(FragmentManager fragmentManager, List<Fragment> mListViews) {
        super(fragmentManager);
        this.mListViews = mListViews;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public Fragment getItem(int position) {
        return mListViews.get(position);
    }

    @Override
    public int getCount() {
        return mListViews.size();
    }



}
