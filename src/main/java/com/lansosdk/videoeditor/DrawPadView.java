package com.lansosdk.videoeditor;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.widget.FrameLayout;

import com.lansosdk.box.AudioLine;
import com.lansosdk.box.BitmapLayer;
import com.lansosdk.box.CameraLayer;
import com.lansosdk.box.CanvasLayer;
import com.lansosdk.box.DataLayer;
import com.lansosdk.box.DrawPadUpdateMode;
import com.lansosdk.box.DrawPadViewRender;
import com.lansosdk.box.FileParameter;
import com.lansosdk.box.GifLayer;
import com.lansosdk.box.Layer;
import com.lansosdk.box.MVLayer;
import com.lansosdk.box.TextureLayer;
import com.lansosdk.box.TwoVideoLayer;
import com.lansosdk.box.VideoLayer;
import com.lansosdk.box.VideoLayer2;
import com.lansosdk.box.ViewLayer;
import com.lansosdk.box.YUVLayer;
import com.lansosdk.box.onDrawPadCompletedListener;
import com.lansosdk.box.onDrawPadErrorListener;
import com.lansosdk.box.onDrawPadOutFrameListener;
import com.lansosdk.box.onDrawPadProgressListener;
import com.lansosdk.box.onDrawPadRunTimeListener;
import com.lansosdk.box.onDrawPadSizeChangedListener;
import com.lansosdk.box.onDrawPadSnapShotListener;
import com.lansosdk.box.onDrawPadThreadProgressListener;

import java.util.ArrayList;

import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageFilter;

/**
 * 对各种图层的画面做处理, 可以预览,最终生成视频.
 * <p>
 * 如果仅仅是对一个完整的视频做处理,则处理完毕后, 是没有音频文件的,需要在外部另行增加,可参照我们的各种Activity来操作.
 * <p>
 * <p>
 * 适用在增加到UI界面中, 一边预览,一边实时保存的场合.
 * <p>
 * 此代码由我们来维护, 请不要修改其中代码,您可以继承该方法,在继承的类中增加您想要的代码.
 */
public class DrawPadView extends FrameLayout {

    /**
     * 视频画面显示模式：不裁剪直接和父view匹配， 这样如果画面超出父view的尺寸，将会只显示视频画面的
     * 一部分，您可以使用这个来平铺视频画面，通过手动拖拽的形式来截取画面的一部分。类似视频画面区域裁剪的功能。
     */
    static final int AR_ASPECT_FIT_PARENT = 0; // without clip
    /**
     * 视频画面显示模式:裁剪和父view匹配, 当视频画面超过父view大小时,不会缩放,会只显示视频画面的一部分. 超出部分不予显示.
     */
    static final int AR_ASPECT_FILL_PARENT = 1; // may clip
    /**
     * 视频画面显示模式: 自适应大小.当小于画面尺寸时,自动显示.当大于尺寸时,缩放显示.
     */
    static final int AR_ASPECT_WRAP_CONTENT = 2;
    /**
     * 视频画面显示模式:和父view的尺寸对其.完全填充满父view的尺寸
     */
    static final int AR_MATCH_PARENT = 3;
    /**
     * 把画面的宽度等于父view的宽度, 高度按照16:9的形式显示. 大部分的网络视频推荐用这种方式显示.
     */
    static final int AR_16_9_FIT_PARENT = 4;
    /**
     * 把画面的宽度等于父view的宽度, 高度按照4:3的形式显示.
     */
    static final int AR_4_3_FIT_PARENT = 5;
    private static final String TAG = "DrawPadView";
    private static final boolean VERBOSE = false;
    private int mVideoRotationDegree;
    private TextureRenderView mTextureRenderView;
    private DrawPadViewRender renderer;
    private SurfaceTexture mSurfaceTexture = null;
    private boolean isUseMainPts = false;
    private int encWidth, encHeight, encFrameRate;
    private int encBitRate = 0;
    private float encodeSpeed = 1.0f;
    /**
     * 经过宽度对齐到手机的边缘后, 缩放后的宽高,作为drawpad(容器)的宽高.
     */
    private int drawPadWidth, drawPadHeight;
    private String encodeOutput = null; // 编码输出路径
    private DrawPadUpdateMode mUpdateMode = DrawPadUpdateMode.ALL_VIDEO_READY;
    private int mAutoFlushFps = 0;
    private onViewAvailable mViewAvailable = null;
    private onDrawPadSizeChangedListener mSizeChangedCB = null;
    private onDrawPadRunTimeListener drawpadRunTimeListener = null;
    /**
     * 实时录制的时候的进度回调 实时录制的时候的进度回调 实时录制的时候的进度回调
     * <p>
     * 此listener中的long类型是当前时间,单位是微秒, currentTimeUs; 此时间为即将渲染这一帧的时间,
     * 比如你要在第3秒增加别的图层或调整指定图层的参数 则应该判断时间是否大于或等于3*1000*1000;
     * <p>
     * 此方法是在实时录制的时候,每次录制一帧调用这里,返回当前录制帧的时间戳.
     *
     * @param currentTimeUs
     * 当前DrawPad处理画面的时间戳.,单位微秒.
     */
    private onDrawPadProgressListener drawpadProgressListener = null;
    /**
     * 方法与 onDrawPadProgressListener不同的地方在于:
     * 此回调是在DrawPad渲染完一帧后,立即执行这个回调中的代码,不通过Handler传递出去,你可以精确的执行一些下一帧的如何操作. 故不能在回调
     * 内增加各种UI相关的代码.
     */
    private onDrawPadThreadProgressListener drawPadThreadProgressListener = null;
    private boolean isThreadProgressInRecording = false;
    private onDrawPadSnapShotListener drawpadSnapShotListener = null;
    private onDrawPadOutFrameListener drawPadPreviewFrameListener = null;
    private int previewFrameWidth;
    private int previewFrameHeight;
    private int previewFrameType;
    private boolean frameListenerInDrawPad = false;
    private onDrawPadCompletedListener drawpadCompletedListener = null;
    private onDrawPadErrorListener drawPadErrorListener = null;
    private boolean isEditModeVideo = false;
    /**
     * 是否也录制mic的声音.
     */
    private boolean isRecordMic = false;
    /**
     * 暂停和恢复有3种方法, 分别是:刷新,预览,录制;
     * <p>
     * 暂停刷新,则都暂停了, 暂停录制则只暂停录制,刷新和预览一样在走动. 暂停预览,则录制还在继续,但只是录制了同一副画面.
     */
    private boolean isPauseRefreshDrawPad = false;
    private boolean isPausePreviewDrawPad = false;
    private boolean isPauseRecord = false;
    private boolean isRecordExtPcm = false; // 是否使用外面的pcm输入.
    private int pcmSampleRate = 44100;
    private int pcmBitRate = 64000;
    private int pcmChannels = 2; // 音频格式. 音频默认是双通道.
    // ----------------------------------------------
    private boolean isCheckBitRate = true;
    private boolean isCheckPadSize = true;

    public DrawPadView(Context context) {
        super(context);
        initVideoView(context);
    }
    public DrawPadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public DrawPadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawPadView(Context context, AttributeSet attrs, int defStyleAttr,
                       int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVideoView(context);
    }

