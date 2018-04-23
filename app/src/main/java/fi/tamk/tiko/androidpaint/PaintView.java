package fi.tamk.tiko.androidpaint;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Class that is used in MainActivity to draw all the lines and shapes.
 * Contains lists of saved lines and shapes, along with information of
 * current brush size, style and color.
 */
public class PaintView extends View {

    /**
     * The original size of the brush.
     */
    public static int BRUSH_SIZE = 20;

    /**
     * The default color of the brush.
     */
    public static final int DEFAULT_COLOR = Color.BLACK;

    /**
     * The default color of the background.
     */
    public static final int DEFAULT_BG_COLOR = Color.WHITE;

    /**
     * The amount the user has to move their finger for the app to draw.
     */
    private static final float TOUCH_TOLERANCE = 4;

    /**
     * Used as temporary x and y coordinates to aid with drawing.
     */
    private float mX, mY;

    /**
     * Used as a temporary Path to aid with drawing.
     */
    private Path mPath;

    /**
     * Used as a temporary Paint to aid with drawing.
     */
    private Paint mPaint;

    /**
     * List of DrawPath objects containing information of drawn lines.
     */
    private ArrayList<DrawPath> paths = new ArrayList<>();

    /**
     * List of ColorShape objects containing information of drawn shapes.
     */
    private ArrayList<ColorShape> shapes = new ArrayList<>();

    /**
     * The current color of the brush.
     */
    private int currentColor;

    /**
     * The current color of the background, initially set as white.
     */
    private int backgroundColor = DEFAULT_BG_COLOR;

    /**
     * The current width of drawn lines and shapes.
     */
    private int strokeWidth;

    /**
     * Whether lines will be drawn with emboss effect or not.
     */
    private boolean emboss;

    /**
     * Whether lines will be drawn with blur effect or not.
     */
    private boolean blur;

    /**
     * Whether the next touch will change color to color of touched pixel.
     */
    private boolean dropperActive = false;

    /**
     * Whether the next stroke will draw a rectangle.
     */
    private boolean drawRectangle = false;

    /**
     * Whether the next stroke will draw a circle.
     */
    private boolean drawCircle = false;

    /**
     * Whether the next stroke will draw a straight line.
     */
    private boolean drawLine = false;

    /**
     * Whether the next stroke will draw an oval.
     */
    private boolean drawOval = false;

    /**
     * Whether the next stroke will draw a round-cornered rectangle.
     */
    private boolean drawRoundedRectangle = false;

    /**
     * Filter that determines how emboss effect should be drawn.
     */
    private MaskFilter mEmboss;

    /**
     * Filter that determines how blur effect should be drawn.
     */
    private MaskFilter mBlur;

    /**
     * Beginning coordinate for drawing shapes. Registered at the
     * start of drawing a shape and used at the end to draw the shape.
     */
    private PointF beginCoordinate = new PointF();

    /**
     * Ending coordinate for drawing shapes. Registered at the
     * end of drawing a shape and used along with the beginning coordinate
     * to draw the shape.
     */
    private PointF endCoordinate = new PointF();

    /**
     * Bitmap that the canvas is created on and on which all the
     * lines and shapes are drawn.
     */
    private Bitmap mBitmap;

    /**
     * Bitmap that a loaded image is saved onto.
     */
    private Bitmap loadedBitmap;

    /**
     * Canvas that the bitmap is drawn on.
     */
    private Canvas mCanvas;

    /**
     * Paint object that is used for drawing bitmaps.
     */
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    /**
     * The shape of the brush with which lines are drawn.
     */
    private Paint.Cap currentCap = Paint.Cap.ROUND;

    /**
     * The width of the bitmap.
     */
    private int bitmapWidth;

    /**
     * The height of the bitmap.
     */
    private int bitmapHeight;

    /**
     * Default constructor without a given attribute set.
     *
     * @param context The context the object is created in.
     */
    public PaintView(Context context) {
        this(context, null);
    }

    /**
     * Constructs the object and sets the mPaint, mEmboss and mBlur attributes.
     *
     * @param context The context the object is created in.
     * @param attributeSet The set of attributes the object is created with.
     */
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

    /**
     * Initializes bitmap size, creates bitmaps and canvas, and sets
     * color and brush size.
     *
     * @param metrics Object that contains size of the display.
     */
    public void init(DisplayMetrics metrics) {
        bitmapHeight = metrics.heightPixels;
        bitmapWidth = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        loadedBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
    }

    /**
     * Removes emboss and blur effects from brush.
     */
    public void normal() {
        emboss = false;
        blur = false;
    }

