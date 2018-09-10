package com.example.lansongcommonui.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;

public class MyVideoView extends TextureView implements SurfaceTextureListener {
    private static final int HANDLER_MESSAGE_LOOP = 1;
    private static final int HANDLER_MESSAGE_PARSE = 0;
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_RELEASED = 5;
    private static final int STATE_STOP = 5;
    private OnCompletionListener mCompletionListener = new C02921();
    private int mCurrentState = 0;
    private int mDuration;
    private OnErrorListener mErrorListener = new C02954();
    private MediaPlayer mMediaPlayer = null;
    private OnCompletionListener mOnCompletionListener;
    private OnErrorListener mOnErrorListener;
    private OnPlayStateListener mOnPlayStateListener;
    private OnPreparedListener mOnPreparedListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;
    OnPreparedListener mPreparedListener = new C02932();
    private OnSeekCompleteListener mSeekCompleteListener = new C02943();
    private SurfaceTexture mSurfaceHolder = null;
    private int mTargetState = 0;
    private Uri mUri;
    private Handler mVideoHandler = new C02965();
    private int mVideoHeight;
    private int mVideoWidth;
    private float mVolumn = -1.0f;

    /* renamed from: com.example.lansongcommonui.view.MyVideoView$1 */
    class C02921 implements OnCompletionListener {
        C02921() {
        }

        public void onCompletion(MediaPlayer mediaPlayer) {
            MyVideoView.this.mCurrentState = 5;
            if (MyVideoView.this.mOnCompletionListener != null) {
                MyVideoView.this.mOnCompletionListener.onCompletion(mediaPlayer);
            }
        }
    }

    /* renamed from: com.example.lansongcommonui.view.MyVideoView$2 */
    class C02932 implements OnPreparedListener {
        C02932() {
        }

        public void onPrepared(MediaPlayer mediaPlayer) {
            if (MyVideoView.this.mCurrentState == 1) {
                MyVideoView.this.mCurrentState = 2;
                try {
                    MyVideoView.this.mDuration = mediaPlayer.getDuration();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                try {
                    MyVideoView.this.mVideoWidth = mediaPlayer.getVideoWidth();
                    MyVideoView.this.mVideoHeight = mediaPlayer.getVideoHeight();
                } catch (IllegalStateException e2) {
                    e2.printStackTrace();
                }
                switch (MyVideoView.this.mTargetState) {
                    case 2:
                        if (MyVideoView.this.mOnPreparedListener != null) {
                            MyVideoView.this.mOnPreparedListener.onPrepared(MyVideoView.this.mMediaPlayer);
                            return;
                        }
                        return;
                    case 3:
                        MyVideoView.this.start();
                        return;
                    default:
                        return;
                }
            }
        }
    }

    /* renamed from: com.example.lansongcommonui.view.MyVideoView$3 */
    class C02943 implements OnSeekCompleteListener {
        C02943() {
        }

        public void onSeekComplete(MediaPlayer mediaPlayer) {
            if (MyVideoView.this.mOnSeekCompleteListener != null) {
                MyVideoView.this.mOnSeekCompleteListener.onSeekComplete(mediaPlayer);
            }
        }
    }

    /* renamed from: com.example.lansongcommonui.view.MyVideoView$4 */
    class C02954 implements OnErrorListener {
        C02954() {
        }

        public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
            MyVideoView.this.mCurrentState = -1;
            if (MyVideoView.this.mOnErrorListener != null) {
                MyVideoView.this.mOnErrorListener.onError(mediaPlayer, i, i2);
            }
            return true;
        }
    }

