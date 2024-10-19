// File: PlayerDrawnLineTest.java
package inkball;

import processing.core.PApplet;
import processing.core.PVector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the PlayerDrawnLine class.
 * This class verifies the functionality of adding points, clearing the line,
 * drawing the line, collision detection, and proximity checks.
 */
public class PlayerDrawnLineTest {

    static App app;
    static PlayerDrawnLine line;
    static Ball ball;

    @BeforeAll
    public static void setup() {
        // Initialize and run the Processing sketch in a separate thread
        app = new App();
        Thread appThread = new Thread(() -> PApplet.runSketch(new String[] {"App"}, app));
        appThread.start();

        // Allow some time for the sketch to initialize
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail("App initialization interrupted");
        }

        // Initialize PlayerDrawnLine and Ball instances
        line = new PlayerDrawnLine();
        ball = new Ball(150, 150, 1, 12, app);
        ball.setXVelocity(5);
        ball.setYVelocity(5);
    }

    /**
     * Test that adding points to the line correctly stores them.
     */
    @Test
    public void testAddPoint() {
        line.addPoint(100, 100);
        line.addPoint(200, 200);

        assertEquals(2, line.points.size(), "Line should contain 2 points after adding them");
        assertEquals(new PVector(100, 100), line.points.get(0), "First point should be (100, 100)");
        assertEquals(new PVector(200, 200), line.points.get(1), "Second point should be (200, 200)");
    }

    /**
     * Test that clearing the line removes all points.
     */
    @Test
    public void testClear() {
        line.addPoint(100, 100);
        line.addPoint(200, 200);
        line.clear();

        assertTrue(line.points.isEmpty(), "Line should have no points after clearing");
    }

    /**
     * Test that drawing the line executes without throwing exceptions.
     */
    @Test
    public void testDraw_NoExceptions() {
        // Add points to the line
        line.addPoint(100, 100);
        line.addPoint(200, 200);

        // Ensure draw does not throw exceptions
        assertDoesNotThrow(() -> line.draw(app), "Draw method should execute without throwing exceptions");
    }

    /**
     * Test that isNear returns true when a point is near the line.
     */
    @Test
    public void testIsNear_NearPoint() {
        // Add points to form a horizontal line from (100, 100) to (200, 100)
        line.addPoint(100, 100);
        line.addPoint(200, 100);

        // Point near the line
        assertTrue(line.isNear(150, 105), "Point (150, 105) should be near the line");
    }

    /**
     * Test that isNear returns false when a point is far from the line.
     */
    @Test
    public void testIsNear_FarPoint() {
        // Add points to form a horizontal line from (100, 100) to (200, 100)
        line.addPoint(100, 100);
        line.addPoint(200, 100);

        // Point far from the line
        assertFalse(line.isNear(150, 200), "Point (150, 200) should not be near the line");
    }

    /**
     * Test that checkCollision does not alter the ball's velocity when there's no collision.
     */
    @Test
    public void testCheckCollision_NoCollision() {
        // Add points to form a horizontal line from (100, 100) to (200, 100)
        line.addPoint(100, 100);
        line.addPoint(200, 100);

        // Ball is moving parallel above the line
        ball.setX(150);
        ball.setY(150); // Far from the line y=100
        ball.setXVelocity(5);
        ball.setYVelocity(0);

        // Perform collision check multiple times to surpass the buffer
        for (int i = 0; i < PlayerDrawnLine.BUFFER_THRESHOLD; i++) {
            line.checkCollision(ball);
        }

        // Verify that the ball's velocity remains unchanged
        assertEquals(5, ball.getXVelocity(), "Ball's X velocity should remain unchanged");
        assertEquals(0, ball.getYVelocity(), "Ball's Y velocity should remain unchanged");
        assertFalse(line.points.isEmpty(), "Line should not be cleared when there's no collision");
    }

    /**
     * Test that checkCollision alters the ball's velocity and clears the line upon collision.
     */
    @Test
    public void testCheckCollision_Collision() {
        // Add points to form a horizontal line from (100, 100) to (200, 100)
        line.addPoint(100, 100);
        line.addPoint(200, 100);

        // Ball is moving towards the line
        ball.setX(150);
        ball.setY(105); // Within DELETE_THRESHOLD
        ball.setXVelocity(0);
        ball.setYVelocity(-5); // Moving upwards towards the line

        // Perform collision check multiple times to surpass the buffer
        for (int i = 0; i < PlayerDrawnLine.BUFFER_THRESHOLD; i++) {
            line.checkCollision(ball);
        }

        // Verify that the ball's velocity has been reflected
        assertEquals(0, ball.getXVelocity(), "Ball's X velocity should remain unchanged after collision");
        assertEquals(5, ball.getYVelocity(), "Ball's Y velocity should be reflected after collision");

        // Verify that the line has been cleared
        assertTrue(line.points.isEmpty(), "Line should be cleared after collision");
    }

    /**
     * Test that checkCollision respects the collision buffer and does not detect collisions too frequently.
     */
    @Test
    public void testCheckCollision_Buffer() {
        // Add points to form a horizontal line from (100, 100) to (200, 100)
        line.addPoint(100, 100);
        line.addPoint(200, 100);

        // Ball is positioned for collision
        ball.setX(150);
        ball.setY(95); // Within DELETE_THRESHOLD
        ball.setXVelocity(0);
        ball.setYVelocity(5); // Moving downwards towards the line

        // Perform collision check without exceeding the buffer
        for (int i = 0; i < PlayerDrawnLine.BUFFER_THRESHOLD - 1; i++) {
            line.checkCollision(ball);
        }

        // Verify that collision has not been detected yet
        assertEquals(5, ball.getYVelocity(), "Ball's Y velocity should remain unchanged before buffer threshold");
        assertFalse(line.points.isEmpty(), "Line should not be cleared before buffer threshold");
    }

    /**
     * Test the distance calculation from a point to a segment.
     */
    @Test
    public void testDistToSegment() {
        // Access the private method distToSegment via reflection
        try {
            java.lang.reflect.Method method = PlayerDrawnLine.class.getDeclaredMethod("distToSegment", PVector.class, PVector.class, PVector.class);
            method.setAccessible(true);

            PVector point = new PVector(150, 150);
            PVector v = new PVector(100, 100);
            PVector w = new PVector(200, 100);

            float distance = (float) method.invoke(line, point, v, w);
            assertEquals(50, distance, 0.001, "Distance from (150,150) to line segment should be 50");
        } catch (Exception e) {
            fail("Exception occurred while testing distToSegment: " + e.getMessage());
        }
    }

    /**
     * Test that isNear handles edge cases correctly.
     */
    @Test
    public void testIsNear_EdgeCases() {
        // Line with a single point
        PlayerDrawnLine singlePointLine = new PlayerDrawnLine();
        singlePointLine.addPoint(100, 100);

        // Point exactly at the single point
        assertTrue(singlePointLine.isNear(100, 100), "Point exactly at the single point should be near");

        // Point slightly away from the single point
        assertFalse(singlePointLine.isNear(110, 110), "Point slightly away from the single point should not be near");
    }

    /**
     * Test that checkCollision correctly handles multiple collision points.
     */
    @Test
    public void testCheckCollision_MultipleCollisions() {
        // Add multiple segments to the line
        line.addPoint(100, 100);
        line.addPoint(200, 100);
        line.addPoint(200, 200);

        // Ball is moving towards the first segment
        ball.setX(150);
        ball.setY(95); // Within DELETE_THRESHOLD
        ball.setXVelocity(0);
        ball.setYVelocity(5); // Moving downwards towards the first segment

        // Perform collision check multiple times to surpass the buffer
        for (int i = 0; i < PlayerDrawnLine.BUFFER_THRESHOLD; i++) {
            line.checkCollision(ball);
        }

        // Verify that the ball's velocity has been reflected
        assertEquals(0, ball.getXVelocity(), "Ball's X velocity should remain unchanged after collision");
        assertEquals(-5, ball.getYVelocity(), "Ball's Y velocity should be reflected after collision");

        // Verify that the entire line has been cleared (since clear() is called)
        assertTrue(line.points.isEmpty(), "Line should be cleared after collision");
    }

    /**
     * Test that checkCollision does not clear the line if collision does not occur.
     */
    @Test
    public void testCheckCollision_NoCollision_DoesNotClearLine() {
        // Add points to form a horizontal line from (100, 100) to (200, 100)
        line.addPoint(100, 100);
        line.addPoint(200, 100);

        // Ball is positioned to miss the line
        ball.setX(150);
        ball.setY(200); // Far from the line y=100
        ball.setXVelocity(0);
        ball.setYVelocity(-5); // Moving upwards away from the line

        // Perform collision check multiple times to surpass the buffer
        for (int i = 0; i < PlayerDrawnLine.BUFFER_THRESHOLD; i++) {
            line.checkCollision(ball);
        }

        // Verify that the ball's velocity remains unchanged
        assertEquals(0, ball.getXVelocity(), "Ball's X velocity should remain unchanged");
        assertEquals(-5, ball.getYVelocity(), "Ball's Y velocity should remain unchanged");

        // Verify that the line remains intact
        assertFalse(line.points.isEmpty(), "Line should not be cleared when there's no collision");
    }
}
