package com.lansosdk.videoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.lansosdk.box.AudioPad;
import com.lansosdk.box.AudioSource;
import com.lansosdk.box.BitmapLayer;
import com.lansosdk.box.CanvasLayer;
import com.lansosdk.box.CanvasRunnable;
import com.lansosdk.box.DrawPad;
import com.lansosdk.box.FileParameter;
import com.lansosdk.box.TimeRange;
import com.lansosdk.box.VideoLayer;
import com.lansosdk.box.onAudioPadCompletedListener;
import com.lansosdk.box.onAudioPadProgressListener;
import com.lansosdk.box.onDrawPadCompletedListener;
import com.lansosdk.box.onDrawPadErrorListener;
import com.lansosdk.box.onDrawPadProgressListener;
import com.lansosdk.box.onDrawPadThreadProgressListener;

import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageFilter;

/**
 * 说明1:用来演示DrawPad, 同时处理裁剪,缩放,压缩,剪切,增加文字, 增加logo等信息. 我们的DrawPad是一个容器,
 * 内部可以放任意图层,并调节图层的各种移动等. 这里仅仅是演示 视频图层+图片图层+Canvas图层的组合.
 * 您可以参考我们其他的各种例子,来实现您的具体需求.
 * <p>
 * 说明2: 如果你有除了我们列举的功能外, 还有做别的, 可以直接拷贝这个类, 然后删除没用的, 增加上你的图层, 来完成您的需求.
 * <p>
 * 说明3: 如果列举的功能,可以满足您的需求,则调用形式是这样的: 场景1: 只裁剪+logo: 则: videoOneDo=new
 * VideoOneDo(getApplicationContext(), videoPath);
 * <p>
 * videoOneDo.setOnVideoOneDoProgressListener(进度监听);
 * videoOneDo.setOnVideoOneDoCompletedListener(完成监听, 返回处理后的结果);
 * videoOneDo.setCropRect(startX, startY, cropW, cropH); //裁剪
 * videoOneDo.setLogo(bmp, VideoOneDo.LOGO_POSITION_RIGHT_TOP); //加logo
 * videoOneDo.start(); //开启另一个线程成功返回true, 失败返回false
 * <p>
 * 场景2: 增加背景音乐, 剪切时长+logo+文字等 则: 创建对象 ===>各种set===> 开始执行
 */
public class VideoOneDo {

    public final static int LOGO_POSITION_LELF_TOP = 0;
    public final static int LOGO_POSITION_LEFT_BOTTOM = 1;
    public final static int LOGO_POSITION_RIGHT_TOP = 2;
    public final static int LOGO_POSITION_RIGHT_BOTTOM = 3;
    public final static int VIDEOONEDO_ERROR_DSTERROR = 6001;
    public final static int VIDEOONEDO_ERROR_SRCFILE = 6002;
    public final static int VIDEOONEDO_ERROR_DRAWPAD = 6003;
    private static final String TAG = "VideoOneDo";
    protected String videoPath = null;
    protected MediaInfo srcInfo;
    protected float tmpvDuration = 0.0f;// drawpad处理后的视频时长.

    protected String dstPath = null;

    protected DrawPadVideoExecute drawPad = null;
    protected AudioPadExecute audioPad;
    protected String audioPadSavePath = null;

    protected boolean isExecuting = false;

    protected VideoLayer videoLayer = null;
    protected BitmapLayer logoBmpLayer = null;
    protected CanvasLayer canvasLayer = null;

    protected Context context;

    // ------------------视频参数.-------------------------------
    protected long startTimeUs = 0;
    protected long cutDurationUs = 0;
    protected FileParameter fileParamter = null;
    protected int startX, startY, cropWidth, cropHeight;
    protected GPUImageFilter videoFilter = null;

    protected Bitmap logoBitmap = null;
    protected int logoPosition = LOGO_POSITION_RIGHT_TOP;
    protected int scaleWidth, scaleHeight;
    protected float compressFactor = 1.0f;
    protected long videoBitRate = 0;

    protected String textAdd = null;
    // -------------------音频参数--------------------
    protected String musicPath = null;
    protected MediaInfo musicInfo;
    protected boolean isMixBgMusic; // 是否要混合背景音乐.
    protected float bgMusicStartTime = 0.0f;
    protected float bgMusicEndTime = 0.0f;
    protected float bgMusicVolume = 0.8f; // 默认减少一点.
    protected float mainMusicVolume = 1.0f;
    protected ArrayList<String> deleteArray = new ArrayList<String>();
    private List<TimeRange> timeStretchArray = null;
    private List<TimeRange> timeFreezeArray = null;
    private List<TimeRange> timeRepeatArray = null;
    private boolean isEditModeVideo;
    private onVideoOneDoProgressListener monVideoOneDoProgressListener;
    private onVideoOneDoCompletedListener monVideoOneDOCompletedListener = null;
    private onVideoOneDoErrorListener monVideoOneDoErrorListener = null;

