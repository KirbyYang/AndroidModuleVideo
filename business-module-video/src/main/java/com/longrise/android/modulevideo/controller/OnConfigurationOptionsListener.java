package com.longrise.android.modulevideo.controller;

/**
 * Created by godliness on 2020-03-13.
 *
 * @author godliness
 * 视频控制器配置回调
 */
public interface OnConfigurationOptionsListener {

    /**
     * 是否允许方向切换
     *
     * @return boolean
     */
    boolean hasSwitcher();


    /**
     * 配置相关选项
     *
     * @param options {@link ConfigurationOptions}
     */
    void onConfigurationControllerOptions(ConfigurationOptions options);

}
