package net.paolorovelli.IceJam;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * DrawViews.
 *
 * @author Paolo Rovelli and Sveinn Fannar Kristjánsson.
 * @date 03/24/2013
 * @time 9:55AM
 */
public class DrawView extends View {

    public static final int NUM_COLS = 6;
    public static final int NUM_ROWS = 6;

    private Paint mPaint = new Paint();
    private Shape mMovingShape = null;
    private DrawEventHandler mListener = null;

    private List<Shape> mShapes = new ArrayList<Shape>();
    private boolean[][] mGrid = new boolean[NUM_COLS][NUM_ROWS];

    private int mOffsetX;
    private int mOffsetY;


    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.WHITE);

        for (int i = 0; i < NUM_COLS; i++)
            for (int j = 0; j < NUM_COLS; j++)
                mGrid[i][j] = false;
    }


    public void setCustomEventHandler(DrawEventHandler listener) {
        mListener = listener;
    }

    public void addShape(Shape shape) {
        mShapes.add(shape);

        if (shape.getOrientation() == Shape.Orientation.Horizontal) {
            for (int end=shape.getCol()+shape.getLength(), col=shape.getCol(); col<end; ++col) {
                mGrid[col][shape.getRow()] = true;
            }
        }
        else {
            for (int end=shape.getRow()+shape.getLength(), row=shape.getRow(); row<end; ++row) {
                mGrid[shape.getCol()][row] = true;
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        for( Shape shape : mShapes ) {
            mPaint.setColor( shape.getColor() );
            canvas.drawRect( shape.getRect(), mPaint );
        }
    }

    public boolean onTouchEvent( MotionEvent motionEvent ) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();

        switch ( motionEvent.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                mMovingShape = shapeLocatedOn(x, y);
                if (mMovingShape != null) {
                    Rect shapeRect = mMovingShape.getRect();
                    mOffsetX = x - shapeRect.left;
                    mOffsetY = y - shapeRect.top;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if( mMovingShape != null ) {
                    if (mMovingShape.getOrientation() == Shape.Orientation.Horizontal) {
                        /*
                        int row = mMovingShape.getRow();

                        int min = 0;
                        for (int i = 0; i < mMovingShape.getCol(); i++)
                            if (mGrid[row][i])
                                min = i + 1;

                        int max = NUM_COLS;
                        for (int i = NUM_COLS; i > mMovingShape.getCol() + mMovingShape.getLength(); i--)
                            if (mGrid[row][i])
                                max = i;

                        x = max()
                        */
                        mMovingShape.moveTo(x - mOffsetX);
                    }
                    else {
                        y = Math.max( 0, Math.min( y, getHeight() - mMovingShape.height()));
                        mMovingShape.moveTo(y - mOffsetY);
                    }

                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                if( mMovingShape != null ) {
                    mMovingShape.snapToGrid();
                    invalidate();

                    mMovingShape = null;

                    if( mListener != null ) {
                        mListener.onShapeMoved();
                    }
                }
                break;
        }

        return true;
    }

    private Shape shapeLocatedOn(int x, int y) {
        for ( Shape shape : mShapes ) {
            if ( shape.getRect().contains( x, y ) ) {
                return shape;
            }
        }

        return null;
    }
}
