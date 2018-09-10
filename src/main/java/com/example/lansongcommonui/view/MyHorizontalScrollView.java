package com.example.lansongcommonui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class MyHorizontalScrollView extends HorizontalScrollView {
    private int currX;
    private OnScrollXListener onScrollXListener;
    private int scrollX;

    public interface OnScrollXListener {
        void onScrollStateChange();

        void onScrollX(int i);
    }

    public MyHorizontalScrollView(Context context) {
        super(context);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        if (this.onScrollXListener != null) {
            this.onScrollXListener.onScrollX(i);
        }
        this.scrollX = i;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if ((action == 1 || action == 3) && this.currX != this.scrollX) {
            this.currX = this.scrollX;
            if (this.onScrollXListener != null) {
                this.onScrollXListener.onScrollStateChange();
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    public int getScroll() {
        return this.scrollX;
    }

    public void setOnScrollXListener(OnScrollXListener onScrollXListener) {
        this.onScrollXListener = onScrollXListener;
    }
}