    private void initVideoView(Context context) {
        setTextureView();

        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }

    private void setTextureView() {
        mTextureRenderView = new TextureRenderView(getContext());
        mTextureRenderView.setSurfaceTextureListener(new SurfaceCallback());

        /**
         * 注意： 此
         */
        mTextureRenderView.setDispalyRatio(AR_ASPECT_FIT_PARENT);

        View renderUIView = mTextureRenderView.getView();
        LayoutParams lp = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        renderUIView.setLayoutParams(lp);
        addView(renderUIView);
        mTextureRenderView.setVideoRotation(mVideoRotationDegree);
    }

    /**
     * 设置DrawPad的刷新模式,默认 {@link DrawPadUpdateMode#ALL_VIDEO_READY};
     *
     * @param mode
     * @param autofps //自动刷新的参数,每秒钟刷新几次(即视频帧率).当自动刷新的时候有用, 不是自动,则不起作用.
     */
    public void setUpdateMode(DrawPadUpdateMode mode, int autofps) {
        mAutoFlushFps = autofps;

        mUpdateMode = mode;

        if (renderer != null) {
            renderer.setUpdateMode(mUpdateMode, mAutoFlushFps);
        }
    }

    /**
     * 调整在录制时的速度, 比如你想预览的时候是正常的, 录制好后, 一部分要快进或慢放,则可以在这里设置 支持在任意时刻来变速; 当前暂时不支持音频,
     * 只是视频的加减速, 请注意!!! 这个只是录制时的有效, 正常界面显示还是原来的界面,请注意!!!
     * <p>
     * 建议5个等级: 0.25f,0.5f,1.0f,1.5f,2.0f; 其中 0.25是放慢4倍; 0.5是放慢2倍;
     * 1.0是采用和预览同样的速度; 1.5是加快一半, 2.0是加快2倍.
     *
     * @param speed 速度系数,
     */
    public void adjustEncodeSpeed(float speed) {
        if (renderer != null) {
            renderer.adjustEncodeSpeed(speed);
        }
        encodeSpeed = speed;
    }

    /**
     * 获取当前View的 宽度
     *
     * @return
     */
    public int getViewWidth() {
        return drawPadWidth;
    }

    /**
     * 获得当前View的高度.
     *
     * @return
     */
    public int getViewHeight() {
        return drawPadHeight;
    }

    public int getDrawPadWidth() {
        return drawPadWidth;
    }

    /**
     * 获得当前View的高度.
     *
     * @return
     */
    public int getDrawPadHeight() {
        return drawPadHeight;
    }

    public boolean isTextureAvailable(){
        return mSurfaceTexture!=null && isDrawPadSizeChanged;
    }
    /**
     * 此回调仅仅是作为演示: 当跳入到别的Activity后的返回时,会再次预览当前画面的功能.
     * 你完全可以重新按照你的界面需求来修改这个DrawPadView类.
     */
    public void setOnViewAvailable(onViewAvailable listener) {
        mViewAvailable = listener;
    }

    /**
     * 设置使能 实时保存, 即把正在DrawPad中呈现的画面实时的保存下来,实现所见即所得的模式, 您可以设置哪些图层需要录制, 哪些图层不需要录制
     * 。比如增加一个Logo，您只需要录制成视频有Logo，但录制中的画面没有这个Logo，则可以设置为只在录制中显示。
     * <p>
     * 如果实时保存的宽高和原视频的宽高不成比例,则会先等比例缩放原视频,然后在多出的部分出增加黑边的形式呈现,比如原视频是16:9,
     * 设置的宽高是480x480,则会先把原视频按照宽度进行16:9的比例缩放. 在缩放后,在视频的上下增加黑边的形式来实现480x480,
     * 从而不会让视频变形.
     * <p>
     * 如果视频在拍摄时有角度, 比如手机相机拍照,会有90度或270, 则会自动的识别拍摄的角度,并在缩放时,自动判断应该左右加黑边还是上下加黑边.
     * <p>
     * 因有缩放的特性, 您可以直接向DrawPad中投入一个视频,然后把宽高比设置一致,这样可实现一个视频压缩的功能;您也可以把宽高比设置一下,
     * 这样可实现视频加黑边的压缩功能. 或者您完全不进行缩放, 仅仅想把视频码率减低一下, 也可以把其他参数设置为和源视频一致,
     * 仅仅调试encBr这个参数,来实现视频压缩的功能.
     *
     * @param encW    录制视频的宽度
     * @param encH    录制视频的高度
     * @param encBr   录制视频的bitrate,
     * @param encFr   录制视频的 帧率
     * @param outPath 录制视频的保存路径. 注意:这个路径在分段录制功能时,无效.即调用
     */
    public void setRealEncodeEnable(int encW, int encH, int encBr, int encFr,
                                    String outPath) {
        if (encW > 0 && encH > 0 && encBr > 0 && encFr > 0) {
            encWidth = encW;
            encHeight = encH;
            encBitRate = encBr;
            encFrameRate = encFr;
            encodeOutput = outPath;
            if(renderer!=null){
                renderer.setEncoderEnable(encWidth,encHeight,encBitRate,encFrameRate,encodeOutput);
            }
        } else {
            Log.w(TAG, "enable real encode is error");
        }
    }

    public void setRealEncodeEnable(int encW, int encH, int encFr,String outPath) {
        if (encW > 0 && encH > 0 && encFr > 0) {
            encWidth = encW;
            encHeight = encH;
            encBitRate = LanSongUtil.checkSuggestBitRate(encHeight* encWidth, encBitRate);
            encFrameRate = encFr;
            encodeOutput = outPath;
            if(renderer!=null){
                renderer.setEncoderEnable(encWidth,encHeight,encBitRate,encFrameRate,encodeOutput);
            }
        } else {
            Log.w(TAG, "enable real encode is error");
        }
    }
    boolean isDrawPadSizeChanged=false;
    /**
     * 设置当前DrawPad的宽度和高度,并把宽度自动缩放到父view的宽度,然后等比例调整高度.
     * <p>
     * 注意: 这里的宽度和高度,会根据手机屏幕的宽度来做调整,默认是宽度对齐到手机的左右两边, 然后调整高度,
     * 把调整后的高度作为DrawPad渲染线程的真正宽度和高度. 注意: 此方法需要在
     * 前调用. 比如设置的宽度和高度是480,480,
     * 而父view的宽度是等于手机分辨率是1080x1920,则DrawPad默认对齐到手机宽度1080,然后把高度也按照比例缩放到1080.
     *
     * @param width  DrawPad宽度
     * @param height DrawPad高度
     * @param cb     设置好后的回调, 注意:如果预设值的宽度和高度经过调整后
     *               已经和父view的宽度和高度一致,则不会触发此回调(当然如果已经是希望的宽高,您也不需要调用此方法).
     */
    public void setDrawPadSize(int width, int height,
                               onDrawPadSizeChangedListener cb) {

        isDrawPadSizeChanged=true;
        if (width != 0 && height != 0 && cb != null) {
            float setAcpect = (float) width / (float) height;

            float setViewacpect = (float) drawPadWidth / (float) drawPadHeight;

            Log.i(TAG, "setAcpect=" + setAcpect + " setViewacpect:"
                    + setViewacpect + "set width:" + width + "x" + height
                    + " view width:" + drawPadWidth + "x" + drawPadHeight);

            if (setAcpect == setViewacpect) { // 如果比例已经相等,不需要再调整,则直接显示.
                if (cb != null) {
                    cb.onSizeChanged(width, height);
                }
            } else if (Math.abs(setAcpect - setViewacpect) * 1000 < 16.0f) {
                if (cb != null) {
                    cb.onSizeChanged(width, height);
                }
            } else if (mTextureRenderView != null) {
                mTextureRenderView.setVideoSize(width, height);
                mTextureRenderView.setVideoSampleAspectRatio(1, 1);
                mSizeChangedCB = cb;
            }
            requestLayout();
        }
    }

