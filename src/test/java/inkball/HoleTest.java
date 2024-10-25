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
        app.setup();
        level = new Level(0, app.configReader, app);
    }

    @BeforeEach
    public void beforeEach() {
        // Reset the app and level state before each test
        app.unspawnedBalls = new ArrayList<>();
        app.currentLevel = level;
    }

    @Test
    public void testAttractBall_OutOfRange() {
        // Test that attractBall does not attract a ball when out of range
        int color = ColorUtils.colorToNumber("green");
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
        int color = ColorUtils.colorToNumber("green");
        Hole hole = new Hole(100, 100, color, app);
        Ball ball = new Ball(105, 105, color, 12, app); // Ball close to hole

        // Place ball close enough for capture
        ball.setX(100 + hole.getWidth() / 2);
        ball.setY(100 + hole.getHeight() / 2);
        ball.setIsActive(true);

        hole.attractBall(ball, level, app);

        // Ball should be deactivated
        assertFalse(ball.getIsActive());
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
        int color = ColorUtils.colorToNumber("orange");
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
    public void testCaptureFailedAndBallHandling() {
        // Test that a grey ball can be captured by any hole
        int holeColor = ColorUtils.colorToNumber("blue");
        int ballColor = ColorUtils.colorToNumber("orange");
        Hole hole = new Hole(100, 100, holeColor, app);
        Ball ball = new Ball(105, 105, ballColor, 12, app);

        // Place ball close enough for capture
        ball.setX(100 + hole.getWidth() / 2);
        ball.setY(100 + hole.getHeight() / 2);
        ball.setIsActive(true);

        hole.attractBall(ball, level, app);

        // Ball should not be deactivated
        assertTrue(ball.getIsActive());
        // Ball should be added back to unspawnedBalls
        assertFalse(app.unspawnedBalls.contains(ball));
        // Ball should be removed from attractedBalls
        assertFalse(hole.getAttractedBalls().contains(ball));
    }

    @Test
    public void testGettersAndSetters() {
        // Test the getters and setters of the Hole class
        int color = ColorUtils.colorToNumber("green");
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
}
