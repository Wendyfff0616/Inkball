package inkball;

import processing.core.PImage;

/**
 * The Spawner class represents a spawning point in the Inkball game where balls are generated.
 * Each spawner has a position, size, and associated image, and it can be drawn on the game screen.
 */
public class Spawner extends Entity implements Drawable {
    PImage spawnerImage;

    /**
     * Constructor for creating a Spawner object.
     *
     * @param x     The x-coordinate of the spawner's position.
     * @param y     The y-coordinate of the spawner's position.
     * @param color The color associated with the spawner (currently unused).
     * @param p     The main game application instance used for loading the image.
     */
    public Spawner(int x, int y, int color, App p) {
        super(x, y, color);

        loadImage(p);
    }

    /**
     * Loads the image for the spawner.
     *
     * @param p The main game application instance used to load the image.
     */
    @Override
    public void loadImage(App p) {
        spawnerImage = p.loadImage("inkball/entrypoint.png");
    }

    /**
     * Draws the spawner at its current position on the game screen.
     *
     * @param p The main game application instance used for rendering.
     */
    @Override
    public void draw(App p) {
        p.image(spawnerImage, x, y);
    }

    // Getter and setter methods
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}