    /**
     * 构造方法
     *
     * @param ctx       android的Context语境
     * @param videoPath 要处理的视频文件;
     */
    public VideoOneDo(Context ctx, String videoPath) {
        this.videoPath = videoPath;
        context = ctx;
    }

    /**
     * 增加背景音乐. 暂时只支持MP3和aac. 如果背景音乐是MP3格式, 我们会转换为AAC格式. 如果背景音乐时间 比视频短,则会循环播放.
     * 如果背景音乐时间 比视频长,则会从开始截取等长部分.
     *
     * @param path
     */
    public void setBackGroundMusic(String path) {
        musicInfo = new MediaInfo(path, false);
        if (musicInfo.prepare() && musicInfo.isHaveAudio()) {
            musicPath = path;
        } else {
            Log.e(TAG, "设置背景音乐出错, 音频文件有误.请查看" + musicInfo.toString());
            musicPath = null;
            musicInfo = null;
        }
    }

    /**
     * 背景音乐是否要和原视频中的声音混合, 即同时保留原音和背景音乐, 背景音乐通常音量略低一些.
     * <p>
     * 暂时只支持MP3和aac. 如果背景音乐是MP3格式, 我们会转换为AAC格式. 如果背景音乐时间 比视频短,则会循环播放. 如果背景音乐时间
     * 比视频长,则会从开始截取等长部分.
     *
     * @param path
     * @param isMix  是否增加,
     * @param volume 如增加,则背景音乐的音量调节 =1.0f为不变, 小于1.0降低; 大于1.0提高; 最大2.0;
     */
    public void setBackGroundMusic(String path, boolean isMix, float volume) {
        setBackGroundMusic(path);
        if (musicInfo != null) {
            isMixBgMusic = isMix;
            bgMusicVolume = volume;
        }
    }

    /**
     * 增加背景音乐, 背景音乐是否和原声音混合, 混合时各自的音量.
     * <p>
     * 暂时只支持MP3和aac. 如果背景音乐是MP3格式, 我们会转换为AAC格式. 如果背景音乐时间 比视频短,则会循环播放. 如果背景音乐时间
     * 比视频长,则会从开始截取等长部分.
     *
     * @param path
     * @param isMix      是否混合
     * @param mainVolume 原音频的音量, 1.0f为原音量; 小于则降低, 大于则放大,
     * @param bgVolume
     */
    public void setBackGroundMusic(String path, boolean isMix,
                                   float mainVolume, float bgVolume) {
        setBackGroundMusic(path, isMix, bgVolume);
        mainMusicVolume = mainVolume;
    }

    /**
     * 增加背景音乐,并裁剪背景音乐, 背景音乐是否和原声音混合, 混合时各自的音量.
     * <p>
     * 暂时只支持MP3和aac. 如果背景音乐是MP3格式, 我们会转换为AAC格式. 如果背景音乐时间 比视频短,则会循环播放. 如果背景音乐时间
     * 比视频长,则会从开始截取等长部分.
     *
     * @param path       背景音乐路径
     * @param startTime  背景音乐的开始时间, 单位秒, 如2.5f,则表示从2.5秒处裁剪
     * @param endTime    背景音乐的结束时间, 单位秒, 如10.0f,则表示裁剪到10.0f为止;
     * @param isMix      是否混合元声音, 即保留原声音
     * @param mainVolume 原声音的音量,1.0f为原音量; 小于则降低, 大于则放大,
     * @param bgVolume   背景音乐的音量
     */
    public void setBackGroundMusic(String path, float startTime, float endTime,
                                   boolean isMix, float mainVolume, float bgVolume) {
        setBackGroundMusic(path, isMix, bgVolume);
        mainMusicVolume = mainVolume;
        if (musicInfo != null && startTime > 0.0f && startTime < endTime
                && endTime <= musicInfo.aDuration) {
            bgMusicStartTime = startTime;
            bgMusicEndTime = endTime;
        }
    }

    /**
     * 缩放到的目标宽度和高度.
     *
     * @param scaleW
     * @param scaleH
     */
    public void setScaleWidth(int scaleW, int scaleH) {
        if (scaleW > 0 && scaleH > 0) {
            scaleWidth = scaleW;
            scaleHeight = scaleH;
        }
    }

    /**
     * 设置压缩比, 此压缩比,在运行时, 会根据缩放后的比例,计算出缩放后的码率 压缩比乘以 缩放后的码率, 等于实际的码率, 如果您缩放后,
     * 建议不要再设置压缩
     *
     * @param percent 压缩比, 值范围0.0f---1.0f;
     */
    public void setCompressPercent(float percent) {
        if (percent > 0.0f && isEditModeVideo == false) {
            compressFactor = percent;
        }
    }

    /**
     * 直接设置码率
     *
     * @param bitrate
     */
    public void setBitrate(long bitrate) {
        if (bitrate > 0 && isEditModeVideo == false) {
            compressFactor = 1.0f;
            videoBitRate = bitrate;
        }
    }

