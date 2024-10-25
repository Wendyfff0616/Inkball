package inkball;

import processing.core.PImage;

import java.util.Random;

/**
 * Represents a ball entity in the Inkball game. The ball can move, be drawn, and interact
 * with other game entities. Each ball has a specific color, velocity, and radius.
 */
public class Ball extends Entity implements Movable, Drawable {

    private PImage ballImage;
    private float radius;
    private float initialRadius;
    private float vx, vy;  // Velocity in x and y directions
    private float initialVx;
    private float initialVy;
    private boolean isActive;
    private static final float SPEED = 2.0f;  // Default speed for ball movement
    private static Random random = new Random();

    /**
     * Constructs a Ball object with specified position, color, radius, and reference to the game app.
     *
     * @param x      Initial x-coordinate of the ball.
     * @param y      Initial y-coordinate of the ball.
     * @param color  Color of the ball.
     * @param radius Radius of the ball.
     * @param p      Reference to the main game object for loading images.
     */
    public Ball(int x, int y, int color, float radius, App p) {
        super(x, y, color);
        this.radius = radius;
        this.initialRadius = radius;

        // Set random velocity for x and y directions
        this.vx = getRandomVelocity();
        this.vy = getRandomVelocity();

        this.initialVx = this.vx;
        this.initialVy = this.vy;

        this.isActive = false;

        loadImage(p);
    }

    // Getter and setter methods

    public int getX() { return this.x; }
    public int getY() { return this.y; }
    public float getXVelocity() { return this.vx; }
    public float getYVelocity() { return this.vy; }

    public float getInitialXVelocity() { return initialVx; }
    public float getInitialYVelocity() { return initialVy; }

    public float getRadius() { return this.radius; }
    public float getInitialRadius() { return  this.initialRadius; }
    public int getColor() { return this.color; }
    public boolean getIsActive() { return this.isActive; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setXVelocity(float vx) { this.vx = vx; }
    public void setYVelocity(float vy) { this.vy = vy; }
    public void setInitialVx(float initialVx) {
        this.initialVx = initialVx;
    }
    public void setInitialVy(float initialVy) {
        this.initialVy = initialVy;
    }

    public void setRadius(float radius) { this.radius = radius; }

    /**
     * Resets the ball's radius to its initial value.
     */
    public void resetRadius() {
        this.radius = this.initialRadius;
    }

    /**
     * Sets the color of the ball and reloads the image accordingly.
     *
     * @param color The new color of the ball.
     * @param p     Reference to the main game object for loading images.
     */
    public void setColor(int color, App p) {
        this.color = color;
        loadImage(p);  // Reload the ball image to reflect the new color
    }

    public void setIsActive(boolean isActive) { this.isActive = isActive; }

    /**
     * Loads the image associated with the ball's color.
     *
     * @param p Reference to the main game object for loading the image.
     */
    @Override
    public void loadImage(App p) {
        ballImage = p.loadImage("inkball/ball" + color + ".png");
    }

    /**
     * Generates a random velocity for the ball, either -SPEED or SPEED.
     *
     * @return A random velocity in either direction.
     */
    public float getRandomVelocity() {
        return random.nextInt(2) == 0 ? -SPEED : SPEED;
    }

    /**
     * Updates the position of the ball based on its velocity.
     */
    @Override
    public void updatePosition() {
        x += vx;
        y += vy;
    }

    /**
     * Draws the ball on the game screen.
     *
     * @param p Reference to the main game object used for rendering.
     */
    @Override
    public void draw(App p) {
        p.image(ballImage, x - radius, y - radius, radius * 2, radius * 2);
    }
}
