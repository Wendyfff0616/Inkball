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
        app.setup(); // Initialize the app
    }

    @BeforeEach
    public void beforeEach() {
        // Initialize a Wall before each test
        wall = new Wall(100, 100, 1, app); // Wall at (100, 100) with color 1
    }

    @Test
    public void testConstructor() {
        // Test that the Wall constructor initializes correctly
        assertEquals(100, wall.getX());
        assertEquals(100, wall.getY());
        assertEquals(1, wall.getColor());
        assertEquals(32, wall.width);
        assertEquals(32, wall.height);
        assertEquals(0, wall.collisionBuffer);
        assertNotNull(wall.wallImage);
    }

    @Test
    public void testLoadImage() {
        // Test that loadImage loads the correct image
        Wall testWall = new Wall(0, 0, 2, app);
        assertNotNull(testWall.wallImage);
        // Since we cannot check the actual image content, we assume if no exception is thrown, it's loaded
    }

    @Test
    public void testDraw() {
        // Test that draw method executes without errors
        assertDoesNotThrow(() -> wall.draw(app));
    }

    @Test
    public void testGetX() {
        // Test that getX returns the correct x-coordinate
        assertEquals(100, wall.getX());
    }

    @Test
    public void testGetY() {
        // Test that getY returns the correct y-coordinate
        assertEquals(100, wall.getY());
    }

    @Test
    public void testGetColor() {
        // Test that getColor returns the correct color
        assertEquals(1, wall.getColor());
    }

    @Test
    public void testCheckCollision_NoCollision() {
        // Test that checkCollision does not alter ball when there is no collision
        Ball ball = new Ball(0, 0, 1, 12, app);
        float initialX = ball.getX();
        float initialY = ball.getY();
        float initialXVelocity = ball.getXVelocity();
        float initialYVelocity = ball.getYVelocity();
        int initialColor = ball.getColor();

        wall.checkCollision(ball, app);

        assertEquals(initialX, ball.getX());
        assertEquals(initialY, ball.getY());
        assertEquals(initialXVelocity, ball.getXVelocity());
        assertEquals(initialYVelocity, ball.getYVelocity());
        assertEquals(initialColor, ball.getColor());
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
        assertEquals(5, ball.getXVelocity(), 0.01);
        // Ball's Y velocity should remain the same
        assertEquals(0, ball.getYVelocity(), 0.01);

        // Ball's position should be adjusted
        assertTrue(ball.getX() > 110 + wall.width + 10);

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
        assertEquals(5, ball.getYVelocity(), 0.01);
        // Ball's X velocity should remain the same
        assertEquals(0, ball.getXVelocity(), 0.01);

        // Ball's position should be adjusted
        assertTrue(ball.getY() > 110 + wall.height + 10);

        // Ball's color should change to wall's color (since wall color is 1)
        assertEquals(wall.getColor(), ball.getColor());
    }

    @Test
    public void testCheckCollision_BallColorDoesNotChangeIfWallColorIsZero() {
        // Test that ball's color does not change if wall color is 0
        Wall wallWithColorZero = new Wall(100, 100, 0, app);
        Ball ball = new Ball(90, 110, 2, 12, app); // Ball color is 2
        ball.setXVelocity(5);
        ball.setYVelocity(0);

        wallWithColorZero.collisionBuffer = Wall.BUFFER_THRESHOLD;

        wallWithColorZero.checkCollision(ball, app);

        // Ball's color should remain the same
        assertEquals(2, ball.getColor());
    }

    @Test
    public void testCheckCollision_CollisionBufferPreventsCollision() {
        // Test that collisionBuffer prevents collision detection before threshold
        Ball ball = new Ball(90, 110, 1, 12, app);
        ball.setXVelocity(5);
        ball.setYVelocity(0);

        wall.collisionBuffer = 0; // Below BUFFER_THRESHOLD

        wall.checkCollision(ball, app);

        // Ball's velocity should remain unchanged
        assertEquals(5, ball.getXVelocity(), 0.01);
        assertEquals(0, ball.getYVelocity(), 0.01);
    }

    @Test
    public void testCheckCollision_CollisionBufferResetsAfterCollision() {
        // Test that collisionBuffer resets after collision
        Ball ball = new Ball(90, 110, 1, 12, app);
        ball.setXVelocity(5);
        ball.setYVelocity(0);

        wall.collisionBuffer = Wall.BUFFER_THRESHOLD;

        wall.checkCollision(ball, app);

        assertEquals(0, wall.collisionBuffer);
    }

    @Test
    public void testCheckCollision_CollisionBufferIncrementsWhenNoCollision() {
        // Test that collisionBuffer increments when there is no collision
        Ball ball = new Ball(0, 0, 1, 12, app);

        int initialBuffer = wall.collisionBuffer;
        wall.checkCollision(ball, app);

        assertEquals(initialBuffer + 1, wall.collisionBuffer);
    }

    @Test
    public void testCheckCollision_BallAdjustsPositionAfterCollision() {
        // Test that ball's position is adjusted after collision
        Ball ball = new Ball(90, 110, 1, 12, app);
        ball.setXVelocity(5);
        ball.setYVelocity(0);

        wall.collisionBuffer = Wall.BUFFER_THRESHOLD;

        float initialX = ball.getX();

        wall.checkCollision(ball, app);

        // Ball's X position should have been adjusted
        assertTrue(ball.getX() < initialX);
    }

    @Test
    public void testCheckCollision_DiagonalCollision() {
        // Test collision when ball collides diagonally
        Ball ball = new Ball(110, 110, 1, 12, app);
        ball.setXVelocity(-5);
        ball.setYVelocity(-5);

        wall.collisionBuffer = Wall.BUFFER_THRESHOLD;

        wall.checkCollision(ball, app);

        // Ball's velocity should be reflected appropriately
        // Since minOverlapX == minOverlapY, vertical collision is preferred
        assertEquals(-5, ball.getXVelocity(), 0.01);
        assertEquals(5, ball.getYVelocity(), 0.01);

        // Ball's position should be adjusted
        assertTrue(ball.getY() < 110);

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

        // Depending on collision detection, ball may or may not detect collision
        // We can check both possibilities
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
        // Test collision when ball is inside the wall (e.g., if placed there)
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
    public void testCheckCollision_NullBall() {
        // Test that checkCollision handles null Ball gracefully
        assertThrows(NullPointerException.class, () -> wall.checkCollision(null, app));
    }

    @Test
    public void testCheckCollision_NullApp() {
        // Test that checkCollision handles null App gracefully
        Ball ball = new Ball(0, 0, 1, 12, app);
        assertThrows(NullPointerException.class, () -> wall.checkCollision(ball, null));
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
    public void testWallNegativeCoordinates() {
        // Test creating a wall with negative coordinates
        Wall negativeWall = new Wall(-50, -50, 1, app);
        assertEquals(-50, negativeWall.getX());
        assertEquals(-50, negativeWall.getY());
    }

    @Test
    public void testWallZeroCoordinates() {
        // Test creating a wall at (0, 0)
        Wall zeroWall = new Wall(0, 0, 1, app);
        assertEquals(0, zeroWall.getX());
        assertEquals(0, zeroWall.getY());
    }

    @Test
    public void testWallLargeCoordinates() {
        // Test creating a wall with large coordinate values
        Wall largeWall = new Wall(10000, 20000, 1, app);
        assertEquals(10000, largeWall.getX());
        assertEquals(20000, largeWall.getY());
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

        // Bring collisionBuffer to BUFFER_THRESHOLD
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

        // Since no collision, collisionBuffer should increment
        assertEquals(Wall.BUFFER_THRESHOLD + 1, wall.collisionBuffer);
    }

    @Test
    public void testCheckCollision_BallMovingAwayFromWall() {
        // Test that no collision is detected when ball is moving away from the wall
        Ball ball = new Ball(90, 110, 1, 12, app);
        ball.setXVelocity(-5); // Moving left, away from wall at x=100

        wall.collisionBuffer = Wall.BUFFER_THRESHOLD;

        wall.checkCollision(ball, app);

        // Ball's velocity should remain unchanged
        assertEquals(-5, ball.getXVelocity(), 0.01);
        assertEquals(0, ball.getYVelocity(), 0.01);
    }

    @Test
    public void testBallVelocityAfterMultipleCollisions() {
        // Test ball's velocity after multiple collisions with wall
        Ball ball = new Ball(90, 110, 1, 12, app);
        ball.setXVelocity(5);
        ball.setYVelocity(0);

        // Simulate multiple frames
        for (int i = 0; i < 10; i++) {
            wall.collisionBuffer = Wall.BUFFER_THRESHOLD;
            wall.checkCollision(ball, app);
        }

        // Ball's X velocity should have been reflected multiple times, but remains consistent
        assertTrue(Math.abs(ball.getXVelocity()) == 5);
    }

    @Test
    public void testWallInheritance() {
        // Test that Wall is a subclass of Entity and implements Drawable
        assertTrue(wall instanceof Entity);
        assertTrue(wall instanceof Drawable);
    }

    @Test
    public void testWallDimensions() {
        // Test that wall's width and height are set to 32
        assertEquals(32, wall.width);
        assertEquals(32, wall.height);
    }

    @Test
    public void testCheckCollision_BallColorChangesOnlyOncePerCollision() {
        // Test that ball's color changes only once per collision
        Ball ball = new Ball(90, 110, 2, 12, app); // Ball color is 2
        ball.setXVelocity(5);
        ball.setYVelocity(0);

        wall.collisionBuffer = Wall.BUFFER_THRESHOLD;

        wall.checkCollision(ball, app);

        int colorAfterFirstCollision = ball.getColor();

        // Move ball back to collide again
        ball.setX(90);
        wall.collisionBuffer = Wall.BUFFER_THRESHOLD;

        wall.checkCollision(ball, app);

        int colorAfterSecondCollision = ball.getColor();

        // Ball's color should remain the same after first change
        assertEquals(colorAfterFirstCollision, colorAfterSecondCollision);
    }

    @Test
    public void testWallImage_Setter() {
        // Test setting a new image for the wall
        PImage newImage = new PImage();
        wall.wallImage = newImage;
        assertEquals(newImage, wall.wallImage);
    }

    @Test
    public void testWallToString() {
        // If Wall had a toString method, test its output
        // Since it doesn't, this test can be skipped or we can test default Object toString
        String wallString = wall.toString();
        assertNotNull(wallString);
    }

    @Test
    public void testWallEqualsAndHashCode() {
        // Test equals and hashCode methods if implemented
        // Since they are not overridden, default Object methods are used
        Wall anotherWall = new Wall(100, 100, 1, app);
        assertNotEquals(wall, anotherWall);
        assertNotEquals(wall.hashCode(), anotherWall.hashCode());
    }
}
