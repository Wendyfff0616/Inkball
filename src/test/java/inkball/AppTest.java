package inkball;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;

import java.util.ArrayList;

public class AppTest {

    static App app;

    @BeforeAll
    public static void setup() {
        app = new App();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup(); // Initialize the app
    }

    @BeforeEach
    public void beforeEach() {
        // Reset the app state before each test
        app.currentLevelIndex = 0;
        app.score = 0;
        app.isPaused = false;
        app.isGameEnded = false;
        app.levelEnded = false;
        app.timerFinished = false;
        app.remainingTime = 0;
        app.playerLines = new ArrayList<>();
    }

    @Test
    public void testConstructor() {
        // Test that the App constructor initializes the configPath correctly
        assertEquals("config.json", app.configPath);
    }

    @Test
    public void testTogglePause() {
        // Test that togglePause correctly toggles the isPaused state
        boolean initialPausedState = app.getIsPaused();
        app.togglePause();
        assertNotEquals(initialPausedState, app.getIsPaused());
        app.togglePause();
        assertEquals(initialPausedState, app.getIsPaused());
    }

    @Test
    public void testRestartGame() {
        // Test that restartGame resets the game state correctly
        app.currentLevelIndex = 2;
        app.isGameEnded = true;
        app.score = 50;
        app.restartGame();
        assertEquals(0, app.currentLevelIndex);
        assertFalse(app.isGameEnded);
        assertEquals(0, app.score);
        assertNotNull(app.currentLevel);
    }

    @Test
    public void testIncreaseScore() {
        // Test that increaseScore increments the score correctly
        int initialScore = app.score;
        app.increaseScore(10);
        assertEquals(initialScore + 10, app.score);
    }

    @Test
    public void testDecreaseScore() {
        // Test that decreaseScore decrements the score correctly
        app.score = 50;
        app.decreaseScore(20);
        assertEquals(30, app.score);
        // Ensure score can go negative as per current implementation
        app.decreaseScore(50);
        assertEquals(-20, app.score);
    }

    @Test
    public void testLoadLevel() {
        // Test that loadLevel loads the level configuration correctly
        app.loadLevel(0);
        assertNotNull(app.currentLevel);
        assertEquals(0, app.currentLevelIndex);
    }

    @Test
    public void testNextLevel() {
        // Test that nextLevel increments the level index or ends the game if no more levels
        app.totalLevels = 2;
        app.currentLevelIndex = 0;
        app.isGameEnded = false;
        app.nextLevel();
        assertEquals(1, app.currentLevelIndex);
        assertFalse(app.isGameEnded);
        app.nextLevel();
        assertTrue(app.isGameEnded);
    }

    @Test
    public void testUpdateTimer() {
        app.isPaused = false;
        app.remainingTime = 10;
        app.totalTime = 10;
        app.startTime = System.currentTimeMillis() - 5000; // Simulate 5 seconds elapsed
        app.timerFinished = false;

        app.updateTimer();

        // Remaining time should have decreased
        assertEquals(5, app.remainingTime, "Remaining time should decrease based on elapsed time");
        assertFalse(app.timerFinished, "Timer should not be finished yet");
    }

    @Test
    public void testSpawnNewBall() {
        // Test that spawnNewBall moves a ball from unspawnedBalls to currentLevel balls
        // Mock Ball and Spawner for testing
        Ball mockBall = new Ball(0, 0, 0xFF0000, 12, app); // Red ball
        app.unspawnedBalls = new ArrayList<>();
        app.unspawnedBalls.add(mockBall);

        Spawner mockSpawner = new Spawner(100, 100, -1, app);
        app.currentLevel = new Level(0, app.configReader, app);
        app.currentLevel.getSpawners().add(mockSpawner);

        int unspawnedSizeBefore = app.unspawnedBalls.size();
        app.spawnNewBall();
        assertEquals(unspawnedSizeBefore - 1, app.unspawnedBalls.size());
        assertEquals(1, app.currentLevel.getBalls().size());
    }

