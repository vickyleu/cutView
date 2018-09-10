package com.example.lansongcommonui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.v4.internal.view.SupportMenu;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.lansongsdk.commonFree.R;
import java.util.ArrayList;
import java.util.List;

public class RecordedButton extends View {
    private int animTime = 150;
    private ValueAnimator buttonAnim;
    private boolean cleanResponse;
    private int colorBlue;
    private int colorGray;
    private float downX;
    private float downY;
    private int dp5;
    private float firstX;
    private float firstY;
    boolean flag;
    private float girthPro;
    private boolean isDeleteMode;
    private boolean isOpenMode = true;
    private boolean isResponseLongTouch = true;
    private long max;
    private int measuredWidth = -1;
    private Handler myHandler = new C02971();
    private OnGestureListener onGestureListener;
    private RectF oval;
    private Paint paint;
    private Paint paintDelete;
    private Paint paintProgress;
    private Paint paintSplit;
    private float progress;
    private float radius1;
    private float radius2;
    private float rawX = -1.0f;
    private float rawY = -1.0f;
    private List<Float> splitList = new ArrayList();
    private float zoom = 0.8f;

    /* renamed from: com.example.lansongcommonui.view.RecordedButton$1 */
    class C02971 extends Handler {
        C02971() {
        }

        public void handleMessage(Message message) {
            if (RecordedButton.this.onGestureListener != null) {
                RecordedButton.this.startAnim(0.0f, 1.0f - RecordedButton.this.zoom);
                RecordedButton.this.isOpenMode = true;
                RecordedButton.this.onGestureListener.onLongClickDown();
            }
        }
    }

    /* renamed from: com.example.lansongcommonui.view.RecordedButton$5 */
    class C03015 implements AnimatorUpdateListener {
        C03015() {
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            RecordedButton.this.radius1 = (((float) RecordedButton.this.measuredWidth) * (RecordedButton.this.zoom + floatValue)) / 2.0f;
            RecordedButton.this.radius2 = ((((float) RecordedButton.this.measuredWidth) * (RecordedButton.this.zoom - floatValue)) / 2.0f) - ((float) RecordedButton.this.dp5);
            float access$100 = (1.0f - RecordedButton.this.zoom) - floatValue;
            RecordedButton.this.oval.left = ((((float) RecordedButton.this.measuredWidth) * access$100) / 2.0f) + ((float) (RecordedButton.this.dp5 / 2));
            RecordedButton.this.oval.top = ((((float) RecordedButton.this.measuredWidth) * access$100) / 2.0f) + ((float) (RecordedButton.this.dp5 / 2));
            float f = 1.0f - (access$100 / 2.0f);
            RecordedButton.this.oval.right = (((float) RecordedButton.this.measuredWidth) * f) - ((float) (RecordedButton.this.dp5 / 2));
            RecordedButton.this.oval.bottom = (((float) RecordedButton.this.measuredWidth) * f) - ((float) (RecordedButton.this.dp5 / 2));
            RecordedButton.this.invalidate();
        }
    }

    public interface OnGestureListener {
        void onClick();

        void onLongClickDown();

        void onLongClickUp();

        void onReachMax();
    }

    public RecordedButton(Context context) {
        super(context);
        init();
    }

