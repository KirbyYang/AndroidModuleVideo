package com.longrise.android.modulevideo.options.definition;

import android.content.Context;
import android.support.annotation.AnimRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.longrise.android.modulevideo.R;

/**
 * Created by godliness on 2020-02-28.
 *
 * @author godliness
 * 清晰度控制器
 */
public final class DefinitController implements DefinitAdapter.OnDefinitAdapterClickListener, View.OnClickListener {

    private ViewGroup mHostView;

    private View mDefinitionView;
    private RecyclerView mDefinitRcv;
    private DefinitAdapter mDefinitAdapter;

    private OnDefinitItemListener mItemListener;
    private DefinitBean[] mBeans;

    @Override
    public void onDefinitAdapterItemClick(DefinitBean definit, int index) {
        if (mItemListener != null) {
            mItemListener.onDefinitItem(definit, index);
        }
    }

    @Override
    public void onClick(View v) {
        hide();
    }

    public interface OnDefinitItemListener {

        void onDefinitItem(DefinitBean definit, int index);
    }

    public static DefinitController build(ViewGroup hostView) {
        if (hostView == null) {
            throw new NullPointerException("hostView == null");
        }
        return new DefinitController(hostView);
    }

    public DefinitController definitItemListener(OnDefinitItemListener definitItemListener) {
        this.mItemListener = definitItemListener;
        return this;
    }

    public DefinitController data(DefinitBean[] beans) {
        this.mBeans = beans;
        return this;
    }

    public void show(int selectedIndex) {
        hide();
        attachToController(selectedIndex);
    }

    private DefinitController(ViewGroup hostView) {
        this.mHostView = hostView;
        initOption(LayoutInflater.from(getContext()));

        final String[] de = {"蓝光 1080P", "超清 720P", "高清", "省流"};

        for(int i = 0; i < 4; i++){
            final DefinitBean bean = new DefinitBean();
            bean.text = de[i];
        }
    }

    private void hide() {
        detachFromController();
        if (mDefinitRcv != null) {
            mDefinitRcv.setAdapter(null);
        }
    }

    private void initOption(LayoutInflater inflater) {
        final View definitionView = inflater.inflate(R.layout.modulevdieo_controller_play_definition, mHostView, false);
        mDefinitRcv = definitionView.findViewById(R.id.modulevideo_definition_rcv);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mDefinitRcv.setLayoutManager(layoutManager);
        this.mDefinitionView = definitionView;
    }

    private void attachToController(int selectedIndex) {
        bindAdapter(selectedIndex);
        mHostView.addView(mDefinitionView);
        mDefinitionView.startAnimation(getAnimation(R.anim.modulevideo_anim_definition_right_in));
    }

    private void detachFromController() {
        if (mDefinitionView != null) {
            final ViewParent parent = mDefinitionView.getParent();
            if (parent instanceof ViewGroup) {
                mDefinitionView.startAnimation(getAnimation(R.anim.modulevideo_anim_definition_right_out));
                ((ViewGroup) parent).removeView(mDefinitionView);
            }
        }
    }

    private void bindAdapter(int currentIndex) {
        if (mDefinitAdapter == null) {
            mDefinitAdapter = new DefinitAdapter(getContext());
            mDefinitAdapter.setOnDefinitAdapterItemListener(this);
        }
        mDefinitAdapter.setData(mBeans);
        mDefinitAdapter.selectedIndex(currentIndex);
        mDefinitRcv.setAdapter(mDefinitAdapter);
    }

    private Context getContext() {
        return mHostView.getContext();
    }

    private Animation getAnimation(@AnimRes int animId) {
        return AnimationUtils.loadAnimation(getContext(), animId);
    }

}