    @Test
    public void testAddUnspawnedBall() {
        // Test that addUnspawnedBall adds a ball back to unspawnedBalls
        Ball mockBall = new Ball(100, 100, 0xFF0000, 12, app);
        app.unspawnedBalls = new ArrayList<>();
        int initialSize = app.unspawnedBalls.size();
        app.addUnspawnedBall(mockBall);
        assertEquals(initialSize + 1, app.unspawnedBalls.size());
        assertTrue(app.unspawnedBalls.contains(mockBall));
    }

    @Test
    public void testGetAndSetRemainingTime() {
        // Test getRemainingTime and setRemainingTime methods
        app.setRemainingTime(30);
        assertEquals(30, app.getRemainingTime());
    }

    @Test
    public void testIsTimerFinished() {
        // Test isTimerFinished method
        app.timerFinished = false;
        assertFalse(app.isTimerFinished());
        app.timerFinished = true;
        assertTrue(app.isTimerFinished());
    }

    @Test
    public void testKeyPressed_GameEnded() {
        // Simulate the game has ended
        app.isGameEnded = true;
        app.handleKeyPress('r');
        // Verify that the game has been restarted
        assertFalse(app.isGameEnded, "Game should not be ended after restarting");
        assertEquals(0, app.currentLevelIndex, "Current level index should be reset to 0");
        assertEquals(0, app.score, "Score should be reset to 0 after restarting game");
    }

    @Test
    public void testKeyPressed_LevelEnded() {
        // Simulate the level has ended
        app.isGameEnded = false;
        app.levelEnded = true;
        app.timerFinished = false;
        app.handleKeyPress('r');
        // Verify that the level has been restarted
        assertFalse(app.levelEnded, "Level should not be ended after restarting");
        assertFalse(app.timerFinished, "Timer should not be finished after restarting level");
        assertNotNull(app.currentLevel, "Current level should be initialized after restarting level");
    }

    @Test
    public void testKeyPressed_TogglePause() {
        // Ensure game is not paused
        app.isPaused = false;
        app.isGameEnded = false;
        app.handleKeyPress(' ');
        assertTrue(app.isPaused, "Game should be paused after pressing spacebar");
        app.handleKeyPress(' ');
        assertFalse(app.isPaused, "Game should resume after pressing spacebar again");
    }

    @Test
    public void testMousePressed_AddLine() {
        // Test mousePressed to add a new player-drawn line
        app.mouseX = 100;
        app.mouseY = App.TOPBAR + 10; // Ensure mouseY > TOPBAR
        app.mouseButton = PApplet.LEFT;
        app.isGameEnded = false;
        app.keyPressed = false;
        app.playerLines.clear();
        app.mousePressed();
        assertEquals(1, app.playerLines.size());
    }

    @Test
    public void testHandleMousePress_LeftClick_RemoveLineWithoutCtrl() {
        // Assuming left-click without CTRL does not remove a line
        // Add a line
        PlayerDrawnLine line = new PlayerDrawnLine();
        line.addPoint(100, 100);
        app.playerLines.add(line);

        int mouseX = 100;
        int mouseY = App.TOPBAR + 10; // Below TOPBAR
        app.isGameEnded = false;
        app.handleMousePress(mouseX, mouseY, PConstants.LEFT, false);
        // The line should not be removed
        assertFalse(app.playerLines.isEmpty(), "Line should not be removed with Left Click without CTRL");
    }

    @Test
    public void testMouseDragged() {
        // Test mouseDragged to add points to the current player-drawn line
        app.mouseX = 100;
        app.mouseY = App.TOPBAR + 20;
        app.mouseButton = PApplet.LEFT;
        app.isGameEnded = false;
        // Start a line
        PlayerDrawnLine line = new PlayerDrawnLine();
        line.addPoint(90, App.TOPBAR + 15);
        app.playerLines.add(line);
        app.mouseDragged();
        assertEquals(2, line.getPoints().size());
    }

    @Test
    public void testRemoveLineAt() {
        // Add a line at a different location
        PlayerDrawnLine line = new PlayerDrawnLine();
        line.addPoint(200, 200);
        app.playerLines.add(line);

        // Attempt to remove line at (100, 100)
        app.removeLineAt(100, 100);
        // The line should still exist
        assertFalse(app.playerLines.isEmpty(), "No line should be removed if none exist at the given coordinates");
        assertEquals(1, app.playerLines.size(), "There should still be one line in playerLines");
    }

