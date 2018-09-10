package com.lansosdk.videoeditor;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.lansosdk.box.LanSoEditorBox;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import jp.co.cyberagent.lansongsdk.gpuimage.LanSongBeautyAdvanceFilter;

public class LanSoEditor {

    private static boolean isLoaded = false;

    public static void initSDK(Context context, String str) {
        loadLibraries(); // 拿出来单独加载库文件.
        LanSoEditor.initSo(context, str);
        checkCPUName();
    }

    private static synchronized void loadLibraries() {
        if (isLoaded)
            return;

        Log.d("lansoeditor", "load libraries.....LanSongffmpeg.");

        System.loadLibrary("LanSongffmpeg");
        System.loadLibrary("LanSongdisplay");
        System.loadLibrary("LanSongplayer");

        isLoaded = true;
    }

    /**
     * 为了统一, 这里请不要调用, 直接调用initSDK即可.
     *
     * @param context
     * @param str
     */
    @Deprecated
    public static void initSo(Context context, String str) {
        nativeInit(context, context.getAssets(), str);
        LanSoEditorBox.init();
    }

    public static void unInitSo() {
        nativeUninit();
    }

    /**
     * 设置默认产生文件的文件夹, 默认是:/sdcard/lansongBox/ 如果您要设置, 则需要改文件夹存在. 比如可以是:
     *
     * @param tmpDir
     */
    public static void setTempFileDir(String tmpDir) {
        LanSoEditorBox.setTempFileDir(tmpDir);
        SDKDir.TMP_DIR = tmpDir;
    }

    /**
     * 获取当前cpu的性能, 我们是根据市面上流行的cpu型号做的一一测试,得到的结果值. 如果返回0,则认为CPU的处理速度还可以.
     * 如果是-1,则一些复杂的, 比如{@link LanSongBeautyAdvanceFilter}这样的操作,
     * 会有点卡顿;比如后台处理可能耗时较长. 如果是-2 则认为cpu性能很低, 基本不能做美颜磨皮操作, 会很卡顿, 后台处理耗时会更长.
     * <p>
     * 可能比较偏门或3年前的cpu没有测试过,请注意.
     *
     * @return
     */
    public static int getCPULevel() {
        return LanSoEditorBox.getCPULevel();
    }

    public static native void nativeInit(Context ctx, AssetManager ass,
                                         String filename);

    public static native void nativeUninit();

    private static void checkCPUName() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            while (str2 != null) {
                if(str2.contains("SDM845")){  //845的平台;
                    VideoEditor.isForceSoftWareEncoder=true;
                }
                str2 = localBufferedReader.readLine();
            }
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
