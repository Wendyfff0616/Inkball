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
        // Create a config with one level
        String jsonString = "{ \"levels\": [ { \"layout\": \"level1.txt\" } ] }";
        JSONObject config = JSONObject.parse(jsonString);
        ConfigReader configReader = new ConfigReader(config, new App());

        // Test getLevelConfig with valid index
        JSONObject levelConfig = configReader.getLevelConfig(1); // Valid index
        assertNull(levelConfig);
    }

    @Test
    public void testGetNumLevels() {
        // Create a config with two levels
        String jsonString = "{ \"levels\": [ { \"layout\": \"level1.txt\" }, { \"layout\": \"level2.txt\" } ] }";
        JSONObject config = JSONObject.parse(jsonString);
        ConfigReader configReader = new ConfigReader(config, new App());

        // Test getNumLevels
        assertEquals(2, configReader.getNumLevels());
    }

    @Test
    public void testGetBallColors_ValidIndex() {
        // Create a config with one level and a list of ball colors
        String jsonString = "{ \"levels\": [ { \"layout\": \"level1.txt\", \"balls\": [\"red\", \"blue\"] } ] }";
        JSONObject config = JSONObject.parse(jsonString);
        ConfigReader configReader = new ConfigReader(config, new App());

        // Test getBallColors with valid index
        JSONArray ballColors = configReader.getBallColors(0); // Valid index
        assertNotNull(ballColors);
        assertEquals(2, ballColors.size());
        assertEquals("red", ballColors.getString(0));
        assertEquals("blue", ballColors.getString(1));
    }

    @Test
    public void testGetScoreIncreaseModifier_ValidIndex() {
        // Create a config with two levels and score increase modifiers
        String jsonString = "{ \"levels\": [ { \"score_increase_from_hole_capture_modifier\": 1.0 }, { \"score_increase_from_hole_capture_modifier\": 1.5 } ] }";
        JSONObject config = JSONObject.parse(jsonString);
        ConfigReader configReader = new ConfigReader(config, new App());

        // Test getScoreIncreaseModifier with valid indices
        double modifier = configReader.getScoreIncreaseModifier(0); // Valid index for level 0
        assertEquals(1.0, modifier);

        modifier = configReader.getScoreIncreaseModifier(1); // Valid index for level 1
        assertEquals(1.5, modifier);
    }

    @Test
    public void testGetScoreDecreaseModifier_ValidIndex() {
        // Create a config with two levels and score decrease modifiers
        String jsonString = "{ \"levels\": [ { \"score_decrease_from_wrong_hole_modifier\": 1.0 }, { \"score_decrease_from_wrong_hole_modifier\": 0.5 } ] }";
        JSONObject config = JSONObject.parse(jsonString);
        ConfigReader configReader = new ConfigReader(config, new App());

        // Test getScoreDecreaseModifier with valid indices
        double modifier = configReader.getScoreDecreaseModifier(0); // Valid index for level 0
        assertEquals(1.0, modifier);

        modifier = configReader.getScoreDecreaseModifier(1); // Valid index for level 1
        assertEquals(0.5, modifier);
    }

    @Test
    public void testGetScoreIncrease_ValidColor() {
        // Test that getScoreIncrease returns the correct value for a valid color
        int scoreIncrease = configReader.getScoreIncrease("blue");

        // Verify that the score increase matches the expected value from the configuration
        assertEquals(20, scoreIncrease);
    }

    @Test
    public void testGetScoreDecrease_ValidColor() {
        // Test that getScoreDecrease returns the correct value for a valid color
        int scoreDecrease = configReader.getScoreDecrease("orange");

        // Verify that the score decrease matches the expected value from the configuration
        assertEquals(20, scoreDecrease);
    }
}