    /**
     * 设置视频的开始位置,等于截取视频中的一段 单位微秒, 如果你打算从2.3秒处开始处理,则这里的应该是2.3*1000*1000; 支持精确截取.
     *
     * @param timeUs
     */
    public void setStartPostion(long timeUs) {
        startTimeUs = timeUs;
    }

    /**
     * 设置结束时间 支持精确截取.
     *
     * @param timeUs
     */
    public void setEndPostion(long timeUs) {
        if (timeUs > 0 && timeUs > startTimeUs) {
            cutDurationUs = timeUs - startTimeUs;
        }
        if (cutDurationUs < 1000 * 1000) {
            Log.w(TAG, "警告: 你设置的最终时间小于1秒,可能只有几帧的时间.请注意!");
        }
    }

    /**
     * 设置截取视频中的多长时间. 单位微秒, 支持精确截取.
     *
     * @param timeUs
     */
    public void setCutDuration(long timeUs) {
        if (timeUs > 0) {
            cutDurationUs = timeUs;
        }
        if (cutDurationUs < 1000 * 1000) {
            Log.w(TAG, "警告: 你设置的最终时间小于1秒,可能只有几帧的时间.请注意!");
        }
    }

    /**
     * 设置裁剪画面的一部分用来处理, 依靠视频呈现出怎样的区域来裁剪.
     * 比如视频显示720x1280,则您可以认为视频画面的宽度就是720,高度就是1280;不做其他角度的判断.
     * <p>
     * 裁剪后, 如果设置了缩放,则会把cropW和cropH缩放到指定的缩放宽度.
     *
     * @param startX 画面的开始横向坐标,
     * @param startY 画面的结束纵向坐标
     * @param cropW  裁剪多少宽度
     * @param cropH  裁剪多少高度
     */
    public void setCropRect(int startX, int startY, int cropW, int cropH) {
        fileParamter = new FileParameter();
        this.startX = startX;
        this.startY = startY;
        cropWidth = cropW;
        cropHeight = cropH;
    }

    /**
     * 这里仅仅是举例,用一个滤镜.如果你要增加多个滤镜,可以判断处理进度,来不断切换滤镜
     *
     * @param filter
     */
    public void setFilter(GPUImageFilter filter) {
        videoFilter = filter;
    }

    /**
     * 设置logo的位置, 这里仅仅是举例,您可以拷贝这个代码, 自行定制各种功能. 原理: 增加一个图片图层到容器DrawPad中, 设置他的位置.
     * 位置这里举例是: {@link #LOGO_POSITION_LEFT_BOTTOM}
     * {@link #LOGO_POSITION_LELF_TOP} {@link #LOGO_POSITION_RIGHT_BOTTOM}
     * {@value #LOGO_POSITION_RIGHT_TOP}
     *
     * @param bmp      logo图片对象
     * @param position 位置
     */
    public void setLogo(Bitmap bmp, int position) {
        logoBitmap = bmp;
        if (position <= LOGO_POSITION_RIGHT_BOTTOM) {
            logoPosition = position;
        }
    }

    public void setEditModeVideo() {
        isEditModeVideo = true;
        compressFactor = 1.0f;
        videoBitRate = 0;
    }

    /**
     * 增加文字, 这里仅仅是举例, 原理: 增加一个CanvasLayer图层, 把文字绘制到Canvas图层上. 文字的位置,
     * 是Canvas绘制出来的.
     *
     * @param text
     */
    public void setText(String text) {
        textAdd = text;
    }

    /**
     * 增加时间拉伸;
     * <p>
     * 可以增加多段, 比如前一段慢放, 后一段加速, 如果中间有不设置的, 则默认原速播放;
     *
     * @param startTimeUs 开始时间,
     * @param endTimeUs   结束时间.
     * @param speed       时间范围是0.5---2.0, 0.5是放慢一倍. 2.0是加速快进一倍;
     */
    public void addTimeStretch(long startTimeUs, long endTimeUs, float speed) {
        if (timeStretchArray == null) {
            timeStretchArray = new ArrayList<TimeRange>();
        }
        timeStretchArray.add(new TimeRange(startTimeUs, endTimeUs, speed));
    }

    /**
     * 注释同上; 只是您可以把前台预览收集到的多个拉伸数组放进来;
     *
     * @param timearray
     */
    public void addTimeStretch(List<TimeRange> timearray) {
        timeStretchArray = timearray;
    }

    /**
     * 增加时间冻结,即在视频的什么时间段开始冻结, 静止的结束时间; 为了统一: 这里用结束时间; 比如你要从原视频的5秒地方开始静止, 静止3秒钟,
     * 则这里是3*1000*1000 , 8*1000*1000 (画面停止的过程中, 可以做一些缩放,移动等特写等)
     *
     * @param startTimeUs 从输入的视频/音频的哪个时间点开始冻结,
     * @param endTimeUs   (这里理解为:冻结的时长+开始时间);
     */
    public void addTimeFreeze(long startTimeUs, long endTimeUs) {
        if (timeFreezeArray == null) {
            timeFreezeArray = new ArrayList<TimeRange>();
        }
        timeFreezeArray.add(new TimeRange(startTimeUs, endTimeUs));
    }

