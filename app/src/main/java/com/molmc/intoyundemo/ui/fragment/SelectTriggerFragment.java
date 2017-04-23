package com.molmc.intoyundemo.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.layoutmanagers.ScrollSmoothLineaerLayoutManager;
import com.molmc.intoyunsdk.bean.DataPointBean;
import com.molmc.intoyunsdk.bean.DeviceBean;
import com.molmc.intoyunsdk.bean.RecipeBean;
import com.molmc.intoyunsdk.utils.IntoUtil;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.bean.FragmentArgs;
import com.molmc.intoyundemo.support.ClickGridItemListener;
import com.molmc.intoyundemo.support.ClickListItemListener;
import com.molmc.intoyundemo.support.adapter.DataPointAdapter;
import com.molmc.intoyundemo.support.adapter.ViewPagerAdapter;
import com.molmc.intoyundemo.support.db.DataPointDataBase;
import com.molmc.intoyundemo.support.db.DeviceDataBase;
import com.molmc.intoyundemo.ui.activity.BaseActivity;
import com.molmc.intoyundemo.ui.activity.FragmentCommonActivity;
import com.molmc.intoyundemo.utils.Constant;
import com.molmc.intoyundemo.utils.Utils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehui on 17/3/27.
 */

public class SelectTriggerFragment extends BaseFragment implements ClickGridItemListener, ViewPager.OnPageChangeListener, ClickListItemListener {


    public static SelectTriggerFragment newInstance() {
        SelectTriggerFragment fragment = new SelectTriggerFragment();
        return fragment;
    }

    public static void launch(Activity from) {
        FragmentArgs args = new FragmentArgs();
        FragmentCommonActivity.launch(from, SelectTriggerFragment.class, args);
    }

    @Bind(R.id.device_select_viewpager)
    ViewPager deviceSelectViewpager;
    @Bind(R.id.dataPointRecycler)
    UltimateRecyclerView dataPointRecycler;

