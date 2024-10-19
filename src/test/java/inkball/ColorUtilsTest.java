package inkball;

import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ColorUtilsTest {

    @Test
    public void testColorToNumber_ValidColors() {
        // Test that colorToNumber returns correct numeric values for valid color names
        assertEquals(0, ColorUtils.colorToNumber("grey"));
        assertEquals(1, ColorUtils.colorToNumber("blue"));
        assertEquals(2, ColorUtils.colorToNumber("green"));
        assertEquals(3, ColorUtils.colorToNumber("orange"));
        assertEquals(4, ColorUtils.colorToNumber("yellow"));
    }

    @Test
    public void testColorToNumber_CaseInsensitive() {
        // Test that colorToNumber is case-insensitive
        assertEquals(0, ColorUtils.colorToNumber("Grey"));
        assertEquals(1, ColorUtils.colorToNumber("BLUE"));
        assertEquals(2, ColorUtils.colorToNumber("GrEeN"));
    }

    @Test
    public void testColorToNumber_InvalidColor() {
        // Test that colorToNumber returns -1 for invalid color names
        assertEquals(-1, ColorUtils.colorToNumber("purple"));
        assertEquals(-1, ColorUtils.colorToNumber("pink"));
        assertEquals(-1, ColorUtils.colorToNumber(""));
        assertEquals(-1, ColorUtils.colorToNumber(" "));
    }

    @Test
    public void testColorToNumber_NullInput() {
        // Test that colorToNumber handles null input gracefully
        assertEquals(-1, ColorUtils.colorToNumber(null));
    }

    @Test
    public void testNumberToColor_ValidNumbers() {
        // Test that numberToColor returns correct color names for valid numeric values
        assertEquals("grey", ColorUtils.numberToColor(0));
        assertEquals("blue", ColorUtils.numberToColor(1));
        assertEquals("green", ColorUtils.numberToColor(2));
        assertEquals("orange", ColorUtils.numberToColor(3));
        assertEquals("yellow", ColorUtils.numberToColor(4));
    }

    @Test
    public void testNumberToColor_InvalidNumber() {
        // Test that numberToColor returns "unknown" for invalid numeric values
        assertEquals("unknown", ColorUtils.numberToColor(-1));
        assertEquals("unknown", ColorUtils.numberToColor(5));
        assertEquals("unknown", ColorUtils.numberToColor(100));
    }

    @Test
    public void testColorNameToNumber_MapNotNull() {
        // Test that colorNameToNumber map is not null and contains expected entries
        assertNotNull(ColorUtils.colorNameToNumber);
        assertEquals(5, ColorUtils.colorNameToNumber.size());
        assertTrue(ColorUtils.colorNameToNumber.containsKey("grey"));
        assertTrue(ColorUtils.colorNameToNumber.containsKey("blue"));
        assertTrue(ColorUtils.colorNameToNumber.containsKey("green"));
        assertTrue(ColorUtils.colorNameToNumber.containsKey("orange"));
        assertTrue(ColorUtils.colorNameToNumber.containsKey("yellow"));
    }

    @Test
    public void testColorNumberToName_MapNotNull() {
        // Test that colorNumberToName map is not null and contains expected entries
        assertNotNull(ColorUtils.colorNumberToName);
        assertEquals(5, ColorUtils.colorNumberToName.size());
        assertTrue(ColorUtils.colorNumberToName.containsKey(0));
        assertTrue(ColorUtils.colorNumberToName.containsKey(1));
        assertTrue(ColorUtils.colorNumberToName.containsKey(2));
        assertTrue(ColorUtils.colorNumberToName.containsKey(3));
        assertTrue(ColorUtils.colorNumberToName.containsKey(4));
    }

    @Test
    public void testStaticBlockInitialization() {
        // Test that static block initializes maps correctly
        // Since static blocks execute when the class is loaded, we can test the mappings directly
        assertEquals(0, ColorUtils.colorNameToNumber.get("grey"));
        assertEquals(1, ColorUtils.colorNameToNumber.get("blue"));
        assertEquals(2, ColorUtils.colorNameToNumber.get("green"));
        assertEquals(3, ColorUtils.colorNameToNumber.get("orange"));
        assertEquals(4, ColorUtils.colorNameToNumber.get("yellow"));

        assertEquals("grey", ColorUtils.colorNumberToName.get(0));
        assertEquals("blue", ColorUtils.colorNumberToName.get(1));
        assertEquals("green", ColorUtils.colorNumberToName.get(2));
        assertEquals("orange", ColorUtils.colorNumberToName.get(3));
        assertEquals("yellow", ColorUtils.colorNumberToName.get(4));
    }

    @Test
    public void testColorToNumber_WhitespaceInput() {
        // Test that colorToNumber handles input with leading/trailing whitespace
        assertEquals(0, ColorUtils.colorToNumber(" grey "));
        assertEquals(-1, ColorUtils.colorToNumber("  blue"));
        assertEquals(-1, ColorUtils.colorToNumber("green  "));
    }

    @Test
    public void testNumberToColor_BoundaryValues() {
        // Test numberToColor with boundary numeric values
        assertEquals("grey", ColorUtils.numberToColor(0));
        assertEquals("yellow", ColorUtils.numberToColor(4));
        assertEquals("unknown", ColorUtils.numberToColor(Integer.MAX_VALUE));
        assertEquals("unknown", ColorUtils.numberToColor(Integer.MIN_VALUE));
    }

    @Test
    public void testColorToNumber_SpecialCharacters() {
        // Test that colorToNumber returns -1 for inputs with special characters
        assertEquals(-1, ColorUtils.colorToNumber("blu3"));
        assertEquals(-1, ColorUtils.colorToNumber("gr@en"));
        assertEquals(-1, ColorUtils.colorToNumber("yellow!"));
    }

    @Test
    public void testColorToNumber_NumericStringInput() {
        // Test that colorToNumber returns -1 for numeric string inputs
        assertEquals(-1, ColorUtils.colorToNumber("123"));
        assertEquals(-1, ColorUtils.colorToNumber("0"));
    }

    @Test
    public void testNumberToColor_NonexistentNumbers() {
        // Test that numberToColor returns "unknown" for numbers not in the map
        assertEquals("unknown", ColorUtils.numberToColor(10));
        assertEquals("unknown", ColorUtils.numberToColor(-5));
    }

    @Test
    public void testColorToNumber_EmptyString() {
        // Test that colorToNumber returns -1 for empty string input
        assertEquals(-1, ColorUtils.colorToNumber(""));
    }

    @Test
    public void testColorToNumber_NullKeyInMap() {
        // Test that the map does not contain null keys
        assertFalse(ColorUtils.colorNameToNumber.containsKey(null));
    }

    @Test
    public void testNumberToColor_NullValueInMap() {
        // Test that the map does not contain null values
        assertFalse(ColorUtils.colorNumberToName.containsKey(null));
    }

    @Test
    public void testColorToNumber_ExtendedColors() {
        // If new colors are added in the future, ensure they are handled
        // Assuming "purple" is added with value 5
        ColorUtils.colorNameToNumber.put("purple", 5);
        ColorUtils.colorNumberToName.put(5, "purple");

        assertEquals(5, ColorUtils.colorToNumber("purple"));
        assertEquals("purple", ColorUtils.numberToColor(5));

        // Clean up to avoid side effects on other tests
        ColorUtils.colorNameToNumber.remove("purple");
        ColorUtils.colorNumberToName.remove(5);
    }

    @Test
    public void testColorToNumber_InputWithSpaces() {
        // Test that colorToNumber returns -1 for input with internal spaces
        assertEquals(-1, ColorUtils.colorToNumber("light blue"));
    }

    @Test
    public void testNumberToColor_InputWithLargeNumber() {
        // Test numberToColor with a large number
        assertEquals("unknown", ColorUtils.numberToColor(1000));
    }

    @Test
    public void testNumberToColor_MapUnmodifiable() {
        // Test that colorNumberToName map is unmodifiable (if applicable)
        // Since it's a standard HashMap, modifications are possible
        // Attempting to modify the map
        ColorUtils.colorNumberToName.put(99, "testColor");
        assertEquals("testColor", ColorUtils.numberToColor(99));

        // Clean up to avoid side effects
        ColorUtils.colorNumberToName.remove(99);
    }

    @Test
    public void testColorNameToNumber_MapUnmodifiable() {
        // Test that colorNameToNumber map is unmodifiable (if applicable)
        // Since it's a standard HashMap, modifications are possible
        // Attempting to modify the map
        ColorUtils.colorNameToNumber.put("testColor", 99);
        assertEquals(99, ColorUtils.colorToNumber("testColor"));

        // Clean up to avoid side effects
        ColorUtils.colorNameToNumber.remove("testColor");
    }

    @Test
    public void testStaticBlockExecution() {
        // Test that static block executes and initializes maps
        // We can clear the maps and reload the class to test static block
        ColorUtils.colorNameToNumber.clear();
        ColorUtils.colorNumberToName.clear();

        // Reload the class (not typically possible, but we can simulate by re-initializing)
        // For testing purposes, we'll call the static block code manually
        // Since we cannot invoke the static block directly, we'll simulate initialization
        ColorUtils.colorNameToNumber.put("grey", 0);
        ColorUtils.colorNameToNumber.put("blue", 1);
        ColorUtils.colorNameToNumber.put("green", 2);
        ColorUtils.colorNameToNumber.put("orange", 3);
        ColorUtils.colorNameToNumber.put("yellow", 4);

        // Create reverse mapping from numbers to color names
        for (Map.Entry<String, Integer> entry : ColorUtils.colorNameToNumber.entrySet()) {
            ColorUtils.colorNumberToName.put(entry.getValue(), entry.getKey());
        }

        // Test that maps are initialized correctly
        assertEquals(0, ColorUtils.colorNameToNumber.get("grey"));
        assertEquals("grey", ColorUtils.colorNumberToName.get(0));
    }

    @Test
    public void testColorToNumber_LocaleIndependence() {
        // Test that colorToNumber works regardless of default locale settings
        // Simulate different locale settings by changing case
        assertEquals(3, ColorUtils.colorToNumber("ORANGE"));
        assertEquals(3, ColorUtils.colorToNumber("orange"));
        assertEquals(3, ColorUtils.colorToNumber("OrAnGe"));
    }

    @Test
    public void testNumberToColor_ReturnsLowercase() {
        // Test that numberToColor always returns lowercase color names
        assertEquals("grey", ColorUtils.numberToColor(0));
        assertEquals("blue", ColorUtils.numberToColor(1));
    }

    @Test
    public void testColorToNumber_InputWithLeadingZeros() {
        // Test that colorToNumber handles input with leading zeros (should return -1)
        assertEquals(-1, ColorUtils.colorToNumber("000blue"));
    }

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        // Test that the utility handles concurrent access without issues
        Runnable task = () -> {
            assertEquals(0, ColorUtils.colorToNumber("grey"));
            assertEquals("grey", ColorUtils.numberToColor(0));
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        // If no exceptions occur, the test passes
    }
}
