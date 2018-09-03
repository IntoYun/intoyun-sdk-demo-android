package com.molmc.intoyundemo.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.molmc.intoyunsdk.bean.DataPointBean;
import com.molmc.intoyunsdk.bean.RecipeBean;
import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.bean.FragmentArgs;
import com.molmc.intoyundemo.support.RecipeChangeListener;
import com.molmc.intoyundemo.support.db.DataPointDataBase;
import com.molmc.intoyundemo.support.eventbus.UpdateRecipe;
import com.molmc.intoyundemo.support.views.RecipeActionBool;
import com.molmc.intoyundemo.support.views.RecipeActionEmail;
import com.molmc.intoyundemo.support.views.RecipeActionEnum;
import com.molmc.intoyundemo.support.views.RecipeActionFloat;
import com.molmc.intoyundemo.support.views.RecipeActionMessage;
import com.molmc.intoyundemo.support.views.RecipeTriggerBool;
import com.molmc.intoyundemo.support.views.RecipeTriggerEnum;
import com.molmc.intoyundemo.support.views.RecipeTriggerFloat;
import com.molmc.intoyundemo.support.views.RecipeTriggerTimer;
import com.molmc.intoyundemo.ui.activity.BaseActivity;
import com.molmc.intoyundemo.ui.activity.FragmentCommonActivity;
import com.molmc.intoyundemo.ui.activity.MainActivity;
import com.molmc.intoyundemo.utils.Constant;
import com.molmc.intoyundemo.utils.Utils;
import com.suke.widget.SwitchButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehui on 17/3/28.
 */

public class RecipeDetailFragment extends BaseFragment implements RecipeChangeListener, IntoYunListener {


    public static RecipeDetailFragment newInstance() {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        return fragment;
    }

    public static void launch(Activity from, RecipeBean recipeBean, boolean isCreate) {
        FragmentArgs args = new FragmentArgs();
        args.add("createRecipe", new Gson().toJson(recipeBean));
        args.add("isCreate", isCreate);
        FragmentCommonActivity.launch(from, RecipeDetailFragment.class, args);
    }


    @Bind(R.id.ivDevice)
    ImageView ivDevice;
    @Bind(R.id.tvDescription)
    TextView tvDescription;
    @Bind(R.id.swEnable)
    SwitchButton swEnable;
    @Bind(R.id.tvEdgeTitle)
    TextView tvEdgeTitle;
    @Bind(R.id.tvEdgeContent)
    TextView tvEdgeContent;
    @Bind(R.id.cbEdge)
    CheckBox cbEdge;
    @Bind(R.id.tvPeriodTitle)
    TextView tvPeriodTitle;
    @Bind(R.id.tvPeriodContent)
    TextView tvPeriodContent;
    @Bind(R.id.cbPeriod)
    CheckBox cbPeriod;
    @Bind(R.id.layTriggerContainer)
    LinearLayout layTriggerContainer;
    @Bind(R.id.layActionContainer)
    LinearLayout layActionContainer;
    @Bind(R.id.layContainer)
    LinearLayout layContainer;