    /**
     * 当前drawpad容器运行了多长时间, 仅供参考使用. 没有特别的意义.
     * <p>
     * 此listener中的long类型是当前时间,单位是微秒, currentTimeUs; 此时间为即将渲染这一帧的时间,
     * 比如你要在第3秒增加别的图层或调整指定图层的参数 则应该判断时间是否大于或等于3*1000*1000;
     * <p>
     * 仅仅作为drawpad容器运行时间的参考, 如果你要看当前视频图层的运行时间,则应设置图层的监听,而不是容器运行时间的监听, 可以通过
     * {@link #resetDrawPadRunTime(long)}来复位这个时间.
     *
     * @param li
     */
    public void setOnDrawPadRunTimeListener(onDrawPadRunTimeListener li) {
        if (renderer != null) {
            renderer.setDrawPadRunTimeListener(li);
        }
        drawpadRunTimeListener = li;
    }

    /**
     * 把运行的时间复位到某一个值, 这样的话, drawpad继续显示, 就会以这个值为参考, 增加相对运行的时间. drawpad已经运行了10秒钟,
     * 你复位到2秒.则drawpad下一个onDrawPadRunTimeListener返回的值,就是2秒+相对运行的值,可能是2000*1000 +
     * 40*1000;
     *
     * @param runtimeUs
     */
    public void resetDrawPadRunTime(long runtimeUs) {
        if (renderer != null) {
            renderer.resetPadRunTime(runtimeUs);
        }
    }

    public void setOnDrawPadProgressListener(onDrawPadProgressListener listener) {
        if (renderer != null) {
            renderer.setDrawPadProgressListener(listener);
        }
        drawpadProgressListener = listener;
    }

    /**
     * 方法与 onDrawPadProgressListener不同的地方在于: 即将开始一帧渲染的时候,
     * 直接执行这个回调中的代码,不通过Handler传递出去,你可以精确的执行一些这一帧的如何操作.
     * <p>
     * listener中的方法是在DrawPad线程中执行的, 请不要在此监听里放置耗时的处理, 以免造成DrawPad线程的卡顿.
     * <p>
     * 不能在回调 内增加各种UI相关的代码.
     *
     * @param listener 此listner工作在opengl线程， 非UI线程。
     */
    public void setOnDrawPadThreadProgressListener(
            onDrawPadThreadProgressListener listener) {
        if (renderer != null) {
            renderer.setDrawPadThreadProgressListener(listener);
        }
        drawPadThreadProgressListener = listener;
    }

    public void setThreadProgressInRecording(boolean is) {
        if (renderer != null) {
            renderer.setThreadProgressInRecording(is);
        }
        isThreadProgressInRecording = is;
    }

    /**
     * 设置 获取当前DrawPad这一帧的画面的监听, 设置截图监听,当截图完成后, 返回当前图片的btimap格式的图片. 此方法工作在主线程.
     * 请不要在此方法里做图片的处理,以免造成拥堵; 建议获取到bitmap后,放入到一个链表中,在外面或另开一个线程处理.
     */
    public void setOnDrawPadSnapShotListener(onDrawPadSnapShotListener listener) {
        if (renderer != null) {
            renderer.setDrawpadSnapShotListener(listener);
        }
        drawpadSnapShotListener = listener;
    }

    /**
     * 触发一下截取当前DrawPad中的内容. 触发后, 会在DrawPad内部设置一个标志位,DrawPad线程会检测到这标志位后,
     * 截取DrawPad, 并通过onDrawPadSnapShotListener监听反馈给您. 请不要多次或每一帧都截取DrawPad,
     * 以免操作DrawPad处理过慢.
     * <p>
     * 此方法,仅在前台工作时有效. (注意:截取的仅仅是各种图层的内容, 不会截取DrawPad的黑色背景)
     */
    public void toggleSnatShot() {
        if (drawpadSnapShotListener != null && renderer != null
                && renderer.isRunning()) {
            renderer.toggleSnapShot(drawPadWidth, drawPadHeight);
        } else {
            Log.e(TAG, "toggle snap shot failed!!!");
        }
    }

    /**
     * 截图一次, 设置截图的宽度和高度; 如果你连续截图,则宽度和高度需每次都一致;
     *
     * @param width
     * @param height
     */
    public void toggleSnatShot(int width, int height) {
        if (drawpadSnapShotListener != null && renderer != null
                && renderer.isRunning()) {
            renderer.toggleSnapShot(width, height);
        } else {
            Log.e(TAG, "toggle snap shot failed!!!");
        }
    }

    /**
     * 设置每处理一帧的数据预览监听, 等于把当前处理的这一帧的画面拉出来, 您可以根据这个画面来自行的编码保存, 或网络传输.
     * <p>
     * 建议在这里拿到数据后, 放到queue中, 然后在其他线程中来异步读取queue中的数据, 请注意queue中数据的总大小, 要及时处理和释放,
     * 以免内存过大,造成OOM问题
     *
     * @param width    可以设置要引出这一帧画面的宽度, 如果宽度不等于drawpad的预览宽度,则会缩放.
     * @param height   画面缩放到的高度,
     * @param type     数据的类型, 当前仅支持Bitmap 这里暂时无效;
     * @param listener 监听对象.
     */
    public void setOnDrawPadOutFrameListener(int width, int height, int type,
                                             onDrawPadOutFrameListener listener) {
        if (renderer != null) {
            renderer.setDrawpadOutFrameListener(width, height, type, listener);
        }
        previewFrameWidth = width;
        previewFrameHeight = height;
        previewFrameType = type;
        drawPadPreviewFrameListener = listener;
    }

    /**
     * 设置setOnDrawPadOutFrameListener后, 你可以设置这个方法来让listener是否运行在Drawpad线程中.
     * 如果你要直接使用里面的数据, 则不用设置, 如果你要开启另一个线程, 把listener传递过来的数据送过去,则建议设置为true;
     * [建议设置为true, 在DrawPad内部执行listener, 间隔取图片后,放入到列表中,然后在另外线程中使用.]
     *
     * @param en
     */
    public void setOutFrameInDrawPad(boolean en) {
        if (renderer != null) {
            renderer.setOutFrameInDrawPad(en);
        }
        frameListenerInDrawPad = en;
    }

