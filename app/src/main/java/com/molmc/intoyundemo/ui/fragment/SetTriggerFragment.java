package com.molmc.intoyundemo.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.molmc.intoyunsdk.bean.DataPointBean;
import com.molmc.intoyunsdk.bean.RecipeBean;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.bean.FragmentArgs;
import com.molmc.intoyundemo.support.RecipeChangeListener;
import com.molmc.intoyundemo.support.db.DataPointDataBase;
import com.molmc.intoyundemo.support.views.RecipeTriggerBool;
import com.molmc.intoyundemo.support.views.RecipeTriggerEnum;
import com.molmc.intoyundemo.support.views.RecipeTriggerFloat;
import com.molmc.intoyundemo.support.views.RecipeTriggerTimer;
import com.molmc.intoyundemo.ui.activity.BaseActivity;
import com.molmc.intoyundemo.ui.activity.FragmentCommonActivity;
import com.molmc.intoyundemo.utils.Constant;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hehui on 17/3/28.
 */

public class SetTriggerFragment extends BaseFragment implements RecipeChangeListener {

    @Bind(R.id.layContainer)
    LinearLayout layContainer;
    @Bind(R.id.btnNext)
    Button btnNext;

    public static SetTriggerFragment newInstance() {
        SetTriggerFragment fragment = new SetTriggerFragment();
        return fragment;
    }

    public static void launch(Activity from, RecipeBean recipeBean) {
        FragmentArgs args = new FragmentArgs();
        args.add("createRecipe", new Gson().toJson(recipeBean));
        FragmentCommonActivity.launch(from, SetTriggerFragment.class, args);
    }

    private RecipeBean createRecipe;
    private DataPointBean dataPoint;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_recipe, container, false);
        ButterKnife.bind(this, view);
        if (getArguments() != null) {
            createRecipe = new Gson().fromJson(getArguments().getString("createRecipe"), RecipeBean.class);
            dataPoint = DataPointDataBase.getInstance(getActivity()).getDataPoint(createRecipe.getPrdIds().get(0), createRecipe.getDpIds().get(0));
        }
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.getSupportActionBar().setTitle(R.string.recipe_set_trigger_title);
        setHasOptionsMenu(false);
        initView();
        return view;
    }

    private void initView() {
        if (createRecipe.getType().equals(Constant.RECIPE_TYPE_SCHEDULE)) {
            RecipeTriggerTimer timerView = new RecipeTriggerTimer(getActivity());
            timerView.initData(createRecipe.getCrontab(), this);
            layContainer.addView(timerView, 0);
        } else {
            if (dataPoint.getType().equals(Constant.BOOL_DT)) {
                RecipeTriggerBool boolView = new RecipeTriggerBool(getActivity());
                boolView.initData(createRecipe.getTriggerVal(), dataPoint, this);
                layContainer.addView(boolView, 0);
            } else if (dataPoint.getType().equals(Constant.NUMBER_DT)) {
                RecipeTriggerFloat floatView = new RecipeTriggerFloat(getActivity());
                floatView.initData(createRecipe.getTriggerVal(), dataPoint, this);
                layContainer.addView(floatView, 0);
            } else if (dataPoint.getType().equals(Constant.ENUM_DT)) {
                RecipeTriggerEnum enumView = new RecipeTriggerEnum(getActivity());
                enumView.initData(createRecipe.getTriggerVal(), dataPoint, this);
                layContainer.addView(enumView, 0);
            }
        }
    }

    @Override
    public void onTriggerChange(RecipeBean.TriggerValBean triggerVal) {
        createRecipe.setTriggerVal(triggerVal);
    }

    @Override
    public void onActionChange(RecipeBean.ActionValBean actionVal) {

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

    @OnClick(R.id.btnNext)
    public void onClick() {
        SelectActionFragment.launch(getActivity(), createRecipe);
    }
}
