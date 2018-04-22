package tiko.tamk.fi.androidpaint;

import android.graphics.Color;
import android.graphics.RectF;

/**
 * Created by TomB on 22.4.2018.
 */

public class ColorRect {

    private RectF rectangle;
    private int color;
    private int strokeWidth;

    public ColorRect(RectF rectangle, int color, int strokeWidth) {
        this.rectangle = rectangle;
        this.color = color;
        this.strokeWidth = strokeWidth;
    }

    public RectF getRectangle() {
        return rectangle;
    }

    public int getColor() {
        return color;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }
}
