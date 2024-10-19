package inkball;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 576;
    public static int HEIGHT = 640;
    public static final int BOARD_WIDTH = WIDTH / CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int FPS = 30;

    ConfigReader configReader;
    boolean isPaused = false;
    boolean isGameEnded = false;
    int currentLevelIndex = 0;
    int totalLevels;
    Level currentLevel;
    int score = 0; // Total score

    public List<Ball> unspawnedBalls; // List of unspawned balls

    int spawnCounter;

    List<PlayerDrawnLine> playerLines = new ArrayList<>();

    public String configPath;

    public static Random random = new Random();

    // Timer-related variables
    int totalTime;       // Total time for the current level in seconds
    int remainingTime;   // Remaining time in seconds
    long startTime;      // Start time in milliseconds
    boolean timerFinished; // Indicates whether the timer has finished

    boolean levelEnded = false;

    /**
     * Constructor to initialize the App with the config path.
     */
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialize the settings for the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images, and initialize elements such as player and map elements.
     */
    @Override
    public void setup() {
        frameRate(FPS);
        configReader = new ConfigReader("config.json", this);
        totalLevels = configReader.getNumLevels();
        loadLevel(currentLevelIndex);
    }

    /**
     * Load a specific level by its index and initialize balls and level layout.
     *
     * @param index the index of the level to load
     */
    public void loadLevel(int index) {
        JSONObject levelConfig = configReader.getLevelConfig(index);
        JSONObject config = configReader.getConfig(); // Get the entire config object
        if (levelConfig != null) {
            String layoutFile = levelConfig.getString("layout");
            int levelTime = levelConfig.getInt("time");
            int spawnInterval = levelConfig.getInt("spawn_interval");
            spawnCounter = spawnInterval * App.FPS;

            // Initialize the unspawnedBalls list
            unspawnedBalls = new ArrayList<>();

            // Get ball colors for the current level
            JSONArray ballColors = configReader.getBallColors(index);
            for (int i = 0; i < ballColors.size(); i++) {
                String colorName = ballColors.getString(i);
                int color = ColorUtils.colorToNumber(colorName);
                int initialX = 10 + i * 30;
                int initialY = 30;
                Ball ball = new Ball(initialX, initialY, color, 12, this);
                unspawnedBalls.add(ball);
            }

            // Create a new Level instance, passing the levelConfig, config, and App instance
            currentLevel = new Level(index, configReader, this);

            // Load the level layout
            currentLevel.loadLevel(layoutFile, this);

            // Immediately spawn a new ball
            spawnNewBall();

            // Initialize timer
            totalTime = levelTime; // Assuming getTimeLeft returns time in seconds
            remainingTime = totalTime;
            timerFinished = false;
            startTime = System.currentTimeMillis();

            // Reset game state for the new level
            levelEnded = false;
            isGameEnded = false;

            playerLines.clear();
        }
    }

    /**
     * Proceed to the next level or end the game if all levels are completed.
     */
    public void nextLevel() {
        currentLevelIndex++;
        if (currentLevelIndex < totalLevels) {
            loadLevel(currentLevelIndex);
        } else {
            isGameEnded = true;
            println("Congratulations! You've completed all levels.");
        }
    }

    /**
     * Add a ball back to the unspawned queue.
     *
     * @param ball the ball to be re-added to the queue
     */
    public void addUnspawnedBall(Ball ball) {
        ball.resetRadius();
        ball.setX(20 + unspawnedBalls.size() * 30);
        ball.setY(30);
        unspawnedBalls.add(ball);
    }

    /**
     * Spawn a new ball at a random spawner location.
     */
    public void spawnNewBall() {
        if (!unspawnedBalls.isEmpty()) {
            Ball ball = unspawnedBalls.remove(0);

            // Get a random spawner position
            if (!currentLevel.getSpawners().isEmpty()) {
                int randomIndex = random.nextInt(currentLevel.getSpawners().size());
                Spawner spawner = currentLevel.getSpawners().get(randomIndex);
                ball.setX(spawner.getX());
                ball.setY(spawner.getY());
            }

            ball.setIsActive(true);
            ball.setXVelocity(ball.getRandomVelocity());
            ball.setYVelocity(ball.getRandomVelocity());
            currentLevel.addBall(ball);
        }
    }

    /**
     * Restart the current level.
     */
    public void restartLevel() {
        loadLevel(currentLevelIndex);
        playerLines.clear();
        score = 0; // Reset level score (optional)
        println("Restarted current level: " + currentLevelIndex);
    }

    /**
     * Restart the entire game from the first level.
     */
    public void restartGame() {
        currentLevelIndex = 0;
        isGameEnded = false;
        loadLevel(currentLevelIndex);
        playerLines.clear();
        score = 0;
        println("Game restarted.");
    }

    /**
     * Toggle the pause state of the game.
     */
    public void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            println("Game Paused");
        } else {
            println("Game Resumed");
            // Adjust startTime to account for the pause duration
            startTime = System.currentTimeMillis() - ((totalTime - remainingTime) * 1000);
        }
    }

    public boolean getIsPaused() {
        return isPaused;
    }

    /**
     * Handle key presses for restarting and pausing the game.
     *
     * @param key the key character that was pressed
     */
    public void handleKeyPress(char key) {
        // If the game is ended, handle restart event
        if (isGameEnded) {
            if (key == 'r' || key == 'R') {
                restartGame();
            }
            return;
        }

        // If the level is ended or time is up, allow restarting the level
        if (levelEnded || timerFinished) {
            if (key == 'r' || key == 'R') {
                restartLevel();
            }
            return;
        }

        // Handle events when the game is active
        if (key == 'r' || key == 'R') {
            restartLevel();
        }

        if (key == ' ') {
            togglePause();
        }
    }

    @Override
    public void keyPressed() {
        handleKeyPress(key);
    }

    /**
     * Handle mouse presses to draw or remove lines.
     *
     * @param mouseX       the x-coordinate of the mouse
     * @param mouseY       the y-coordinate of the mouse
     * @param mouseButton  the mouse button that was pressed
     * @param isCtrlPressed whether the CTRL key is pressed
     */
    public void handleMousePress(int mouseX, int mouseY, int mouseButton, boolean isCtrlPressed) {
        if (mouseY > TOPBAR && !isGameEnded) {
            // First check for CTRL + left click to remove a line
            if (mouseButton == LEFT && isCtrlPressed) {
                removeLineAt(mouseX, mouseY);
            }
            // Otherwise, add a new line on left click
            else if (mouseButton == LEFT) {
                PlayerDrawnLine newLine = new PlayerDrawnLine();
                newLine.addPoint(mouseX, mouseY);
                playerLines.add(newLine);
            }
            // Remove a line on right click
            if (mouseButton == RIGHT) {
                removeLineAt(mouseX, mouseY);
            }
        }
    }

    @Override
    public void mousePressed() {
        boolean isCtrlPressed = keyPressed && keyCode == CONTROL;
        handleMousePress(mouseX, mouseY, mouseButton, isCtrlPressed);
    }

    /**
     * Handle mouse dragging to extend a drawn line.
     *
     * @param mouseX      the x-coordinate of the mouse
     * @param mouseY      the y-coordinate of the mouse
     * @param mouseButton the mouse button being pressed
     */
    public void handleMouseDrag(int mouseX, int mouseY, int mouseButton) {
        if (mouseY > TOPBAR && !isGameEnded) {
            if (mouseButton == LEFT && !playerLines.isEmpty()) {
                PlayerDrawnLine currentLine = playerLines.get(playerLines.size() - 1);
                currentLine.addPoint(mouseX, mouseY);
            }
        }
    }

    @Override
    public void mouseDragged() {
        handleMouseDrag(mouseX, mouseY, mouseButton);
    }

    /**
     * Remove a drawn line near the specified position.
     *
     * @param x the x position
     * @param y the y position
     */
    void removeLineAt(float x, float y) {
        for (int i = playerLines.size() - 1; i >= 0; i--) {
            PlayerDrawnLine line = playerLines.get(i);
            if (line.isNear(x, y)) {
                playerLines.remove(i);
                break;
            }
        }
    }

    /**
     * Draw the top bar displaying score, time, and ball spawn countdown.
     */
    public void drawTopBar() {
        fill(0);
        textSize(20);
        textAlign(LEFT, CENTER);

        // Display the score
        text("Score: " + score, 450, TOPBAR - 45);
        text("Time: " + remainingTime, 450, TOPBAR - 15);

        // Only show the ball spawn countdown if necessary
        if (!unspawnedBalls.isEmpty()) {
            float timeRemaining = Math.max(spawnCounter / (float) App.FPS, 0);
            text(String.format("%.1f", timeRemaining), 192, TOPBAR - 30);
        } else {
            spawnCounter = 0;
        }

        // Display bottom black frame
        fill(0);
        rect(0, 12, 160, 36); // Draw black rectangle at the bottom
    }

    /**
     * Main drawing method that updates and draws game elements.
     */
    @Override
    public void draw() {
        background(200, 200, 200);

        //----------------------------------
        // Render Phase
        //----------------------------------
        render();

        //----------------------------------
        // Update Phase
        //----------------------------------
        if (!isPaused && !isGameEnded && !levelEnded) {
            updateGame();
        }

        //----------------------------------
        // Final Render Phase
        //----------------------------------
        postRender();
    }

    /**
     * Render game elements.
     */
    void render() {
        drawTopBar();

        // Isolate clipping using push/pop
        pushMatrix();
        pushStyle();
        clip(0, 12, 160, 36); // Define the clipping region

        // Draw unspawned balls, only display at most 5
        for (int i = 0; i < Math.min(5, unspawnedBalls.size()); i++) {
            Ball ball = unspawnedBalls.get(i);

            // Move the unspawned balls to the left by 1 pixel per frame
            if (ball.getX() > 20 + i * 30) {
                ball.setX(ball.getX() - 1);
            }
            // Ensure the ball is within the clipping region
            if (ball.getX() >= 0 && ball.getX() <= 160 && ball.getY() >= 12 && ball.getY() <= 48) {
                ball.draw(this);
            }

        }

        // Disable clipping to avoid affecting further drawing
        noClip(); // Disable clipping
        popStyle();
        popMatrix(); // Restore transformation and style states

        if (currentLevel != null) {
            // Display pause message if the game is paused
            if (isPaused) {
                fill(0);
                textSize(20);
                textAlign(CENTER, CENTER);
                text("***PAUSED***", WIDTH / 2 + 50, TOPBAR / 2);
            }

            // Draw the current level
            currentLevel.draw(this);
        }

        // Draw player-drawn lines
        for (PlayerDrawnLine line : playerLines) {
            line.draw(this);
        }

        // Display game end messages
        if (timerFinished && !(currentLevel.getBalls().isEmpty() && unspawnedBalls.isEmpty())) {
            fill(0);
            textSize(20);
            textAlign(CENTER, CENTER);
            text("===TIME’S UP===", WIDTH / 2 + 50, TOPBAR / 2);
        } else if (isGameEnded) {
            fill(0);
            textSize(20);
            textAlign(CENTER, CENTER);
            text("===ENDED===", WIDTH / 2 + 50, TOPBAR / 2);
        }
    }

    /**
     * Update game logic.
     */
    void updateGame() {
        // Update timer
        updateTimer();

        // Update balls and check collisions
        currentLevel.update(this);  // Update balls, check collisions

        // Check for ball collisions with player-drawn lines
        for (Ball ball : currentLevel.getBalls()) {
            for (PlayerDrawnLine line : playerLines) {
                line.checkCollision(ball);
            }
        }

        // Remove inactive balls and add them back to the unspawned queue
        List<Ball> inactiveBalls = new ArrayList<>();
        for (Ball ball : currentLevel.getBalls()) {
            if (!ball.getIsActive()) {
                inactiveBalls.add(ball);
            }
        }
        for (Ball ball : inactiveBalls) {
            currentLevel.removeBall(ball);
            addUnspawnedBall(ball);
        }

        // Update spawn counter and spawn a new ball if ready
        if (spawnCounter > 0) {
            spawnCounter--;
        } else {
            spawnNewBall();
            spawnCounter = configReader.getLevelConfig(currentLevelIndex)
                    .getInt("spawn_interval") * App.FPS;
        }

        // Check if the level should end
        if (currentLevel.getBalls().isEmpty() && unspawnedBalls.isEmpty()) {
            currentLevel.endLevel(this, "normal");
            levelEnded = true;
            isGameEnded = true;
        }

        // Update all holes to manage attracted balls
        for (Hole hole : currentLevel.getHoles()) {
            hole.updateAttractedBalls();
        }

        // Handle level end due to time up
        if (timerFinished && !(currentLevel.getBalls().isEmpty() && unspawnedBalls.isEmpty())) {
            currentLevel.endLevel(this, "timeUp"); // End the level only once
            levelEnded = true;  // Mark the level as ended to prevent multiple calls
            isGameEnded = true; // Stop further game updates
        }
    }

    /**
     * Final rendering tasks after game updates.
     */
    void postRender() {
        // Currently, all rendering is handled in render()
        // Additional post-rendering tasks can be added here if necessary
    }

    /**
     * Update the timer based on the elapsed time.
     */
    public void updateTimer() {
        if (timerFinished || (currentLevel != null && currentLevel.getIsLevelEnded())) return;

        if (!isPaused) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = (currentTime - startTime) / 1000; // Convert to seconds
            remainingTime = totalTime - (int) elapsedTime;

            if (remainingTime <= -1) {
                remainingTime = 0;
                timerFinished = true;
            }
        }
    }

    /**
     * Gets the remaining time for the current level.
     *
     * @return The remaining time in seconds.
     */
    public int getRemainingTime() {
        return remainingTime;
    }

    /**
     * Sets the remaining time for the level.
     *
     * @param time The new remaining time in seconds.
     */
    public void setRemainingTime(int time) {
        this.remainingTime = time;
    }

    public boolean isTimerFinished() {
        return timerFinished;
    }

    /**
     * Increase the game score by a specified amount.
     *
     * @param amount The amount to increase the score by.
     */
    public void increaseScore(int amount) {
        score += amount;
    }

    /**
     * Decrease the game score by a specified amount.
     * Ensures the score does not fall below zero.
     *
     * @param amount The amount to decrease the score by.
     */
    public void decreaseScore(int amount) {
        score -= amount;
    }

    /**
     * Spawn a new ball immediately, ignoring the timer.
     * Resets the ball position, sets it as active, and re-adds it to the current level.
     *
     * @param ball The ball to spawn immediately.
     */
    public void spawnNewBallImmediate(Ball ball) {
        currentLevel.removeBall(ball);

        // Set the ball to a random spawner position
        if (!currentLevel.getSpawners().isEmpty()) {
            int randomIndex = random.nextInt(currentLevel.getSpawners().size());
            Spawner spawner = currentLevel.getSpawners().get(randomIndex);
            ball.setX(spawner.getX());
            ball.setY(spawner.getY());
        }

        // Set the ball as active and reset its velocity and radius
        ball.setIsActive(true);
        ball.setXVelocity(ball.getRandomVelocity());
        ball.setYVelocity(ball.getRandomVelocity());
        ball.resetRadius();
        currentLevel.addBall(ball);  // Re-add the ball to the current level
    }

    /**
     * Main entry point for the game application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }
}
