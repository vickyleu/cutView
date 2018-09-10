package com.lansosdk.videoeditor;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;

public class BitmapUtil {

    public static void savePng(Bitmap bmp,int index) {
        if (bmp != null) {
            File dir = new File("/sdcard/extractd/");
            if (dir.exists() == false) {
                dir.mkdirs();
            }
            try {
                BufferedOutputStream bos;
                String name = String.format("/sdcard/extractd/cc_%05d.png",index);
                Log.i("savePng", "path:" + name);
                bos = new BufferedOutputStream(new FileOutputStream(name));
                bmp.compress(Bitmap.CompressFormat.PNG, 90, bos);
                bos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Log.i("savePng", "error  bmp  is null");
        }
    }
    public static Bitmap intBufferToBmp(IntBuffer buffer,int w,int h){
        Bitmap stitchBmp = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        stitchBmp.copyPixelsFromBuffer(buffer);
        return stitchBmp;
    }
}
