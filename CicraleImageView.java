package com.example.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class CicraleImageView extends ImageView {
	private static final String TAG = "CicraleView";
	public Bitmap bitmap = null;
	
	public CicraleView(Context context) {
		this(context, null);
	}

	public CicraleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CicraleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		try{
			BitmapDrawable drawable = (BitmapDrawable) getDrawable();
			bitmap = drawable.getBitmap();//获取图片
		}catch (ClassCastException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if(bitmap == null){
			return;
		}
		
		//布局宽高
		int width = getWidth();
		int height = getHeight();
		int min = Math.min(width, height);
//		Log.i(TAG, "布局 width:"+width+",height:"+height+",min:"+min);
		
		// 图片宽高
		int bwidth = bitmap.getWidth();
		int bheight = bitmap.getHeight();
		int bmin = Math.min(bwidth, bheight);
		int bmax = Math.max(bwidth, bheight);
//		Log.i(TAG, "图片 width:"+bwidth+",height:"+bheight+",min:"+bmin);
		
		Bitmap zhengfang = widthEqualHeight(bitmap);//变成正方形
		Bitmap transCicrale = transCicrale(zhengfang);//变成圆形
		int cwidth = transCicrale.getWidth();
		int cheight = transCicrale.getHeight();
		int cmin = Math.min(cwidth, cheight);
		
		// 新建画笔
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		canvas.drawCircle(width / 2, height / 2, min/2, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
		canvas.drawBitmap(changeSize(transCicrale,(float)min/(float)cmin), 0, 0, null);
		
	}

	/**
	 * 传入一个普通Bitmap图像，返回一个圆形Bitmap图像
	 * 
	 * @param bitmap
	 *            你要变成圆形的图片
	 * @return ok变好了
	 */
	private Bitmap transCicrale(Bitmap bitmap) {
		// 布局宽高
		int width = getWidth();
		int height = getHeight();
		int min = Math.min(width, height);
//		Log.i(TAG, "布局 width:"+width+",height:"+height+",min:"+min);
		
		// 图片宽高
		int bwidth = bitmap.getWidth();
		int bheight = bitmap.getHeight();
		int bmin = Math.min(bwidth, bheight);
//		Log.i(TAG, "图片 width:"+bwidth+",height:"+bheight+",min:"+bmin);
		
		// 新建Bitmap,为正方形
		Bitmap bitmap2 = Bitmap.createBitmap(bwidth, bheight, Config.ARGB_8888);
		int bwidth2 = bitmap2.getWidth();
		int bheight2 = bitmap2.getHeight();
		int bmin2 = Math.min(bwidth2, bheight2);
//		Log.i(TAG, "新建Bitmap width:"+bwidth2+",height:"+bheight2+",min:"+bmin2);
		
		// 新建画笔
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);

		// 新建画布
		Canvas canvas = new Canvas(bitmap2);
//		Log.i(TAG, "画布 width:"+canvas.getWidth()+",height:"+canvas.getHeight()+",min:"+Math.min(canvas.getWidth(), canvas.getHeight()));
		canvas.drawCircle(bwidth2 / 2, bheight2 / 2, (bmin2/2)-(bmin2/35), paint);

		// 在新建的画布上吧Bitmap图像画上去，然后取交集并且Bitmap图像居上
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		
		canvas.drawBitmap(bitmap, 0, 0, paint);
//		Log.i(TAG, "变圆后Bitmap width:"+bwidth2+",height:"+bheight2+",min:"+bmin2);
		return bitmap2;
	}

	private static Bitmap changeSize(Bitmap bitmap, float scale) {
//		Log.i(TAG, "缩放比例为："+scale);
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
//		Log.i(TAG, "图片缩放后 width:"+resizeBmp.getWidth()+",height:"+resizeBmp.getHeight());
		return resizeBmp;
	}
	
	private static Bitmap widthEqualHeight(Bitmap bitmap) {
		int bwidth = bitmap.getWidth();
		int bheight = bitmap.getHeight();
		int bmin = Math.min(bwidth, bheight);
		Matrix matrix = new Matrix();
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, bwidth/2-bmin/2, bheight/2-bmin/2, bmin,
				bmin, matrix, true);
//		Log.i(TAG, "图片变方后 width:"+resizeBmp.getWidth()+",height:"+resizeBmp.getHeight());
		return resizeBmp;
	}

}
