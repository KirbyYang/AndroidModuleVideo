package com.longrise.android.modulevideo.controller;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.longrise.android.modulevideo.R;
import com.longrise.android.modulevideo.controller.base.BaseController;
import com.longrise.android.modulevideo.controller.base.bar.BaseControllerBar;
import com.longrise.android.modulevideo.controller.base.bar.ErrorBar;
import com.longrise.android.modulevideo.controller.base.bar.LandscapeBar;
import com.longrise.android.modulevideo.controller.base.bar.PortraitBar;
import com.longrise.android.modulevideo.controller.base.bar.TitleBar;
import com.longrise.android.modulevideo.controller.base.delegate.ShadowDelegate;
import com.longrise.android.modulevideo.ijk.IjkVideoView;

import java.util.Formatter;
import java.util.Locale;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by godliness on 2020-03-07.
 *
 * @author godliness
 */
public final class VideoController extends BaseController implements View.OnTouchListener, ShadowDelegate.OnShadowDelegateListener, Handler.Callback
        , IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnCompletionListener {

    private final int DEFAULT_HIDE_CONTROLLER_TIMEOUT = 3000;
    private final int UPDATE_PROGRESS_MSG = 0;
    private final int HIDE_MSG = 1;
    private final int TO_FULL_SCREEN = 2;
    private final int TO_WRAP_SCREEN = 3;
    private final int ENABLE_ON_TOUCH = 4;

    private TitleBar mTitleBar;
    private ErrorBar mErrorBar;
    private BaseControllerBar mControllerBar;

    private IjkVideoView mHost;
    private GestureDetector mGestureDetector;
    private Handler mControllerHandler;
    private ShadowDelegate mShadow;

    private SeekBar.OnSeekBarChangeListener mSeekBarChangeCallback;
    private BaseControllerBar.OnControllerBarListener mControllerBarCallback;
    private ErrorBar.OnErrorBarListener mErrorBarCallback;
    private TitleBar.OnTitleBarListener mTitleBarCallback;

    private final StringBuilder mFormatBuilder;
    private final Formatter mFormatter;

    private final OnVideoControllerListener mCallback;
    private final OnConfigurationOptionsListener mOptionsCallback;
    private final Switcher mSwitcher;
    private ConfigurationOptions mOptions;

    private boolean mShowing;
    private boolean mCompleted;

    private int mCurrentPosition;
    private String mVideoTitle;

    private Drawable mPlayDrawable;
    private Drawable mPauseDrawable;

    VideoController(Builder builder) {
        super(builder.mHost);
        this.mSwitcher = builder.mSwitcher;
        this.mCallback = builder.mControllerCallback;
        this.mOptionsCallback = builder.mOptionsCallback;

        this.mFormatBuilder = new StringBuilder();
        this.mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        initControllerHandler();
        getShadow().showOrHideShadowOrientation(mOptionsCallback.hasSwitcher() && screenOrientationFromPortrait());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mSwitcher != null) {
            mSwitcher.onConfigurationChanged(newConfig);
            adjustShowModeFromConfigurationChanged();
        }
    }

    public void setVideoPath(String path, int position) {
        this.mCurrentPosition = position;
        mHost.setVideoPath(path);
    }

    public void setVideoTitle(String title) {
        if (mTitleBar != null) {
            mTitleBar.setTitle(title);
        } else {
            this.mVideoTitle = title;
        }
    }

    @Override
    public void show() {
        show(DEFAULT_HIDE_CONTROLLER_TIMEOUT);
    }

    public void show(int timeOut) {
        if (!mShowing) {
            showController();
            mShowing = true;

            mControllerHandler.sendEmptyMessageDelayed(ENABLE_ON_TOUCH, 300);
        }

        mControllerHandler.sendEmptyMessage(UPDATE_PROGRESS_MSG);

        final Message msg = mControllerHandler.obtainMessage(HIDE_MSG);
        if (timeOut != 0) {
            mControllerHandler.removeMessages(HIDE_MSG);
            mControllerHandler.sendMessageDelayed(msg, timeOut);
        }

        updatePlayWidgetState();
    }

    @Override
    public void hide() {
        hide(true);
    }

    public void hide(boolean openAnimation) {
        if (mShowing) {
            hideController(openAnimation);
            mShowing = false;

            mControllerHandler.sendEmptyMessageDelayed(ENABLE_ON_TOUCH, 300);
        }

        mControllerHandler.removeMessages(HIDE_MSG);
        mControllerHandler.removeMessages(UPDATE_PROGRESS_MSG);
    }

    @Override
    public boolean isShowing() {
        return mShowing;
    }

    @Override
    public void release() {
        super.release();
        mControllerHandler.removeCallbacksAndMessages(null);
        if (mControllerBar != null) {
            mControllerBar.release();
            mControllerBar = null;
        }
    }

    public void buffering(boolean show) {
        getShadow().buffering(show);
    }

    public void notifyControllerToCompleted(String completedText) {
        buffering(false);
        getShadow().showShadow();
        showErrorBar(false, completedText);
    }

    public void notifyControllerToRestore() {
        hide(false);
        pause();
        buffering(true);
        enableShowControllerBar(false);
        getShadow().showShadow();
        hideErrorBar();
    }

    public void notifyControllerToError(String errorText) {
        hide(false);
        buffering(false);
        enableShowControllerBar(false);
        getShadow().showShadow();
        showErrorBar(true, errorText);
    }

    public boolean onKeyDown(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (screenOrientationFromPortrait()) {
                return false;
            }
            mControllerHandler.sendEmptyMessage(TO_WRAP_SCREEN);
            return true;
        }
        return false;
    }

    @Override
    protected int getControllerViewId() {
        return R.layout.modulevideo_video_controller;
    }

    @Override
    protected void initControllerView() {
        final ViewGroup controllerView = getControllerView();
        controllerView.setOnTouchListener(this);
        controllerView.setEnabled(false);
        initShadowDelegate();
    }

    @Override
    protected void regEvent(boolean isClick) {
        final IjkVideoView player = getHost();
        player.setOnPreparedListener(isClick ? this : null);
        player.setOnInfoListener(isClick ? this : null);
        player.setOnErrorListener(isClick ? this : null);
        player.setOnCompletionListener(isClick ? this : null);

        getShadow().setOnShadowListener(isClick ? this : null);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HIDE_MSG:
                hide();
                return true;

            case UPDATE_PROGRESS_MSG:
                final int pos = setProgress();
                if (mShowing && isPlaying()) {
                    msg = mControllerHandler.obtainMessage(UPDATE_PROGRESS_MSG);
                    mControllerHandler.sendMessageDelayed(msg, 1000 - (pos % 1000));
                }
                return true;

            case TO_FULL_SCREEN:
                switchScreenOrientationFromUser(false);
                return true;

            case TO_WRAP_SCREEN:
                switchScreenOrientationFromUser(true);
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onShadowBack() {
        mControllerHandler.sendEmptyMessage(TO_WRAP_SCREEN);
    }

    @Override
    public void onShadowOrientation() {
        mControllerHandler.sendEmptyMessage(TO_FULL_SCREEN);
    }

    @Override
    public void onShadowMore() {
        if (mCallback != null) {
            mCallback.onMore();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        initHandGesture().onTouchEvent(event);
        return true;
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        if (mCallback != null) {
            mCallback.onCompleted(iMediaPlayer);
        }
        this.mCompleted = true;
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
        if (!hasNetwork()) {
            notifyControllerToError(getString(R.string.modulevideo_string_network_disconnected));
        } else {
            notifyControllerToError(getString(R.string.modulevideo_string_play_error) + extra);
        }
        if (mCallback != null) {
            mCallback.onError(iMediaPlayer);
        }
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int i1) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                onInfoToBuffering();
                return true;

            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                buffering(false);
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        seekTo(mCurrentPosition);
        if (mCallback != null && mCallback.onPrepared(iMediaPlayer)) {
            start();
        }

        mControllerHandler.removeCallbacks(mInitRunnable);
        mControllerHandler.postDelayed(mInitRunnable, 800);

        this.mCompleted = false;
    }

    private Runnable mInitRunnable = new Runnable() {
        @Override
        public void run() {
            //delay 1s 用于屏蔽画面过渡
            initPrepared();
        }
    };

    private void initPrepared() {
        buffering(false);
        hideErrorBar();
        hideShadowBar(true);
        enableShowControllerBar(true);
        show();
    }

    private void seekTo(int currentPosition) {
        getHost().seekTo(currentPosition);
    }

    private boolean isPlaying() {
        return getHost().isPlaying();
    }

    private void start() {
        getHost().start();
    }

    private void pause() {
        getHost().pause();
    }

    private int getDuration() {
        return getHost().getDuration();
    }

    private int getCurrentPosition() {
        return getHost().getCurrentPosition();
    }

    private int getBufferPercentage() {
        return getHost().getBufferPercentage();
    }

    private IjkVideoView getHost() {
        if (mHost == null) {
            return mHost = (IjkVideoView) getHostView();
        }
        return mHost;
    }

    private void showController() {
        final BaseControllerBar currentControllerBar = getCurrentControllerBar();
        if (screenOrientationFromPortrait()) {
            if (getOptions().mShowMore) {
                getShadow().showShadowMore();
            }
            getShadow().showOrHideShadowBack(true);
        } else {
            showTitleBar();
            getShadow().showOrHideShadowBack(false);
        }
        currentControllerBar.show();
    }

    private void hideController(boolean openAnimation) {
        if (mTitleBar != null) {
            mTitleBar.hide(openAnimation);
        }
        if (mControllerBar != null) {
            mControllerBar.hide(openAnimation);
        }
        if (getOptions().mShowMore) {
            getShadow().hideShadowMore(openAnimation);
        }
    }

    private void showTitleBar() {
        if (mTitleBar == null) {
            mTitleBar = new TitleBar(getControllerView());
            mTitleBar.bindTitlebarListener(getTitleBarCallback());
            mTitleBar.showOrHideMore(getOptions().mShowMore);
        }
        mTitleBar.setTitle(mVideoTitle);
        mTitleBar.show();
    }

    private void showErrorBar(boolean showRetry, String text) {
        if (mErrorBar == null) {
            mErrorBar = new ErrorBar(getControllerView());
            mErrorBar.bindErrorBarCallback(getErrorBarCallback());
        }
        mErrorBar.showErrorBar(showRetry, text);
    }

    private void hideErrorBar() {
        if (mErrorBar != null) {
            mErrorBar.hide();
        }
    }

    private void hideShadowBar(boolean openAnimation) {
        hideErrorBar();
        getShadow().showOrHideShadowBack(screenOrientationFromPortrait());
        getShadow().showOrHideShadowOrientation(false);

        getShadow().hideShadow(openAnimation);
    }

    private void enableShowControllerBar(boolean enable) {
        getControllerView().setEnabled(enable);
        getShadow().showOrHideShadowBack(!enable || screenOrientationFromPortrait());
        getShadow().showOrHideShadowOrientation(!enable && screenOrientationFromPortrait());
    }

    private void initShadowDelegate() {
        getShadow();
    }

    private ShadowDelegate getShadow() {
        if (mShadow == null) {
            mShadow = new ShadowDelegate(getControllerView());
        }
        return mShadow;
    }

    private GestureDetector initHandGesture() {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(getContext(), new VideoControllerOnGesture());
        }
        return mGestureDetector;
    }

    private void doPlayState() {
        if (mShowing) {
            show();
        }

        if (isPlaying()) {
            pause();

            changePlayWidgetDrawable(getPlayDrawable());
        } else {
            start();
            changePlayWidgetDrawable(getPauseDrawable());
        }

        hideShadowBar(false);
        mCompleted = false;
    }

    private int setProgress() {
        int duration = getDuration();
        int position = getCurrentPosition();

        if (mCompleted) {
            if (position < duration) {
                position = duration;
            }
        }

        final String current = stringForTime(position);
        final String end = stringForTime(duration);

        if (duration > 0) {
            final long pos = 1000L * position / duration;
            if (mControllerBar != null) {
                mControllerBar.updateProgressChanged((int) pos, getBufferPercentage(), current, end);
            }
        }
        return position;
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void initControllerHandler() {
        if (mControllerHandler == null) {
            mControllerHandler = new Handler(this);
        }
    }

    private BaseControllerBar getCurrentControllerBar() {
        if (mControllerBar == null) {
            mControllerBar = createControllerBarFromOrientation();
        } else {
            if (!mControllerBar.inSameOrientation(getConfigurationOrientation())) {
                mControllerBar = createControllerBarFromOrientation();
            }
        }
        return mControllerBar;
    }

    private BaseControllerBar createControllerBarFromOrientation() {
        BaseControllerBar controllerBar;
        if (screenOrientationFromPortrait()) {
            controllerBar = createPortraitBar();
        } else {
            controllerBar = createLandscapeBar();
        }
        controllerBar.bindSeekBarChangeListener(getSeekBarChangeCallback());
        controllerBar.bindControllerBarListener(getControllerBarCallback());
        controllerBar.enableDragProgress(getOptions().mDragProgress || getOptions().mMaxDragPosition > 0);
        controllerBar.enableSwitchScreenOrientation(mOptionsCallback.hasSwitcher());
        return controllerBar;
    }

    private PortraitBar createPortraitBar() {
        return new PortraitBar(getControllerView());
    }

    private LandscapeBar createLandscapeBar() {
        final LandscapeBar landscapeBar = new LandscapeBar(getControllerView());
        final ConfigurationOptions options = getOptions();
        landscapeBar.options(options.mShowSpeed, options.mShowDefinition, options.mShowCatalog);
        return landscapeBar;
    }

    private void switchScreenOrientationFromUser(boolean pressedBack) {
        if (mCallback != null) {
            if (screenOrientationFromPortrait()) {
                if (pressedBack) {
                    mCallback.onBack();
                } else {
                    mCallback.onSwitchOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            } else {
                if (mOptionsCallback.hasSwitcher()) {
                    mCallback.onSwitchOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    mCallback.onBack();
                }
            }
        }
    }

    private void changePlayWidgetDrawable(Drawable drawable) {
        if (mControllerBar != null) {
            mControllerBar.updatePlayWidgetDrawable(drawable);
        }
    }

    private void updatePlayWidgetState() {
        if (mControllerBar != null) {
            mControllerBar.updatePlayWidgetDrawable(isPlaying() ? getPauseDrawable() : getPlayDrawable());
        }
    }

    private void onInfoToBuffering() {
        buffering(true);
        hideErrorBar();
    }

    private ConfigurationOptions getOptions() {
        if (mOptions == null) {
            mOptions = new ConfigurationOptions();
            if (mOptionsCallback != null) {
                mOptionsCallback.onConfigurationControllerOptions(mOptions);
            }
        }
        return mOptions;
    }

    private void adjustShowModeFromConfigurationChanged() {
        if (!getControllerView().isEnabled()) {
            getShadow().showOrHideShadowBack(true);
            getShadow().showOrHideShadowOrientation(screenOrientationFromPortrait());
            return;
        }
        if (mShowing) {
            hide(false);
            show();
        } else {
            getShadow().showOrHideShadowBack(screenOrientationFromPortrait());
        }
    }

    private Drawable getPlayDrawable() {
        if (mPlayDrawable == null) {
            mPlayDrawable = getDrawable(R.drawable.modulevideo_selector_iv_play);
        }
        return mPlayDrawable;
    }

    private Drawable getPauseDrawable() {
        if (mPauseDrawable == null) {
            mPauseDrawable = getDrawable(R.drawable.modulevideo_selector_iv_pause);
        }
        return mPauseDrawable;
    }

    private final class VideoControllerOnGesture extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mControllerHandler.hasMessages(ENABLE_ON_TOUCH)) {
                return false;
            }
            if (mShowing) {
                hide();
            } else {
                show();
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            doPlayState();
            return false;
        }
    }

    private SeekBar.OnSeekBarChangeListener getSeekBarChangeCallback() {
        if (mSeekBarChangeCallback == null) {
            mSeekBarChangeCallback = new SeekBar.OnSeekBarChangeListener() {

                private long mChangePosition;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (!fromUser) {
                        return;
                    }

                    long duration = getDuration();
                    mChangePosition = (duration * progress) / 1000L;
                    if (mControllerBar != null) {
                        mControllerBar.updateCurrentProgress(stringForTime((int) mChangePosition));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    final ConfigurationOptions options = getOptions();
                    if (!options.mDragProgress) {
                        final int currentPosition = getCurrentPosition();
                        if (currentPosition > options.mMaxDragPosition) {
                            options.mMaxDragPosition = currentPosition;
                        }
                    }
                    show(3600000);
                    mControllerHandler.removeMessages(UPDATE_PROGRESS_MSG);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    final ConfigurationOptions options = getOptions();
                    if (options.mDragProgress) {
                        seekTo((int) mChangePosition);
                    } else if (options.mMaxDragPosition > 0) {
                        if (mChangePosition <= options.mMaxDragPosition) {
                            seekTo((int) mChangePosition);
                        } else {
                            seekTo(options.mMaxDragPosition);
                        }
                    }

                    setProgress();
                    updatePlayWidgetState();
                    show(DEFAULT_HIDE_CONTROLLER_TIMEOUT);

                    mControllerHandler.sendEmptyMessage(UPDATE_PROGRESS_MSG);

                    if (mCompleted) {
                        hideShadowBar(false);
                        mCompleted = false;
                    }
                }
            };
        }
        return mSeekBarChangeCallback;
    }

    private BaseControllerBar.OnControllerBarListener getControllerBarCallback() {
        if (mControllerBarCallback == null) {
            mControllerBarCallback = new BaseControllerBar.OnControllerBarListener() {
                @Override
                public void onPlay() {
                    doPlayState();
                }

                @Override
                public void onOrientation() {
                    mControllerHandler.sendEmptyMessage(TO_FULL_SCREEN);
                }

                @Override
                public void onSpeed() {
                    hide();
                    if (mCallback != null) {
                        mCallback.onSwitchSpeed();
                    }
                }

                @Override
                public void onDefinition() {
                    hide();
                    if (mCallback != null) {
                        mCallback.onSwitchDefinition();
                    }
                }

                @Override
                public void onCatalog() {
                    hide();
                    if (mCallback != null) {
                        mCallback.onCatalog();
                    }
                }
            };
        }
        return mControllerBarCallback;
    }

    private ErrorBar.OnErrorBarListener getErrorBarCallback() {
        if (mErrorBarCallback == null) {
            mErrorBarCallback = new ErrorBar.OnErrorBarListener() {
                @Override
                public void onErrorBarRetry() {
                    if (hasNetwork()) {
                        notifyControllerToRestore();
                        if (mCallback != null) {
                            mCallback.onRetry();
                        }
                    }
                }
            };
        }
        return mErrorBarCallback;
    }

    private TitleBar.OnTitleBarListener getTitleBarCallback() {
        if (mTitleBarCallback == null) {
            mTitleBarCallback = new TitleBar.OnTitleBarListener() {
                @Override
                public void onTitleBack() {
                    mControllerHandler.sendEmptyMessage(TO_WRAP_SCREEN);
                }

                @Override
                public void onMore() {
                    hide();
                    if (mCallback != null) {
                        mCallback.onMore();
                    }
                }
            };
        }
        return mTitleBarCallback;
    }
}
