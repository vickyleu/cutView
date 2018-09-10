package com.example.lansongcommonui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import com.lansongsdk.commonFree.R;
import java.util.ArrayList;
import java.util.List;

public class FocusSurfaceView extends SurfaceView {
    float downX;
    float downY;
    private String focusMode;
    private ImageView imageView;
    /* renamed from: va */
    private ValueAnimator f31va;

    /* renamed from: com.example.lansongcommonui.view.FocusSurfaceView$1 */
    class C02891 implements AutoFocusCallback {
        C02891() {
        }

        public void onAutoFocus(boolean z, Camera camera) {
            Parameters parameters = camera.getParameters();
            parameters.setFocusMode(FocusSurfaceView.this.focusMode);
            camera.setParameters(parameters);
        }
    }

    /* renamed from: com.example.lansongcommonui.view.FocusSurfaceView$2 */
    class C02902 implements AnimatorUpdateListener {
        C02902() {
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if (FocusSurfaceView.this.imageView != null) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                float f;
                if (floatValue <= 0.5f) {
                    f = 1.0f + floatValue;
                    FocusSurfaceView.this.imageView.setScaleX(f);
                    FocusSurfaceView.this.imageView.setScaleY(f);
                    return;
                }
                f = 2.0f - floatValue;
                FocusSurfaceView.this.imageView.setScaleX(f);
                FocusSurfaceView.this.imageView.setScaleY(f);
            }
        }
    }

    public void setTouchFocus(MediaRecorderBase mediaRecorderBase) {
    }

    public FocusSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FocusSurfaceView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public FocusSurfaceView(Context context) {
        super(context);
    }

    private void focusOnTouch(int i, int i2, Camera camera) {
        Rect rect = new Rect(i - 100, i2 - 100, i + 100, i2 + 100);
        i2 = 1000;
        i = ((rect.left * 2000) / getWidth()) - 1000;
        int height = ((rect.top * 2000) / getHeight()) - 1000;
        int width = ((rect.right * 2000) / getWidth()) - 1000;
        int height2 = ((rect.bottom * 2000) / getHeight()) - 1000;
        if (i < NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
            i = NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (height < NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) {
            height = NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
        if (width > 1000) {
            width = 1000;
        }
        if (height2 <= 1000) {
            i2 = height2;
        }
        try {
            focusOnRect(new Rect(i, height, width, i2), camera);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void focusOnRect(Rect rect, Camera camera) {
        if (TextUtils.isEmpty(this.focusMode)) {
            this.focusMode = camera.getParameters().getFocusMode();
        }
        Parameters parameters = camera.getParameters();
        parameters.setFocusMode("auto");
        if (parameters.getMaxNumFocusAreas() > 0) {
            List arrayList = new ArrayList();
            arrayList.add(new Area(rect, 1000));
            parameters.setFocusAreas(arrayList);
        }
        camera.cancelAutoFocus();
        camera.setParameters(parameters);
        camera.autoFocus(new C02891());
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.downX = motionEvent.getX();
            this.downY = motionEvent.getY();
        }
        return true;
    }

    private void addFocusToWindow() {
        if (this.f31va == null) {
            this.imageView = new ImageView(getContext());
            this.imageView.setImageResource(R.mipmap.video_focus);
            this.imageView.setLayoutParams(new LayoutParams(-2, -2));
            this.imageView.measure(0, 0);
            this.imageView.setX(this.downX - ((float) (this.imageView.getMeasuredWidth() / 2)));
            this.imageView.setY(this.downY - ((float) (this.imageView.getMeasuredHeight() / 2)));
            final ViewGroup viewGroup = (ViewGroup) getParent();
            viewGroup.addView(this.imageView);
            this.f31va = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(500);
            this.f31va.addUpdateListener(new C02902());
            this.f31va.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (FocusSurfaceView.this.imageView != null) {
                        viewGroup.removeView(FocusSurfaceView.this.imageView);
                        FocusSurfaceView.this.f31va = null;
                    }
                }
            });
            this.f31va.start();
        }
    }
}
