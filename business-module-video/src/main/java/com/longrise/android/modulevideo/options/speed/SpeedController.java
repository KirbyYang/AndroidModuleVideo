package com.longrise.android.modulevideo.options.speed;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.longrise.android.modulevideo.R;

/**
 * Created by godliness on 2020-02-27.
 *
 * @author godliness
 * 播放器倍速管理器
 */
public final class SpeedController implements SpeedAdapter.OnSpeedAdapterClickListener, View.OnClickListener {

    private final ViewGroup mHostView;

    private View mSpeedControllerView;
    private RecyclerView mSpeedRcv;
    private SpeedAdapter mSpeedAdapter;

    private OnSpeedItemListener mSpeedItemListener;

    public SpeedController speedItemListener(OnSpeedItemListener callback) {
        this.mSpeedItemListener = callback;
        return this;
    }

    public void show(int selectedIndex) {
        hide();
        attachToController(selectedIndex);
    }

    public SpeedController(ViewGroup parasitiferView) {
        this(parasitiferView, null);
    }

    public SpeedController(ViewGroup parasitiferView, OnSpeedItemListener callback) {
        if (parasitiferView == null) {
            throw new NullPointerException("parasitiferView == null");
        }
        this.mHostView = parasitiferView;
        this.mSpeedItemListener = callback;
        final LayoutInflater inflater = LayoutInflater.from(parasitiferView.getContext());
        initController(inflater);
    }

    public interface OnSpeedItemListener {

        void onSpeedItem(String[] speeds, int index);
    }

    private void hide() {
        removeSpeedView();
        if (mSpeedRcv != null) {
            mSpeedRcv.setAdapter(null);
        }
    }

    private void initController(LayoutInflater inflater) {
        final View speedView = inflater.inflate(R.layout.modulevideo_controller_play_speed, mHostView, false);
        speedView.setOnClickListener(this);
        mSpeedRcv = speedView.findViewById(R.id.modulevideo_rcv_speed);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getTarget());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSpeedRcv.setLayoutManager(layoutManager);
        this.mSpeedControllerView = speedView;
    }

    private void attachToController(int selectedIndex) {
        bindAdapter(selectedIndex);
        mHostView.addView(mSpeedControllerView);
        mSpeedControllerView.post(new Runnable() {
            @Override
            public void run() {
                final AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                alphaAnimation.setDuration(1000);
                mSpeedControllerView.startAnimation(alphaAnimation);
            }
        });
    }

    private void bindAdapter(int selectedIndex) {
        if (mSpeedAdapter == null) {
            mSpeedAdapter = new SpeedAdapter(getTarget());
            mSpeedAdapter.setOnSpeedAdapterItemListener(this);
        }
        mSpeedAdapter.selectedIndex(selectedIndex);
        mSpeedRcv.setAdapter(mSpeedAdapter);
    }

    private Context getTarget() {
        return mHostView.getContext();
    }

    private void removeSpeedView() {
        if (mSpeedControllerView != null) {
            final ViewParent viewParent = mSpeedControllerView.getParent();
            if (viewParent instanceof ViewGroup) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                alphaAnimation.setDuration(300);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ((ViewGroup) viewParent).removeView(mSpeedControllerView);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mSpeedControllerView.startAnimation(AnimationUtils.loadAnimation(getTarget(), R.anim.modulevideo_anim_definition_right_out));
            }
        }
    }

    @Override
    public void onSpeedAdapterItemClick(String[] speeds, int index) {
        hide();
        if (mSpeedItemListener != null) {
            mSpeedItemListener.onSpeedItem(speeds, index);
        }
    }

    @Override
    public void onClick(View v) {
        hide();
    }
}
