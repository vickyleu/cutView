package com.lansosdk.videoeditor;

/**
 * Created by sno on 2018/4/18.
 */

public class VideoLayoutParam {

    /**
     * 完整的视频路径
     */
    public String video;

    /**
     * 在布局的时候, 当前视频的左上角0,0布局到输出尺寸的 横向坐标X位置
     */
    public int x;

    /**
     * 在布局的时候, 当前视频的左上角0,0布局到输出尺寸的 横向坐标Y位置
     */
    public int y;

    /**
     * 输入的视频宽度是否要缩放; 如果不缩放则这里等于视频的宽度;
     * <p>
     * 默认不建议缩放, 因为当前采用的是ffmpeg内部的软件缩放,缩放耗时严重;
     */
    public int scaleW;

    /**
     * 输入的视频高度是否要缩放; 如果不缩放则这里等于视频的高度;
     * <p>
     * 默认不建议缩放, 因为当前采用的是ffmpeg内部的软件缩放,缩放耗时严重;
     */
    public int scaleH;

}
