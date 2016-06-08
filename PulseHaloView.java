package com.animapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PulseHaloView extends View {

    private List<WaterCicle> mCicleces = new ArrayList<WaterCicle>();
    private boolean isRunning = false;
    private int mWaterLife = 1500;//一个水波动画总时长
    private int mColor = Color.RED;//颜色
    private float mRadius = 500;//一个水波纹半径
    private int mTimeRedraw = 16;//重画时长
    private int mWaterInterval = 100;//重画时长
    private Paint mPaint;

    public PulseHaloView(Context context) {
        super(context);
        init();
    }

    public PulseHaloView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PulseHaloView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPaint = new Paint();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画个圈圈
        for (int i = 0; i < mCicleces.size(); i++) {
            WaterCicle cicle = mCicleces.get(i);

            //全透明的不再绘画
            if (cicle.alpha < 0) {
                continue;
            }

            mPaint.setColor((int) cicle.color);
            mPaint.setAlpha((int) cicle.alpha);
            canvas.drawCircle(
                    getWidth() / 2,
                    getHeight() / 2,
                    cicle.radius + 10,
                    mPaint);

            //越画越大越透明
            if (cicle.alpha > 0) {
                cicle.alpha -= (255f / mWaterLife * mTimeRedraw);
                cicle.radius += (mRadius / mWaterLife * mTimeRedraw);
            }

        }

        //如果当前动画开启，而且圆圈个数不够了
        if (isRunning) {
            if (mCicleces.size() <= 0) {
                WaterCicle waterCicle = new WaterCicle();
                waterCicle.color = mColor;
                waterCicle.alpha = 255;
                waterCicle.radius = 0;
                mCicleces.add(waterCicle);

            } else {
                float minRadius = mRadius;
                for (WaterCicle waterCicle : mCicleces) {
                    minRadius = Math.min(minRadius, waterCicle.radius);
                }

                if (minRadius >= mWaterInterval) {
                    WaterCicle invalidCicle = null;

                    for (WaterCicle waterCicle : mCicleces) {
                        if (waterCicle.alpha <= 0) {
                            invalidCicle = waterCicle;
                            break;
                        }
                    }

                    if (invalidCicle == null) {
                        invalidCicle = new WaterCicle();
                        mCicleces.add(invalidCicle);
                    }

                    invalidCicle.color = mColor;
                    invalidCicle.alpha = 255;
                    invalidCicle.radius = 0;

                }



            }

        }

        //重新绘制
        postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        }, mTimeRedraw);
    }

    public void start() {
        isRunning = true;
    }

    public void stop() {
        isRunning = false;
    }

    /**
     * 调用该方法会增加一个水波纹
     */
    public void newStart() {
    }

    /**
     * 设置水波纹颜色
     *
     * @param color
     */
    public void setColor(int color) {
        mPaint.setColor(color);
    }

    /**
     * 一个水波纹的生命周期时间
     *
     * @param lifeTime
     */
    public void setCicleLifeTime(int lifeTime) {

    }

    /**
     * 多长时间创建一个水波纹
     *
     * @param createTime
     */
    public void setCicleCreateTime(int createTime) {

    }

    /**
     * 设置水波纹的半径
     *
     * @param radius
     */
    public void setRadius(int radius) {

    }

    public boolean isRunning() {
        return isRunning;
    }

    class WaterCicle {
        float color;
        float alpha;
        float radius;

        public WaterCicle() {
            Log.i("PulseHaloView", "hashCode:" + this.hashCode());
        }

        @Override
        public String toString() {
            return "WaterCicle{" +
                    "color=" + color +
                    ", alpha=" + alpha +
                    ", radius=" + radius +
                    '}';
        }
    }

}

