package com.lansosdk.videoeditor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DemoUtil {
    static int bmtcnt;
    private List<Bitmap> bmpLists = new ArrayList();
    private int interval = 5;

    /* renamed from: com.lansosdk.videoeditor.DemoUtil$1 */
    static class DemoClick implements OnClickListener {
        DemoClick() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
        }
    }

    /* renamed from: com.lansosdk.videoeditor.DemoUtil$2 */
    static class DemoClick2 implements OnClickListener {
        DemoClick2() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
        }
    }

    public static void deleteAll() {
        deleteDir(new File("/sdcard/extract/"));
    }

    static boolean deleteDir(File file) {
        if (file.isDirectory()) {
            String[] list = file.list();
            for (String file2 : list) {
                if (!deleteDir(new File(file, file2))) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    public static void savePng(Bitmap bitmap) {
        if (bitmap != null) {
            File file = new File("/sdcard/extractf/");
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                StringBuilder stringBuilder = new StringBuilder("/sdcard/extractf/tt");
                int i = bmtcnt;
                bmtcnt = i + 1;
                stringBuilder.append(i);
                stringBuilder.append(".png");
                String stringBuilder2 = stringBuilder.toString();
                StringBuilder stringBuilder3 = new StringBuilder("name:");
                stringBuilder3.append(stringBuilder2);
                Log.i("savePng", stringBuilder3.toString());
                OutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(stringBuilder2));
                bitmap.compress(CompressFormat.PNG, 90, bufferedOutputStream);
                bufferedOutputStream.close();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        Log.i("savePng", "error  bmp  is null");
    }

    public static void showHintDialog(Activity activity, int i) {
        new Builder(activity).setTitle("提示").setMessage(i).setPositiveButton("确定", new DemoClick()).show();
    }

    public static void showHintDialog(Activity activity, String str) {
        new Builder(activity).setTitle("提示").setMessage(str).setPositiveButton("确定", new DemoClick2()).show();
    }

    @SuppressLint("WrongConstant")
    public static void showToast(Context context, String str) {
        Toast.makeText(context, str, 0).show();
        Log.i("x", str);
    }

    public void pushBitmap(Bitmap bitmap) {
        this.interval++;
        if (this.bmpLists.size() >= 5 || bitmap == null || this.interval % 5 != 0) {
            Log.i("T", " size >20; push error!");
        } else {
            this.bmpLists.add(bitmap);
        }
    }

    public void saveToSdcard() {
        for (Bitmap savePng : this.bmpLists) {
            savePng(savePng);
        }
    }
}
