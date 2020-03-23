package com.longrise.android.modulevideo.controller.base.bar;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.longrise.android.modulevideo.controller.base.bar.base.BaseBar;


/**
 * Created by godliness on 2020-03-09.
 *
 * @author godliness
 */
public abstract class BaseControllerBar extends BaseBar {

    BaseControllerBar(ViewGroup host) {
        super(host);
    }

    public boolean inSameOrientation(int currentOrientation) {
        return currentOrientation == getScreenOrientation();
    }

    public abstract void updatePlayWidgetDrawable(Drawable drawable);

    public abstract void updateCurrentProgress(String current);

    public abstract void updateProgressChanged(int progress, int percent, String current, String end);

    public abstract void bindSeekBarChangeListener(SeekBar.OnSeekBarChangeListener callback);

    public abstract void bindControllerBarListener(OnControllerBarListener callback);

    public abstract void enableDragProgress(boolean drag);

    public abstract int getScreenOrientation();

    public void enableSwitchScreenOrientation(boolean enable) {

    }

    public interface OnControllerBarListener {

        /**
         * Play or pause
         */
        void onPlay();

        /**
         * On switch orientation event
         */
        void onOrientation();

        /**
         * On switch speed event
         */
        void onSpeed();

        /**
         * On switch definition event
         */
        void onDefinition();

        /**
         * On switch catalog event
         */
        void onCatalog();
    }
}
