package com.lansosdk.videoeditor;

import android.text.TextUtils;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Calendar;

public class LanSongFileUtil {
    public static final String TAG = "LanSongFileUtil";
    public static String TMP_DIR = "/sdcard/lansongBox/";
    public static final boolean VERBOSE = false;

    public static boolean close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
                return true;
            } catch (IOException unused) {
                return false;
            }
        }
    }

    public static String copyFile(String str, String str2) {
        str2 = createFile(TMP_DIR, str2);
        File file = new File(str);
        File file2 = new File(str2);
        copyFile(file, file2);
        if (file.length() == file2.length()) {
            return str2;
        }
        String str3 = TAG;
        StringBuilder stringBuilder = new StringBuilder("fileCopy is failed! ");
        stringBuilder.append(str);
        stringBuilder.append(" src size:");
        stringBuilder.append(file.length());
        stringBuilder.append(" dst size:");
        stringBuilder.append(file2.length());
        Log.e(str3, stringBuilder.toString());
        deleteFile(str2);
        return null;
    }

    public static void copyFile(InputStream inputStream, OutputStream outputStream) {
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr);
            if (read != -1) {
                outputStream.write(bArr, 0, read);
            } else {
                return;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0058 A:{Splitter: B:13:0x0042, ExcHandler: java.io.FileNotFoundException (unused java.io.FileNotFoundException)} */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0065 A:{Splitter: B:8:0x002c, ExcHandler: java.io.FileNotFoundException (unused java.io.FileNotFoundException)} */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0066 A:{PHI: r0 r3 , Splitter: B:10:0x0036, ExcHandler: java.io.FileNotFoundException (unused java.io.FileNotFoundException)} */
    /* JADX WARNING: Missing block: B:23:0x0058, code:
            r0 = r6;
     */
    /* JADX WARNING: Missing block: B:29:0x0065, code:
            r3 = null;
     */
    /* JADX WARNING: Missing block: B:30:0x0066, code:
            close(r3);
            close(r0);
     */
    /* JADX WARNING: Missing block: B:31:0x006c, code:
            return false;
     */
    public static boolean copyFile(File r6, File r7) {
        /*
        r0 = r6.isDirectory();
        r1 = 0;
        r2 = 1;
        if (r0 == 0) goto L_0x0025;
    L_0x0008:
        r6 = r6.listFiles();
        r7.mkdirs();
        r0 = r6.length;
    L_0x0010:
        if (r1 >= r0) goto L_0x006d;
    L_0x0012:
        r3 = r6[r1];
        r4 = new java.io.File;
        r5 = r3.getName();
        r4.<init>(r7, r5);
        r3 = copyFile(r3, r4);
        r2 = r2 & r3;
        r1 = r1 + 1;
        goto L_0x0010;
    L_0x0025:
        r0 = r6.isFile();
        if (r0 == 0) goto L_0x006d;
    L_0x002b:
        r0 = 0;
        r3 = new java.io.BufferedInputStream;	 Catch:{ FileNotFoundException -> 0x0065, FileNotFoundException -> 0x0065, all -> 0x005c }
        r4 = new java.io.FileInputStream;	 Catch:{ FileNotFoundException -> 0x0065, FileNotFoundException -> 0x0065, all -> 0x005c }
        r4.<init>(r6);	 Catch:{ FileNotFoundException -> 0x0065, FileNotFoundException -> 0x0065, all -> 0x005c }
        r3.<init>(r4);	 Catch:{ FileNotFoundException -> 0x0065, FileNotFoundException -> 0x0065, all -> 0x005c }
        r6 = new java.io.BufferedOutputStream;	 Catch:{ FileNotFoundException -> 0x0066, FileNotFoundException -> 0x0066, all -> 0x005a }
        r4 = new java.io.FileOutputStream;	 Catch:{ FileNotFoundException -> 0x0066, FileNotFoundException -> 0x0066, all -> 0x005a }
        r4.<init>(r7);	 Catch:{ FileNotFoundException -> 0x0066, FileNotFoundException -> 0x0066, all -> 0x005a }
        r6.<init>(r4);	 Catch:{ FileNotFoundException -> 0x0066, FileNotFoundException -> 0x0066, all -> 0x005a }
        r7 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r7 = new byte[r7];	 Catch:{ FileNotFoundException -> 0x0058, FileNotFoundException -> 0x0058, all -> 0x0055 }
    L_0x0044:
        r0 = r3.read(r7);	 Catch:{ FileNotFoundException -> 0x0058, FileNotFoundException -> 0x0058, all -> 0x0055 }
        if (r0 <= 0) goto L_0x004e;
    L_0x004a:
        r6.write(r7, r1, r0);	 Catch:{ FileNotFoundException -> 0x0058, FileNotFoundException -> 0x0058, all -> 0x0055 }
        goto L_0x0044;
    L_0x004e:
        close(r3);
        close(r6);
        return r2;
    L_0x0055:
        r7 = move-exception;
        r0 = r6;
        goto L_0x005e;
    L_0x0058:
        r0 = r6;
        goto L_0x0066;
    L_0x005a:
        r7 = move-exception;
        goto L_0x005e;
    L_0x005c:
        r7 = move-exception;
        r3 = r0;
    L_0x005e:
        close(r3);
        close(r0);
        throw r7;
    L_0x0065:
        r3 = r0;
    L_0x0066:
        close(r3);
        close(r0);
        return r1;
    L_0x006d:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lansosdk.videoeditor.LanSongFileUtil.copyFile(java.io.File, java.io.File):boolean");
    }

    public static String createAACFileInBox() {
        return createFile(TMP_DIR, ".aac");
    }

    public static String createFile(String str, String str2) {
        StringBuilder stringBuilder;
        Calendar instance = Calendar.getInstance();
        int i = instance.get(11);
        int i2 = instance.get(12);
        int i3 = instance.get(1);
        int i4 = instance.get(2) + 1;
        int i5 = instance.get(5);
        int i6 = instance.get(13);
        int i7 = instance.get(14);
        i3 -= 2000;
        File file = new File(str);
        if (!file.exists()) {
            file.mkdir();
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append("/");
        str = stringBuilder2.toString();
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append(String.valueOf(i3));
        str = stringBuilder2.toString();
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append(str);
        stringBuilder3.append(String.valueOf(i4));
        str = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(str);
        stringBuilder3.append(String.valueOf(i5));
        str = stringBuilder3.toString();
        StringBuilder stringBuilder4 = new StringBuilder();
        stringBuilder4.append(str);
        stringBuilder4.append(String.valueOf(i));
        str = stringBuilder4.toString();
        StringBuilder stringBuilder5 = new StringBuilder();
        stringBuilder5.append(str);
        stringBuilder5.append(String.valueOf(i2));
        str = stringBuilder5.toString();
        stringBuilder5 = new StringBuilder();
        stringBuilder5.append(str);
        stringBuilder5.append(String.valueOf(i6));
        str = stringBuilder5.toString();
        stringBuilder5 = new StringBuilder();
        stringBuilder5.append(str);
        stringBuilder5.append(String.valueOf(i7));
        str = stringBuilder5.toString();
        if (!str2.startsWith(".")) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(".");
            str = stringBuilder.toString();
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(str2);
        str = stringBuilder.toString();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File file2 = new File(str);
        if (!file2.exists()) {
            try {
                file2.createNewFile();
                return str;
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return str;
    }

    public static String createFileInBox(String str) {
        return createFile(TMP_DIR, str);
    }

    public static String createM4AFileInBox() {
        return createFile(TMP_DIR, ".m4a");
    }

    public static String createMP3FileInBox() {
        return createFile(TMP_DIR, ".mp3");
    }

    public static String createMp4FileInBox() {
        return createFile(TMP_DIR, ".mp4");
    }

    public static boolean deleteDir(File file) {
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

    public static void deleteEmptyDir(String str) {
        PrintStream printStream;
        StringBuilder stringBuilder;
        if (new File(str).delete()) {
            printStream = System.out;
            stringBuilder = new StringBuilder("Successfully deleted empty directory: ");
        } else {
            printStream = System.out;
            stringBuilder = new StringBuilder("Failed to delete empty directory: ");
        }
        stringBuilder.append(str);
        printStream.println(stringBuilder.toString());
    }

    public static void deleteFile(String str) {
        if (str != null) {
            File file = new File(str);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static boolean equalSize(String str, String str2) {
        return new File(str).length() == new File(str2).length();
    }

    public static boolean fileExist(String str) {
        return str != null && new File(str).exists();
    }

    public static boolean filesExist(String[] strArr) {
        for (String fileExist : strArr) {
            if (!fileExist(fileExist)) {
                return false;
            }
        }
        return true;
    }

    public static String getFileNameFromPath(String str) {
        if (str == null) {
            return "";
        }
        int lastIndexOf = str.lastIndexOf(47);
        if (lastIndexOf >= 0) {
            str = str.substring(lastIndexOf + 1);
        }
        return str;
    }

    public static float getFileSize(String str) {
        if (str == null) {
            return 0.0f;
        }
        File file = new File(str);
        return !file.exists() ? 0.0f : ((float) ((int) ((((float) file.length()) / 1048576.0f) * 100.0f))) / 100.0f;
    }

    public static String getFileSuffix(String str) {
        if (str == null) {
            return "";
        }
        int lastIndexOf = str.lastIndexOf(46);
        return lastIndexOf >= 0 ? str.substring(lastIndexOf + 1) : "";
    }

    public static String getParent(String str) {
        if (TextUtils.equals("/", str)) {
            return str;
        }
        if (str.endsWith("/")) {
            str = str.substring(0, str.length() - 1);
        }
        int lastIndexOf = str.lastIndexOf(47);
        if (lastIndexOf > 0) {
            return str.substring(0, lastIndexOf);
        }
        if (lastIndexOf == 0) {
            str = "/";
        }
        return str;
    }

    public static String getPath() {
        File file = new File(TMP_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
        return TMP_DIR;
    }

    public static String newFilePath(String str, String str2) {
        Calendar instance = Calendar.getInstance();
        int i = instance.get(11);
        int i2 = instance.get(12);
        int i3 = instance.get(1);
        int i4 = instance.get(2) + 1;
        int i5 = instance.get(5);
        int i6 = instance.get(13);
        int i7 = instance.get(14);
        i3 -= 2000;
        File file = new File(str);
        if (!file.exists()) {
            file.mkdir();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append("/");
        str = stringBuilder.toString();
        stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(String.valueOf(i3));
        str = stringBuilder.toString();
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append(String.valueOf(i4));
        str = stringBuilder2.toString();
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append(String.valueOf(i5));
        str = stringBuilder2.toString();
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append(str);
        stringBuilder3.append(String.valueOf(i));
        str = stringBuilder3.toString();
        StringBuilder stringBuilder4 = new StringBuilder();
        stringBuilder4.append(str);
        stringBuilder4.append(String.valueOf(i2));
        str = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(str);
        stringBuilder4.append(String.valueOf(i6));
        str = stringBuilder4.toString();
        stringBuilder4 = new StringBuilder();
        stringBuilder4.append(str);
        stringBuilder4.append(String.valueOf(i7));
        str = stringBuilder4.toString();
        StringBuilder stringBuilder5 = new StringBuilder();
        stringBuilder5.append(str);
        stringBuilder5.append(str2);
        str = stringBuilder5.toString();
        try {
            Thread.sleep(1);
            return str;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return str;
        }
    }

    public static String newMp4PathInBox() {
        return newFilePath(TMP_DIR, ".mp4");
    }
}
