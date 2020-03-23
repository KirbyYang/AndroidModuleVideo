package com.longrise.android.modulevideo.controller;

import android.view.ViewGroup;

/**
 * Created by godliness on 2020-03-09.
 *
 * @author godliness
 */
public final class Builder {

    ViewGroup mHost;
    Switcher mSwitcher;
    OnVideoControllerListener mControllerCallback;
    OnConfigurationOptionsListener mOptionsCallback;

    public Builder host(ViewGroup host) {
        this.mHost = host;
        return this;
    }

    public Builder screenSwitcher(Switcher switcher) {
        this.mSwitcher = switcher;
        return this;
    }

    public Builder controllerCallback(OnVideoControllerListener callback) {
        this.mControllerCallback = callback;
        return this;
    }

    public Builder optionsCallback(OnConfigurationOptionsListener optionsCallback) {
        this.mOptionsCallback = optionsCallback;
        return this;
    }

    public VideoController build() {
        if (mHost == null) {
            throw new NullPointerException("host == null");
        }
        if(mControllerCallback == null){
            throw new NullPointerException("ControllerCallback == null");
        }
        if(mOptionsCallback == null){
            throw new NullPointerException("OptionsCallback == null");
        }
        if (mSwitcher == null) {
            mSwitcher = new Switcher();
        }
        mSwitcher.onConfiguartionCreated(mHost);
        return new VideoController(this);
    }
}