    public void addTimeFreeze(List<TimeRange> list) {
        timeFreezeArray = list;
    }

    /**
     * 增加时间重复; 把原视频中的 从开始到结束这一段时间, 重复. 可以设置重复次数;
     * <p>
     * 类似综艺节目中, 当意外发生的时候, 多次重复的效果.
     *
     * @param startUs 相对原视频/原音频的开始时间;
     * @param endUs   相对原视频/原音频的结束时间;
     * @param loopcnt 重复的次数;
     */
    public void addTimeRepeat(long startUs, long endUs, int loopcnt) {
        if (timeRepeatArray == null) {
            timeRepeatArray = new ArrayList<TimeRange>();
        }
        timeRepeatArray.add(new TimeRange(startUs, endUs, loopcnt));
    }

    public void addTimeRepeat(List<TimeRange> list) {
        timeRepeatArray = list;
    }

    public void setOnVideoOneDoProgressListener(onVideoOneDoProgressListener li) {
        monVideoOneDoProgressListener = li;
    }

    public void setOnVideoOneDoCompletedListener(
            onVideoOneDoCompletedListener li) {
        monVideoOneDOCompletedListener = li;
    }

    public void setOnVideoOneDoErrorListener(onVideoOneDoErrorListener li) {
        monVideoOneDoErrorListener = li;
    }

    /**
     * 进度回调
     *
     * @param v
     * @param currentTimeUs
     */
    public void progressCallback(DrawPad v, long currentTimeUs) {
        /**
         * 您可以继承我们这个类, 然后在这里随着进度来增加您自己的其他代码. (此代码工作在UI线程)
         */
    }

    /**
     * 进度回调(工作在线程内)
     *
     * @param v
     * @param currentTimeUs
     */
    public void progressThreadCallback(DrawPad v, long currentTimeUs) {
        /**
         * 您可以继承我们这个类, 然后在这里随着进度来增加您自己的其他代码.
         * (此代码工作在DrawPad线程,可以增加一些更精确的操作,或纹理操作等. 但不能增加UI操作.)
         */
    }

    /**
     * 开始执行, 内部会开启一个线程去执行. 开启成功,返回true. 失败返回false;
     *
     * @return
     */
    public boolean start() {
        if (isExecuting)
            return false;

        srcInfo = new MediaInfo(videoPath, false);
        if (srcInfo.prepare() == false) {
            if (monVideoOneDoErrorListener != null) {
                monVideoOneDoErrorListener.oError(this, VIDEOONEDO_ERROR_SRCFILE);
            }
            return false;
        }

        if (startTimeUs > 0 || cutDurationUs > 0) // 有剪切.
        {
            long du = (long) (srcInfo.vDuration * 1000 * 1000);
            long aDuration = (long) (srcInfo.aDuration * 1000 * 1000);
            if (aDuration > 0) {
                du = Math.min(du, aDuration);
            }
            if (startTimeUs > du) {
                startTimeUs = 0;
                Log.w(TAG, "开始时间无效,恢复为0...");
            }
            if (du < (startTimeUs + cutDurationUs)) { // 如果总时间 小于要截取的时间,则截取时间默认等于总时间.
                cutDurationUs = 0;
                Log.w(TAG, "剪切时长无效,恢复为0...");
            }
        }
        if (srcInfo.isHaveAudio() == false) {
            isMixBgMusic = false;// 没有音频则不混合.
        }

        isExecuting = true;
        dstPath = SDKFileUtils.createMp4FileInBox();

        tmpvDuration = srcInfo.vDuration;
        if (cutDurationUs > 0 && cutDurationUs < (srcInfo.vDuration * 1000000)) {
            tmpvDuration = (float) cutDurationUs / 1000000f;
        }

        if (isOnlyDoMusic()) { // 如果仅有音频,则用音频容器即可.没有必要把视频执行一遍;
            return startAudioPad();
        } else {
            return startDrawPad();
        }

    }

