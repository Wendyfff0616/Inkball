package inkball;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import processing.core.PApplet;
import processing.core.PImage;

public class YellowTileTest {

    static App app;
    static YellowTile yellowTile;

    @BeforeAll
    public static void setup() {
        app = new App();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();

        // Wait for AppTest to be initialized
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void beforeEach() {
        yellowTile = new YellowTile(0, App.TOPBAR, -1, app);
    }

    @Test
    public void testConstructor() {
        // Test that the YellowTile constructor initializes correctly
        assertEquals(0, yellowTile.getX());
        assertEquals(App.TOPBAR, yellowTile.getY());
        assertEquals(0, yellowTile.getDirection()); // Starts moving right
        assertNotNull(yellowTile.getYellowTileImage());
    }

    @Test
    public void testLoadImage() {
        // Test that loadImage loads the correct image
        YellowTile tile = new YellowTile(0, 0, -1, app);
        assertNotNull(tile.getYellowTileImage());
    }

    @Test
    public void testDraw() {
        // Test that draw method executes without errors
        assertDoesNotThrow(() -> yellowTile.draw(app));
    }

    @Test
    public void testUpdate_MovingRight() {
        // Test update when moving right
        yellowTile.setDirection(0); // Moving right
        int initialX = yellowTile.getX();
        yellowTile.update(app);
        assertEquals(initialX + App.CELLSIZE, yellowTile.getX());
        assertEquals(yellowTile.getDirection(), 0);
    }

    @Test
    public void testUpdate_MovingDown() {
        // Test update when moving down
        yellowTile.setDirection(1); // Moving down
        int initialY = yellowTile.getY();
        yellowTile.update(app);
        assertEquals(initialY + App.CELLSIZE, yellowTile.getY());
        assertEquals(yellowTile.getDirection(), 1);
    }

    @Test
    public void testUpdate_MovingLeft() {
        // Test update when moving left
        yellowTile.setDirection(2); // Moving left
        yellowTile.setX(App.WIDTH - App.CELLSIZE); // Start from the right edge
        int initialX = yellowTile.getX();
        yellowTile.update(app);
        assertEquals(initialX - App.CELLSIZE, yellowTile.getX());
        assertEquals(yellowTile.getDirection(), 2);
    }

    @Test
    public void testUpdate_MovingUp() {
        // Test update when moving up
        yellowTile.setDirection(3); // Moving up
        yellowTile.setY(App.HEIGHT - App.CELLSIZE); // Start from the bottom edge
        int initialY = yellowTile.getY();
        yellowTile.update(app);
        assertEquals(initialY - App.CELLSIZE, yellowTile.getY());
        assertEquals(yellowTile.getDirection(), 3);
    }

    @Test
    public void testUpdate_ChangeDirectionAtRightEdge() {
        // Test that direction changes from right to down at right edge
        yellowTile.setDirection(0); // Moving right
        yellowTile.setX(App.WIDTH - App.CELLSIZE); // At right edge
        yellowTile.update(app);
        assertEquals(App.WIDTH - App.CELLSIZE, yellowTile.getX()); // Should not exceed right edge
        assertEquals(1, yellowTile.getDirection()); // Direction should change to down
    }

    @Test
    public void testUpdate_ChangeDirectionAtBottomEdge() {
        // Test that direction changes from down to left at bottom edge
        yellowTile.setDirection(1); // Moving down
        yellowTile.setY(App.HEIGHT - App.CELLSIZE); // At bottom edge
        yellowTile.update(app);
        assertEquals(App.HEIGHT - App.CELLSIZE, yellowTile.getY()); // Should not exceed bottom edge
        assertEquals(2, yellowTile.getDirection()); // Direction should change to left
    }

    @Test
    public void testUpdate_ChangeDirectionAtLeftEdge() {
        // Test that direction changes from left to up at left edge
        yellowTile.setDirection(2); // Moving left
        yellowTile.setX(0); // At left edge
        yellowTile.update(app);
        assertEquals(0, yellowTile.getX()); // Should not go below 0
        assertEquals(3, yellowTile.getDirection()); // Direction should change to up
    }

    @Test
    public void testUpdate_ChangeDirectionAtTopEdge() {
        // Test that direction changes from up to right at top edge
        yellowTile.setDirection(3); // Moving up
        yellowTile.setY(App.TOPBAR); // At top edge
        yellowTile.update(app);
        assertEquals(App.TOPBAR, yellowTile.getY()); // Should not go above TOPBAR
        assertEquals(0, yellowTile.getDirection()); // Direction should change to right
    }

    @Test
    public void testGettersAndSetters() {
        // Test getters and setters
        yellowTile.setX(100);
        yellowTile.setY(150);
        yellowTile.setDirection(2);
        assertEquals(100, yellowTile.getX());
        assertEquals(150, yellowTile.getY());
        assertEquals(2, yellowTile.getDirection());
    }

    @Test
    public void testUpdate_FullCycle() {
        // Test that the tile moves around the entire perimeter
        int steps = ((App.WIDTH - App.CELLSIZE) / App.CELLSIZE + (App.HEIGHT - App.TOPBAR - App.CELLSIZE) / App.CELLSIZE) * 2;
        for (int i = 0; i < steps; i++) {
            yellowTile.update(app);
        }
        assertEquals(0, yellowTile.getX());
        assertEquals(App.TOPBAR, yellowTile.getY());
        assertEquals(0, yellowTile.getDirection()); // Should be back to moving right
    }

    @Test
    public void testUpdate_PositionWithinBounds() {
        // Test that the tile's position stays within bounds after multiple updates
        for (int i = 0; i < 1000; i++) {
            yellowTile.update(app);
            assertTrue(yellowTile.getX() >= 0 && yellowTile.getX() <= App.WIDTH - App.CELLSIZE);
            assertTrue(yellowTile.getY() >= App.TOPBAR && yellowTile.getY() <= App.HEIGHT - App.CELLSIZE);
        }
    }

    @Test
    public void testSetYellowTileImage() {
        // Test setting a new image for the yellow tile
        PImage newImage = new PImage();
        yellowTile.setYellowTileImage(newImage);
        assertEquals(newImage, yellowTile.getYellowTileImage());
    }

    @Test
    public void testSetColor() {
        // Test setting a new color
        yellowTile.setColor(2);
        assertEquals(2, yellowTile.getColor());
    }

    @Test
    public void testDirectionSetter_InvalidValue() {
        // Test setting an invalid direction
        yellowTile.setDirection(5); // Invalid direction
        yellowTile.update(app);
        assertTrue(yellowTile.getX() >= 0 && yellowTile.getX() <= App.WIDTH - App.CELLSIZE);
        assertTrue(yellowTile.getY() >= App.TOPBAR && yellowTile.getY() <= App.HEIGHT - App.CELLSIZE);
    }

    @Test
    public void testUpdate_InvalidDirection() {
        // Test update with invalid direction
        yellowTile.setDirection(-1); // Invalid direction
        yellowTile.update(app);
        assertEquals(0, yellowTile.getX());
        assertEquals(App.TOPBAR, yellowTile.getY());
    }
}