    @Test
    public void testUpdateGame_LevelEndsNormally() {
        // Test updateGame when all balls are cleared, and unspawnedBalls are empty
        app.unspawnedBalls.clear();
        app.currentLevel = new Level(0, app.configReader, app);
        app.currentLevel.getBalls().clear();
        app.updateGame();
        assertTrue(app.levelEnded);
        assertTrue(app.isGameEnded);
    }

    @Test
    public void testUpdateGame_TimerFinished() {
        // Test updateGame when timer finishes and balls remain
        app.timerFinished = true;
        app.currentLevel = new Level(0, app.configReader, app);
        Ball mockBall = new Ball(100, 100, 0xFF0000, 12, app);
        app.currentLevel.getBalls().add(mockBall);
        app.updateGame();
        assertTrue(app.levelEnded);
        assertTrue(app.isGameEnded);
    }

    @Test
    public void testSpawnNewBallImmediate() {
        // Test that spawnNewBallImmediate respawns a ball immediately
        Ball mockBall = new Ball(0, 0, 0xFF0000, 12, app);
        app.currentLevel = new Level(0, app.configReader, app);
        app.currentLevel.removeBall(mockBall);
        app.currentLevel.getSpawners().add(new Spawner(100, 100, -1, app));
        app.spawnNewBallImmediate(mockBall);
        assertTrue(mockBall.getIsActive());
        assertEquals(1, app.currentLevel.getBalls().size());
    }

    @Test
    public void testRestartLevel() {
        // Test that restartLevel reloads the current level
        app.currentLevelIndex = 1;
        app.score = 50;
        app.restartLevel();
        assertEquals(1, app.currentLevelIndex);
        assertEquals(0, app.score); // Assuming score resets on level restart
        assertNotNull(app.currentLevel);
    }

    @Test
    public void testGetIsPaused() {
        // Test getIsPaused method
        app.isPaused = false;
        assertFalse(app.getIsPaused());
        app.isPaused = true;
        assertTrue(app.getIsPaused());
    }

    @Test
    public void testDrawTopBar() {
        // Test that drawTopBar executes without errors
        // Since drawing methods have side effects on the PApplet graphics,
        // we can only ensure that no exceptions are thrown during execution
        assertDoesNotThrow(() -> app.drawTopBar());
    }

    @Test
    public void testRender() {
        // Ensure necessary components are initialized
        app.currentLevel = new Level(0, app.configReader, app);
        app.unspawnedBalls = new ArrayList<>();
        app.isGameEnded = false;
        app.isPaused = false;
        // Call render and ensure no exceptions are thrown
        assertDoesNotThrow(() -> app.render(), "Render should not throw an exception");
    }

    @Test
    public void testPostRender() {
        // Test that postRender executes without errors
        assertDoesNotThrow(() -> app.postRender());
    }

    @Test
    public void testMainMethod() {
        // Test that main method executes without errors
        assertDoesNotThrow(() -> App.main(new String[]{}));
    }

    // Test Cases for Key Events

    @Test
    public void testKeyPressed_PauseKey() {
        // Ensure game is not ended
        app.isGameEnded = false;
        app.levelEnded = false;
        app.timerFinished = false;
        app.isPaused = false;

        // Simulate pressing the pause key (spacebar)
        app.key = ' ';
        app.keyPressed();
        assertTrue(app.getIsPaused(), "Game should be paused after pressing spacebar");

        // Press spacebar again to unpause
        app.keyPressed();
        assertFalse(app.getIsPaused(), "Game should resume after pressing spacebar again");
    }

    @Test
    public void testKeyPressed_RestartGameKey_GameEnded() {
        // Simulate the game has ended
        app.isGameEnded = true;
        app.key = 'r';
        app.keyPressed();
        // Verify that the game has been restarted
        assertFalse(app.isGameEnded, "Game should be restarted after pressing 'r'");
        assertEquals(0, app.currentLevelIndex, "Current level index should be reset to 0");
        assertEquals(0, app.score, "Score should be reset to 0 after restarting game");
    }

