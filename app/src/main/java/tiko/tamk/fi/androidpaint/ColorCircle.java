package tiko.tamk.fi.androidpaint;

/**
 * Class that saves the information necessary for saving drawn circles
 * into a List and drawing them on the canvas.
 */
public class ColorCircle extends ColorShape {

    /**
     * X-coordinate of the center of the circle.
     */
    private float x;

    /**
     * Y-coordinate of the center of the circle.
     */
    private float y;

    /**
     * Radius of the circle.
     */
    private float radius;

    /**
     * Constructs the object and sets its attributes.
     *
     * @param color The color in which the circle was drawn.
     * @param strokeWidth The width of the line with which the circle
     *                    was drawn.
     * @param x X-coordinate of the center of the circle.
     * @param y Y-coordinate of the center of the circle.
     * @param radius Radius of the circle.
     */
    public ColorCircle(int color, int strokeWidth, float x, float y, float radius) {
        super(color, strokeWidth);
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    /**
     * @return X-coordinate of the center of the circle.
     */
    public float getX() {
        return x;
    }

    /**
     * @return Y-coordinate of the center of the circle.
     */
    public float getY() {
        return y;
    }

    /**
     * @return Radius of the circle.
     */
    public float getRadius() {
        return radius;
    }
}
