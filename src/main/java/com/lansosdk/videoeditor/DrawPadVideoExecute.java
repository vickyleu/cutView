package com.lansosdk.videoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.lansosdk.box.AudioSource;
import com.lansosdk.box.BitmapLayer;
import com.lansosdk.box.CanvasLayer;
import com.lansosdk.box.DataLayer;
import com.lansosdk.box.DrawPadUpdateMode;
import com.lansosdk.box.DrawPadVideoRunnable;
import com.lansosdk.box.FileParameter;
import com.lansosdk.box.GifLayer;
import com.lansosdk.box.Layer;
import com.lansosdk.box.MVLayer;
import com.lansosdk.box.TimeRange;
import com.lansosdk.box.VideoLayer;
import com.lansosdk.box.onAudioPadThreadProgressListener;
import com.lansosdk.box.onDrawPadCompletedListener;
import com.lansosdk.box.onDrawPadErrorListener;
import com.lansosdk.box.onDrawPadOutFrameListener;
import com.lansosdk.box.onDrawPadProgressListener;
import com.lansosdk.box.onDrawPadThreadProgressListener;

import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageFilter;

public class DrawPadVideoExecute {

    private static final String TAG = "DrawpadVideoExecute";
    protected boolean isCheckBitRate = true;
    protected boolean isCheckPadSize = true;
    private DrawPadVideoRunnable renderer = null;
    private int padWidth, padHeight;
    private boolean mPauseRecord = false;

    /**
     * @param ctx       语境,android的Context
     * @param srcPath   主视频的路径
     * @param padwidth  DrawPad的的宽度
     * @param padheight DrawPad的的高度
     * @param bitrate   编码视频所希望的码率,比特率.
     * @param filter    为视频图层增加一个滤镜 如果您要增加多个滤镜,则用
     *                  {@link #switchFilterList(Layer, ArrayList)}
     * @param dstPath   视频处理后保存的路径.
     */
    public DrawPadVideoExecute(Context ctx, String srcPath, int padwidth,
                               int padheight, int bitrate, GPUImageFilter filter, String dstPath) {
        if (renderer == null) {
            renderer = new DrawPadVideoRunnable(ctx, srcPath, padwidth,
                    padheight, bitrate, filter, dstPath);
        }
        this.padWidth = padwidth;
        this.padHeight = padheight;
    }

    /**
     * 只比上面少了一个码率的设置.
     *
     * @param ctx
     * @param srcPath
     * @param padwidth
     * @param padheight
     * @param filter
     * @param dstPath
     */
    public DrawPadVideoExecute(Context ctx, String srcPath, int padwidth,
                               int padheight, GPUImageFilter filter, String dstPath) {
        if (renderer == null) {
            renderer = new DrawPadVideoRunnable(ctx, srcPath, padwidth,
                    padheight, 0, filter, dstPath);
        }
        this.padWidth = padwidth;
        this.padHeight = padheight;
    }

    /**
     * Drawpad后台执行, 可以指定开始时间. 因视频编码原理, 会定位到 [指定时间]前面最近的一个IDR刷新帧,
     * 然后解码到[指定时间],容器才开始渲染视频,中间或许有一些延迟.
     *
     * @param ctx
     * @param srcPath     主视频的完整路径.
     * @param startTimeUs 开始时间. 单位为微秒 1s=1000*1000微秒; 注意!!!
     * @param padwidth    容器宽度.
     * @param padheight   容器高度.
     * @param bitrate     容器编码的码率
     * @param filter      为这视频设置一个滤镜, 如果您要增加多个滤镜,则用
     *                    {@link #switchFilterList(Layer, ArrayList)}
     * @param dstPath     处理后保存的目标文件.
     */
    public DrawPadVideoExecute(Context ctx, String srcPath, long startTimeUs,
                               int padwidth, int padheight, int bitrate, GPUImageFilter filter,
                               String dstPath) {
        if (renderer == null) {
            renderer = new DrawPadVideoRunnable(ctx, srcPath, startTimeUs,
                    padwidth, padheight, bitrate, filter, dstPath);
        }
        this.padWidth = padwidth;
        this.padHeight = padheight;
    }

