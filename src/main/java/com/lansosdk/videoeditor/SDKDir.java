package com.lansosdk.videoeditor;

import java.io.File;

public class SDKDir {

    // Environment.getExternalStorageDirectory()
    // FileWriter out = new FileWriter(new
    // File(Environment.getExternalStorageDirectory(), "content.txt"));
    public static String TMP_DIR = "/sdcard/lansongBox/";

    public static String getPath() {
        File file = new File(TMP_DIR);
        if (file.exists() == false) {
            file.mkdirs();
        }
        return TMP_DIR;
    }
}
