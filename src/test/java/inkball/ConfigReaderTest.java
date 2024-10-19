package inkball;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import processing.core.PApplet;
import processing.data.JSONObject;
import processing.data.JSONArray;

public class ConfigReaderTest {

    static App app;
    static ConfigReader configReader;
    static String testConfigPath = "test_config.json";

    @BeforeAll
    public static void setup() {
        app = new App();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup(); // Initialize the app

        // Create a test configuration JSON
        JSONObject testConfig = new JSONObject();

        // Add global score increase and decrease configurations
        JSONObject scoreIncrease = new JSONObject();
        scoreIncrease.setInt("grey", 10);
        scoreIncrease.setInt("blue", 20);
        scoreIncrease.setInt("green", 30);
        scoreIncrease.setInt("orange", 40);
        scoreIncrease.setInt("yellow", 50);
        testConfig.setJSONObject("score_increase_from_hole_capture", scoreIncrease);

        JSONObject scoreDecrease = new JSONObject();
        scoreDecrease.setInt("grey", 5);
        scoreDecrease.setInt("blue", 10);
        scoreDecrease.setInt("green", 15);
        scoreDecrease.setInt("orange", 20);
        scoreDecrease.setInt("yellow", 25);
        testConfig.setJSONObject("score_decrease_from_wrong_hole", scoreDecrease);

        // Add levels array
        JSONArray levels = new JSONArray();

        // Level 0
        JSONObject level0 = new JSONObject();
        level0.setDouble("score_increase_from_hole_capture_modifier", 1.0);
        level0.setDouble("score_decrease_from_wrong_hole_modifier", 1.0);
        JSONArray balls0 = new JSONArray();
        balls0.append("grey");
        balls0.append("blue");
        level0.setJSONArray("balls", balls0);
        levels.append(level0);

        // Level 1
        JSONObject level1 = new JSONObject();
        level1.setDouble("score_increase_from_hole_capture_modifier", 1.5);
        level1.setDouble("score_decrease_from_wrong_hole_modifier", 0.5);
        JSONArray balls1 = new JSONArray();
        balls1.append("green");
        balls1.append("orange");
        balls1.append("yellow");
        level1.setJSONArray("balls", balls1);
        levels.append(level1);

        testConfig.setJSONArray("levels", levels);

        // Save the test configuration to a file
        app.saveJSONObject(testConfig, testConfigPath);

        // Initialize ConfigReader with the test configuration
        configReader = new ConfigReader(testConfigPath, app);
    }

