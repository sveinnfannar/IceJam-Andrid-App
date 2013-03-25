/**
 * This class provides the logic for the TrafficJam puzzle.
 *
 */
package net.paolorovelli.IceJam;

import android.util.Log;

import java.util.*;
import java.util.regex.MatchResult;

public class GameLogic {
    public static final int NUM_COLS = 6;
    public static final int NUM_ROWS = 6;

    public static final int GOAL_COL = 5;
    public static final int GOAL_ROW = 3;
    public static final int GOAL_SHAPE_ID = 0;  // first can assumed to be the goal car.

    private boolean[][] mGrid = new boolean[NUM_COLS][NUM_ROWS];
    private List<Shape> mShapes;
    private boolean mIsSolved;

    /**
     *  Constructor
     */
    public GameLogic() {
        mShapes = new ArrayList<Shape>();
        mIsSolved = false;

        // Setup grid
        for (int i = 0; i < NUM_COLS; i++)
            for (int j = 0; j < NUM_COLS; j++)
                mGrid[i][j] = false;
    }

    public int leftMovementBounds(Shape shape) {
        int row = shape.getRow();
        int col = shape.getCol();
        for (; col > 0; col--)
            if (mGrid[row][col - 1])
                break;
        return col;
    }

    public int rightMovementBounds(Shape shape) {
        int row = shape.getRow();
        int col = shape.getCol();
        for (; col + shape.getLength() < NUM_COLS; col++)
            if (mGrid[row][col + shape.getLength()])
                break;
        return col;
    }

    public int topMovementBounds(Shape shape) {
        int row = shape.getRow();
        int col = shape.getCol();
        for (; row > 0; row--)
            if (mGrid[row - 1][col])
                break;
        return row;
    }

    public int bottomMovementBounds(Shape shape) {
        int row = shape.getRow();
        int col = shape.getCol();
        for (; row + shape.getLength() < NUM_ROWS; row++)
            if (mGrid[row + shape.getLength()][col])
                break;
        return row;
    }

    public void addToGrid(Shape shape) {
        if (shape.getOrientation() == Shape.Orientation.Horizontal) {
            for (int col = shape.getCol(); col < shape.getCol() + shape.getLength(); col++)
                mGrid[shape.getRow()][col] = true;
        }
        else {
            for (int row = shape.getRow(); row < shape.getRow() + shape.getLength(); row++)
                mGrid[row][shape.getCol()] = true;
        }
    }

    public void rebuildGrid() {
        // Clear grid
        for (int i = 0; i < NUM_COLS; i++)
            for (int j = 0; j < NUM_COLS; j++)
                mGrid[i][j] = false;

        // Add shapes
        for (Shape shape : mShapes) {
            addToGrid(shape);
        }

        Log.d("GameLogic", this.toString());
    }

    /**
     * Check whether the puzzle has been solved.
     *
     * @return True if puzzle is solved, false otherwise.
     */
    public boolean isSolved( ) {
        return mIsSolved;
    }

    /**
     * Adds a shape to the game logic
     *
     * @param shape
     */
    public void addShape(Shape shape) {
        mShapes.add(shape);
        addToGrid(shape);
    }


    /**
     * Get information about the puzzle cars.
     *
     * @return List of cars.
     */
    public List<Shape> getShapes() {
        return Collections.unmodifiableList(mShapes);
    }


    /**
     * Returns a string representation of the puzzle state.
     *
     * @return  A string, representing the state.
     */
    public String toString()  {
        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                if (mGrid[row][col]) {
                    sb.append('x');
                }
                else {
                    sb.append('_');
                }
            }
            sb.append('\n');
        }
        sb.append("Solved: ");
        sb.append(isSolved());
        sb.append('\n');
        return sb.toString();
    }
}