    @Test
    public void testKeyPressed_RestartLevelKey_LevelEnded() {
        // Simulate the level has ended
        app.levelEnded = true;
        app.key = 'r';
        app.keyPressed();
        // Verify that the level has been restarted
        assertFalse(app.levelEnded, "Level should be restarted after pressing 'r'");
        // Level should be reloaded
        assertNotNull(app.currentLevel, "Current level should be initialized after restarting level");
    }

    @Test
    public void testKeyPressed_RestartLevelKey_TimerFinished() {
        // Simulate the timer has finished
        app.timerFinished = true;
        app.key = 'r';
        app.keyPressed();
        // Verify that the level has been restarted
        assertFalse(app.timerFinished, "Timer should be reset after restarting level");
        // Level should be reloaded
        assertNotNull(app.currentLevel, "Current level should be initialized after restarting level");
    }

    @Test
    public void testKeyPressed_RestartLevelKey_GameActive() {
        // Simulate the game is active
        app.key = 'r';
        app.keyPressed();
        // Verify that the level has been restarted
        assertEquals(0, app.currentLevelIndex, "Current level index should remain the same after restarting level");
        assertEquals(0, app.score, "Score should reset to 0 after restarting level");
        // Level should be reloaded
        assertNotNull(app.currentLevel, "Current level should be initialized after restarting level");
    }

    @Test
    public void testKeyPressed_InvalidKey() {
        // Simulate pressing an invalid key
        app.key = 'x';
        app.keyPressed();
        // No changes should occur
        assertFalse(app.getIsPaused(), "Game should not pause after pressing an invalid key");
        assertFalse(app.isGameEnded, "Game should not end after pressing an invalid key");
    }

    // Test Cases for Mouse Events

    @Test
    public void testMousePressed_LeftButton_DrawLine() {
        int mouseX = 100;
        int mouseY = App.TOPBAR + 10; // Ensure it's below TOPBAR
        app.isGameEnded = false;
        app.handleMousePress(mouseX, mouseY, PConstants.LEFT, false);
        // A new line should be added
        assertFalse(app.playerLines.isEmpty(), "A new line should be added to playerLines");
        assertEquals(1, app.playerLines.size(), "There should be exactly one line in playerLines");
        PlayerDrawnLine line = app.playerLines.get(0);
        assertEquals(1, line.getPoints().size(), "The new line should have one point");
        assertEquals(mouseX, line.getPoints().get(0).x, 0.01, "The point x-coordinate should match mouseX");
        assertEquals(mouseY, line.getPoints().get(0).y, 0.01, "The point y-coordinate should match mouseY");
    }

    @Test
    public void testMousePressed_RightButton_RemoveLine() {
        // Add a line to remove
        PlayerDrawnLine line = new PlayerDrawnLine();
        line.addPoint(100, 100);
        app.playerLines.add(line);

        int mouseX = 100;
        int mouseY = App.TOPBAR + 10; // Ensure it's below TOPBAR
        app.isGameEnded = false;
        app.handleMousePress(mouseX, mouseY, PConstants.RIGHT, false);
        // The line should be removed
        assertTrue(app.playerLines.isEmpty(), "Line should be removed with Right Click");
    }

    @Test
    public void testMousePressed_LeftButton_RemoveLineWithCtrl() {
        // Add a line to remove
        PlayerDrawnLine line = new PlayerDrawnLine();
        line.addPoint(100, 100);
        app.playerLines.add(line);

        int mouseX = 100;
        int mouseY = App.TOPBAR + 10; // Ensure it's below TOPBAR
        app.isGameEnded = false;
        app.handleMousePress(mouseX, mouseY, PConstants.LEFT, true); // CTRL key is pressed
        // The line should be removed
        assertTrue(app.playerLines.isEmpty(), "Line should be removed with Ctrl + Left Click");
    }

    @Test
    public void testMousePressed_GameEnded() {
        // Simulate mouse pressed when game has ended
        app.isGameEnded = true;
        app.mouseButton = PConstants.LEFT;
        app.mouseX = 100;
        app.mouseY = 100;
        app.mousePressed();
        // Should not start a new line
        assertTrue(app.playerLines.isEmpty(), "No lines should be added when game has ended");
    }

