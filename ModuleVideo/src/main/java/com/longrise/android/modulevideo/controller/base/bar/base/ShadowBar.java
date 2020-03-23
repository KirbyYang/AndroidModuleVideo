package com.longrise.android.modulevideo.controller.base.bar.base;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import com.longrise.android.modulevideo.R;

/**
 * Created by godliness on 2020-03-12.
 *
 * @author godliness
 */
public final class ShadowBar extends BaseBar {

    private View mShadow;

    private View mShadowBack;
    private View mLoadView;
    private View mOrientation;

    private OnShadowListener mCallback;

    public ShadowBar(ViewGroup host) {
        super(host);
    }

    public interface OnShadowListener {

        /**
         * On shadow page back event
         */
        void onShadowBack();

        /**
         * On shadow page orientation event
         */
        void onShadowOrientation();

//        void onMore();
    }

    public void setOnShadowListener(OnShadowListener callback) {
        this.mCallback = callback;
    }

    public void buffering(boolean show) {
        if (mLoadView != null) {
            mLoadView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void showOrHideShadowBack(boolean show) {
        if (mShadowBack != null) {
            mShadowBack.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void showOrHideShadowOrientation(boolean show) {
        if (mOrientation != null) {
            mOrientation.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void showShadow() {
        if (mShadow != null) {
            mShadow.setVisibility(View.VISIBLE);
        }
    }

    public void hideShadow(boolean openAnimation) {
        if (mShadow != null) {
            if (openAnimation) {
                mShadow.startAnimation(getAlphaAnimation());
            }
            mShadow.setVisibility(View.GONE);
        }
    }

    @Override
    public void show() {
        // do nothing
    }

    @Override
    public void hide() {
        // do nothing
    }

    @Override
    protected boolean attachToRoot() {
        return true;
    }

    @Override
    protected int getBarLayoutId() {
        return R.layout.modulevideo_controller_shadow_bar;
    }

    @Override
    protected void initBar() {
        mShadow = findViewById(R.id.modulevideo_video_controller_shadow);
        mShadowBack = findViewById(R.id.modulevideo_shadow_iv_back);
        mLoadView = findViewById(R.id.modulevideo_shadow_ll_loading);
        mOrientation = findViewById(R.id.modulevideo_shadow_iv_orientation);
    }

    @Override
    protected void regEvent(boolean event) {
        mShadowBack.setOnClickListener(event ? this : null);
        mOrientation.setOnClickListener(event ? this : null);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.modulevideo_shadow_iv_back) {
            if (mCallback != null) {
                mCallback.onShadowBack();
            }
        } else if (id == R.id.modulevideo_shadow_iv_orientation) {
            if (mCallback != null) {
                mCallback.onShadowOrientation();
            }
        }
    }

    private AlphaAnimation getAlphaAnimation() {
        final AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(300);
        return alphaAnimation;
    }
}