    // ---------------
    public void setOnDrawPadCompletedListener(onDrawPadCompletedListener listener) {
        if (renderer != null) {
            renderer.setDrawPadCompletedListener(listener);
        }
        drawpadCompletedListener = listener;
    }

    /**
     * 设置当前DrawPad运行错误的回调监听.
     *
     * @param listener
     */
    public void setOnDrawPadErrorListener(onDrawPadErrorListener listener) {
        if (renderer != null) {
            renderer.setDrawPadErrorListener(listener);
        }
        drawPadErrorListener = listener;
    }

    /**
     * 此方法仅仅使用在录制视频的同时,您也设置了录制音频
     *
     * @return 在录制结束后, 返回录制mic的音频文件路径,
     */
    public String getMicPath() {
        if (renderer != null) {
            return renderer.getAudioRecordPath();
        } else {
            return null;
        }
    }

    /**
     * 设置录制好的视频,
     *
     * @param is
     */
    public void setEditModeVideo(boolean is) {
        if (renderer != null) {
            renderer.setEditModeVideo(is);
        } else {
            isEditModeVideo = is;
        }
    }

    /**
     * 开始DrawPad的渲染线程, 阻塞执行, 直到DrawPad真正开始执行后才退出当前方法.
     * <p>
     * 可以先通过 {@link #setDrawPadSize(int, int, onDrawPadSizeChangedListener)}
     * 来设置宽高,然后在回调中执行此方法. 如果您已经在xml中固定了view的宽高,则可以直接调用这里, 无需再设置DrawPadSize
     *
     * @return
     */
    public boolean startDrawPad() {
        return startDrawPad(isPauseRecord);
    }

    /**
     * 开始DrawPad的渲染线程, 阻塞执行, 直到DrawPad真正开始执行后才退出当前方法. 如果DrawPad设置了录制功能,
     * 这里可以在开启后暂停录制. 适用在当您开启录制后, 需要先增加一个图层的场合后,在让它开始录制的场合, 可用resume *
     * {@link #resumeDrawPadRecord()} 来回复录制.
     *
     * @param pauseRecord 如果DrawPad设置了录制功能, 这里可以在开启后暂停录制. 适用在当您开启录制后,
     *                    需要先增加一个图层的场合后,在让它开始录制的场合, 可用resume
     *                    {@link #resumeDrawPadRecord()} 来回复录制.
     * @return
     */
    public boolean startDrawPad(boolean pauseRecord) {
        boolean ret = false;
        if(renderer!=null){
            renderer.stopDrawPad();
            renderer=null;
        }
        if (mSurfaceTexture != null && renderer == null && drawPadWidth > 0
                && drawPadHeight > 0) {
            renderer = new DrawPadViewRender(getContext(), drawPadWidth,drawPadHeight);
            if (renderer != null) {
                renderer.setUseMainVideoPts(isUseMainPts);
                // 因为要预览,这里设置显示的Surface,当然如果您有特殊情况需求,也可以不用设置,但displayersurface和EncoderEnable要设置一个,DrawPadRender才可以工作.
                renderer.setDisplaySurface(new Surface(mSurfaceTexture));

                if (isCheckPadSize) {
                    encWidth = LanSongUtil.make16Multi(encWidth); // 编码默认16字节对齐.
                    encHeight = LanSongUtil.make16Multi(encHeight);
                }
                if (isCheckBitRate || encBitRate == 0) {
                    encBitRate = LanSongUtil.checkSuggestBitRate(encHeight
                            * encWidth, encBitRate);
                }
                if(encWidth>0 && encHeight>0 && encodeOutput!=null){
                    renderer.setEncoderEnable(encWidth, encHeight, encBitRate,
                            encFrameRate, encodeOutput);
                }
                if (isEditModeVideo) {
                    renderer.setEditModeVideo(isEditModeVideo);
                }
                renderer.setUpdateMode(mUpdateMode, mAutoFlushFps);

                // 设置DrawPad处理的进度监听, 回传的currentTimeUs单位是微秒.
                renderer.setDrawpadSnapShotListener(drawpadSnapShotListener);
                renderer.setDrawpadOutFrameListener(previewFrameWidth,
                        previewFrameHeight, previewFrameType,
                        drawPadPreviewFrameListener);
                renderer.setOutFrameInDrawPad(frameListenerInDrawPad);

                renderer.setDrawPadProgressListener(drawpadProgressListener);
                renderer.setDrawPadCompletedListener(drawpadCompletedListener);
                renderer.setThreadProgressInRecording(isThreadProgressInRecording);
                renderer.setDrawPadThreadProgressListener(drawPadThreadProgressListener);
                renderer.setDrawPadErrorListener(drawPadErrorListener);
                renderer.setDrawPadRunTimeListener(drawpadRunTimeListener);

                if (isRecordMic) {
                    renderer.setRecordMic(isRecordMic);
                } else if (isRecordExtPcm) {
                    renderer.setRecordExtraPcm(isRecordExtPcm, pcmChannels,pcmSampleRate, pcmBitRate);
                }

                if (pauseRecord || isPauseRecord) {
                    renderer.pauseRecordDrawPad();
                }
                if (isPauseRefreshDrawPad) {
                    renderer.pauseRefreshDrawPad();
                }
                if (isPausePreviewDrawPad) {
                    renderer.pausePreviewDrawPad();
                }
                renderer.adjustEncodeSpeed(encodeSpeed);

                ret = renderer.startDrawPad();
                if (ret == false) {
                    Log.e(TAG,
                            "开启 DrawPad 失败, 或许是您之前的DrawPad没有Stop, 或者传递进去的surface对象已经被系统Destory!!,"
                                    + "请检测您 的代码或参考本文件中的SurfaceCallback 这个类中的注释;\n");
                }else {
                    Log.i(TAG,"Drawpad is running..."+ret);
                }
            }
        } else {
            if (mSurfaceTexture == null) {
                Log.e(TAG,
                        "可能没有您的UI界面还没有完全启动,您就startDrawPad了, 建议oncreate后延迟300ms再调用");
            } else {
                Log.e(TAG, "无法开启DrawPad, 您当前的参数有问题,您对照下参数:宽度和高度是:"
                        + drawPadWidth + " x " + drawPadHeight
                        + " mSurfaceTexture:" + mSurfaceTexture);
            }
        }
        return ret;
    }

    /**
     * 暂停DrawPad的画面刷新, 暂停后, 预览界面和后台录制被同时暂停. 在一些场景里,您需要开启DrawPad后,暂停下,
     * 然后增加各种Layer后,安排好各种事宜后,再让其画面更新,则用到这个方法.
     * <p>
     * 此方法是对DrawPad线程 暂停和恢复的, 不能用在一个Activity的onPause和onResume中.
     * 如果您要跳入到别的Activity, 则应该这里 {@link #stopDrawPad()} 在回到当前Activity的时候, 调用
     * {@link #startDrawPad()}
     */
    public void pauseDrawPad() {
        if (renderer != null) {
            renderer.pauseRefreshDrawPad();
        }
        isPauseRefreshDrawPad = true;
    }