    private RecipeBean createRecipe;
    private DataPointBean triggerDataPoint;
    private DataPointBean actionDataPoint;
    private boolean isCreate;
    private Map<String, String> logicMap = new HashMap<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, view);
        if (getArguments() != null) {
            createRecipe = new Gson().fromJson(getArguments().getString("createRecipe"), RecipeBean.class);
            isCreate = (boolean) getArguments().get("isCreate");
            triggerDataPoint = DataPointDataBase.getInstance(getActivity()).getDataPoint(createRecipe.getPrdIds().get(0), createRecipe.getDpIds().get(0));
            actionDataPoint = DataPointDataBase.getInstance(getActivity()).getDataPoint(createRecipe.getPrdIds().get(1), createRecipe.getDpIds().get(1));
        }
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.getSupportActionBar().setTitle(R.string.recipe_detail_title);
        setHasOptionsMenu(true);
        initView();
        return view;
    }

    private void initView() {
        if (!isCreate) {
            transferTimeZone(true);
        }
        initLogicMap();
        initTriggerView();
        initActionView();
        getDescription();
        setViewListener();
    }

    private void setViewListener(){
        tvDescription.setText(createRecipe.getDescription());
        swEnable.setChecked(createRecipe.isEnabled());
        cbEdge.setChecked(Constant.RECIPE_TYPE_EDGE.equals(createRecipe.getCategory()));
        cbPeriod.setChecked(Constant.RECIPE_TYPE_PERIOD.equals(createRecipe.getCategory()));
        swEnable.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                createRecipe.setEnabled(isChecked);
            }
        });
        cbEdge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cbPeriod.setChecked(!isChecked);
                createRecipe.setCategory(Constant.RECIPE_TYPE_EDGE);

            }
        });
        cbPeriod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cbEdge.setChecked(!isChecked);
                createRecipe.setCategory(Constant.RECIPE_TYPE_PERIOD);
            }
        });
    }


    private void initLogicMap() {
        String[] logicOp = getResources().getStringArray(R.array.logic_op);
        String[] logicText = getResources().getStringArray(R.array.logic);
        for (int i = 0; i < logicOp.length; i++) {
            logicMap.put(logicOp[i], logicText[i]);
        }
    }

    private void initTriggerView() {
        if (createRecipe.getType().equals(Constant.RECIPE_TYPE_SCHEDULE)) {
            RecipeTriggerTimer timerView = new RecipeTriggerTimer(getActivity());
            timerView.initData(createRecipe.getCrontab(), this);
            layTriggerContainer.addView(timerView, 0);
        } else {
            if (triggerDataPoint.getType().equals(Constant.BOOL_DT)) {
                RecipeTriggerBool boolView = new RecipeTriggerBool(getActivity());
                boolView.initData(createRecipe.getTriggerVal(), triggerDataPoint, this);
                layTriggerContainer.addView(boolView, 0);
            } else if (triggerDataPoint.getType().equals(Constant.NUMBER_DT)) {
                RecipeTriggerFloat floatView = new RecipeTriggerFloat(getActivity());
                floatView.initData(createRecipe.getTriggerVal(), triggerDataPoint, this);
                layTriggerContainer.addView(floatView, 0);
            } else if (triggerDataPoint.getType().equals(Constant.ENUM_DT)) {
                RecipeTriggerEnum enumView = new RecipeTriggerEnum(getActivity());
                enumView.initData(createRecipe.getTriggerVal(), triggerDataPoint, this);
                layTriggerContainer.addView(enumView, 0);
            }
        }
    }

    private void initActionView() {
        if (Constant.RECIPE_ACTION_MSGBOX.equals(createRecipe.getActionVal().get(0).getType())) {
            RecipeActionMessage view = new RecipeActionMessage(getActivity());
            view.initData(createRecipe.getActionVal().get(0), Utils.SYSTEM_DATA_POINTS(getActivity()).get(2), this);
            layActionContainer.addView(view, 0);
        } else if (Constant.RECIPE_ACTION_EMAIL.equals(createRecipe.getActionVal().get(0).getType())) {
            RecipeActionEmail view = new RecipeActionEmail(getActivity());
            view.initData(createRecipe.getActionVal().get(0), Utils.SYSTEM_DATA_POINTS(getActivity()).get(1), this);
            layActionContainer.addView(view, 0);
        } else if (actionDataPoint != null) {
            if (actionDataPoint.getType().equals(Constant.BOOL_DT)) {
                RecipeActionBool boolView = new RecipeActionBool(getActivity());
                boolView.initData(createRecipe.getActionVal().get(0), actionDataPoint, this);
                layActionContainer.addView(boolView, 0);
            } else if (actionDataPoint.getType().equals(Constant.NUMBER_DT)) {
                RecipeActionFloat floatView = new RecipeActionFloat(getActivity());
                floatView.initData(createRecipe.getActionVal().get(0), actionDataPoint, this);
                layActionContainer.addView(floatView, 0);
            } else if (actionDataPoint.getType().equals(Constant.ENUM_DT)) {
                RecipeActionEnum enumView = new RecipeActionEnum(getActivity());
                enumView.initData(createRecipe.getActionVal().get(0), actionDataPoint, this);
                layActionContainer.addView(enumView, 0);
            } else if (actionDataPoint.getType().equals(Constant.STRING_DT)) {
                RecipeActionMessage view = new RecipeActionMessage(getActivity());
                view.initData(createRecipe.getActionVal().get(0), actionDataPoint, this);
                layActionContainer.addView(view, 0);
            }
        }
    }

    private void getDescription() {
        getTriggerDescription();
        getActionDescription();
    }

    private void getTriggerDescription() {
        String description = getString(R.string.recipe_desc_if);
        if (Constant.RECIPE_TYPE_SCHEDULE.equals(createRecipe.getType())) {
            RecipeBean.CrontabBean crontab = createRecipe.getCrontab();
            description = description + getString(R.string.system_data_point_timer);
            if (!"*".equals(crontab.getDay_of_week())) {
                if (!"7".equals(crontab.getDay_of_week())) {
                    description = description + getString(R.string.recipe_desc_every_week);
                }
                description = description + getResources().getStringArray(R.array.dayOfWeek)[Integer.parseInt(crontab.getDay_of_week())];
            }
            description = description + crontab.getHour() + ":" + crontab.getMinute();

        } else {
            description = description + Utils.getDatapointName(getActivity(), triggerDataPoint);
            switch (triggerDataPoint.getType()) {
                case Constant.BOOL_DT:
                    description = description + getString(R.string.recipe_desc_trigger_status) + ((int)Float.parseFloat(String.valueOf(createRecipe.getTriggerVal().getValue()))==1 ? true: false);
                    break;
                case Constant.NUMBER_DT:
                    description = description + logicMap.get(createRecipe.getTriggerVal().getOp()) + Utils.parseFloat(Float.parseFloat(String.valueOf(createRecipe.getTriggerVal().getValue())), triggerDataPoint);
                    break;
                case Constant.ENUM_DT:
                    description = description + logicMap.get("eq") + triggerDataPoint.get_enum().get((int) Float.parseFloat(String.valueOf(createRecipe.getTriggerVal().getValue())));
                    break;
            }
        }
        description = description + ", ";
        createRecipe.setDescription(description);
    }


    private void getActionDescription() {
        String description = getString(R.string.recipe_desc_then);
        RecipeBean.ActionValBean action = createRecipe.getActionVal().get(0);
        if (Constant.RECIPE_ACTION_EMAIL.equals(action.getType())) {
            description = description + String.format(getString(R.string.recipe_desc_email), action.getValue(), action.getTo());
        } else if (Constant.RECIPE_ACTION_MSGBOX.equals(action.getType())) {
            description = description + getString(R.string.recipe_message_box_title) + action.getValue();
        } else {
            switch (actionDataPoint.getType()) {
                case Constant.BOOL_DT:
                    description = description + Utils.getDatapointName(getActivity(), actionDataPoint) + getString(R.string.recipe_desc_action_status) + ((int)Float.parseFloat(String.valueOf(action.getValue())) == 1 ? true : false);
                    break;
                case Constant.NUMBER_DT:
                    description = description + Utils.getDatapointName(getActivity(), actionDataPoint) + getString(R.string.recipe_desc_action_status) + Utils.parseFloat(Float.parseFloat(String.valueOf(action.getValue())), actionDataPoint);
                    break;
                case Constant.ENUM_DT:
                    description = description + Utils.getDatapointName(getActivity(), actionDataPoint) + getString(R.string.recipe_desc_action_status) + actionDataPoint.get_enum().get((int) Float.parseFloat(String.valueOf(action.getValue())));
                    break;
                case Constant.STRING_DT:
                    description = description + getString(R.string.recipe_message_box_title) + action.getValue() + getString(R.string.recipe_desc_to) + Utils.getDatapointName(getActivity(), actionDataPoint);
                    break;
            }
        }
        createRecipe.setDescription(createRecipe.getDescription() + description);
    }

    private void transferTimeZone(boolean initial) {
        if (!Constant.RECIPE_TYPE_SCHEDULE.equals(createRecipe.getType())){
            return;
        }
        int zone = TimeZone.getDefault().getRawOffset() / 3600000;
        RecipeBean.CrontabBean crontab = createRecipe.getCrontab();
        int hour = Integer.parseInt(crontab.getHour());
        if (initial) {
            hour = hour + zone;
        } else {
            hour = hour - zone;
        }

        if (hour > 23) {
            if (!"*".equals(crontab.getDay_of_week())) {
                int week = Integer.parseInt(crontab.getDay_of_week());
                week = week + 1;
                if (week > 6) {
                    week = 0;
                }
                crontab.setDay_of_week(String.valueOf(week));
            }
            hour = hour - 24;
            crontab.setHour(String.valueOf(hour));
        } else if (hour < 0) {
            if (!"*".equals(crontab.getDay_of_week())) {
                int week = Integer.parseInt(crontab.getDay_of_week());
                week = week - 1;
                if (week < 0) {
                    week = 6;
                }
                crontab.setDay_of_week(String.valueOf(week));
            }
            hour = hour + 24;
            crontab.setHour(String.valueOf(hour));
        } else {
            crontab.setHour(String.valueOf(hour));
        }
        createRecipe.setCrontab(crontab);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_add).setVisible(false);
        menu.findItem(R.id.menu_save).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_save) {
            getDescription();
            transferTimeZone(false);
            if (isCreate) {
                IntoYunSdk.createRecipe(createRecipe, this);
            } else {
                IntoYunSdk.updateRecipe(createRecipe.get_id(), createRecipe.getType(), createRecipe, this);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTriggerChange(RecipeBean.TriggerValBean triggerVal) {
        createRecipe.setTriggerVal(triggerVal);
    }

    @Override
    public void onActionChange(RecipeBean.ActionValBean actionVal) {
        List<RecipeBean.ActionValBean> mActionVal = new ArrayList<>();
        mActionVal.add(actionVal);
        createRecipe.setActionVal(mActionVal);
    }

    @Override
    public void onCrontabChange(RecipeBean.CrontabBean crontab) {
        createRecipe.setCrontab(crontab);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onSuccess(Object result) {
        EventBus.getDefault().post(new UpdateRecipe("refresh"));
        showToast(R.string.suc_save);
        MainActivity.launch(getActivity());
    }

    @Override
    public void onFail(NetError error) {
        showToast(error.getMessage());
    }
}
