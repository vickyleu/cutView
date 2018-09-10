package com.lansosdk.videoeditor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

/**
 * 封装一下 编辑模式, 在转换的时候, 有提示;
 */
public class EditModeVideoDialog {

    private final static String TAG = "EditModeVideoDialog";
    private final static boolean VERBOSE = false;

    String srcVideo = null;

    VideoEditor mEditor = null;
    ProgressDialog mProgressDialog;
    private String dstVideo = null;
    private TextView tvHint;
    private Activity mActivity;
    private boolean isRunning;


    /**
     * 把视频转换为编辑模式的视频, 有
     * @param activity
     * @param src  输入的视频.
     * @param tvHint  转换后设置到 TextView上;
     */
    public EditModeVideoDialog(Activity activity, String src, TextView tvHint) {
        mActivity = activity;
        this.tvHint = tvHint;
        srcVideo = src;
        mEditor = new VideoEditor();
        mEditor.setOnProgessListener(new onVideoEditorProgressListener() {

            @Override
            public void onProgress(VideoEditor v, int percent) {
                if (mProgressDialog != null) {
                    mProgressDialog.setMessage("正在转换为编辑模式..." + String.valueOf(percent) + "%");
                }
            }
        });


    }

    public void start() {
        if (isRunning == false) {
            new SubAsyncTask().execute();
        }
    }

    public void release() {
        if (isRunning) {
            mEditor.cancel();
            isRunning=false;
        }
        calcelProgressDialog();
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("正在转换为编辑模式...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void calcelProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

    private class SubAsyncTask extends AsyncTask<Object, Object, Boolean> {
        @Override
        protected void onPreExecute() {
            showProgressDialog();
            isRunning = true;
            super.onPreExecute();
        }

        @Override
        protected synchronized Boolean doInBackground(Object... params) {
                MediaInfo info = new MediaInfo(srcVideo, false);
                if(info.prepare()){
                    dstVideo = SDKFileUtils.newMp4PathInBox();
                    mEditor.convertToEditMode(srcVideo,dstVideo);
                }
                return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (isRunning) {
                calcelProgressDialog();
                isRunning = false;
                if (tvHint != null) {
                    tvHint.setText(dstVideo);
                }
            }
        }
    }
}
