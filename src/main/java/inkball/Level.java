package inkball;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.core.PImage;

/**
 * The Level class manages the entities and layout of a specific game level.
 * It loads the level layout from a configuration file and creates game entities such as walls, spawners,
 * holes, and balls. Additionally, it handles the updating and drawing of these entities, as well as
 * managing game logic such as score and timing.
 */
public class Level {
    /** Score increase values for each ball color */
    int[] scoreIncreaseArray = new int[5];
    int[] scoreDecreaseArray = new int[5];
    private List<Ball> balls;
    private List<Wall> walls;
    private List<Spawner> spawners;
    private List<Hole> holes;
    List<AccelerateTile> accelerationTiles;
    private ConfigReader configReader;

    boolean isLevelEnded = false;
    int timeBonusRemaining = 0;
    private PImage defaultTileImage;
    YellowTile yellowTileTopLeft;
    YellowTile yellowTileBottomRight;

    /**
     * Constructs a Level object and initializes the entity lists and score modification arrays.
     *
     * @param levelIndex   The index of the level.
     * @param configReader The ConfigReader instance for accessing configuration settings.
     * @param p            The App instance used to load images.
     */
    public Level(int levelIndex, ConfigReader configReader, App p) {

        balls = new ArrayList<>();
        walls = new ArrayList<>();
        spawners = new ArrayList<>();
        holes = new ArrayList<>();
        accelerationTiles = new ArrayList<>();

        // Load images directly in Level class
        defaultTileImage = p.loadImage("inkball/tile.png");

        // Retrieve score modifiers from the ConfigReader
        double scoreIncreaseModifier = configReader.getScoreIncreaseModifier(levelIndex);
        double scoreDecreaseModifier = configReader.getScoreDecreaseModifier(levelIndex);

        // Populate the score arrays for each color using ConfigReader
        for (int i = 0; i < 5; i++) {
            String color = ColorUtils.numberToColor(i);
            int baseIncreaseValue = configReader.getScoreIncrease(color);
            int baseDecreaseValue = configReader.getScoreDecrease(color);

            // Apply score modifiers to base values
            scoreIncreaseArray[i] = (int) (baseIncreaseValue * scoreIncreaseModifier);
            scoreDecreaseArray[i] = (int) (baseDecreaseValue * scoreDecreaseModifier);
        }
    }

    /**
     * Increases the player's score based on the captured ball's color.
     *
     * @param colorIndex The index of the ball's color.
     * @param app        The main game application to update the score.
     */
    public void increaseScore(int colorIndex, App app) {
        if (colorIndex >= 0 && colorIndex < scoreIncreaseArray.length) {
            int amount = scoreIncreaseArray[colorIndex];
            app.increaseScore(amount);
        }
    }

    /**
     * Decreases the player's score based on the wrong hole capture for a specific ball color.
     *
     * @param colorIndex The index of the ball's color.
     * @param app        The main game application to update the score.
     */
    public void decreaseScore(int colorIndex, App app) {
        if (colorIndex >= 0 && colorIndex < scoreDecreaseArray.length) {
            int amount = scoreDecreaseArray[colorIndex];
            app.decreaseScore(amount);
        }
    }

    public boolean getIsLevelEnded() { return isLevelEnded; }
    public List<Ball> getBalls() {
        return balls;
    }
    public List<Wall> getWalls() {
        return walls;
    }
    public List<Spawner> getSpawners() {
        return spawners;
    }
    public List<Hole> getHoles() {
        return holes;
    }

    /**
     * Adds a new ball to the list of active balls in the current level.
     *
     * @param ball The ball to be added.
     */
    public void addBall(Ball ball) {
        balls.add(ball);
    }

    /**
     * Removes a ball from the list of active balls in the current level.
     *
     * @param ball The ball to be removed.
     */
    public void removeBall(Ball ball) {
        balls.remove(ball);
    }

