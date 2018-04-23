package fi.tamk.tiko.androidpaint;

/**
 * Super class used to pass its methods and attributes to subclasses,
 * and also to create a List that can contain all kinds of shapes.
 */
public class ColorShape {

    /**
     * The color in which the shape was drawn.
     */
    private int color;

    /**
     * The width of the line with which the shape was drawn.
     */
    private int strokeWidth;

    /**
     * Constructs the object and sets its attributes.
     *
     * @param color The color in which the shape was drawn.
     * @param strokeWidth The width of the line with which the shape was drawn.
     */
    public ColorShape(int color, int strokeWidth) {
        this.color = color;
        this.strokeWidth = strokeWidth;
    }

    /**
     * @return The color in which the shape was drawn.
     */
    public int getColor() {
        return color;
    }

    /**
     * @return The width of the line with which the shape was drawn.
     */
    public int getStrokeWidth() {
        return strokeWidth;
    }
}
