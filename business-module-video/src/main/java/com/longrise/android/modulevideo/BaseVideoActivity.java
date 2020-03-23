package com.longrise.android.modulevideo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.longrise.android.modulevideo.controller.Builder;
import com.longrise.android.modulevideo.controller.OnConfigurationOptionsListener;
import com.longrise.android.modulevideo.controller.OnVideoControllerListener;
import com.longrise.android.modulevideo.controller.Switcher;
import com.longrise.android.modulevideo.controller.VideoController;
import com.longrise.android.modulevideo.detector.GravityDetector;
import com.longrise.android.modulevideo.ijk.IjkVideoView;
import com.longrise.android.mvp.internal.BaseMvpActivity;
import com.longrise.android.mvp.internal.mvp.BasePresenter;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by godliness on 2020-03-09.
 *
 * @author godliness
 * ModuleVideo组件实现
 */
public abstract class BaseVideoActivity<P extends BasePresenter> extends BaseMvpActivity<P> implements
        OnVideoControllerListener, OnConfigurationOptionsListener {

    private IjkVideoView mPlayer;
    private VideoController mVideoController;

    private ViewGroup mRoot;
    private View mContentView;

    private boolean mResumed;
    private boolean mPrepared;
    private boolean mPlayStateBeforeOnPause;
    private boolean mError;

    private GravityDetector mDetector;

    /**
     * Returns the layout resource id, Notice it's going to be divided by the bottom of Player
     *
     * @param savedInstanceState restore state
     * @return Layout resource
     */
    @LayoutRes
    protected abstract int getVideoLayoutResourceId(Bundle savedInstanceState);

    /**
     * Init layout resource
     */
    protected abstract void initLayout();

    /**
     * Initialize the layout resource {@link #getVideoLayoutResourceId(Bundle)}
     *
     * @param regEvent register/unregister
     */
    @Override
    protected abstract void regEvent(boolean regEvent);

    /**
     * The player is completed
     *
     * @param mediaPlayer {@link IMediaPlayer}
     */
    protected abstract void onPlayerCompleted(IMediaPlayer mediaPlayer);

    /**
     * The player is prepared
     *
     * @param mediaPlayer {@link IMediaPlayer}
     */
    protected abstract void onPlayerPrepared(IMediaPlayer mediaPlayer);

    /**
     * The player is error
     *
     * @param mediaPlayer {@link IMediaPlayer}
     */
    protected abstract void onPlayerError(IMediaPlayer mediaPlayer);

    /**
     * Controls screen orientation, or style, etc
     */
    protected Switcher switcher() {
        return null;
    }

    /**
     * Whether to turn on gravity induction
     */
    protected boolean hasGravity() {
        return true;
    }

    /**
     * Whether or not shown switcher
     */
    @Override
    public final boolean hasSwitcher() {
        return hasGravity();
    }

    @Override
    protected final int getLayoutResourceId(@Nullable Bundle savedInstanceState) {
        return R.layout.modulevideo_activity_video;
    }

    @Override
    protected final void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            final View contentView = LayoutInflater.from(this).inflate(getVideoLayoutResourceId(savedInstanceState), mRoot, false);
            mRoot.addView(contentView);
            this.mContentView = contentView;
        }
        initLayout();
    }

    @Override
    protected final void initView() {
        mRoot = findViewById(R.id.modulevideo_video_template);
        mPlayer = findViewById(R.id.modulevideo_video_view);
        createVideoController();
    }

    /**
     * 设置Video资源
     *
     * @param path 资源地址
     */
    protected final void setVideoPath(String path) {
        setVideoPath(path, 0);
    }

    /**
     * 设置Video资源
     *
     * @param path     资源地址
     * @param position 断点位置
     */
    protected final void setVideoPath(String path, int position) {
        mPrepared = false;
        mVideoController.setVideoPath(path, position);
    }

    /**
     * 设置资源标题
     *
     * @param title 资源标题
     */
    protected final void setVideoTitle(String title) {
        mVideoController.setVideoTitle(title);
    }

    /**
     * 通知视频控制器完成时提示信息
     *
     * @param completedText 文本内容
     */
    protected final void notifyControllerToCompleted(String completedText) {
        mVideoController.notifyControllerToCompleted(completedText);
    }

    /**
     * 通知视频播放控制器恢复初始状态
     */
    protected final void notifyControllerToRestore() {
        mVideoController.notifyControllerToRestore();
    }

    /**
     * 获取当前视频控制器
     */
    @NonNull
    protected final VideoController getController() {
        return mVideoController;
    }

    @Override
    public final void onBack() {
        finish();
    }

    @Override
    public final void onSwitchOrientation(int orientation) {
        setRequestedOrientation(orientation);
    }

    @Override
    public void onCatalog() {
        // 目录回调
    }

    @Override
    public void onSwitchSpeed() {
        // 倍速回调
    }

    @Override
    public void onSwitchDefinition() {
        // 清晰度回调
    }

    @Override
    public void onMore() {
        // 标题栏更多内容回调
    }

    @Override
    public final void onCompleted(IMediaPlayer mediaPlayer) {
        onPlayerCompleted(mediaPlayer);
    }

    @Override
    public final void onError(IMediaPlayer mediaPlayer) {
        mPrepared = false;
        mError = true;
        onPlayerError(mediaPlayer);
    }

    @Override
    public final boolean onPrepared(IMediaPlayer mediaPlayer) {
        initPrepared();
        onPlayerPrepared(mediaPlayer);
        return mResumed;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mVideoController != null) {
            if (mVideoController.onKeyDown(keyCode)) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mVideoController != null) {
            mVideoController.onConfigurationChanged(newConfig);
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            addContentViewToRoot();
        } else {
            removeContentViewFromRoot();
        }
    }

    @Override
    protected void onResume() {
        this.mResumed = true;
        super.onResume();

        if (mPlayStateBeforeOnPause && mPrepared) {
            mPlayer.start();
        }
        if (!mPlayStateBeforeOnPause && mPrepared && !mError) {
            mVideoController.show();
        }

        if (hasGravity()) {
            openGravityDetector();
        }
    }

    @Override
    protected void onPause() {
        this.mResumed = false;
        if (mPrepared) {
            mPlayStateBeforeOnPause = mPlayer.isPlaying();
        } else {
            mPlayStateBeforeOnPause = true;
        }
        super.onPause();

        if (mPlayer != null) {
            mPlayer.pause();
        }

        if (mDetector != null) {
            mDetector.disable();
        }

        if (mVideoController.isShowing()) {
            mVideoController.hide(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoController != null) {
            mVideoController.release();
        }
        if (mPlayer != null) {
            mPlayer.stopPlayback();
            mPlayer.release(true);
            mPlayer.releaseWithoutStop();
        }
    }

    private void initPrepared() {
        mPrepared = true;
        mError = false;
    }

    private void removeContentViewFromRoot() {
        if (mContentView != null) {
            final ViewParent parent = mContentView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(mContentView);
            }
        }
    }

    private void addContentViewToRoot() {
        removeContentViewFromRoot();
        if (mRoot != null && mContentView != null) {
            mRoot.addView(mContentView);
        }
    }

    private void createVideoController() {
        mVideoController = new Builder()
                .host(mPlayer)
                .screenSwitcher(switcher())
                .controllerCallback(this)
                .optionsCallback(this)
                .build();
    }

    private void openGravityDetector() {
        if (mDetector == null) {
            mDetector = new GravityDetector(this);
        }
        mDetector.enable();
    }
}
