package com.longrise.android.modulevideo.controller.base.bar;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.longrise.android.modulevideo.R;

/**
 * Created by godliness on 2020-03-07.
 *
 * @author godliness
 */
public final class LandscapeBar extends BaseControllerBar {

    private ImageView mPlay;
    private TextView mCurrent;
    private TextView mEnd;
    private SeekBar mProgress;

    private View mOptionsView;
    private View mSpeed;
    private View mDefinition;
    private View mCatalog;

    private OnControllerBarListener mCallback;

    public LandscapeBar(ViewGroup host) {
        super(host);
    }

    public void options(boolean showSpeed, boolean showDefinition, boolean showCatalog) {
        if (!showSpeed && !showDefinition && !showCatalog) {
            return;
        }
        initOptionsView();
        mSpeed.setVisibility(showSpeed ? View.VISIBLE : View.GONE);
        mDefinition.setVisibility(showDefinition ? View.VISIBLE : View.GONE);
        mCatalog.setVisibility(showCatalog ? View.VISIBLE : View.GONE);
    }

    @Override
    protected int getBarLayoutId() {
        return R.layout.modulevideo_video_controller_landscape;
    }

    @Override
    protected void initBar() {
        mPlay = findViewById(R.id.modulevideo_controller_bar_iv_play_landscape);
        mCurrent = findViewById(R.id.modulevideo_controller_tv_current_landscape);
        mEnd = findViewById(R.id.modulevideo_controller_tv_end_landscape);
        mProgress = findViewById(R.id.modulevideo_video_controller_progress_landscape);
    }

    @Override
    protected void regEvent(boolean event) {
        mPlay.setOnClickListener(event ? this : null);
    }

    @Override
    public void updatePlayWidgetDrawable(Drawable drawable) {
        mPlay.setImageDrawable(drawable);
    }

    @Override
    public void updateCurrentProgress(String current) {
        mCurrent.setText(current);
    }

    @Override
    public void updateProgressChanged(int progress, int percent, String current, String end) {
        mProgress.setProgress(progress);
        mProgress.setSecondaryProgress(percent);
        mCurrent.setText(current);
        mEnd.setText(String.format("/%s", end));
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
        return Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.modulevideo_controller_bar_iv_play_landscape) {
            if (mCallback != null) {
                mCallback.onPlay();
            }
        } else if (id == R.id.modulevideo_controller_tv_speed) {
            if (mCallback != null) {
                mCallback.onSpeed();
            }
        } else if (id == R.id.modulevideo_controller_tv_definition) {
            if (mCallback != null) {
                mCallback.onDefinition();
            }
        } else if (id == R.id.modulevideo_controller_tv_catalog) {
            if (mCallback != null) {
                mCallback.onCatalog();
            }
        }
    }

    private void initOptionsView() {
        if (mOptionsView == null) {
            final ViewStub optionsView = findViewById(R.id.modulevideo_controller_vs_options_landscape);
            mOptionsView = optionsView.inflate();
            mSpeed = findViewById(R.id.modulevideo_controller_tv_speed);
            mDefinition = findViewById(R.id.modulevideo_controller_tv_definition);
            mCatalog = findViewById(R.id.modulevideo_controller_tv_catalog);
            mDefinition.setOnClickListener(this);
            mSpeed.setOnClickListener(this);
            mCatalog.setOnClickListener(this);
        }
    }
}
