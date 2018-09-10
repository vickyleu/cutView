package com.example.lansongcommonui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lansongcommonui.view.FocusImageView;
import com.example.lansongcommonui.view.MyVideoView;
import com.example.lansongcommonui.view.RecordedButton;
import com.example.lansongcommonui.view.RecordedButton.OnGestureListener;
import com.lansongsdk.commonFree.R;
import com.lansosdk.box.onDrawPadErrorListener;
import com.lansosdk.box.onDrawPadProgressListener;
import com.lansosdk.box.onDrawPadSnapShotListener;
import com.lansosdk.videoeditor.DemoUtil;
import com.lansosdk.videoeditor.DrawPadCameraView;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.LanSongUtil;
import com.lansosdk.videoeditor.MediaInfo;
import com.lansosdk.videoeditor.doFousEventListener;
import com.lansosdk.videoeditor.onViewAvailable;

@SuppressLint("WrongConstant")
public class CameraRecordActivity extends Activity implements OnClickListener {
    private static final String TAG = "CameraFullRecord";
    private float beautyLevel = 0.0f;
    private RecordedButton btnRecord;
    private String dstPath;
    private FocusImageView focusImageView;
    private ImageView ivChangeFlash;
    private ImageView ivPhoto;
    private RelativeLayout layoutBottom;
    private RelativeLayout layoutRecord;
    private RelativeLayout layoutTop;
    private Context mContext = null;
    private WakeLock mWakeLock;
    private int maxDuration = 10000000;
    private String recordVideo;
    private TextView tvBeautyHint;
    private TextView tvHint;
    private DrawPadCameraView viewCamera;
    private MyVideoView viewVideoPlay;

    /* renamed from: com.example.lansongcommonui.CameraRecordActivity$10 */
    class C024410 implements OnClickListener {
        C024410() {
        }

        public void onClick(View view) {
            CameraRecordActivity.this.viewCamera.downBrightness();
        }
    }

    /* renamed from: com.example.lansongcommonui.CameraRecordActivity$7 */
    class C02457 implements OnPreparedListener {
        C02457() {
        }

        public void onPrepared(MediaPlayer mediaPlayer) {
            CameraRecordActivity.this.viewVideoPlay.setLooping(true);
            CameraRecordActivity.this.viewVideoPlay.start();
        }
    }

    /* renamed from: com.example.lansongcommonui.CameraRecordActivity$8 */
    class C02468 implements OnClickListener {
        C02468() {
        }

        public void onClick(View view) {
            if (CameraRecordActivity.this.beautyLevel == 0.0f) {
                CameraRecordActivity.this.viewCamera.enableBeauty();
                CameraRecordActivity.this.beautyLevel = CameraRecordActivity.this.beautyLevel + 0.22f;
                CameraRecordActivity.this.tvBeautyHint.setText("美颜打开");
                return;
            }
            CameraRecordActivity.this.beautyLevel = CameraRecordActivity.this.beautyLevel + 0.1f;
            CameraRecordActivity.this.viewCamera.setBeautyWarmCool(CameraRecordActivity.this.beautyLevel);
            if (CameraRecordActivity.this.beautyLevel >= 1.0f) {
                CameraRecordActivity.this.viewCamera.disableBeauty();
                CameraRecordActivity.this.beautyLevel = 0.0f;
                CameraRecordActivity.this.tvBeautyHint.setText("美颜关闭");
                return;
            }
            float access$800 = ((float) ((int) (CameraRecordActivity.this.beautyLevel * 100.0f))) / 100.0f;
            TextView access$900 = CameraRecordActivity.this.tvBeautyHint;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("美颜等级:");
            stringBuilder.append(access$800);
            access$900.setText(stringBuilder.toString());
        }
    }

    /* renamed from: com.example.lansongcommonui.CameraRecordActivity$9 */
    class C02479 implements OnClickListener {
        C02479() {
        }

        public void onClick(View view) {
            CameraRecordActivity.this.viewCamera.upBrightness();
        }
    }

    /* renamed from: com.example.lansongcommonui.CameraRecordActivity$1 */
    class C02481 implements onViewAvailable {
        C02481() {
        }

        public void viewAvailable(DrawPadCameraView drawPadCameraView) {
            CameraRecordActivity.this.startDrawPad();
        }
    }

