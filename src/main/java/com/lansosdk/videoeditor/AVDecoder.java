package com.lansosdk.videoeditor;

import android.util.Log;

import java.nio.IntBuffer;

/**
 * 注意: 此类采用的ffmpeg中的软解码来做的, 如果您感觉软解有点慢, 我们提供了异步线程解码的形式, 可以加速解码处理,请联系我们.
 */
public class AVDecoder {
    /**
     * @param filename 音频文件名字.[当前请不要用AAC编码的,m4a后缀的文件]
     * @return
     */
    public static native long audioInit(String filename);

    /**
     * @param handle 当前没有用到
     * @param seekUs 当前没有用到
     * @param out    需要外面创建好数据. 如果是(44100, 双通道的MP3格式,则数组大小建议是1152*4)
     * @return 返回当前解码一帧的字节数. 如果字节等于0,则认为是解码结束.
     */
    public static native int audioDecode(long handle, long seekUs, byte[] out);

    /**
     * 当前解码的是否是最后一帧.
     *
     * @param handle
     * @return
     */
    public static native boolean audioIsEnd(long handle);

    /**
     * 释放当前音频解码器
     *
     * @param handle 暂时没有用到
     * @return
     */
    public static native int audioRelease(long handle);

    /**
     * 一下是代码测试. FileWriteUtils write=new FileWriteUtils("/sdcard/hddd3.pcm");
     * AVDecoder.audioInit("/sdcard/hongdou.mp3"); byte[] pcmOut=new
     * byte[1152*4]; //这个是44100, 双通道的
     *
     * while(true){ int ret=AVDecoder.audioDecode((long)0, (long)0, pcmOut);
     * if(AVDecoder.audioIsEnd((long)0)){ break; }else{ //
     * Log.i(TAG,"audio decode ret is:"+ret); write.writeFile(pcmOut); }
     * if(ret<=0){ break; } } write.closeWriteFile();
     * AVDecoder.audioRelease((long)0);
     */

    // ------------------------------------------------------------------------

    /**
     * [视频解码]
     *
     * @param filepath
     * @return
     */
    public static native long decoderInit(String filepath);

    /**
     * [视频解码] 解码一帧, 发送上去. seekUS大于等于0, 说明要seek, 注意:如果您设置了seek大于等于0,
     * 因为视频编码原理是基于IDR刷新帧的, seek时会选择在你设置时间的最近前一个IDR刷新帧的位置,请注意!
     * <p>
     * <p>
     * 这里只seek一次开始解码, 解码后直接把数据发送上去. 用decoderIsEnd来判断当前是否已经解码好.
     * <p>
     * 建议:如果您的需求每次都解码同一个视频,视频总帧数在20帧以下,并每帧的字节不是很大, 建议一次解码后,
     * 用list保存起来,不用每次都解码同一个视频.
     *
     * @param handle 当前文件的句柄,
     * @param seekUs 是否要seek, 大于等于0说明要seek; 如果seek的时间大于视频的总时长,则返回视频最后一帧的画面;
     * @param out    输出. 数组由外部创建, 创建时的大小应等于 视频的宽度*高度*4;
     *               注意: 此类采用的ffmpeg中的软解码来做的, 如果您感觉软解有点慢, 我们提供了异步线程解码的形式,
     *               可以加速解码处理,请联系我们.
     * @return 返回的是当前帧的时间戳.单位是US 微秒
     */
    public static native long decoderFrame(long handle, long seekUs, int[] out);

    /**
     * [视频解码] 释放当前解码器.
     *
     * @param handle
     * @return
     */
    public static native int decoderRelease(long handle);

    /**
     * [视频解码] 解码是否到文件尾.
     * <p>
     * 如果seek的位置,大于视频的时长,则返回视频最后一帧的时间戳和画面;
     *
     * @param handle
     * @return
     */
    public static native boolean decoderIsEnd(long handle);

    /**
     * 临时为了获取一个bitmap图片,临时测试.
     *
     * @param src
     */
    public static void testGetFirstOnekey(String src) {
        long decoderHandler = 0;
        IntBuffer mGLRgbBuffer;
        MediaInfo info = new MediaInfo(src);
        if (info.prepare()) {
            decoderHandler = AVDecoder.decoderInit(src);
            if (decoderHandler != 0) {
                mGLRgbBuffer = IntBuffer.allocate(info.vWidth * info.vHeight);
                long beforeDraw = System.currentTimeMillis();
                mGLRgbBuffer.position(0);
                AVDecoder.decoderFrame(decoderHandler, -1, mGLRgbBuffer.array());
                Log.i("TIME",
                        "draw comsume time is :"
                                + (System.currentTimeMillis() - beforeDraw));
                AVDecoder.decoderRelease(decoderHandler);

                // 转换为bitmap
                // Bitmap stitchBmp = Bitmap.createBitmap(info.vWidth ,
                // info.vHeight, Bitmap.Config.ARGB_8888);
                // stitchBmp.copyPixelsFromBuffer(mGLRgbBuffer);
                // saveBitmap(stitchBmp); //您可以修改下, 然后返回bitmap
                // 这里得到的图像在mGLRgbBuffer中, 可以用来返回一张图片.
                decoderHandler = 0;
            }
        } else {
            Log.e("TAG", "get first one key error!");
        }
    }
    /**
     * 代码测试.
     *
     * 测试证明, 是可以多个线程同时解码的. private void testDecoder() { new Thread(new
     * Runnable() {
     *
     * @Override public void run() { // TODO Auto-generated method stub
     *           testDecoder2("/sdcard/480x480.mp4","/sdcard/480x480.yuv"); }
     *           }).start();
     *
     *           new Thread(new Runnable() {
     * @Override public void run() { // TODO Auto-generated method stub
     *           testDecoder2("/sdcard/ping20s.mp4","/sdcard/ping20s.yuv"); }
     *           }).start(); }
     *
     *
   //2018年6月15日19:01:39 再次测试, 是按照顺序解码的, 如果发现解码顺序不对, 可能是视频中有B帧;

    private void testDecoder3(String src) {
    private void testDecoder3(String src) {
    long decoderHandler;
    IntBuffer mGLRgbBuffer;

    int index=0;
    MediaInfo gifInfo = new MediaInfo(src);
    if (gifInfo.prepare()) {

    long starttime = System.currentTimeMillis();
    decoderHandler = AVDecoder.decoderInit(src);
    mGLRgbBuffer = IntBuffer.allocate(gifInfo.vWidth * gifInfo.vHeight);

    Log.i(TAG,"RGBA时间戳是:---->开始");
    index=0;
    while (AVDecoder.decoderIsEnd(decoderHandler) == false) {
    mGLRgbBuffer.position(0);
    long time=AVDecoder.decoderFrame(decoderHandler, -1, mGLRgbBuffer.array());
    Log.i(TAG,"RGBA时间戳是:---->"+time);
    index++;
    }
    AVDecoder.decoderRelease(decoderHandler);
    Log.i(TAG, "RGBA 解码用时:" + (System.currentTimeMillis() - starttime));
    }
    }

     */
}
