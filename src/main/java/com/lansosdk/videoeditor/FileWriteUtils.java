package com.lansosdk.videoeditor;

import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class FileWriteUtils {
    private final String TAG = "FileWriteUtils";
    private final boolean VERBOSE = false;
    BufferedOutputStream bos = null;
    FileOutputStream fos = null;
    private String mSavePath = null;

    public FileWriteUtils(String str) {
        openWriteFile(str);
    }

    public static void saveData(int[] iArr, String str) {
        FileWriteUtils fileWriteUtils = new FileWriteUtils(str);
        fileWriteUtils.writeFile(IntBuffer.wrap(iArr));
        fileWriteUtils.closeWriteFile();
    }

    public void closeWriteFile() {
        try {
            if (this.bos != null) {
                this.bos.close();
            }
            if (this.fos != null) {
                this.fos.close();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public boolean openWriteFile(String str) {
        if (this.mSavePath == null) {
            this.mSavePath = str;
            if (str != null) {
                try {
                    this.fos = new FileOutputStream(new File(str));
                    this.bos = new BufferedOutputStream(this.fos);
                    this.fos = null;
                    return true;
                } catch (IOException e) {
                    StringBuilder stringBuilder = new StringBuilder("video encoder cannot open write file: ");
                    stringBuilder.append(e.toString());
                    Log.e("FileWriteUtils", stringBuilder.toString());
                }
            }
        }
        return false;
    }

    public void writeFile(ByteBuffer byteBuffer) {
        byte[] bArr = new byte[byteBuffer.remaining()];
        StringBuilder stringBuilder = new StringBuilder("writeFile to ");
        stringBuilder.append(this.mSavePath);
        stringBuilder.append(" size:");
        stringBuilder.append(bArr.length);
        Log.d("FileWriteUtils", stringBuilder.toString());
        if (this.bos != null) {
            byteBuffer.get(bArr);
            try {
                this.bos.write(bArr);
                return;
            } catch (IOException e) {
                Log.e("FileWriteUtils", e.toString());
                return;
            }
        }
        Log.e("FileWriteUtils", "videoencoder write file error bos is null");
    }

    public void writeFile(IntBuffer intBuffer) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(intBuffer.remaining() << 2);
        allocateDirect.asIntBuffer().put(intBuffer);
        writeFile(allocateDirect);
    }

    public void writeFile(byte[] bArr) {
        if (this.bos != null) {
            try {
                this.bos.write(bArr);
                return;
            } catch (IOException e) {
                Log.e("FileWriteUtils", e.toString());
                return;
            }
        }
        Log.e("FileWriteUtils", "videoencoder write file error bos is null");
    }
}
