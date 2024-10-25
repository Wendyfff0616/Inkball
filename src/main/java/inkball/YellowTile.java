package inkball;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * The YellowTile class represents a yellow tile that moves around the perimeter of the game board.
 * It handles the movement logic to ensure the tile moves clockwise and manages its visual representation.
 */
public class YellowTile extends Entity implements Drawable {
    private PImage yellowTileImage;
    /** The direction of movement: 0=right, 1=down, 2=left, 3=up */
    private int direction;

    /**
     * Constructs a YellowTile object with a starting position and image.
     *
     * @param x                The initial x-coordinate.
     * @param y                The initial y-coordinate.
     * @param color            The color index (if needed).
     * @param p                The App instance used to load images.
     */
    public YellowTile(int x, int y, int color, App p) {
        super(x, y, color);
        this.x = x;
        this.y = y;
        this.direction = 0; // Start moving to the right
        loadImage(p);
    }

    /**
     * Loads the image for the yellow tile.
     *
     * @param p The App instance used to load the image.
     */
    public void loadImage(App p) {
        yellowTileImage = p.loadImage("inkball/wall4.png");
    }

    /**
     * Draws the yellow tile on the game screen.
     *
     * @param p The App instance used for rendering.
     */
    @Override
    public void draw(App p) {
        p.image(yellowTileImage, x, y, App.CELLSIZE, App.CELLSIZE);
    }

    /**
     * Updates the position of the yellow tile, moving it clockwise around the perimeter.
     *
     * @param p The PApplet instance used for accessing global properties.
     */
    public void update(App p) {

        switch (direction) {
            case 0: // Moving right
                x += App.CELLSIZE;
                if (x >= App.WIDTH - App.CELLSIZE) {
                    x = App.WIDTH - App.CELLSIZE;
                    direction = 1; // Change direction to down
                }
                break;
            case 1: // Moving down
                y += App.CELLSIZE;
                if (y >= App.HEIGHT - App.CELLSIZE) {
                    y = App.HEIGHT - App.CELLSIZE;
                    direction = 2; // Change direction to left
                }
                break;
            case 2: // Moving left
                x -= App.CELLSIZE;
                if (x <= 0) {
                    x = 0;
                    direction = 3; // Change direction to up
                }
                break;
            case 3: // Moving up
                y -= App.CELLSIZE;
                if (y <= App.TOPBAR) {
                    y = App.TOPBAR;
                    direction = 0; // Change direction to right
                }
                break;
        }
    }

    // Getters and Setters
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getDirection() {
        return direction;
    }
    public void setDirection(int direction) {
        this.direction = direction;
    }
    public PImage getYellowTileImage() {
        return yellowTileImage;
    }
    public void setYellowTileImage(PImage yellowTileImage) {
        this.yellowTileImage = yellowTileImage;
    }
    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }
}