    /**
     * Loads a level layout from a text file and creates corresponding game entities.
     *
     * @param layoutFile The path to the layout text file.
     * @param p          The App object used to load the file and create entities.
     */
    public void loadLevel(String layoutFile, App p) {
        String[] lines = p.loadStrings(layoutFile);  // Load the layout file as lines

        // Iterate over each line in the file (row by row)
        for (int row = 0; row < lines.length; row++) {
            String line = lines[row];

            // Iterate over each character in the line (column by column)
            for (int col = 0; col < line.length(); col++) {
                char entity = line.charAt(col);

                switch (entity) {
                    case 'X':  // Wall 0
                        walls.add(new Wall(col * App.CELLSIZE, row * App.CELLSIZE + App.TOPBAR, 0, p));
                        break;
                    case '1':  // Wall 1
                        walls.add(new Wall(col * App.CELLSIZE, row * App.CELLSIZE + App.TOPBAR, 1, p));
                        break;
                    case '2':  // Wall 2
                        walls.add(new Wall(col * App.CELLSIZE, row * App.CELLSIZE + App.TOPBAR, 2, p));
                        break;
                    case '3':  // Wall 3
                        walls.add(new Wall(col * App.CELLSIZE, row * App.CELLSIZE + App.TOPBAR, 3, p));
                        break;
                    case '4':  // Wall 4
                        walls.add(new Wall(col * App.CELLSIZE, row * App.CELLSIZE + App.TOPBAR, 4, p));
                        break;
                    case 'S':  // Spawner
                        spawners.add(new Spawner(col * App.CELLSIZE, row * App.CELLSIZE + App.TOPBAR, -1, p));
                        break;
                    case 'H':  // Hole
                        if (col + 1 < line.length()) {
                            int holeColor = Character.getNumericValue(line.charAt(col + 1));
                            if (holeColor >= 0 && holeColor <= 4) {
                                holes.add(new Hole(col * App.CELLSIZE, row * App.CELLSIZE + App.TOPBAR, holeColor, p));
                                col++;  // Move to next character
                            }
                        }
                        break;
                    case 'B':  // Ball
                        if (col + 1 < line.length()) {
                            int ballColor = Character.getNumericValue(line.charAt(col + 1));
                            Ball ball = new Ball(col * App.CELLSIZE, row * App.CELLSIZE + App.TOPBAR, ballColor, 12, p);
                            ball.setIsActive(true);
                            balls.add(ball);
                            col++;
                        }
                        break;
                    case 'A':  // AccelerateTile
                        if (col + 1 < line.length()) {
                            int direction = Character.getNumericValue(line.charAt(col + 1));
                            String accelDirection = "up";
                            switch (direction) {
                                case 0: accelDirection = "up"; break;
                                case 1: accelDirection = "down"; break;
                                case 2: accelDirection = "left"; break;
                                case 3: accelDirection = "right"; break;
                            }
                            accelerationTiles.add(new AccelerateTile(col * App.CELLSIZE, row * App.CELLSIZE + App.TOPBAR, accelDirection, p));
                            col++;
                        }
                        break;
                }
            }
        }
    }

    /**
     * Updates the positions of all balls and checks for collisions with walls and holes.
     *
     * @param p The App object used for the game's main loop.
     */
    public void update(App p) {
        // Iterate through the list of active balls
        for (int i = 0; i < balls.size(); i++) {
            Ball ball = balls.get(i);
            ball.updatePosition();

            // Check for collisions between the ball and walls
            for (Wall wall : walls) {
                wall.checkCollision(ball, p);
            }

            // Check for attraction between the ball and holes
            for (Hole hole : holes) {
                hole.attractBall(ball, this, p);

                // If the ball is no longer active (captured or deactivated)
                if (!ball.getIsActive()) {
                    balls.remove(i);
                    break; // No need to check further holes for this ball
                }
            }

            // Check for collisions with acceleration tiles
            for (AccelerateTile tile : accelerationTiles) {
                tile.checkCollision(ball);  // Accelerates the ball if it's on the tile
            }
        }

        // Check if the level should end (no active balls and no spawners)
        if (balls.isEmpty() && spawners.isEmpty() && !isLevelEnded) {
            endLevel(p, "normal");
        }
    }

