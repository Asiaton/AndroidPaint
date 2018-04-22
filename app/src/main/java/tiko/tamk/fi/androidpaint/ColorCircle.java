package tiko.tamk.fi.androidpaint;

import android.graphics.PointF;

/**
 * Created by TomB on 22.4.2018.
 */

public class ColorCircle extends ColorShape {

    private PointF pointF;
    private float radius;

    public ColorCircle(int color, int strokeWidth, PointF pointF, float radius) {
        super(color, strokeWidth);
        this.pointF = pointF;
        this.radius = radius;
    }

    public PointF getPointF() {
        return pointF;
    }

    public void setPointF(PointF pointF) {
        this.pointF = pointF;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
