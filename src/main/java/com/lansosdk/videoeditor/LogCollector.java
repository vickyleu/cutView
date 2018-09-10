package com.lansosdk.videoeditor;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LogCollector implements Runnable {
    private static final String TAG = "LogCollector";
    private Context context;
    private boolean isRunning;
    String logFilePath;
    private final Object mLock = new Object();
    private volatile boolean mReady = false;
    private Process process;
    private List<ProcessInfo> processInfoList;

    class ProcessInfo {
        public String name;
        public String pid;
        public String ppid;
        public String user;

        ProcessInfo() {
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder("user=");
            stringBuilder.append(this.user);
            stringBuilder.append(" pid=");
            stringBuilder.append(this.pid);
            stringBuilder.append(" ppid=");
            stringBuilder.append(this.ppid);
            stringBuilder.append(" name=");
            stringBuilder.append(this.name);
            return stringBuilder.toString();
        }
    }

    class StreamConsumer extends Thread {
        /* renamed from: is */
        InputStream f532is;
        List list;

        StreamConsumer(InputStream inputStream) {
            this.f532is = inputStream;
        }

        StreamConsumer(InputStream inputStream, List list) {
            this.f532is = inputStream;
            this.list = list;
        }

        public void run() {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.f532is));
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine != null && readLine.contains(LogCollector.this.context.getPackageName())) {
                        if (this.list != null) {
                            this.list.add(readLine);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public LogCollector(Context context) {
        this.context = context;
    }

    private void clearLogCache() throws Exception {
        Throwable e;
        List arrayList = new ArrayList();
        arrayList.add("logcat");
        arrayList.add("-c");
        Process exec;
        try {
            exec = Runtime.getRuntime().exec((String[]) arrayList.toArray(new String[arrayList.size()]));
            try {
                StreamConsumer streamConsumer = new StreamConsumer(exec.getErrorStream());
                StreamConsumer streamConsumer2 = new StreamConsumer(exec.getInputStream());
                streamConsumer.start();
                streamConsumer2.start();
                if (exec.waitFor() != 0) {
                    Log.e(TAG, " clearLogCache proc.waitFor() != 0");
                }
            } catch (Exception e2) {
                e = e2;
                try {
                    Log.e(TAG, "clearLogCache failed", e);
                    exec.destroy();
                } catch (Throwable th) {
                    e = th;
                    try {
                        exec.destroy();
                    } catch (Throwable e3) {
                        Log.e(TAG, "clearLogCache failed", e3);
                    }
                    throw e;
                }
            }
            try {
                exec.destroy();
            } catch (Throwable e32) {
                Log.e(TAG, "clearLogCache failed", e32);
            }
        } catch (IOException e322) {
            e = e322;
            exec = null;
            Log.e(TAG, "clearLogCache failed", e);
            exec.destroy();
        } catch (Throwable e3222) {
            e = e3222;
            exec = null;
            exec.destroy();
            throw new Exception(e);
        }
    }

    private List<String> getAllProcess() throws Exception {
        Throwable e;
        Throwable th;
        List arrayList = new ArrayList();
        Process exec;
        try {
            exec = Runtime.getRuntime().exec("ps");
            try {
                StreamConsumer streamConsumer = new StreamConsumer(exec.getErrorStream());
                StreamConsumer streamConsumer2 = new StreamConsumer(exec.getInputStream(), arrayList);
                streamConsumer.start();
                streamConsumer2.start();
                if (exec.waitFor() != 0) {
                    Log.e(TAG, "getAllProcess proc.waitFor() != 0");
                }
                try {
                    exec.destroy();
                    return arrayList;
                } catch (Throwable e2) {
                    Log.e(TAG, "getAllProcess failed", e2);
                    return arrayList;
                }
            } catch (Exception e3) {
                e = e3;
                try {
                    Log.e(TAG, "getAllProcess failed", e);
                    exec.destroy();
                    return arrayList;
                } catch (Throwable th2) {
                    th = th2;
                    try {
                        exec.destroy();
                    } catch (Throwable e22) {
                        Log.e(TAG, "getAllProcess failed", e22);
                    }
                    throw th;
                }
            }
        } catch (IOException e4) {
            Throwable th3 = e4;
            exec = null;
            e = th3;
            Log.e(TAG, "getAllProcess failed", e);
            exec.destroy();
            return arrayList;
        } catch (Throwable th4) {
            th = th4;
            exec = null;
            exec.destroy();
            throw new Exception(th);
        }
    }

    private String getAppUser(String str, List<ProcessInfo> list) {
        for (ProcessInfo processInfo : list) {
            if (processInfo.name.equals(str)) {
                return processInfo.user;
            }
        }
        return null;
    }

    private List getProcessInfoList(List<String> list) {
        List<ProcessInfo> arrayList = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            String[] split = list.get(i).split(" ");
            List arrayList2 = new ArrayList();
            for (Object obj : split) {
                if (!"".equals(obj)) {
                    arrayList2.add(obj);
                }
            }
            if (arrayList2.size() == 9) {
                ProcessInfo processInfo = new ProcessInfo();
                processInfo.user = (String) arrayList2.get(0);
                processInfo.pid = (String) arrayList2.get(1);
                processInfo.ppid = (String) arrayList2.get(2);
                processInfo.name = (String) arrayList2.get(8);
                arrayList.add(processInfo);
            }
        }
        return arrayList;
    }

    private void killLogcatProc(List<ProcessInfo> list) {
        if (this.process != null) {
            this.process.destroy();
        }
        String appUser = getAppUser(this.context.getPackageName(), list);
        for (ProcessInfo processInfo : list) {
            if (processInfo.name.toLowerCase().equals("logcat") && processInfo.user.equals(appUser)) {
                android.os.Process.killProcess(Integer.parseInt(processInfo.pid));
            }
        }
        if (this.logFilePath != null) {
            LanSongFileUtil.deleteFile(this.logFilePath);
            this.logFilePath = null;
        }
    }

    private void notifyReady() {
        synchronized (this.mLock) {
            this.mReady = true;
            this.mLock.notify();
        }
    }

    private String readFile() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(this.logFilePath), 8192);
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    String str = TAG;
                    StringBuilder stringBuilder2 = new StringBuilder("readFile: str2:");
                    stringBuilder2.append(readLine);
                    Log.e(str, stringBuilder2.toString());
                    stringBuilder.append(readLine);
                    stringBuilder.append("\r\n");
                } else {
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void waitUntilReady() {
        synchronized (this.mLock) {
            try {
                this.mReady = false;
                this.mLock.wait(0);
            } catch (InterruptedException unused) {
            }
        }
    }

    public void createLogCollector() throws IOException {
        List arrayList = new ArrayList();
        arrayList.add("logcat");
        arrayList.add("-f");
        this.logFilePath = LanSongFileUtil.createFileInBox("log");
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder("createLogCollector: LSTODO file:");
        stringBuilder.append(this.logFilePath);
        Log.i(str, stringBuilder.toString());
        arrayList.add(this.logFilePath);
        arrayList.add("-v");
        arrayList.add("time");
        arrayList.add("*:E");
        this.process = Runtime.getRuntime().exec(new String[arrayList.size()]);
    }

    protected void finalize() {
//        super.finalize();
        if (this.process != null) {
            this.process.destroy();
        }
    }

    public synchronized boolean isRunning() {
        return this.isRunning;
    }

    public void run() {
        try {
            this.isRunning = false;
            runEntry();
        } catch (Exception e) {
            e.printStackTrace();
            notifyReady();
        }
    }

    public void runEntry() throws Exception {
        if (LanSongFileUtil.fileExist(this.logFilePath)) {
            LanSongFileUtil.deleteFile(this.logFilePath);
            this.logFilePath = null;
        }
        clearLogCache();
        this.processInfoList = getProcessInfoList(getAllProcess());
        killLogcatProc(this.processInfoList);
        createLogCollector();
        this.isRunning = true;
        notifyReady();


    }

    public synchronized boolean start() {
        if (!this.isRunning) {
            new Thread(this).start();
            waitUntilReady();
        }
        return this.isRunning;
    }

    public synchronized String stop() {
        String appUser;
        if (this.process != null) {
            this.process.destroy();
        }
        if (this.processInfoList != null) {
            appUser = getAppUser(this.context.getPackageName(), this.processInfoList);
            for (ProcessInfo processInfo : this.processInfoList) {
                if (processInfo.name.toLowerCase().equals("logcat") && processInfo.user.equals(appUser)) {
                    android.os.Process.killProcess(Integer.parseInt(processInfo.pid));
                    this.isRunning = false;
                }
            }
        }
        if (this.logFilePath == null) {
            return null;
        }
        appUser = readFile();
        LanSongFileUtil.deleteFile(this.logFilePath);
        this.logFilePath = null;
        this.isRunning = false;
        return appUser;
    }
}
