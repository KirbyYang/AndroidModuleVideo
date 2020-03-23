package com.longrise.android.modulevideo.options.speed;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.longrise.android.modulevideo.R;

/**
 * Created by godliness on 2020-02-27.
 *
 * @author godliness
 */
final class SpeedAdapter extends RecyclerView.Adapter<SpeedAdapter.SpeedViewHolder> {

    private final Context mTarget;

    private String[] mSpeeds;
    private int mCurrentIndex;

    private OnSpeedAdapterClickListener mOnItemClickListener;

    SpeedAdapter(Context target) {
        this.mTarget = target;
        this.mSpeeds = target.getResources().getStringArray(R.array.modulevideo_play_speed);
    }

    void selectedIndex(int index) {
        this.mCurrentIndex = index;
    }

    void setOnSpeedAdapterItemListener(OnSpeedAdapterClickListener itemListener) {
        this.mOnItemClickListener = itemListener;
    }

    interface OnSpeedAdapterClickListener {

        /**
         * 通知点击的倍速类型
         *
         * @param speeds 倍速类型
         * @param index  下标
         */
        void onSpeedAdapterItemClick(String[] speeds, int index);
    }

    @NonNull
    @Override
    public SpeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final LayoutInflater inflater = LayoutInflater.from(mTarget);
        return new SpeedViewHolder(inflater.inflate(R.layout.modulevideo_speed_rcv_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SpeedViewHolder speedViewHolder, final int i) {
        final String value = mSpeeds[i];
        speedViewHolder.mTvSpeed.setText(value + "X");

        if (this.mCurrentIndex == i) {
            speedViewHolder.mTvSpeed.setTextColor(mTarget.getResources().getColor(R.color.modulevideo_color_ff9600));
            speedViewHolder.mLine.setBackgroundDrawable(mTarget.getResources().getDrawable(R.drawable.modulevideo_shape_speed_item_line));
        } else {
            speedViewHolder.mTvSpeed.setTextColor(mTarget.getResources().getColor(android.R.color.white));
            speedViewHolder.mLine.setVisibility(View.INVISIBLE);
        }

        if (!speedViewHolder.itemView.hasOnClickListeners()) {
            speedViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onSpeedAdapterItemClick(mSpeeds, i);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mSpeeds == null ? 0 : mSpeeds.length;
    }

    static final class SpeedViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTvSpeed;
        private final View mLine;

        SpeedViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mTvSpeed = itemView.findViewById(R.id.modulevideo_speed_item_tv);
            this.mLine = itemView.findViewById(R.id.modulevideo_speed_item_line);
        }
    }
}