    @Test
    public void testMousePressed_MouseAboveTopBar() {
        // Simulate mouse pressed above TOPBAR
        app.mouseButton = PConstants.LEFT;
        app.mouseX = 100;
        app.mouseY = App.TOPBAR - 10; // Above TOPBAR
        app.isGameEnded = false;
        app.mousePressed();
        // Should not start a new line
        assertTrue(app.playerLines.isEmpty(), "No lines should be added when mouse is above TOPBAR");
    }

    @Test
    public void testMouseDragged_DrawingLine() {
        // Start drawing a line
        app.mouseButton = PConstants.LEFT;
        app.mouseX = 100;
        app.mouseY = App.TOPBAR + 10; // Ensure it's below TOPBAR
        app.isGameEnded = false;
        app.mousePressed(); // Start line

        // Drag the mouse to extend the line
        app.mouseX = 110;
        app.mouseY = App.TOPBAR + 20;
        app.mouseDragged();

        // Current line should have multiple points
        PlayerDrawnLine line = app.playerLines.get(app.playerLines.size() - 1);
        assertTrue(line.getPoints().size() > 1, "Current line should have multiple points after dragging");
    }

    @Test
    public void testMouseDragged_NotDrawingLine() {
        // Not started drawing a line
        app.playerLines.clear();
        app.mouseButton = PConstants.LEFT;
        app.mouseX = 100;
        app.mouseY = App.TOPBAR + 10;
        app.isGameEnded = false;
        app.mouseDragged();
        // No action should be taken
        assertTrue(app.playerLines.isEmpty(), "Player lines should remain empty when not drawing a line");
    }

    @Test
    public void testMouseDragged_GameEnded() {
        // Start drawing a line
        app.mouseButton = PConstants.LEFT;
        app.mouseX = 100;
        app.mouseY = App.TOPBAR + 10;
        app.isGameEnded = false;
        app.mousePressed(); // Start line

        // Set game as ended
        app.isGameEnded = true;

        // Attempt to drag the mouse
        app.mouseX = 110;
        app.mouseY = App.TOPBAR + 20;
        app.mouseDragged();

        // Line should not have additional points
        PlayerDrawnLine line = app.playerLines.get(app.playerLines.size() - 1);
        assertEquals(1, line.getPoints().size(), "Line should not have additional points when game has ended");
    }

    // Test Cases for Line Removal

    @Test
    public void testRemoveLineAt_LineExists() {
        // Add a line to playerLines
        PlayerDrawnLine line = new PlayerDrawnLine();
        line.addPoint(100, 100);
        line.addPoint(150, 150);
        app.playerLines.add(line);

        // Attempt to remove line at (100, 100)
        app.removeLineAt(100, 100);
        // Line should be removed
        assertTrue(app.playerLines.isEmpty(), "Line should be removed if it exists at the given coordinates");
    }

    @Test
    public void testRemoveLineAt_LineDoesNotExist() {
        // playerLines is empty
        app.playerLines.clear();

        // Attempt to remove line at arbitrary coordinates
        app.removeLineAt(200, 200);
        // No action should be taken
        assertTrue(app.playerLines.isEmpty(), "Player lines should remain empty when no line exists at given coordinates");
    }

    // Test Cases for Timer

    @Test
    public void testUpdateTimer_GamePaused() {
        // Simulate updating timer when game is paused
        app.isPaused = true;
        app.remainingTime = 30;
        int initialTime = app.remainingTime;
        app.updateTimer();
        // Remaining time should not decrease
        assertEquals(initialTime, app.remainingTime, "Remaining time should not change when game is paused");
    }

    @Test
    public void testUpdateTimer_TimeRunningOut() {
        app.isPaused = false;
        app.remainingTime = 1;
        app.totalTime = 1;
        app.startTime = System.currentTimeMillis() - 2000; // Simulate 2 seconds elapsed
        app.timerFinished = false;

        app.updateTimer();

        // Remaining time should be 0, and timerFinished should be true
        assertEquals(0, app.remainingTime, "Remaining time should be 0 after time runs out");
        assertTrue(app.timerFinished, "Timer should be marked as finished");
    }