    /* renamed from: com.example.lansongcommonui.CameraRecordActivity$2 */
    class C02492 implements onDrawPadErrorListener {
        C02492() {
        }

        public void onError(int i) {
            String str = CameraRecordActivity.TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DrawPad容器线程运行出错!!!");
            stringBuilder.append(i);
            Log.e(str, stringBuilder.toString());
        }
    }

    /* renamed from: com.example.lansongcommonui.CameraRecordActivity$3 */
    class C02503 implements onDrawPadProgressListener {
        C02503() {
        }

        public void onProgress(long j) {
            CameraRecordActivity.this.btnRecord.setProgress(j);
        }
    }

    /* renamed from: com.example.lansongcommonui.CameraRecordActivity$4 */
    class C02514 implements onDrawPadSnapShotListener {
        C02514() {
        }

        public void onSnapShot(Bitmap bitmap) {
            String str = CameraRecordActivity.TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("得到图片");
            stringBuilder.append(bitmap.getHeight());
            stringBuilder.append(bitmap.getWidth());
            Log.i(str, stringBuilder.toString());
        }
    }

    /* renamed from: com.example.lansongcommonui.CameraRecordActivity$5 */
    class C02525 implements doFousEventListener {
        C02525() {
        }

        public void onFocus(int i, int i2) {
            if (CameraRecordActivity.this.focusImageView != null && CameraRecordActivity.this.focusImageView.getVisibility() == View.VISIBLE) {
                CameraRecordActivity.this.focusImageView.startFocus(i, i2);
            }
        }
    }

    /* renamed from: com.example.lansongcommonui.CameraRecordActivity$6 */
    class C02536 implements OnGestureListener {
        C02536() {
        }

        public void onClick() {
            CameraRecordActivity.this.viewCamera.toggleSnatShot();
        }

        public void onLongClickDown() {
            CameraRecordActivity.this.viewCamera.segmentStart();
            CameraRecordActivity.this.btnRecord.setSplit();
        }

        public void onLongClickUp() {
            CameraRecordActivity.this.recordVideo = CameraRecordActivity.this.viewCamera.segmentStop();
            if (MediaInfo.isSupport(CameraRecordActivity.this.recordVideo)) {
                CameraRecordActivity.this.playRecordVideo();
                return;
            }
            DemoUtil.showHintDialog(CameraRecordActivity.this, "时间太短,创建失败");
            CameraRecordActivity.this.showCameraView();
        }

        public void onReachMax() {
            CameraRecordActivity.this.btnRecord.closeButton();
            CameraRecordActivity.this.recordVideo = CameraRecordActivity.this.viewCamera.segmentStop();
            if (MediaInfo.isSupport(CameraRecordActivity.this.recordVideo)) {
                CameraRecordActivity.this.playRecordVideo();
            }
        }
    }

