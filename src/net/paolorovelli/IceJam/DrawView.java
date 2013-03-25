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
    private GameLogic mGameLogic;

    private int mOffsetX;
    private int mOffsetY;


    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.WHITE);
    }

    public void setGameLogic(GameLogic gameLogic) {
        mGameLogic = gameLogic;
    }

    public void setCustomEventHandler(DrawEventHandler listener) {
        mListener = listener;
    }

    public void addShape(Shape shape) {
        mGameLogic.addShape(shape);
        mShapes = mGameLogic.getShapes();
    }

    protected void onDraw(Canvas canvas) {
        mPaint.setColor(Color.LTGRAY);
        canvas.drawRect(0f, 0f, NUM_COLS*50f, NUM_ROWS*50f, mPaint);

        for( Shape shape : mShapes ) {
            mPaint.setColor( shape.getColor() );
            canvas.drawRect( shape.getRect(), mPaint );
        }
    }

    private int mBoundsMin;
    private int mBoundsMax;

    public boolean onTouchEvent( MotionEvent motionEvent ) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();

        switch ( motionEvent.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                mMovingShape = shapeLocatedOn(x, y);
                if (mMovingShape != null) {

                    // Calculate offset
                    Rect shapeRect = mMovingShape.getRect();
                    mOffsetX = x - shapeRect.left;
                    mOffsetY = y - shapeRect.top;

                    // Get the movement bounds
                    if (mMovingShape.getOrientation() == Shape.Orientation.Horizontal) {
                        mBoundsMin = mGameLogic.leftMovementBounds(mMovingShape);
                        mBoundsMax = mGameLogic.rightMovementBounds(mMovingShape);
                    }
                    else {
                        mBoundsMin = mGameLogic.topMovementBounds(mMovingShape);
                        mBoundsMax = mGameLogic.bottomMovementBounds(mMovingShape);
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if( mMovingShape != null ) {
                    x -= mOffsetX;
                    y -= mOffsetY;

                    if (mMovingShape.getOrientation() == Shape.Orientation.Horizontal) {
                        x = Math.max(mBoundsMin * 50, Math.min(x, mBoundsMax * 50));
                        mMovingShape.moveTo(x);
                    }
                    else {
                        y = Math.max(mBoundsMin * 50, Math.min(y, mBoundsMax * 50));
                        mMovingShape.moveTo(y);
                    }

                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                if( mMovingShape != null ) {

                    // Snap position to grid
                    mMovingShape.snapToGrid();
                    invalidate();

                    // Add shape to grid again
                    mGameLogic.rebuildGrid();

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
