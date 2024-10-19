package inkball;

import processing.core.PImage;
import processing.core.PVector;

/**
 * The Wall class represents a wall in the Inkball game. Walls are stationary objects
 * that interact with balls when they collide. Each wall has a position, a color, and
 * a collision buffer to control collision detection frequency.
 */
public class Wall extends Entity implements Drawable {
    PImage wallImage;  // The image representing the wall
    int width;
    int height;  // Dimensions of the wall (32x32 pixels)
    int collisionBuffer;  // Buffer to reduce frequent collision detection
    public static final int BUFFER_THRESHOLD = 5;  // Minimum frames between collision checks

    /**
     * Constructs a Wall object.
     *
     * @param x     The x-coordinate of the wall.
     * @param y     The y-coordinate of the wall.
     * @param color The color of the wall (used to load the appropriate image).
     * @param p     The App instance used to load images.
     */
    public Wall(int x, int y, int color, App p) {
        super(x, y, color);
        this.width = 32;  // Default width
        this.height = 32; // Default height
        this.collisionBuffer = 0;
        loadImage(p);  // Load the wall image
    }

    public int getX() { return this.x; }
    public int getY() { return this.y; }

    /**
     * Loads the image for the wall based on its color.
     *
     * @param p The App instance used to load the image.
     */
    @Override
    public void loadImage(App p) {
        wallImage = p.loadImage("inkball/wall" + color + ".png");  // Load the image based on the wall's color
    }

    /**
     * Checks for a collision between the ball and the wall. If a collision is detected,
     * the ball's velocity is reflected, and its color may be updated to match the wall's color.
     *
     * @param ball The Ball object to check for collision.
     * @param p    The App instance, used for updating the ball's color if necessary.
     */
    public void checkCollision(Ball ball, App p) {
        collisionBuffer++;  // Increment the collision buffer to prevent continuous detection

        // Check for collision only if the buffer threshold is reached
        if (collisionBuffer >= BUFFER_THRESHOLD) {
            // Check if the ball's bounding box intersects with the wall
            if (ball.getX() + ball.getRadius() > x && ball.getX() - ball.getRadius() < x + width &&
                    ball.getY() + ball.getRadius() > y && ball.getY() - ball.getRadius() < y + height) {

                // Calculate overlap distances between the ball and the wall on each side
                float overlapLeft = (ball.getX() + ball.getRadius()) - x;
                float overlapRight = (x + width) - (ball.getX() - ball.getRadius());
                float overlapTop = (ball.getY() + ball.getRadius()) - y;
                float overlapBottom = (y + height) - (ball.getY() - ball.getRadius());

                // Find the minimum overlap distance on both x and y axes
                float minOverlapX = Math.min(overlapLeft, overlapRight);
                float minOverlapY = Math.min(overlapTop, overlapBottom);

                PVector normal;

                // Determine the side of the wall the ball collided with (horizontal or vertical)
                if (minOverlapX < minOverlapY) {
                    // Horizontal collision
                    if (overlapLeft < overlapRight) {
                        normal = new PVector(-1, 0);  // Left side
                        ball.setX((int) (ball.getX() - overlapLeft));  // Adjust the ball's position
                    } else {
                        normal = new PVector(1, 0);  // Right side
                        ball.setX((int) (ball.getX() + overlapRight));  // Adjust the ball's position
                    }
                } else {
                    // Vertical collision
                    if (overlapTop < overlapBottom) {
                        normal = new PVector(0, -1);  // Top side
                        ball.setY((int) (ball.getY() - overlapTop));  // Adjust the ball's position
                    } else {
                        normal = new PVector(0, 1);  // Bottom side
                        ball.setY((int) (ball.getY() + overlapBottom));  // Adjust the ball's position
                    }
                }

                // Reflect the ball's velocity based on the collision normal
                PVector velocity = new PVector(ball.getXVelocity(), ball.getYVelocity());
                PVector reflectedVelocity = PVector.sub(velocity, PVector.mult(normal, 2 * velocity.dot(normal)));

                // Update the ball's velocity after the collision
                ball.setXVelocity(reflectedVelocity.x);
                ball.setYVelocity(reflectedVelocity.y);

                // Update the ball's color to match the wall's color (except for color 0)
                if (color != 0) {
                    ball.setColor(this.color, p);  // Change the ball's color to match the wall's
                }

                // Reset the collision buffer after detecting a collision
                collisionBuffer = 0;
            }
        }
    }

    /**
     * Draws the wall at its current position on the screen.
     *
     * @param p The App instance used for drawing the wall.
     */
    @Override
    public void draw(App p) {
        p.image(wallImage, x, y, width, height);  // Draw the wall image at the specified position
    }

    public int getColor() {
        return color;
    }
}
