package tiko.tamk.fi.androidpaint;

import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by TomB on 22.4.2018.
 */

public class PaintRect {

    private RectF rectangle;
    private Paint paint;

    public PaintRect(RectF rectangle, Paint paint) {
        this.rectangle = rectangle;
        this.paint = paint;
    }

    public RectF getRectangle() {

        return rectangle;
    }

    public void setRectangle(RectF rectangle) {
        this.rectangle = rectangle;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
