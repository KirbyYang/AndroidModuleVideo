package com.longrise.android.modulevideo.options.definition;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.longrise.android.modulevideo.R;

/**
 * Created by godliness on 2020-02-28.
 *
 * @author godliness
 */
public final class DefinitAdapter extends RecyclerView.Adapter<DefinitAdapter.DefinitViewHolder> {

    private final Context mCxt;

    private DefinitBean[] mDefinitBeans;
    private int mCurrentIndex;

    private OnDefinitAdapterClickListener mCallback;

    interface OnDefinitAdapterClickListener {

        void onDefinitAdapterItemClick(DefinitBean definit, int index);
    }

    void selectedIndex(int index) {
        this.mCurrentIndex = index;
    }

    void setOnDefinitAdapterItemListener(OnDefinitAdapterClickListener adapterItemListener) {
        this.mCallback = adapterItemListener;
    }

    void setData(DefinitBean[] definitBeans) {
        this.mDefinitBeans = definitBeans;
    }

    DefinitAdapter(Context cxt) {
        this.mCxt = cxt;
    }

    @NonNull
    @Override
    public DefinitViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final LayoutInflater inflater = LayoutInflater.from(mCxt);
        return new DefinitViewHolder(inflater.inflate(R.layout.modulevideo_definition_item_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DefinitViewHolder definitViewHolder, final int i) {
        final DefinitBean bean = mDefinitBeans[i];
        definitViewHolder.mTv.setText(bean.text);

        if (mCurrentIndex == 0) {
            definitViewHolder.mTv.setTextColor(mCxt.getResources().getColor(R.color.modulevideo_color_ff9600));
        } else {
            definitViewHolder.mTv.setTextColor(mCxt.getResources().getColor(android.R.color.white));
        }

        definitViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onDefinitAdapterItemClick(bean, i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDefinitBeans == null ? 0 : mDefinitBeans.length;
    }

    static final class DefinitViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTv;

        public DefinitViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mTv = itemView.findViewById(R.id.modulevideo_definition_item_tv);
        }
    }
}
