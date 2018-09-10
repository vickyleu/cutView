package com.example.lansongcommonui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class TouchView extends View {
    private boolean clickable = true;
    private float coreX;
    private float coreY;
    private boolean doubleMove = false;
    private float downX;
    private float downY;
    private float firstX;
    private float firstY;
    private boolean isOutLimits;
    private float lastDis;
    float lastRota;
    private OnClickListener listener;
    private int maxHeight;
    private int maxWidth;
    private int maxX = -1;
    private int maxY = -1;
    private int minHeight;
    private int minWidth;
    private int minX = -1;
    private int minY = -1;
    private OnLimitsListener onLimitsListener;
    private OnTouchListener onTouchListener;
    View tempView;
    private float whRatio;

    public interface OnLimitsListener {
        void OnInnerLimits(float f, float f2);

        void OnOutLimits(float f, float f2);
    }

    public interface OnTouchListener {
        void onDown(TouchView touchView, MotionEvent motionEvent);

        void onMove(TouchView touchView, MotionEvent motionEvent);

        void onUp(TouchView touchView, MotionEvent motionEvent);
    }

    public TouchView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public TouchView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TouchView(Context context) {
        super(context);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.listener = onClickListener;
    }

    public void setClickable(boolean z) {
        this.clickable = z;
    }

    public void setLimitsX(int i, int i2) {
        this.minX = i;
        this.maxX = i2;
    }

    public void setLimitsY(int i, int i2) {
        this.minY = i;
        this.maxY = i2;
    }

    public void setOnLimitsListener(OnLimitsListener onLimitsListener) {
        this.onLimitsListener = onLimitsListener;
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public boolean isOutLimits() {
        return this.isOutLimits;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.minWidth == 0) {
            this.whRatio = (((float) getWidth()) * 1.0f) / ((float) getHeight());
            this.minWidth = getWidth() / 2;
            this.maxWidth = ((ViewGroup) getParent()).getWidth();
            this.minHeight = getHeight() / 2;
            this.maxHeight = (int) (((float) this.maxWidth) / this.whRatio);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action != 6) {
            float rawX;
            float rawY;
            switch (action) {
                case 0:
                    if (this.onTouchListener != null) {
                        this.onTouchListener.onDown(this, motionEvent);
                    }
                    rawX = motionEvent.getRawX();
                    this.downX = rawX;
                    this.firstX = rawX;
                    rawY = motionEvent.getRawY();
                    this.downY = rawY;
                    this.firstY = rawY;
                    this.coreX = ((float) (getWidth() / 2)) + getX();
                    this.coreY = ((float) (getHeight() / 2)) + getY();
                    break;
                case 1:
                    if (this.tempView != null) {
                        setAlpha(1.0f);
                        setRotation(this.tempView.getRotation());
                        ((ViewGroup) getParent()).removeView(this.tempView);
                    }
                    this.lastRota = 0.0f;
                    this.tempView = null;
                    this.doubleMove = false;
                    this.lastDis = 0.0f;
                    if (this.onTouchListener != null) {
                        this.onTouchListener.onUp(this, motionEvent);
                    }
                    rawX = motionEvent.getRawX();
                    rawY = motionEvent.getRawY();
                    if (Math.abs(rawX - this.firstX) < 10.0f && Math.abs(rawY - this.firstY) < 10.0f && this.clickable && this.listener != null) {
                        this.listener.onClick(this);
                        break;
                    }
                case 2:
                    action = motionEvent.getPointerCount();
                    if (action < 2) {
                        if (!this.doubleMove && action == 1) {
                            if (this.onTouchListener != null) {
                                this.onTouchListener.onMove(this, motionEvent);
                            }
                            rawX = motionEvent.getRawX();
                            rawY = motionEvent.getRawY();
                            if (!(rawX == -1.0f || rawY == -1.0f)) {
                                if (rawX <= ((float) this.minX) || rawX >= ((float) this.maxX) || rawY <= ((float) this.minY) || rawY >= ((float) this.maxY)) {
                                    if (this.onLimitsListener != null) {
                                        this.onLimitsListener.OnOutLimits(rawX, rawY);
                                    }
                                    this.isOutLimits = true;
                                } else if (rawX > ((float) this.minX) && rawX < ((float) this.maxX) && rawY > ((float) this.minY) && rawY < ((float) this.maxY)) {
                                    if (this.onLimitsListener != null) {
                                        this.onLimitsListener.OnInnerLimits(rawX, rawY);
                                    }
                                    this.isOutLimits = false;
                                }
                            }
                            float y = (rawY - this.downY) + getY();
                            setX((rawX - this.downX) + getX());
                            setY(y);
                            this.downX = rawX;
                            this.downY = rawY;
                            break;
                        }
                    }
                    this.doubleMove = true;
                    rawX = getSlideDis(motionEvent);
                    rawY = getRotation(motionEvent);
                    if (this.tempView == null) {
                        this.tempView = new View(getContext());
                        this.tempView.setX(getX());
                        this.tempView.setY(getY());
                        this.tempView.setRotation(getRotation());
                        this.tempView.setBackground(getBackground());
                        this.tempView.setLayoutParams(new LayoutParams(getWidth(), getHeight()));
                        ((ViewGroup) getParent()).addView(this.tempView);
                        setAlpha(0.0f);
                    } else {
                        float f = this.lastDis - rawX;
                        LayoutParams layoutParams = getLayoutParams();
                        layoutParams.width = (int) (((float) layoutParams.width) - f);
                        layoutParams.height = (int) (((float) layoutParams.height) - (f / this.whRatio));
                        if (layoutParams.width > this.maxWidth || layoutParams.height > this.maxHeight) {
                            layoutParams.width = this.maxWidth;
                            layoutParams.height = this.maxHeight;
                        } else if (layoutParams.width < this.minWidth || layoutParams.height < this.minHeight) {
                            layoutParams.width = this.minWidth;
                            layoutParams.height = this.minHeight;
                        }
                        setLayoutParams(layoutParams);
                        f = this.coreX - ((float) (getWidth() / 2));
                        float height = this.coreY - ((float) (getHeight() / 2));
                        setX(f);
                        setY(height);
                        this.tempView.setX(f);
                        this.tempView.setY(height);
                        LayoutParams layoutParams2 = this.tempView.getLayoutParams();
                        layoutParams2.width = layoutParams.width;
                        layoutParams2.height = layoutParams.height;
                        this.tempView.setLayoutParams(layoutParams2);
                        if (this.lastRota != 0.0f) {
                            this.tempView.setRotation(this.tempView.getRotation() - (this.lastRota - rawY));
                        }
                    }
                    this.lastRota = rawY;
                    this.lastDis = rawX;
                    break;
            }
        }
        return true;
    }

    private float getRotation(MotionEvent motionEvent) {
        return (float) Math.toDegrees(Math.atan2((double) (motionEvent.getY(0) - motionEvent.getY(1)), (double) (motionEvent.getX(0) - motionEvent.getX(1))));
    }

    private float getSlideDis(MotionEvent motionEvent) {
        float x = motionEvent.getX(0) - motionEvent.getX(1);
        float y = motionEvent.getY(0) - motionEvent.getY(1);
        return (float) Math.sqrt((double) ((x * x) + (y * y)));
    }
}