    private List<DeviceBean> deviceList;
    private List<Fragment> fragmentList = new ArrayList<>();
    private ViewPagerAdapter mViewPagerAdapter;
    private DataPointAdapter mDataPointAdapter;
    private List<DataPointBean> dataPoints = new ArrayList<>();
    private RecipeBean createRecipe;
    private DeviceBean selectedDevice;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_recipe, container, false);
        ButterKnife.bind(this, view);
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.getSupportActionBar().setTitle(R.string.recipe_select_trigger_title);
        setHasOptionsMenu(false);
        initView();
        return view;
    }

    private void initView() {
        deviceList = filterDevice();
        deviceList.add(0, Utils.SYSTEM_DEVICE(getActivity()));
        Logger.i(new Gson().toJson(deviceList));
        if (!IntoUtil.Empty.check(deviceList)) {
            Logger.i("length: " + Math.ceil(deviceList.size() / 6.0f));
            Logger.i("length: " + Math.floor(deviceList.size() / 6.0f));
            for (int i = 0; i < Math.ceil(deviceList.size() / 6.0f); i++) {
                int index = i * 6;
                int end = deviceList.size();
                if (index + 6 < deviceList.size()) {
                    end = index + 6;
                }
                GridFragment gridFragment = GridFragment.newInstance(deviceList.subList(index, end));
                gridFragment.setOnClickGridItemListener(this);
                fragmentList.add(gridFragment);
            }
            dataPoints = filterDataPoint(deviceList.get(0).getPidImp());
        }

        configLinearLayoutManager(dataPointRecycler);
        dataPointRecycler.setHasFixedSize(true);
        dataPointRecycler.disableLoadmore();

        mViewPagerAdapter = new ViewPagerAdapter(getFragmentManager(), fragmentList);
        deviceSelectViewpager.setAdapter(mViewPagerAdapter);
        deviceSelectViewpager.addOnPageChangeListener(this);

        mDataPointAdapter = new DataPointAdapter(getActivity(), dataPoints);
        mDataPointAdapter.setClickListItemListener(this);
        dataPointRecycler.setAdapter(mDataPointAdapter);
        selectedDevice = deviceList.get(0);
    }

    private void initCreateRecipe(DeviceBean selectDevice, DataPointBean selectDataPoint) {
        createRecipe = new RecipeBean();
        String deviceId = selectDevice.getDeviceId();
        List<String> devices = new ArrayList<>(2);
        devices.add(deviceId);
        createRecipe.setDevices(devices);
        List<String> productIds = new ArrayList<>(2);
        productIds.add(selectDevice.getPidImp());
        createRecipe.setPrdIds(productIds);
        List<Integer> dpIds = new ArrayList<>(2);
        dpIds.add(selectDataPoint.getDpId());
        createRecipe.setDpIds(dpIds);
        createRecipe.setEnabled(true);
        createRecipe.setCategory(Constant.RECIPE_TYPE_EDGE);

        if (Constant.SYSTEM_DEVICE_ID.equals(deviceId)) {
            createRecipe.setType(Constant.RECIPE_TYPE_SCHEDULE);
            RecipeBean.CrontabBean crontab = new RecipeBean.CrontabBean();
            crontab.setMinute("*");
            crontab.setHour("*");
            crontab.setDay_of_week("*");
            crontab.setDay_of_month("*");
            crontab.setMonth_of_year("*");
            createRecipe.setCrontab(crontab);
            createRecipe.setTriggerVal(new RecipeBean.TriggerValBean());
        } else {
            createRecipe.setType(Constant.RECIPE_TYPE_RECIPE);
            RecipeBean.TriggerValBean triggerVal = new RecipeBean.TriggerValBean();
            triggerVal.setFrom(deviceId);
            triggerVal.setDpId(selectDataPoint.getDpId());
            triggerVal.setOp("eq");
            triggerVal.setValue(0);
            createRecipe.setTriggerVal(triggerVal);
            createRecipe.setCrontab(new RecipeBean.CrontabBean());
        }
    }

    private final void configLinearLayoutManager(UltimateRecyclerView rv) {
        final ScrollSmoothLineaerLayoutManager mgm = new ScrollSmoothLineaerLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false, 300);
        rv.setLayoutManager(mgm);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(DeviceBean device) {
        Logger.i(device.getName());
        selectedDevice = device;
        dataPoints = filterDataPoint(device.getPidImp());
        mDataPointAdapter.setDataPointList(dataPoints);
    }

    private List<DeviceBean> filterDevice() {
        List<DeviceBean> devices = DeviceDataBase.getInstance(getActivity()).getDevices();
        List<DeviceBean> deviceFilters = new ArrayList<>();
        for (DeviceBean device : devices) {
            if (filterDataPoint(device.getPidImp()).size() > 0) {
                deviceFilters.add(device);
            }
        }
        return deviceFilters;
    }


    private List<DataPointBean> filterDataPoint(String productId) {
        List<DataPointBean> dataPoints = DataPointDataBase.getInstance(getActivity()).getDataPoints(productId);
        List<DataPointBean> dataPointFilters = new ArrayList<>();
        if (Constant.SYSTEM_PRODUCT_ID.equals(productId)) {
            dataPointFilters.add(Utils.SYSTEM_DATA_POINTS(getActivity()).get(0));
        } else {
            if (!IntoUtil.Empty.check(dataPoints)) {
                for (DataPointBean datapoint : dataPoints) {
                    if (Constant.TRANSFROM_CMD != datapoint.getDirection() && !datapoint.getType().equals(Constant.STRING_DT)) {
                        dataPointFilters.add(datapoint);
                    }
                }
            }
        }
        return dataPointFilters;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Logger.i("page: " + position);
        selectedDevice = deviceList.get(position * 6);
        dataPoints = filterDataPoint(selectedDevice.getPidImp());
        mDataPointAdapter.setDataPointList(dataPoints);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(DataPointBean dataPoint) {
        initCreateRecipe(selectedDevice, dataPoint);
        SetTriggerFragment.launch(getActivity(), createRecipe);
    }
}