    /**
     * Ends the current level and starts the time bonus phase if applicable.
     *
     * @param p      The main game application instance.
     * @param reason The reason for ending the level ("normal" or "timeUp").
     */
    public void endLevel(App p, String reason) {
        if (isLevelEnded) {
            return; // Avoid duplicated call
        }
        isLevelEnded = true;

        switch (reason) {
            case "normal":
                if (p.getRemainingTime() > 0) {
                    timeBonusRemaining = p.getRemainingTime(); // Use remaining time for bonus
                }
                break;
            case "timeUp":
                timeBonusRemaining = 0; // No bonus if time is up
                break;
        }

        // Initialize positions for yellow tiles animation at the end of the level
        yellowTileTopLeft = new YellowTile(0, App.TOPBAR, -1, p);
        yellowTileBottomRight = new YellowTile(App.WIDTH - App.CELLSIZE, App.HEIGHT - App.CELLSIZE, -1, p);
    }

    /**
     * Draws all entities in the current level, including walls, spawners, holes, and balls.
     *
     * @param p The main game application instance used to draw entities.
     */
    public void draw(App p) {
        drawDefaultTiles(p);

        for (Wall wall : walls) {
            wall.draw(p);
        }

        for (Hole hole : holes) {
            hole.draw(p);
        }

        for (Spawner spawner : spawners) {
            spawner.draw(p);
        }

        for (AccelerateTile tile : accelerationTiles) {
            tile.draw(p);
        }

        for (Ball ball : balls) {
            ball.draw(p);
        }

        // Handle the remaining time and yellow tile animation
        if (isLevelEnded && !p.getIsPaused() && !p.isTimerFinished()) {
            updateTimeBonus(p);
            moveYellowTiles(p);
        }

        // Draw yellow tile
        if (isLevelEnded && !p.getIsPaused() && !p.isTimerFinished()) {
            if (yellowTileTopLeft != null) {
                yellowTileTopLeft.draw(p);
            }
            if (yellowTileBottomRight != null) {
                yellowTileBottomRight.draw(p);
            }
        }
    }

    /**
     * Draws the default tiles for all positions (except the top bar).
     *
     * @param p The main game application instance used for drawing.
     */
    void drawDefaultTiles(App p) {
        for (int row = 0; row < (App.HEIGHT - App.TOPBAR) / App.CELLSIZE; row++) {
            for (int col = 0; col < App.BOARD_WIDTH; col++) {
                int x = col * App.CELLSIZE;
                int y = row * App.CELLSIZE + App.TOPBAR;
                p.image(defaultTileImage, x, y, App.CELLSIZE, App.CELLSIZE);
            }
        }
    }

    /**
     * Updates the time bonus and converts remaining time into score points.
     *
     * @param p The main game application instance used to update the score.
     */
    void updateTimeBonus(App p) {
        if (timeBonusRemaining > 0) {
            long currentTime = p.millis();
            if (p.frameCount % 2 == 0) { // Every 2 frames (~0.067 seconds at 30 FPS)
                p.increaseScore(1);
                timeBonusRemaining -= 1;
                p.setRemainingTime(timeBonusRemaining);

                // Ensure timeBonusRemaining does not go negative
                if (timeBonusRemaining < 0) {
                    timeBonusRemaining = 0;
                }
            }
        } else {
            isLevelEnded = false;
            p.nextLevel();
        }
    }

    /**
     * Animates the movement of yellow tiles in a clockwise direction around the game area.
     *
     * @param p The main game application instance used for animation.
     */
    void moveYellowTiles(App p) {
        if (p.frameCount % 2 == 0) { // Every 2 frames (~0.067 seconds at 30 FPS)
            // Move the top-left yellow tile clockwise
            if (yellowTileTopLeft != null) {
                yellowTileTopLeft.update(p);
            }
            // Move the bottom-right yellow tile clockwise
            if (yellowTileBottomRight != null) {
                yellowTileBottomRight.update(p);
            }
        }
    }
}
