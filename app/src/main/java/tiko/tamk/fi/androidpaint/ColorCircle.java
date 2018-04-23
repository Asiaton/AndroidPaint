package tiko.tamk.fi.androidpaint;

import android.graphics.PointF;

/**
 * Created by TomB on 22.4.2018.
 */

public class ColorCircle extends ColorShape {

    private float x;
    private float y;
    private float radius;

    public ColorCircle(int color, int strokeWidth, float x, float y, float radius) {
        super(color, strokeWidth);
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }
}
