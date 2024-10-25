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
        app = new App();
        Thread appThread = new Thread(() -> PApplet.runSketch(new String[] {"App"}, app));
        appThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail("App initialization interrupted");
        }

        line = new PlayerDrawnLine();
        ball = new Ball(150, 150, 1, 12, app);
        ball.setXVelocity(5);
        ball.setYVelocity(5);
    }

    @Test
    public void testAddPoint() {
        // Test that adding points to the line correctly stores them
        line.addPoint(100, 100);
        line.addPoint(200, 200);

        assertEquals(2, line.points.size());
        assertEquals(new PVector(100, 100), line.points.get(0));
        assertEquals(new PVector(200, 200), line.points.get(1));
    }

    @Test
    public void testClear() {
        // Test that clearing the line removes all points
        line.addPoint(100, 100);
        line.addPoint(200, 200);
        line.clear();
        assertTrue(line.points.isEmpty());
    }

    @Test
    public void testDraw_NoExceptions() {
        // Test that drawing the line executes without throwing exceptions
        line.addPoint(100, 100);
        line.addPoint(200, 200);
        assertDoesNotThrow(() -> line.draw(app));
    }

    @Test
    public void testIsNear_NearPoint() {
        // Test that isNear returns true when a point is near the line
        line.addPoint(100, 100);
        line.addPoint(200, 100);
        assertTrue(line.isNear(150, 105));
    }

    @Test
    public void testIsNear_FarPoint() {
        // Test that isNear returns false when a point is far from the line
        line.addPoint(100, 100);
        line.addPoint(200, 100);

        // Point far from the line
        assertFalse(line.isNear(150, 200));
    }

    @Test
    public void testCheckCollision() {
        // Test that checkCollision does not alter the ball's velocity when there's no collision
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

        assertEquals(5, ball.getXVelocity());
        assertEquals(0, ball.getYVelocity());
        assertFalse(line.points.isEmpty());
    }

    @Test
    public void testCheckCollision_Collision() {
        // Test that checkCollision alters the ball's velocity and clears the line upon collision
        line.addPoint(100, 100);
        line.addPoint(200, 100);

        // Ball is moving towards the line
        ball.setX(150);
        ball.setY(105); // Within DELETE_THRESHOLD
        ball.setXVelocity(0);
        ball.setYVelocity(-5); // Moving upwards towards the line

        for (int i = 0; i < PlayerDrawnLine.BUFFER_THRESHOLD; i++) {
            line.checkCollision(ball);
        }

        // Verify that the ball's velocity has been reflected
        assertEquals(0, ball.getXVelocity());
        assertEquals(5, ball.getYVelocity());

        // Verify that the line has been cleared
        assertTrue(line.points.isEmpty());
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
            assertEquals(50, distance, 0.00);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
