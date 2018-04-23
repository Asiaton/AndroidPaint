package tiko.tamk.fi.androidpaint;

/**
 * Created by TomB on 5.4.2018.
 */

import android.graphics.Paint;
import android.graphics.Path;

public class DrawPath {

    private int color;
    private boolean emboss;
    private boolean blur;
    private int strokeWidth;
    private Path path;
    private Paint.Cap cap;

    public DrawPath(int color, boolean emboss, boolean blur, int strokeWidth, Path path, Paint.Cap cap) {
        this.color = color;
        this.emboss = emboss;
        this.blur = blur;
        this.strokeWidth = strokeWidth;
        this.path = path;
        this.cap = cap;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean getEmboss() {
        return emboss;
    }

    public boolean getBlur() {
        return blur;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public Path getPath() {
        return path;
    }

    public Paint.Cap getCap() {
        return cap;
    }
}
