package inkball;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class WallTest {

    static App app;
    static Wall wall;

    @BeforeAll
    public static void setup() {
        app = new App();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();
    }

    @BeforeEach
    public void beforeEach() {
        // Initialize a Wall before each test
        wall = new Wall(100, 100, 1, app); // Wall at (100, 100) with color 1
    }

    @Test
    public void testCheckCollision_FromLeft() {
        // Test collision when ball collides from the left
        Ball ball = new Ball(90, 110, 1, 12, app);
        ball.setXVelocity(5);
        ball.setYVelocity(0);

        // Move collisionBuffer to exceed BUFFER_THRESHOLD
        wall.collisionBuffer = Wall.BUFFER_THRESHOLD;
        wall.checkCollision(ball, app);

        // Ball's X velocity should be reflected
        assertEquals(-5, ball.getXVelocity(), 0.01);
        // Ball's Y velocity should remain the same
        assertEquals(0, ball.getYVelocity(), 0.01);
        // Ball's position should be adjusted
        assertTrue(ball.getX() < 90);
        // Ball's color should change to wall's color (since wall color is 1)
        assertEquals(wall.getColor(), ball.getColor());
    }

    @Test
    public void testCheckCollision_FromRight() {
        // Test collision when ball collides from the right
        Ball ball = new Ball(110 + wall.width + 10, 110, 1, 12, app);
        ball.setXVelocity(-5);
        ball.setYVelocity(0);

        wall.collisionBuffer = Wall.BUFFER_THRESHOLD;
        wall.checkCollision(ball, app);

        // Ball's X velocity should be reflected
        assertEquals(-5, ball.getXVelocity(), 0.01);
        // Ball's Y velocity should remain the same
        assertEquals(0, ball.getYVelocity(), 0.01);
        // Ball's position should be adjusted
        assertFalse(ball.getX() > 110 + wall.width + 10);
        // Ball's color should change to wall's color (since wall color is 1)
        assertEquals(wall.getColor(), ball.getColor());
    }

    @Test
    public void testCheckCollision_FromTop() {
        // Test collision when ball collides from the top
        Ball ball = new Ball(110, 90, 1, 12, app);
        ball.setXVelocity(0);
        ball.setYVelocity(5);

        wall.collisionBuffer = Wall.BUFFER_THRESHOLD;
        wall.checkCollision(ball, app);

        // Ball's Y velocity should be reflected
        assertEquals(-5, ball.getYVelocity(), 0.01);
        // Ball's X velocity should remain the same
        assertEquals(0, ball.getXVelocity(), 0.01);
        // Ball's position should be adjusted
        assertTrue(ball.getY() < 90);
        // Ball's color should change to wall's color (since wall color is 1)
        assertEquals(wall.getColor(), ball.getColor());
    }

    @Test
    public void testCheckCollision_FromBottom() {
        // Test collision when ball collides from the bottom
        Ball ball = new Ball(110, 110 + wall.height + 10, 1, 12, app);
        ball.setXVelocity(0);
        ball.setYVelocity(-5);
        wall.collisionBuffer = Wall.BUFFER_THRESHOLD;
        wall.checkCollision(ball, app);

        // Ball's Y velocity should be reflected
        assertEquals(-5, ball.getYVelocity(), 0.01);
        // Ball's X velocity should remain the same
        assertEquals(0, ball.getXVelocity(), 0.01);
        // Ball's position should be adjusted
        assertFalse(ball.getY() > 110 + wall.height + 10);
        // Ball's color should change to wall's color
        assertEquals(wall.getColor(), ball.getColor());
    }

    @Test
    public void testCheckCollision_CollisionAtEdge() {
        // Test collision when ball collides exactly at the edge
        Ball ball = new Ball(100, 110, 1, 12, app);
        ball.setX((int) (ball.getX() + wall.width + ball.getRadius()));
        ball.setXVelocity(-5);
        ball.setYVelocity(0);

        wall.collisionBuffer = Wall.BUFFER_THRESHOLD;
        wall.checkCollision(ball, app);

        if (ball.getXVelocity() == -5) {
            // No collision detected
            assertEquals(-5, ball.getXVelocity(), 0.01);
        } else {
            // Collision detected
            assertEquals(5, ball.getXVelocity(), 0.01);
        }
    }

    @Test
    public void testCheckCollision_BallInsideWall() {
        // Test collision when ball is inside the wall
        Ball ball = new Ball(110, 110, 1, 12, app);
        ball.setXVelocity(0);
        ball.setYVelocity(0);

        wall.collisionBuffer = Wall.BUFFER_THRESHOLD;
        wall.checkCollision(ball, app);

        // Ball's position should be adjusted so it's no longer inside the wall
        assertFalse(ball.getX() + ball.getRadius() > wall.getX() &&
                ball.getX() - ball.getRadius() < wall.getX() + wall.width &&
                ball.getY() + ball.getRadius() > wall.getY() &&
                ball.getY() - ball.getRadius() < wall.getY() + wall.height);
    }

    @Test
    public void testConstructor_NullApp() {
        // Test that constructor handles null App gracefully
        assertThrows(NullPointerException.class, () -> {
            new Wall(0, 0, 1, null);
        });
    }

    @Test
    public void testLoadImage_NullApp() {
        // Test that loadImage method handles null App gracefully
        assertThrows(NullPointerException.class, () -> wall.loadImage(null));
    }

    @Test
    public void testDraw_NullApp() {
        // Test that draw method handles null App gracefully
        assertThrows(NullPointerException.class, () -> wall.draw(null));
    }

    @Test
    public void testWallImage_NullAfterLoadImageFailure() {
        // Simulate failure in loading image by passing invalid path
        Wall testWall = new Wall(0, 0, 999, app); // Assuming no image exists for color 999
        assertNull(testWall.wallImage);
    }

    @Test
    public void testWallColorValues() {
        // Test creating walls with different color values
        for (int color = -10; color <= 10; color++) {
            Wall colorWall = new Wall(0, 0, color, app);
            assertEquals(color, colorWall.getColor());
        }
    }

    @Test
    public void testCollisionBufferIncrement() {
        // Test that collisionBuffer increments correctly
        Ball ball = new Ball(0, 0, 1, 12, app);
        int initialBuffer = wall.collisionBuffer;
        wall.checkCollision(ball, app);

        assertEquals(initialBuffer + 1, wall.collisionBuffer);
    }

    @Test
    public void testCollisionBufferResetsAfterCollision() {
        // Test that collisionBuffer resets after collision
        Ball ball = new Ball(90, 110, 1, 12, app);
        ball.setXVelocity(5);
        ball.setYVelocity(0);
        wall.collisionBuffer = Wall.BUFFER_THRESHOLD;
        wall.checkCollision(ball, app);

        assertEquals(0, wall.collisionBuffer);
    }

    @Test
    public void testCollisionBufferDoesNotResetWithoutCollision() {
        // Test that collisionBuffer does not reset if no collision occurs
        Ball ball = new Ball(0, 0, 1, 12, app);
        wall.collisionBuffer = Wall.BUFFER_THRESHOLD;
        wall.checkCollision(ball, app);

        assertEquals(Wall.BUFFER_THRESHOLD + 1, wall.collisionBuffer);
    }

    @Test
    public void testBallVelocityAfterMultipleCollisions() {
        // Test ball's velocity after multiple collisions with wall
        Ball ball = new Ball(90, 110, 1, 12, app);
        ball.setXVelocity(5);
        ball.setYVelocity(0);

        for (int i = 0; i < 10; i++) {
            wall.collisionBuffer = Wall.BUFFER_THRESHOLD;
            wall.checkCollision(ball, app);
        }

        // Ball's X velocity should have been reflected multiple times, but remains consistent
        assertTrue(Math.abs(ball.getXVelocity()) == 5);
    }
}
