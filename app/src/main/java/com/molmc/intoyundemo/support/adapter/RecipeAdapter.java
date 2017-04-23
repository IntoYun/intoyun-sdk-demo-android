package com.molmc.intoyundemo.support.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.support.db.DataPointDataBase;
import com.molmc.intoyundemo.support.db.DeviceDataBase;
import com.molmc.intoyundemo.ui.fragment.RecipeDetailFragment;
import com.molmc.intoyundemo.utils.DialogUtil;
import com.molmc.intoyundemo.utils.Interface;
import com.molmc.intoyundemo.utils.Utils;
import com.molmc.intoyunsdk.bean.DataPointBean;
import com.molmc.intoyunsdk.bean.DeviceBean;
import com.molmc.intoyunsdk.bean.RecipeBean;
import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.openapi.Constant;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.suke.widget.SwitchButton;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by hehui on 17/3/23.
 */

public class RecipeAdapter extends easyRegularAdapter<RecipeBean, RecipeAdapter.RecipeNode> {

    private Context mContext;
    private int[] colors = {R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_6};
    private static final int[] defaultDrawables = {R.mipmap.ic_default_1, R.mipmap.ic_default_2, R.mipmap.ic_default_3, R.mipmap.ic_default_4, R.mipmap.ic_default_5, R.mipmap.ic_default_6};

    public RecipeAdapter(Context context, List<RecipeBean> recipes) {
        super(recipes);
        mContext = context;
    }

