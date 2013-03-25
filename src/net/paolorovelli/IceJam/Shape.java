package net.paolorovelli.IceJam;

import android.graphics.Color;
import android.graphics.PixelXorXfermode;
import android.graphics.Rect;

import java.util.Scanner;
import java.util.regex.MatchResult;

/**
 * IceJam
 * User: Sveinn Fannar Kristjánsson
 * Date: 3/24/13
 * Time: 6:21 AM
 */
public class Shape {

    public static enum Orientation { Horizontal, Vertical }

    private final int PIXELS_PER_UNIT = 50;
    private Orientation mOrientation;
    private Rect mRect;
    private int mLength;
    private int mColor; // TODO: Add a texture instead of color

    public Shape(Orientation orientation, int col, int row, int length) {
        this(orientation, col, row, length, false);
    }

    public Shape(Orientation orientation, int col, int row, int length, boolean goal) {
        if (orientation == Orientation.Horizontal)
            mRect = new Rect(col * PIXELS_PER_UNIT, row * PIXELS_PER_UNIT, (col + length) * PIXELS_PER_UNIT, (row + 1) * PIXELS_PER_UNIT);
        else
            mRect = new Rect(col * PIXELS_PER_UNIT, row * PIXELS_PER_UNIT, (col + 1) * PIXELS_PER_UNIT, (row + length) * PIXELS_PER_UNIT);

        mOrientation = orientation;
        mLength = length;

        mColor = Color.BLUE;
    }

    public void moveTo(int pos) {
        if (mOrientation == Orientation.Horizontal)
            mRect.offsetTo(pos, mRect.top);
        else
            mRect.offsetTo(mRect.left, pos);
    }

    public void snapToGrid() {
        int newValue;

        if (mOrientation == Orientation.Horizontal)
            newValue = ((mRect.left + PIXELS_PER_UNIT / 2) / PIXELS_PER_UNIT) * PIXELS_PER_UNIT;
        else
            newValue = ((mRect.top + PIXELS_PER_UNIT / 2) / PIXELS_PER_UNIT) * PIXELS_PER_UNIT;

        moveTo(newValue);
    }

    private static boolean intersect( int x1, int dx1, int x2, int dx2 ) {
        return ( (x1 <= x2) && (x2 < x1 + dx1) ) || ( (x2 <= x1) && (x1 < x2 + dx2) );
    }

    private boolean doOverlap(Shape otherShape) {
        if (mOrientation  == Orientation.Horizontal) {
            if (otherShape.getOrientation() == Orientation.Horizontal) {
                return (getRow() == otherShape.getRow()) &&
                        intersect(getCol(), getLength(), otherShape.getCol(), otherShape.getLength() );
            }
            else {
                return intersect(getCol(), getLength(), otherShape.getCol(), 1 ) &&
                        intersect(getRow(), 1, otherShape.getRow(), otherShape.getLength());
            }
        }
        else {
            if (otherShape.getOrientation() == Orientation.Vertical) {
                return (getCol() == otherShape.getCol()) &&
                        intersect(getRow(), getLength(), otherShape.getRow(), otherShape.getLength());
            }
            else {
                return intersect(getRow(), getLength(), otherShape.getRow(), 1) &&
                        intersect(getCol(), 1, otherShape.getCol(), otherShape.getLength());
            }
        }
    }

    public Rect getRect() {
        return mRect;
    }

    public int getColor() {
        return mColor;
    }

    public Orientation getOrientation() {
        return mOrientation;
    }

    public int getLength() {
        return mLength;
    }

    public int getCol() {
        return mRect.left / PIXELS_PER_UNIT;
    }

    public int getRow() {
        return mRect.top / PIXELS_PER_UNIT;
    }

    public int getWidth() {
        return mRect.width();
    }

    public int getHeight() {
        return mRect.height();
    }

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