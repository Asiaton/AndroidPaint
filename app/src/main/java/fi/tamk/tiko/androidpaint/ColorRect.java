package fi.tamk.tiko.androidpaint;

import android.graphics.RectF;

/**
 * Class that saves the information necessary for saving drawn rectangles
 * into a List and drawing them on the canvas.
 */
public class ColorRect extends ColorShape {

    /**
     * RectF object that contains location of the rectangle in floats.
     */
    private RectF rectangle;

    /**
     * Enum that determines what kind of rectangle this rectangle is.
     */
    private RectangleShape shape;

    /**
     * Constructs the object and sets its attributes.
     *
     * @param color The color in which the rectangle was drawn.
     * @param strokeWidth The width of the line with which the
     *                    rectangle was drawn.
     * @param rectangle RectF object that contains location of
     *                  the rectangle in floats.
     * @param shape Enum that determines what kind of rectangle
     *              this rectangle is.
     */
    public ColorRect(int color, int strokeWidth, RectF rectangle, RectangleShape shape) {
        super(color, strokeWidth);
        this.rectangle = rectangle;
        this.shape = shape;
    }

    /**
     * @return RectF object that contains location of the rectangle in floats.
     */
    public RectF getRectangle() {
        return rectangle;
    }

    /**
     * @return Enum that determines what kind of rectangle this rectangle is.
     */
    public RectangleShape getShape() {
        return shape;
    }
}
