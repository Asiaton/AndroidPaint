package tiko.tamk.fi.androidpaint;

/**
 * Enum that is used for drawing ColorRect objects. ColorRect can be
 * used to draw several shapes that all require the same data to draw,
 * so this enum is used to be able to save them all as ColorRect objects.
 */
public enum RectangleShape {

    /**
     * Regular rectangle with right corners.
     */
    NORMAL,

    /**
     * Regular rectangle with rounded corners.
     */
    ROUNDED,

    /**
     * Oval that is contained inside given rectangle.
     */
    OVAL
}
