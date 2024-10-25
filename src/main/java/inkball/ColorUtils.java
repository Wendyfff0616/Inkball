package inkball;

import java.util.*;

/**
 * Utility class for converting between color names and their corresponding numbers.
 * This is used to simplify the process of handling colors in the Inkball game.
 */
public class ColorUtils {

    // Maps color names to their corresponding numeric values
    static Map<String, Integer> colorNameToNumber = new HashMap<>();

    // Maps numeric values to their corresponding color names
    static Map<Integer, String> colorNumberToName = new HashMap<>();

    static {
        colorNameToNumber.put("grey", 0);
        colorNameToNumber.put("blue", 1);
        colorNameToNumber.put("green", 2);
        colorNameToNumber.put("orange", 3);
        colorNameToNumber.put("yellow", 4);

        // Create reverse mapping from numbers to color names
        for (Map.Entry<String, Integer> entry : colorNameToNumber.entrySet()) {
            colorNumberToName.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * Converts a color name to its corresponding numeric value.
     *
     * @param colorName The name of the color.
     * @return The corresponding numeric value for the color, or -1 if the color is not found.
     */
    public static int colorToNumber(String colorName) {
        return colorNameToNumber.getOrDefault(colorName.toLowerCase(), -1);
    }

    /**
     * Converts a numeric value to its corresponding color name.
     *
     * @param colorNumber The numeric value of the color.
     * @return The corresponding color name, or "unknown" if the number does not match any color.
     */
    public static String numberToColor(int colorNumber) {
        return colorNumberToName.getOrDefault(colorNumber, "unknown");
    }
}
