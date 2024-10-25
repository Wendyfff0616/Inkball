package inkball;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import processing.core.PApplet;
import processing.core.PImage;

public class AccelerateTileTest {

    static App app;

    @BeforeAll
    public static void setup() {
        app = new App();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();
    }

    @Test
    public void testConstructor() {
        // Test that the AccelerateTile constructor initializes correctly
        AccelerateTile tile = new AccelerateTile(100, 100, "up", app);
        assertEquals(100, tile.getX());
        assertEquals(100, tile.getY());
        assertEquals("up", tile.getDirection());
        assertEquals(32, tile.width);
        assertEquals(32, tile.height);
        assertEquals(0, tile.collisionBuffer);
        assertNotNull(tile.tileImage);
    }

    @Test
    public void testLoadImage_ValidDirection() {
        // Test that loadImage loads the correct image for a valid direction
        AccelerateTile tile = new AccelerateTile(0, 0, "down", app);
        assertNotNull(tile.tileImage);
    }

    @Test
    public void testCheckCollision_NoCollisionBufferThreshold() {
        // Test that checkCollision does not check collision when collisionBuffer < BUFFER_THRESHOLD
        AccelerateTile tile = new AccelerateTile(0, 0, "up", app);
        Ball ball = new Ball(0, 0, 0, 12, app);
        tile.collisionBuffer = 0;
        tile.checkCollision(ball);
        assertEquals(1, tile.collisionBuffer);
    }

    @Test
    public void testCheckCollision_NoCollisionDetected() {
        // Test that when collisionBuffer >= BUFFER_THRESHOLD and no collision is detected, applyAcceleration is not called
        AccelerateTile tile = new AccelerateTile(0, 0, "up", app);
        Ball ball = new Ball(1000, 1000, 0, 12, app); // Place ball far away
        tile.collisionBuffer = AccelerateTile.BUFFER_THRESHOLD;
        tile.checkCollision(ball);
        // Ball's velocity should remain unchanged
        assertEquals(ball.getInitialXVelocity(), ball.getXVelocity());
        assertEquals(ball.getInitialYVelocity(), ball.getYVelocity());
        // collisionBuffer should be incremented
        assertEquals(AccelerateTile.BUFFER_THRESHOLD + 1, tile.collisionBuffer);
    }

    @Test
    public void testCheckCollision_CollisionDetected() {
        // Test that when collisionBuffer >= BUFFER_THRESHOLD and collision is detected, applyAcceleration is called
        AccelerateTile tile = new AccelerateTile(0, 0, "up", app);
        Ball ball = new Ball(10, 10, 0, 12, app); // Place ball within tile
        tile.collisionBuffer = AccelerateTile.BUFFER_THRESHOLD;
        float initialYVelocity = ball.getYVelocity();
        tile.checkCollision(ball);
        // Ball's Y velocity should be decreased by ACCELERATION_AMOUNT
        assertEquals(initialYVelocity - AccelerateTile.ACCELERATION_AMOUNT, ball.getYVelocity());
        // collisionBuffer should be reset to 0
        assertEquals(0, tile.collisionBuffer);
    }

    @Test
    public void testApplyAcceleration_Up() {
        // Test applyAcceleration method for "up" direction
        AccelerateTile tile = new AccelerateTile(0, 0, "up", app);
        Ball ball = new Ball(0, 0, 0, 12, app);
        ball.setYVelocity(2.0f);
        tile.applyAcceleration(ball);
        // Y velocity should decrease
        assertEquals(2.0f - AccelerateTile.ACCELERATION_AMOUNT, ball.getYVelocity());
        // Test that velocity does not exceed MAX_SPEED or fall below MIN_SPEED
        ball.setYVelocity(-AccelerateTile.MAX_SPEED - 1.0f);
        tile.applyAcceleration(ball);
        assertEquals(-AccelerateTile.MAX_SPEED, ball.getYVelocity());
        // Test reset to initial velocity if below MIN_SPEED
        ball.setYVelocity(0.0f);
        tile.applyAcceleration(ball);
        assertEquals(-0.5, ball.getYVelocity());
    }