    @Test
    public void testUpdateTimer_NormalUpdate() {
        app.isPaused = false;
        app.remainingTime = 30;
        app.totalTime = 30;
        app.startTime = System.currentTimeMillis() - 1000; // Simulate 1 second elapsed
        app.timerFinished = false;

        app.updateTimer();

        // Remaining time should decrease by 1
        assertEquals(29, app.remainingTime, "Remaining time should decrease by 1 after update");
        assertFalse(app.timerFinished, "Timer should not be finished");
    }

    // Test Cases for Level Loading

    @Test
    public void testLoadLevel_ValidIndex() {
        // Simulate loading a valid level
        app.loadLevel(0);
        assertNotNull(app.currentLevel, "Current level should be initialized after loading level");
    }

    @Test
    public void testLoadLevel_InvalidIndex() {
        // Attempt to load an invalid level index
        int invalidIndex = -1;
        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            app.loadLevel(invalidIndex);
        });
        assertNotNull(exception, "An IndexOutOfBoundsException should be thrown for invalid level index");
    }

    // Test Cases for Game Update

    @Test
    public void testUpdateGame_GamePaused() {
        // Simulate updating game when paused
        app.isPaused = true;
        assertDoesNotThrow(() -> app.updateGame(), "updateGame should not throw an exception when game is paused");
    }

    @Test
    public void testUpdateGame_GameEnded() {
        // Simulate updating game when ended
        app.isGameEnded = true;
        assertDoesNotThrow(() -> app.updateGame(), "updateGame should not throw an exception when game has ended");
    }

    @Test
    public void testUpdateGame_NormalUpdate() {
        // Simulate normal game update
        app.isGameEnded = false;
        app.isPaused = false;
        assertDoesNotThrow(() -> app.updateGame(), "updateGame should not throw an exception during normal update");
    }

    // Test Cases for Rendering

    @Test
    public void testRender_GameRunning() {
        // Ensure game is running
        app.isGameEnded = false;
        app.isPaused = false;
        app.levelEnded = false;
        app.currentLevel = new Level(0, app.configReader, app);
        app.unspawnedBalls = new ArrayList<>();
        // Call render and ensure no exceptions are thrown
        assertDoesNotThrow(() -> app.render(), "Render should not throw an exception when game is running");
    }

    @Test
    public void testRender_GamePaused() {
        // Simulate rendering while game is paused
        app.isPaused = true;
        assertDoesNotThrow(() -> app.render(), "render should not throw an exception when game is paused");
    }

    @Test
    public void testRender_GameEnded() {
        // Simulate rendering when game is over
        app.isGameEnded = true;
        assertDoesNotThrow(() -> app.render(), "render should not throw an exception when game has ended");
    }

    // Test Cases for Spawning Balls

    @Test
    public void testSpawnNewBall_UnspawnedBallsExist() {
        // Initialize unspawnedBalls and currentLevel
        app.unspawnedBalls = new ArrayList<>();
        app.currentLevel = new Level(0, app.configReader, app);

        // Add an unspawned ball
        Ball unspawnedBall = new Ball(0, 0, 0, 12, app);
        app.unspawnedBalls.add(unspawnedBall);

        app.spawnNewBall();

        // The ball should now be active and in the current level's balls list
        assertTrue(app.currentLevel.getBalls().contains(unspawnedBall), "Unspawned ball should be added to the level");
        assertFalse(app.unspawnedBalls.contains(unspawnedBall), "Unspawned ball should be removed from unspawnedBalls list");
    }

    @Test
    public void testSpawnNewBall_NoUnspawnedBalls() {
        // Ensure unspawnedBalls list is empty and initialized
        app.unspawnedBalls = new ArrayList<>();
        app.currentLevel = new Level(0, app.configReader, app);

        app.spawnNewBall();

        // No balls should be added to currentLevel
        assertTrue(app.currentLevel.getBalls().isEmpty(), "No balls should be spawned when unspawnedBalls list is empty");
    }

    @Test
    public void testSetRemainingTime() {
        // Test setting remaining time
        app.setRemainingTime(60);
        assertEquals(60, app.remainingTime, "Remaining time should be set to the specified value");
    }

}