    public RecordedButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public RecordedButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        this.dp5 = (int) getResources().getDimension(R.dimen.dp6);
        this.colorGray = getResources().getColor(R.color.video_gray);
        this.colorBlue = getResources().getColor(R.color.blue);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paintProgress = new Paint();
        this.paintProgress.setAntiAlias(true);
        this.paintProgress.setColor(this.colorBlue);
        this.paintProgress.setStrokeWidth((float) this.dp5);
        this.paintProgress.setStyle(Style.STROKE);
        this.paintSplit = new Paint();
        this.paintSplit.setAntiAlias(true);
        this.paintSplit.setColor(-1);
        this.paintSplit.setStrokeWidth((float) this.dp5);
        this.paintSplit.setStyle(Style.STROKE);
        this.paintDelete = new Paint();
        this.paintDelete.setAntiAlias(true);
        this.paintDelete.setColor(SupportMenu.CATEGORY_MASK);
        this.paintDelete.setStrokeWidth((float) this.dp5);
        this.paintDelete.setStyle(Style.STROKE);
        this.oval = new RectF();
    }

    public void setResponseLongTouch(boolean z) {
        this.isResponseLongTouch = z;
    }

    public int getSplitCount() {
        return this.splitList.size();
    }

    public float getCurrentPro() {
        return this.progress;
    }

    public void setOnGestureListener(OnGestureListener onGestureListener) {
        this.onGestureListener = onGestureListener;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float rawX;
        float rawY;
        switch (motionEvent.getAction()) {
            case 0:
                if (this.isResponseLongTouch) {
                    this.myHandler.sendEmptyMessageDelayed(0, (long) this.animTime);
                }
                rawX = motionEvent.getRawX();
                this.downX = rawX;
                this.firstX = rawX;
                rawY = motionEvent.getRawY();
                this.downY = rawY;
                this.firstY = rawY;
                break;
            case 1:
            case 3:
                rawX = motionEvent.getRawX();
                rawY = motionEvent.getRawY();
                if (!this.cleanResponse) {
                    if (!this.isResponseLongTouch || this.myHandler.hasMessages(0)) {
                        this.myHandler.removeMessages(0);
                        if (Math.abs(rawX - this.firstX) < ((float) this.dp5) && Math.abs(rawY - this.firstY) < ((float) this.dp5) && this.onGestureListener != null) {
                            this.onGestureListener.onClick();
                        }
                    } else if (this.isOpenMode) {
                        if (this.onGestureListener != null) {
                            this.onGestureListener.onLongClickUp();
                        }
                        closeButton();
                    }
                }
                this.cleanResponse = false;
                if (!(rawX == this.firstX && rawY == this.firstY)) {
                    startMoveAnim();
                    break;
                }
                break;
            case 2:
                rawX = motionEvent.getRawX();
                rawY = motionEvent.getRawY();
                if ((Math.abs(rawX - this.firstX) > ((float) this.dp5) || Math.abs(rawY - this.firstY) > ((float) this.dp5)) && this.myHandler.hasMessages(0)) {
                    this.cleanResponse = true;
                    this.myHandler.removeMessages(0);
                }
                float f = rawY - this.downY;
                setX(getX() + (rawX - this.downX));
                setY(getY() + f);
                this.downX = rawX;
                this.downY = rawY;
                break;
        }
        return true;
    }

    private void startMoveAnim() {
        final float x = this.rawX - getX();
        final float y = this.rawY - getY();
        final float x2 = getX();
        final float y2 = getY();
        ValueAnimator duration = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(50);
        final float f = x;
        final float f2 = y;
        duration.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                RecordedButton.this.setX(x2 + (f * floatValue));
                RecordedButton.this.setY(y2 + (f2 * floatValue));
            }
        });
        duration.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (Math.abs(x) > Math.abs(y)) {
                    RecordedButton.this.jitterAnim(x / 5.0f, true);
                } else {
                    RecordedButton.this.jitterAnim(y / 5.0f, false);
                }
            }
        });
        duration.start();
    }

    private void jitterAnim(float f, final boolean z) {
        ValueAnimator duration = ValueAnimator.ofFloat(new float[]{f, 0.0f}).setDuration(100);
        duration.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                if (RecordedButton.this.flag) {
                    floatValue = -floatValue;
                }
                if (z) {
                    RecordedButton.this.setX(RecordedButton.this.rawX + floatValue);
                } else {
                    RecordedButton.this.setY(RecordedButton.this.rawY + floatValue);
                }
                RecordedButton.this.flag=false;
            }
        });
        duration.start();
    }

    public void closeButton() {
        if (this.isOpenMode) {
            this.isOpenMode = false;
            startAnim(1.0f - this.zoom, 0.0f);
        }
    }

    private void startAnim(float f, float f2) {
        if (this.buttonAnim == null || !this.buttonAnim.isRunning()) {
            this.buttonAnim = ValueAnimator.ofFloat(new float[]{f, f2}).setDuration((long) this.animTime);
            this.buttonAnim.addUpdateListener(new C03015());
            this.buttonAnim.start();
        }
    }

    public void setMax(long j) {
        this.max = j;
    }

    public void setProgress(long j) {
        float f = (float) j;
        this.progress = f;
        f /= (float) this.max;
        this.girthPro = 365.0f * f;
        invalidate();
        if (f >= 1.0f && this.onGestureListener != null) {
            this.onGestureListener.onReachMax();
        }
    }

    public void setSplit() {
        this.splitList.add(Float.valueOf(this.girthPro));
        invalidate();
    }

    public void deleteSplit() {
        if (this.isDeleteMode && this.splitList.size() > 0) {
            this.splitList.remove(this.splitList.size() - 1);
            this.isDeleteMode = false;
            invalidate();
        }
    }

    public void cleanSplit() {
        this.girthPro = 0.0f;
        if (this.splitList.size() > 0) {
            this.splitList.clear();
        }
        invalidate();
    }

    public void setDeleteMode(boolean z) {
        this.isDeleteMode = z;
        invalidate();
    }

    public boolean isDeleteMode() {
        return this.isDeleteMode;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.measuredWidth == -1) {
            this.measuredWidth = getMeasuredWidth();
            this.radius1 = (((float) this.measuredWidth) * this.zoom) / 2.0f;
            this.radius2 = ((((float) this.measuredWidth) * this.zoom) / 2.0f) - ((float) this.dp5);
            this.oval.left = (float) (this.dp5 / 2);
            this.oval.top = (float) (this.dp5 / 2);
            this.oval.right = (float) (this.measuredWidth - (this.dp5 / 2));
            this.oval.bottom = (float) (this.measuredWidth - (this.dp5 / 2));
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.rawX == -1.0f) {
            this.rawX = getX();
            this.rawY = getY();
        }
    }

    protected void onDraw(Canvas canvas) {
        this.paint.setColor(this.colorGray);
        canvas.drawCircle((float) (this.measuredWidth / 2), (float) (this.measuredWidth / 2), this.radius1, this.paint);
        this.paint.setColor(-1);
        canvas.drawCircle((float) (this.measuredWidth / 2), (float) (this.measuredWidth / 2), this.radius2, this.paint);
        canvas.drawArc(this.oval, 270.0f, this.girthPro, false, this.paintProgress);
        for (int i = 0; i < this.splitList.size(); i++) {
            if (i != 0) {
                canvas.drawArc(this.oval, 270.0f + ((Float) this.splitList.get(i)).floatValue(), 1.0f, false, this.paintSplit);
            }
        }
        if (this.isDeleteMode && this.splitList.size() > 0) {
            float floatValue = ((Float) this.splitList.get(this.splitList.size() - 1)).floatValue();
            canvas.drawArc(this.oval, 270.0f + floatValue, this.girthPro - floatValue, false, this.paintDelete);
        }
    }
}
