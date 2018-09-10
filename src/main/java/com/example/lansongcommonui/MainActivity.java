package com.example.lansongcommonui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.example.lansongcommonui.view.MediaRecorderBase;
import com.example.lansongcommonui.view.MyVideoView;
import com.lansongsdk.commonFree.R;
import com.lansosdk.videoeditor.DemoUtil;
import com.lansosdk.videoeditor.LanSoEditor;

public class MainActivity extends Activity {
    String TAG = "ss";
    private boolean isPermissionOk = false;
    private RelativeLayout layoutShow;
    int permissionCnt = 0;
    private MyVideoView viewPlayView;

    /* renamed from: com.example.lansongcommonui.MainActivity$1 */
    class C02851 implements OnClickListener {
        C02851() {
        }

        public void onClick(View view) {
            if (!MainActivity.this.isPermissionOk) {
                MainActivity.this.testPermission();
            }
            MainActivity.this.startActivity(new Intent(MainActivity.this, CameraRecordActivity.class));
        }
    }

    /* renamed from: com.example.lansongcommonui.MainActivity$2 */
    class C02862 implements OnPreparedListener {
        C02862() {
        }

        public void onPrepared(MediaPlayer mediaPlayer) {
            MainActivity.this.viewPlayView.setLooping(true);
            MainActivity.this.viewPlayView.start();
            float videoWidth = (((float) MainActivity.this.viewPlayView.getVideoWidth()) * 1.0f) / ((float) MediaRecorderBase.VIDEO_HEIGHT);
            float videoHeight = (((float) MainActivity.this.viewPlayView.getVideoHeight()) * 1.0f) / ((float) MediaRecorderBase.VIDEO_WIDTH);
            LayoutParams layoutParams = MainActivity.this.viewPlayView.getLayoutParams();
            layoutParams.width = (int) (((float) MainActivity.this.layoutShow.getWidth()) * videoWidth);
            layoutParams.height = (int) (((float) MainActivity.this.layoutShow.getHeight()) * videoHeight);
            MainActivity.this.viewPlayView.setLayoutParams(layoutParams);
        }
    }

    /* renamed from: com.example.lansongcommonui.MainActivity$3 */
    class C02873 extends PermissionsResultAction {
        C02873() {
        }

        public void onGranted() {
            MainActivity.this.isPermissionOk = true;
        }

        public void onDenied(String str) {
            MainActivity.this.isPermissionOk = false;
        }
    }

    private void testFile() {
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_main);
        LanSoEditor.initSDK(getApplicationContext(), null);
        this.viewPlayView = (MyVideoView) findViewById(R.id.vv_play);
        this.layoutShow = (RelativeLayout) findViewById(R.id.rl_show);
        testPermission();
        findViewById(R.id.id_main_button).setOnClickListener(new C02851());
        testFile();
    }

    @SuppressLint("WrongConstant")
    protected void onResume() {
        super.onResume();
        if (EditFile.getInstance().getEditedVideo() != null) {
            this.viewPlayView.setVisibility(0);
            this.viewPlayView.setOnPreparedListener(new C02862());
            this.viewPlayView.setVideoPath(EditFile.getInstance().getEditedVideo());
            return;
        }
        this.layoutShow.setVisibility(0);
    }

    private void testPermission() {
        if (this.permissionCnt > 2) {
            DemoUtil.showHintDialog((Activity) this, "Demo没有读写权限,请关闭后重新打开demo,并在弹出框中选中[允许]");
            return;
        }
        this.permissionCnt++;
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new C02873());
    }
}
