package net.paolorovelli.IceJam;

import android.graphics.Color;
import android.graphics.Rect;

import java.util.Random;
import java.util.Scanner;
import java.util.regex.MatchResult;

/**
 * Shape class.
 *
 * @author Paolo Rovelli and Sveinn Fannar Kristjánsson.
 * @date 3/24/13
 * @time 6:21 AM
 */
public class Shape {
    public static enum Orientation { Horizontal, Vertical }

    private int mPixelsPerUnit = 50;
    private Orientation mOrientation;
    private boolean mIsGoalShape;
    private Rect mRect;
    private int mCol, mRow;
    private int mLength;
    private int mColor; // TODO: Add a texture instead of color

    /**
     * Class constructor.
     *
     * @param orientation
     * @param col
     * @param row
     * @param length
     */
    public Shape(Orientation orientation, int col, int row, int length) {
        mOrientation = orientation;
        mLength = length;
        mCol = col;
        mRow = row;

        createRect();
        setIsGoalShape(false);
    }


    private void createRect() {
        if (mOrientation == Orientation.Horizontal)
            mRect = new Rect(mCol * mPixelsPerUnit, mRow * mPixelsPerUnit, (mCol + mLength) * mPixelsPerUnit, (mRow + 1) * mPixelsPerUnit);
        else
            mRect = new Rect(mCol * mPixelsPerUnit, mRow * mPixelsPerUnit, (mCol + 1) * mPixelsPerUnit, (mRow + mLength) * mPixelsPerUnit);
    }

    /**
     * Move to.
     *
     * @param pos
     */
    public void moveTo(int pos) {
        if (mOrientation == Orientation.Horizontal)
            mRect.offsetTo(pos, mRect.top);
        else
            mRect.offsetTo(mRect.left, pos);
    }


    /**
     * Snap to grid.
     */
    public void snapToGrid() {
        int newValue;

        if (mOrientation == Orientation.Horizontal) {
            newValue = ((mRect.left + mPixelsPerUnit / 2) / mPixelsPerUnit) * mPixelsPerUnit;
            mCol = newValue / mPixelsPerUnit;
        }
        else {
            newValue = ((mRect.top + mPixelsPerUnit / 2) / mPixelsPerUnit) * mPixelsPerUnit;
            mRow = newValue / mPixelsPerUnit;
        }


        moveTo(newValue);
    }


    /**
     * Intersect.
     *
     * @param x1
     * @param dx1
     * @param x2
     * @param dx2
     * @return
     */
    private static boolean intersect( int x1, int dx1, int x2, int dx2 ) {
        return ( (x1 <= x2) && (x2 < x1 + dx1) ) || ( (x2 <= x1) && (x1 < x2 + dx2) );
    }


    /**
     * Get rect.
     *
     * @return
     */
    public Rect getRect() {
        return mRect;
    }


    /**
     * Get color.
     *
     * @return
     */
    public int getColor() {
        return mColor;
    }


    /**
     * Get orientation.
     *
     * @return
     */
    public Orientation getOrientation() {
        return mOrientation;
    }


    /**
     * Get lenght.
     *
     * @return
     */
    public int getLength() {
        return mLength;
    }


    /**
     * Get color.
     *
     * @return
     */
    public int getCol() {
        return mCol;
    }


    /**
     * Get row.
     *
     * @return
     */
    public int getRow() {
        return mRow;
    }


    /**
     * Get width.
     *
     * @return
     */
    public int getWidth() {
        return mRect.width();
    }


    /**
     * Get height.
     *
     * @return
     */
    public int getHeight() {
        return mRect.height();
    }

    /**
     * Set pixels per unit (row, col)
     *
     * @param pixelsPerUnit
     */
    public void setPixelsPerUnit(int pixelsPerUnit) {
        mPixelsPerUnit = pixelsPerUnit;
        createRect();
    }


    public void setIsGoalShape(boolean isGoalShape) {
        mIsGoalShape = isGoalShape;

        if (isGoalShape)
            mColor = Color.rgb(172, 209, 233);
        else
            mColor = Color.rgb(123, 74, 18);
    }


    public boolean isGoalShape() {
        return mIsGoalShape;
    }


    /**
     * To string.
     *
     * @return
     */
    public String toString( ) {
        StringBuilder sb = new StringBuilder();
        sb.append( '(' );
        sb.append( getOrientation() == Orientation.Horizontal ? 'H' : 'V' );
        sb.append( ' ' );
        sb.append( getCol() );
        sb.append( ' ' );
        sb.append( getRow() );
        sb.append( ' ' );
        sb.append( getLength() );
        sb.append( ')' );
        return sb.toString();
    }


    /**
     * Create a new shape from a string representation
     *
     *   (orientation col row length)
     *
     *  e.g.  (H 1 2 2) or (V 2 3 3)
     *
     * @param shapeStr A string representing a car.
     * @return A shape object if successful, null otherwise.
     */
    public static Shape shapeFromString( String shapeStr ) {
        Shape shapeReturn = null;
        Scanner s = new Scanner(shapeStr);
        s.findInLine("\\s*\\(\\s*(\\w+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\)\\s*");
        try {
            MatchResult result = s.match();
            if ( result.groupCount() == 4 ) {
                boolean isSuccessful = true;
                Orientation orientation = null;
                if ( result.group(1).equals("H") ) {
                    orientation = Orientation.Horizontal;
                }
                else if ( result.group(1).equals( "V" ) ) {
                    orientation = Orientation.Vertical;
                }
                else { isSuccessful = false; }
                if ( isSuccessful ) {
                    int col = Integer.parseInt( result.group( 2 ) );
                    int row = Integer.parseInt( result.group( 3 ) );
                    int length = Integer.parseInt( result.group( 4 ) );
                    shapeReturn = new Shape( orientation, col, row, length);
                }
            }
        }
        catch ( IllegalStateException e ) {
            // Match not found.
        }
        s.close();
        return shapeReturn;
    }
}