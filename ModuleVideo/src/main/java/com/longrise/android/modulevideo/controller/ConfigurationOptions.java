package com.longrise.android.modulevideo.controller;

/**
 * Created by godliness on 2020-03-13.
 *
 * @author godliness
 * 视频控制器相关选项配置(懒加载配置项)
 */
public final class ConfigurationOptions {

    /**
     * 是否显示倍速选项，默认不显示
     */
    public boolean mShowSpeed;
    /**
     * 是否显示清晰度选项，默认不显示
     */
    public boolean mShowDefinition;
    /**
     * 是否显示目录选项，默认不显示
     */
    public boolean mShowCatalog;
    /**
     * 是否系显示更多选项，默认不显示
     */
    public boolean mShowMore;
    /**
     * 是否允许拖动进度条，默认允许，该值将影响 mMaxDragPosition
     */
    public boolean mDragProgress = true;
    /**
     * 允许最大拖动位置，该值不能大于当前资源的最大值
     */
    public int mMaxDragPosition;
}
