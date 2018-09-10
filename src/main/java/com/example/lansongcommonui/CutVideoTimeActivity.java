package com.example.lansongcommonui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lansongcommonui.view.MediaRecorderBase;
import com.example.lansongcommonui.view.ThumbnailView;
import com.example.lansongcommonui.view.ThumbnailView.OnScrollBorderListener;
import com.lansongsdk.commonFree.R;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;

@SuppressLint("WrongConstant")
public class CutVideoTimeActivity extends BaseActivity {
    private static final int GET_FRAME_NUMBER = 15;
    private VideoEditor editor;
    private int endTimeMS = 0;
    private LinearLayout ll_thumbnail;
    private MediaPlayer mediaPlayer;
    private Handler myHandler = new C02596();
    private String path;
    private RelativeLayout rl_close;
    private RelativeLayout rl_video;
    private int startTimeMS = 0;
    private TextureView textureView;
    private ThumbnailView thumbnailView;
    private TextView tv_finish_video;
    private int videoDurationMS;
    private int videoHeight;
    private int videoWidth;
    private int windowHeight;
    private int windowWidth;

    /* renamed from: com.example.lansongcommonui.CutVideoTimeActivity$2 */
    class C02562 implements OnClickListener {
        C02562() {
        }

        public void onClick(View view) {
            CutVideoTimeActivity.this.finish();
        }
    }

    /* renamed from: com.example.lansongcommonui.CutVideoTimeActivity$3 */
    class C02573 implements OnClickListener {
        C02573() {
        }

        public void onClick(View view) {
            if (CutVideoTimeActivity.this.endTimeMS > 0) {
                new CutTimeAsyncTask(CutVideoTimeActivity.this, null).execute(new Void[0]);
            } else {
                CutVideoTimeActivity.this.finish();
            }
        }
    }

    /* renamed from: com.example.lansongcommonui.CutVideoTimeActivity$4 */
    class C02584 implements SurfaceTextureListener {
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        C02584() {
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            CutVideoTimeActivity.this.initMediaPlay(surfaceTexture);
        }
    }

    /* renamed from: com.example.lansongcommonui.CutVideoTimeActivity$6 */
    class C02596 extends Handler {
        C02596() {
        }

        public void handleMessage(Message message) {
            ImageView imageView = (ImageView) CutVideoTimeActivity.this.ll_thumbnail.getChildAt(message.arg1);
            Bitmap bitmap = (Bitmap) message.obj;
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    /* renamed from: com.example.lansongcommonui.CutVideoTimeActivity$7 */
    class C02607 implements OnPreparedListener {
        C02607() {
        }

        public void onPrepared(MediaPlayer mediaPlayer) {
            CutVideoTimeActivity.this.mediaPlayer.start();
            CutVideoTimeActivity.this.videoDurationMS = CutVideoTimeActivity.this.mediaPlayer.getDuration();
            CutVideoTimeActivity.this.videoWidth = CutVideoTimeActivity.this.mediaPlayer.getVideoWidth();
            CutVideoTimeActivity.this.videoHeight = CutVideoTimeActivity.this.mediaPlayer.getVideoHeight();
            CutVideoTimeActivity.this.initVideoSize();
            CutVideoTimeActivity.this.initThumbs();
        }
    }

    private class CutTimeAsyncTask extends AsyncTask<Void, Void, String> {
        private CutTimeAsyncTask() {
        }

        /* synthetic */ CutTimeAsyncTask(CutVideoTimeActivity cutVideoTimeActivity, C04071 c04071) {
            this();
        }

        protected void onPreExecute() {
            CutVideoTimeActivity.this.showProgressDialog().setText("视频剪切中");
        }

        protected String doInBackground(Void... voidArr) {
            return CutVideoTimeActivity.this.editor.executeCutVideoExact(CutVideoTimeActivity.this.path, ((float) CutVideoTimeActivity.this.startTimeMS) / 1000.0f, ((float) (CutVideoTimeActivity.this.endTimeMS - CutVideoTimeActivity.this.startTimeMS)) / 1000.0f);
        }


        protected void onPostExecute(String str) {
            CutVideoTimeActivity.this.closeProgressDialog();
            if (TextUtils.isEmpty(str)) {
                Toast.makeText(CutVideoTimeActivity.this.getApplicationContext(), "视频编辑失败,请联系我们", 0).show();
                return;
            }
            EditFile.getInstance().saveEditedVideo(str);
            CutVideoTimeActivity.this.finish();
        }
    }

    private class GetFrameAsyncTask extends AsyncTask<Void, Void, Boolean> {
        protected void onPostExecute(Boolean bool) {
        }

        private GetFrameAsyncTask() {
        }

        /* synthetic */ GetFrameAsyncTask(CutVideoTimeActivity cutVideoTimeActivity, C04071 c04071) {
            this();
        }

        protected Boolean doInBackground(Void... voidArr) {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(CutVideoTimeActivity.this.mContext, Uri.parse(CutVideoTimeActivity.this.path));
            long access$900 = (long) ((CutVideoTimeActivity.this.videoDurationMS * 1000) / 15);
            for (int i = 0; i < 15; i++) {
                Bitmap frameAtTime = mediaMetadataRetriever.getFrameAtTime(((long) i) * access$900, 2);
                Message obtainMessage = CutVideoTimeActivity.this.myHandler.obtainMessage();
                obtainMessage.obj = frameAtTime;
                obtainMessage.arg1 = i;
                CutVideoTimeActivity.this.myHandler.sendMessage(obtainMessage);
            }
            mediaMetadataRetriever.release();
            return Boolean.valueOf(true);
        }
    }

    /* renamed from: com.example.lansongcommonui.CutVideoTimeActivity$1 */
    class C04071 implements onVideoEditorProgressListener {
        C04071() {
        }

        public void onProgress(VideoEditor videoEditor, int i) {
            CutVideoTimeActivity.this.setProgressPercent(i);
        }
    }

    /* renamed from: com.example.lansongcommonui.CutVideoTimeActivity$5 */
    class C04085 implements OnScrollBorderListener {
        C04085() {
        }

        public void OnScrollBorder(float f, float f2) {
            CutVideoTimeActivity.this.changeTime();
        }

        public void onScrollStateChange() {
            CutVideoTimeActivity.this.changeVideoPlay();
        }
    }

    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_cut_time);
        this.windowWidth = getWindowManager().getDefaultDisplay().getWidth();
        this.windowHeight = getWindowManager().getDefaultDisplay().getHeight();
        this.path = getIntent().getStringExtra("path");
        initUI();
        this.editor = new VideoEditor();
        this.editor.setOnProgessListener(new C04071());
    }