    @SuppressLint("WrongConstant")
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LanSongUtil.hideBottomUIMenu(this);
        this.mContext = getApplicationContext();
        if (!LanSongUtil.checkRecordPermission(getBaseContext())) {
            Toast.makeText(getApplicationContext(), "当前无权限,请打开权限后,重试!!!", 1).show();
            finish();
        }
        setContentView(R.layout.camera_full_record_layout);
        this.viewCamera = (DrawPadCameraView) findViewById(R.id.id_fullrecord_padview);
        this.focusImageView = (FocusImageView) findViewById(R.id.id_fullrecord_focusview);
        initUI();
        initCamera();
        initBeautyView();
    }

    protected void onResume() {
        LanSongUtil.hideBottomUIMenu(this);
        super.onResume();
        if (this.mWakeLock == null) {
            this.mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(10, TAG);
            this.mWakeLock.acquire();
        }
        startDrawPad();
    }

    private void initCamera() {
        this.viewCamera.setOnViewAvailable(new C02481());
        this.viewCamera.setOnDrawPadErrorListener(new C02492());
        this.viewCamera.setOnDrawPadProgressListener(new C02503());
        this.viewCamera.setOnDrawPadSnapShotListener(new C02514());
        this.viewCamera.setCameraFocusListener(new C02525());
    }

    private void startDrawPad() {
        this.viewCamera.setEncodeParams(544, 960, 3072000, 25);
        if (LanSongUtil.isFullScreenRatio(this.viewCamera.getViewWidth(), this.viewCamera.getViewHeight())) {
            this.viewCamera.setEncodeParams(544, 1088, 3584000, 25);
        }
        if (this.viewCamera.setupDrawpad()) {
            this.viewCamera.startPreview();
        } else {
            Log.i(TAG, "建立drawpad线程失败.");
        }
    }

    private void stopDrawPad() {
        if (this.viewCamera != null && this.viewCamera.isRunning()) {
            this.viewCamera.stopDrawPad();
        }
    }

    protected void onPause() {
        super.onPause();
        if (this.viewCamera != null) {
            this.viewCamera.stopDrawPad();
        }
        if (this.mWakeLock != null) {
            this.mWakeLock.release();
            this.mWakeLock = null;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        stopDrawPad();
        this.viewVideoPlay.release();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                return;
            case R.id.iv_change_camera:
                this.viewCamera.changeCamera();
                return;
            case R.id.iv_change_flash:
                if (this.viewCamera.changeFlash()) {
                    this.ivChangeFlash.setImageResource(R.mipmap.video_flash_open);
                    return;
                } else {
                    this.ivChangeFlash.setImageResource(R.mipmap.video_flash_close);
                    return;
                }
            case R.id.iv_recorddelete:
                showCameraView();
                return;
            case R.id.iv_recordfinish:
                EditFile.getInstance().initEditVideo(this.recordVideo);
                startActivity(new Intent(this, EditVideoActivity.class));
                finish();
                return;
            default:
                return;
        }
    }


    private void initUI() {
        this.btnRecord = (RecordedButton) findViewById(R.id.rb_start);
        this.viewVideoPlay = (MyVideoView) findViewById(R.id.vv_play);
        this.ivPhoto = (ImageView) findViewById(R.id.iv_photo);
        this.tvHint = (TextView) findViewById(R.id.tv_hint);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_recorddelete).setOnClickListener(this);
        findViewById(R.id.iv_recordfinish).setOnClickListener(this);
        this.ivChangeFlash = (ImageView) findViewById(R.id.iv_change_flash);
        this.ivChangeFlash.setOnClickListener(this);
        findViewById(R.id.iv_change_camera).setOnClickListener(this);
        findViewById(R.id.iv_photo).setOnClickListener(this);
        this.layoutRecord = (RelativeLayout) findViewById(R.id.id_record_layout);
        this.layoutBottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        this.layoutTop = (RelativeLayout) findViewById(R.id.rl_top);
        this.viewVideoPlay.setVisibility(8);
        this.ivPhoto.setVisibility(8);
        this.layoutTop.setVisibility(0);
        this.btnRecord.setVisibility(0);
        this.tvHint.setVisibility(0);
        this.layoutBottom.setVisibility(8);
        this.btnRecord.cleanSplit();
        this.btnRecord.setMax((long) this.maxDuration);
        this.btnRecord.setOnGestureListener(new C02536());
    }

    private void playRecordVideo() {
        this.layoutRecord.setVisibility(8);
        this.tvHint.setVisibility(8);
        this.layoutTop.setVisibility(8);
        this.focusImageView.setVisibility(8);
        this.layoutBottom.setVisibility(0);
        this.btnRecord.cleanSplit();
        findViewById(R.id.id_layout_videophoto).setVisibility(0);
        this.viewVideoPlay.setVisibility(0);
        this.viewVideoPlay.setVideoPath(this.recordVideo);
        this.viewVideoPlay.setOnPreparedListener(new C02457());
    }

    private void showCameraView() {
        this.layoutRecord.setVisibility(0);
        this.tvHint.setVisibility(0);
        this.layoutTop.setVisibility(0);
        this.focusImageView.setVisibility(0);
        this.layoutBottom.setVisibility(8);
        findViewById(R.id.id_layout_videophoto).setVisibility(8);
        this.btnRecord.cleanSplit();
        this.viewVideoPlay.stop();
        this.viewVideoPlay.release();
        this.viewVideoPlay.setVisibility(8);
        LanSongFileUtil.deleteFile(this.recordVideo);
        this.recordVideo = null;
    }

    private void initBeautyView() {
        this.tvBeautyHint = (TextView) findViewById(R.id.id_beauty_hint);
        findViewById(R.id.id_camerabeauty_btn).setOnClickListener(new C02468());
        findViewById(R.id.id_camerabeauty_brightadd_btn).setOnClickListener(new C02479());
        findViewById(R.id.id_camerabeaty_brightsub_btn).setOnClickListener(new C024410());
    }
}
