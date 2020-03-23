package com.godliness.android.modulevideodemo;

import android.os.Bundle;

import com.longrise.android.modulevideo.BaseVideoActivity;
import com.longrise.android.modulevideo.controller.ConfigurationOptions;
import com.longrise.android.modulevideo.controller.OnConfigurationOptionsListener;
import com.longrise.android.modulevideo.controller.OnVideoControllerListener;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by godliness on 2020-03-09.
 *
 * @author godliness
 * <p>
 * BaseVideoActivity 使用用例
 * <p>
 * 组件基于MVP框架搭建，可以完善MVP任务
 */
public final class VideoDemoActivity extends BaseVideoActivity implements OnVideoControllerListener, OnConfigurationOptionsListener {

    private final String mVideo = "http://download.yxybb.com/bbvideo/2017/9/hlwbxxqyfxq1_3.mp4";
    private int mCurrentPosition;

    @Override
    protected int getVideoLayoutResourceId(Bundle savedInstanceState) {
        return R.layout.modulevideo_demo_activity;
    }

    @Override
    protected void initLayout() {
        // 初始化相关资源，或者网络请求
        setVideoPath("https://rioweb.szns.gov.cn/xinyitech249/xinyitech/44030504041310267199-17686764130126903533/hls.m3u8?uuid=14523783076505592038");
        setVideoTitle("设置视频标题栏");
    }

    @Override
    protected void regEvent(boolean regEvent) {
    }

    @Override
    protected void onPlayerCompleted(IMediaPlayer mediaPlayer) {
        // 当前视频资源播放完成
        // 通过 notifyControllerToCompleted 通知视频播放控制器-完成时相关状态
        notifyControllerToCompleted("视频播放完成");
    }

    @Override
    protected void onPlayerPrepared(IMediaPlayer mediaPlayer) {
        // 视频资源准备完成
    }

    @Override
    protected void onPlayerError(IMediaPlayer mediaPlayer) {
        // 视频播放出错

        // 获取获取当前播放进度，以便在恢复时设置断点
        this.mCurrentPosition = (int) mediaPlayer.getCurrentPosition();
    }

    @Override
    public void onRetry() {
        // 视屏播放出错后，重试回调
        setVideoPath(mVideo, mCurrentPosition);
    }

    @Override
    public void onConfigurationControllerOptions(ConfigurationOptions options) {
        options.mShowSpeed = true;
        options.mShowDefinition = true;
        options.mShowCatalog = true;
        options.mShowMore = true;
        options.mDragProgress = false;
        options.mMaxDragPosition = 300000;
        options.mShowMore = true;
    }
}