    private void initUI() {
        this.rl_close = (RelativeLayout) findViewById(R.id.rl_close);
        this.tv_finish_video = (TextView) findViewById(R.id.tv_finish_video);
        this.textureView = (TextureView) findViewById(R.id.textureView);
        this.rl_video = (RelativeLayout) findViewById(R.id.rl_video);
        this.ll_thumbnail = (LinearLayout) findViewById(R.id.ll_thumbnail);
        this.thumbnailView = (ThumbnailView) findViewById(R.id.thumbnailView);
        this.rl_close.setOnClickListener(new C02562());
        this.tv_finish_video.setOnClickListener(new C02573());
        this.textureView.setSurfaceTextureListener(new C02584());
        this.thumbnailView.setOnScrollBorderListener(new C04085());
    }

    private void changeTime() {
        this.startTimeMS = (int) (((float) this.videoDurationMS) * (this.thumbnailView.getLeftInterval() / ((float) this.ll_thumbnail.getWidth())));
        this.endTimeMS = (int) (((float) this.videoDurationMS) * (this.thumbnailView.getRightInterval() / ((float) this.ll_thumbnail.getWidth())));
    }

    private void changeVideoPlay() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.seekTo(this.startTimeMS);
        }
    }

    private void initVideoSize() {
        float f = (((float) this.videoWidth) * 1.0f) / ((float) this.videoHeight);
        float f2 = (((float) this.videoWidth) * 1.0f) / ((float) MediaRecorderBase.VIDEO_HEIGHT);
        int i = this.videoHeight;
        i = MediaRecorderBase.VIDEO_WIDTH;
        LayoutParams layoutParams = this.textureView.getLayoutParams();
        layoutParams.width = (int) (((float) this.windowWidth) * f2);
        layoutParams.height = (int) (((float) layoutParams.width) / f);
        this.textureView.setLayoutParams(layoutParams);
        this.thumbnailView.setMinInterval((int) ((500.0f / ((float) this.videoDurationMS)) * ((float) this.thumbnailView.getWidth())));
    }

    private void initThumbs() {
        int width = this.ll_thumbnail.getWidth() / 15;
        for (int i = 0; i < 15; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LayoutParams(width, -1));
            imageView.setBackgroundColor(Color.parseColor("#666666"));
            imageView.setScaleType(ScaleType.CENTER);
            this.ll_thumbnail.addView(imageView);
        }
        new GetFrameAsyncTask(this, null).execute(new Void[0]);
    }

    private void initMediaPlay(SurfaceTexture surfaceTexture) {
        try {
            this.mediaPlayer = new MediaPlayer();
            this.mediaPlayer.setDataSource(this.path);
            this.mediaPlayer.setSurface(new Surface(surfaceTexture));
            this.mediaPlayer.setLooping(true);
            this.mediaPlayer.setOnPreparedListener(new C02607());
            this.mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onPause() {
        super.onPause();
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }
}
