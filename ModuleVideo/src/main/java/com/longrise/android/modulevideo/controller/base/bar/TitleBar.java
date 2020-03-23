package com.longrise.android.modulevideo.controller.base.bar;

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
public final class TitleBar extends BaseBar {

    private View mBack;
    private TextView mTitle;
    private View mMore;

    private OnTitleBarListener mCallback;

    public TitleBar(ViewGroup host) {
        super(host);
    }

    public void setTitle(String text) {
        mTitle.setText(text);
    }

    @Override
    protected int getBarLayoutId() {
        return R.layout.modulevideo_controller_titlebar;
    }

    public void showOrHideMore(boolean show) {
        if (mMore != null) {
            mMore.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void initBar() {
        mBack = findViewById(R.id.modulevideo_titlebar_iv_back);
        mTitle = findViewById(R.id.modulevideo_titlebar_tv_title);
        mMore = findViewById(R.id.modulevideo_titlebar_iv_more);
    }

    @Override
    protected void regEvent(boolean event) {
        mBack.setOnClickListener(event ? this : null);
        mMore.setOnClickListener(event ? this : null);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.modulevideo_titlebar_iv_back) {
            if (mCallback != null) {
                mCallback.onTitleBack();
            }
        } else if (id == R.id.modulevideo_titlebar_iv_more) {
            if (mCallback != null) {
                mCallback.onMore();
            }
        }
    }

    public interface OnTitleBarListener {

        /**
         * On back event of {@link TitleBar}
         */
        void onTitleBack();

        void onMore();
    }

    public void bindTitlebarListener(OnTitleBarListener callback) {
        this.mCallback = callback;
    }
}
