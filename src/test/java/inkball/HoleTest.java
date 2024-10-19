package inkball;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashSet;

public class HoleTest {

    static App app;
    static Level level;

    @BeforeAll
    public static void setup() {
        app = new App();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup(); // Initialize the app
        level = new Level(0, app.configReader, app);
    }

    @BeforeEach
    public void beforeEach() {
        // Reset the app and level state before each test
        app.unspawnedBalls = new ArrayList<>();
        app.currentLevel = level;
    }

    @Test
    public void testConstructor() {
        // Test that the Hole constructor initializes correctly
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        assertEquals(100, hole.getX());
        assertEquals(100, hole.getY());
        assertEquals(color, hole.getColor());
        assertEquals(64, hole.getWidth());
        assertEquals(64, hole.getHeight());
        assertNotNull(hole.getHoleImage());
    }

    @Test
    public void testLoadImage() {
        // Test that loadImage loads the correct image based on color
        int color = ColorUtils.colorToNumber("blue");
        Hole hole = new Hole(0, 0, color, app);
        assertNotNull(hole.getHoleImage());
        // Since we cannot check the actual image, we assume if no exception is thrown, it's loaded
    }

    @Test
    public void testDraw() {
        // Test that draw method executes without errors
        int color = ColorUtils.colorToNumber("green");
        Hole hole = new Hole(0, 0, color, app);
        assertDoesNotThrow(() -> hole.draw(app));
    }

    @Test
    public void testAttractBall_WithinRange() {
        // Test that attractBall attracts a ball when within range
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        Ball ball = new Ball(110, 110, color, 12, app);
        float initialXVelocity = ball.getXVelocity();
        float initialYVelocity = ball.getYVelocity();

        hole.attractBall(ball, level, app);

        // Ball should be added to attractedBalls
        assertTrue(hole.getAttractedBalls().contains(ball));
        // Ball's velocity should have changed
        assertNotEquals(initialXVelocity, ball.getXVelocity());
        assertNotEquals(initialYVelocity, ball.getYVelocity());
        // Ball's radius should have decreased
        assertTrue(ball.getRadius() < ball.getInitialRadius());
    }

    @Test
    public void testAttractBall_OutOfRange() {
        // Test that attractBall does not attract a ball when out of range
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        Ball ball = new Ball(200, 200, color, 12, app);
        float initialXVelocity = ball.getXVelocity();
        float initialYVelocity = ball.getYVelocity();

        hole.attractBall(ball, level, app);

        // Ball should not be added to attractedBalls
        assertFalse(hole.getAttractedBalls().contains(ball));
        // Ball's velocity should remain the same
        assertEquals(initialXVelocity, ball.getXVelocity());
        assertEquals(initialYVelocity, ball.getYVelocity());
        // Ball's radius should remain the same
        assertEquals(ball.getInitialRadius(), ball.getRadius());
    }

    @Test
    public void testAttractBall_SuccessfulCapture() {
        // Test that attractBall captures the ball when close enough and colors match
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        Ball ball = new Ball(105, 105, color, 12, app); // Ball close to hole

        // Place ball close enough for capture
        ball.setX(100 + hole.getWidth() / 2);
        ball.setY(100 + hole.getHeight() / 2);
        ball.setIsActive(true);

        hole.attractBall(ball, level, app);

        // Ball should be removed (set as inactive)
        assertFalse(ball.getIsActive());
        // Ball should be removed from attractedBalls
        assertFalse(hole.getAttractedBalls().contains(ball));
    }

    @Test
    public void testAttractBall_UnsuccessfulCapture() {
        // Test that attractBall does not capture the ball when colors do not match
        int holeColor = ColorUtils.colorToNumber("red");
        int ballColor = ColorUtils.colorToNumber("blue");
        Hole hole = new Hole(100, 100, holeColor, app);
        Ball ball = new Ball(105, 105, ballColor, 12, app); // Ball close to hole

        // Place ball close enough for capture
        ball.setX(100 + hole.getWidth() / 2);
        ball.setY(100 + hole.getHeight() / 2);
        ball.setIsActive(true);

        hole.attractBall(ball, level, app);

        // Ball should be deactivated
        assertFalse(ball.getIsActive());
        // Ball should be added back to unspawnedBalls
        assertTrue(app.unspawnedBalls.contains(ball));
        // Ball should be removed from attractedBalls
        assertFalse(hole.getAttractedBalls().contains(ball));
    }

    @Test
    public void testAttractBall_GreyHoleAnyBall() {
        // Test that a grey hole can capture any ball
        int holeColor = ColorUtils.colorToNumber("grey");
        int ballColor = ColorUtils.colorToNumber("yellow");
        Hole hole = new Hole(100, 100, holeColor, app);
        Ball ball = new Ball(105, 105, ballColor, 12, app);

        // Place ball close enough for capture
        ball.setX(100 + hole.getWidth() / 2);
        ball.setY(100 + hole.getHeight() / 2);
        ball.setIsActive(true);

        hole.attractBall(ball, level, app);

        // Ball should be deactivated
        assertFalse(ball.getIsActive());
        // Ball should not be added back to unspawnedBalls
        assertFalse(app.unspawnedBalls.contains(ball));
        // Ball should be removed from attractedBalls
        assertFalse(hole.getAttractedBalls().contains(ball));
    }

