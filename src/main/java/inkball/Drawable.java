package inkball;

/**
 * The Drawable interface should be implemented by any class whose instances
 * are intended to be drawn in the Inkball game. It provides a single method
 * `draw` that must be defined by any implementing class.
 */
public interface Drawable {

    /**
     * Draws the object on the screen.
     *
     * @param p The main game application object (of type App) that provides
     *          necessary drawing methods and context.
     */
    void draw(App p);
}
