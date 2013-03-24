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

    private Paint mPaint = new Paint();
    private Shape mMovingShape = null;
    private DrawEventHandler mListener = null;

    private List<Shape> mShapes = new ArrayList<Shape>();



    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.WHITE);
    }


    public void setCustomEventHandler(DrawEventHandler listener) {
        mListener = listener;
    }

    public void addShape(Shape shape) {
        mShapes.add(shape);
    }

    protected void onDraw(Canvas canvas) {
        for( Shape shape : mShapes ) {
            mPaint.setColor( shape.getColor() );
            canvas.drawRect( shape.getRect(), mPaint );
        }
    }

    int mOffsetX;
    int mOffsetY;
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
                        x = Math.max( 0, Math.min( x, getWidth() - mMovingShape.width()));  //TODO: instead of SHAPE_SIZE here I should have the max lenght I can reach (see getAction()...)
                        mMovingShape.moveTo(x - mOffsetX);
                    }
                    else {
                        y = Math.max( 0, Math.min( y, getHeight() - mMovingShape.height()));  //TODO: instead of SHAPE_SIZE here I should have the max lenght I can reach (see getAction()...)
                        mMovingShape.moveTo(y - mOffsetY);
                    }

                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                if( mMovingShape != null ) {
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