    /**
     * 恢复之前暂停的DrawPad,让其继续画面刷新. 与{@link #pauseDrawPad()}配对使用.
     * <p>
     * 此方法是对DrawPad线程 暂停和恢复的, 不能用在一个Activity的onPause和onResume中.
     * 如果您要跳入到别的Activity, 则应该这里 {@link #stopDrawPad()} 在回到当前Activity的时候, 调用
     * {@link #startDrawPad()}
     */
    public void resumeDrawPad() {
        if (renderer != null) {
            renderer.resumeRefreshDrawPad();
        }
        isPauseRefreshDrawPad = false;
    }

    /**
     * 暂停预览刷新, 停止预览后, 如果您的后台还在录制中,则会一直录制同一副画面. 一般的场合不建议调用.
     * <p>
     * 暂停和恢复有3种方法, 分别是:刷新,预览,录制;
     * <p>
     * 暂停刷新,则都暂停了, 暂停录制则只暂停录制,刷新和预览一样在走动. 暂停预览,则录制还在继续,但只是录制了同一副画面.
     * <p>
     * 此方法是对DrawPad线程 暂停和恢复的, 不能用在一个Activity的onPause和onResume中.
     * 如果你要从一个Activity切换到另一个Activity,则需要 {@link #stopDrawPad()} 然后重新开始
     */
    public void pausePreview() {
        if (renderer != null) {
            renderer.pausePreviewDrawPad();
        }
        isPausePreviewDrawPad = true;
    }

    /**
     * 不建议使用.
     * <p>
     * 此方法是对DrawPad线程 暂停和恢复的, 不能用在一个Activity的onPause和onResume中.
     * 如果你要从一个Activity切换到另一个Activity,则需要 {@link #stopDrawPad()} 然后重新开始
     */
    public void resumePreview() {
        if (renderer != null) {
            renderer.resumePreviewDrawPad();
        }
        isPausePreviewDrawPad = false;
    }

    /**
     * 暂停drawpad的录制,这个方法使用在暂停录制后, 在当前画面做其他的一些操作, 不可用来暂停后跳入到别的Activity中.
     * <p>
     * 此方法是对DrawPad线程 暂停和恢复的, 不能用在一个Activity的onPause和onResume中.
     * 如果你要从一个Activity切换到另一个Activity,则需要 {@link #stopDrawPad()} 然后重新开始
     */
    public void pauseDrawPadRecord() {
        if (renderer != null) {
            renderer.pauseRecordDrawPad();
        }
        isPauseRecord = true;
    }

    /**
     * 恢复drawpad的录制. 如果刷新也是暂停状态, 同时恢复刷新.
     * <p>
     * 此方法是对DrawPad线程 暂停和恢复的, 不能用在一个Activity的onPause和onResume中.
     * 如果你要从一个Activity切换到另一个Activity,则需要 {@link #stopDrawPad()} 然后重新开始
     */
    public void resumeDrawPadRecord() {
        if (renderer != null) {
            renderer.resumeRecordDrawPad();
        }
        isPauseRecord = false;
    }

    /**
     * 是否在CameraLayer录制的同时, 录制mic的声音. 在drawpad开始前调用.
     * <p>
     * 此方法仅仅使用在录像的时候, 同时录制Mic的场合.录制的采样默认是44100, 码率64000, 编码为aac格式. 录制的同时,
     * 视频编码以音频的时间戳为参考. 可通过 {@link #stopDrawPad2()}停止,停止后返回mic录制的音频文件 m4a格式的文件,
     *
     * @param record
     */
    public void setRecordMic(boolean record) {
        if (renderer != null && renderer.isRecording()) {
            Log.e(TAG, "DrawPad is running. set Mic Error!");
        } else {
            isRecordMic = record;
        }
    }

    /**
     * 是否在录制画面的同时,录制外面的push进去的音频数据 .
     * <p>
     * 适用在需要实时录制的视频, 如果您仅仅是对视频增加背景音乐等, 可以使用
     * {@link VideoEditor#executeVideoMergeAudio(String, String, String)} 来做处理.
     * <p>
     * 注意:当设置了录制外部的pcm数据后, 当前容器上录制的视频帧,就以音频的帧率为参考时间戳,从而保证音视频同步进行. 故您在投递音频的时候,
     * 需要严格按照音频播放的速度投递.
     * <p>
     * 如采用外面的pcm数据,则视频在录制过程中,会参考音频时间戳,来计算得出视频的时间戳,
     * 如外界音频播放完毕,无数据push,应及时stopDrawPad2
     * <p>
     * 可以通过 AudioLine 的getFrameSize()方法获取到每次应该投递多少个字节,此大小为固定大小,
     * 每次投递必须push这么大小字节的数据,
     * <p>
     * 可通过 {@link #stopDrawPad2()}停止,停止后返回mic录制的音频文件 m4a格式的文件,
     *
     * @param isrecord   是否录制
     * @param channels   声音通道个数, 如是mp3或aac文件,可根据MediaInfo获得
     * @param samplerate 采样率 如是mp3或aac文件,可根据MediaInfo获得
     * @param bitrate    码率 如是mp3或aac文件,可根据MediaInfo获得
     */
    public void setRecordExtraPcm(boolean isrecord, int channels,
                                  int samplerate, int bitrate) {
        if (renderer != null) {
            renderer.setRecordExtraPcm(isrecord, channels, samplerate, bitrate);
        } else {
            isRecordExtPcm = isrecord;
            pcmSampleRate = samplerate;
            pcmBitRate = bitrate;
            pcmChannels = channels;
        }
    }

    /**
     * 获取一个音频输入对象, 向内部投递数据, 只有当开启容器录制,并设置了录制外面数据的情况下,才有效.
     *
     * @return
     */
    public AudioLine getAudioLine() {
        if (renderer != null) {
            return renderer.getAudioLine();
        } else {
            return null;
        }
    }

    /**
     * 分段录制, 开始录制一段.
     */
    public void segmentStart() {
        if (renderer != null) {
            renderer.segmentStart();
        }
    }

    /**
     * 录制一段结束.
     * <p>
     * 录制完成后, 返回当前录制这一段的视频文件完整路径名, 因为这里会等待 编码和音频采集模块处理完毕释放后才返回,
     * 故有一定的阻塞时间(在低端手机上大概100ms), 建议用Handler的 HandlerMessage的形式来处理
     */
    public String segmentStop() {
        if (renderer != null) {
            return renderer.segmentStop();
        } else {
            return null;
        }
    }

    public boolean isRecording() {
        if (renderer != null)
            return renderer.isRecording();
        else
            return false;
    }

    /**
     * 设置是否使用主视频的时间戳为录制视频的时间戳, 如果您传递过来的是一个完整的视频, 只是需要在此视频上做一些操作,
     * 操作完成后,时长等于源视频的时长, 则使用主视频的时间戳, 如果视频是从中间截取一般开始的 则不建议使用, 默认是这里为false;
     * <p>
     * 注意:需要在DrawPad开始前使用.
     */
    public void setUseMainVideoPts(boolean use) {
        isUseMainPts = use;
    }

