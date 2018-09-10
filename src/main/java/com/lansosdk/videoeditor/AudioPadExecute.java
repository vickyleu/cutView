package com.lansosdk.videoeditor;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.lansosdk.box.AudioPad;
import com.lansosdk.box.AudioSource;
import com.lansosdk.box.IAudioSourceInput;
import com.lansosdk.box.onAudioPadCompletedListener;
import com.lansosdk.box.onAudioPadProgressListener;
import com.lansosdk.box.onAudioPadThreadProgressListener;

/**
 * 音频图层后的后台处理. 此类是用来在后台做音频混合处理使用.
 * <p>
 * 使用在两种场景中: 场景一: 给一段完整的音频上:增加别的声音, 如搞笑声,闪电声等等.生成的文件和源声音一样的长度,只是内容根据您的设置而变化了.
 * <p>
 * 场景二: 先设置整体的音频长度, 然后在分别增加声音.比如创建一段20s的声音, 1--4秒一种; 3--5秒一种,可以交叉的;最后生成您设置长度的声音.
 * <p>
 * <p>
 * 当前处理后的音频编码成aac格式, 采样率是44100, 双通道, 64000码率.
 * <p>
 * 如果您仅仅用来做音频拼接, 可以采用 {@link AudioConcat}来做. 当前的音频格式支持 MP3,WAV, AAC(m4a后缀),
 * 采样率为44100, 通道数为2,其他格式暂不支持,请注意 当前的音频格式支持 MP3,WAV, AAC(m4a后缀), 采样率为44100,
 * 通道数为2,其他格式暂不支持,请注意 当前的音频格式支持 MP3,WAV, AAC(m4a后缀), 采样率为44100,
 * 通道数为2,其他格式暂不支持,请注意 当前的音频格式支持 MP3,WAV, AAC(m4a后缀), 采样率为44100,
 * 通道数为2,其他格式暂不支持,请注意
 */
public class AudioPadExecute {

    private static final String TAG = "AudioPadExecute";
    // //举例3: 先设置AudioPad的总长度, 然后在不同的时间点增加几段声音.处理成同一个.
    static AudioSource audioSrc1;
    static long starttime = 0;
    AudioPad audioPad;

    public AudioPadExecute(Context ctx, IAudioSourceInput input) {
        audioPad = new AudioPad(ctx, input);
    }
    /**
     * 构造方法,
     *
     * @param ctx
     * @param dstPath 因编码后的为aac格式, 故此路径的文件后缀需m4a或aac; 比如 "/sdcard/testAudioPad.m4a"
     */
    public AudioPadExecute(Context ctx, String dstPath) {
        audioPad = new AudioPad(ctx, dstPath);
    }

    /**
     * AudioPad容器处理两种情况的声音: 1, 一整段音乐上增加别的音频, (这里是第一种); 2,设置容器处理的声音总长度,
     * 然后分别增加不同声音段; 这里是第一种;
     * <p>
     * 增加后, AudioPad会以音频的总长度为pad的长度, 其他增加的音频则是和这个音频的某一段混合.
     * <p>
     * 返回的AudioSource
     *
     * @param mainAudio 音频文件路径, 可以是有音频的视频路径;
     * @return 返回增加好的这个音频的对象, 可以根据这个来实时调节音量, 禁止声音等
     */
    public AudioSource setAudioPadSource(String mainAudio) {
        if (audioPad != null) {
            return audioPad.addMainAudio(mainAudio);
        } else {
            return null;
        }
    }

    /**
     * AudioPad容器处理两种情况的声音: 1, 一整段音乐上增加别的音频 2,设置容器处理的声音总长度, 然后分别增加不同声音段; 这里是第二种;
     * <p>
     * 设置 音频处理的总长度.单位秒. 开始线程前调用.
     * <p>
     * 如果您只想在 一整段音乐上增加别的音频,可以用{@link #setAudioPadSource(String)}
     *
     * @return
     */
    public AudioSource setAudioPadLength(float duration) {
        if (audioPad != null) {
            return audioPad.addMainAudio(duration);
        } else {
            return null;
        }
    }

