package net.paolorovelli.IceJam;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * DrawViews.
 *
 * @author Paolo Rovelli and Sveinn Fannar Kristjánsson.
 * @date 03/24/2013
 * @time 9:55AM
 */
public class DrawView extends View {

    public static final int DEFAULT_NUM_COLS = 6;
    public static final int DEFAULT_NUM_ROWS = 6;

    private Paint mPaint = new Paint();
    private Shape mMovingShape = null;
    private DrawEventHandler mListener = null;

    private List<Shape> mShapes = new ArrayList<Shape>();
    private GameLogic mGameLogic = new GameLogic(DEFAULT_NUM_COLS, DEFAULT_NUM_ROWS);

    private int mOffsetX, mOffsetY;
    private int mPreviousCol, mPreviousRow;
    private int mPixelsPerUnit;


    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.TRANSPARENT);

        mPixelsPerUnit = getWidth() / DEFAULT_NUM_COLS;
    }

    public void setGridSize(int cols, int rows) {
        mGameLogic.setGridSize(cols, rows);
        mPixelsPerUnit = getWidth() / cols;
        for (Shape shape : mShapes) {
            shape.setPixelsPerUnit(mPixelsPerUnit);
        }
    }

    public void setCustomEventHandler(DrawEventHandler listener) {
        mListener = listener;
    }

    public void addShape(Shape shape) {
        shape.setPixelsPerUnit(getWidth() / mGameLogic.getNumCols());
        mGameLogic.addShape(shape);
        mShapes = mGameLogic.getShapes();
    }

    protected void onDraw(Canvas canvas) {
        for( Shape shape : mShapes ) {
            // Draw rect
            mPaint.setColor(shape.getColor());
            mPaint.setStrokeWidth(0);
            canvas.drawRect(shape.getRect(), mPaint);
        }
    }

    private int mBoundsMin;
    private int mBoundsMax;

    public boolean onTouchEvent( MotionEvent motionEvent ) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();

        switch (motionEvent.getAction() ) {
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

                    mPreviousCol = mMovingShape.getCol();
                    mPreviousRow = mMovingShape.getRow();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if( mMovingShape != null ) {
                    x -= mOffsetX;
                    y -= mOffsetY;

                    if (mMovingShape.getOrientation() == Shape.Orientation.Horizontal) {
                        x = Math.max(mBoundsMin * mPixelsPerUnit, Math.min(x, mBoundsMax * mPixelsPerUnit));
                        mMovingShape.moveTo(x);
                    }
                    else {
                        y = Math.max(mBoundsMin * mPixelsPerUnit, Math.min(y, mBoundsMax * mPixelsPerUnit));
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

                    // Rebuild the grid after the change
                    mGameLogic.rebuildGrid();

                    // Notify the the listeners
                    if(mListener != null) {
                        if (mMovingShape.getCol() != mPreviousCol || mMovingShape.getRow() != mPreviousRow)
                            mListener.onShapeMoved();

                        if (mGameLogic.isSolved())
                            mListener.onPuzzleSolved();
                    }

                    // Throw away the current moving shape
                    mMovingShape = null;
                }
                break;
        }

        return true;
    }

    private Shape shapeLocatedOn(int x, int y) {
        for (Shape shape : mShapes)
            if (shape.getRect().contains(x, y))
                return shape;

        return null;
    }
}