    public void setData(List<RecipeBean> recipes) {
        source = recipes;
        notifyDataSetChanged();
    }

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.lay_item_recipe;
    }

    @Override
    protected RecipeNode newViewHolder(View view) {
        return new RecipeNode(view);
    }

    @Override
    protected void withBindHolder(RecipeNode holder, RecipeBean data, int position) {
        initView(holder, data, position);
    }


    private void initView(RecipeNode holder, RecipeBean recipe, int position) {
        holder.itemView.setBackgroundColor(mContext.getResources().getColor(colors[position % colors.length]));
        holder.txtDesc.setText(recipe.getDescription());
        holder.swEnable.setChecked(recipe.isEnabled());
        if (recipe.getCategory().equals("edge")) {
            holder.txtCategory.setText(R.string.recipe_edge);
        } else {
            holder.txtCategory.setText(R.string.recipe_period);
        }
        holder.swEnable.setOnCheckedChangeListener(onCheckedChangeListener(recipe));
        holder.runTest.setOnClickListener(onClickListener(recipe, holder));
        holder.itemView.setOnLongClickListener(onLongClickListener(recipe, position));
        holder.itemView.setOnClickListener(onItemClickListener(recipe, position));

        DeviceBean triggerDevice = DeviceDataBase.getInstance(mContext).getDeviceById(recipe.getDevices().get(0));
        DeviceBean actionDevice = DeviceDataBase.getInstance(mContext).getDeviceById(recipe.getDevices().get(1));

        if (triggerDevice != null)
            Glide.with(mContext).load(Constant.INTOYUN_HTTP_HOST + triggerDevice.getImgSrc()).fitCenter().placeholder(defaultDrawables[position % defaultDrawables.length])
                    .bitmapTransform(new RoundedCornersTransformation(mContext, Utils.dip2px(40), 0)).into(holder.imgPhoto);
        if (actionDevice != null)
            Glide.with(mContext).load(Constant.INTOYUN_HTTP_HOST + actionDevice.getImgSrc()).fitCenter().placeholder(defaultDrawables[position % defaultDrawables.length])
                    .bitmapTransform(new RoundedCornersTransformation(mContext, Utils.dip2px(20), 0)).into(holder.imgActionPhoto);
    }

    private SwitchButton.OnCheckedChangeListener onCheckedChangeListener(final RecipeBean recipe) {
        return new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton buttonView, boolean isChecked) {
                recipe.setEnabled(isChecked);
                IntoYunSdk.updateRecipe(recipe.get_id(), recipe.getType(), recipe, new IntoYunListener() {
                    @Override
                    public void onSuccess(Object result) {
                        DialogUtil.showToast(R.string.suc_change);
                    }

                    @Override
                    public void onFail(NetError error) {
                        DialogUtil.showToast(error.getMessage());
                    }
                });
            }
        };
    }

    private View.OnClickListener onClickListener(final RecipeBean recipe, final RecipeNode holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recipe.isEnabled()) {
                    DialogUtil.showToast(R.string.recipe_should_enabled);
                    return;
                }
                holder.runTest.setImageResource(R.mipmap.ic_play_circle);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.runTest.setImageResource(R.mipmap.ic_pause_circle);
                            }
                        });
                    }
                }, 2000);
                IntoYunSdk.testRunRecipe(recipe.get_id(), recipe.getType(), new IntoYunListener() {
                    @Override
                    public void onSuccess(Object result) {

                    }

                    @Override
                    public void onFail(NetError error) {
                        DialogUtil.showToast(error.getMessage());
                    }
                });
            }
        };
    }

    private View.OnLongClickListener onLongClickListener(final RecipeBean recipe, final int position) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogUtil.showDialog(mContext, R.string.delete_tips, R.string.delete_content, R.string.cancel, R.string.confirm, new Interface.DialogCallback() {
                    @Override
                    public void onNegative() {

                    }

                    @Override
                    public void onPositive() {
                        IntoYunSdk.deleteRecipeById(recipe.get_id(), recipe.getType(), new IntoYunListener() {
                            @Override
                            public void onSuccess(Object result) {
                                removeAt(position);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onFail(NetError error) {
                                DialogUtil.showToast(error.getMessage());
                            }
                        });
                    }
                });
                return true;
            }
        };
    }

    private View.OnClickListener onItemClickListener(final RecipeBean recipe, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceBean triggerDev;
                DataPointBean triggerDp;
                if (recipe.getDevices().get(0).equals(com.molmc.intoyundemo.utils.Constant.SYSTEM_DEVICE_ID)) {
                    triggerDev = Utils.SYSTEM_DEVICE(mContext);
                    triggerDp = Utils.SYSTEM_DATA_POINTS(mContext).get(0);
                } else {
                    triggerDev = DeviceDataBase.getInstance(mContext).getDeviceById(recipe.getDevices().get(0));
                    triggerDp = DataPointDataBase.getInstance(mContext).getDataPoint(recipe.getPrdIds().get(0), recipe.getDpIds().get(0));
                }
                DeviceBean actionDev;
                DataPointBean actionDp;
                if (recipe.getDevices().get(1).equals(com.molmc.intoyundemo.utils.Constant.SYSTEM_DEVICE_ID)) {
                    actionDev = Utils.SYSTEM_DEVICE(mContext);
                    actionDp = Utils.SYSTEM_DATA_POINTS(mContext).get(recipe.getDpIds().get(1) - 1);
                } else {
                    actionDev = DeviceDataBase.getInstance(mContext).getDeviceById(recipe.getDevices().get(1));
                    actionDp = DataPointDataBase.getInstance(mContext).getDataPoint(recipe.getPrdIds().get(1), recipe.getDpIds().get(1));
                }

                if (triggerDev != null && actionDev != null && triggerDp != null && actionDp != null) {
                    RecipeDetailFragment.launch((Activity) mContext, recipe, false);
                } else {
                    DialogUtil.showDialog(mContext, R.string.err_tips, R.string.err_recipe_info, R.string.cancel, R.string.confirm, new Interface.DialogCallback() {
                        @Override
                        public void onNegative() {

                        }

                        @Override
                        public void onPositive() {
                            IntoYunSdk.deleteRecipeById(recipe.get_id(), recipe.getType(), new IntoYunListener() {
                                @Override
                                public void onSuccess(Object result) {
                                    removeAt(position);
                                    notifyDataSetChanged();
                                }

                                @Override
                                public void onFail(NetError error) {
                                    DialogUtil.showToast(error.getMessage());
                                }
                            });
                        }
                    });
                }
            }
        };
    }

    class RecipeNode extends UltimateRecyclerviewViewHolder {

        @Bind(R.id.imgPhoto)
        ImageView imgPhoto;
        @Bind(R.id.txtDesc)
        TextView txtDesc;
        @Bind(R.id.txtCategory)
        TextView txtCategory;
        @Bind(R.id.swEnable)
        SwitchButton swEnable;
        @Bind(R.id.runTest)
        ImageView runTest;
        @Bind(R.id.imgActionPhoto)
        ImageView imgActionPhoto;
        @Bind(R.id.itemRecipe)
        RelativeLayout itemRecipe;

        public RecipeNode(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
