package com.longrise.android.modulevideo.controller.base.bar;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.longrise.android.modulevideo.R;

/**
 * Created by godliness on 2020-03-07.
 *
 * @author godliness
 */
public final class PortraitBar extends BaseControllerBar {

    private ImageView mPlay;
    private TextView mCurrent;
    private TextView mEnd;
    private SeekBar mProgress;
    private View mOrientation;

    private OnControllerBarListener mCallback;

    public PortraitBar(ViewGroup host) {
        super(host);
    }

    @Override
    protected int getBarLayoutId() {
        return R.layout.modulevideo_video_controller_portrait;
    }

    @Override
    protected void initBar() {
        mPlay = findViewById(R.id.modulevideo_controller_bar_iv_play_portrait);
        mCurrent = findViewById(R.id.modulevideo_controller_bar_tv_current_portrait);
        mEnd = findViewById(R.id.modulevideo_controller_bar_tv_end_portrait);
        mProgress = findViewById(R.id.modulevideo_controller_bar_progress_portrait);
        mOrientation = findViewById(R.id.modulevideo_controller_bar_iv_orientation_portrait);
    }

    @Override
    protected void regEvent(boolean event) {
        mPlay.setOnClickListener(event ? this : null);
        mOrientation.setOnClickListener(event ? this : null);
    }

    @Override
    public void updatePlayWidgetDrawable(Drawable drawable) {
        mPlay.setImageDrawable(drawable);
    }

    @Override
    public void updateCurrentProgress(String text) {
        mCurrent.setText(text);
    }

    @Override
    public void updateProgressChanged(int progress, int percent, String current, String end) {
        mProgress.setProgress(progress);
        mProgress.setSecondaryProgress(percent);
        mCurrent.setText(current);
        mEnd.setText(end);
    }

    @Override
    public void bindSeekBarChangeListener(SeekBar.OnSeekBarChangeListener callback) {
        mProgress.setOnSeekBarChangeListener(callback);
    }

    @Override
    public void bindControllerBarListener(OnControllerBarListener callback) {
        this.mCallback = callback;
    }

    @Override
    public void enableDragProgress(boolean drag) {
        mProgress.setEnabled(drag);
    }

    @Override
    public int getScreenOrientation() {
        return Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    public void enableSwitchScreenOrientation(boolean enable) {
        mOrientation.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.modulevideo_controller_bar_iv_play_portrait) {
            if (mCallback != null) {
                mCallback.onPlay();
            }
        } else if (id == R.id.modulevideo_controller_bar_iv_orientation_portrait) {
            if (mCallback != null) {
                mCallback.onOrientation();
            }
        }
    }
}