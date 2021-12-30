package com.example.java_final_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
/**
 * VolumeControll
 * 커스텀뷰로 걸음기록에 대한 만족도 평가 시 사용된다.
 */
public class VolumeControll extends ImageView implements View.OnTouchListener {

    private double angle = 0.0;
    private knobListener lis;
    float x, y;
    float mx, my;

    public VolumeControll(Context context) {
        super(context);
        this.setImageResource(R.drawable.ic_baseline_swipe_24);
        this.setOnTouchListener(this);
    }

    public VolumeControll(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setImageResource(R.drawable.ic_baseline_swipe_24);
        this.setOnTouchListener(this);
    }

    public interface knobListener {
        void onChanged(double angle);
    }

    public void setknobListener(knobListener listener) {
        lis = listener;
    }

    public double getAngle(float x, float y) {
        mx = x - (getWidth() / 2.0f);
        my = (getHeight() / 2.0f) - y;
        double degree = Math.atan2(mx, my) * 180.0 / 3.141592;
        return degree;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        x = motionEvent.getX();
        y = motionEvent.getY();
        angle = getAngle(x, y);
        invalidate();
        lis.onChanged(angle);
        return false;
    }
    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        canvas.rotate((float) angle, getWidth() / 2, getHeight() / 2);
        super.onDraw(canvas);
    }
}
