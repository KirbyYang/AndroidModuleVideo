package com.longrise.android.modulevideo.detector;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.view.OrientationEventListener;

/**
 * Created by godliness on 2020-03-11.
 *
 * @author godliness
 */
public final class GravityDetector extends OrientationEventListener {

    private final Activity mTarget;
    private final ContentResolver mResolver;

    private int mOrientation;
//    private boolean mLock;

    public GravityDetector(Activity context) {
        super(context);
        this.mTarget = context;
        this.mResolver = context.getContentResolver();
    }

//    public void setLock(boolean enable) {
//        this.mLock = enable;
//    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (mTarget == null || mTarget.isFinishing()) {
            return;
        }
        int lastOrientation = mOrientation;
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            mOrientation = -1;
            return;
        }

        if (orientation > 350 || orientation < 10) {
            //0度，用户竖直拿着手机
            mOrientation = 0;
        } else if (orientation > 80 && orientation < 100) {
            //90度，用户右侧横屏拿着手机
            mOrientation = 90;
        } else if (orientation > 170 && orientation < 190) {
            //180度，用户反向竖直拿着手机
            mOrientation = 180;
        } else if (orientation > 260 && orientation < 280) {
            //270度，用户左侧横屏拿着手机
            mOrientation = 270;
        }

//        if (mLock) {
            // 如果开启视频锁，则return
//            return;
//        }

        try {
            if (Settings.System.getInt(mResolver, Settings.System.ACCELEROMETER_ROTATION) == 0) {
                // 0 未开启屏幕旋转功能 1 已开启屏幕旋转功能
                return;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (lastOrientation != mOrientation) {
            mTarget.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }
}
