package com.example.lansongcommonui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout.LayoutParams;

import com.lansongsdk.commonFree.R;
@SuppressLint("WrongConstant")
public class FocusImageView extends AppCompatImageView {
    public static final String TAG = "FocusImageView";
    private Animation mAnimation;
    private Handler mHandler;

    /* renamed from: com.example.lansongcommonui.view.FocusImageView$1 */
    class C02881 implements Runnable {
        C02881() {
        }

        public void run() {
            FocusImageView.this.setVisibility(8);
        }
    }


    public FocusImageView(Context context) {
        super(context);
        this.mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.focusview_show);
        setVisibility(8);
        this.mHandler = new Handler();
        setImageResource(R.drawable.focus_focused);
        setVisibility(8);
    }

    public FocusImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.focusview_show);
        this.mHandler = new Handler();
        setImageResource(R.drawable.focus_focused);
        setVisibility(8);
    }

    public void startFocus(int i, int i2) {
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        layoutParams.topMargin = i2 - (getMeasuredHeight() / 2);
        layoutParams.leftMargin = i - (getMeasuredWidth() / 2);
        setLayoutParams(layoutParams);
        setVisibility(0);
        startAnimation(this.mAnimation);
        this.mHandler.postDelayed(new C02881(), 80);
    }

    private AnimatorSet startAnimation(View view, long j, int i, long j2, float... fArr) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setStartDelay(j2);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "scaleY", fArr);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view, "scaleX", fArr);
        ofFloat.setDuration(j);
        ofFloat2.setDuration(j);
        ofFloat.setRepeatCount(i);
        ofFloat2.setRepeatCount(i);
        animatorSet.playTogether(new Animator[]{ofFloat, ofFloat2});
        animatorSet.start();
        return animatorSet;
    }
}
