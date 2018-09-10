package com.lansosdk.videoeditor;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class CopyDefaultVideoAsyncTask extends
        AsyncTask<Object, Object, Boolean> {
    private ProgressDialog mProgressDialog;
    private Context mContext = null;
    private TextView tvHint;
    private String fileName;

    /**
     * @param ctx
     * @param tvhint 拷贝后, 把拷贝到的目标完整路径显示到这个TextView上.
     * @param file   需要拷贝的文件名字.
     */
    public CopyDefaultVideoAsyncTask(Context ctx, TextView tvhint, String file) {
        mContext = ctx;
        tvHint = tvhint;
        fileName = file;
    }

    /**
     * 阻塞执行, 拷贝Assert中的文件.
     *
     * @param ctx
     * @param fileName
     * @return
     */
    public static String copyFile(Context ctx, String fileName) {
        String str = SDKDir.TMP_DIR + fileName;
        if (SDKFileUtils.fileExist(str) == false) {
            copy(ctx, fileName, SDKDir.TMP_DIR, fileName);
        }
        return str;
    }

    private static void copy(Context mContext, String ASSETS_NAME,
                             String savePath, String saveName) {
        String filename = savePath + "/" + saveName;

        File dir = new File(savePath);
        // 如果目录不中存在，创建这个目录

        if (!dir.exists())
            dir.mkdirs();
        try {
            if (!(new File(filename)).exists()) {
                InputStream is = mContext.getResources().getAssets()
                        .open(ASSETS_NAME);
                FileOutputStream fos = new FileOutputStream(filename);
                byte[] buffer = new byte[7168];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("正在拷贝...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected synchronized Boolean doInBackground(Object... params) {
        // TODO Auto-generated method stub

        // copy ping20s.mp4
        String str = SDKDir.TMP_DIR + fileName;
        if (SDKFileUtils.fileExist(str) == false) {
            copy(mContext, fileName, SDKDir.TMP_DIR, fileName);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }

        String str = SDKDir.TMP_DIR + fileName;
        if (SDKFileUtils.fileExist(str)) {
            Toast.makeText(mContext, "默认视频文件拷贝完成.视频样片路径:" + str,
                    Toast.LENGTH_SHORT).show();
            if (tvHint != null)
                tvHint.setText(str);
        } else {
            Toast.makeText(mContext, "抱歉! 默认视频文件拷贝失败,请联系我们:视频样片路径:" + str,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