    /**
     * 相对于上面,只是少了码率.
     *
     * @param ctx
     * @param srcPath
     * @param startTimeUs 单位为微秒 1s=1000*1000微秒; 注意!!!
     * @param padwidth
     * @param padheight
     * @param filter
     * @param dstPath
     */
    public DrawPadVideoExecute(Context ctx, String srcPath, long startTimeUs,
                               int padwidth, int padheight, GPUImageFilter filter, String dstPath) {
        if (renderer == null) {
            renderer = new DrawPadVideoRunnable(ctx, srcPath, startTimeUs,
                    padwidth, padheight, 0, filter, dstPath);
        }
        this.padWidth = padwidth;
        this.padHeight = padheight;
    }

    /**
     * 增加了FileParameter类, 其中FileParameter的配置是:
     * <p>
     * FileParameter param=new FileParameter();
     * if(param.setDataSoure(mVideoPath)){
     * <p>
     * 设置当前需要显示的区域 ,以左上角为0,0坐标.
     *
     * @param startX    开始的X坐标, 即从宽度的什么位置开始
     * @param startY    开始的Y坐标, 即从高度的什么位置开始
     * @param cropW     需要显示的宽度
     * @param cropH     需要显示的高度. param.setShowRect(0, 0, 300, 200);
     *                  param.setStartTimeUs(5*1000*1000); //从5秒处开始处理, 当前仅在后台处理时有效.
     *                  videoMainLayer=mDrawPadView.addMainVideoLayer(param,new
     *                  GPUImageSepiaFilter()); }
     *                  ------------------------------------------------------
     * @param ctx
     * @param filebox
     * @param padwidth
     * @param padheight
     * @param filter
     * @param dstPath
     */
    public DrawPadVideoExecute(Context ctx, FileParameter fileParam,
                               int padwidth, int padheight, GPUImageFilter filter, String dstPath) {
        if (renderer == null) {
            renderer = new DrawPadVideoRunnable(ctx, fileParam, padwidth,
                    padheight, 0, filter, dstPath);
        }
        this.padWidth = padwidth;
        this.padHeight = padheight;
    }

    public DrawPadVideoExecute(Context ctx, FileParameter fileParam,
                               int padwidth, int padheight, int bitrate, GPUImageFilter filter,
                               String dstPath) {
        if (renderer == null) {
            renderer = new DrawPadVideoRunnable(ctx, fileParam, padwidth,
                    padheight, bitrate, filter, dstPath);
        }
        this.padWidth = padwidth;
        this.padHeight = padheight;
    }

    /**
     * 在处理中, 忽略音频部分;默认不忽略;
     */
    public void setIngoreAudio() {
        if (renderer != null) {
            renderer.setIngoreAudio();
        }
    }

    public void setEditModeVideo(boolean is) {
        if (renderer != null) {
            renderer.setEditModeVideo(is);
        }
    }

    /**
     * 启动DrawPad,开始执行.
     * <p>
     * 开启成功,返回true, 失败返回false
     */
    public boolean startDrawPad() {
        if (renderer != null && renderer.isRunning() == false) {
            return renderer.startDrawPad();
        }
        return false;
    }

    public void stopDrawPad() {
        if (renderer != null && renderer.isRunning()) {
            renderer.stopDrawPad();
        }
    }

    /**
     * 代码不再使用, 请用 {@link #addTimeStretch(float, long, long)}
     */
    @Deprecated
    public void adjustEncodeSpeed(float speed) {
        if (renderer != null) {
            renderer.adjustEncodeSpeed(speed);
        }
    }

    /**
     * 此方法已废弃, 请不要使用;
     *
     * @param use
     */
    @Deprecated
    public void setUseMainVideoPts(boolean use) {
    }

    /**
     * 此方法已废弃, 请不要使用;
     *
     * @param mode
     * @param fps
     */
    @Deprecated
    public void setUpdateMode(DrawPadUpdateMode mode, int fps) {
    }

    /**
     * 在您配置了 OutFrame, 要输出每一帧的时候, 是否要禁止编码器. 当你只想要处理后的 数据, 而暂时不需要编码成最终的目标文件时,
     * 把这里设置为true. 默认是false;
     *
     * @param dis
     */
    public void setDisableEncode(boolean dis) {
        if (renderer != null && renderer.isRunning() == false) {
            renderer.setDisableEncode(dis);
        }
    }