    /* renamed from: com.example.lansongcommonui.view.MyVideoView$5 */
    class C02965 extends Handler {
        C02965() {
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    MyVideoView.this.pause();
                    break;
                case 1:
                    if (MyVideoView.this.isPlaying()) {
                        MyVideoView.this.seekTo(message.arg1);
                        sendMessageDelayed(MyVideoView.this.mVideoHandler.obtainMessage(1, message.arg1, message.arg2), (long) message.arg2);
                        break;
                    }
                    break;
            }
            super.handleMessage(message);
        }
    }

    public interface OnPlayStateListener {
        void onStateChanged(boolean z);
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public MyVideoView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initVideoView();
    }

    public MyVideoView(Context context) {
        super(context);
        initVideoView();
    }

    public MyVideoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initVideoView();
    }

    public MediaPlayer getMediaPlayer() {
        return this.mMediaPlayer;
    }

    protected void initVideoView() {
        try {
            this.mVolumn = (float) ((AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(3);
        } catch (UnsupportedOperationException unused) {
            this.mVideoWidth = 0;
            this.mVideoHeight = 0;
            setSurfaceTextureListener(this);
            this.mCurrentState = 0;
            this.mTargetState = 0;
        }
    }

    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        this.mOnPreparedListener = onPreparedListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.mOnErrorListener = onErrorListener;
    }

    public void setOnPlayStateListener(OnPlayStateListener onPlayStateListener) {
        this.mOnPlayStateListener = onPlayStateListener;
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener) {
        this.mOnSeekCompleteListener = onSeekCompleteListener;
    }

    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        this.mOnCompletionListener = onCompletionListener;
    }

    public void setVideoPath(String str) {
        this.mTargetState = 2;
        openVideo(Uri.parse(str));
    }

    public int getVideoWidth() {
        return this.mVideoWidth;
    }

    public int getVideoHeight() {
        return this.mVideoHeight;
    }

    public void reOpen() {
        this.mTargetState = 2;
        openVideo(this.mUri);
    }

    public int getDuration() {
        return this.mDuration;
    }

    private void tryAgain(Exception exception) {
        exception.printStackTrace();
        this.mCurrentState = -1;
        openVideo(this.mUri);
    }

    public void start() {
        this.mTargetState = 3;
        if (this.mMediaPlayer == null) {
            return;
        }
        if (this.mCurrentState == 2 || this.mCurrentState == 4 || this.mCurrentState == 3 || this.mCurrentState == 5) {
            try {
                if (!isPlaying()) {
                    this.mMediaPlayer.start();
                }
                this.mCurrentState = 3;
                if (this.mOnPlayStateListener != null) {
                    this.mOnPlayStateListener.onStateChanged(true);
                }

            } catch (Exception e2) {
                tryAgain(e2);
            }
        }
    }

    public void pause() {
        this.mTargetState = 4;
        if (this.mMediaPlayer == null) {
            return;
        }
        if (this.mCurrentState == 3 || this.mCurrentState == 4) {
            try {
                this.mMediaPlayer.pause();
                this.mCurrentState = 4;
                if (this.mOnPlayStateListener != null) {
                    this.mOnPlayStateListener.onStateChanged(false);
                }

            } catch (Exception e2) {
                tryAgain(e2);
            }
        }
    }

    public void stop() {
        this.mTargetState = 5;
        if (this.mMediaPlayer == null) {
            return;
        }
        if (this.mCurrentState == 3 || this.mCurrentState == 4) {
            try {
                this.mMediaPlayer.stop();
                this.mCurrentState = 5;
                if (this.mOnPlayStateListener != null) {
                    this.mOnPlayStateListener.onStateChanged(false);
                }

            } catch (Exception e2) {
                tryAgain(e2);
            }
        }
    }

    public void setVolume(float f) {
        if (this.mMediaPlayer == null) {
            return;
        }
        if (this.mCurrentState == 2 || this.mCurrentState == 3 || this.mCurrentState == 4 || this.mCurrentState == 5) {
            try {
                this.mMediaPlayer.setVolume(f, f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setLooping(boolean z) {
        if (this.mMediaPlayer == null) {
            return;
        }
        if (this.mCurrentState == 2 || this.mCurrentState == 3 || this.mCurrentState == 4 || this.mCurrentState == 5) {
            try {
                this.mMediaPlayer.setLooping(z);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void seekTo(int i) {
        if (this.mMediaPlayer == null) {
            return;
        }
        if (this.mCurrentState == 2 || this.mCurrentState == 3 || this.mCurrentState == 4 || this.mCurrentState == 5) {
            if (i < 0) {
                i = 0;
            }
            try {
                this.mMediaPlayer.seekTo(i);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public int getCurrentPosition() {
        if (this.mMediaPlayer != null) {
            switch (this.mCurrentState) {
                case 3:
                case 4:
                    try {
                        return this.mMediaPlayer.getCurrentPosition();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        break;
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        break;
                    }
                case 5:
                    return getDuration();
            }
        }
        return 0;
    }

    public boolean isPlaying() {
        if (this.mMediaPlayer != null && this.mCurrentState == 3) {
            try {
                return this.mMediaPlayer.isPlaying();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    public void release() {
        this.mTargetState = 5;
        this.mCurrentState = 5;
        if (this.mMediaPlayer != null) {
            try {
                this.mMediaPlayer.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            this.mMediaPlayer = null;
        }
    }

    public void openVideo(Uri uri) {
        if (uri == null || this.mSurfaceHolder == null || getContext() == null) {
            if (this.mSurfaceHolder == null && uri != null) {
                this.mUri = uri;
            }
            return;
        }
        this.mUri = uri;
        this.mDuration = 0;
        Exception exception = null;
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.stop();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
        try {
            if (this.mMediaPlayer == null) {
                this.mMediaPlayer = new MediaPlayer();
                this.mMediaPlayer.setOnPreparedListener(this.mPreparedListener);
                this.mMediaPlayer.setOnCompletionListener(this.mCompletionListener);
                this.mMediaPlayer.setOnErrorListener(this.mErrorListener);
                this.mMediaPlayer.setAudioStreamType(3);
                this.mMediaPlayer.setOnSeekCompleteListener(this.mSeekCompleteListener);
                this.mMediaPlayer.setVolume(this.mVolumn, this.mVolumn);
                this.mMediaPlayer.setSurface(new Surface(this.mSurfaceHolder));
            } else {
                this.mMediaPlayer.reset();
            }
            this.mMediaPlayer.setDataSource(getContext(), uri);
            this.mMediaPlayer.prepareAsync();
            this.mCurrentState = 1;
        } catch (Exception e) {
            exception = e;
        }
        if (exception != null) {
            exception.printStackTrace();
            this.mCurrentState = -1;
            if (this.mErrorListener != null) {
                this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
            }
        }
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        Object obj = this.mSurfaceHolder == null ? 1 : null;
        this.mSurfaceHolder = surfaceTexture;
        if (obj != null) {
            reOpen();
        }
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        this.mSurfaceHolder = null;
        release();
        return true;
    }

    public boolean isPrepared() {
        return this.mMediaPlayer != null && this.mCurrentState == 2;
    }

    public void pauseDelayed(int i) {
        if (this.mVideoHandler.hasMessages(0)) {
            this.mVideoHandler.removeMessages(0);
        }
        this.mVideoHandler.sendEmptyMessageDelayed(0, (long) i);
    }

    public void pauseClearDelayed() {
        pause();
        if (this.mVideoHandler.hasMessages(0)) {
            this.mVideoHandler.removeMessages(0);
        }
        if (this.mVideoHandler.hasMessages(1)) {
            this.mVideoHandler.removeMessages(1);
        }
    }

    public void loopDelayed(int i, int i2) {
        i2 -= i;
        seekTo(i);
        if (!isPlaying()) {
            start();
        }
        if (this.mVideoHandler.hasMessages(1)) {
            this.mVideoHandler.removeMessages(1);
        }
        this.mVideoHandler.sendMessageDelayed(this.mVideoHandler.obtainMessage(1, getCurrentPosition(), i2), (long) i2);
    }
}
