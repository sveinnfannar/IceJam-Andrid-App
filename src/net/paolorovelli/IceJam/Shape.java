package net.paolorovelli.IceJam;

import android.graphics.Color;
import android.graphics.PixelXorXfermode;
import android.graphics.Rect;

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
        return (width() + (PIXELS_PER_UNIT / 2)) / PIXELS_PER_UNIT;
    }

    public int getRow() {
        return (height() + (PIXELS_PER_UNIT / 2)) / PIXELS_PER_UNIT;
    }

    public int width() {
        return mRect.width();
    }

    public int height() {
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
}