    /**
     * 音频处理的进度, 如果你要调整每个AudioSource对象在不同时段内的各种状态,则可以在这个进度中, 判断时间戳来调整.
     * 比如静音,比如增大音量
     *
     * @param li
     */
    public void setAudioProgressListener(onAudioPadThreadProgressListener li) {
        if (renderer != null) {
            renderer.setAudioProgressListener(li);
        }
    }

    /**
     * DrawPad每执行完一帧画面,会调用这个Listener,返回的timeUs是当前画面的时间戳(微妙),
     * 可以利用这个时间戳来做一些变化,比如在几秒处缩放, 在几秒处平移等等.从而实现一些动画效果.
     * <p>
     * (注意, 这个进度回调, 是经过Handler异步调用, 工作在主线程的. 如果你要严格按照时间来,则需要用setDrawPadThreadProgressListener)
     *
     * @param currentTimeUs 当前DrawPad处理画面的时间戳.,单位微秒.
     */
    public void setDrawPadProgressListener(onDrawPadProgressListener listener) {
        if (renderer != null) {
            renderer.setDrawPadProgressListener(listener);
        }
    }

    /**
     * 方法与 onDrawPadProgressListener不同的地方在于: 即将开始一帧渲染的时候,
     * 直接执行这个回调中的代码,不通过Handler传递出去,你可以精确的执行一些这一帧的如何操作. 故不能在回调 内增加各种UI相关的代码.
     */
    public void setDrawPadThreadProgressListener(
            onDrawPadThreadProgressListener listener) {
        if (renderer != null) {
            renderer.setDrawPadThreadProgressListener(listener);
        }
    }

    /**
     * DrawPad执行完成后的回调.
     *
     * @param listener
     */
    public void setDrawPadCompletedListener(onDrawPadCompletedListener listener) {
        if (renderer != null) {
            renderer.setDrawPadCompletedListener(listener);
        }
    }

    /**
     * 设置当前DrawPad运行错误的回调监听.
     *
     * @param listener
     */
    public void setDrawPadErrorListener(onDrawPadErrorListener listener) {
        if (renderer != null) {
            renderer.setDrawPadErrorListener(listener);
        }
    }

    /**
     * 设置每处理一帧的数据监听, 等于把当前处理的这一帧的画面拉出来, 您可以根据这个画面来自行的编码保存, 或网络传输.
     * <p>
     * 建议在这里拿到数据后, 放到queue中, 然后在其他线程中来异步读取queue中的数据, 请注意queue中数据的总大小, 要及时处理和释放,
     * 以免内存过大,造成OOM问题
     *
     * @param listener 监听对象
     */
    public void setDrawPadOutFrameListener(onDrawPadOutFrameListener listener) {
        if (renderer != null) {
            renderer.setDrawpadOutFrameListener(padWidth, padHeight, 1,
                    listener);
        }
    }

    public void setDrawPadOutFrameListener(int width, int height,
                                           onDrawPadOutFrameListener listener) {
        if (renderer != null) {
            renderer.setDrawpadOutFrameListener(width, height, 1, listener);
        }
    }

    /**
     * 设置setOnDrawPadOutFrameListener后, 你可以设置这个方法来让listener是否运行在Drawpad线程中.
     * 如果你要直接使用里面的数据, 则不用设置, 如果你要开启另一个线程, 把listener传递过来的数据送过去,则建议设置为true;
     *
     * @param en
     */
    public void setOutFrameInDrawPad(boolean en) {
        if (renderer != null) {
            renderer.setOutFrameInDrawPad(en);
        }
    }

    /**
     * 把当前图层放到DrawPad的最底部. DrawPad运行后,有效.
     *
     * @param pen
     */
    public void bringToBack(Layer layer) {
        if (renderer != null && renderer.isRunning()) {
            renderer.bringToBack(layer);
        }
    }

    /**
     * 把当前图层放到最顶层
     *
     * @param layer
     */
    public void bringToFront(Layer layer) {
        if (renderer != null && renderer.isRunning()) {
            renderer.bringToFront(layer);
        }
    }

