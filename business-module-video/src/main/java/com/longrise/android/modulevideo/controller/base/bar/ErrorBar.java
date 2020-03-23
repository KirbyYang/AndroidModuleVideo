package com.longrise.android.modulevideo.controller.base.bar;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.longrise.android.modulevideo.R;
import com.longrise.android.modulevideo.controller.base.bar.base.BaseBar;

/**
 * Created by godliness on 2020-03-07.
 *
 * @author godliness
 */
public final class ErrorBar extends BaseBar {

    private TextView mInfo;
    private View mRetry;

    private OnErrorBarListener mCallback;

    public ErrorBar(ViewGroup host) {
        super(host);
    }

    public void showErrorBar(boolean showRetry, String text) {
        show();
        mRetry.setVisibility(showRetry ? View.VISIBLE : View.GONE);
        mInfo.setText(text);
        mInfo.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void show() {
        super.show(false);
    }

    @Override
    public void hide() {
        removeFromController();
    }

    @Override
    protected int getBarLayoutId() {
        return R.layout.modulevideo_controller_loadingstatus;
    }

    @Override
    protected void initBar() {
        mInfo = findViewById(R.id.modulevideo_loading_error_info);
        mRetry = findViewById(R.id.modulevideo_loading_error_btn);
    }

    @Override
    protected void regEvent(boolean event) {
        mRetry.setOnClickListener(event ? this : null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.modulevideo_loading_error_btn) {
            if (mCallback != null) {
                mCallback.onErrorBarRetry();
            }
        }
    }

    public void bindErrorBarCallback(OnErrorBarListener callback) {
        this.mCallback = callback;
    }

    public interface OnErrorBarListener {

        void onErrorBarRetry();
    }

}
