package com.molmc.intoyundemo.ui.fragment;

import android.support.v4.widget.SwipeRefreshLayout;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.molmc.intoyunsdk.bean.MessageBean;
import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.molmc.intoyundemo.support.adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by hehui on 17/3/21.
 */

public class MessageFragment extends BaseRefreshFragment implements IntoYunListener<List<MessageBean>>, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "MessageFragment";

    public static MessageFragment newInstance() {
        MessageFragment fragment = new MessageFragment();
        return fragment;
    }

    private MessageAdapter simpleRecyclerViewAdapter = null;
    private List<MessageBean> messages = new ArrayList<>();

    @Override
    protected void doURV(UltimateRecyclerView urv) {
        getMessages();
        configLinearLayoutManager(ultimateRecyclerView);
        ultimateRecyclerView.setHasFixedSize(true);
        ultimateRecyclerView.setDefaultOnRefreshListener(this);
        enableEmptyViewPolicy();
        simpleRecyclerViewAdapter = new MessageAdapter(getActivity(), messages);
        ultimateRecyclerView.setAdapter(simpleRecyclerViewAdapter);
    }

    @Override
    protected void onLoadmore() {

    }

    @Override
    protected void onFireRefresh() {

    }

    @Override
    public void onSuccess(List<MessageBean> result) {
        messages = result;
        if (messages!=null&&messages.size()>0) {
            Collections.sort(messages, new Comparator<MessageBean>() {
                @Override
                public int compare(MessageBean lhs, MessageBean rhs) {
                    if (lhs.getTimestamp() > rhs.getTimestamp()) {
                        return -1;
                    }
                    return 1;
                }
            });
            simpleRecyclerViewAdapter.setData(messages);
        } else {
            finishRefresh();
        }
    }

    @Override
    public void onFail(NetError error) {
        finishRefresh();
        showToast(error.getMessage());
    }

    @Override
    public void onRefresh() {
        getMessages();
    }

    private void getMessages() {
        IntoYunSdk.getMessages("1", this);
    }
}
