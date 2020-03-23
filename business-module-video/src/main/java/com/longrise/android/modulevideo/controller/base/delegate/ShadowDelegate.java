package com.longrise.android.modulevideo.controller.base.delegate;

import android.view.View;
import android.view.animation.AlphaAnimation;

import com.longrise.android.modulevideo.R;

/**
 * Created by godliness on 2020-03-13.
 *
 * @author godliness
 */
public final class ShadowDelegate extends BaseDelegate implements View.OnClickListener {

    private View mShadow;

    private View mShadowBack;
    private View mLoadView;
    private View mOrientation;
    private View mShadowMore;

    private OnShadowDelegateListener mCallback;

    private AlphaAnimation mShowAnimation;
    private AlphaAnimation mHideAnimation;

    public interface OnShadowDelegateListener {

        /**
         * On shadow page back event
         */
        void onShadowBack();

        /**
         * On shadow page orientation event
         */
        void onShadowOrientation();

        /**
         * On shadow page more
         */
        void onShadowMore();
    }

    public ShadowDelegate(View delegateView) {
        super(delegateView);
    }

    public void setOnShadowListener(OnShadowDelegateListener callback) {
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
                mShadow.startAnimation(getHideAnimation());
            }
            mShadow.setVisibility(View.GONE);
        }
    }

    public void showShadowMore() {
        showShadowMore(true);
    }

    public void showShadowMore(boolean openAnimation) {
        if (mShadowMore != null) {
            mShadowMore.setVisibility(View.VISIBLE);
            if (openAnimation) {
                mShadowMore.startAnimation(getShowAnimation());
            }
        }
    }

    public void hideShadowMore() {
        hideShadowMore(true);
    }

    public void hideShadowMore(boolean openAnimation) {
        if (mShadowMore != null) {
            if (openAnimation) {
                mShadowMore.startAnimation(getHideAnimation());
            }
            mShadowMore.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initDelegate() {
        mShadow = findViewById(R.id.modulevideo_video_controller_shadow);
        mShadowBack = findViewById(R.id.modulevideo_shadow_iv_back);
        mLoadView = findViewById(R.id.modulevideo_shadow_ll_loading);
        mOrientation = findViewById(R.id.modulevideo_shadow_iv_orientation);
        mShadowMore = findViewById(R.id.modulevideo_shadow_iv_more);
    }

    @Override
    protected void regEvent(boolean isClick) {
        mShadowBack.setOnClickListener(isClick ? this : null);
        mOrientation.setOnClickListener(isClick ? this : null);
        mShadowMore.setOnClickListener(isClick ? this : null);
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
        } else if (id == R.id.modulevideo_shadow_iv_more) {
            if (mCallback != null) {
                mCallback.onShadowMore();
            }
        }
    }

    private AlphaAnimation getShowAnimation() {
        if (mShowAnimation == null) {
            mShowAnimation = new AlphaAnimation(0, 1);
            mShowAnimation.setDuration(300);
        }
        return mShowAnimation;
    }

    private AlphaAnimation getHideAnimation() {
        if (mHideAnimation == null) {
            mHideAnimation = new AlphaAnimation(1, 0);
            mHideAnimation.setDuration(300);
        }
        return mHideAnimation;
    }
}
