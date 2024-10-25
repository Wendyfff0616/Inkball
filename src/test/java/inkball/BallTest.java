package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import static org.junit.jupiter.api.Assertions.*;

public class BallTest {

    private Ball ball;
    private App app;

    @BeforeEach
    public void setup() {
        // Initialize the PApplet (App) and Ball for testing
        app = new App();
        PApplet.runSketch(new String[]{"App"}, app);

        // Initialize a ball with starting position (50, 50), color 1, and radius 10
        ball = new Ball(50, 50, 1, 12, app);
    }

    @Test
    public void testInitialValues() {
        // Ensure the ball is initialized with correct values
        assertEquals(50, ball.getX());
        assertEquals(50, ball.getY());
        assertEquals(12, ball.getRadius());
        assertEquals(1, ball.getColor());
        assertFalse(ball.getIsActive()); // Ball should be inactive at initialization

        // Check initial velocity is within the defined range
        assertTrue(Math.abs(ball.getXVelocity()) == 2.0f || Math.abs(ball.getXVelocity()) == 2.0f);
        assertTrue(Math.abs(ball.getYVelocity()) == 2.0f || Math.abs(ball.getYVelocity()) == 2.0f);
    }

    @Test
    public void testBallMovement() {
        // Set a specific velocity and update the position
        ball.setXVelocity(2.0f);
        ball.setYVelocity(3.0f);
        ball.updatePosition();

        // Verify that the ball moved according to the velocity
        assertEquals(52, ball.getX());
        assertEquals(53, ball.getY());
    }

    @Test
    public void testSetPosition() {
        // Test setting a new position for the ball
        ball.setX(100);
        ball.setY(150);

        // Ensure the position was updated correctly
        assertEquals(100, ball.getX());
        assertEquals(150, ball.getY());
    }

    @Test
    public void testResetRadius() {
        // Change the radius and reset it back
        ball.setRadius(20);
        assertEquals(20, ball.getRadius());

        // Reset to the initial radius
        ball.resetRadius();
        assertEquals(12, ball.getRadius()); // Initial radius was 12
    }

    @Test
    public void testSetColor() {
        // Test changing the ball's color and ensure it is updated
        ball.setColor(3, app);
        assertEquals(3, ball.getColor());
    }

    @Test
    public void testRandomVelocity() {
        // Test the random velocity generator to ensure it returns expected values
        float velocity = ball.getRandomVelocity();
        assertTrue(velocity == 2.0f || velocity == -2.0f);
    }

    @Test
    public void testActiveState() {
        // Ensure the ball's active state can be modified
        assertFalse(ball.getIsActive());

        // Set the ball as active
        ball.setIsActive(true);
        assertTrue(ball.getIsActive());

        // Set the ball as inactive
        ball.setIsActive(false);
        assertFalse(ball.getIsActive());
    }

    @Test
    public void testVelocitySetterAndGetter() {
        // Test setting and getting the velocity of the ball
        ball.setXVelocity(5.0f);
        ball.setYVelocity(-3.0f);

        assertEquals(5.0f, ball.getXVelocity());
        assertEquals(-3.0f, ball.getYVelocity());
    }

    @Test
    public void testInitialVelocity() {
        // Ensure the ball stores the initial velocity properly
        assertEquals(ball.getInitialXVelocity(), ball.getXVelocity());
        assertEquals(ball.getInitialYVelocity(), ball.getYVelocity());
    }
}