    @Test
    public void testApplyAcceleration_Down() {
        // Test applyAcceleration method for "down" direction
        AccelerateTile tile = new AccelerateTile(0, 0, "down", app);

        // Create a ball with an initial Y velocity of 2.0f
        Ball ball = new Ball(0, 0, 0, 12, app);
        ball.setYVelocity(2.0f);

        // Apply acceleration
        tile.applyAcceleration(ball);

        // Expected: The Y velocity should increase by ACCELERATION_AMOUNT
        assertEquals(2.5, ball.getYVelocity(), 0.01);

        // Test that velocity does not exceed MAX_SPEED
        ball.setYVelocity(AccelerateTile.MAX_SPEED + 1.0f);
        tile.applyAcceleration(ball);
        assertEquals(AccelerateTile.MAX_SPEED, ball.getYVelocity(), 0.01);

        // Test reset to initial velocity if below MIN_SPEED
        ball.setYVelocity(0.0f);
        tile.applyAcceleration(ball);
        assertEquals(0.5, ball.getYVelocity(), 0.01);
    }

    @Test
    public void testApplyAcceleration_Left() {
        // Test applyAcceleration method for "left" direction
        AccelerateTile tile = new AccelerateTile(0, 0, "left", app);
        Ball ball = new Ball(0, 0, 0, 12, app);
        ball.setXVelocity(2.0f);
        tile.applyAcceleration(ball);
        // X velocity should decrease
        assertEquals(1.5, ball.getXVelocity());
        // Test that velocity does not exceed MAX_SPEED
        ball.setXVelocity(-AccelerateTile.MAX_SPEED - 1.0f);
        tile.applyAcceleration(ball);
        assertEquals(-AccelerateTile.MAX_SPEED, ball.getXVelocity());
        // Test reset to initial velocity if below MIN_SPEED
        ball.setXVelocity(0.0f);
        tile.applyAcceleration(ball);
        assertEquals(-0.5, ball.getXVelocity());
    }

    @Test
    public void testApplyAcceleration_Right() {
        // Test applyAcceleration method for "right" direction
        AccelerateTile tile = new AccelerateTile(0, 0, "right", app);
        Ball ball = new Ball(0, 0, 0, 12, app);
        ball.setXVelocity(-2.0f);
        tile.applyAcceleration(ball);
        // X velocity should increase
        assertEquals(-1.5, ball.getXVelocity());
        // Test that velocity does not exceed MAX_SPEED
        ball.setXVelocity(AccelerateTile.MAX_SPEED + 1.0f);
        tile.applyAcceleration(ball);
        assertEquals(AccelerateTile.MAX_SPEED, ball.getXVelocity());
        // Test reset to initial velocity if below MIN_SPEED
        ball.setXVelocity(0.0f);
        tile.applyAcceleration(ball);
        assertEquals(0.5, ball.getXVelocity());
    }

    @Test
    public void testApplyAcceleration_InvalidDirection() {
        // Test that applyAcceleration throws IllegalArgumentException for invalid direction
        AccelerateTile tile = new AccelerateTile(0, 0, "invalid", app);
        Ball ball = new Ball(0, 0, 0, 12, app);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tile.applyAcceleration(ball);
        });
        assertEquals("Invalid acceleration direction: invalid", exception.getMessage());
    }

    @Test
    public void testCollisionBufferResetAfterCollision() {
        // Test that collisionBuffer resets to 0 after a collision is detected
        AccelerateTile tile = new AccelerateTile(0, 0, "up", app);
        Ball ball = new Ball(10, 10, 0xFF0000, 12, app);
        tile.collisionBuffer = AccelerateTile.BUFFER_THRESHOLD;
        tile.checkCollision(ball);
        assertEquals(0, tile.collisionBuffer);
    }

    @Test
    public void testCollisionBufferIncrementsWhenNoCollision() {
        // Test that collisionBuffer increments when no collision is detected
        AccelerateTile tile = new AccelerateTile(0, 0, "up", app);
        Ball ball = new Ball(1000, 1000, 0xFF0000, 12, app); // Ball far away
        tile.collisionBuffer = AccelerateTile.BUFFER_THRESHOLD;
        tile.checkCollision(ball);
        assertEquals(AccelerateTile.BUFFER_THRESHOLD + 1, tile.collisionBuffer);
    }
}
