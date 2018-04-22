package tiko.tamk.fi.androidpaint;

import android.graphics.Color;
import android.graphics.RectF;

/**
 * Created by TomB on 22.4.2018.
 */

public class ColorRect extends ColorShape {

    private RectF rectangle;

    public ColorRect(int color, int strokeWidth, RectF rectangle) {
        super(color, strokeWidth);
        this.rectangle = rectangle;
    }

    public RectF getRectangle() {
        return rectangle;
    }

}
