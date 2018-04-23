package tiko.tamk.fi.androidpaint;

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
}
