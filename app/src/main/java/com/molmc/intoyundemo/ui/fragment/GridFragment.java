package com.molmc.intoyundemo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.molmc.intoyunsdk.bean.DeviceBean;
import com.molmc.intoyunsdk.utils.IntoUtil;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.support.ClickGridItemListener;
import com.molmc.intoyundemo.support.adapter.GridAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehui on 17/3/27.
 */

public class GridFragment extends Fragment implements AdapterView.OnItemClickListener {


    public static GridFragment newInstance(List<DeviceBean> devices) {
        GridFragment fragment = new GridFragment();
        Bundle bundle = new Bundle();
        bundle.putString("devices", new Gson().toJson(devices));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Bind(R.id.device_grid)
    GridView deviceGrid;


    private List<DeviceBean> devices = new ArrayList<>();
    private GridAdapter mGridAdapter;
    private ClickGridItemListener onClickGridItemListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid, container, false);
        if (!IntoUtil.Empty.check(getArguments())){
            String devicesStr = getArguments().getString("devices");
            if (!IntoUtil.Empty.check(devicesStr)){
                devices = new Gson().fromJson(devicesStr, new TypeToken<List<DeviceBean>>(){}.getType());
            }
        }
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mGridAdapter = new GridAdapter(getActivity(), devices);
        deviceGrid.setAdapter(mGridAdapter);
        deviceGrid.setOnItemClickListener(this);
    }

    public void setOnClickGridItemListener(ClickGridItemListener listener) {
        onClickGridItemListener = listener;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Logger.i("position: " + position);
        onClickGridItemListener.onItemClick(devices.get(position));
    }
}
