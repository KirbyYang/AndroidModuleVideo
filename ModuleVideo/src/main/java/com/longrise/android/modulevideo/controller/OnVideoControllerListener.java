package com.longrise.android.modulevideo.controller;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by godliness on 2020-03-02.
 *
 * @author godliness
 */
public interface OnVideoControllerListener {

    void onBack();

    void onSwitchOrientation(int orientation);

    void onCatalog();

    void onSwitchSpeed();

    void onSwitchDefinition();

    void onRetry();

    void onCompleted(IMediaPlayer mediaPlayer);

    void onError(IMediaPlayer mediaPlayer);

    boolean onPrepared(IMediaPlayer mediaPlayer);

    void onMore();
}
