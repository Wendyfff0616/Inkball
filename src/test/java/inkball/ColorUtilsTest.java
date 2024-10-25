package inkball;

import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ColorUtilsTest {
    @Test
    public void testColorToNumber_InvalidColor() {
        // Test that colorToNumber returns -1 for invalid color names
        assertEquals(-1, ColorUtils.colorToNumber("purple"));
        assertEquals(-1, ColorUtils.colorToNumber("pink"));
        assertEquals(-1, ColorUtils.colorToNumber(""));
        assertEquals(-1, ColorUtils.colorToNumber(" "));
    }

    @Test
    public void testNumberToColor_InvalidNumber() {
        // Test that numberToColor returns "unknown" for invalid numeric values
        assertEquals("unknown", ColorUtils.numberToColor(-1));
        assertEquals("unknown", ColorUtils.numberToColor(5));
        assertEquals("unknown", ColorUtils.numberToColor(100));
    }
}