    /**
     * 当前DrawPad是否在工作.
     *
     * @return
     */
    public boolean isRunning() {
        if (renderer != null)
            return renderer.isRunning();
        else
            return false;
    }

    /**
     * 停止DrawPad的渲染线程
     */
    public void stopDrawPad() {
        if (renderer != null) {
            renderer.release();
            renderer = null;
        }
    }

    /**
     * 停止DrawPad的渲染线程 如果设置了在录制的时候,设置了录制mic或extPcm, 则返回录制音频的文件路径.
     *
     * @return
     */
    public String stopDrawPad2() {
        String ret = null;
        if (renderer != null) {
            renderer.release();
            ret = renderer.getAudioRecordPath();
            renderer = null;
        }
        return ret;
    }

    /**
     * 作用同 {@link #setDrawPadSize(int, int, onDrawPadSizeChangedListener)},
     * 只是有采样宽高比, 如用我们的VideoPlayer则使用此方法, 建议用
     * {@link #setDrawPadSize(int, int, onDrawPadSizeChangedListener)}
     *
     * @param width
     * @param height
     * @param sarnum 如mediaplayer设置后,可以为1,
     * @param sarden 如mediaplayer设置后,可以为1,
     * @param cb
     */
    public void setDrawPadSize(int width, int height, int sarnum, int sarden,
                               onDrawPadSizeChangedListener cb) {
        if (width != 0 && height != 0) {
            if (mTextureRenderView != null) {
                mTextureRenderView.setVideoSize(width, height);
                mTextureRenderView.setVideoSampleAspectRatio(sarnum, sarden);
            }
            mSizeChangedCB = cb;
            requestLayout();
        }
    }

    /**
     * 直接设置容器的宽高, 不让他自动缩放.
     * <p>
     * 要在容器开始前调用.
     *
     * @param width
     * @param height
     */
    public void setDrawpadSizeDirect(int width, int height) {
        drawPadWidth = width;
        drawPadHeight = height;
        if (renderer != null) {
            Log.w(TAG,
                    "renderer maybe is running. your setting is not available!!");
        }
    }

    /**
     * 把当前图层放到最里层, 里面有 一个handler-loop机制, 将会在下一次刷新后执行.
     *
     * @param layer
     */
    public void bringLayerToBack(Layer layer) {
        if (renderer != null) {
            renderer.bringLayerToBack(layer);
        }
    }

    /**
     * 把当前图层放到最外层, 里面有 一个handler-loop机制, 将会在下一次刷新后执行.
     *
     * @param layer
     */
    public void bringLayerToFront(Layer layer) {
        if (renderer != null) {
            renderer.bringLayerToFront(layer);
        }
    }

    /**
     * 设置当前图层对象layer 在DrawPad中所有图层队列中的位置, 您可以认为内部是一个ArrayList的列表, 先add进去的
     * 的position是0, 后面增加的依次是1,2,3等等 可以通过{@link Layer#getIndexLayerInDrawPad()}
     * 来获取当前图层的位置.
     *
     * @param layer
     * @param position
     */
    public void changeLayerLayPosition(Layer layer, int position) {
        if (renderer != null) {
            renderer.changeLayerLayPosition(layer, position);
        }
    }

    /**
     * 交换两个图层的位置.
     *
     * @param first
     * @param second
     */
    public void swapTwoLayerPosition(Layer first, Layer second) {
        if (renderer != null) {
            renderer.swapTwoLayerPosition(first, second);
        }
    }

    /**
     * 获取当前容器中有多少个图层.
     *
     * @return
     */
    public int getLayerSize() {
        if (renderer != null) {
            return renderer.getLayerSize();
        } else {
            return 0;
        }
    }

    /**
     * 获取一个主视频的 VideoLayer 主视频的意思是: 对一个完整的视频做操作, 从视频的0秒一直运行到视频的最后一帧, 比如对一个视频做滤镜,
     * 增加图片图层, 增加mv图层等等.
     * <p>
     * 如果你要先把一个视频add进来, 后面再增加另一个视频,则用 addVideoLayer(),不能用这个方法.
     *
     * @param width  主视频的画面宽度 建议用 {@link MediaInfo#vWidth}来赋值
     * @param height 主视频的画面高度
     * @return
     */
    public VideoLayer addMainVideoLayer(int width, int height,
                                        GPUImageFilter filter) {
        VideoLayer ret = null;

        if (renderer != null)
            ret = renderer.addMainVideoLayer(width, height, filter);
        else {
            Log.e(TAG, "setMainVideoLayer error render is not avalid");
        }
        return ret;
    }

    /**
     * 增加主视频VideoLayer; 主视频的意思是: 对一个完整的视频做操作, 从视频的0秒一直运行到视频的最后一帧, 比如对一个视频做滤镜,
     * 增加图片图层, 增加mv图层等等.
     * <p>
     * 如果你要先把一个视频add进来, 后面再增加另一个视频,则用 addVideoLayer(),不能用这个方法.
     * <p>
     * 其中FileParameter的配置是:
     * <p>
     * FileParameter param=new FileParameter();
     * if(param.setDataSoure(mVideoPath)){
     * <p>
     * 设置当前需要显示的区域 ,以左上角为0,0坐标.
     *
     * @param startX 开始的X坐标, 即从宽度的什么位置开始
     * @param startY 开始的Y坐标, 即从高度的什么位置开始
     * @param cropW  需要显示的宽度
     * @param cropH  需要显示的高度. param.setShowRect(0, 0, 300, 200);
     *               videoMainLayer=mDrawPadView.addMainVideoLayer(param,new
     *               GPUImageSepiaFilter()); }
     * @param param  这个参数, 您可以设置视频的区域, 因为预览是外界提供的播放,
     *               如果你要从某个位置开始,则需要把外面的播放器seek到某个位置即可.
     * @param filter 给这个视频增加的滤镜.
     * @return
     */
    public VideoLayer addMainVideoLayer(FileParameter param,
                                        GPUImageFilter filter) {
        VideoLayer ret = null;

        if (renderer != null)
            ret = renderer.addMainVideoLayer(param, filter);
        else {
            Log.e(TAG, "setMainVideoLayer error render is not avalid");
        }
        return ret;
    }

    /**
     * 增加双视频图层, 一个视频是内容,另一个视频是效果画面. 适用在把一个效果视频融合在当前视频中.
     *
     * @param width
     * @param height
     * @return
     */
    public TwoVideoLayer addTwoVideoLayer(int width, int height) {
        TwoVideoLayer ret = null;

        if (renderer != null)
            ret = renderer.addTwoVideoLayer(width, height);
        else {
            Log.e(TAG, "addTwoVideoLayer error render is not avalid");
        }
        return ret;
    }

    /**
     * 获取一个VideoLayer,从中获取surface,来设置到视频播放器中,
     * 用视频播放器提供的画面,来作为DrawPad的画面输入源.
     * <p>
     * 注意:此方法一定在 startDrawPad之后,在stopDrawPad之前调用.
     *
     * @param width  视频的宽度
     * @param height 视频的高度
     * @return VideoLayer对象
     */
    public VideoLayer addVideoLayer(int width, int height, GPUImageFilter filter) {
        if (renderer != null)
            return renderer.addVideoLayer(width, height, filter);
        else {
            Log.e(TAG, "addVideoLayer error render is not avalid");
            return null;
        }
    }

