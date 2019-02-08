package com.sprinthubmobile.example.sprintviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class CompletionStatusView extends View {

    private static final int EDIT_MODE_COMPLETION_COUNT = 7;
    private static final int INVALID_INDEX = -1;
    private static final int SHAPE_CIRCLE = 0;
    private static final float DEFAULT_OUTLINE_WIDTH_DP = 2f;
    public static final float DEFAULT_SHAPE_SIZE_DP = 48f;
    public static final float DEFAULT_SPACING_DP = 8f;
    private boolean[] mCompletionStatus;
    private Rect[] mCompletionRectangles;
    private int mOutlineColor;
    private Paint mPaintOutline;
    private int mFillColor;
    private Paint mPaintFill;
    private float mRadius;
    private int mMaxHorizontalItems;
    private int mShape;

    public CompletionStatusView(Context context) {
        super(context);
        init(null, 0);
    }

    public CompletionStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CompletionStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private float mOutlineWidth;
    private float mShapeSize;
    private float mSpacing;
    private void init(AttributeSet attrs, int defStyle) {

        if (isInEditMode()) {
            setupEditModeValues();
        }

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CompletionStatusView, defStyle, 0);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        float displayDensity = dm.density;
        float defaultOutlineWidthPixels = displayDensity * DEFAULT_OUTLINE_WIDTH_DP;

        float defaultShapeSizePixels = displayDensity * DEFAULT_SHAPE_SIZE_DP;
        float defaultSpacingPixels = displayDensity * DEFAULT_SPACING_DP;

        mOutlineColor = a.getColor(R.styleable.CompletionStatusView_outlineColor, Color.BLACK);
        mShape = a.getInt(R.styleable.CompletionStatusView_shape, SHAPE_CIRCLE);
        mOutlineWidth = a.getDimension(R.styleable.CompletionStatusView_outlineWidth, defaultOutlineWidthPixels);

        a.recycle();

        mShapeSize = defaultShapeSizePixels;
        mSpacing = defaultSpacingPixels;
        mRadius = (mShapeSize - mOutlineWidth) /2;
        mPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintOutline.setStyle(Paint.Style.STROKE);
        mPaintOutline.setStrokeWidth(mOutlineWidth);
        mPaintOutline.setColor(mOutlineColor);

        mFillColor = getContext().getResources().getColor(R.color.sprint_orange);
        mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(mFillColor);


    }

    /**
     * Displays half completed items while in the layout editor or designer
     */
    private void setupEditModeValues() {
        boolean[] exampleModuleValues = new boolean[EDIT_MODE_COMPLETION_COUNT];
        int middle = EDIT_MODE_COMPLETION_COUNT / 2;
        for (int i = 0; i < middle; i++) {
            exampleModuleValues[i] = true;
        }

        setCompletionStatus(exampleModuleValues);
    }


    /**
     * Items are drawn inside rectangles
     */
    private void setupCompletionRectangles(int width) {
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        int availableWidth = width - paddingLeft - paddingRight;
        int horizontalItemsThatCanFit = (int)(availableWidth / (mShapeSize + mSpacing));
        int maxHorizontalItems = Math.min(horizontalItemsThatCanFit, mCompletionStatus.length);

        mCompletionRectangles = new Rect[mCompletionStatus.length];
        for (int completionIndex=0; completionIndex < mCompletionRectangles.length; completionIndex++) {

            int column = completionIndex % maxHorizontalItems;
            int row = completionIndex / maxHorizontalItems;
            int x = paddingLeft + (int) (column * (mShapeSize + mSpacing));
            int y = paddingTop + (int) (row * (mShapeSize + mSpacing));
            mCompletionRectangles[completionIndex] = new Rect(x, y, x + (int) mShapeSize, y + (int) mShapeSize);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setupCompletionRectangles(w);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int completionIndex =0; completionIndex < mCompletionRectangles.length; completionIndex++) {
            if (mShape == SHAPE_CIRCLE) {
                float x = mCompletionRectangles[completionIndex].centerX();
                float y = mCompletionRectangles[completionIndex].centerY();

                if (mCompletionStatus[completionIndex]) {
                    canvas.drawCircle(x, y, mRadius, mPaintFill);
                }
                canvas.drawCircle(x, y, mRadius, mPaintOutline);
            } else {
                drawSquare(canvas, completionIndex);
            }
        }
    }

    private void drawSquare(Canvas canvas, int itemIndex) {
        Rect itemRectangle = mCompletionRectangles[itemIndex];

        if (mCompletionStatus[itemIndex]) {
            canvas.drawRect(itemRectangle, mPaintFill);
        }

        canvas.drawRect(itemRectangle.left + (mOutlineWidth/2),
                itemRectangle.top + (mOutlineWidth/2),
                itemRectangle.right - (mOutlineWidth/2),
                itemRectangle.bottom - (mOutlineWidth/2),
                mPaintOutline);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth;
        int desiredHeight;

        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = specWidth - getPaddingLeft() - getPaddingRight();
        int horizontalItemsThatCanFit = (int) (availableWidth / (mShapeSize + mSpacing));
        mMaxHorizontalItems = Math.min(horizontalItemsThatCanFit, mCompletionStatus.length);

        desiredWidth = (int) ((mMaxHorizontalItems * (mShapeSize + mSpacing)) - mSpacing);
        desiredWidth += getPaddingLeft() + getPaddingRight();

        int rows = ((mCompletionStatus.length - 1) / mMaxHorizontalItems) + 1;
        desiredHeight = (int) ((rows * (mShapeSize + mSpacing)) - mSpacing);
        desiredHeight += getPaddingTop() + getPaddingBottom();

        int width = resolveSizeAndState(desiredWidth, widthMeasureSpec, 0);
        int height = resolveSizeAndState(desiredHeight, heightMeasureSpec, 0);

        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                int itemIdex = findItemAtPoint(event.getX(), event.getY());
                onItemSelected(itemIdex);
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void onItemSelected(int itemIndex) {
        if (itemIndex == INVALID_INDEX) return;

        mCompletionStatus[itemIndex] = !mCompletionStatus[itemIndex];
        invalidate();
    }

    private int findItemAtPoint(float x, float y) {
        int touchedIdex = INVALID_INDEX;
        for (int i = 0; i < mCompletionRectangles.length; i++) {
            if (mCompletionRectangles[i].contains((int)x, (int)y)) {
                touchedIdex = i;
                break;
            }
        }
        return touchedIdex;
    }

    public boolean[] getCompletionStatus() {
        return mCompletionStatus;
    }

    public void setCompletionStatus(boolean[] completionStatus) {
        this.mCompletionStatus = completionStatus;
    }
}