    @AfterAll
    public static void cleanup() {
        // Delete the test configuration file after tests
        java.io.File file = new java.io.File(testConfigPath);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testGetConfig() {
        // Create a simple config
        String jsonString = "{ \"levels\": [] }";
        JSONObject config = JSONObject.parse(jsonString);
        ConfigReader configReader = new ConfigReader(config, new App());

        // Test getConfig
        assertNotNull(configReader.getConfig(), "Config should not be null");
    }

    @Test
    public void testGetLevelConfig_ValidIndex() {
        // Test that getLevelConfig returns the correct level configuration for a valid index
        JSONObject levelConfig = configReader.getLevelConfig(0);
        assertNotNull(levelConfig);
        assertEquals(1.0, levelConfig.getDouble("score_increase_from_hole_capture_modifier"));
        assertEquals(1.0, levelConfig.getDouble("score_decrease_from_wrong_hole_modifier"));
        assertTrue(levelConfig.hasKey("balls"));
    }

    @Test
    public void testGetLevelConfig_InvalidIndex() {
        // Create a config with one level
        String jsonString = "{ \"levels\": [ { \"layout\": \"level1.txt\" } ] }";
        JSONObject config = JSONObject.parse(jsonString);
        ConfigReader configReader = new ConfigReader(config, new App());

        // Test getLevelConfig with invalid index
        assertThrows(IndexOutOfBoundsException.class, () -> {
            configReader.getLevelConfig(1); // Invalid index
        });
    }

    @Test
    public void testGetNumLevels() {
        // Create a config with two levels
        String jsonString = "{ \"levels\": [ { \"layout\": \"level1.txt\" }, { \"layout\": \"level2.txt\" } ] }";
        JSONObject config = JSONObject.parse(jsonString);
        ConfigReader configReader = new ConfigReader(config, new App());

        // Test getNumLevels
        assertEquals(2, configReader.getNumLevels(), "Should return 2 levels");
    }

    @Test
    public void testGetBallColors_ValidIndex() {
        // Create a config with one level and some balls
        String jsonString = "{ \"levels\": [ { \"layout\": \"level1.txt\", \"balls\": [\"red\", \"blue\"] } ] }";
        JSONObject config = JSONObject.parse(jsonString);
        ConfigReader configReader = new ConfigReader(config, new App());

        // Test getBallColors
        JSONArray ballColors = configReader.getBallColors(0);

        assertNotNull(ballColors, "Ball colors should not be null");
        assertEquals(2, ballColors.size(), "Ball colors array should have 2 elements");
        assertEquals("red", ballColors.getString(0));
        assertEquals("blue", ballColors.getString(1));
    }

    @Test
    public void testGetBallColors_InvalidIndex() {
        // Create a config with one level
        String jsonString = "{ \"levels\": [ { \"layout\": \"level1.txt\", \"balls\": [\"red\", \"blue\"] } ] }";
        JSONObject config = JSONObject.parse(jsonString);
        ConfigReader configReader = new ConfigReader(config, new App());

        // Test getBallColors with invalid index
        assertThrows(IndexOutOfBoundsException.class, () -> {
            configReader.getBallColors(1); // Invalid index
        });
    }

    @Test
    public void testGetScoreIncreaseModifier_ValidIndex() {
        // Test that getScoreIncreaseModifier returns correct modifier for valid level index
        double modifierLevel0 = configReader.getScoreIncreaseModifier(0);
        assertEquals(1.0, modifierLevel0);
        double modifierLevel1 = configReader.getScoreIncreaseModifier(1);
        assertEquals(1.5, modifierLevel1);
    }

    @Test
    public void testGetScoreIncreaseModifier_InvalidIndex() {
        // Test that getScoreIncreaseModifier returns default value for invalid level index
        double modifier = configReader.getScoreIncreaseModifier(-1);
        assertEquals(1.0, modifier);
        modifier = configReader.getScoreIncreaseModifier(10);
        assertEquals(1.0, modifier);
    }

    @Test
    public void testGetScoreDecreaseModifier_ValidIndex() {
        // Create a config with a level that has a score decrease modifier
        String jsonString = "{ \"levels\": [ { \"layout\": \"level1.txt\", \"score_decrease_modifier\": 0.5 } ] }";
        JSONObject config = JSONObject.parse(jsonString);
        ConfigReader configReader = new ConfigReader(config, new App());

        // Test getScoreDecreaseModifier
        double modifier = configReader.getScoreDecreaseModifier(0);

        assertEquals(0.5, modifier, 0.001, "Modifier should be 0.5");
    }

    @Test
    public void testGetScoreDecreaseModifier_InvalidIndex() {
        // Create a config with one level
        String jsonString = "{ \"levels\": [ { \"layout\": \"level1.txt\" } ] }";
        JSONObject config = JSONObject.parse(jsonString);
        ConfigReader configReader = new ConfigReader(config, new App());

        // Test getScoreDecreaseModifier with invalid index
        assertThrows(IndexOutOfBoundsException.class, () -> {
            configReader.getScoreDecreaseModifier(1); // Invalid index
        });
    }

    @Test
    public void testGetScoreIncrease_ValidColor() {
        // Test that getScoreIncrease returns correct value for valid color
        int increaseGrey = configReader.getScoreIncrease("grey");
        assertEquals(10, increaseGrey);
        int increaseBlue = configReader.getScoreIncrease("blue");
        assertEquals(20, increaseBlue);
    }

    @Test
    public void testGetScoreIncrease_InvalidColor() {
        // Test that getScoreIncrease throws exception for invalid color
        Exception exception = assertThrows(RuntimeException.class, () -> {
            configReader.getScoreIncrease("purple");
        });
        assertEquals("Score increase not found for color: purple", exception.getMessage());
    }

    @Test
    public void testGetScoreDecrease_ValidColor() {
        // Create a config with valid score decrease values
        String jsonString = "{ \"score_decrease\": { \"red\": 10 } }";
        JSONObject config = JSONObject.parse(jsonString);
        ConfigReader configReader = new ConfigReader(config, new App());

        // Test getScoreDecrease
        int scoreDecrease = configReader.getScoreDecrease("red");

        assertEquals(10, scoreDecrease, "Score decrease for 'red' should be 10");
    }

    @Test
    public void testGetScoreDecrease_InvalidColor() {
        // Test that getScoreDecrease throws exception for invalid color
        Exception exception = assertThrows(RuntimeException.class, () -> {
            configReader.getScoreDecrease("black");
        });
        assertEquals("Score decrease not found for color: black", exception.getMessage());
    }

    @Test
    public void testGetScoreIncrease_NullColor() {
        // Test that getScoreIncrease handles null color gracefully
        Exception exception = assertThrows(RuntimeException.class, () -> {
            configReader.getScoreIncrease(null);
        });
        assertEquals("Score increase not found for color: null", exception.getMessage());
    }

    @Test
    public void testGetScoreDecrease_NullColor() {
        // Create a config with valid score decrease values
        String jsonString = "{ \"score_decrease\": { \"red\": 10 } }";
        JSONObject config = JSONObject.parse(jsonString);
        ConfigReader configReader = new ConfigReader(config, new App());

        // Test getScoreDecrease with null color
        assertThrows(NullPointerException.class, () -> {
            configReader.getScoreDecrease(null);
        });
    }

    @Test
    public void testGetConfig_NullConfig() {
        ConfigReader configReader = new ConfigReader((JSONObject) null, new App());

        // Test getConfig
        assertNull(configReader.getConfig(), "Config should be null");
    }

    @Test
    public void testGetLevelConfig_NullConfig() {
        ConfigReader configReader = new ConfigReader((JSONObject) null, new App());

        // Test getLevelConfig
        assertThrows(NullPointerException.class, () -> {
            configReader.getLevelConfig(0);
        });
    }

    @Test
    public void testGetNumLevels_NullConfig() {
        ConfigReader configReader = new ConfigReader((JSONObject) null, new App());

        // Test getNumLevels
        assertThrows(NullPointerException.class, () -> {
            configReader.getNumLevels();
        });
    }

    @Test
    public void testGetBallColors_NullConfig() {
        ConfigReader configReader = new ConfigReader((JSONObject) null, new App());

        // Test getBallColors
        assertThrows(NullPointerException.class, () -> {
            configReader.getBallColors(0);
        });
    }

    @Test
    public void testGetScoreIncreaseModifier_NullConfig() {
        // Test getScoreIncreaseModifier when config is null
        ConfigReader nullConfigReader = new ConfigReader("nonexistent_config.json", app);
        double modifier = nullConfigReader.getScoreIncreaseModifier(0);
        assertEquals(1.0, modifier);
    }

    @Test
    public void testGetScoreDecreaseModifier_NullConfig() {
        ConfigReader configReader = new ConfigReader((JSONObject) null, new App());

        // Test getScoreDecreaseModifier
        assertThrows(NullPointerException.class, () -> {
            configReader.getScoreDecreaseModifier(0);
        });
    }

    @Test
    public void testConstructor_NullApp() {
        // Test that constructor handles null App gracefully
        Exception exception = assertThrows(NullPointerException.class, () -> {
            new ConfigReader(testConfigPath, null);
        });
    }

    @Test
    public void testGetScoreIncrease_MissingKey() {
        // Test behavior when score increase key is missing
        // Remove "grey" from score_increase_from_hole_capture
        JSONObject config = configReader.getConfig();
        JSONObject scoreIncreaseConfig = config.getJSONObject("score_increase_from_hole_capture");
        scoreIncreaseConfig.remove("grey");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            configReader.getScoreIncrease("grey");
        });
        assertEquals("Score increase not found for color: grey", exception.getMessage());
    }

    @Test
    public void testGetScoreDecrease_MissingKey() {
        // Test behavior when score decrease key is missing
        // Remove "blue" from score_decrease_from_wrong_hole
        JSONObject config = configReader.getConfig();
        JSONObject scoreDecreaseConfig = config.getJSONObject("score_decrease_from_wrong_hole");
        scoreDecreaseConfig.remove("blue");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            configReader.getScoreDecrease("blue");
        });
        assertEquals("Score decrease not found for color: blue", exception.getMessage());
    }

    @Test
    public void testGetLevelConfig_MissingLevels() {
        // Test behavior when "levels" key is missing in config
        JSONObject config = configReader.getConfig();
        config.remove("levels");

        JSONObject levelConfig = configReader.getLevelConfig(0);
        assertNull(levelConfig);
    }

    @Test
    public void testGetNumLevels_MissingLevels() {
        // Test behavior when "levels" key is missing in config
        JSONObject config = configReader.getConfig();
        config.remove("levels");

        assertThrows(NullPointerException.class, () -> {
            configReader.getNumLevels();
        });
    }

    @Test
    public void testGetLevelConfig_LevelWithoutModifiers() {
        // Test behavior when a level config lacks modifiers
        JSONObject levelConfig = configReader.getLevelConfig(0);
        levelConfig.remove("score_increase_from_hole_capture_modifier");
        levelConfig.remove("score_decrease_from_wrong_hole_modifier");

        double increaseModifier = configReader.getScoreIncreaseModifier(0);
        assertEquals(1.0, increaseModifier); // Should default to 1.0

        double decreaseModifier = configReader.getScoreDecreaseModifier(0);
        assertEquals(1.0, decreaseModifier); // Should default to 1.0
    }

    @Test
    public void testGetLevelConfig_LevelWithoutBalls() {
        // Test behavior when a level config lacks "balls" array
        JSONObject levelConfig = configReader.getLevelConfig(0);
        levelConfig.remove("balls");

        JSONArray balls = configReader.getBallColors(0);
        assertNull(balls);
    }

    @Test
    public void testGetBallColors_EmptyBallsArray() {
        // Create a config with an empty 'balls' array for level 0
        String jsonString = "{ \"levels\": [ { \"layout\": \"level1.txt\", \"time\": 60, \"spawn_interval\": 5, \"balls\": [] } ] }";
        JSONObject config = JSONObject.parse(jsonString);
        ConfigReader configReader = new ConfigReader(config, new App());

        // Test getBallColors
        JSONArray ballColors = configReader.getBallColors(0);

        assertNotNull(ballColors, "Ball colors should not be null");
        assertEquals(0, ballColors.size(), "Ball colors array should be empty");
    }

    @Test
    public void testGetScoreIncrease_InvalidType() {
        // Test behavior when score increase value is of invalid type
        JSONObject config = configReader.getConfig();
        JSONObject scoreIncreaseConfig = config.getJSONObject("score_increase_from_hole_capture");
        scoreIncreaseConfig.setString("green", "invalid");

        Exception exception = assertThrows(ClassCastException.class, () -> {
            configReader.getScoreIncrease("green");
        });
    }

    @Test
    public void testGetScoreDecrease_InvalidType() {
        // Create a config with an invalid type for score decrease
        String jsonString = "{ \"score_decrease\": { \"red\": \"invalid_type\" } }";
        JSONObject config = JSONObject.parse(jsonString);
        ConfigReader configReader = new ConfigReader(config, new App());

        // Test getScoreDecrease
        assertThrows(ClassCastException.class, () -> {
            configReader.getScoreDecrease("red");
        });
    }

    @Test
    public void testGetLevelConfig_InvalidType() {
        // Test behavior when level config is of invalid type
        JSONObject config = configReader.getConfig();
        JSONArray levels = config.getJSONArray("levels");
        levels.setString(0, "invalid");

        Exception exception = assertThrows(ClassCastException.class, () -> {
            configReader.getLevelConfig(0);
        });
    }

    @Test
    public void testGetScoreModifiers_InvalidType() {
        // Test behavior when score modifiers are of invalid type
        JSONObject levelConfig = configReader.getLevelConfig(0);
        levelConfig.setString("score_increase_from_hole_capture_modifier", "invalid");
        levelConfig.setString("score_decrease_from_wrong_hole_modifier", "invalid");

        double increaseModifier = configReader.getScoreIncreaseModifier(0);
        assertEquals(1.0, increaseModifier); // Should default to 1.0

        double decreaseModifier = configReader.getScoreDecreaseModifier(0);
        assertEquals(1.0, decreaseModifier); // Should default to 1.0
    }
}
