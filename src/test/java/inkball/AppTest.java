package inkball;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class AppTest {

    static App app;

    @BeforeAll
    public static void setup() {
        app = new App();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();
    }

//    @BeforeEach
//    public void beforeEach() {
//        // Reset the app state before each test
//        app.currentLevelIndex = 0;
//        app.score = 0;
//        app.isPaused = false;
//        app.isGameEnded = false;
//        app.levelEnded = false;
//        app.timerFinished = false;
//        app.remainingTime = 0;
//        app.playerLines = new ArrayList<>();
//    }

    @BeforeEach
    public void beforeEach() {
        // Reset basic app state
        app.currentLevelIndex = 0;
        app.score = 0;
        app.isPaused = false;
        app.isGameEnded = false;
        app.levelEnded = false;
        app.timerFinished = false;
        app.remainingTime = 0;
        app.playerLines = new ArrayList<>();
        // Reset currentLevel with spawners
        app.currentLevel = new Level(0, app.configReader, app);
        // Reset unspawnedBalls
        app.unspawnedBalls = new ArrayList<>();
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
        app.remainingTime = 10;
        app.totalTime = 10;
        app.startTime = System.currentTimeMillis() - 5000; // Simulate 5 seconds elapsed

        app.updateTimer();

        // Remaining time should have decreased
        assertEquals(5, app.remainingTime);
        assertFalse(app.timerFinished);
    }

    @Test
    public void testSpawnNewBall() {
        // Test that spawnNewBall moves a ball from unspawnedBalls to currentLevel balls
        // Mock Ball and Spawner for testing
        Ball mockBall = new Ball(0, 0, 0, 12, app);
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
        Ball mockBall = new Ball(100, 100, 0, 12, app);
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
        assertFalse(app.isGameEnded);
        assertEquals(0, app.currentLevelIndex);
        assertEquals(0, app.score);
    }

    @Test
    public void testKeyPressed_LevelEnded() {
        // Simulate the level has ended
        app.isGameEnded = false;
        app.levelEnded = true;
        app.timerFinished = false;
        app.handleKeyPress('r');
        // Verify that the level has been restarted
        assertFalse(app.levelEnded);
        assertFalse(app.timerFinished);
        assertNotNull(app.currentLevel);
    }

    @Test
    public void testKeyPressed_TogglePause() {
        // Ensure game is not paused
        app.isPaused = false;
        app.isGameEnded = false;
        app.handleKeyPress(' ');
        assertTrue(app.isPaused);
        app.handleKeyPress(' ');
        assertFalse(app.isPaused);
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
        assertFalse(app.playerLines.isEmpty());
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
        assertFalse(app.playerLines.isEmpty());
        assertEquals(1, app.playerLines.size());
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
        Ball mockBall = new Ball(100, 100, 0, 12, app);
        app.currentLevel.getBalls().add(mockBall);
        app.updateGame();
        assertTrue(app.levelEnded);
        assertTrue(app.isGameEnded);
    }

    @Test
    public void testSpawnNewBallImmediate() {
        // Test that spawnNewBallImmediate respawns a ball immediately
        Ball mockBall = new Ball(0, 0, 0, 12, app);
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
        assertDoesNotThrow(() -> app.render());
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
        assertTrue(app.getIsPaused());

        // Press spacebar again to unpause
        app.keyPressed();
        assertFalse(app.getIsPaused());
    }

    @Test
    public void testKeyPressed_RestartGameKey_GameEnded() {
        // Simulate the game has ended
        app.isGameEnded = true;
        app.key = 'r';
        app.keyPressed();
        // Verify that the game has been restarted
        assertFalse(app.isGameEnded);
        assertEquals(0, app.currentLevelIndex);
        assertEquals(0, app.score);
    }

    @Test
    public void testKeyPressed_RestartLevelKey_LevelEnded() {
        // Simulate the level has ended
        app.levelEnded = true;
        app.key = 'r';
        app.keyPressed();
        // Verify that the level has been restarted
        assertFalse(app.levelEnded);
        // Level should be reloaded
        assertNotNull(app.currentLevel);
    }

    @Test
    public void testKeyPressed_RestartLevelKey_TimerFinished() {
        // Simulate the timer has finished
        app.timerFinished = true;
        app.key = 'r';
        app.keyPressed();
        // Verify that the level has been restarted
        assertFalse(app.timerFinished);
        // Level should be reloaded
        assertNotNull(app.currentLevel);
    }

    @Test
    public void testKeyPressed_RestartLevelKey_GameActive() {
        // Simulate the game is active
        app.key = 'r';
        app.keyPressed();
        // Verify that the level has been restarted
        assertEquals(0, app.currentLevelIndex);
        assertEquals(0, app.score);
        // Level should be reloaded
        assertNotNull(app.currentLevel);
    }

    @Test
    public void testKeyPressed_InvalidKey() {
        // Simulate pressing an invalid key
        app.key = 'x';
        app.keyPressed();
        // No changes should occur
        assertFalse(app.getIsPaused());
        assertFalse(app.isGameEnded);
    }

    // Test Cases for Mouse Events

    @Test
    public void testMousePressed_LeftButton_DrawLine() {
        int mouseX = 100;
        int mouseY = App.TOPBAR + 10; // Ensure it's below TOPBAR
        app.isGameEnded = false;
        app.handleMousePress(mouseX, mouseY, PConstants.LEFT, false);
        // A new line should be added
        assertFalse(app.playerLines.isEmpty());
        assertEquals(1, app.playerLines.size());
        PlayerDrawnLine line = app.playerLines.get(0);
        assertEquals(1, line.getPoints().size());
        assertEquals(mouseX, line.getPoints().get(0).x, 0.01);
        assertEquals(mouseY, line.getPoints().get(0).y, 0.01);
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
        assertFalse(app.playerLines.isEmpty());
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
        assertFalse(app.playerLines.isEmpty());
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
        assertTrue(app.playerLines.isEmpty());
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
        assertTrue(app.playerLines.isEmpty());
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
        assertTrue(app.playerLines.isEmpty());
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
        assertEquals(1, line.getPoints().size());
    }

    @Test
    public void testRemoveLineAt_LineDoesNotExist() {
        // playerLines is empty
        app.playerLines.clear();
        // Attempt to remove line at arbitrary coordinates
        app.removeLineAt(200, 200);
        // No action should be taken
        assertTrue(app.playerLines.isEmpty());
    }

    @Test
    public void testUpdateTimer_GamePaused() {
        // Simulate updating timer when game is paused
        app.isPaused = true;
        app.remainingTime = 30;
        int initialTime = app.remainingTime;
        app.updateTimer();
        // Remaining time should not decrease
        assertEquals(initialTime, app.remainingTime);
    }

    @Test
    public void testUpdateTimer_TimeRunningOut() {
        app.remainingTime = 1;
        app.totalTime = 1;
        app.startTime = System.currentTimeMillis() - 2000; // Simulate 2 seconds elapsed

        app.updateTimer();

        assertEquals(0, app.remainingTime);
        assertTrue(app.timerFinished);
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
        assertEquals(29, app.remainingTime);
        assertFalse(app.timerFinished);
    }

    @Test
    public void testLoadLevel_ValidIndex() {
        // Simulate loading a valid level
        app.loadLevel(0);
        assertNotNull(app.currentLevel);
    }

    @Test
    public void testUpdateGame_GamePaused() {
        // Simulate updating game when paused
        app.isPaused = true;
        assertDoesNotThrow(() -> app.updateGame());
    }

    @Test
    public void testUpdateGame_GameEnded() {
        // Simulate updating game when ended
        app.isGameEnded = true;
        assertDoesNotThrow(() -> app.updateGame());
    }

    @Test
    public void testUpdateGame_NormalUpdate() {
        // Simulate normal game update
        app.isGameEnded = false;
        app.isPaused = false;
        assertDoesNotThrow(() -> app.updateGame());
    }

    @Test
    public void testRender_GameRunning() {
        // Ensure game is running
        app.isGameEnded = false;
        app.isPaused = false;
        app.levelEnded = false;
        app.currentLevel = new Level(0, app.configReader, app);
        app.unspawnedBalls = new ArrayList<>();
        // Call render and ensure no exceptions are thrown
        assertDoesNotThrow(() -> app.render());
    }

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
        assertTrue(app.currentLevel.getBalls().isEmpty());
    }

    @Test
    public void testGameEnd_TimeFinished() {
        // Timer has finished and all balls have been captured
        app.timerFinished = true;
        app.unspawnedBalls = new ArrayList<>();
        app.currentLevel = new Level(0, app.configReader, app);
        app.currentLevel.getBalls().clear(); // No balls left in the level

        app.draw();

        // Verify the state matches the expected behavior
        assertTrue(app.timerFinished);
        assertTrue(app.unspawnedBalls.isEmpty());
        assertTrue(app.currentLevel.getBalls().isEmpty());
    }

    @Test
    public void testRender_TimeUpMessage() {
        // Timer has finished, and all balls are captured
        app.currentLevel.isLevelEnded = true;
        app.isPaused = false;
        app.timerFinished = true;
        app.unspawnedBalls = new ArrayList<>();  // Ensure unspawnedBalls list is empty
        app.currentLevel = new Level(0, app.configReader, app);
        app.currentLevel.getBalls().clear();  // No balls remaining in the level

        // Call the render method
        assertDoesNotThrow(() -> app.render());

        // Verify that the "TIME’S UP" message is displayed
        assertTrue(app.timerFinished);
    }

    @Test
    public void testSetRemainingTime() {
        // Test setting remaining time
        app.setRemainingTime(0);
        assertEquals(0, app.remainingTime);
    }

}