    private boolean startDrawPad() {
        // 先判断有无裁剪画面
        if (cropHeight > 0 && cropWidth > 0) {
            fileParamter = new FileParameter();
            fileParamter.setDataSoure(videoPath);

            /**
             * 设置当前需要显示的区域 ,以左上角为0,0坐标.
             * @param startX 开始的X坐标, 即从宽度的什么位置开始
             * @param startY 开始的Y坐标, 即从高度的什么位置开始
             * @param cropW 需要显示的宽度
             * @param cropH 需要显示的高度.
             */
            fileParamter.setShowRect(startX, startY, cropWidth, cropHeight);
            fileParamter.setStartTimeUs(startTimeUs);

            int padWidth = cropWidth;
            int padHeight = cropHeight;
            if (scaleHeight > 0 && scaleWidth > 0) {
                padWidth = scaleWidth;
                padHeight = scaleHeight;
            }

            float f = (float) (padHeight * padWidth) / (float) (fileParamter.info.vWidth * fileParamter.info.vHeight);
            float bitrate = f * fileParamter.info.vBitRate * compressFactor * 2.0f;
            if (videoBitRate > 0) {
                bitrate = videoBitRate;
            }
            drawPad = new DrawPadVideoExecute(context, fileParamter, padWidth,
                    padHeight, (int) bitrate, videoFilter, dstPath);
        } else { // 没有裁剪

            int padWidth = srcInfo.getWidth();
            int padHeight = srcInfo.getHeight();

            float bitrate = (float) srcInfo.vBitRate * compressFactor * 1.5f;
            if (videoBitRate > 0) {
                bitrate = videoBitRate;
            } else {
                if (scaleHeight > 0 && scaleWidth > 0) {
                    padWidth = scaleWidth;
                    padHeight = scaleHeight;
                    float f = (float) (padHeight * padWidth)
                            / (float) (srcInfo.vWidth * srcInfo.vHeight);
                    bitrate *= f;
                }
            }
            if (videoBitRate > 0) {
                bitrate = videoBitRate;
            }

            drawPad = new DrawPadVideoExecute(context, videoPath, startTimeUs,
                    padWidth, padHeight, (int) bitrate, videoFilter, dstPath);
        }

        if (isEditModeVideo) {
            drawPad.setEditModeVideo(isEditModeVideo);
        }
        drawPad.setDrawPadErrorListener(new onDrawPadErrorListener() {

            @Override
            public void onError(DrawPad d, int what) {
                if (monVideoOneDoErrorListener != null) {
                    monVideoOneDoErrorListener.oError(VideoOneDo.this,
                            VIDEOONEDO_ERROR_DRAWPAD);
                }
            }
        });
        /**
         * 设置DrawPad处理的进度监听, 回传的currentTimeUs单位是微秒.
         */
        drawPad.setDrawPadProgressListener(new onDrawPadProgressListener() {
            @Override
            public void onProgress(DrawPad v, long currentTimeUs) {

                progressCallback(v, currentTimeUs);
                if (monVideoOneDoProgressListener != null) {
                    float time = (float) currentTimeUs / 1000000f;

                    float percent = time / (float) tmpvDuration;

                    float b = (float) (Math.round(percent * 100)) / 100; // 保留两位小数.
                    if (b < 1.0f && monVideoOneDoProgressListener != null
                            && isExecuting) {
                        monVideoOneDoProgressListener.onProgress(
                                VideoOneDo.this, b);
                    }
                }
                if (cutDurationUs > 0 && currentTimeUs > cutDurationUs) { // 设置了结束时间,
                    // 如果当前时间戳大于结束时间,则停止容器.
                    drawPad.stopDrawPad();
                }
            }
        });
        // 容器内部的进度回调.
        drawPad.setDrawPadThreadProgressListener(new onDrawPadThreadProgressListener() {

            @Override
            public void onThreadProgress(DrawPad v, long currentTimeUs) {
                progressThreadCallback(v, currentTimeUs);
            }
        });

        /**
         * 设置DrawPad处理完成后的监听.
         */
        drawPad.setDrawPadCompletedListener(new onDrawPadCompletedListener() {

            @Override
            public void onCompleted(DrawPad v) {
                completeDrawPad();
            }
        });

        /**
         * 增加音频参数.
         */
        addAudioParamter();

        /**
         * 增加时间拉伸.
         */
        if (timeStretchArray != null) {
            drawPad.addTimeStretch(timeStretchArray);
        }

        if (timeRepeatArray != null) {
            drawPad.addTimeRepeat(timeRepeatArray);
        }

        if (timeFreezeArray != null) {
            drawPad.addTimeFreeze(timeFreezeArray);
        }

        drawPad.pauseRecord();

        if (drawPad.startDrawPad()) {
            videoLayer = drawPad.getMainVideoLayer();
            videoLayer.setScaledValue(videoLayer.getPadWidth(), videoLayer.getPadHeight());

            addBitmapLayer(); // 增加图片图层

            addCanvasLayer(); // 增加文字图层.
            drawPad.resumeRecord(); // 开始恢复处理.
            return true;
        } else {
            return false;
        }
    }

    /**
     * 处理完成后的动作.
     */
    protected void completeDrawPad() {
        if (isExecuting == false) {
            return;
        }

        if (audioPad != null) { // 只有音频的情况

            if (SDKFileUtils.fileExist(audioPadSavePath)) {
                mergeAV(audioPadSavePath);
                SDKFileUtils.deleteFile(audioPadSavePath);
                audioPadSavePath = null;
            } else {
                Log.e(TAG, "音频容器执行失败,请把打印信息发给我们.谢谢!");
            }
        }
        MediaInfo info = new MediaInfo(dstPath, false);
        if (info.prepare()) {
            if (monVideoOneDOCompletedListener != null && isExecuting) {
                monVideoOneDOCompletedListener.onCompleted(VideoOneDo.this,
                        dstPath);
            }
        } else {
            Log.e(TAG, "VideoOneDo执行错误!!!");
            if (monVideoOneDoErrorListener != null && isExecuting) {
                monVideoOneDoErrorListener.oError(this,
                        VIDEOONEDO_ERROR_DSTERROR);
            }
        }

        isExecuting = false;

        // Log.d(TAG,"最后的视频文件是:"+MediaInfo.checkFile(dstPath));
    }

