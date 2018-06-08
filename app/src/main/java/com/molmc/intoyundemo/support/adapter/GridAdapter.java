package com.molmc.intoyundemo.support.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.molmc.intoyunsdk.bean.DeviceBean;
import com.molmc.intoyunsdk.openapi.Constant;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.utils.Utils;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by hehui on 17/3/27.
 */

public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private List<DeviceBean> devices;
    private LayoutInflater mInflater;


    private static final int[] colors = {R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_6};
    private static final int[] defaultDrawables = {R.mipmap.ic_default_1, R.mipmap.ic_default_2, R.mipmap.ic_default_3, R.mipmap.ic_default_4, R.mipmap.ic_default_5, R.mipmap.ic_default_6};


    public GridAdapter(Context context, List<DeviceBean> devices) {
        this.mContext = context;
        this.devices = devices;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public DeviceBean getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        DeviceBean device = getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lay_item_device_grid, null);

            // construct an item tag
            viewHolder = new ViewHolder();
            viewHolder.setDeviceImage((ImageView) convertView.findViewById(R.id.iv_device_image))
                    .setDeviceName((TextView) convertView.findViewById(R.id.tv_device_name));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        convertView.setBackgroundColor(mContext.getResources().getColor(colors[position % colors.length]));

        // set name
        viewHolder.getDeviceName().setText(device.getName());

        // set image
        RequestOptions opts = new RequestOptions();
        opts.placeholder(defaultDrawables[position % defaultDrawables.length]);
        opts.fitCenter();
        Glide.with(mContext).load(Constant.getHttpHost() + device.getImgSrc())
                .apply(opts)
                .apply(bitmapTransform(new RoundedCornersTransformation(Utils.dip2px(35), 0)))
                .into(viewHolder.getDeviceImage());
        return convertView;
    }


    class ViewHolder {
        private ImageView deviceImage;
        private TextView deviceName;

        public ImageView getDeviceImage() {
            return deviceImage;
        }

        public ViewHolder setDeviceImage(ImageView deviceImage) {
            this.deviceImage = deviceImage;
            return this;
        }

        public TextView getDeviceName() {
            return deviceName;
        }

        public ViewHolder setDeviceName(TextView deviceName) {
            this.deviceName = deviceName;
            return this;
        }
    }
}
