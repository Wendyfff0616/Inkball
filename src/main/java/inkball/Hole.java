package inkball;

import processing.core.PImage;
import processing.core.PVector;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Hole object in the Inkball game.
 */
public class Hole extends Entity implements Drawable {
    private PImage holeImage;
    private int width, height;  // Size of the hole (64x64 pixels)
    private static final float ATTRACTION_FACTOR = 0.005f;  // Attraction force as 0.5% (0.005)

    // Used to track the currently attracted balls
    private Set<Ball> attractedBalls = new HashSet<>();

    /**
     * Constructor for the Hole object.
     */
    public Hole(int x, int y, int color, App p) {
        super(x, y, color);
        this.width = 64;  // Each hole is 64x64 pixels (2x2 tiles)
        this.height = 64;
        loadImage(p);
    }

    /**
     * Loads the image of the hole based on its color.
     */
    @Override
    public void loadImage(App p) {
        holeImage = p.loadImage("inkball/hole" + color + ".png");
    }

    /**
     * Draws the hole on the game screen.
     */
    @Override
    public void draw(App p) {
        p.image(holeImage, x, y, width, height);
    }

    /**
     * Attracts the ball toward the hole if the ball is within a certain range.
     */
    public void attractBall(Ball ball, Level level, App app) {
        float holeCenterX = x + width / 2.0f;
        float holeCenterY = y + height / 2.0f;

        float ballCenterX = ball.getX();
        float ballCenterY = ball.getY();

        // Calculate the distance between the ball and the center of the hole
        float distanceToHole = PVector.dist(new PVector(ballCenterX, ballCenterY), new PVector(holeCenterX, holeCenterY));

        // Check if the ball is within the attraction range (32 pixels)
        if (distanceToHole <= 32) {
            // Add the ball to the attractedBalls set if not already present
            if (!attractedBalls.contains(ball)) {
                attractedBalls.add(ball);
            }

            // Calculate attraction force
            PVector direction = new PVector(holeCenterX - ballCenterX, holeCenterY - ballCenterY);
            direction.normalize();
            PVector attraction = direction.mult(ATTRACTION_FACTOR / distanceToHole);

            // Update the ball's velocity to move toward the hole
            ball.setXVelocity(ball.getXVelocity() + attraction.x);
            ball.setYVelocity(ball.getYVelocity() + attraction.y);

            // Gradually decrease the ball's size to simulate it falling into the hole
            float newRadius = ball.getInitialRadius() * (distanceToHole / 32.0f);  // Scale radius based on distance
            newRadius = Math.max(newRadius, 0);  // Prevent negative radius
            ball.setRadius(newRadius);

            // Check if the ball has been successfully captured by the hole
            if (distanceToHole < 15) {  // Ball is close enough to be captured
                if (ball.getColor() == this.color || ball.getColor() == ColorUtils.colorToNumber("grey") || this.color == ColorUtils.colorToNumber("grey")) {
                    // Successful capture, remove the ball
//                    System.out.println("---------Capture Successful");
                    level.increaseScore(ball.getColor(), app);  // Pass the color number to increase score
                    ball.setIsActive(false);  // Deactivate the ball to prevent further updates
                } else {
                    // Capture failed, move the ball back to the unspawned queue
//                    System.out.println("---------Capture Failed");
                    level.decreaseScore(ball.getColor(), app);  // Pass the color number to decrease score
                    ball.setIsActive(false);  // Deactivate the ball

                    // Check if the unspawned queue is empty, and if so, immediately spawn a new ball
                    if (app.unspawnedBalls.isEmpty()) {
                        app.spawnNewBallImmediate(ball);  // Immediately spawn the new ball if queue is empty
                    } else {
                        app.addUnspawnedBall(ball);  // Add the ball back to the unspawned queue
                    }
                }
                // Remove the ball from attractedBalls as it's no longer active
                attractedBalls.remove(ball);
            }
        } else {
            // If the ball was previously attracted but is no longer within range, reset its radius
            if (attractedBalls.contains(ball)) {
                ball.resetRadius();
                attractedBalls.remove(ball);
            }
        }
    }

    /**
     * Updates attracted balls and restores their radius if they are no longer attracted.
     */
    public void updateAttractedBalls() {
        Set<Ball> ballsToRemove = new HashSet<>();
        for (Ball ball : attractedBalls) {
            float holeCenterX = x + width / 2.0f;
            float holeCenterY = y + height / 2.0f;
            float ballCenterX = ball.getX();
            float ballCenterY = ball.getY();
            float distanceToHole = PVector.dist(new PVector(ballCenterX, ballCenterY), new PVector(holeCenterX, holeCenterY));

            if (distanceToHole > 32) {
                // Ball is no longer within attraction range, reset its radius
                ball.resetRadius();
                ballsToRemove.add(ball);
            }
        }
        // Remove balls that are no longer attracted
        attractedBalls.removeAll(ballsToRemove);
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public PImage getHoleImage() {
        return holeImage;
    }

    public void setHoleImage(PImage holeImage) {
        this.holeImage = holeImage;
    }

    public Set<Ball> getAttractedBalls() {
        return attractedBalls;
    }

    public void setAttractedBalls(Set<Ball> attractedBalls) {
        this.attractedBalls = attractedBalls;
    }

    public float getAttractionFactor() {
        return ATTRACTION_FACTOR;
    }
}