    public void stop() {
        if (isExecuting) {
            isExecuting = false;

            monVideoOneDOCompletedListener = null;
            monVideoOneDoProgressListener = null;
            if (drawPad != null) {
                drawPad.stopDrawPad();
                drawPad = null;
            }
            if (audioPad != null) {
                audioPad.release();
                audioPad = null;
                SDKFileUtils.deleteFile(audioPadSavePath);
                audioPadSavePath = null;
            }
            for (String string : deleteArray) {
                SDKFileUtils.deleteFile(string);
            }
            deleteArray.clear();

            videoPath = null;
            srcInfo = null;
            drawPad = null;

            logoBitmap = null;
            textAdd = null;
            musicInfo = null;
        }
    }

    public void release() {
        stop();
    }

    /**
     * 增加图片图层
     */
    private void addBitmapLayer() {
        // 如果需要增加图片.
        if (logoBitmap != null) {
            logoBmpLayer = drawPad.addBitmapLayer(logoBitmap);
            if (logoBmpLayer != null) {
                int w = logoBmpLayer.getLayerWidth();
                int h = logoBmpLayer.getLayerHeight();
                if (logoPosition == LOGO_POSITION_LELF_TOP) { // 左上角.

                    logoBmpLayer.setPosition(w / 2, h / 2); // setPosition设置的是当前中心点的方向;

                } else if (logoPosition == LOGO_POSITION_LEFT_BOTTOM) { // 左下角

                    logoBmpLayer.setPosition(w / 2, logoBmpLayer.getPadHeight()
                            - h / 2);
                } else if (logoPosition == LOGO_POSITION_RIGHT_TOP) { // 右上角

                    logoBmpLayer.setPosition(
                            logoBmpLayer.getPadWidth() - w / 2, h / 2);

                } else if (logoPosition == LOGO_POSITION_RIGHT_BOTTOM) { // 右下角
                    logoBmpLayer.setPosition(
                            logoBmpLayer.getPadWidth() - w / 2,
                            logoBmpLayer.getPadHeight() - h / 2);
                } else {
                    Log.w(TAG, "logo默认居中显示");
                }
            }
        }
    }

    /**
     * 增加Android的Canvas类图层.
     */
    private void addCanvasLayer() {
        if (textAdd != null) {
            canvasLayer = drawPad.addCanvasLayer();

            canvasLayer.addCanvasRunnable(new CanvasRunnable() {

                @Override
                public void onDrawCanvas(CanvasLayer layer, Canvas canvas,
                                         long currentTimeUs) {
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    paint.setAntiAlias(true);
                    paint.setTextSize(20);
                    canvas.drawText(textAdd, 20, 20, paint);
                }
            });
        }
    }

    /**
     * 在运行drawpad的情况下, 增加音频的参数.
     */
    private void addAudioParamter() {
        if (drawPad != null && musicInfo != null) {
            /**
             * 第一步: 增加一个音频;
             */
            AudioSource source = null;
            if (bgMusicEndTime > bgMusicStartTime && bgMusicStartTime > 0) {
                source = drawPad
                        .addSubAudio(
                                musicPath,
                                0,
                                (long) (bgMusicStartTime * 1000 * 1000),
                                (long) ((bgMusicEndTime - bgMusicStartTime) * 1000 * 1000));
            } else {
                source = drawPad.addSubAudio(musicPath);
            }
            source.setLooping(true);
            source.setVolume(bgMusicVolume);

            /**
             * 第二步:是否保留原有的声音;
             */
            if (isMixBgMusic == false) {
                drawPad.getMainAudioSource().setDisabled(false); // 禁止主音频.
            } else {
                drawPad.getMainAudioSource().setVolume(mainMusicVolume);
            }
        }
    }

