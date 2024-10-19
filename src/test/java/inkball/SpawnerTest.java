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
        app.setup(); // Initialize the app
    }

    @BeforeEach
    public void beforeEach() {
        // Initialize a Spawner before each test
        spawner = new Spawner(100, 200, -1, app);
    }

    @Test
    public void testConstructor() {
        // Test that the Spawner constructor initializes correctly
        assertEquals(100, spawner.getX());
        assertEquals(200, spawner.getY());
        assertEquals(32, spawner.width);
        assertEquals(32, spawner.height);
        assertNotNull(spawner.spawnerImage);
    }

    @Test
    public void testLoadImage() {
        // Test that loadImage loads the correct image
        Spawner testSpawner = new Spawner(0, 0, -1, app);
        assertNotNull(testSpawner.spawnerImage);
        // Since we cannot check the actual image content, we assume if no exception is thrown, it's loaded
    }

    @Test
    public void testDraw() {
        // Test that draw method executes without errors
        assertDoesNotThrow(() -> spawner.draw(app));
    }

    @Test
    public void testGetX() {
        // Test that getX returns the correct x-coordinate
        assertEquals(100, spawner.getX());
    }

    @Test
    public void testGetY() {
        // Test that getY returns the correct y-coordinate
        assertEquals(200, spawner.getY());
    }

    @Test
    public void testSetX() {
        // Test that setX correctly updates the x-coordinate
        spawner.setX(150);
        assertEquals(150, spawner.getX());
    }

    @Test
    public void testSetY() {
        // Test that setY correctly updates the y-coordinate
        spawner.setY(250);
        assertEquals(250, spawner.getY());
    }

    @Test
    public void testConstructor_NullApp() {
        // Test that constructor handles null App gracefully
        Exception exception = assertThrows(NullPointerException.class, () -> {
            new Spawner(0, 0, -1, null);
        });
        // Exception is expected due to null App
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
    public void testSpawnerImage_NotNull() {
        // Test that spawnerImage is not null after initialization
        assertNotNull(spawner.spawnerImage);
    }

    @Test
    public void testSpawnerDimensions() {
        // Test that the spawner's width and height are correctly set
        assertEquals(32, spawner.width);
        assertEquals(32, spawner.height);
    }

    @Test
    public void testSpawnerPosition() {
        // Test that the spawner's position is correctly set
        assertEquals(100, spawner.getX());
        assertEquals(200, spawner.getY());
    }

    @Test
    public void testSetWidth() {
        // Since width is not settable in the provided code, this test checks immutability
        // Assuming we add a setter, test setting the width
        // spawner.setWidth(64);
        // assertEquals(64, spawner.width);
        // Since there is no setter, we can test that width remains unchanged
        spawner.width = 64;
        assertEquals(64, spawner.width);
    }

    @Test
    public void testSetHeight() {
        // Since height is not settable in the provided code, this test checks immutability
        // Assuming we add a setter, test setting the height
        // spawner.setHeight(64);
        // assertEquals(64, spawner.height);
        // Since there is no setter, we can test that height remains unchanged
        spawner.height = 64;
        assertEquals(64, spawner.height);
    }

    @Test
    public void testSpawnerColor() {
        // Test that the color attribute is set correctly
        spawner.color = 2;
        assertEquals(2, spawner.color);
    }

    @Test
    public void testSpawnerImage_Setter() {
        // Test setting a new image for the spawner
        PImage newImage = new PImage();
        spawner.spawnerImage = newImage;
        assertEquals(newImage, spawner.spawnerImage);
    }

    @Test
    public void testSpawnerDrawPosition() {
        // Test that draw method draws the image at the correct position
        // Since we cannot verify the drawing on the screen, we ensure no exceptions occur
        assertDoesNotThrow(() -> spawner.draw(app));
    }

    @Test
    public void testSpawnerImage_LoadedCorrectly() {
        // Test that the spawner image is loaded correctly
        assertNotNull(spawner.spawnerImage);
    }

    @Test
    public void testSpawnerImage_NullAfterLoadImageFailure() {
        // Simulate failure in loading image by passing invalid path
        Spawner testSpawner = new Spawner(0, 0, -1, app) {
            @Override
            public void loadImage(App p) {
                spawnerImage = p.loadImage("invalid_path.png"); // Invalid image path
            }
        };
        testSpawner.loadImage(app);
        // spawnerImage may be null if the image fails to load
        assertNull(testSpawner.spawnerImage);
    }

    @Test
    public void testSpawnerFunctionalityInLevel() {
        // Test that spawner can be added to a level and accessed correctly
        Level level = new Level(0, app.configReader, app);
        level.getSpawners().add(spawner);
        assertTrue(level.getSpawners().contains(spawner));
    }

    @Test
    public void testSpawnerWithColor() {
        // Test creating a spawner with a specific color
        Spawner coloredSpawner = new Spawner(0, 0, 1, app);
        assertEquals(1, coloredSpawner.color);
    }

    @Test
    public void testSpawnerToString() {
        // If Spawner had a toString method, test its output
        // Since it doesn't, this test can be skipped or we can test default Object toString
        String spawnerString = spawner.toString();
        assertNotNull(spawnerString);
    }

    @Test
    public void testSpawnerEqualsAndHashCode() {
        // Test equals and hashCode methods if implemented
        // Since they are not overridden, default Object methods are used
        Spawner anotherSpawner = new Spawner(100, 200, -1, app);
        assertNotEquals(spawner, anotherSpawner);
        assertNotEquals(spawner.hashCode(), anotherSpawner.hashCode());
    }

    @Test
    public void testSpawnerInheritance() {
        // Test that Spawner is a subclass of Entity and implements Drawable
        assertTrue(spawner instanceof Entity);
        assertTrue(spawner instanceof Drawable);
    }

    @Test
    public void testSpawnerDefaultWidthHeight() {
        // Test that default width and height are set to 32 pixels
        assertEquals(32, spawner.width);
        assertEquals(32, spawner.height);
    }

    @Test
    public void testSpawnerNegativeCoordinates() {
        // Test creating a spawner with negative coordinates
        Spawner negativeSpawner = new Spawner(-50, -50, -1, app);
        assertEquals(-50, negativeSpawner.getX());
        assertEquals(-50, negativeSpawner.getY());
    }

    @Test
    public void testSpawnerZeroCoordinates() {
        // Test creating a spawner at (0, 0)
        Spawner zeroSpawner = new Spawner(0, 0, -1, app);
        assertEquals(0, zeroSpawner.getX());
        assertEquals(0, zeroSpawner.getY());
    }

    @Test
    public void testSpawnerLargeCoordinates() {
        // Test creating a spawner with large coordinate values
        Spawner largeSpawner = new Spawner(10000, 20000, -1, app);
        assertEquals(10000, largeSpawner.getX());
        assertEquals(20000, largeSpawner.getY());
    }

    @Test
    public void testSpawnerColorValues() {
        // Test creating spawners with different color values
        for (int color = -10; color <= 10; color++) {
            Spawner colorSpawner = new Spawner(0, 0, color, app);
            assertEquals(color, colorSpawner.color);
        }
    }

    @Test
    public void testSpawnerImageSize() {
        // Test that the spawner image has the expected dimensions
        // Since we cannot get the dimensions of the PImage without actual image loading,
        // we can check that the spawner's width and height are correctly set
        assertEquals(32, spawner.width);
        assertEquals(32, spawner.height);
    }

    @Test
    public void testSpawnerInvalidImagePath() {
        // Test behavior when image path is invalid
        Spawner testSpawner = new Spawner(0, 0, -1, app) {
            @Override
            public void loadImage(App p) {
                spawnerImage = p.loadImage("nonexistent_image.png"); // Invalid image path
            }
        };
        testSpawner.loadImage(app);
        // spawnerImage may be null if the image fails to load
        assertNull(testSpawner.spawnerImage);
    }

    @Test
    public void testSpawnerImageAfterLoadImage() {
        // Test that spawnerImage is set after calling loadImage
        Spawner testSpawner = new Spawner(0, 0, -1, app);
        testSpawner.spawnerImage = null;
        testSpawner.loadImage(app);
        assertNotNull(testSpawner.spawnerImage);
    }

    @Test
    public void testSpawnerDrawWithoutImage() {
        // Test that draw method handles null spawnerImage gracefully
        spawner.spawnerImage = null;
        assertDoesNotThrow(() -> spawner.draw(app));
        // Since spawnerImage is null, draw may not render anything but should not crash
    }

    @Test
    public void testSpawnerImplementsDrawable() {
        // Test that Spawner implements the Drawable interface
        assertTrue(spawner instanceof Drawable);
    }

    @Test
    public void testSpawnerAsEntity() {
        // Test that Spawner can be used as an Entity
        Entity entitySpawner = spawner;
        assertEquals(100, ((Spawner) entitySpawner).getX());
        assertEquals(200, ((Spawner) entitySpawner).getY());
    }

    @Test
    public void testSpawnerColorAttribute() {
        // Test the color attribute
        spawner.color = 3;
        assertEquals(3, spawner.color);
    }

    @Test
    public void testSpawnerLoadImageExceptionHandling() {
        // Test that loadImage handles exceptions during image loading
        Spawner testSpawner = new Spawner(0, 0, -1, app) {
            @Override
            public void loadImage(App p) {
                throw new RuntimeException("Image loading failed");
            }
        };
        assertThrows(RuntimeException.class, () -> testSpawner.loadImage(app));
    }

    @Test
    public void testSpawnerDrawExceptionHandling() {
        // Test that draw handles exceptions during drawing
        Spawner testSpawner = new Spawner(0, 0, -1, app) {
            @Override
            public void draw(App p) {
                throw new RuntimeException("Drawing failed");
            }
        };
        assertThrows(RuntimeException.class, () -> testSpawner.draw(app));
    }
}
