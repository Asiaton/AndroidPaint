package tiko.tamk.fi.androidpaint;

/**
 * Created by TomB on 5.4.2018.
 */

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class PaintView extends View {

    public static int BRUSH_SIZE = 20;
    public static final int DEFAULT_COLOR = Color.BLACK;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<DrawPath> paths = new ArrayList<>();
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private boolean emboss;
    private boolean blur;
    private boolean dropperActive = false;
    private boolean drawRectangle = false;
    private boolean drawCircle = false;

    private MaskFilter mEmboss;
    private MaskFilter mBlur;

    private PointF beginCoordinate = new PointF();
    private PointF endCoordinate = new PointF();

    private ArrayList<ColorRect> rectangles = new ArrayList<>();
    private ArrayList<ColorCircle> circles = new ArrayList<>();

    private Bitmap mBitmap;
    private Bitmap loadedBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    private int bitmapWidth;
    private int bitmapHeight;

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

        mEmboss = new EmbossMaskFilter(new float[] {1, 1, 1}, 0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);
    }

    public void init(DisplayMetrics metrics) {
        bitmapHeight = metrics.heightPixels;
        bitmapWidth = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        loadedBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
    }

    public void normal() {
        emboss = false;
        blur = false;
    }

    public void emboss() {
        emboss = true;
        blur = false;
    }

    public void blur() {
        emboss = false;
        blur = true;
    }

    public void clear() {
        currentColor = DEFAULT_COLOR;
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        loadedBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        normal();
        invalidate();
    }

    public void undo() {
        paths.remove(paths.size()-1);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(backgroundColor);

        for (DrawPath dp : paths) {
            mPaint.setColor(dp.getColor());
            mPaint.setStrokeWidth(dp.getStrokeWidth());
            mPaint.setMaskFilter(null);

            if (dp.getEmboss())
                mPaint.setMaskFilter(mEmboss);
            else if (dp.getBlur())
                mPaint.setMaskFilter(mBlur);

            mCanvas.drawPath(dp.getPath(), mPaint);
        }

        for (ColorRect r : rectangles) {
            mPaint.setColor(r.getColor());
            mPaint.setStrokeWidth(r.getStrokeWidth());
            mCanvas.drawRect(r.getRectangle(), mPaint);
        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawBitmap(loadedBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        DrawPath dp = new DrawPath(currentColor, emboss, blur, strokeWidth, mPath);
        paths.add(dp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (dropperActive) {
            currentColor = mBitmap.getPixel((int) x, (int) y);
            dropperActive = false;
        } else if (drawRectangle) {
            rectangleDraw(event);
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchStart(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchMove(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touchUp();
                    invalidate();
                    break;
            }
        }

        return true;
    }

    public void rectangleDraw(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                beginCoordinate.x = x;
                beginCoordinate.y = y;
                endCoordinate.x = x;
                endCoordinate.y = y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                endCoordinate.x = x;
                endCoordinate.y = y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                rectangles.add(new ColorRect(
                        currentColor,
                        strokeWidth,
                        new RectF(
                                beginCoordinate.x,
                                beginCoordinate.y,
                                endCoordinate.x,
                                endCoordinate.y)));
                drawRectangle = false;
                invalidate();
                break;
        }
    }

    public void circleDraw(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                beginCoordinate.x = x;
                beginCoordinate.y = y;
                endCoordinate.x = x;
                endCoordinate.y = y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                endCoordinate.x = x;
                endCoordinate.y = y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                float dx = beginCoordinate.x - endCoordinate.x;
                float dy = beginCoordinate.y - endCoordinate.y;
                float radius = (float) Math.sqrt((dx * dx) + (dy * dy));
                circles.add(new ColorCircle(
                        currentColor,
                        strokeWidth,
                        new PointF(
                                beginCoordinate.x,
                                beginCoordinate.y),
                        radius));
                drawCircle = false;
                invalidate();
                break;
        }
    }

    public void loadBitmap(Bitmap bmp) {
        backgroundColor = Color.TRANSPARENT;
        loadedBitmap = Bitmap.createScaledBitmap(bmp, bitmapWidth, bitmapHeight, false);
        mCanvas.setBitmap(loadedBitmap);
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidate();
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public Canvas getmCanvas() {
        return mCanvas;
    }

    public void setmCanvas(Canvas mCanvas) {
        this.mCanvas = mCanvas;
    }

    public Bitmap getLoadedBitmap() {
        return loadedBitmap;
    }

    public void setLoadedBitmap(Bitmap bmp) {
        loadedBitmap = bmp;
    }

    public boolean getDropperActive() {
        return dropperActive;
    }

    public void setDropperActive(boolean dropperActive) {
        this.dropperActive = dropperActive;
    }

    public boolean getDrawRectangle() {
        return drawRectangle;
    }

    public void setDrawRectangle(boolean drawRectangle) {
        this.drawRectangle = drawRectangle;
    }
}
