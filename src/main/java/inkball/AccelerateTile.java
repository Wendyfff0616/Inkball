package inkball;

import processing.core.PImage;

/**
 * Represents an acceleration tile that speeds up the ball in a specific direction when it passes over.
 */
public class AccelerateTile extends Entity implements Drawable {

    PImage tileImage;

    /** The direction of acceleration ("up", "down", "left", "right"). */
    private String direction;
    public static final float ACCELERATION_AMOUNT = 0.5f;
    public static final float MAX_SPEED = 5.0f;

    int width;
    int height;

    /** Buffer to reduce frequent collision detection. */
    int collisionBuffer;
    /** Minimum frames between collision checks. */
    public static final int BUFFER_THRESHOLD = 10;

    /**
     * Constructs an AccelerateTile object.
     *
     * @param x         The x-coordinate of the tile.
     * @param y         The y-coordinate of the tile.
     * @param direction The direction of acceleration ("up", "down", "left", "right").
     * @param p         The App instance used to load images.
     */
    public AccelerateTile(int x, int y, String direction, App p) {
        super(x, y, 0);  // color is not used for tiles, set to 0
        this.direction = direction;
        this.width = 32;  // Assuming tile size 32x32
        this.height = 32;
        this.collisionBuffer = 0;
        loadImage(p);
    }

    /**
     * Loads the image for the acceleration tile based on its direction.
     *
     * @param p The App instance used to load the image.
     */
    @Override
    public void loadImage(App p) {
        tileImage = p.loadImage("inkball/acceleration_" + direction + ".png");
    }

    /**
     * Checks for a collision between the ball and the acceleration tile.
     * If a collision is detected, applies acceleration to the ball in the tile's direction.
     *
     * @param ball The Ball object to check for collision and apply acceleration.
     */
    public void checkCollision(Ball ball) {
        collisionBuffer++;  // Increment the collision buffer to prevent continuous detection

        // Check collision only if buffer threshold is reached
        if (collisionBuffer >= BUFFER_THRESHOLD) {
            // Check if the ball's bounding box intersects with the tile
            if (ball.getX() + ball.getRadius() > x && ball.getX() - ball.getRadius() < x + width &&
                    ball.getY() + ball.getRadius() > y && ball.getY() - ball.getRadius() < y + height) {

                // Apply acceleration based on direction
                applyAcceleration(ball);

                // Reset the collision buffer after collision
                collisionBuffer = 0;
            }
        }
    }

    /**
     * Applies acceleration to the ball in the tile's direction.
     *
     * @param ball The Ball object to accelerate.
     */
    void applyAcceleration(Ball ball) {
        switch (direction) {
            case "up":
                ball.setYVelocity(ball.getYVelocity() - ACCELERATION_AMOUNT);
                if (ball.getYVelocity() < -MAX_SPEED) {
                    ball.setYVelocity(-MAX_SPEED);
                }
                break;
            case "down":
                ball.setYVelocity(ball.getYVelocity() + ACCELERATION_AMOUNT);
                if (ball.getYVelocity() > MAX_SPEED) {
                    ball.setYVelocity(MAX_SPEED);
                }
                break;
            case "left":
                ball.setXVelocity(ball.getXVelocity() - ACCELERATION_AMOUNT);
                if (ball.getXVelocity() < -MAX_SPEED) {
                    ball.setXVelocity(-MAX_SPEED);
                }
                break;
            case "right":
                ball.setXVelocity(ball.getXVelocity() + ACCELERATION_AMOUNT);
                if (ball.getXVelocity() > MAX_SPEED) {
                    ball.setXVelocity(MAX_SPEED);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid acceleration direction: " + direction);
        }
    }

    /**
     * Draws the acceleration tile at its current position on the screen.
     *
     * @param p The App instance used for drawing.
     */
    @Override
    public void draw(App p) {
        p.image(tileImage, x, y, width, height);
    }

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
    public String getDirection() {
        return direction;
    }
    public void setDirection(String direction) {
        this.direction = direction;
    }
}