    @Test
    public void testAttractBall_GreyBallAnyHole() {
        // Test that a grey ball can be captured by any hole
        int holeColor = ColorUtils.colorToNumber("blue");
        int ballColor = ColorUtils.colorToNumber("grey");
        Hole hole = new Hole(100, 100, holeColor, app);
        Ball ball = new Ball(105, 105, ballColor, 12, app);

        // Place ball close enough for capture
        ball.setX(100 + hole.getWidth() / 2);
        ball.setY(100 + hole.getHeight() / 2);
        ball.setIsActive(true);

        hole.attractBall(ball, level, app);

        // Ball should be deactivated
        assertFalse(ball.getIsActive());
        // Ball should not be added back to unspawnedBalls
        assertFalse(app.unspawnedBalls.contains(ball));
        // Ball should be removed from attractedBalls
        assertFalse(hole.getAttractedBalls().contains(ball));
    }

    @Test
    public void testUpdateAttractedBalls() {
        // Test that updateAttractedBalls removes balls no longer in attraction range
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        Ball ball = new Ball(110, 110, color, 12, app);

        // Manually add ball to attractedBalls
        hole.getAttractedBalls().add(ball);

        // Place ball out of attraction range
        ball.setX(200);
        ball.setY(200);

        hole.updateAttractedBalls();

        // Ball should be removed from attractedBalls
        assertFalse(hole.getAttractedBalls().contains(ball));
        // Ball's radius should be reset
        assertEquals(ball.getInitialRadius(), ball.getRadius());
    }

    @Test
    public void testAttractBall_BallNull() {
        // Test that attractBall handles null ball gracefully
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        assertThrows(NullPointerException.class, () -> {
            hole.attractBall(null, level, app);
        });
    }

    @Test
    public void testAttractBall_LevelNull() {
        // Test that attractBall handles null level gracefully
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        Ball ball = new Ball(110, 110, color, 12, app);
        assertThrows(NullPointerException.class, () -> {
            hole.attractBall(ball, null, app);
        });
    }

    @Test
    public void testAttractBall_AppNull() {
        // Test that attractBall handles null app gracefully
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        Ball ball = new Ball(110, 110, color, 12, app);
        assertThrows(NullPointerException.class, () -> {
            hole.attractBall(ball, level, null);
        });
    }

    @Test
    public void testUpdateAttractedBalls_NoBalls() {
        // Test updateAttractedBalls when there are no attracted balls
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        assertDoesNotThrow(() -> hole.updateAttractedBalls());
    }

    @Test
    public void testAttractBall_BallJustOutOfRange() {
        // Test that attractBall does not attract a ball just outside the range
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        Ball ball = new Ball(100 + 32 + 1, 100, color, 12, app); // Just outside range

        hole.attractBall(ball, level, app);

        // Ball should not be attracted
        assertFalse(hole.getAttractedBalls().contains(ball));
    }

    @Test
    public void testAttractBall_BallJustInRange() {
        // Test that attractBall attracts a ball just within the range
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        float distance = 32 - 0.1f;
        int x = Math.round(100 + distance);  // This rounds to the nearest integer
        Ball ball = new Ball(x, 100, color, 12, app);

        hole.attractBall(ball, level, app);

        // Ball should be attracted
        assertTrue(hole.getAttractedBalls().contains(ball));
    }

    @Test
    public void testAttractBall_BallRadiusCannotBeNegative() {
        // Test that the ball's radius does not become negative during attraction
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        Ball ball = new Ball(100, 100, color, 12, app);

        // Simulate ball very close to hole center
        ball.setX(100 + hole.getWidth() / 2);
        ball.setY(100 + hole.getHeight() / 2);

        hole.attractBall(ball, level, app);

        // Ball's radius should not be negative
        assertTrue(ball.getRadius() >= 0);
    }

    @Test
    public void testAttractBall_DecreasesRadiusProportionally() {
        // Test that the ball's radius decreases proportionally to the distance
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        Ball ball = new Ball(110, 110, color, 12, app);

        float initialRadius = ball.getInitialRadius();
        float distanceToHole = PVector.dist(new PVector(ball.getX(), ball.getY()),
                new PVector(100 + hole.getWidth() / 2, 100 + hole.getHeight() / 2));

        hole.attractBall(ball, level, app);

        float expectedRadius = initialRadius * (distanceToHole / 32.0f);
        expectedRadius = Math.max(expectedRadius, 0);

        assertEquals(expectedRadius, ball.getRadius(), 0.01);
    }

