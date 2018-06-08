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
import android.widget.TextView;

import com.google.gson.Gson;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.layoutmanagers.ScrollSmoothLineaerLayoutManager;
import com.molmc.intoyundemo.utils.AppSharedPref;
import com.molmc.intoyunsdk.bean.BoardInfoBean;
import com.molmc.intoyunsdk.bean.DataPointBean;
import com.molmc.intoyunsdk.bean.DeviceBean;
import com.molmc.intoyunsdk.bean.RecipeBean;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.molmc.intoyunsdk.utils.IntoUtil;
import com.molmc.intoyunsdk.utils.IntoYunSharedPrefs;
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
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehui on 17/3/27.
 */

public class SelectActionFragment extends BaseFragment implements ClickGridItemListener, ViewPager.OnPageChangeListener, ClickListItemListener {


    public static SelectActionFragment newInstance() {
        SelectActionFragment fragment = new SelectActionFragment();
        return fragment;
    }

    public static void launch(Activity from, RecipeBean recipeBean) {
        FragmentArgs args = new FragmentArgs();
        args.add("createRecipe", new Gson().toJson(recipeBean));
        FragmentCommonActivity.launch(from, SelectActionFragment.class, args);
    }

    @Bind(R.id.device_select_viewpager)
    ViewPager deviceSelectViewpager;
    @Bind(R.id.dataPointRecycler)
    UltimateRecyclerView dataPointRecycler;
    @Bind(R.id.tvDataPointTitle)
    TextView tvDataPointTitle;

    private List<DeviceBean> deviceList;
    private List<Fragment> fragmentList = new ArrayList<>();
    private ViewPagerAdapter mViewPagerAdapter;
    private DataPointAdapter mDataPointAdapter;
    private List<DataPointBean> dataPoints = new ArrayList<>();
    private RecipeBean createRecipe;
    private DeviceBean selectDevice;
    private Map<String, BoardInfoBean> boardInfoBeanMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_recipe, container, false);
        ButterKnife.bind(this, view);
        if (getArguments() != null) {
            createRecipe = new Gson().fromJson(getArguments().getString("createRecipe"), RecipeBean.class);
        }
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.getSupportActionBar().setTitle(R.string.recipe_select_action_title);
        setHasOptionsMenu(false);
        initView();
        return view;
    }

    private void initView() {
        boardInfoBeanMap = AppSharedPref.getInstance(this.getActivity()).getBoarInfo();
        tvDataPointTitle.setText(R.string.recipe_action_data_point);
        deviceList = filterDevice();
        deviceList.add(0, Utils.SYSTEM_DEVICE(getActivity()));
        Logger.i(new Gson().toJson(deviceList));
        if (!IntoUtil.Empty.check(deviceList)) {
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
        selectDevice = deviceList.get(0);
    }

    private void initCreateRecipe(DeviceBean selectDevice, DataPointBean selectDataPoint) {
        String deviceId = selectDevice.getDeviceId();
        List<String> devices = new ArrayList<>();
        devices.add(createRecipe.getDevices().get(0));
        devices.add(deviceId);
        createRecipe.setDevices(devices);
        List<String> productIds = new ArrayList<>();
        productIds.add(createRecipe.getPrdIds().get(0));
        productIds.add(selectDevice.getPidImp());
        createRecipe.setPrdIds(productIds);
        List<Integer> dpIds = new ArrayList<>();
        dpIds.add(createRecipe.getDpIds().get(0));
        dpIds.add(selectDataPoint.getDpId());
        createRecipe.setDpIds(dpIds);

        RecipeBean.ActionValBean actionVal = new RecipeBean.ActionValBean();
        actionVal.setDpId(selectDataPoint.getDpId());
        actionVal.setDpType(Utils.parseDataPointType(selectDataPoint));
        if (Constant.RECIPE_ACTION_EMAIL.equals(selectDataPoint.getType())) {
            actionVal.setType(Constant.RECIPE_ACTION_EMAIL);
            actionVal.setTo(IntoYunSharedPrefs.getUserInfo(getActivity()).getEmail());
        } else if (Constant.RECIPE_ACTION_MSGBOX.equals(selectDataPoint.getType())) {
            String to = "v2/user/" + IntoYunSharedPrefs.getUserInfo(getActivity()).getUid() + "/rx";
            actionVal.setType(Constant.RECIPE_ACTION_MSGBOX);
            actionVal.setTo(to);
        } else {
            String topicMode = "device";
            if (IntoYunSdk.isLoRaNode(selectDevice.getType())){
                topicMode = "lora";
            }
            String to = "v2/" + topicMode + '/' + deviceId + "/tx";
            actionVal.setType(Constant.RECIPE_ACTION_MQTT);
            actionVal.setTo(to);
        }
        List<RecipeBean.ActionValBean> actionValList = new ArrayList<>();
        actionValList.add(actionVal);
        createRecipe.setActionVal(actionValList);
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
        selectDevice = device;
        dataPoints = filterDataPoint(device.getPidImp());
        mDataPointAdapter.setDataPointList(dataPoints);
    }

    private List<DeviceBean> filterDevice() {
        List<DeviceBean> devices = DeviceDataBase.getInstance(getActivity()).getDevices();
        List<DeviceBean> deviceFilters = new ArrayList<>();
        for (DeviceBean device : devices) {
            if (!IntoYunSdk.isGateway(device.getType()) && filterDataPoint(device.getPidImp()).size() > 0) {
                deviceFilters.add(device);
            }
        }
        return deviceFilters;
    }


    private List<DataPointBean> filterDataPoint(String productId) {

        List<DataPointBean> dataPoints = DataPointDataBase.getInstance(getActivity()).getDataPoints(productId);
        List<DataPointBean> dataPointFilters = new ArrayList<>();
        if (Constant.SYSTEM_PRODUCT_ID.equals(productId)) {
            dataPointFilters.add(Utils.SYSTEM_DATA_POINTS(getActivity()).get(1));
            dataPointFilters.add(Utils.SYSTEM_DATA_POINTS(getActivity()).get(2));
        } else {
            if (!IntoUtil.Empty.check(dataPoints)) {
                for (DataPointBean datapoint : dataPoints) {
                    if (Constant.TRANSFROM_DATA != datapoint.getDirection()) {
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
        selectDevice = deviceList.get(position * 6);
        dataPoints = filterDataPoint(selectDevice.getPidImp());
        mDataPointAdapter.setDataPointList(dataPoints);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(DataPointBean dataPoint) {
        initCreateRecipe(selectDevice, dataPoint);
        SetActionFragment.launch(getActivity(), createRecipe);
    }
}
