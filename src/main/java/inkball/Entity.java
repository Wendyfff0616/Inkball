package inkball;

/**
 * The Entity class serves as an abstract base class for all objects in the Inkball game
 * that have a position (x, y) and a color. This class provides a foundation for
 * other game objects like Balls and Walls, which will extend it and implement
 * the abstract method to load images.
 */
public abstract class Entity {
    protected int x;
    protected int y;
    protected int color;

    /**
     * Constructs an Entity with specified position and color.
     *
     * @param x     The initial x-coordinate of the entity.
     * @param y     The initial y-coordinate of the entity.
     * @param color The color of the entity, represented as an integer.
     */
    public Entity(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    /**
     * Abstract method to load the image of the entity.
     * This method must be implemented by any subclass to provide the specific image loading logic.
     *
     * @param p The main game application object (of type App) used to load images.
     */
    public abstract void loadImage(App p);
}
