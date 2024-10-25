package inkball;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a line drawn by the player in the Inkball game.
 * Manages the points that define the line, handles drawing the line on the game screen,
 * and checks for collisions with balls.
 */
public class PlayerDrawnLine {
    /** Stores the points that make up the line */
    List<PVector> points;
    static final float LINE_THICKNESS = 10;
    public static final int DELETE_THRESHOLD = 15;
    /** Buffer to avoid continuous collision detection */
    private int collisionBuffer;
    /** Number of frames to wait before checking collisions again */
    public static final int BUFFER_THRESHOLD = 5;

    /**
     * Constructs a PlayerDrawnLine object.
     */
    public PlayerDrawnLine() {
        points = new ArrayList<>();
    }

    /**
     * Adds a new point to the line.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    public void addPoint(float x, float y) {
        points.add(new PVector(x, y));
    }

    /**
     * Clears the points in the line, effectively removing it.
     */
    public void clear() {
        points.clear();
    }

    /**
     * Draws the player-drawn line on the screen.
     *
     * @param p The App instance used to draw the line.
     */
    public void draw(App p) {
        p.pushStyle(); // Save current drawing style

        if (points.size() > 1) {
            p.stroke(0);  // Set the line color to black
            p.strokeWeight(LINE_THICKNESS);  // Set the thickness of the line
            for (int i = 0; i < points.size() - 1; i++) {
                PVector p1 = points.get(i);
                PVector p2 = points.get(i + 1);
                p.line(p1.x, p1.y, p2.x, p2.y);  // Draw the line between consecutive points
            }
        }

        p.popStyle(); // Restore drawing style
    }

    /**
     * Checks for collisions between the ball and the line.
     * If a collision is detected, the ball's velocity is updated, and the line is cleared.
     *
     * @param ball The ball object to check for collision.
     */
    public void checkCollision(Ball ball) {
        // Increment the collision buffer to avoid constant collision checks
        collisionBuffer++;

        // Only check for collisions after the buffer threshold has been reached
        if (collisionBuffer >= BUFFER_THRESHOLD) {
            for (int i = 0; i < points.size() - 1; i++) {
                PVector p1 = points.get(i);
                PVector p2 = points.get(i + 1);

                // Calculate the future position of the ball based on its current velocity
                PVector ballPosition = new PVector(ball.getX() + ball.getXVelocity(), ball.getY() + ball.getYVelocity());

                // Calculate the distances from the ball to both ends of the line segment
                float distanceToP1 = PVector.dist(p1, ballPosition);
                float distanceToP2 = PVector.dist(p2, ballPosition);
                float lineLength = PVector.dist(p1, p2);

                // Check if the ball is close enough to the line to detect a collision
                if (distanceToP1 + distanceToP2 < lineLength + ball.getRadius()) {
                    // Collision detected, calculate the reflection vector
                    PVector lineVector = PVector.sub(p2, p1);
                    PVector normal1 = new PVector(-lineVector.y, lineVector.x);  // Perpendicular normal vector 1
                    PVector normal2 = new PVector(lineVector.y, -lineVector.x);  // Perpendicular normal vector 2

                    // Normalize the normal vectors
                    normal1.normalize();
                    normal2.normalize();

                    // Choose the normal vector that is closer to the ball
                    PVector midpoint = PVector.add(p1, p2).div(2);
                    PVector normal = PVector.dist(midpoint.copy().add(normal1), ballPosition) <
                            PVector.dist(midpoint.copy().add(normal2), ballPosition) ? normal1 : normal2;

                    // Calculate the new velocity using the reflection formula
                    PVector velocity = new PVector(ball.getXVelocity(), ball.getYVelocity());
                    float dotProduct = velocity.dot(normal);
                    PVector newVelocity = PVector.sub(velocity, PVector.mult(normal, 2 * dotProduct));

                    // Update the ball's velocity
                    ball.setXVelocity(newVelocity.x);
                    ball.setYVelocity(newVelocity.y);

                    // Clear the line after the collision
                    clear();

                    // Reset the collision buffer after detecting a collision
                    collisionBuffer = 0;
                    break;
                }
            }
        }
    }

    /**
     * Checks if any part of the line is near a given point, used for detecting line deletion.
     *
     * @param x The x-coordinate of the point to check.
     * @param y The y-coordinate of the point to check.
     * @return True if the line is near the point, otherwise false.
     */
    public boolean isNear(float x, float y) {
        for (int i = 0; i < points.size() - 1; i++) {
            PVector p1 = points.get(i);
            PVector p2 = points.get(i + 1);

            // Calculate the shortest distance from the point to the line segment
            float distanceToLine = distToSegment(new PVector(x, y), p1, p2);
            if (distanceToLine < DELETE_THRESHOLD) {
                return true;  // Return true if the point is close enough to the line
            }
        }
        return false;
    }

    /**
     * Helper method to calculate the shortest distance from a point to a line segment.
     *
     * @param point The point to check.
     * @param v     One endpoint of the line segment.
     * @param w     The other endpoint of the line segment.
     * @return The shortest distance from the point to the line segment.
     */
    private float distToSegment(PVector point, PVector v, PVector w) {
        float l2 = PVector.dist(v, w) * PVector.dist(v, w);  // Length of the segment squared
        if (l2 == 0.0) return PVector.dist(point, v);  // If v == w, return distance to point v
        float t = PVector.dot(PVector.sub(point, v), PVector.sub(w, v)) / l2;
        t = Math.max(0, Math.min(1, t));  // Clamp t to the range [0,1]
        PVector projection = PVector.add(v, PVector.mult(PVector.sub(w, v), t));  // Projection of the point on the line
        return PVector.dist(point, projection);  // Distance from the point to the line segment
    }

    public List<PVector> getPoints() {
        return points;
    }
}