    /**
     * Adds emboss effect to brush.
     */
    public void emboss() {
        emboss = true;
        blur = false;
    }

    /**
     * Adds blur effect to brush.
     */
    public void blur() {
        emboss = false;
        blur = true;
    }

    /**
     * Clears the PaintView by setting colors to original, clearing
     * all drawn lines and shapes, and creating new bitmaps and
     * canvas in stead of the old ones.
     */
    public void clear() {
        currentColor = DEFAULT_COLOR;
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        shapes.clear();
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        loadedBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        normal();
        invalidate();
    }

    /**
     * Removes last drawn line. If no lines are in the list, does nothing.
     */
    public void undoLine() {
        if (paths.size() > 0) {
            paths.remove(paths.size() - 1);
        }
        invalidate();
    }

    /**
     * Removes last drawn shape. If no shapes are in the list, does nothing.
     */
    public void undoShape() {
        if (shapes.size() > 0) {
            shapes.remove(shapes.size() - 1);
        }
        invalidate();
    }

    /**
     * Draws all lines, shapes and bitmaps on canvas.
     *
     * @param canvas Canvas that the bitmaps are drawn on.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(backgroundColor);

        // Goes through the paths list, sets mPaint attributes to match
        // with each path, and draws the path.
        for (DrawPath dp : paths) {
            mPaint.setColor(dp.getColor());
            mPaint.setStrokeWidth(dp.getStrokeWidth());
            mPaint.setStrokeCap(dp.getCap());
            mPaint.setMaskFilter(null);

            if (dp.getEmboss())
                mPaint.setMaskFilter(mEmboss);
            else if (dp.getBlur())
                mPaint.setMaskFilter(mBlur);

            mCanvas.drawPath(dp.getPath(), mPaint);
        }

        // Goes through the shapes list, sets mPaint attributes to match
        // with each shape, and draws the shape. Does conditional checks
        // to determine how each shape should be drawn.
        for (ColorShape shape : shapes) {
            mPaint.setColor(shape.getColor());
            mPaint.setStrokeWidth(shape.getStrokeWidth());
            if (shape instanceof ColorRect) {
                ColorRect temp = (ColorRect) shape;
                if (temp.getShape() == RectangleShape.NORMAL) {
                    mCanvas.drawRect(temp.getRectangle(), mPaint);
                } else if (temp.getShape() == RectangleShape.OVAL) {
                    mCanvas.drawOval(temp.getRectangle(), mPaint);
                } else if (temp.getShape() == RectangleShape.ROUNDED) {
                    mCanvas.drawRoundRect(temp.getRectangle(), 30, 30, mPaint);
                }
            } else if (shape instanceof  ColorCircle) {
                ColorCircle temp = (ColorCircle) shape;
                mCanvas.drawCircle(temp.getX(), temp.getY(), temp.getRadius(), mPaint);
            }
        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawBitmap(loadedBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    /**
     * Method that is called when starting to draw a new line. Creates
     * a new DrawPath object with current attributes and adds it to the
     * list of DrawPaths.
     *
     * @param x X-coordinate of touched spot.
     * @param y Y-coordinate of touched spot.
     */
    private void touchStart(float x, float y) {
        mPath = new Path();
        DrawPath dp = new DrawPath(currentColor, emboss, blur, strokeWidth, mPath, currentCap);
        paths.add(dp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    /**
     * Method that is called when finger is being moved to draw a new line.
     * Modifies the current DrawPath by modifying mPath, an attribute of it.
     *
     * @param x X-coordinate of touched spot.
     * @param y Y-coordinate of touched spot.
     */
    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    /**
     * Method that is called when finger is lifted from screen.
     * Ends DrawPath modification by drawing the line to its final location.
     */
    private void touchUp() {
        mPath.lineTo(mX, mY);
    }

    /**
     * Determines what the app should do with each touch event.
     * It will either draw a line, draw a shape or use the dropper
     * tool to get color of touched pixel.
     *
     * @param event The MotionEvent that the user did.
     * @return Whether the event was handled or not.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        // Use dropper tool
        if (dropperActive) {
            currentColor = mBitmap.getPixel((int) x, (int) y);
            dropperActive = false;

        // Draw shape
        } else if (drawRectangle ||
                drawCircle ||
                drawLine ||
                drawOval ||
                drawRoundedRectangle) {
            shapeDraw(event);

        // Draw line
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

    /**
     * Changes the brush shape between round and square.
     */
    public void changeStrokeShape() {
        if (currentCap == Paint.Cap.ROUND) {
            currentCap = Paint.Cap.SQUARE;
        } else {
            currentCap = Paint.Cap.ROUND;
        }
    }

    /**
     * Draws a shape depending on which boolean is active.
     *
     * @param event The MotionEvent that the user did.
     */
    public void shapeDraw(MotionEvent event) {
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

                // DRAW RECTANGLE
                if (drawRectangle ||
                        drawOval ||
                        drawRoundedRectangle) {

                    // Determine the shape of the rectangle.
                    RectangleShape rectangleShape;
                    if (drawRectangle) {
                        rectangleShape = RectangleShape.NORMAL;
                    } else if (drawOval) {
                        rectangleShape = RectangleShape.OVAL;
                    } else {
                        rectangleShape = RectangleShape.ROUNDED;
                    }

                    // Create new rectangle and add it to the shapes list.
                    shapes.add(new ColorRect(
                            currentColor,
                            strokeWidth,
                            new RectF(
                                    beginCoordinate.x,
                                    beginCoordinate.y,
                                    endCoordinate.x,
                                    endCoordinate.y),
                            rectangleShape));
                    drawRectangle = false;
                    drawOval = false;
                    drawRoundedRectangle = false;
                    invalidate();
                    break;

                // DRAW CIRCLE
                } else if (drawCircle) {
                    // Calculate the difference between the beginning and
                    // ending coordinates to calculate radius using the
                    // Pythagorean theorem.
                    float dx = beginCoordinate.x - endCoordinate.x;
                    float dy = beginCoordinate.y - endCoordinate.y;

                    // Shift the center of the circle to halfway
                    // between the beginning and ending points to make
                    // the two points define the circle's diameter instead
                    // of its radius.
                    beginCoordinate.x -= dx/2;
                    beginCoordinate.y -= dy/2;

                    // Halve the radius to make the circle the correct size.
                    float radius = (float) (Math.sqrt((dx * dx) + (dy * dy)) / 2);

                    // Add the circle to the shapes list.
                    shapes.add(new ColorCircle(
                            currentColor,
                            strokeWidth,
                            beginCoordinate.x,
                            beginCoordinate.y,
                            radius));

                    drawCircle = false;
                    invalidate();
                    break;

                // DRAW LINE
                } else if (drawLine) {
                    Path p = new Path();
                    p.moveTo(beginCoordinate.x, beginCoordinate.y);
                    p.lineTo(endCoordinate.x, endCoordinate.y);
                    paths.add(new DrawPath(
                            currentColor,
                            false,
                            false,
                            strokeWidth,
                            p,
                            currentCap
                    ));
                    drawLine = false;
                    invalidate();
                    break;
                }
                break;
        }
    }

    /**
     * Inserts given bitmap to the loadedBitmap attribute and makes
     * mCanvas use loadedBitmap for drawing.
     *
     * @param bmp Bitmap to be added to loadedBitmap.
     */
    public void loadBitmap(Bitmap bmp) {
        backgroundColor = Color.TRANSPARENT;
        loadedBitmap = Bitmap.createScaledBitmap(bmp, bitmapWidth, bitmapHeight, false);
        mCanvas.setBitmap(loadedBitmap);
    }

    /**
     * @return The current color of the brush.
     */
    public int getCurrentColor() {
        return currentColor;
    }

    /**
     * @param currentColor Color that is set to brush.
     */
    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }

    /**
     * @return The current color of the background.
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @param backgroundColor Color that is set to background.
     */
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidate();
    }

    /**
     * @param dropperActive Whether the dropper tool will be active.
     */
    public void setDropperActive(boolean dropperActive) {
        this.dropperActive = dropperActive;
    }

    /**
     * @param drawRectangle Whether the next stroke will draw a rectangle.
     */
    public void setDrawRectangle(boolean drawRectangle) {
        this.drawRectangle = drawRectangle;
    }

    /**
     * @param drawCircle Whether the next stroke will draw a circle.
     */
    public void setDrawCircle(boolean drawCircle) {
        this.drawCircle = drawCircle;
    }

    /**
     * @param drawLine Whether the next stroke will draw a line.
     */
    public void setDrawLine(boolean drawLine) {
        this.drawLine = drawLine;
    }

    /**
     * @param drawOval Whether the next stroke will draw an oval.
     */
    public void setDrawOval(boolean drawOval) {
        this.drawOval = drawOval;
    }

    /**
     * @param drawRoundedRectangle Whether the next stroke will draw a
     *                             round-cornered rectangle.
     */
    public void setDrawRoundedRectangle(boolean drawRoundedRectangle) {
        this.drawRoundedRectangle = drawRoundedRectangle;
    }

    /**
     * @param strokeWidth Number that is set as stroke width.
     */
    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
}