    @Test
    public void testGettersAndSetters() {
        // Test the getters and setters of the Hole class
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        hole.setX(200);
        hole.setY(300);
        hole.setWidth(128);
        hole.setHeight(128);
        hole.setColor(ColorUtils.colorToNumber("blue"));
        PImage image = new PImage();
        hole.setHoleImage(image);

        assertEquals(200, hole.getX());
        assertEquals(300, hole.getY());
        assertEquals(128, hole.getWidth());
        assertEquals(128, hole.getHeight());
        assertEquals(ColorUtils.colorToNumber("blue"), hole.getColor());
        assertEquals(image, hole.getHoleImage());
    }

    @Test
    public void testAttractionFactor() {
        // Test that getAttractionFactor returns the correct value
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        assertEquals(0.005f, hole.getAttractionFactor());
    }

    @Test
    public void testAttractBall_BallAlreadyAttracted() {
        // Test that attractBall does not duplicate balls in attractedBalls
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        Ball ball = new Ball(110, 110, color, 12, app);

        hole.attractBall(ball, level, app);
        int attractedSizeAfterFirstAttract = hole.getAttractedBalls().size();

        hole.attractBall(ball, level, app);
        int attractedSizeAfterSecondAttract = hole.getAttractedBalls().size();

        assertEquals(attractedSizeAfterFirstAttract, attractedSizeAfterSecondAttract);
    }

    @Test
    public void testAttractBall_BallInactive() {
        // Test that attractBall does not process inactive balls
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        Ball ball = new Ball(110, 110, color, 12, app);
        ball.setIsActive(false);

        hole.attractBall(ball, level, app);

        // Ball should not be added to attractedBalls
        assertFalse(hole.getAttractedBalls().contains(ball));
    }

    @Test
    public void testAttractBall_BallRadiusResetWhenOutOfRange() {
        // Test that ball's radius resets when it moves out of attraction range
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        Ball ball = new Ball(110, 110, color, 12, app);

        hole.attractBall(ball, level, app);
        // Ball is now attracted
        assertTrue(hole.getAttractedBalls().contains(ball));

        // Move ball out of range
        ball.setX(200);
        ball.setY(200);

        hole.attractBall(ball, level, app);

        // Ball should be removed from attractedBalls
        assertFalse(hole.getAttractedBalls().contains(ball));
        // Ball's radius should be reset
        assertEquals(ball.getInitialRadius(), ball.getRadius());
    }

    @Test
    public void testAttractBall_CaptureWithNoUnspawnedBalls() {
        // Test that when capture fails and unspawnedBalls is empty, ball is respawned immediately
        app.unspawnedBalls.clear(); // Ensure unspawnedBalls is empty

        int holeColor = ColorUtils.colorToNumber("red");
        int ballColor = ColorUtils.colorToNumber("blue");
        Hole hole = new Hole(100, 100, holeColor, app);
        Ball ball = new Ball(105, 105, ballColor, 12, app);

        // Place ball close enough for capture
        ball.setX(100 + hole.getWidth() / 2);
        ball.setY(100 + hole.getHeight() / 2);
        ball.setIsActive(true);

        hole.attractBall(ball, level, app);

        // Ball should be respawned immediately
        assertTrue(app.currentLevel.getBalls().contains(ball));
        assertTrue(ball.getIsActive());
    }

    @Test
    public void testAttractBall_CaptureFailsWithUnspawnedBalls() {
        // Test that when capture fails and unspawnedBalls is not empty, ball is added back to unspawnedBalls
        app.unspawnedBalls.clear();
        Ball unspawnedBall = new Ball(0, 0, ColorUtils.colorToNumber("green"), 12, app);
        app.unspawnedBalls.add(unspawnedBall); // Add a ball to unspawnedBalls

        int holeColor = ColorUtils.colorToNumber("red");
        int ballColor = ColorUtils.colorToNumber("blue");
        Hole hole = new Hole(100, 100, holeColor, app);
        Ball ball = new Ball(105, 105, ballColor, 12, app);

        // Place ball close enough for capture
        ball.setX(100 + hole.getWidth() / 2);
        ball.setY(100 + hole.getHeight() / 2);
        ball.setIsActive(true);

        hole.attractBall(ball, level, app);

        // Ball should be added back to unspawnedBalls
        assertTrue(app.unspawnedBalls.contains(ball));
        // Ball should not be active
        assertFalse(ball.getIsActive());
    }

    @Test
    public void testAttractBall_BallResetWhenNoLongerAttracted() {
        // Test that ball resets its radius when no longer attracted
        int color = ColorUtils.colorToNumber("red");
        Hole hole = new Hole(100, 100, color, app);
        Ball ball = new Ball(110, 110, color, 12, app);

        hole.attractBall(ball, level, app); // Attract ball
        assertTrue(ball.getRadius() < ball.getInitialRadius());

        // Move ball out of range
        ball.setX(200);
        ball.setY(200);

        hole.attractBall(ball, level, app); // Update attraction

        // Ball's radius should be reset
        assertEquals(ball.getInitialRadius(), ball.getRadius());
    }
}
