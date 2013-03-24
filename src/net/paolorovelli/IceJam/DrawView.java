package net.paolorovelli.IceJam;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
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
    private class MyShape {
        private int mColor;
        private Rect mRect;

        MyShape(int color, Rect rect) {
            mColor = color;
            mRect = rect;
        }

        public int getColor() {
            return mColor;
        }

        public Rect getRect() {
            return mRect;
        }
    }

    private final int SHAPE_SIZE = 100;
    private Paint mPaint = new Paint();
    private MyShape mMovingShape = null;
    private DrawEventHandler mListener = null;

    private List<MyShape> mShapes = new ArrayList<MyShape>();
    private Random mRandom = new Random();



    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.WHITE);
    }


    public void setCustomEventHandler(DrawEventHandler listener) {
        mListener = listener;
    }


    public void addShape( int color ) {
        Rect rect = new Rect();

        int x = mRandom.nextInt( getWidth() - SHAPE_SIZE );
        int y = mRandom.nextInt( getHeight() - SHAPE_SIZE );
        rect.set(x, y, x + SHAPE_SIZE, y + SHAPE_SIZE);
        mShapes.add( new MyShape(color, rect) );

    }


    protected void onDraw(Canvas canvas) {
        for( MyShape shape : mShapes ) {
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
                break;

            case MotionEvent.ACTION_MOVE:
                    if( mMovingShape != null ) {
                        x = Math.max( 0, Math.min( x, getWidth() - SHAPE_SIZE) );  //TODO: instead of SHAPE_SIZE here I should have the max lenght I can reach (see getAction()...)
                        y = Math.max( 0, Math.min( y, getHeight() - SHAPE_SIZE) );  //TODO: instead of SHAPE_SIZE here I should have the max lenght I can reach (see getAction()...)

                        //x = mMovingShape.getRect().left;  // vertical movement
                        //y = mMovingShape.getRect().top;  // horizontal movement

                        //Adjust the offset (otherwise it will always move to the left/top side):
                        //TODO: ...

                        mMovingShape.getRect().offsetTo(x, y);
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

    private MyShape shapeLocatedOn(int x, int y) {
        for ( MyShape shape : mShapes ) {
            if ( shape.getRect().contains( x, y ) ) {
                return shape;
            }
        }

        return null;
    }
}
