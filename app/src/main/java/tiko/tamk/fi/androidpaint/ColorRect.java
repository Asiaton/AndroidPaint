package tiko.tamk.fi.androidpaint;

import android.graphics.RectF;

/**
 * Created by TomB on 22.4.2018.
 */

public class ColorRect extends ColorShape {

    private RectF rectangle;
    private RectangleShape shape;

    public ColorRect(int color, int strokeWidth, RectF rectangle, RectangleShape shape) {
        super(color, strokeWidth);
        this.rectangle = rectangle;
        this.shape = shape;
    }

    public RectF getRectangle() {
        return rectangle;
    }

    public RectangleShape getShape() {
        return shape;
    }
}