    /**
     * 增加一个音频,
     *
     * @param srcPath
     * @return 返回音频对象.可以设置音量, 是否循环等;
     */
    public AudioSource addSubAudio(String srcPath) {
        if (audioPad != null) {
            return audioPad.addSubAudio(srcPath);
        } else {
            return null;
        }
    }

    /**
     * 把音频的 指定时间段, 增加到audiopad音频容器里.
     * <p>
     * 如果有循环或其他操作, 可以在获取的AudioSource对象中设置.
     *
     * @param srcPath      音频文件路径, 可以是有音频的视频路径;
     * @param startPadUs   从容器的什么时间开始增加.
     * @param startAudioUs 该音频的开始时间
     * @param endAudioUs   该音频的结束时间. 如果要增加到文件尾,则可以直接填入-1;
     * @return
     */
    public AudioSource addSubAudio(String srcPath, long startPadUs,
                                   long startAudioUs, long endAudioUs) {
        if (audioPad != null) {
            return audioPad.addSubAudio(srcPath, startPadUs, startAudioUs,
                    endAudioUs);
        } else {
            return null;
        }
    }

    /**
     * 设置监听当前audioPad的处理进度.
     * 此监听是通过handler机制,传递到UI线程的, 你可以在里面增加ui的代码. 因为经过了handler机制,
     * 可能会进度比正在处理延迟一些,不完全等于当前处理的帧时间戳.
     *
     * @param listener
     */
    public void setAudioPadProgressListener(onAudioPadProgressListener listener) {
        if (audioPad != null) {
            audioPad.setAudioPadProgressListener(listener);
        }
    }

    /**
     * 设置监听当前audioPad的处理进度. 一个音频帧处理完毕后, 直接执行您listener中的代码.
     * 在audioPad线程中执行,不能在里面增加UI代码.
     * <p>
     * 建议使用这个.
     * <p>
     * 如果您声音在40s一下,建议使用这个, 因为音频本身很短,处理时间很快.
     *
     * @param listener
     */
    public void setAudioPadThreadProgressListener(
            onAudioPadThreadProgressListener listener) {
        if (audioPad != null) {
            audioPad.setAudioPadThreadProgressListener(listener);
        }
    }

    /**
     * 完成监听. 经过handler传递到主线程, 可以在里面增加UI代码.
     *
     * @param listener
     */
    public void setAudioPadCompletedListener(
            onAudioPadCompletedListener listener) {
        if (audioPad != null) {
            audioPad.setAudioPadCompletedListener(listener);
        }
    }

    /**
     * 开启另一个线程, 并开始音频处理
     *
     * @return
     */
    public boolean start() {
        if (audioPad != null) {
            return audioPad.start();
        } else {
            return false;
        }
    }

    /**
     * 等待执行完毕;适合在音频较短,为了代码的整洁, 不想设置listener回调的场合; 注意:这里设置后,
     * 当前线程将停止在这个方法处,直到音频执行完毕退出为止.建议放到另一个线程中执行. 可选使用.
     */
    public void waitComplete() {
        if (audioPad != null) {
            audioPad.joinSampleEnd();
        }
    }

    /**
     * 停止当前audioPad的处理;
     */
    public void stop() {
        if (audioPad != null) {
            audioPad.stop();
        }
    }

    /**
     * 释放AudioPad容器;
     */
    public void release() {
        if (audioPad != null) {
            audioPad.release();
            audioPad = null;
        }
    }

    // ----------------------------一下为测试代码-------------------------------------------
    // 向一段音频中,增加另一段音频.
    public static void test1(Context ctx) {
        AudioPadExecute audioPad = new AudioPadExecute(ctx, "/sdcard/i19.m4a");
        AudioSource asource = audioPad
                .setAudioPadSource("/sdcard/audioPadTest/niu30s_44100_2.m4a");

        asource.setVolume(0.2f);

        AudioSource asource2 = audioPad.addSubAudio(
                "/sdcard/audioPadTest/hongdou10s_44100_2.mp3", 3 * 1000 * 1000,
                0, -1); // 中间3秒增加一段

        asource2.setVolume(2.5f);

        audioPad.start();
        audioPad.waitComplete();
        audioPad.release();
    }

