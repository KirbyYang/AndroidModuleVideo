package com.longrise.android.modulevideo.controller.base;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * Created by godliness on 2020-02-27.
 *
 * @author godliness
 */
public abstract class BaseController<Host extends ViewGroup> implements View.OnClickListener {

    private final Context mCxt;
    private final Host mHostView;
    private Host mControllerView;

    private int mCurrentOrientation = -1;

    /**
     * @param hostView 宿主View
     */
    protected BaseController(Host hostView) {
        this.mHostView = hostView;
        this.mCxt = hostView.getContext();
        initAndAttachController();
    }

    /**
     * Show controller view
     */
    public abstract void show();

    /**
     * Hide controller view
     */
    public abstract void hide();

    /**
     * Return display state
     *
     * @return isShowing
     */
    public abstract boolean isShowing();

    /**
     * Return controller layout id
     *
     * @return Layout resource id
     */
    protected abstract int getControllerViewId();

    /**
     * Initialize the controller view here
     */
    protected abstract void initControllerView();

    /**
     * Register related events
     *
     * @param isClick register/unregister
     */
    protected abstract void regEvent(boolean isClick);

    public void onConfigurationChanged(Configuration newConfig) {
        this.mCurrentOrientation = newConfig.orientation;
    }

    /**
     * Release the controller view
     */
    public void release() {
        if (mControllerView != null) {
            final ViewParent parent = mControllerView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(mControllerView);
            }
        }
        regEvent(false);
    }

    /**
     * @return 返回宿主View
     */
    protected final Host getHostView() {
        return mHostView;
    }

    protected final Host getControllerView() {
        return mControllerView;
    }

    protected final <T extends View> T findViewById(@IdRes int id) {
        return mControllerView.findViewById(id);
    }

    protected final LayoutInflater getInflater() {
        return LayoutInflater.from(mCxt);
    }

    protected final Context getContext() {
        return mCxt;
    }

    protected final Resources getResources() {
        return mCxt.getResources();
    }

    public final Drawable getDrawable(int id) {
        return getResources().getDrawable(id);
    }

    protected final int getColor(@ColorRes int resId) {
        return getResources().getColor(resId);
    }

    protected final String getString(@StringRes int strId) {
        return getResources().getString(strId);
    }

    protected final int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    protected final boolean hasNetwork() {
        return hasNetwork(getContext());
    }

    protected final int getConfigurationOrientation() {
        if (mCurrentOrientation != -1) {
            return mCurrentOrientation;
        }
        return getResources().getConfiguration().orientation;
    }

    protected final boolean screenOrientationFromPortrait() {
        return getConfigurationOrientation() == Configuration.ORIENTATION_PORTRAIT;
    }

    private void initAndAttachController() {
        final View controllerView = getInflater().inflate(getControllerViewId(), mHostView, false);
        mHostView.addView(controllerView);
        this.mControllerView = (Host) controllerView;
        initControllerView();
        regEvent(true);
    }

    private boolean hasNetwork(Context cxt) {
        if (cxt != null) {
            final ConnectivityManager manager = (ConnectivityManager) cxt.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null) {
                final NetworkInfo info = manager.getActiveNetworkInfo();
                return info != null && info.isAvailable();
            }
        }
        return false;
    }
}
