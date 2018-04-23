package fi.tamk.tiko.androidpaint;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Class that is used to save lines drawn on canvas. Contains information
 * about the color, style, width and location of the lines.
 */
public class DrawPath {

    /**
     * The color in which the line was drawn.
     */
    private int color;

    /**
     * Whether the line had an emboss effect applied to it or not.
     */
    private boolean emboss;

    /**
     * Whether the line had a blur effect applied to it or not.
     */
    private boolean blur;

    /**
     * The width of the drawn line.
     */
    private int strokeWidth;

    /**
     * The path of the drawn line.
     */
    private Path path;

    /**
     * The paint cap that was used to draw the line (round or square).
     */
    private Paint.Cap cap;

    /**
     * Constructs the object and sets its attributes.
     *
     * @param color The color in which the line was drawn.
     * @param emboss Whether the line had an emboss effect applied to it or not.
     * @param blur Whether the line had a blur effect applied to it or not.
     * @param strokeWidth The width of the drawn line.
     * @param path The path of the drawn line.
     * @param cap The paint cap that was used to draw the line (round or square).
     */
    public DrawPath(int color, boolean emboss, boolean blur, int strokeWidth, Path path, Paint.Cap cap) {
        this.color = color;
        this.emboss = emboss;
        this.blur = blur;
        this.strokeWidth = strokeWidth;
        this.path = path;
        this.cap = cap;
    }

    /**
     * @return The color in which the line was drawn.
     */
    public int getColor() {
        return color;
    }

    /**
     * @return Whether the line had an emboss effect applied to it or not.
     */
    public boolean getEmboss() {
        return emboss;
    }

    /**
     * @return Whether the line had a blur effect applied to it or not.
     */
    public boolean getBlur() {
        return blur;
    }

    /**
     * @return The width of the drawn line.
     */
    public int getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * @return The path of the drawn line.
     */
    public Path getPath() {
        return path;
    }

    /**
     * @return The paint cap that was used to draw the line (round or square).
     */
    public Paint.Cap getCap() {
        return cap;
    }
}
