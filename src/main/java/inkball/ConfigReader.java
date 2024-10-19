package inkball;

import processing.data.JSONObject;
import processing.data.JSONArray;

/**
 * The ConfigReader class is responsible for reading and managing game settings
 * from a configuration JSON file.
 */
public class ConfigReader {
    private JSONObject config;
    private App app;

    /**
     * Constructs a ConfigReader object and loads the configuration file.
     *
     * @param configPath The path to the JSON configuration file.
     * @param p          The App object used to load the file.
     */
    public ConfigReader(String configPath, App p) {
        this.config = p.loadJSONObject(configPath); // Loads the JSON config file
    }

    // Additional constructor for testing
    public ConfigReader(JSONObject config, App app) {
        this.app = app;
        this.config = config;
    }

    /**
     * Retrieves the full game configuration.
     *
     * @return A JSONObject representing the entire game configuration.
     */
    public JSONObject getConfig() {
        return config;
    }

    /**
     * Retrieves the configuration for a specific level based on its index.
     *
     * @param levelIndex The index of the level (0-based).
     * @return A JSONObject containing the configuration for the specified level,
     *         or null if the index is invalid.
     */
    public JSONObject getLevelConfig(int levelIndex) {
        JSONArray levels = config.getJSONArray("levels");
        if (levelIndex >= 0 && levelIndex < levels.size()) {
            return levels.getJSONObject(levelIndex);
        }
        return null;
    }

    /**
     * Retrieves the total number of levels in the game.
     *
     * @return The total number of levels available in the configuration.
     */
    public int getNumLevels() {
        return config.getJSONArray("levels").size();
    }

    /**
     * Retrieves the list of ball colors for a specific level.
     *
     * @param levelIndex The index of the level (0-based).
     * @return A JSONArray containing the colors of the balls for the specified level,
     *         or null if the level index is invalid.
     */
    public JSONArray getBallColors(int levelIndex) {
        JSONObject levelConfig = getLevelConfig(levelIndex);
        if (levelConfig != null) {
            return levelConfig.getJSONArray("balls");
        }
        return null;
    }

    /**
     * Retrieves the score increase modifier for the current level.
     *
     * @param levelIndex The index of the level.
     * @return The score increase modifier for the given level.
     */
    public double getScoreIncreaseModifier(int levelIndex) {
        JSONObject levelConfig = getLevelConfig(levelIndex);
        if (levelConfig != null) {
            return levelConfig.getDouble("score_increase_from_hole_capture_modifier", 1.0); // Default to 1.0 if not found
        }
        return 1.0;
    }

    /**
     * Retrieves the score decrease modifier for the current level.
     *
     * @param levelIndex The index of the level.
     * @return The score decrease modifier for the given level.
     */
    public double getScoreDecreaseModifier(int levelIndex) {
        JSONObject levelConfig = getLevelConfig(levelIndex);
        if (levelConfig != null) {
            return levelConfig.getDouble("score_decrease_from_wrong_hole_modifier", 1.0); // Default to 1.0 if not found
        }
        return 1.0;
    }

    /**
     * Retrieves the score increase value for successfully capturing a ball of a specific color.
     * This method retrieves the value from the global configuration, not from the level-specific configuration.
     *
     * @param color      The color of the ball (e.g., "grey", "orange").
     * @return The score increase value for the given color.
     */
    public int getScoreIncrease(String color) {
        JSONObject scoreIncreaseConfig = config.getJSONObject("score_increase_from_hole_capture");
        if (scoreIncreaseConfig != null && scoreIncreaseConfig.hasKey(color)) {
            return scoreIncreaseConfig.getInt(color);
        }
        throw new RuntimeException("Score increase not found for color: " + color);
    }

    /**
     * Retrieves the score decrease value for incorrectly capturing a ball of a specific color.
     * This method retrieves the value from the global configuration, not from the level-specific configuration.
     *
     * @param color      The color of the ball (e.g., "grey", "orange").
     * @return The score decrease value for the given color.
     */
    public int getScoreDecrease(String color) {
        JSONObject scoreDecreaseConfig = config.getJSONObject("score_decrease_from_wrong_hole");
        if (scoreDecreaseConfig != null && scoreDecreaseConfig.hasKey(color)) {
            return scoreDecreaseConfig.getInt(color);
        }
        throw new RuntimeException("Score decrease not found for color: " + color);
    }
}
