package com.longrise.android.modulevideo.controller.base.bar.base;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Created by godliness on 2020-03-07.
 *
 * @author godliness
 */
public abstract class BaseBar implements View.OnClickListener {

    private final ViewGroup mHost;
    private final Context mCxt;
    private final View mBar;

    private View.OnAttachStateChangeListener mStateChangeCallback;
    private Animation.AnimationListener mHideAnimationCallback;

    private Animation mShowAnimation;
    private Animation mHideAnimation;

    protected BaseBar(ViewGroup host) {
        this.mHost = host;
        final Context context = host.getContext();
        final View bar = LayoutInflater.from(context).inflate(getBarLayoutId(), host, attachToRoot());
        bar.setOnClickListener(this);
        this.mCxt = context;
        this.mBar = bar;
        initBar();
        regEvent(true);
    }

    /**
     * Returns the bar layout resource id
     *
     * @return Layout Id
     */
    protected abstract int getBarLayoutId();

    /**
     * Initializes the bar layout resource
     */
    protected abstract void initBar();

    /**
     * Registration/unregister events
     *
     * @param event register/unregister
     */
    protected abstract void regEvent(boolean event);

    protected boolean attachToRoot() {
        return false;
    }

    public void show() {
        show(true);
    }

    public void show(boolean openAnimation) {
        if (openAnimation) {
            if (mStateChangeCallback == null) {
                mBar.addOnAttachStateChangeListener(getStateChangeCallback());
            }
        }
        addToController();
    }

    public void hide() {
        hide(true);
    }

    public void hide(boolean openAnim) {
        if (openAnim) {
            final ViewParent parent = mBar.getParent();
            if (parent != null) {
                mBar.startAnimation(getHideAnimation());
            }
        } else {
            removeFromController();
        }
    }

    public void release() {
        regEvent(false);
        if (mStateChangeCallback != null) {
            mBar.removeOnAttachStateChangeListener(mStateChangeCallback);
        }
    }

    protected Animation getShowAnimation() {
        if (mShowAnimation == null) {
            mShowAnimation = new AlphaAnimation(0, 1);
            mShowAnimation.setDuration(300);
        }
        return mShowAnimation;
    }

    protected Animation getHideAnimation() {
        if (mHideAnimation == null) {
            mHideAnimation = new AlphaAnimation(1, 0);
            mHideAnimation.setDuration(300);
            mHideAnimation.setAnimationListener(getHideAnimationCallback());
        }
        return mHideAnimation;
    }

    protected final Context getContext() {
        return mCxt;
    }

    protected final void removeFromController() {
        final ViewParent parent = mBar.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mBar);
        }
    }

    protected final void addToController() {
        if (mBar.getParent() == null) {
            mHost.addView(mBar);
        }
    }

    protected final <T extends View> T findViewById(@IdRes int id) {
        return mBar.findViewById(id);
    }

    private Animation.AnimationListener getHideAnimationCallback() {
        if (mHideAnimationCallback == null) {
            mHideAnimationCallback = new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    removeFromController();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            };
        }
        return mHideAnimationCallback;
    }

    private View.OnAttachStateChangeListener getStateChangeCallback() {
        if (mStateChangeCallback == null) {
            mStateChangeCallback = new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {
                    view.startAnimation(getShowAnimation());
                }

                @Override
                public void onViewDetachedFromWindow(View v) {

                }
            };
        }
        return mStateChangeCallback;
    }

}
