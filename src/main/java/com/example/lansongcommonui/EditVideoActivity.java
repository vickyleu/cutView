package com.example.lansongcommonui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.internal.view.SupportMenu;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lansongcommonui.view.MediaRecorderBase;
import com.example.lansongcommonui.view.MyVideoView;
import com.example.lansongcommonui.view.MyVideoView.OnPlayStateListener;
import com.example.lansongcommonui.view.TouchView;
import com.example.lansongcommonui.view.TouchView.OnLimitsListener;
import com.example.lansongcommonui.view.TuyaView;
import com.lansongsdk.commonFree.R;
import com.lansosdk.box.MicRecorder;
import com.lansosdk.videoeditor.DemoUtil;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.LanSongMergeAV;
import com.lansosdk.videoeditor.MediaInfo;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@SuppressLint("WrongConstant")
public class EditVideoActivity extends BaseActivity {
    private static final int SELECT_AUDIO_CODE = 601;
    private static final String TAG = "EditVideoActivity";
    private String bgMusicPath = null;
    private int[] colors = new int[]{R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5};
    private int currentColorPosition;
    private int dp100;
    private int dp20;
    private int dp25;
    private int[] drawableBg = new int[]{R.drawable.color1, R.drawable.color2, R.drawable.color3, R.drawable.color4, R.drawable.color5};
    private VideoEditor editor;
    private EditText etTag;
    private int[] expressions = new int[]{R.mipmap.expression1, R.mipmap.expression2, R.mipmap.expression3, R.mipmap.expression4, R.mipmap.expression5, R.mipmap.expression6, R.mipmap.expression7, R.mipmap.expression8};
    boolean isFirstShowEditText;
    private boolean isImage;
    private boolean isPen;
    private boolean isSpeed;
    private RelativeLayout layoutEditText;
    private RelativeLayout layoutExpression;
    private LinearLayout layoutProgress;
    private RelativeLayout layoutTouchView;
    private LinearLayout ll_color;
    private InputMethodManager manager;
    private MediaInfo mediaInfo;
    private MyVideoView myVideoView;
    private String path;
    private MicRecorder recorder;
    private RelativeLayout rl_bottom;
    private RelativeLayout rl_close;
    private RelativeLayout rl_title;
    private RelativeLayout rl_tuya;
    private SeekBar skbSpeed;
    private TuyaView tuyaView;
    private TextView tvSpeed;
    private TextView tv_hint_delete;
    private TextView tv_tag;
    private int videoHeight;
    private float videoSpeed = 1.0f;
    private int videoWidth;
    private int windowHeight;
    private int windowWidth;

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$10 */
    class C026110 implements OnClickListener {
        C026110() {
        }

