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
import com.molmc.intoyundemo.support.views.RecipeActionBool;
import com.molmc.intoyundemo.support.views.RecipeActionEmail;
import com.molmc.intoyundemo.support.views.RecipeActionEnum;
import com.molmc.intoyundemo.support.views.RecipeActionFloat;
import com.molmc.intoyundemo.support.views.RecipeActionMessage;
import com.molmc.intoyundemo.ui.activity.BaseActivity;
import com.molmc.intoyundemo.ui.activity.FragmentCommonActivity;
import com.molmc.intoyundemo.utils.Constant;
import com.molmc.intoyundemo.utils.Utils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hehui on 17/3/28.
 */

public class SetActionFragment extends BaseFragment implements RecipeChangeListener {

    @Bind(R.id.layContainer)
    LinearLayout layContainer;
    @Bind(R.id.btnNext)
    Button btnNext;

    public static SetActionFragment newInstance() {
        SetActionFragment fragment = new SetActionFragment();
        return fragment;
    }

    public static void launch(Activity from, RecipeBean recipeBean) {
        FragmentArgs args = new FragmentArgs();
        args.add("createRecipe", new Gson().toJson(recipeBean));
        FragmentCommonActivity.launch(from, SetActionFragment.class, args);
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
            dataPoint = DataPointDataBase.getInstance(getActivity()).getDataPoint(createRecipe.getPrdIds().get(1), createRecipe.getDpIds().get(1));
        }
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.getSupportActionBar().setTitle(R.string.recipe_set_action_title);
        setHasOptionsMenu(false);
        initView();
        return view;
    }

    private void initView() {
        if (Constant.RECIPE_ACTION_MSGBOX.equals(createRecipe.getActionVal().get(0).getType())) {
            RecipeActionMessage view = new RecipeActionMessage(getActivity());
            view.initData(createRecipe.getActionVal().get(0), Utils.SYSTEM_DATA_POINTS(getActivity()).get(2), this);
            layContainer.addView(view, 0);
        } else if (Constant.RECIPE_ACTION_EMAIL.equals(createRecipe.getActionVal().get(0).getType())) {
            RecipeActionEmail view = new RecipeActionEmail(getActivity());
            view.initData(createRecipe.getActionVal().get(0), Utils.SYSTEM_DATA_POINTS(getActivity()).get(1), this);
            layContainer.addView(view, 0);
        } else if (dataPoint != null) {
            if (dataPoint.getType().equals(Constant.BOOL_DT)) {
                RecipeActionBool boolView = new RecipeActionBool(getActivity());
                createRecipe.getActionVal().get(0).setValue(1);
                boolView.initData(createRecipe.getActionVal().get(0), dataPoint, this);
                layContainer.addView(boolView, 0);
            } else if (dataPoint.getType().equals(Constant.NUMBER_DT)) {
                RecipeActionFloat floatView = new RecipeActionFloat(getActivity());
                createRecipe.getActionVal().get(0).setValue(dataPoint.getMin());
                floatView.initData(createRecipe.getActionVal().get(0), dataPoint, this);
                layContainer.addView(floatView, 0);
            } else if (dataPoint.getType().equals(Constant.ENUM_DT)) {
                RecipeActionEnum enumView = new RecipeActionEnum(getActivity());
                createRecipe.getActionVal().get(0).setValue(0);
                enumView.initData(createRecipe.getActionVal().get(0), dataPoint, this);
                layContainer.addView(enumView, 0);
            } else if (dataPoint.getType().equals(Constant.STRING_DT)) {
                RecipeActionMessage view = new RecipeActionMessage(getActivity());
                view.initData(createRecipe.getActionVal().get(0), dataPoint, this);
                layContainer.addView(view, 0);
            }
        }
    }

    @Override
    public void onTriggerChange(RecipeBean.TriggerValBean triggerVal) {
    }

    @Override
    public void onActionChange(RecipeBean.ActionValBean actionVal) {
        List<RecipeBean.ActionValBean> mActionVal = new ArrayList<>();
        mActionVal.add(actionVal);
        createRecipe.setActionVal(mActionVal);
    }

    @Override
    public void onCrontabChange(RecipeBean.CrontabBean crontab) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btnNext)
    public void onClick() {
        Logger.i(new Gson().toJson(createRecipe));
        RecipeDetailFragment.launch(getActivity(), createRecipe, true);
    }
}
