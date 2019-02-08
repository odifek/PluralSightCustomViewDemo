package com.sprinthubmobile.example.sprintviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class CompletionStatusView extends View {

    private static final int EDIT_MODE_COMPLETION_COUNT = 7;
    private boolean[] mCompletionStatus;
    private Rect[] mCompletionRectangles;
    private int mOutlineColor;
    private Paint mPaintOutline;
    private int mFillColor;
    private Paint mPaintFill;
    private float mRadius;
    private int mMaxHorizontalItems;

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



        a.recycle();

        mOutlineWidth = 6f;
        mShapeSize = 144f;
        mSpacing = 30f;
        mRadius = (mShapeSize - mOutlineWidth) /2;

        mOutlineColor = Color.BLACK;
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
    private void setupCompletionRectangles() {
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        mCompletionRectangles = new Rect[mCompletionStatus.length];
        for (int completionIndex=0; completionIndex < mCompletionRectangles.length; completionIndex++) {

            int column = completionIndex % mMaxHorizontalItems;
            int row = completionIndex / mMaxHorizontalItems;
            int x = paddingLeft + (int) (column * (mShapeSize + mSpacing));
            int y = paddingTop + (int) (row * (mShapeSize + mSpacing));
            mCompletionRectangles[completionIndex] = new Rect(x, y, x + (int) mShapeSize, y + (int) mShapeSize);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setupCompletionRectangles();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int completionIndex =0; completionIndex < mCompletionRectangles.length; completionIndex++) {
            float x = mCompletionRectangles[completionIndex].centerX();
            float y = mCompletionRectangles[completionIndex].centerY();

            if (mCompletionStatus[completionIndex]) {
                canvas.drawCircle(x, y, mRadius, mPaintFill);
            }
            canvas.drawCircle(x, y, mRadius, mPaintOutline);
        }
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

    public boolean[] getCompletionStatus() {
        return mCompletionStatus;
    }

    public void setCompletionStatus(boolean[] completionStatus) {
        this.mCompletionStatus = completionStatus;
    }
}