    /**
     * 是否仅仅是处理音频.
     *
     * @return
     */
    protected boolean isOnlyDoMusic() {
        // 各种视频参数都没变;
        if (startTimeUs == 0 && cutDurationUs == 0 && fileParamter == null
                && startX == 0 && startY == 0 && cropWidth == 0
                && cropHeight == 0 && videoFilter == null && logoBitmap == null
                && scaleWidth == 0 && scaleHeight == 0
                && compressFactor == 1.0f && textAdd == null
                && timeStretchArray == null && timeFreezeArray == null
                && timeRepeatArray == null
                && isEditModeVideo == false) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 开启音频容器;
     *
     * @return
     */
    private boolean startAudioPad() {

        if (musicInfo != null) {
            audioPadSavePath = SDKFileUtils.createM4AFileInBox();
            audioPad = new AudioPadExecute(context, audioPadSavePath);

            if (isMixBgMusic == false) {
                audioPad.setAudioPadLength(srcInfo.vDuration); // 如果不需要主音频,则直接设置AudioPad的长度.
            } else {
                AudioSource mainSource = audioPad.setAudioPadSource(videoPath);
                mainSource.setVolume(mainMusicVolume);
            }
            /**
             * 增加另一个音频;
             */
            AudioSource subsource = null;
            if (bgMusicEndTime > bgMusicStartTime && bgMusicStartTime > 0) {
                subsource = audioPad
                        .addSubAudio(
                                musicPath,
                                0,
                                (long) (bgMusicStartTime * 1000 * 1000),
                                (long) ((bgMusicEndTime - bgMusicStartTime) * 1000 * 1000));
            } else {
                subsource = audioPad.addSubAudio(musicPath);
            }
            subsource.setLooping(true);
            subsource.setVolume(bgMusicVolume);
            audioPad.setAudioPadProgressListener(new onAudioPadProgressListener() {

                @Override
                public void onProgress(AudioPad v, long currentTimeUs) {

                    progressCallback(null, currentTimeUs);

                    if (monVideoOneDoProgressListener != null) {
                        float time = (float) currentTimeUs / 1000000f;

                        float percent = time / (float) srcInfo.vDuration;

                        float b = (float) (Math.round(percent * 100)) / 100; // 保留两位小数.
                        if (b < 1.0f && monVideoOneDoProgressListener != null
                                && isExecuting) {
                            monVideoOneDoProgressListener.onProgress(
                                    VideoOneDo.this, b);
                        }
                    }
                }
            });
            audioPad.setAudioPadCompletedListener(new onAudioPadCompletedListener() {

                @Override
                public void onCompleted(AudioPad v) {

                    completeDrawPad();
                }
            });
            return audioPad.start();
        } else {
            return false;
        }
    }

    /**
     * 在audioPad工作最后,合并音频
     *
     * @param audioPath
     */
    private void mergeAV(String audioPath) {
        if (srcInfo.isHaveAudio()) {
            String onlyVideo = SDKFileUtils.newMp4PathInBox();

            VideoEditor editor = new VideoEditor();
            editor.executeDeleteAudio(videoPath, onlyVideo);
            doVideoMergeAudio(onlyVideo, audioPath, dstPath);

            SDKFileUtils.deleteFile(onlyVideo);
        } else {
            doVideoMergeAudio(videoPath, audioPath, dstPath);
        }
    }

    // private Thread audioThread=null;
    // /**
    // * 音频处理线程.
    // */
    // private void startAudioThread()
    // {
    // if(audioThread==null)
    // {
    // audioThread=new Thread(new Runnable() {
    // @Override
    // public void run() {
    //
    // if(bgMusicEndTime>bgMusicStartTime){//需要裁剪,则先裁剪, 然后做处理.
    // String audio;
    // if(musicMp3Path!=null){
    // audio=SDKFileUtils.createMP3FileInBox();
    // executeAudioCutOut(musicMp3Path, audio, bgMusicStartTime,
    // bgMusicEndTime);
    // //不删除原声音;
    // musicMp3Path=audio;
    // }else{
    // audio=SDKFileUtils.createAACFileInBox();
    // executeAudioCutOut(musicAACPath, audio, bgMusicStartTime,
    // bgMusicEndTime);
    // musicAACPath=audio;
    // }
    // deletedFileList.add(audio);
    // }
    //
    //
    // /**
    // * 1, 如果mp3, 看是否要mix, 如果要,则长度拼接够, 然后mix;如果不mix,则先转码,再拼接.
    // * 2, 如果是aac, 是否要mix, 要则拼接 再mix,; 不需要则直接拼接.
    // */
    // if(musicMp3Path!=null){ //输入的是MP3;
    // if(isMixBgMusic){ //混合.
    // dstAACPath=SDKFileUtils.createAACFileInBox();
    //
    // String startMp3=getEnoughAudio(musicMp3Path, true);
    //
    // VideoEditor editor=new VideoEditor();
    // editor.executeAudioVolumeMix(srcAudioPath, startMp3,mainMusicVolume,
    // bgMusicVolume,tmpvDuration, dstAACPath);
    //
    // deletedFileList.add(dstAACPath);
    // }else{//直接增加背景音乐;
    // VideoEditor editor=new VideoEditor();
    // float duration=(float)cutDurationUs/1000000f;
    // String tmpAAC=SDKFileUtils.createAACFileInBox();
    // editor.executeConvertMp3ToAAC(musicMp3Path, 0,duration, tmpAAC);
    // dstAACPath=getEnoughAudio(tmpAAC, false);
    // deletedFileList.add(tmpAAC);
    // }
    // }else if(musicAACPath!=null){
    // if(isMixBgMusic){ //混合.
    // dstAACPath=SDKFileUtils.createAACFileInBox();
    // String startAAC=getEnoughAudio(musicAACPath, false);
    // VideoEditor editor=new VideoEditor();
    // editor.executeAudioVolumeMix(srcAudioPath, startAAC, 1.0f,
    // bgMusicVolume,tmpvDuration, dstAACPath);
    // deletedFileList.add(dstAACPath);
    // }else{
    // dstAACPath=getEnoughAudio(musicAACPath, false);
    // }
    // }
    // audioThread=null;
    // }
    // });
    // audioThread.start();
    // }
    // }
    // private void joinAudioThread()
    // {
    // if(audioThread!=null){
    // try {
    // audioThread.join(2000);
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // Log.w(TAG,"背景音乐转码失败....使用源音频");
    // dstAACPath=null;
    // }
    // audioThread=null;
    // }
    // }
    // /**
    // * 得到拼接好的mp3或aac文件. 如果够长,则直接返回;
    // * @param input
    // * @param isMp3
    // * @return
    // */
    // private String getEnoughAudio(String input, boolean isMp3)
    // {
    // String audio=input;
    // if(musicInfo.aDuration<tmpvDuration){ //如果小于则自行拼接.
    //
    // Log.d(TAG,"音频时长不够,开始转换.musicInfo.aDuration:"+musicInfo.aDuration+
    // " tmpvDuration:"+ tmpvDuration);
    //
    // int num= (int)(tmpvDuration/musicInfo.aDuration +1.0f);
    // String[] array=new String[num];
    // for(int i=0;i<num;i++){
    // array[i]=input;
    // }
    // if(isMp3){
    // audio=SDKFileUtils.createMP3FileInBox();
    // }else{
    // audio=SDKFileUtils.createAACFileInBox();
    // }
    // concatAudio(array,audio); //拼接好.
    //
    // deleteArray.add(audio);
    // }
    // return audio;
    // }
    // /**
    // * @param tsArray
    // * @param dstFile
    // * @return
    // */
    // private int concatAudio(String[] tsArray,String dstFile)
    // {
    // if(SDKFileUtils.filesExist(tsArray)){
    // String concat="concat:";
    // for(int i=0;i<tsArray.length-1;i++){
    // concat+=tsArray[i];
    // concat+="|";
    // }
    // concat+=tsArray[tsArray.length-1];
    //
    // Log.i(TAG,"==============ttt  yinp pinjie ");
    //
    //
    //
    // List<String> cmdList=new ArrayList<String>();
    //
    // cmdList.add("-i");
    // cmdList.add(concat);
    //
    // cmdList.add("-c");
    // cmdList.add("copy");
    //
    // cmdList.add("-y");
    //
    // cmdList.add(dstFile);
    // String[] command=new String[cmdList.size()];
    // for(int i=0;i<cmdList.size();i++){
    // command[i]=(String)cmdList.get(i);
    // }
    // VideoEditor editor=new VideoEditor();
    // return editor.executeVideoEditor(command);
    // }else{
    // return -1;
    // }
    // }
    // public int executeAudioCutOut(String srcFile,String dstFile,float
    // startS,float durationS)
    // {
    // VideoEditor editor=new VideoEditor();
    // List<String> cmdList=new ArrayList<String>();
    //
    // cmdList.add("-ss");
    // cmdList.add(String.valueOf(startS));
    //
    // cmdList.add("-i");
    // cmdList.add(srcFile);
    //
    // cmdList.add("-t");
    // cmdList.add(String.valueOf(durationS));
    //
    // cmdList.add("-acodec");
    // cmdList.add("copy");
    // cmdList.add("-y");
    // cmdList.add(dstFile);
    // String[] command=new String[cmdList.size()];
    // for(int i=0;i<cmdList.size();i++){
    // command[i]=(String)cmdList.get(i);
    // }
    // return editor.executeVideoEditor(command);
    //
    // }

    /**
     * 之所有从VideoEditor.java中拿过来另外写, 是为了省去两次MediaInfo的时间;
     */
    private void doVideoMergeAudio(String videoFile, String audioFile,
                                   String dstFile) {
        VideoEditor editor = new VideoEditor();
        List<String> cmdList = new ArrayList<String>();

        cmdList.add("-i");
        cmdList.add(videoFile);

        cmdList.add("-i");
        cmdList.add(audioFile);

        if (tmpvDuration > 0.0f) {
            cmdList.add("-t");
            cmdList.add(String.valueOf(tmpvDuration));
        }

        cmdList.add("-vcodec");
        cmdList.add("copy");
        cmdList.add("-acodec");
        cmdList.add("copy");

        cmdList.add("-absf");
        cmdList.add("aac_adtstoasc");

        cmdList.add("-y");
        cmdList.add(dstFile);
        String[] command = new String[cmdList.size()];
        for (int i = 0; i < cmdList.size(); i++) {
            command[i] = (String) cmdList.get(i);
        }
        editor.executeVideoEditor(command);
    }

}
