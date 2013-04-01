/**
 * This class provides the logic for the TrafficJam puzzle.
 *
 */
package net.paolorovelli.IceJam;

import android.util.Log;

import java.util.*;

/**
 * Play activity of the game.
 *
 * @author Yngvi.
 * @date 03/24/2013
 * @time 9:55AM
 */
public class GameLogic {
    public static final int GOAL_SHAPE_ID = 0;  // first can assumed to be the goal car.
    
    private int mNumCols;
    private int mNumRows;

    private boolean[][] mGrid;
    private List<Shape> mShapes = new ArrayList<Shape>();


    /**
     *  Class constructor.
     */
    public GameLogic(int numCols, int numRows) {
        setGridSize(numCols, numRows);
    }


    /**
     * Set the size of the grid.
     *
     * @param numCols
     * @param numRows
     */
    public void setGridSize(int numCols, int numRows) {
        mNumCols = numCols;
        mNumRows = numRows;

        // Setup grid
        mGrid = new boolean[mNumCols][mNumRows];
        for (int i = 0; i < mNumCols; i++)
            for (int j = 0; j < mNumCols; j++)
                mGrid[i][j] = false;

        // Place the cars on the grid if there are any
        rebuildGrid();
    }


    /**
     * Returns the number of columns a give shape can move to the left.
     *
     * @param shape
     * @return
     */
    public int leftMovementBounds(Shape shape) {
        int row = shape.getRow();
        int col = shape.getCol();
        for (; col > 0; col--)
            if (mGrid[row][col - 1])
                break;
        return col;
    }


    /**
     * Returns the number of columns a give shape can move to the right.
     *
     * @param shape
     * @return
     */
    public int rightMovementBounds(Shape shape) {
        int row = shape.getRow();
        int col = shape.getCol();
        for (; col + shape.getLength() < mNumCols; col++)
            if (mGrid[row][col + shape.getLength()])
                break;
        return col;
    }


    /**
     * Returns the number of columns a give shape can move upwards.
     *
     * @param shape
     * @return
     */
    public int topMovementBounds(Shape shape) {
        int row = shape.getRow();
        int col = shape.getCol();
        for (; row > 0; row--)
            if (mGrid[row - 1][col])
                break;
        return row;
    }


    /**
     * Returns the number of columns a give shape can move downwards.
     *
     * @param shape
     * @return
     */
    public int bottomMovementBounds(Shape shape) {
        int row = shape.getRow();
        int col = shape.getCol();
        for (; row + shape.getLength() < mNumRows; row++)
            if (mGrid[row + shape.getLength()][col])
                break;
        return row;
    }


    /**
     * Add a shape to the boolean grid used by the game logic calculations
     *
     * @param shape
     */
    private void addToGrid(Shape shape) {
        if (shape.getOrientation() == Shape.Orientation.Horizontal) {
            for (int col = shape.getCol(); col < shape.getCol() + shape.getLength(); col++)
                mGrid[shape.getRow()][col] = true;
        }
        else {
            for (int row = shape.getRow(); row < shape.getRow() + shape.getLength(); row++)
                mGrid[row][shape.getCol()] = true;
        }
    }


    /**
     * Rebuild the boolean grid for the game logic.
     * Should be called after a shape moves.
     */
    public void rebuildGrid() {
        // Clear grid
        for (int i = 0; i < mNumCols; i++)
            for (int j = 0; j < mNumCols; j++)
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
        if (!mShapes.isEmpty()) {
            Shape shape = mShapes.get(GOAL_SHAPE_ID);
            if ((shape.getCol() + shape.getLength()) == mNumCols)
                return true;
        }
        return false;
    }


    /**
     * Adds a shape to the game logic
     *
     * @param shape
     */
    public void addShape(Shape shape) {
        if (mShapes.isEmpty())
            shape.setIsGoalShape(true);
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
     * Get the number of columns in the grid
     *
     * @return Number of columns
     */
    public int getNumCols() {
        return mNumCols;
    }


    /**
     * Get the number of rows in the grid
     *
     * @return Number of rows
     */
    public int getNumRows() {
        return mNumRows;
    }


    /**
     * Returns a string representation of the puzzle state.
     *
     * @return  A string, representing the state.
     */
    public String toString()  {
        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        for (int row = 0; row < mNumRows; row++) {
            for (int col = 0; col < mNumCols; col++) {
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