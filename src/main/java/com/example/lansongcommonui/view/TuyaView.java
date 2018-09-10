package com.example.lansongcommonui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lansongsdk.commonFree.R;

import java.util.ArrayList;
import java.util.List;

public class TuyaView extends View {
    private boolean isDrawMode;
    private Paint mPaint;
    private Path mPath;
    /* renamed from: mX */
    private float f32mX;
    /* renamed from: mY */
    private float f33mY;
    private OnLineChangeListener onLineChangeListener;
    private OnTouchListener onTouchListener;
    private List<Paint> paintList = new ArrayList();
    private List<Path> savePathList = new ArrayList();
    private boolean touchMode;

    public interface OnLineChangeListener {
        void onDeleteLine(int i);

        void onDrawLine(int i);
    }

    public interface OnTouchListener {
        void onDown();

        void onUp();
    }

    public TuyaView(Context context) {
        super(context);
        init();
    }

    public TuyaView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public TuyaView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public void setNewPaintColor(int i) {
        this.mPaint.setColor(i);
    }

    public int getPathSum() {
        return this.savePathList.size();
    }

    public Paint newPaint(int i) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(getResources().getDimension(R.dimen.dp3));
        paint.setStyle(Style.STROKE);
        paint.setColor(i);
        return paint;
    }

    private void init() {
        this.mPaint = newPaint(-1);
        this.mPath = new Path();
    }

    public void setDrawMode(boolean z) {
        this.isDrawMode = z;
    }

    public void backPath() {
        if (this.savePathList.size() != 0) {
            if (this.savePathList.size() == 1) {
                this.mPath.reset();
                this.savePathList.clear();
                this.paintList.clear();
            } else {
                this.savePathList.remove(this.savePathList.size() - 1);
                this.paintList.remove(this.paintList.size() - 1);
                this.mPath = (Path) this.savePathList.get(this.savePathList.size() - 1);
                this.mPaint = (Paint) this.paintList.get(this.paintList.size() - 1);
            }
            if (this.onLineChangeListener != null) {
                this.onLineChangeListener.onDeleteLine(this.savePathList.size());
            }
        }
        invalidate();
    }

    public void setOnLineChangeListener(OnLineChangeListener onLineChangeListener) {
        this.onLineChangeListener = onLineChangeListener;
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.isDrawMode) {
            switch (motionEvent.getAction()) {
                case 0:
                    this.touchMode = true;
                    touchDown(motionEvent);
                    if (this.onTouchListener != null) {
                        this.onTouchListener.onDown();
                        break;
                    }
                    break;
                case 1:
                case 3:
                    this.touchMode = false;
                    this.savePathList.add(new Path(this.mPath));
                    this.paintList.add(new Paint(this.mPaint));
                    if (this.onTouchListener != null) {
                        this.onTouchListener.onUp();
                    }
                    if (this.onLineChangeListener != null) {
                        this.onLineChangeListener.onDrawLine(this.savePathList.size());
                        break;
                    }
                    break;
                case 2:
                    touchMove(motionEvent);
                    break;
            }
            invalidate();
        }
        return this.isDrawMode;
    }

    private void touchDown(MotionEvent motionEvent) {
        this.mPath = new Path();
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        this.f32mX = x;
        this.f33mY = y;
        this.mPath.moveTo(x, y);
    }

    private void touchMove(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float f = this.f32mX;
        float f2 = this.f33mY;
        f = Math.abs(x - f);
        f2 = Math.abs(y - f2);
        if (f >= 3.0f || f2 >= 3.0f) {
            this.mPath.lineTo(x, y);
            this.f32mX = x;
            this.f33mY = y;
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < this.savePathList.size(); i++) {
            canvas.drawPath((Path) this.savePathList.get(i), (Paint) this.paintList.get(i));
        }
        if (this.touchMode) {
            canvas.drawPath(this.mPath, this.mPaint);
        }
    }
}