    // 举例2:创建一段静音的音频.
    public static void test2(Context ctx) {
        starttime = System.currentTimeMillis();
        AudioPadExecute audioPad = new AudioPadExecute(ctx, "/sdcard/i6.m4a");
        audioPad.setAudioPadLength(60.0f);
        audioPad.start();
        audioPad.waitComplete();
        audioPad.release();
        Log.i(TAG, "已经执行完毕了....耗时:" + (System.currentTimeMillis() - starttime));
    }

    public static void test3(Context ctx) {
        AudioPadExecute audioPad = new AudioPadExecute(ctx, "/sdcard/i8.m4a");

        audioPad.setAudioPadLength(60.0f); // 定义生成一段15秒的声音./或者你可以把某一个音频作为一个主音频

        audioPad.addSubAudio("/sdcard/audioPadTest/du15s_44100_2.mp3", 0, 0,
                3 * 1000 * 1000); // 在这15内, 的前3秒增加一个声音


        audioSrc1 = audioPad.addSubAudio(
                "/sdcard/audioPadTest/hongdou10s_44100_2.mp3", 3 * 1000 * 1000,
                0, 3 * 1000 * 1000); // 中间3秒增加一段
        audioPad.addSubAudio("/sdcard/audioPadTest/niu30s_44100_2.m4a",
                10 * 1000 * 1000, 0, -1); // 最后3秒增加一段.

        audioPad.setAudioPadCompletedListener(new onAudioPadCompletedListener() {

            @Override
            public void onCompleted(AudioPad v) {
                Log.i(TAG, "已经执行完毕了....耗时:"
                        + (System.currentTimeMillis() - starttime));

                v.release(); // 释放(内部会检测是否执行完, 如没有,则等待执行完毕).
                MediaPlayer player = new MediaPlayer(); // 测试后, 开始播放.
                try {
                    player.setDataSource("/sdcard/i8.m4a");
                    player.prepare();
                    player.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        audioPad.setAudioPadProgressListener(new onAudioPadProgressListener() {

            @Override
            public void onProgress(AudioPad v, long currentTimeUs) {
                // Log.i(TAG,"当前progess的进度是:"+currentTimeUs);
            }
        });
        audioPad.setAudioPadThreadProgressListener(new onAudioPadThreadProgressListener() {

            @Override
            public void onProgress(AudioPad v, long currentTimeUs) {
                // Log.i(TAG,"当前Thread progess的进度是:"+currentTimeUs);
                if (audioSrc1 != null) {

                    if (currentTimeUs > 5000 * 1000) {
                        audioSrc1.setVolume(0.2f);
                    } else if (currentTimeUs > 3500 * 1000) {
                        audioSrc1.setVolume(3.0f);
                    }
                }
            }
        });
        starttime = System.currentTimeMillis();
        audioPad.start(); // 开始运行 ,另开一个线程,异步执行.
    }

    /**
     * 举例4:时间拉伸演示
     *
     * @param ctx
     */
    public static void test4(Context ctx) {
        AudioPadExecute audioPad = new AudioPadExecute(ctx, "/sdcard/i10.m4a");
        AudioSource source = audioPad
                .setAudioPadSource("/sdcard/audioPadTest/niu30s_44100_2.m4a");
        // 增加一个时间拉伸, 声音快慢播放, 用拉伸请形象一些; 请注意一定要在开始前调用;请注意一定要在开始前调用;请注意一定要在开始前调用
        // 参数分别是:
        // 1,拉伸系数, 最小是0.5倍;最大是2.0倍; 1.0f为默认值.
        // 2,原音频的开始时间:单位微秒;
        // 3,原音频的结束时间:单位微秒;
        source.addTimeStretch(5 * 1000 * 1000, 10 * 1000 * 1000, 0.5f);
        source.addTimeStretch(15 * 1000 * 1000, 20 * 1000 * 1000, 1.5f);
        source.addTimeStretch(23 * 1000 * 1000, 27 * 1000 * 1000, 2.0f);

        AudioSource audioSource = audioPad.addSubAudio(
                "/sdcard/audioPadTest/hongdou10s_44100_2.mp3", 3 * 1000 * 1000,
                0, 6 * 1000 * 1000); // 中间3秒增加一段
        audioSource.setVolume(6.0f);

        audioPad.start();
        audioPad.waitComplete();
        audioPad.release();
    }


}
