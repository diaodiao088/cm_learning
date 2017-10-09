package core.display;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import core.assist.LoadedFrom;
import core.imageaware.ImageAware;

/**
 * Created by zhangdan on 2017/9/29.
 * comments:
 */

public class CircleBitmapDisplayer implements BitmapDisplayer {

    protected final Integer strokeColor;
    protected final float strokeWidth;

    public CircleBitmapDisplayer(){
        this(null);
    }

    public CircleBitmapDisplayer(Integer strokeColor) {
        this(strokeColor, 0);
    }

    public CircleBitmapDisplayer(Integer strokeColor , float strokeWidth){
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {


    }


    private class CircleDrawable extends Drawable{

        private float radius;

        private Paint mStrokePaint;
        private Paint mPaint;

        private RectF mBitmapRect = new RectF();
        private BitmapShader bitmapShader;

        public CircleDrawable(Bitmap bitmap , Integer strokeColor , Integer strokeWidth){
            radius = Math.min(bitmap.getWidth() / 2 , bitmap.getHeight() / 2);
            bitmapShader = new BitmapShader(bitmap , Shader.TileMode.CLAMP , Shader.TileMode.CLAMP);
            mBitmapRect = new RectF(0 , 0 , bitmap.getWidth() , bitmap.getHeight());

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setShader(bitmapShader);
            mPaint.setFilterBitmap(true);

            if (strokeWidth == 0){
                mStrokePaint = null;
            }else{
                mStrokePaint = new Paint();
                mStrokePaint.setStyle(Paint.Style.STROKE);
                mStrokePaint.setStrokeWidth(strokeWidth);
                mStrokePaint.setColor(strokeColor);
                mStrokePaint.setAntiAlias(true);
            }
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawCircle(radius , radius , radius , mPaint);
            if (mStrokePaint != null){
                canvas.drawCircle(radius , radius , strokeWidth , mStrokePaint);
            }
        }

        @Override
        public void setAlpha(int alpha) {
            mPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            mPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
