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
        app.setup(); // Initialize the app
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
        // Since we cannot check the actual image, we assume if no exception is thrown, it's loaded
    }

    @Test
    public void testCheckCollision_NoCollisionBufferThreshold() {
        // Test that checkCollision does not check collision when collisionBuffer < BUFFER_THRESHOLD
        AccelerateTile tile = new AccelerateTile(0, 0, "up", app);
        Ball ball = new Ball(0, 0, 0, 12, app);
        tile.collisionBuffer = 0;
        tile.checkCollision(ball);
        assertEquals(1, tile.collisionBuffer);
        // Since collisionBuffer < BUFFER_THRESHOLD, collision should not be checked
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
        assertEquals(ball.getInitialYVelocity(), ball.getYVelocity());
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
        assertEquals(2.0f + AccelerateTile.ACCELERATION_AMOUNT, ball.getYVelocity(), 0.01);

        // Test that velocity does not exceed MAX_SPEED
        ball.setYVelocity(AccelerateTile.MAX_SPEED + 1.0f);
        tile.applyAcceleration(ball);
        assertEquals(AccelerateTile.MAX_SPEED, ball.getYVelocity(), 0.01);

        // Test reset to initial velocity if below MIN_SPEED
        ball.setYVelocity(0.0f);
        tile.applyAcceleration(ball);
        assertEquals(ball.getInitialYVelocity(), ball.getYVelocity(), 0.01);
    }

    @Test
    public void testApplyAcceleration_Left() {
        // Test applyAcceleration method for "left" direction
        AccelerateTile tile = new AccelerateTile(0, 0, "left", app);
        Ball ball = new Ball(0, 0, 0, 12, app);
        ball.setXVelocity(2.0f);
        tile.applyAcceleration(ball);
        // X velocity should decrease
        assertEquals(2.0f - AccelerateTile.ACCELERATION_AMOUNT, ball.getXVelocity());
        // Test that velocity does not exceed MAX_SPEED
        ball.setXVelocity(-AccelerateTile.MAX_SPEED - 1.0f);
        tile.applyAcceleration(ball);
        assertEquals(-AccelerateTile.MAX_SPEED, ball.getXVelocity());
        // Test reset to initial velocity if below MIN_SPEED
        ball.setXVelocity(0.0f);
        tile.applyAcceleration(ball);
        assertEquals(ball.getInitialXVelocity(), ball.getXVelocity());
    }

    @Test
    public void testApplyAcceleration_Right() {
        // Test applyAcceleration method for "right" direction
        AccelerateTile tile = new AccelerateTile(0, 0, "right", app);
        Ball ball = new Ball(0, 0, 0, 12, app);
        ball.setXVelocity(-2.0f);
        tile.applyAcceleration(ball);
        // X velocity should increase
        assertEquals(-2.0f + AccelerateTile.ACCELERATION_AMOUNT, ball.getXVelocity());
        // Test that velocity does not exceed MAX_SPEED
        ball.setXVelocity(AccelerateTile.MAX_SPEED + 1.0f);
        tile.applyAcceleration(ball);
        assertEquals(AccelerateTile.MAX_SPEED, ball.getXVelocity());
        // Test reset to initial velocity if below MIN_SPEED
        ball.setXVelocity(0.0f);
        tile.applyAcceleration(ball);
        assertEquals(ball.getInitialXVelocity(), ball.getXVelocity());
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
    public void testDraw() {
        // Test that draw method executes without errors
        AccelerateTile tile = new AccelerateTile(0, 0, "up", app);
        assertDoesNotThrow(() -> tile.draw(app));
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

    @Test
    public void testApplyAcceleration_MaxSpeedBoundary() {
        // Test that applyAcceleration does not exceed MAX_SPEED
        AccelerateTile tile = new AccelerateTile(0, 0, "right", app);
        Ball ball = new Ball(0, 0, 0xFF0000, 12, app);
        ball.setXVelocity(AccelerateTile.MAX_SPEED);
        tile.applyAcceleration(ball);
        assertEquals(AccelerateTile.MAX_SPEED, ball.getXVelocity());
    }

    @Test
    public void testApplyAcceleration_MinSpeedBoundary() {
        // Test that applyAcceleration resets velocity if below MIN_SPEED
        AccelerateTile tile = new AccelerateTile(0, 0, "left", app);
        Ball ball = new Ball(0, 0, 0xFF0000, 12, app);
        ball.setXVelocity(AccelerateTile.MIN_SPEED);
        tile.applyAcceleration(ball);
        assertEquals(ball.getInitialXVelocity(), ball.getXVelocity());
    }

    @Test
    public void testCheckCollision_WithBallAtEdge() {
        // Test collision detection when the ball is exactly at the edge of the tile
        AccelerateTile tile = new AccelerateTile(100, 100, "up", app);
        // Initialize the Ball at the edge of the tile's right side
        int ballX = 100 + tile.width + 12; // The ball's center is at the tile's right edge
        int ballY = 100; // Same y-coordinate as the tile
        Ball ball = new Ball(ballX, ballY, 0, 12, app); // Assuming color and radius values
        tile.collisionBuffer = AccelerateTile.BUFFER_THRESHOLD;
        tile.checkCollision(ball);
        // No collision should be detected
        assertNotEquals(0, tile.collisionBuffer);
    }

    @Test
    public void testCheckCollision_BallFullyWithinTile() {
        // Test collision detection when the ball is fully within the tile
        AccelerateTile tile = new AccelerateTile(100, 100, "up", app);
        Ball ball = new Ball(100 + tile.width / 2, 100 + tile.height / 2, 0xFF0000, 12, app);
        tile.collisionBuffer = AccelerateTile.BUFFER_THRESHOLD;
        tile.checkCollision(ball);
        // Collision should be detected
        assertEquals(0, tile.collisionBuffer);
    }

    @Test
    public void testCollisionBufferDoesNotExceedThreshold() {
        // Test that collisionBuffer does not reset before reaching BUFFER_THRESHOLD
        AccelerateTile tile = new AccelerateTile(0, 0, "up", app);
        Ball ball = new Ball(10, 10, 0xFF0000, 12, app);
        tile.collisionBuffer = 0;
        // Simulate multiple frames without reaching threshold
        for (int i = 0; i < AccelerateTile.BUFFER_THRESHOLD - 1; i++) {
            tile.checkCollision(ball);
        }
        assertEquals(AccelerateTile.BUFFER_THRESHOLD - 1, tile.collisionBuffer);
    }

    @Test
    public void testApplyAcceleration_BallNull() {
        // Test that applyAcceleration handles null ball gracefully
        AccelerateTile tile = new AccelerateTile(0, 0, "up", app);
        assertThrows(NullPointerException.class, () -> {
            tile.applyAcceleration(null);
        });
    }

    @Test
    public void testCheckCollision_BallNull() {
        // Test that checkCollision handles null ball gracefully
        AccelerateTile tile = new AccelerateTile(0, 0, "up", app);
        assertThrows(NullPointerException.class, () -> {
            tile.checkCollision(null);
        });
    }

    @Test
    public void testConstructor_NullDirection() {
        // Test that constructor throws exception when direction is null
        Exception exception = assertThrows(NullPointerException.class, () -> {
            new AccelerateTile(0, 0, null, app);
        });
        // Exception is expected due to null direction
    }

    @Test
    public void testConstructor_NullApp() {
        // Test that constructor throws exception when app is null
        Exception exception = assertThrows(NullPointerException.class, () -> {
            new AccelerateTile(0, 0, "up", null);
        });
        // Exception is expected due to null app
    }

    @Test
    public void testLoadImage_NullApp() {
        // Test that loadImage handles null app gracefully
        AccelerateTile tile = new AccelerateTile(0, 0, "up", app);
        assertThrows(NullPointerException.class, () -> {
            tile.loadImage(null);
        });
    }

    @Test
    public void testDraw_NullApp() {
        // Test that draw handles null app gracefully
        AccelerateTile tile = new AccelerateTile(0, 0, "up", app);
        assertThrows(NullPointerException.class, () -> {
            tile.draw(null);
        });
    }

    @Test
    public void testDirectionSetterAndGetter() {
        // Assuming there are setter and getter for direction (if not, this test can be skipped)
        AccelerateTile tile = new AccelerateTile(0, 0, "up", app);
        assertEquals("up", tile.getDirection());
        // If setter exists
        // tile.setDirection("down");
        // assertEquals("down", tile.getDirection());
    }

    @Test
    public void testTileDimensions() {
        // Test that tile dimensions are set correctly
        AccelerateTile tile = new AccelerateTile(0, 0, "up", app);
        assertEquals(32, tile.width);
        assertEquals(32, tile.height);
    }
}
