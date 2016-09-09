package com.rxjava;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class ProgressCircleImageView extends ImageView {
    private static final String TAG = "ProgressImageView";

    public Bitmap mBitmap = null;
    private int mBorderWidth = 20;//边框默认十个像素点
    private int mAnimTime = 3000;//动画默认执行时间
    private int mColor = Color.YELLOW;//默认白色
    private float mCurrAngle;//当前角度
    private RectF mRect;
    private Paint mPaint;
    private Paint mBgPaint;
    private Animation.AnimationListener mListener;
    private boolean mStart;
    private Timer mTimer = new Timer();
    private boolean mResetImage;

    public ProgressCircleImageView(Context context) {
        this(context, null);
    }

    public ProgressCircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressCircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void onDraw(final Canvas canvas) {
        long l = System.currentTimeMillis();

        if (mBitmap == null || mResetImage) {
            mResetImage = false;
            try {
                BitmapDrawable drawable = (BitmapDrawable) getDrawable();
                if (drawable != null) {
                    Bitmap bitmap = drawable.getBitmap();//获取图片

                    Bitmap changeSize = changeSize(widthEqualHeight(bitmap), getWidth() - mBorderWidth, getHeight() - mBorderWidth);
                    mBitmap = transCicrale(changeSize);//变成圆形
                }
            } catch (ClassCastException e) {
//                e.printStackTrace();
                mBitmap = null;
            } catch (Exception e) {
//                e.printStackTrace();
                mBitmap = null;
            }
        }

        // 新建画笔
        if (mRect == null) {
            mRect = new RectF();
            mRect.set(mBorderWidth, mBorderWidth, getWidth() - mBorderWidth, getHeight() - mBorderWidth);

            //灰色圆框
            mBgPaint = new Paint();
//            mBgPaint.setColor(Color.parseColor("#757575"));
            mBgPaint.setColor(Color.parseColor("#FFFFFF"));
            mBgPaint.setStyle(Paint.Style.STROKE);//设置画笔类型为填充
            mBgPaint.setStrokeWidth(mBorderWidth);
            mBgPaint.setAntiAlias(true);//抗锯齿
            mBgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

            //白色圆框
            mPaint = new Paint();    //采用默认设置创建一个画笔
            mPaint.setColor(mColor);    //设置画笔的颜色为绿色
            mPaint.setStyle(Paint.Style.STROKE);//设置画笔类型为填充
            mPaint.setStrokeWidth(mBorderWidth);
            mPaint.setAntiAlias(true);//抗锯齿
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        }

        //先画图
        if (mBitmap != null)
            canvas.drawBitmap(mBitmap, mBorderWidth, mBorderWidth, null);
        //画进度背景
        canvas.drawArc(mRect, 0, 360, false, mBgPaint);
        //画进度
        canvas.drawArc(mRect, -90, mCurrAngle, false, mPaint);

        Log.i(TAG, "mCurrAngle:" + mCurrAngle + ", handle time:" + (System.currentTimeMillis() - l));

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mCurrAngle < 361 && mStart) {
                    invalidate();
                } else {
                    mCurrAngle = 0;
                    mStart = false;
                }
            }
        }, 16);
    }

    public void startProgress() {
        mStart = true;
        mCurrAngle = 0;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mCurrAngle < 361) {
                    mCurrAngle += (360f / mAnimTime) * 10;
                } else {
                    cancel();
                    if (mListener != null) {
                        mListener.onAnimationEnd(null);
                    }
                }
            }
        }, 10, 10);
        invalidate();

    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mResetImage = true;
    }

    /**
     * 传入一个普通Bitmap图像，返回一个圆形Bitmap图像
     *
     * @param bitmap 你要变成圆形的图片
     * @return ok变好了
     */
    private Bitmap transCicrale(Bitmap bitmap) {
        // 新建Bitmap,为正方形
        Bitmap bitmap2 = Bitmap.createBitmap(Math.min(bitmap.getWidth(), bitmap.getHeight()), Math.min(bitmap.getWidth(), bitmap.getHeight()), Config.ARGB_8888);

        // 新建画笔
        Paint paint = new Paint();
        paint.setStrokeWidth(0);

        // 新建画布
        Canvas canvas = new Canvas(bitmap2);

        canvas.drawCircle(bitmap.getWidth() / 2 - mBorderWidth / 2, bitmap.getHeight() / 2 - mBorderWidth / 2, Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2, paint);

        // 在新建的画布上吧Bitmap图像画上去，然后取交集并且Bitmap图像居上
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmap2;
    }

    private static Bitmap changeSize(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    private static Bitmap widthEqualHeight(Bitmap bitmap) {
        int bwidth = bitmap.getWidth();
        int bheight = bitmap.getHeight();
        int bmin = Math.min(bwidth, bheight);
        Matrix matrix = new Matrix();
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, bwidth / 2 - bmin / 2, bheight / 2 - bmin / 2, bmin,
                bmin, matrix, true);
        return resizeBmp;
    }

    public void setListener(Animation.AnimationListener mListener) {
        this.mListener = mListener;
    }
}