    /**
     * 增加一个视频图层, 视频可以设置显示区域. 其中FileParameter的配置是:
     * <p>
     * FileParameter param=new FileParameter();
     * if(param.setDataSoure(mVideoPath)){
     * <p>
     * 设置当前需要显示的区域 ,以左上角为0,0坐标.
     *
     * @param startX 开始的X坐标, 即从宽度的什么位置开始
     * @param startY 开始的Y坐标, 即从高度的什么位置开始
     * @param cropW  需要显示的宽度
     * @param cropH  需要显示的高度. param.setShowRect(0, 0, 300, 200);
     *               videoMainLayer=mDrawPadView.addMainVideoLayer(param,new
     *               GPUImageSepiaFilter()); }
     * @param param
     * @param filter
     * @return
     */
    public VideoLayer addVideoLayer(FileParameter param, GPUImageFilter filter) {
        if (renderer != null)
            return renderer.addVideoLayer(param, filter);
        else {
            Log.e(TAG, "addVideoLayer error render is not avalid");
            return null;
        }
    }

    /**
     * 2.8.5版本后新增的,是经过优化后的VideoLayer, 取名字叫VideoLayer2;
     * 用法和VideoLayer相同,增加子图层功能;
     *
     * @param width  视频的宽度
     * @param height 视频的高度
     * @param filter 增加滤镜, 如果不需要滤镜,设置为null;
     * @return
     */
    public VideoLayer2 addVideoLayer2(int width, int height, GPUImageFilter filter) {
        if (renderer != null)
            return renderer.addVideoLayer2(width, height, filter);
        else {
            Log.e(TAG, "addVideoLayer error render is not avalid");
            return null;
        }
    }


    /**
     * 增加一个相机图层. 建议使用 {@link DrawPadCameraView}
     *
     * @param isFaceFront 是否使用前置镜头
     * @param filter      当前使用到的滤镜 ,如果不用, 则设置为null
     * @return
     */
    public CameraLayer addCameraLayer(boolean isFaceFront, GPUImageFilter filter) {
        CameraLayer ret = null;
        if (renderer != null)
            if (filter != null) {
                ret = renderer.addCameraLayer(isFaceFront, filter);
            } else {
                ret = renderer.addCameraLayer(isFaceFront, null);
            }
        else {
            Log.e(TAG, "addCameraLayer error render is not avalid");
        }
        return ret;
    }

    /**
     * 因之前有客户自定义一个Camera图层, 我们的Drawpad可以接受外部客户自定义图层.这里填入.
     */
    // public Layer addCustemLayer()
    // {
    // CameraLayer ret=null;
    // if(renderer!=null){
    // ret =new
    // CameraLayer(getContext(),false,viewWidth,viewHeight,null,mUpdateMode);
    // renderer.addCustemLayer(ret);
    // }else{
    // Log.e(TAG,"CameraMaskLayer error render is not avalid");
    // }
    // return ret;
    // }

    /**
     * 获取一个BitmapLayer 注意:此方法一定在 startDrawPad之后,在stopDrawPad之前调用.
     *
     * @param bmp 图片的bitmap对象,可以来自png或jpg等类型,这里是通过BitmapFactory.
     *            decodeXXX的方法转换后的bitmap对象.
     * @return 一个BitmapLayer对象
     */
    public BitmapLayer addBitmapLayer(Bitmap bmp) {
        if (bmp != null) {
            if (renderer != null && renderer.isRunning())
                return renderer.addBitmapLayer(bmp, null);
            else {
                Log.e(TAG, "addBitmapLayer error render is not avalid");
                return null;
            }
        } else {
            Log.e(TAG, "addBitmapLayer error, bitmap is null");
            return null;
        }
    }

    public BitmapLayer addBitmapLayer(Bitmap bmp, GPUImageFilter filter) {
        if (bmp != null) {
            // Log.i(TAG,"imgBitmapLayer:"+bmp.getWidth()+" height:"+bmp.getHeight());
            if (renderer != null)
                return renderer.addBitmapLayer(bmp, filter);
            else {
                Log.e(TAG, "addBitmapLayer error render is not avalid");
                return null;
            }
        } else {
            Log.e(TAG, "addBitmapLayer error, bitmap is null");
            return null;
        }
    }

    /**
     * 增加纹理图层.
     *
     * @param texid  纹理ID, 这个纹理务必要在我们的ThreadProgress中创建.
     * @param width  纹理的宽度
     * @param height 纹理的高度
     * @param filter 初始化的滤镜.
     * @return
     */
    public TextureLayer addTextureLayer(int texid, int width, int height,
                                        GPUImageFilter filter) {
        if (texid != -1) {
            if (renderer != null && renderer.isRunning())
                return renderer.addTextureLayer(texid, width, height, filter);
            else {
                Log.e(TAG, "addTextureLayer error render is not avalid");
                return null;
            }
        } else {
            Log.e(TAG, "addTextureLayer error, texid is error");
            return null;
        }
    }

    /**
     * 获取一个DataLayer的图层, 数据图层, 是一个RGBA格式的数据, 内部是一个RGBA格式的图像.
     *
     * @param dataWidth  图像的宽度
     * @param dataHeight 图像的高度.
     * @return
     */
    public DataLayer addDataLayer(int dataWidth, int dataHeight) {
        if (dataWidth > 0 && dataHeight > 0) {
            if (renderer != null)
                return renderer.addDataLayer(dataWidth, dataHeight);
            else {
                Log.e(TAG, "addDataLayer error render is not avalid");
                return null;
            }
        } else {
            Log.e(TAG, "addDataLayer error, data size is error");
            return null;
        }
    }

    /**
     * 增加一个gif图层.
     *
     * @param gifPath gif的绝对地址,
     * @return
     */
    public GifLayer addGifLayer(String gifPath) {
        if (renderer != null)
            return renderer.addGifLayer(gifPath);
        else {
            Log.e(TAG, "addYUVLayer error! render is not avalid");
            return null;
        }
    }

    /**
     * 增加一个gif图层,
     * <p>
     * resId 来自apk中drawable文件夹下的各种资源文件, 我们会在GifLayer中拷贝这个资源到默认文件夹下面,
     * 然后作为一个普通的gif文件来做处理,使用完后, 会在Giflayer 图层释放的时候, 删除.
     *
     * @param resId 来自apk中drawable文件夹下的各种资源文件.
     * @return
     */
    public GifLayer addGifLayer(int resId) {
        if (renderer != null)
            return renderer.addGifLayer(resId);
        else {
            Log.e(TAG, "addGifLayer error! render is not avalid");
            return null;
        }
    }

    /**
     * 增加一个mv图层, mv图层分为两个视频文件, 一个是彩色的视频, 一个黑白视频
     *
     * @param srcPath
     * @param maskPath
     * @return
     */
    public MVLayer addMVLayer(String srcPath, String maskPath) {
        if (renderer != null)
            return renderer.addMVLayer(srcPath, maskPath);
        else {
            Log.e(TAG, "addMVLayer error render is not avalid");
            return null;
        }
    }

