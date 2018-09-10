package com.example.lansongcommonui;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lansongcommonui.view.CutView;
import com.example.lansongcommonui.view.MediaRecorderBase;
import com.example.lansongcommonui.view.MyVideoView;
import com.lansongsdk.commonFree.R;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;

public class CropVideoFrameActivity extends BaseActivity implements OnClickListener {
    private CutView cv_video;
    private int dp50;
    private VideoEditor editor;
    private String path;
    private int videoHeight;
    private int videoWidth;
    private MyVideoView vv_play;
    private int windowHeight;
    private int windowWidth;

    /* renamed from: com.example.lansongcommonui.CropVideoFrameActivity$2 */
    class C02542 implements OnPreparedListener {
        C02542() {
        }

        public void onPrepared(MediaPlayer mediaPlayer) {
            CropVideoFrameActivity.this.vv_play.setLooping(true);
            CropVideoFrameActivity.this.vv_play.start();
            CropVideoFrameActivity.this.videoWidth = CropVideoFrameActivity.this.vv_play.getVideoWidth();
            CropVideoFrameActivity.this.videoHeight = CropVideoFrameActivity.this.vv_play.getVideoHeight();
            float ratio = (((float) CropVideoFrameActivity.this.videoWidth) * 1.0f) / ((float) CropVideoFrameActivity.this.videoHeight);
            float vh = (((float) CropVideoFrameActivity.this.videoWidth) * 1.0f) / ((float) MediaRecorderBase.VIDEO_HEIGHT);
//            CropVideoFrameActivity.this.videoHeight;//todo
            int i = MediaRecorderBase.VIDEO_WIDTH;
            LayoutParams layoutParams = CropVideoFrameActivity.this.vv_play.getLayoutParams();
            layoutParams.width = (int) (((float) CropVideoFrameActivity.this.windowWidth) * vh);
            layoutParams.height = (int) (((float) layoutParams.width) / ratio);
            CropVideoFrameActivity.this.vv_play.setLayoutParams(layoutParams);
        }
    }

    /* renamed from: com.example.lansongcommonui.CropVideoFrameActivity$3 */
    class C02553 implements OnLayoutChangeListener {
        C02553() {
        }

        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            CropVideoFrameActivity.this.cv_video.setMargin((float) CropVideoFrameActivity.this.vv_play.getLeft(), (float) CropVideoFrameActivity.this.vv_play.getTop(), (float) (CropVideoFrameActivity.this.windowWidth - CropVideoFrameActivity.this.vv_play.getRight()), (float) ((CropVideoFrameActivity.this.windowHeight - CropVideoFrameActivity.this.vv_play.getBottom()) - CropVideoFrameActivity.this.dp50));
        }
    }

    private class CropAsyncTask extends AsyncTask<Void, Void, String> {
        private CropAsyncTask() {
        }

        /* synthetic */ CropAsyncTask(CropVideoFrameActivity cropVideoFrameActivity, C04061 c04061) {
            this();
        }

        protected void onPreExecute() {
            CropVideoFrameActivity.this.showProgressDialog();
        }

        protected String doInBackground(Void... voidArr) {
            return CropVideoFrameActivity.this.editVideo();
        }

        @SuppressLint("WrongConstant")
        protected void onPostExecute(String str) {
            CropVideoFrameActivity.this.closeProgressDialog();
            if (TextUtils.isEmpty(str)) {
                Toast.makeText(CropVideoFrameActivity.this.getApplicationContext(), "视频编辑失败,请联系我们", 0).show();
                return;
            }
            EditFile.getInstance().saveEditedVideo(str);
            CropVideoFrameActivity.this.finish();
        }
    }

    /* renamed from: com.example.lansongcommonui.CropVideoFrameActivity$1 */
    class C04061 implements onVideoEditorProgressListener {
        C04061() {
        }

        public void onProgress(VideoEditor videoEditor, int i) {
            CropVideoFrameActivity.this.setProgressPercent(i);
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_cut_size);
        this.windowWidth = getWindowManager().getDefaultDisplay().getWidth();
        this.windowHeight = getWindowManager().getDefaultDisplay().getHeight();
        this.dp50 = (int) getResources().getDimension(R.dimen.dp50);
        initUI();
        this.editor = new VideoEditor();
        this.editor.setOnProgessListener(new C04061());
        this.path = getIntent().getStringExtra("path");
        this.vv_play.setVideoPath(this.path);
        this.vv_play.setOnPreparedListener(new C02542());
        this.vv_play.addOnLayoutChangeListener(new C02553());
    }

    private void initUI() {
        this.vv_play = (MyVideoView) findViewById(R.id.vv_play);
        this.cv_video = (CutView) findViewById(R.id.cv_video);
        TextView textView = (TextView) findViewById(R.id.rl_finish);
        ((RelativeLayout) findViewById(R.id.rl_close)).setOnClickListener(this);
        textView.setOnClickListener(this);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rl_close) {
            finish();
        } else if (id == R.id.rl_finish) {
            new CropAsyncTask(this, null).execute(new Void[0]);
        }
    }

    private String editVideo() {
        float[] cutArr = this.cv_video.getCutArr();
        float f = cutArr[0];
        float f2 = cutArr[1];
        float f3 = cutArr[2];
        float f4 = cutArr[3];
        float rectWidth = (float) this.cv_video.getRectWidth();
        f /= rectWidth;
        float rectHeight = (float) this.cv_video.getRectHeight();
        f2 /= rectHeight;
        int i = (int) (((float) this.videoHeight) * ((f4 / rectHeight) - f2));
        int i2 = (int) (f * ((float) this.videoWidth));
        int i3 = (int) (f2 * ((float) this.videoHeight));
        return this.editor.executeCropVideoFrame(this.path, VideoEditor.make16Before((int) (((float) this.videoWidth) * ((f3 / rectWidth) - f))), VideoEditor.make16Before(i), i2, i3);
    }
}
