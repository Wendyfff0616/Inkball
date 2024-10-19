package inkball;

/**
 * The Movable interface should be implemented by any class whose instances
 * are intended to move or change position in the Inkball game.
 * It provides a single method `updatePosition` that must be implemented
 * by any class that handles movement logic.
 */
public interface Movable {

    /**
     * Updates the position of the object.
     * This method should contain the logic that changes the object's coordinates
     * based on its velocity or other movement-related properties.
     */
    void updatePosition();
}
