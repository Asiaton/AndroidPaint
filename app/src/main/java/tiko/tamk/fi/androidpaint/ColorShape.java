package tiko.tamk.fi.androidpaint;

/**
 * Created by TomB on 22.4.2018.
 */

public class ColorShape {

    private int color;
    private int strokeWidth;

    public ColorShape(int color, int strokeWidth) {

        this.color = color;
        this.strokeWidth = strokeWidth;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
}
