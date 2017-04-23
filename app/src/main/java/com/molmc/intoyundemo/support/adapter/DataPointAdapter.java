package com.molmc.intoyundemo.support.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.molmc.intoyunsdk.bean.DataPointBean;
import com.molmc.intoyunsdk.utils.IntoUtil;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.support.ClickListItemListener;
import com.molmc.intoyundemo.utils.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehui on 17/3/27.
 */

public class DataPointAdapter extends easyRegularAdapter<DataPointBean, DataPointAdapter.DataPointNode> {

    private Context mContext;
    private ClickListItemListener clickListItemListener;

    public DataPointAdapter(Context context, List<DataPointBean> list) {
        super(list);
        this.mContext = context;
    }

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.lay_item_data_point;
    }

    @Override
    protected DataPointNode newViewHolder(View view) {
        return new DataPointNode(view);
    }

    @Override
    protected void withBindHolder(DataPointNode holder, DataPointBean data, int position) {
        initView(holder, data, position);
    }

    public void setClickListItemListener(ClickListItemListener listener){
        this.clickListItemListener = listener;
    }

    /**
     * Sets device list.
     *
     * @param dataPointList the dataPoint list
     */
    public void setDataPointList(List<DataPointBean> dataPointList) {
        if (IntoUtil.Empty.check(dataPointList)){
            return;
        }
        source = dataPointList;
        notifyDataSetChanged();
    }

    private void initView(DataPointNode holder, DataPointBean data, int position) {
        holder.dataPointName.setText(Utils.getDatapointName(mContext, data));
        holder.itemView.setOnClickListener(onClickListener(data, position));
    }

    private View.OnClickListener onClickListener(final DataPointBean data, int position){
        return new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                clickListItemListener.onClick(data);
            }
        };
    }

    /**
     * The type View holder.
     */
    class DataPointNode extends UltimateRecyclerviewViewHolder {

        @Bind(R.id.dataPointName)
        TextView dataPointName;
        @Bind(R.id.itemDataPoint)
        RelativeLayout itemDataPoint;

        public DataPointNode(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

}
