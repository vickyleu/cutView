package com.example.lansongcommonui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lansongsdk.commonFree.R;

public class CutView extends View {
    private int cornerLength;
    float downX;
    float downY;
    private int dp1;
    private int dp3;
    boolean isBottom;
    boolean isLeft;
    boolean isMove;
    boolean isRight;
    boolean isTop;
    float lastSlideX;
    float lastSlideY;
    private float marginBottom;
    private float marginLeft;
    private float marginRight;
    private float marginTop;
    private int measuredHeight;
    private int measuredWidth;
    private Paint paint;
    float rectBottom;
    float rectLeft;
    float rectRight;
    float rectTop;

    public CutView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public CutView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public CutView(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.marginLeft = (float) ((int) getResources().getDimension(R.dimen.dp30));
        this.marginRight = (float) ((int) getResources().getDimension(R.dimen.dp30));
        this.marginTop = (float) ((int) getResources().getDimension(R.dimen.dp30));
        this.marginBottom = (float) ((int) getResources().getDimension(R.dimen.dp80));
        this.dp3 = (int) getResources().getDimension(R.dimen.dp3);
        this.dp1 = (int) getResources().getDimension(R.dimen.dp1);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setColor(-1);
        this.paint.setStyle(Style.STROKE);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                this.downX = motionEvent.getX();
                this.downY = motionEvent.getY();
                if (Math.abs(this.rectLeft - this.downX) < ((float) this.cornerLength)) {
                    this.isLeft = true;
                } else if (Math.abs(this.rectRight - this.downX) < ((float) this.cornerLength)) {
                    this.isRight = true;
                }
                if (Math.abs(this.rectTop - this.downY) < ((float) this.cornerLength)) {
                    this.isTop = true;
                } else if (Math.abs(this.rectBottom - this.downY) < ((float) this.cornerLength)) {
                    this.isBottom = true;
                }
                if (!(this.isLeft || this.isTop || this.isRight || this.isBottom)) {
                    this.isMove = true;
                    break;
                }
            case 1:
            case 3:
                this.isLeft = false;
                this.isRight = false;
                this.isTop = false;
                this.isBottom = false;
                this.isMove = false;
                break;
            case 2:
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                float f = (x - this.downX) + this.lastSlideX;
                float f2 = (y - this.downY) + this.lastSlideY;
                if (this.isMove) {
                    this.rectLeft += f;
                    this.rectRight += f;
                    this.rectTop += f2;
                    this.rectBottom += f2;
                    if (this.rectLeft < this.marginLeft || this.rectRight > ((float) this.measuredWidth) - this.marginRight) {
                        this.rectLeft -= f;
                        this.rectRight -= f;
                    }
                    if (this.rectTop < this.marginTop || this.rectBottom > ((float) this.measuredHeight) - this.marginBottom) {
                        this.rectTop -= f2;
                        this.rectBottom -= f2;
                    }
                } else {
                    if (this.isLeft) {
                        this.rectLeft += f;
                        if (this.rectLeft < this.marginLeft) {
                            this.rectLeft = this.marginLeft;
                        }
                        if (this.rectLeft > this.rectRight - ((float) (this.cornerLength * 2))) {
                            this.rectLeft = this.rectRight - ((float) (this.cornerLength * 2));
                        }
                    } else if (this.isRight) {
                        this.rectRight += f;
                        if (this.rectRight > ((float) this.measuredWidth) - this.marginRight) {
                            this.rectRight = ((float) this.measuredWidth) - this.marginRight;
                        }
                        if (this.rectRight < this.rectLeft + ((float) (this.cornerLength * 2))) {
                            this.rectRight = this.rectLeft + ((float) (this.cornerLength * 2));
                        }
                    }
                    if (this.isTop) {
                        this.rectTop += f2;
                        if (this.rectTop < this.marginTop) {
                            this.rectTop = this.marginTop;
                        }
                        if (this.rectTop > this.rectBottom - ((float) (this.cornerLength * 2))) {
                            this.rectTop = this.rectBottom - ((float) (this.cornerLength * 2));
                        }
                    } else if (this.isBottom) {
                        this.rectBottom += f2;
                        if (this.rectBottom > ((float) this.measuredHeight) - this.marginBottom) {
                            this.rectBottom = ((float) this.measuredHeight) - this.marginBottom;
                        }
                        if (this.rectBottom < this.rectTop + ((float) (this.cornerLength * 2))) {
                            this.rectBottom = this.rectTop + ((float) (this.cornerLength * 2));
                        }
                    }
                }
                invalidate();
                this.downX = x;
                this.downY = y;
                break;
        }
        return true;
    }

    public float[] getCutArr() {
        return new float[]{this.rectLeft - this.marginLeft, this.rectTop - this.marginTop, this.rectRight - this.marginLeft, this.rectBottom - this.marginTop};
    }

    public int getRectWidth() {
        return (int) ((((float) this.measuredWidth) - this.marginLeft) - this.marginRight);
    }

    public int getRectHeight() {
        return (int) ((((float) this.measuredHeight) - this.marginTop) - this.marginBottom);
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.measuredWidth == 0) {
            initParams();
        }
    }

    public void setMargin(float f, float f2, float f3, float f4) {
        this.marginLeft = f;
        this.marginTop = f2;
        this.marginRight = f3;
        this.marginBottom = f4;
        initParams();
        invalidate();
    }

    private void initParams() {
        this.measuredWidth = getMeasuredWidth();
        this.measuredHeight = getMeasuredHeight();
        this.cornerLength = this.measuredWidth / 10;
        this.rectLeft = this.marginLeft;
        this.rectRight = ((float) this.measuredWidth) - this.marginRight;
        this.rectTop = this.marginTop;
        this.rectBottom = ((float) this.measuredHeight) - this.marginBottom;
    }

    protected void onDraw(Canvas canvas) {
        this.paint.setStrokeWidth((float) this.dp1);
        canvas.drawRect(this.rectLeft, this.rectTop, this.rectRight, this.rectBottom, this.paint);
        drawLine(canvas, this.rectLeft, this.rectTop, this.rectRight, this.rectBottom);
    }

    private void drawLine(Canvas canvas, float f, float f2, float f3, float f4) {
        this.paint.setStrokeWidth(1.0f);
        float f5 = (f3 - f) / 3.0f;
        float f6 = f5 + f;
        canvas.drawLine(f6, f2, f6, f4, this.paint);
        float f7 = (f5 * 2.0f) + f;
        Canvas canvas2 = canvas;
        canvas2.drawLine(f7, f2, f7, f4, this.paint);
        f5 = (f4 - f2) / 3.0f;
        float f8 = f5 + f2;
        canvas2.drawLine(f, f8, f3, f8, this.paint);
        f6 = (f5 * 2.0f) + f2;
        canvas.drawLine(f, f6, f3, f6, this.paint);
        this.paint.setStrokeWidth((float) this.dp3);
        Canvas canvas3 = canvas;
        float f9 = f2;
        canvas3.drawLine(f - ((float) (this.dp3 / 2)), f9, f + ((float) this.cornerLength), f2, this.paint);
        canvas3.drawLine(f, f9, f, f2 + ((float) this.cornerLength), this.paint);
        canvas3.drawLine(f3 + ((float) (this.dp3 / 2)), f9, f3 - ((float) this.cornerLength), f2, this.paint);
        canvas3.drawLine(f3, f9, f3, f2 + ((float) this.cornerLength), this.paint);
        Canvas canvas4 = canvas;
        float f10 = f4;
        canvas4.drawLine(f, f10, f, f4 - ((float) this.cornerLength), this.paint);
        float f11 = f4;
        canvas4.drawLine(f - ((float) (this.dp3 / 2)), f10, f + ((float) this.cornerLength), f11, this.paint);
        canvas4.drawLine(f3 + ((float) (this.dp3 / 2)), f10, f3 - ((float) this.cornerLength), f11, this.paint);
        canvas4.drawLine(f3, f10, f3, f4 - ((float) this.cornerLength), this.paint);
    }
}