        public void onClick(View view) {
            EditVideoActivity.this.changeTextState(EditVideoActivity.this.layoutEditText.getVisibility() != 0);
            EditVideoActivity.this.changePenState(false);
            EditVideoActivity.this.changeExpressionIcon(false);
            EditVideoActivity.this.changeSpeedState(false);
            EditVideoActivity.this.changeRecordMicState(false);
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$11 */
    class C026211 implements OnClickListener {
        C026211() {
        }

        public void onClick(View view) {
            EditVideoActivity.this.tuyaView.backPath();
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$12 */
    class C026312 implements OnClickListener {
        C026312() {
        }

        public void onClick(View view) {
            EditVideoActivity.this.changeTextState(EditVideoActivity.this.layoutEditText.getVisibility() != 0);
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$13 */
    class C026413 implements OnClickListener {
        C026413() {
        }

        public void onClick(View view) {
            EditVideoActivity.this.changeTextState(EditVideoActivity.this.layoutEditText.getVisibility() != 0);
            if (EditVideoActivity.this.etTag.getText().length() > 0) {
                EditVideoActivity.this.addTextToWindow();
            }
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$14 */
    class C026514 implements OnClickListener {
        C026514() {
        }

        public void onClick(View view) {
            EditVideoActivity.this.doVideoEdit();
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$15 */
    class C026615 implements OnClickListener {
        C026615() {
        }

        public void onClick(View view) {
            EditVideoActivity.this.onBackPressed();
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$16 */
    class C026716 implements OnClickListener {
        C026716() {
        }

        public void onClick(View view) {
            boolean z = false;
            EditVideoActivity.this.changeTextState(false);
            EditVideoActivity.this.changePenState(false);
            EditVideoActivity.this.changeExpressionIcon(false);
            EditVideoActivity.this.changeRecordMicState(false);
            EditVideoActivity editVideoActivity = EditVideoActivity.this;
            if (EditVideoActivity.this.layoutProgress.getVisibility() != 0) {
                z = true;
            }
            editVideoActivity.changeSpeedState(z);
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$17 */
    class C026817 implements OnClickListener {
        C026817() {
        }

        public void onClick(View view) {
            EditVideoActivity.this.changeCutState();
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$18 */
    class C026918 implements OnClickListener {
        C026918() {
        }

        public void onClick(View view) {
            Intent intent = new Intent(EditVideoActivity.this, CutVideoTimeActivity.class);
            intent.putExtra("path", EditVideoActivity.this.path);
            EditVideoActivity.this.startActivityForResult(intent, 2);
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$19 */
    class C027019 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C027019() {
        }

        public void afterTextChanged(Editable editable) {
            EditVideoActivity.this.tv_tag.setText(editable.toString());
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$20 */
    class C027120 implements OnSeekBarChangeListener {
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        C027120() {
        }

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            int i2 = 50;
            if (i < 50) {
                EditVideoActivity.this.skbSpeed.setProgress(50);
            } else {
                i2 = i;
            }
            EditVideoActivity.this.videoSpeed = ((float) i2) / 100.0f;
            TextView access$2800 = EditVideoActivity.this.tvSpeed;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(EditVideoActivity.this.videoSpeed);
            stringBuilder.append("");
            access$2800.setText(stringBuilder.toString());
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$27 */
    class C027427 implements OnGlobalLayoutListener {
        C027427() {
        }

        public void onGlobalLayout() {
            if (EditVideoActivity.this.isFirstShowEditText) {
                EditVideoActivity.this.isFirstShowEditText = false;
                EditVideoActivity.this.etTag.setFocusable(true);
                EditVideoActivity.this.etTag.setFocusableInTouchMode(true);
                EditVideoActivity.this.etTag.requestFocus();
                EditVideoActivity.this.isFirstShowEditText = EditVideoActivity.this.manager.showSoftInput(EditVideoActivity.this.etTag, 0) ^ true;
            }
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$28 */
    class C027528 implements AnimatorUpdateListener {
        C027528() {
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            EditVideoActivity.this.layoutEditText.setY(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$29 */
    class C027629 extends AnimatorListenerAdapter {
        C027629() {
        }

        public void onAnimationEnd(Animator animator) {
            EditVideoActivity.this.layoutEditText.setVisibility(8);
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$2 */
    class C02772 implements OnPreparedListener {
        C02772() {
        }

        public void onPrepared(MediaPlayer mediaPlayer) {
            EditVideoActivity.this.myVideoView.setLooping(true);
            EditVideoActivity.this.myVideoView.start();
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$30 */
    class C027830 extends AsyncTask<Void, Void, String> {
        C027830() {
        }

        protected void onPreExecute() {
            EditVideoActivity.this.showProgressDialog();
        }

        protected String doInBackground(Void... voidArr) {
            if ((EditVideoActivity.this.isPen || EditVideoActivity.this.isImage) && EditVideoActivity.this.isSpeed) {
                return EditVideoActivity.this.editor.executeOverLaySpeed(EditVideoActivity.this.path, EditVideoActivity.this.saveImage(), 0, 0, EditVideoActivity.this.videoSpeed);
            }
            if (EditVideoActivity.this.isPen || EditVideoActivity.this.isImage) {
                return EditVideoActivity.this.editor.executeOverLayVideoFrame(EditVideoActivity.this.path, EditVideoActivity.this.saveImage(), 0, 0);
            }
            return EditVideoActivity.this.isSpeed ? EditVideoActivity.this.editor.executeAdjustVideoSpeed(EditVideoActivity.this.path, EditVideoActivity.this.videoSpeed) : null;
        }

        protected void onPostExecute(String str) {
            EditVideoActivity.this.closeProgressDialog();
            if (TextUtils.isEmpty(str)) {
                Toast.makeText(EditVideoActivity.this.getApplicationContext(), "视频编辑失败", 0).show();
                return;
            }
            MediaInfo.checkFile(str);
            EditFile.getInstance().saveEditedVideo(str);
            EditVideoActivity.this.finish();
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$5 */
    class C02795 implements OnClickListener {
        C02795() {
        }

        public void onClick(View view) {
            EditVideoActivity.this.changeTextState(false);
            EditVideoActivity.this.changePenState(false);
            EditVideoActivity.this.changeExpressionIcon(false);
            EditVideoActivity.this.changeSpeedState(false);
            EditVideoActivity.this.changeRecordMicState(false);
            EditVideoActivity.this.selectAudio();
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$6 */
    class C02806 implements OnClickListener {
        C02806() {
        }

        public void onClick(View view) {
            EditVideoActivity.this.changeTextState(false);
            EditVideoActivity.this.changePenState(false);
            EditVideoActivity.this.changeExpressionIcon(false);
            EditVideoActivity.this.changeSpeedState(false);
            EditVideoActivity.this.changeRecordMicState(true);
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$7 */
    class C02817 implements OnTouchListener {
        C02817() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case 0:
                    EditVideoActivity.this.startRecordMic();
                    break;
                case 1:
                    EditVideoActivity.this.stopRecordMic();
                    EditVideoActivity.this.changeRecordMicState(false);
                    break;
            }
            return true;
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$8 */
    class C02828 implements OnClickListener {
        C02828() {
        }

        public void onClick(View view) {
            EditVideoActivity.this.changePenState(EditVideoActivity.this.ll_color.getVisibility() != 0);
            EditVideoActivity.this.changeExpressionIcon(false);
            EditVideoActivity.this.changeTextState(false);
            EditVideoActivity.this.changeSpeedState(false);
            EditVideoActivity.this.changeRecordMicState(false);
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$9 */
    class C02839 implements OnClickListener {
        C02839() {
        }

        public void onClick(View view) {
            EditVideoActivity.this.changeExpressionIcon(EditVideoActivity.this.layoutExpression.getVisibility() != 0);
            EditVideoActivity.this.changePenState(false);
            EditVideoActivity.this.changeTextState(false);
            EditVideoActivity.this.changeSpeedState(false);
            EditVideoActivity.this.changeRecordMicState(false);
        }
    }

    private class addMusicTask extends AsyncTask<Void, Void, String> {
        LanSongMergeAV mergeAV;

        /* renamed from: com.example.lansongcommonui.EditVideoActivity$addMusicTask$1 */
        class OnVideoEditor implements onVideoEditorProgressListener {
            OnVideoEditor() {
            }

            public void onProgress(VideoEditor videoEditor, int i) {
                EditVideoActivity.this.setProgressPercent(i);
            }
        }

        private addMusicTask() {
        }

        /* synthetic */ addMusicTask(EditVideoActivity editVideoActivity, C04091 c04091) {
            this();
        }

        protected void onPreExecute() {
            EditVideoActivity.this.showProgressDialog();
            this.mergeAV = new LanSongMergeAV();
            this.mergeAV.setOnProgessListener(new OnVideoEditor());
        }

        protected String doInBackground(Void... voidArr) {
            return this.mergeAV.mergeAudioVideo(EditVideoActivity.this.bgMusicPath, EditVideoActivity.this.path, EditVideoActivity.this.path);
        }

        protected void onPostExecute(String str) {
            EditVideoActivity.this.closeProgressDialog();
            if (TextUtils.isEmpty(str)) {
                DemoUtil.showHintDialog(EditVideoActivity.this, "增加背景音乐失败,请联系我们");
                return;
            }
            EditFile.getInstance().bgMusicPath = EditVideoActivity.this.bgMusicPath;
            EditFile.getInstance().saveEditedVideo(str);
            EditVideoActivity.this.playVideo();
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$1 */
    class C04091 implements onVideoEditorProgressListener {
        C04091() {
        }

        public void onProgress(VideoEditor videoEditor, int i) {
            EditVideoActivity.this.setProgressPercent(i);
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$22 */
    class C041022 implements OnLimitsListener {
        C041022() {
        }

        public void OnOutLimits(float f, float f2) {
            EditVideoActivity.this.tv_hint_delete.setTextColor(SupportMenu.CATEGORY_MASK);
        }

        public void OnInnerLimits(float f, float f2) {
            EditVideoActivity.this.tv_hint_delete.setTextColor(-1);
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$23 */
    class C041123 implements TouchView.OnTouchListener {
        public void onMove(TouchView touchView, MotionEvent motionEvent) {
        }

        C041123() {
        }

        public void onDown(TouchView touchView, MotionEvent motionEvent) {
            EditVideoActivity.this.tv_hint_delete.setVisibility(0);
            EditVideoActivity.this.changeMode(false);
        }

        public void onUp(TouchView touchView, MotionEvent motionEvent) {
            EditVideoActivity.this.tv_hint_delete.setVisibility(8);
            EditVideoActivity.this.changeMode(true);
            if (touchView.isOutLimits()) {
                EditVideoActivity.this.layoutTouchView.removeView(touchView);
            }
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$24 */
    class C041224 implements OnLimitsListener {
        C041224() {
        }

        public void OnOutLimits(float f, float f2) {
            EditVideoActivity.this.tv_hint_delete.setTextColor(SupportMenu.CATEGORY_MASK);
        }

        public void OnInnerLimits(float f, float f2) {
            EditVideoActivity.this.tv_hint_delete.setTextColor(-1);
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$25 */
    class C041325 implements TouchView.OnTouchListener {
        public void onMove(TouchView touchView, MotionEvent motionEvent) {
        }

        C041325() {
        }

        public void onDown(TouchView touchView, MotionEvent motionEvent) {
            EditVideoActivity.this.tv_hint_delete.setVisibility(0);
            EditVideoActivity.this.changeMode(false);
        }

        public void onUp(TouchView touchView, MotionEvent motionEvent) {
            EditVideoActivity.this.tv_hint_delete.setVisibility(8);
            EditVideoActivity.this.changeMode(true);
            if (touchView.isOutLimits()) {
                EditVideoActivity.this.layoutTouchView.removeView(touchView);
            }
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$3 */
    class C04143 implements OnPlayStateListener {
        C04143() {
        }

        public void onStateChanged(boolean z) {
            if (z) {
                EditVideoActivity.this.videoWidth = EditVideoActivity.this.myVideoView.getVideoWidth();
                EditVideoActivity.this.videoHeight = EditVideoActivity.this.myVideoView.getVideoHeight();
                float access$100 = (((float) EditVideoActivity.this.videoWidth) * 1.0f) / ((float) EditVideoActivity.this.videoHeight);
                float access$1002 = (((float) EditVideoActivity.this.videoWidth) * 1.0f) / ((float) MediaRecorderBase.VIDEO_HEIGHT);
//                EditVideoActivity.this.videoHeight;//todo
                int i = MediaRecorderBase.VIDEO_WIDTH;
                LayoutParams layoutParams = EditVideoActivity.this.myVideoView.getLayoutParams();
                layoutParams.width = (int) (((float) EditVideoActivity.this.windowWidth) * access$1002);
                layoutParams.height = (int) (((float) layoutParams.width) / access$100);
                EditVideoActivity.this.myVideoView.setLayoutParams(layoutParams);
                LayoutParams layoutParams2 = EditVideoActivity.this.rl_tuya.getLayoutParams();
                layoutParams2.width = layoutParams.width;
                layoutParams2.height = layoutParams.height;
                EditVideoActivity.this.rl_tuya.setLayoutParams(layoutParams2);
            }
        }
    }

    /* renamed from: com.example.lansongcommonui.EditVideoActivity$4 */
    class C04154 implements TuyaView.OnTouchListener {
        C04154() {
        }

        public void onDown() {
            EditVideoActivity.this.changeMode(false);
        }

        public void onUp() {
            EditVideoActivity.this.changeMode(true);
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_edit_video);
        this.manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        this.windowWidth = getWindowManager().getDefaultDisplay().getWidth();
        this.windowHeight = getWindowManager().getDefaultDisplay().getHeight();
        this.dp100 = (int) getResources().getDimension(R.dimen.dp100);
        initUI();
        this.editor = new VideoEditor();
        this.editor.setOnProgessListener(new C04091());
    }

    public void onBackPressed() {
        setResult(-1);
        if (this.layoutEditText.getVisibility() == 0) {
            changeTextState(false);
        } else {
            super.onBackPressed();
        }
    }

    protected void onPause() {
        super.onPause();
        this.myVideoView.stop();
        this.myVideoView.release();
    }

    protected void onResume() {
        super.onResume();
        playVideo();
    }

    private void playVideo() {
        this.path = EditFile.getInstance().getEditedVideo();
        this.mediaInfo = new MediaInfo(this.path, false);
        if (this.mediaInfo.prepare()) {
            this.myVideoView.setVideoPath(this.path);
            this.myVideoView.setOnPreparedListener(new C02772());
            this.myVideoView.setOnPlayStateListener(new C04143());
        }
        this.tuyaView.setOnTouchListener(new C04154());
    }

    private void initUI() {
        this.myVideoView = (MyVideoView) findViewById(R.id.vv_play);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rl_pen);
        RelativeLayout relativeLayout2 = (RelativeLayout) findViewById(R.id.rl_icon);
        RelativeLayout relativeLayout3 = (RelativeLayout) findViewById(R.id.rl_text);
        this.layoutExpression = (RelativeLayout) findViewById(R.id.rl_expression);
        this.layoutProgress = (LinearLayout) findViewById(R.id.ll_progress);
        this.layoutTouchView = (RelativeLayout) findViewById(R.id.rl_touch_view);
        this.ll_color = (LinearLayout) findViewById(R.id.ll_color);
        this.tuyaView = (TuyaView) findViewById(R.id.tv_video);
        TextView textView = (TextView) findViewById(R.id.tv_close);
        TextView textView2 = (TextView) findViewById(R.id.tv_finish);
        this.layoutEditText = (RelativeLayout) findViewById(R.id.rl_edit_text);
        this.etTag = (EditText) findViewById(R.id.et_tag);
        this.tv_tag = (TextView) findViewById(R.id.tv_tag);
        TextView textView3 = (TextView) findViewById(R.id.tv_finish_video);
        this.rl_tuya = (RelativeLayout) findViewById(R.id.rl_tuya);
        this.rl_close = (RelativeLayout) findViewById(R.id.rl_close);
        this.rl_title = (RelativeLayout) findViewById(R.id.rl_title);
        this.rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        this.tv_hint_delete = (TextView) findViewById(R.id.tv_hint_delete);
        RelativeLayout relativeLayout4 = (RelativeLayout) findViewById(R.id.rl_speed);
        this.skbSpeed = (SeekBar) findViewById(R.id.sb_speed);
        this.tvSpeed = (TextView) findViewById(R.id.tv_speed);
        RelativeLayout relativeLayout5 = (RelativeLayout) findViewById(R.id.rl_cut_size);
        RelativeLayout relativeLayout6 = (RelativeLayout) findViewById(R.id.rl_cut_time);
        RelativeLayout relativeLayout7 = (RelativeLayout) findViewById(R.id.rl_back);
        findViewById(R.id.id_edit_btn_music).setOnClickListener(new C02795());
        findViewById(R.id.id_edit_btn_recordmic).setOnClickListener(new C02806());
        findViewById(R.id.id_edit_iv_recordmic).setOnTouchListener(new C02817());
        relativeLayout.setOnClickListener(new C02828());
        relativeLayout2.setOnClickListener(new C02839());
        relativeLayout3.setOnClickListener(new C026110());
        relativeLayout7.setOnClickListener(new C026211());
        textView.setOnClickListener(new C026312());
        textView2.setOnClickListener(new C026413());
        textView3.setOnClickListener(new C026514());
        this.rl_close.setOnClickListener(new C026615());
        relativeLayout4.setOnClickListener(new C026716());
        relativeLayout5.setOnClickListener(new C026817());
        relativeLayout6.setOnClickListener(new C026918());
        initColors();
        initExpression();
        initSpeed();
        this.etTag.addTextChangedListener(new C027019());
    }

    private void selectAudio() {
        startActivityForResult(new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI), SELECT_AUDIO_CODE);
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == SELECT_AUDIO_CODE && i2 == -1 && intent != null) {
            String[] strArr = new String[]{"_data"};
            Cursor query = getContentResolver().query(intent.getData(), strArr, null, null, null);
            query.moveToFirst();
            this.bgMusicPath = query.getString(query.getColumnIndex(strArr[0]));
            query.close();
            MediaInfo.checkFile(this.bgMusicPath);
            new addMusicTask(this, null).execute(new Void[0]);
        }
    }

    private void initSpeed() {
        this.skbSpeed.setMax(200);
        this.skbSpeed.setProgress(100);
        this.skbSpeed.setOnSeekBarChangeListener(new C027120());
    }

    private void changeMode(boolean z) {
        if (z) {
            this.rl_title.setVisibility(0);
            this.rl_bottom.setVisibility(0);
            return;
        }
        this.rl_title.setVisibility(8);
        this.rl_bottom.setVisibility(8);
    }

    private void initExpression() {
        int dimension = (int) getResources().getDimension(R.dimen.dp80);
        int dimension2 = (int) getResources().getDimension(R.dimen.dp10);
        for (int i = 0; i < this.expressions.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setPadding(dimension2, dimension2, dimension2, dimension2);
            final int i2 = this.expressions[i];
            imageView.setImageResource(i2);
            imageView.setLayoutParams(new LayoutParams(this.windowWidth / 4, dimension));
            imageView.setX((float) (((i % 4) * this.windowWidth) / 4));
            imageView.setY((float) ((i / 4) * dimension));
            imageView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    EditVideoActivity.this.layoutExpression.setVisibility(8);
                    EditVideoActivity.this.addExpressionToWindow(i2);
                }
            });
            this.layoutExpression.addView(imageView);
        }
    }

    private void addExpressionToWindow(int i) {
        TouchView touchView = new TouchView(this);
        touchView.setBackgroundResource(i);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(this.dp100, this.dp100);
        layoutParams.addRule(13);
        touchView.setLayoutParams(layoutParams);
        touchView.setLimitsX(0, this.windowWidth);
        touchView.setLimitsY(0, this.windowHeight - (this.dp100 / 2));
        touchView.setOnLimitsListener(new C041022());
        touchView.setOnTouchListener(new C041123());
        this.layoutTouchView.addView(touchView);
    }

    private void addTextToWindow() {
        TouchView touchView = new TouchView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(this.tv_tag.getWidth(), this.tv_tag.getHeight());
        layoutParams.addRule(13);
        touchView.setLayoutParams(layoutParams);
        Bitmap createBitmap = Bitmap.createBitmap(this.tv_tag.getWidth(), this.tv_tag.getHeight(), Config.ARGB_8888);
        this.tv_tag.draw(new Canvas(createBitmap));
        touchView.setBackground(new BitmapDrawable(createBitmap));
        touchView.setLimitsX(0, this.windowWidth);
        touchView.setLimitsY(0, this.windowHeight - (this.dp100 / 2));
        touchView.setOnLimitsListener(new C041224());
        touchView.setOnTouchListener(new C041325());
        this.layoutTouchView.addView(touchView);
        this.etTag.setText("");
        this.tv_tag.setText("");
    }

    private void initColors() {
        this.dp20 = (int) getResources().getDimension(R.dimen.dp20);
        this.dp25 = (int) getResources().getDimension(R.dimen.dp25);
        for (int i = 0; i < this.drawableBg.length; i++) {
            RelativeLayout relativeLayout = new RelativeLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -1);
            layoutParams.weight = 1.0f;
            relativeLayout.setLayoutParams(layoutParams);
            View view = new View(this);
            view.setBackgroundDrawable(getResources().getDrawable(this.drawableBg[i]));
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(this.dp20, this.dp20);
            layoutParams2.addRule(13);
            view.setLayoutParams(layoutParams2);
            relativeLayout.addView(view);
            view = new View(this);
            view.setBackgroundResource(R.mipmap.color_click);
            layoutParams2 = new RelativeLayout.LayoutParams(this.dp25, this.dp25);
            layoutParams2.addRule(13);
            view.setLayoutParams(layoutParams2);
            if (i != 0) {
                view.setVisibility(8);
            }
            relativeLayout.addView(view);
            final int finalI = i;
            relativeLayout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (EditVideoActivity.this.currentColorPosition != finalI) {
                        view.setVisibility(0);
                        ((ViewGroup) ((ViewGroup) view.getParent()).getChildAt(EditVideoActivity.this.currentColorPosition)).getChildAt(1).setVisibility(8);
                        EditVideoActivity.this.tuyaView.setNewPaintColor(EditVideoActivity.this.getResources().getColor(EditVideoActivity.this.colors[finalI]));
                        EditVideoActivity.this.currentColorPosition = finalI;
                    }
                }
            });
            this.ll_color.addView(relativeLayout, i);
        }
    }

    public void popupEditText() {
        this.isFirstShowEditText = true;
        this.etTag.getViewTreeObserver().addOnGlobalLayoutListener(new C027427());
    }

    private void startAnim(float f, float f2, AnimatorListenerAdapter animatorListenerAdapter) {
        ValueAnimator duration = ValueAnimator.ofFloat(new float[]{f, f2}).setDuration(200);
        duration.addUpdateListener(new C027528());
        if (animatorListenerAdapter != null) {
            duration.addListener(animatorListenerAdapter);
        }
        duration.start();
    }

    private void changePenState(boolean z) {
        if (z) {
            this.tuyaView.setDrawMode(z);
            this.tuyaView.setNewPaintColor(getResources().getColor(this.colors[this.currentColorPosition]));
            this.ll_color.setVisibility(0);
            return;
        }
        this.tuyaView.setDrawMode(z);
        this.ll_color.setVisibility(8);
    }


    private void changeExpressionIcon(boolean z) {
        if (z) {
            this.layoutExpression.setVisibility(0);
        } else {
            this.layoutExpression.setVisibility(8);
        }
    }

    private void changeTextState(boolean z) {
        if (z) {
            this.layoutEditText.setY((float) this.windowHeight);
            this.layoutEditText.setVisibility(0);
            startAnim(this.layoutEditText.getY(), 0.0f, null);
            popupEditText();
            return;
        }
        this.manager.hideSoftInputFromWindow(this.etTag.getWindowToken(), 2);
        startAnim(0.0f, (float) this.windowHeight, new C027629());
    }

    private void changeCutState() {
        Intent intent = new Intent(this, CropVideoFrameActivity.class);
        intent.putExtra("path", this.path);
        startActivityForResult(intent, 1);
    }

    @SuppressLint("WrongConstant")
    private void changeSpeedState(boolean z) {
        if (z) {
            this.layoutProgress.setVisibility(0);
        } else {
            this.layoutProgress.setVisibility(8);
        }
    }

    @SuppressLint("WrongConstant")
    private void changeRecordMicState(boolean z) {
        findViewById(R.id.rl_recordmic).setVisibility(z ? 0 : 8);
    }

    @SuppressLint({"StaticFieldLeak"})
    private void doVideoEdit() {
        if (this.tuyaView.getPathSum() == 0) {
            this.isPen = false;
        } else {
            this.isPen = true;
        }
        if (this.layoutTouchView.getChildCount() == 0) {
            this.isImage = false;
        } else {
            this.isImage = true;
        }
        if (this.skbSpeed.getProgress() == 100) {
            this.isSpeed = false;
        } else {
            this.isSpeed = true;
        }
        if (this.isPen || this.isImage || this.isSpeed) {
            new C027830().execute(new Void[0]);
        } else {
            finish();
        }
    }

    private String saveImage() {
        Bitmap createBitmap = Bitmap.createBitmap(this.rl_tuya.getWidth(), this.rl_tuya.getHeight(), Config.ARGB_8888);
        this.rl_tuya.draw(new Canvas(createBitmap));
        Matrix matrix = new Matrix();
        matrix.postScale((((float) this.videoWidth) * 1.0f) / ((float) createBitmap.getWidth()), (((float) this.videoHeight) * 1.0f) / ((float) createBitmap.getHeight()));
        Bitmap createBitmap2 = Bitmap.createBitmap(createBitmap, 0, 0, createBitmap.getWidth(), createBitmap.getHeight(), matrix, true);
        try {
            String createFileInBox = LanSongFileUtil.createFileInBox("png");
            OutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(createFileInBox)));
            createBitmap2.compress(CompressFormat.PNG, 100, bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            return createFileInBox;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void startRecordMic() {
        this.myVideoView.stop();
        showProgressDialog("正在录制外音...");
        if (this.mediaInfo == null || !this.mediaInfo.isHaveAudio()) {
            this.recorder = new MicRecorder(false/*, 44100*/);
        } else {
            this.recorder = new MicRecorder(false/*, this.mediaInfo.aSampleRate*/);
        }
        this.recorder.start();
    }

    private void stopRecordMic() {
        if (this.recorder != null) {
            String stop = this.recorder.stop();
            this.recorder.release();
            this.recorder = null;
            if (this.mediaInfo.isHaveAudio()) {
                this.bgMusicPath = stop;
                new addMusicTask(this, null).execute(new Void[0]);
            } else {
                this.path = EditFile.getInstance().saveEditedVideo(LanSongMergeAV.mergeAVDirectly(stop, this.path, true));
                playVideo();
            }
            closeProgressDialog();
        }
    }
}
