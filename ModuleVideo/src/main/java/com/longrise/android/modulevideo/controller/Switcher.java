package com.longrise.android.modulevideo.controller;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by godliness on 2020-03-05.
 *
 * @author godliness
 * 屏幕方向切换器
 */
public class Switcher {

    private ViewGroup mHost;
    private Activity mHostParent;

    private int mHeightOfPortrait = -1;

    protected void switchToLandscape() {
        setSystemUIHide();
        adjustPlayerHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected void switchToPortrait() {
        setSystemUIShow();
        adjustPlayerHeight(getPortrainHeight());
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

    public final void onConfiguartionCreated(ViewGroup host) {
        this.mHost = host;
        final Context cxt = host.getContext();
        if (!(cxt instanceof Activity)) {
            throw new IllegalArgumentException("The Host must be created by the Activity type: new ViewGroup(Activity)");
        }
        this.mHostParent = (Activity) cxt;

        final int orientation = cxt.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            switchToPortrait();
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switchToLandscape();
        }
    }

    public final void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            switchToPortrait();
        } else {
            switchToLandscape();
        }
    }

    protected final WindowManager getWindowManager() {
        return mHostParent.getWindowManager();
    }

    protected final Window getWindow() {
        return mHostParent.getWindow();
    }

    private void adjustPlayerHeight(int height) {
        final ViewGroup.LayoutParams lp = mHost.getLayoutParams();
        lp.height = height;
    }

    private void setSystemUIHide() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void setSystemUIShow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
        // 以下新增控制
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            // 使用该flag 使内容延伸到状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            // 将状态栏设置为透明
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        } else {
//            // 延伸到状态栏并且设置状态栏颜色透明
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
    }

    private int getPortrainHeight() {
        if (mHeightOfPortrait <= 0) {
            final Display display = getWindowManager().getDefaultDisplay();
            return mHeightOfPortrait = display.getWidth() / 2 ;
        }
        return mHeightOfPortrait;
    }

    private int getStatusBarHeight(Context target) {
        final Resources resources = target.getResources();
        final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }
}
