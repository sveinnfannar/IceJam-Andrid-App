package net.paolorovelli.IceJam;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

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

    public static final int DEFAULT_NUM_COLS = 6;
    public static final int DEFAULT_NUM_ROWS = 6;

    private Paint mPaint = new Paint();
    private Shape mMovingShape = null;
    private DrawEventHandler mListener = null;

    private List<Shape> mShapes = new ArrayList<Shape>();
    private GameLogic mGameLogic = new GameLogic(DEFAULT_NUM_COLS, DEFAULT_NUM_ROWS);

    private int mOffsetX, mOffsetY;
    private int mPreviousCol, mPreviousRow;
    private int mBoundsMin, mBoundsMax;
    private int mPixelsPerUnit;

    private Bitmap mIceTexture;


    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.TRANSPARENT);
        mPixelsPerUnit = getWidth() / DEFAULT_NUM_COLS;
        mIceTexture = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ice);
    }

    public void setGridSize(int cols, int rows) {
        mGameLogic.setGridSize(cols, rows);
        mPixelsPerUnit = getWidth() / cols;
        for (Shape shape : mShapes) {
            shape.setPixelsPerUnit(mPixelsPerUnit);
            shape.setBitmap(bitmapForShape(shape));
        }
    }

    public void setCustomEventHandler(DrawEventHandler listener) {
        mListener = listener;
    }

    public void addShape(Shape shape) {
        shape.setPixelsPerUnit(getWidth() / mGameLogic.getNumCols());
        shape.setBitmap(bitmapForShape(shape));
        mGameLogic.addShape(shape);
        mShapes = mGameLogic.getShapes();
    }

    protected void onDraw(Canvas canvas) {
        mPaint.setColor(Color.argb(128, 128, 128, 128));
        mPaint.setAntiAlias(true);
        RectF backgroundRect = new RectF(0, 0, mGameLogic.getNumCols() * mPixelsPerUnit, mGameLogic.getNumRows() * mPixelsPerUnit);
        canvas.drawRoundRect(backgroundRect, 12, 12, mPaint);

        mPaint.setColor(Color.WHITE);
        for( Shape shape : mShapes ) {
            // Draw rect
            Rect rect = shape.getRect();
            Bitmap bitmap = shape.getBitmap();

            RectF borderRect = new RectF(rect);
            borderRect.inset(1, 1);
            canvas.drawRoundRect(borderRect, 12, 12, mPaint);
            if (shape.isGoalShape()) {
                Paint paint = new Paint(Color.RED);
                ColorFilter filter = new LightingColorFilter(Color.RED, 1);
                paint.setColorFilter(filter);
                canvas.drawBitmap(bitmap, rect.left, rect.top, paint);
            }
            else {
                canvas.drawBitmap(bitmap, rect.left, rect.top, mPaint);
            }
        }
    }

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

    private Bitmap bitmapForShape(Shape shape) {
        Random r = new Random();
        Bitmap ice = Bitmap.createBitmap(mIceTexture, r.nextInt(mIceTexture.getWidth() - shape.getWidth()),
                r.nextInt(mIceTexture.getHeight() - shape.getHeight()),
                shape.getWidth(), shape.getHeight());
        return roundedCornerBitmap(ice);
    }

    public static Bitmap roundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(2, 2, bitmap.getWidth() - 3, bitmap.getHeight() - 3);
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
