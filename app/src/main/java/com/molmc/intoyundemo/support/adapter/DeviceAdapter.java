package com.molmc.intoyundemo.support.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.support.db.VirtaulDeviceDataBase;
import com.molmc.intoyundemo.ui.fragment.DeviceFragment;
import com.molmc.intoyundemo.ui.fragment.DeviceInfoFragment;
import com.molmc.intoyundemo.ui.fragment.SmartLightFragment;
import com.molmc.intoyundemo.utils.AppSharedPref;
import com.molmc.intoyundemo.utils.DialogUtil;
import com.molmc.intoyundemo.utils.Utils;
import com.molmc.intoyunsdk.bean.BoardInfoBean;
import com.molmc.intoyunsdk.bean.DeviceBean;
import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * features: 设备列表适配器
 * Author：  hhe on 16-7-30 14:10
 * Email：   hhe@molmc.com
 */
public class DeviceAdapter extends easyRegularAdapter<DeviceBean, DeviceAdapter.DeviceNode> {

    private Context mContext;
    private Map<String, BoardInfoBean> boardInfoBeanMap;

    private static final int[] colors = {R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_6};
    private static final int[] defaultDrawables = {R.mipmap.ic_default_1, R.mipmap.ic_default_2, R.mipmap.ic_default_3, R.mipmap.ic_default_4, R.mipmap.ic_default_5, R.mipmap.ic_default_6};

    /**
     * Instantiates a new Device adapter.
     *
     * @param context    the context
     * @param deviceList the device list
     */
    public DeviceAdapter(Context context, List<DeviceBean> deviceList) {
        super(deviceList);
        this.mContext = context;
    }

    /**
     * 设置设备
     *
     * @param deviceList the device list
     */
    public void changeData(List<DeviceBean> deviceList) {
        if (boardInfoBeanMap==null){
            boardInfoBeanMap = AppSharedPref.getInstance(this.mContext).getBoarInfo();
        }
        source = deviceList;
        notifyDataSetChanged();
    }

    /**
     * Add data.
     *
     * @param deviceList the device list
     */
    public void addData(List<DeviceBean> deviceList) {
        if (source == null) {
            changeData(deviceList);
        } else {
            source.addAll(deviceList);
            notifyDataSetChanged();
        }
    }

    /**
     * Sets device list.
     *
     * @param deviceList the device list
     */
    public void setDeviceList(List<DeviceBean> deviceList) {
        source = deviceList;
    }

    /**
     * Gets device list.
     *
     * @return the device list
     */
    public List<DeviceBean> getDeviceList() {
        return source;
    }

    private void initView(DeviceBean dev, DeviceNode holder, int position) {
        holder.txtName.setText(dev.getName());
        if (TextUtils.isEmpty(dev.getBoard())){
            holder.txtAccessMode.setText(R.string.unknown);
        }else{
            holder.txtAccessMode.setText(IntoYunSdk.boardToName(dev.getBoard()));
        }

        RequestOptions opts = new RequestOptions();
        opts.placeholder(defaultDrawables[position % colors.length]);
        opts.fitCenter();
        Glide.with(mContext)
                .load(com.molmc.intoyunsdk.openapi.Constant.getHttpHost() + dev.getImgSrc())
                .apply(opts)
                .apply(bitmapTransform(new RoundedCornersTransformation(Utils.dip2px(40), 0)))
                .thumbnail()
                .into(holder.imgPhoto);

        holder.itemDevice.setOnClickListener(onClickListener(holder, dev));
        holder.itemView.setOnLongClickListener(onLongClickListener(position, dev));
        holder.itemView.setBackgroundColor(mContext.getResources().getColor(colors[position % colors.length]));
        if (dev.isOnline()) {
            holder.onlineStatus.setText(mContext.getString(R.string.device_online_status));
        } else {
            holder.onlineStatus.setText(mContext.getString(R.string.device_offline_status));
        }
    }

    private View.OnClickListener onClickListener(final DeviceNode holder, final DeviceBean device) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                List<DataPointBean> dataPoints = DataPointDataBase.getInstance(mContext).getDataPoints(device.getPidImp());
//                if (IntoUtil.Empty.check(dataPoints)) {
//                    DialogUtil.showToast(R.string.err_data_point_not_found);
//                    return;
//                }
//                if ("o8KOYgFBkowGnEd5".equals(device.getPidImp())) {
//                    SmartLightFragment.launch((Activity) mContext, device);
//                } else {
                    DeviceFragment.launch((Activity) mContext, device);
//                }
            }
        };
    }

    private View.OnLongClickListener onLongClickListener(final int position, final DeviceBean device) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String[] menu = mContext.getResources().getStringArray(R.array.device_menu);
                DialogUtil.showMenuDialog(mContext, menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            //查看设备信息
                            case 0: {
                                if (device.getDeviceId().startsWith("0abcdef")){
                                    DialogUtil.showToast(R.string.err_vir_dev);
                                } else {
                                    DeviceInfoFragment.launch((Activity) mContext, device);
                                }
                                break;
                            }
                            //删除设备
                            case 1: {
                                IntoYunSdk.deleteDeviceById(device.getDeviceId(), new IntoYunListener() {
                                    @Override
                                    public void onSuccess(Object result) {
                                        if (device.getDeviceId().startsWith("0abcdef")){
                                            VirtaulDeviceDataBase.getInstance(mContext).deleteDeviceById(device.getDeviceId());
                                        }
                                        source.remove(position);
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onFail(NetError error) {
                                        DialogUtil.showToast(error.getMessage());
                                    }
                                });
                                break;
                            }
                        }
                    }
                });
                return true;
            }
        };
    }

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.lay_item_device;
    }

    @Override
    protected DeviceNode newViewHolder(View view) {
        return new DeviceNode(view);
    }


    @Override
    protected void withBindHolder(DeviceNode holder, DeviceBean data, int position) {
        initView(data, holder, position);
    }


    /**
     * The type View holder.
     */
    class DeviceNode extends UltimateRecyclerviewViewHolder {
        /**
         * The Item device.
         */
        @Bind(R.id.itemDevice)
        RelativeLayout itemDevice;
        /**
         * The Img photo.
         */
        @Bind(R.id.imgPhoto)
        ImageView imgPhoto;
        /**
         * The Txt name.
         */
        @Bind(R.id.txtName)
        TextView txtName;
        /**
         * The Online status.
         */
        @Bind(R.id.onlineStatus)
        TextView onlineStatus;

        /**
         * Instantiates a new View holder.
         *
         * @param view the view
         */

        @Bind(R.id.accessMode)
        TextView txtAccessMode;

        public DeviceNode(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