    /**
     * 是否在mv好了之后, 直接去显示, 如果不想直接显示, 可以先设置isShow=false,然后在需要显示的使用, 调用
     * {@link MVLayer #setPlayEnable(boolean)}, 此方法暂时只能被调用一次.
     *
     * @param srcPath
     * @param maskPath
     * @param isplay   是否直接显示.
     * @return
     */
    public MVLayer addMVLayer(String srcPath, String maskPath, boolean isplay) {
        if (renderer != null)
            return renderer.addMVLayer(srcPath, maskPath, isplay);
        else {
            Log.e(TAG, "addMVLayer error render is not avalid");
            return null;
        }
    }

    /**
     * 获得一个 ViewLayer,您可以在获取后,仿照我们的例子,来为视频增加各种UI空间. 注意:此方法一定在
     * startDrawPad之后,在stopDrawPad之前调用.
     *
     * @return 返回ViewLayer对象.
     */
    public ViewLayer addViewLayer() {
        if (renderer != null)
            return renderer.addViewLayer();
        else {
            Log.e(TAG, "addViewLayer error render is not avalid");
            return null;
        }
    }

    /**
     * 获得一个 CanvasLayer
     * 在 startDrawPad之后,在stopDrawPad之前调用.
     * @return
     */
    public CanvasLayer addCanvasLayer() {
        if (renderer != null)
            return renderer.addCanvasLayer();
        else {
            Log.e(TAG, "addCanvasLayer error render is not avalid");
            return null;
        }
    }

    /**
     * 增加一个yuv图层, 让您可以把YUV数据输入进来,当前仅支持NV21的格式
     * <p>
     * yuv数据,可以是别家SDK处理后的结果, 或Camera的onPreviewFrame回调的数据,
     * 或您本地的视频数据.也可以是您本地的视频数据.
     *
     * @param width
     * @param height
     * @return
     */
    public YUVLayer addYUVLayer(int width, int height) {
        if (renderer != null)
            return renderer.addYUVLayer(width, height);
        else {
            Log.e(TAG, "addCanvasLayer error render is not avalid");
            return null;
        }
    }

    /**
     * 从渲染线程列表中移除并销毁这个Layer; 注意:此方法一定在 startDrawPad之后,在stopDrawPad之前调用.
     * @param layer
     */
    public void removeLayer(Layer layer) {
        if (layer != null) {
            if (renderer != null)
                renderer.removeLayer(layer);
            else {
                Log.w(TAG, "removeLayer error render is not avalid");
            }
        }
    }

    /**
     * [不再使用,请使用每个图层的switchFilterTo方法] 为已经创建好的图层对象切换滤镜效果 注意: 这里内部会在切换的时候, 会销毁
     * 之前的滤镜对象, 然后重新增加, 故您不可以把同一个滤镜对象再次放到进来, 您如果还想使用之前的滤镜,则应该重新创建一个对象.
     *
     * @param layer  已经创建好的Layer对象
     * @param filter 要切换到的滤镜对象.
     * @return 切换成功, 返回true; 失败返回false
     */
    @Deprecated
    public boolean switchFilterTo(Layer layer, GPUImageFilter filter) {
        if (renderer != null) {
            return renderer.switchFilter(layer, filter);
        }
        return false;
    }

    /**
     * [不再使用,请使用每个图层的switchFilterList方法]
     * <p>
     * 为一个图层切换多个滤镜. 即一个滤镜处理完后的输出, 作为下一个滤镜的输入.
     * <p>
     * filter的列表, 是先add进去,最新渲染, 把第一个渲染的结果传递给第二个,第二个传递给第三个,以此类推.
     * <p>
     * 注意: 这里内部会在切换的时候, 会销毁 之前的列表中的所有滤镜对象, 然后重新增加, 故您不可以把同一个滤镜对象再次放到进来,
     * 您如果还想使用之前的滤镜,则应该重新创建一个对象.
     *
     * @param layer   图层对象,
     * @param filters 滤镜数组; 如果设置为null,则不增加滤镜.
     * @return
     */
    @Deprecated
    public boolean switchFilterList(Layer layer,
                                    ArrayList<GPUImageFilter> filters) {
        if (renderer != null) {
            return renderer.switchFilterList(layer, filters);
        }
        return false;
    }

    /**
     * 是否在开始运行DrawPad的时候,检查您设置的码率和分辨率是否正常.
     * <p>
     * 默认是检查, 如果您清楚码率大小的设置,请调用此方法,不再检查.
     */
    public void setNotCheckBitRate() {
        isCheckBitRate = false;
    }

    /**
     * 是否在开始运行DrawPad的时候, 检查您设置的DrawPad宽高是否是16的倍数. 默认是检查.
     */
    public void setNotCheckDrawPadSize() {
        isCheckPadSize = false;
    }

    public interface onViewAvailable {
        void viewAvailable(DrawPadView v);
    }

    private class SurfaceCallback implements SurfaceTextureListener {

        /**
         * Invoked when a {@link TextureView}'s SurfaceTexture is ready for use.
         * 当画面呈现出来的时候, 会调用这个回调.
         * <p>
         * 当Activity跳入到别的界面后,这时会调用
         * {@link #onSurfaceTextureDestroyed(SurfaceTexture)} 销毁这个Texture,
         * 如果您想在再次返回到当前Activity时,再次显示预览画面, 可以在这个方法里重新设置一遍DrawPad,并再次startDrawPad
         */

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                              int width, int height) {
            mSurfaceTexture = surface;
            drawPadHeight = height;
            drawPadWidth = width;
            Log.i(TAG,"LSTODO ------onSurfaceTextureAvailable---onview available");
            if (mViewAvailable != null) {
                mViewAvailable.viewAvailable(null);
            }
        }

        /**
         * Invoked when the {@link SurfaceTexture}'s buffers size changed.
         * 当创建的TextureView的大小改变后, 会调用回调.
         * <p>
         * 当您本来设置的大小是480x480,而DrawPad会自动的缩放到父view的宽度时,会调用这个回调,提示大小已经改变,
         * 这时您可以开始startDrawPad 如果你设置的大小更好等于当前Texture的大小,则不会调用这个, 详细的注释见
         * {@link DrawPadView#startDrawPad(onDrawPadProgressListener, onDrawPadCompletedListener)}
         */
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                                int width, int height) {
            mSurfaceTexture = surface;
            drawPadHeight = height;
            drawPadWidth = width;
            if (mSizeChangedCB != null)
                mSizeChangedCB.onSizeChanged(width, height);
        }

        /**
         * Invoked when the specified {@link SurfaceTexture} is about to be
         * destroyed.
         * <p>
         * 当您跳入到别的Activity的时候, 会调用这个,销毁当前Texture;
         */
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            mSurfaceTexture = null;
            // drawPadHeight=0;
            // drawPadWidth=0;
            Log.i(TAG,"LSTODO ------onSurfaceTextureDestroyed--");

            stopDrawPad(); // 可以在这里增加以下. 这样当Texture销毁的时候, 停止DrawPad


            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    }
}
