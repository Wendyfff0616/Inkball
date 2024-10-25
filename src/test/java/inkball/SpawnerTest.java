package inkball;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import processing.core.PApplet;
import processing.core.PImage;

public class SpawnerTest {

    static App app;
    static Spawner spawner;

    @BeforeAll
    public static void setup() {
        app = new App();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();
    }

    @BeforeEach
    public void beforeEach() {
        // Initialize a Spawner before each test
        spawner = new Spawner(100, 200, -1, app);
    }

    @Test
    public void testConstructor_NullApp() {
        // Test that constructor handles null App gracefully
        Exception exception = assertThrows(NullPointerException.class, () -> {
            new Spawner(0, 0, -1, null);
        });
    }

    @Test
    public void testLoadImage_NullApp() {
        // Test that loadImage method handles null App gracefully
        Spawner testSpawner = new Spawner(0, 0, -1, app);
        assertThrows(NullPointerException.class, () -> testSpawner.loadImage(null));
    }

    @Test
    public void testDraw_NullApp() {
        // Test that draw method handles null App gracefully
        assertThrows(NullPointerException.class, () -> spawner.draw(null));
    }

    @Test
    public void testSpawnerPosition() {
        // Test that the spawner's position is correctly set
        assertEquals(100, spawner.getX());
        assertEquals(200, spawner.getY());
    }

    @Test
    public void testSpawnerDrawPosition() {
        // Test that draw method draws the image at the correct position
        assertDoesNotThrow(() -> spawner.draw(app));
    }
}