    /**
     * 改变指定图层的位置.
     *
     * @param layer
     * @param position
     */
    public void changeLayerPosition(Layer layer, int position) {
        if (renderer != null && renderer.isRunning()) {
            renderer.changeLayerPosition(layer, position);
        }
    }

    /**
     * 交换两个图层的位置.
     *
     * @param first
     * @param second
     */
    public void swapTwoLayerPosition(Layer first, Layer second) {
        if (renderer != null && renderer.isRunning()) {
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
     * 得到当前DrawPadVideoRunnable中设置的视频图层对象.
     *
     * @return
     */
    public VideoLayer getMainVideoLayer() {
        if (renderer != null && renderer.isRunning()) {
            return renderer.getMainVideoLayer();
        } else {
            return null;
        }
    }

    public AudioSource getMainAudioSource() {
        if (renderer != null) {
            return renderer.getMainAudioSource();
        } else {
            return null;
        }
    }

    /**
     * 在DrawPad线程开始前增加;
     *
     * @param srcPath
     * @param startFromPadTimeUs
     * @param durationUs
     * @param volume
     * @return
     */
    @Deprecated
    public boolean addSubAudio(String srcPath, long startFromPadTimeUs,
                               long durationUs, float volume) {
        if (renderer != null && renderer.isRunning() == false) {
            AudioSource audio = renderer.addSubAudio(srcPath,
                    startFromPadTimeUs, 0, durationUs);
            if (audio != null) {
                audio.setVolume(volume);
            }
            return audio != null;
        } else {
            return false;
        }
    }

    /**
     * 不再使用. 在DrawPad线程开始前增加;
     *
     * @return
     */
    @Deprecated
    public boolean addSubAudio(String srcPath, long startTimeMs,
                               long durationMs, float mainvolume, float volume) {
        if (renderer != null && renderer.isRunning() == false) {
            AudioSource audio = renderer.addSubAudio(srcPath, startTimeMs,
                    durationMs);
            if (audio != null) {
                audio.setVolume(volume);
            }
            return audio != null;
        } else {
            return false;
        }
    }

    /**
     * 增加音频, 在DrawPad线程开始前增加;
     *
     * @param srcPath
     * @return
     */
    public AudioSource addSubAudio(String srcPath) {
        if (renderer != null && renderer.isRunning() == false) {
            return renderer.addSubAudio(srcPath);
        } else {
            return null;
        }
    }

    /**
     * 增加其他声音; 在DrawPad线程开始前增加;
     *
     * @param srcPath
     * @param startFromPadTime 从主音频的什么时间开始增加
     * @return
     */
    public AudioSource addSubAudio(String srcPath, long startFromPadTime) {
        if (renderer != null && renderer.isRunning() == false) {
            return renderer.addSubAudio(srcPath, startFromPadTime, -1);
        } else {
            return null;
        }
    }

    /**
     * 增加其他声音;
     * <p>
     * 在DrawPad线程开始前增加;
     *
     * @param srcPath        路径, 可以是mp3或m4a或 带有音频的MP4文件;
     * @param startFromPadUs 从主音频的什么时间开始增加
     * @param durationUs     把这段声音多长插入进去.
     * @return 返回一个AudioSource对象;
     */
    public AudioSource addSubAudio(String srcPath, long startFromPadUs,
                                   long durationUs) {
        if (renderer != null && renderer.isRunning() == false) {
            return renderer.addSubAudio(srcPath, startFromPadUs, durationUs);
        } else {
            return null;
        }
    }

    /**
     * 如果要调节音量, 则增加拿到对象后, 开始调节.
     * <p>
     * 在DrawPad线程开始前增加;
     *
     * @param srcPath
     * @param startFromPadUs   从容器的什么位置开始增加
     * @param startAudioTimeUs 把当前声音的开始时间增加进去.
     * @param durationUs       增加多少, 时长.
     * @param isLoop           是否循环.
     * @return
     */
    public AudioSource addSubAudio(String srcPath, long startFromPadUs,
                                   long startAudioTimeUs, long durationUs) {
        if (renderer != null && renderer.isRunning() == false) {
            return renderer.addSubAudio(srcPath, startFromPadUs,
                    startAudioTimeUs, durationUs);
        } else {
            return null;
        }
    }

    /**
     * 增加时间冻结,即在视频的什么时间段开始冻结, 静止的结束时间; 为了统一: 这里用结束时间; 比如你要从原视频的5秒地方开始静止, 静止3秒钟,
     * 则这里是3*1000*1000 , 8*1000*1000 (画面停止的过程中, 可以做一些缩放,移动等特写等)
     *
     * @param startTimeUs 从输入的视频/音频的哪个时间点开始冻结,
     * @param endTimeUs   (这里理解为:冻结的时长+开始时间);
     */
    public void addTimeFreeze(long startTimeUs, long endTimeUs) {
        if (renderer != null && renderer.isRunning() == false) {
            renderer.addTimeFreeze(startTimeUs, endTimeUs);
        } else {
            Log.e(TAG, "addTimeFreeze error, maybe drawpad is running");
        }
    }

    /**
     * 给这个主视频的音频部分和视频部分,分别做时间拉伸(某一段的速度调节)
     * <p>
     * 这个设置等于分别给当前视频的 VideoLayer和AudioSource分别设置 时间拉伸;
     * <p>
     * 可以被多次调用.
     * <p>
     * 在DrawPad容器开始前调用
     *
     * @param rate        拉伸的速度, 范围0.5--2.0; 0.5是放慢1倍, 2.0是加快一倍; 1.0f是默认,
     *                    没有设置的时间段,默认是1.0f;
     * @param startTimeUs
     * @param endTimeUs
     */
    public void addTimeStretch(float rate, long startTimeUs, long endTimeUs) {
        if (renderer != null && renderer.isRunning() == false) {
            renderer.addTimeStretch(rate, startTimeUs, endTimeUs);
        } else {
            Log.e(TAG, "addTimeStretch error, maybe drawpad is running");
        }
    }

    /**
     * 增加时间重复;
     * <p>
     * 类似综艺节目中, 当好玩的画面发生的时候, 多次重复的效果.
     * <p>
     * 在DrawPad容器开始前调用
     *
     * @param startUs 相对原视频/原音频的开始时间;
     * @param endUs   相对原视频/原音频的结束时间;
     * @param loopcnt 重复的次数;
     */
    public void addTimeRepeat(long startUs, long endUs, int loopcnt) {
        if (renderer != null && renderer.isRunning() == false) {
            renderer.addTimeRepeat(startUs, endUs, loopcnt);
        } else {
            Log.e(TAG, "addTimeRepeat error, maybe drawpad is running");
        }
    }

    /**
     * 增加时间拉伸
     * <p>
     * 在DrawPad容器开始前调用
     *
     * @param list
     */
    public void addTimeStretch(List<TimeRange> list) {
        if (renderer != null && renderer.isRunning() == false) {
            renderer.addTimeStretch(list);
        } else {
            Log.e(TAG, "addTimeStretch error, maybe drawpad is running");
        }
    }

    /**
     * 时间冻结
     * <p>
     * 在DrawPad容器开始前调用
     *
     * @param list
     */
    public void addTimeFreeze(List<TimeRange> list) {
        if (renderer != null && renderer.isRunning() == false) {
            renderer.addTimeFreeze(list);
        } else {
            Log.e(TAG, "addTimeFreeze error, maybe drawpad is running");
        }
    }

    /**
     * 时间重复.
     * <p>
     * 在DrawPad容器开始前调用
     *
     * @param list
     */
    public void addTimeRepeat(List<TimeRange> list) {
        if (renderer != null && renderer.isRunning() == false) {
            renderer.addTimeRepeat(list);
        } else {
            Log.e(TAG, "addTimeRepeat error, maybe drawpad is running");
        }
    }

    /**
     * 增加图片图层.
     * <p>
     * 在DrawPad容器开始后调用;
     *
     * @param bmp
     * @return
     */
    public BitmapLayer addBitmapLayer(Bitmap bmp) {
        if (renderer != null && renderer.isRunning()) {
            return renderer.addBitmapLayer(bmp, null);
        } else {
            return null;
        }
    }

    /**
     * 增加数据图层, DataLayer有一个
     * {@link DataLayer#pushFrameToTexture(java.nio.IntBuffer)}
     * 可以把数据或图片传递到DrawPad中.
     *
     * @param dataWidth
     * @param dataHeight
     * @return
     */
    public DataLayer addDataLayer(int dataWidth, int dataHeight) {
        if (renderer != null && renderer.isRunning()) {
            return renderer.addDataLayer(dataWidth, dataHeight);
        } else {
            return null;
        }
    }

    /**
     * 不再使用
     *
     * @param videoPath
     * @param vWidth
     * @param vHeight
     * @param filter
     * @return
     */
    @Deprecated
    public VideoLayer addVideoLayer(String videoPath, int vWidth, int vHeight,
                                    GPUImageFilter filter) {
        if (renderer != null && renderer.isRunning()) {
            return renderer.addVideoLayer(videoPath, filter);
        } else {
            return null;
        }
    }

    /**
     * 向DrawPad容器里增加另一个视频, 增加后,等于叠加在原有的视频上面; 这里叠加后, 当前的视频是不透明的;如果你要增加透明视频,则可以用
     * TwoVideoLayer 或MVLayer;
     *
     * @param videoPath  视频的完整路径;
     * @param filter视频滤镜 ,如果不增加滤镜,则赋值为null
     * @return
     */
    public VideoLayer addVideoLayer(String videoPath, GPUImageFilter filter) {
        if (renderer != null && renderer.isRunning()) {
            return renderer.addVideoLayer(videoPath, filter);
        } else {
            return null;
        }
    }

    /**
     * 当mv在解码的时候, 是否异步执行; 如果异步执行,则MV解码可能没有那么快,从而MV画面会有慢动作的现象.
     * 如果同步执行,则视频处理会等待MV解码完成, 从而处理速度会慢一些,但MV在播放时,是正常的.
     *
     * @param srcPath  MV的彩色视频
     * @param maskPath MV的黑白视频.
     * @param isAsync  是否异步执行.
     * @return
     */
    public MVLayer addMVLayer(String srcPath, String maskPath, boolean isAsync) {
        if (renderer != null && renderer.isRunning()) {
            return renderer.addMVLayer(srcPath, maskPath);
        } else {
            return null;
        }
    }

    /**
     * 增加一个MV图层.
     *
     * @param srcPath
     * @param maskPath
     * @return
     */
    public MVLayer addMVLayer(String srcPath, String maskPath) {
        if (renderer != null && renderer.isRunning()) {
            return renderer.addMVLayer(srcPath, maskPath);
        } else {
            return null;
        }
    }

    /**
     * 增加gif图层
     *
     * @param gifPath
     * @return
     */
    public GifLayer addGifLayer(String gifPath) {
        if (renderer != null && renderer.isRunning()) {
            return renderer.addGifLayer(gifPath);
        } else {
            return null;
        }
    }

    /**
     * 增加gif图层 resId 来自apk中drawable文件夹下的各种资源文件, 我们会在GifLayer中拷贝这个资源到默认文件夹下面,
     * 然后作为一个普通的gif文件来做处理,使用完后, 会在Giflayer 图层释放的时候, 删除.
     *
     * @param resId
     * @return
     */
    public GifLayer addGifLayer(int resId) {
        if (renderer != null && renderer.isRunning()) {
            return renderer.addGifLayer(resId);
        } else {
            return null;
        }
    }

    /**
     * 增加一个Canvas图层, 可以用Android系统的Canvas来绘制一些文字线条,颜色等. 可参考我们的的 "花心形"的举例
     * 因为Android的View机制是无法在非UI线程中使用View的. 但可以使用Canvas这个类工作在其他线程.
     * 因此我们设计了CanvasLayer,从而可以用Canvas来做各种Draw文字, 线条,图案等.
     *
     * @return
     */
    public CanvasLayer addCanvasLayer() {
        if (renderer != null && renderer.isRunning()) {
            return renderer.addCanvasLayer();
        } else {
            return null;
        }
    }

    /**
     * 删除一个图层.
     *
     * @param layer
     */
    public void removeLayer(Layer layer) {
        if (renderer != null && renderer.isRunning()) {
            renderer.removeLayer(layer);
        }
    }

    /**
     * 已废弃.请用pauseRecord();
     */
    @Deprecated
    public void pauseRecordDrawPad() {
        pauseRecord();
    }

    /**
     * 已废弃,请用resumeRecord();
     */
    @Deprecated
    public void resumeRecordDrawPad() {
        resumeRecord();
    }

    /**
     * 暂停录制, 使用在 : 开始DrawPad后, 需要暂停录制, 来增加一些图层, 然后恢复录制的场合. 此方法使用在DrawPad线程中的
     * 暂停和恢复的作用, 不能用在一个Activity的onPause和onResume中.
     */
    public void pauseRecord() {
        if (renderer != null && renderer.isRunning()) {
            renderer.pauseRecordDrawPad();
        } else {
            mPauseRecord = true;
        }
    }

    /**
     * 恢复录制. 此方法使用在DrawPad线程中的 暂停和恢复的作用, 不能用在一个Activity的onPause和onResume中.
     */
    public void resumeRecord() {
        if (renderer != null && renderer.isRunning()) {
            renderer.resumeRecordDrawPad();
        } else {
            mPauseRecord = false;
        }
    }

    /**
     * 是否在录制.
     *
     * @return
     */
    public boolean isRecording() {
        if (renderer != null && renderer.isRunning()) {
            return renderer.isRecording();
        } else {
            return false;
        }
    }

    /**
     * DrawPad是否在运行
     *
     * @return
     */
    public boolean isRunning() {
        if (renderer != null) {
            return renderer.isRunning();
        } else {
            return false;
        }
    }

    public void switchFilterTo(Layer layer, GPUImageFilter filter) {
        if (renderer != null && renderer.isRunning()) {
            renderer.switchFilterTo(layer, filter);
        }
    }

    /**
     * 切换滤镜 为一个图层切换多个滤镜. 即一个滤镜处理完后的输出, 作为下一个滤镜的输入.
     * <p>
     * filter的列表, 是先add进去,最新渲染, 把第一个渲染的结果传递给第二个,第二个传递给第三个,以此类推.
     * <p>
     * 注意: 这里内部会在切换的时候, 会销毁 之前的列表中的所有滤镜对象, 然后重新增加, 故您不可以把同一个滤镜对象再次放到进来,
     * 您如果还想使用之前的滤镜,则应该重新创建一个对象.
     *
     * @param layer
     * @param filters
     */
    public void switchFilterList(Layer layer, ArrayList<GPUImageFilter> filters) {
        if (renderer != null && renderer.isRunning()) {
            renderer.switchFilterList(layer, filters);
        }
    }

    /**
     * 释放DrawPad,方法等同于 {@link #stopDrawPad()} 只是为了代码标准化而做.
     */
    public void releaseDrawPad() {
        // TODO Auto-generated method stub
        if (renderer != null && renderer.isRunning()) {
            renderer.releaseDrawPad();
        }
        mPauseRecord = false;
        renderer = null;
    }

    /**
     * 停止DrawPad, 并释放资源.如果想再次开始,需要重新new, 然后start.
     * <p>
     * 注意:这里阻塞执行, 只有等待opengl线程执行退出完成后,方返回. 方法等同于 {@link #stopDrawPad()}
     * 只是为了代码标准化而做.
     */
    public void release() {
        releaseDrawPad();
    }

    /**
     * 是否在开始运行DrawPad的时候,检查您设置的码率和分辨率是否正常.
     * <p>
     * 默认是检查, 如果您清楚码率大小的设置,请调用此方法,不再检查.
     */
    public void setNotCheckBitRate() {
        if (renderer != null && renderer.isRunning() == false) {
            renderer.setNotCheckBitRate();
        } else {
            isCheckBitRate = false;
        }
    }

    /**
     * 是否在开始运行DrawPad的时候, 检查您设置的DrawPad宽高是否是16的倍数. 默认是检查.
     */
    public void setNotCheckDrawPadSize() {
        if (renderer != null && renderer.isRunning() == false) {
            renderer.setNotCheckDrawPadSize();
        } else {
            isCheckPadSize = false;
        }
    }

    public void setCheckDrawPadSize(boolean check) {
        if (renderer != null && renderer.isRunning() == false) {
            renderer.setCheckDrawPadSize(check);
        } else {
            isCheckPadSize = check;
        }
    }
}
