package net.paolorovelli.IceJam;

import android.graphics.Color;
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
        return height() / PIXELS_PER_UNIT;
    }

    public int getRow() {
        return height() / PIXELS_PER_UNIT;
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