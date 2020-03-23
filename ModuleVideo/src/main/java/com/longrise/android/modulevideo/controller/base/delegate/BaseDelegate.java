package com.longrise.android.modulevideo.controller.base.delegate;

import android.support.annotation.IdRes;
import android.view.View;

/**
 * Created by godliness on 2020-03-13.
 *
 * @author godliness
 */
public abstract class BaseDelegate {

    private final View mDelegateView;

    protected BaseDelegate(View delegateView) {
        this.mDelegateView = delegateView;

        initDelegate();
        regEvent(true);
    }

    protected abstract void initDelegate();

    protected abstract void regEvent(boolean isClick);

    public void release() {
        regEvent(false);
    }

    protected final <T extends View> T findViewById(@IdRes int id) {
        return mDelegateView.findViewById(id);
    }

}
