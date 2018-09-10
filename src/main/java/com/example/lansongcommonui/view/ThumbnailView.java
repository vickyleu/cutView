package com.example.lansongcommonui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lansongsdk.commonFree.R;

public class ThumbnailView extends View {
    private Bitmap bitmap;
    private float downX;
    private int mHeight;
    private Paint mPaint;
    private int mWidth;
    private int minPx;
    private OnScrollBorderListener onScrollBorderListener;
    private RectF rectF;
    private RectF rectF2;
    private int rectWidth;
    boolean scrollChange;
    private boolean scrollLeft;
    private boolean scrollRight;

    public interface OnScrollBorderListener {
        void OnScrollBorder(float f, float f2);

        void onScrollStateChange();
    }

    public ThumbnailView(Context context) {
        super(context);
        init();
    }

    public ThumbnailView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public ThumbnailView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStrokeWidth((float) ((int) getResources().getDimension(R.dimen.dp5)));
        this.bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.video_thumbnail);
        this.rectWidth = (int) getResources().getDimension(R.dimen.dp10);
        this.minPx = this.rectWidth;
    }

    public void setMinInterval(int i) {
        if (this.mWidth > 0 && i > this.mWidth) {
            i = this.mWidth;
        }
        this.minPx = i;
    }

    public void setOnScrollBorderListener(OnScrollBorderListener onScrollBorderListener) {
        this.onScrollBorderListener = onScrollBorderListener;
    }

    public float getLeftInterval() {
        return this.rectF.left;
    }

    public float getRightInterval() {
        return this.rectF2.right;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mWidth == 0) {
            this.mWidth = getWidth();
            this.mHeight = getHeight();
            this.rectF = new RectF();
            this.rectF.left = 0.0f;
            this.rectF.top = 0.0f;
            this.rectF.right = (float) this.rectWidth;
            this.rectF.bottom = (float) this.mHeight;
            this.rectF2 = new RectF();
            this.rectF2.left = (float) (this.mWidth - this.rectWidth);
            this.rectF2.top = 0.0f;
            this.rectF2.right = (float) this.mWidth;
            this.rectF2.bottom = (float) this.mHeight;
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        move(motionEvent);
        return this.scrollLeft || this.scrollRight;
    }

    private boolean move(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                this.downX = motionEvent.getX();
                if (this.downX > this.rectF.left - ((float) (this.rectWidth / 2)) && this.downX < this.rectF.right + ((float) (this.rectWidth / 2))) {
                    this.scrollLeft = true;
                }
                if (this.downX > this.rectF2.left - ((float) (this.rectWidth / 2)) && this.downX < this.rectF2.right + ((float) (this.rectWidth / 2))) {
                    this.scrollRight = true;
                    break;
                }
            case 1:
            case 3:
                this.downX = 0.0f;
                this.scrollLeft = false;
                this.scrollRight = false;
                if (this.scrollChange && this.onScrollBorderListener != null) {
                    this.onScrollBorderListener.onScrollStateChange();
                }
                this.scrollChange = false;
                break;
            case 2:
                float x = motionEvent.getX();
                float f = x - this.downX;
                if (this.scrollLeft) {
                    this.rectF.left += f;
                    this.rectF.right += f;
                    if (this.rectF.left < 0.0f) {
                        this.rectF.left = 0.0f;
                        this.rectF.right = (float) this.rectWidth;
                    }
                    if (this.rectF.left > this.rectF2.right - ((float) this.minPx)) {
                        this.rectF.left = this.rectF2.right - ((float) this.minPx);
                        this.rectF.right = this.rectF.left + ((float) this.rectWidth);
                    }
                    this.scrollChange = true;
                    invalidate();
                } else if (this.scrollRight) {
                    this.rectF2.left += f;
                    this.rectF2.right += f;
                    if (this.rectF2.right > ((float) this.mWidth)) {
                        this.rectF2.right = (float) this.mWidth;
                        this.rectF2.left = this.rectF2.right - ((float) this.rectWidth);
                    }
                    if (this.rectF2.right < this.rectF.left + ((float) this.minPx)) {
                        this.rectF2.right = this.rectF.left + ((float) this.minPx);
                        this.rectF2.left = this.rectF2.right - ((float) this.rectWidth);
                    }
                    this.scrollChange = true;
                    invalidate();
                }
                if (this.onScrollBorderListener != null) {
                    this.onScrollBorderListener.OnScrollBorder(this.rectF.left, this.rectF2.right);
                }
                this.downX = x;
                break;
        }
        return true;
    }

    protected void onDraw(Canvas canvas) {
        this.mPaint.setColor(-1);
        Rect rect = new Rect();
        rect.left = (int) this.rectF.left;
        rect.top = (int) this.rectF.top;
        rect.right = (int) this.rectF.right;
        rect.bottom = (int) this.rectF.bottom;
        canvas.drawBitmap(this.bitmap, null, this.rectF, this.mPaint);
        rect = new Rect();
        rect.left = (int) this.rectF2.left;
        rect.top = (int) this.rectF2.top;
        rect.right = (int) this.rectF2.right;
        rect.bottom = (int) this.rectF2.bottom;
        canvas.drawBitmap(this.bitmap, null, this.rectF2, this.mPaint);
        Canvas canvas2 = canvas;
        canvas2.drawLine(this.rectF.left, 0.0f, this.rectF2.right, 0.0f, this.mPaint);
        canvas2.drawLine(this.rectF.left, (float) this.mHeight, this.rectF2.right, (float) this.mHeight, this.mPaint);
        this.mPaint.setColor(Color.parseColor("#99313133"));
        RectF rectF = new RectF();
        rectF.left = 0.0f;
        rectF.top = 0.0f;
        rectF.right = this.rectF.left;
        rectF.bottom = (float) this.mHeight;
        canvas.drawRect(rectF, this.mPaint);
        rectF = new RectF();
        rectF.left = this.rectF2.right;
        rectF.top = 0.0f;
        rectF.right = (float) this.mWidth;
        rectF.bottom = (float) this.mHeight;
        canvas.drawRect(rectF, this.mPaint);
    }
}
