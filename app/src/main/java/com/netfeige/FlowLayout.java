package com.netfeige;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.InputDeviceCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes.dex */
public class FlowLayout extends ViewGroup {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private boolean debugDraw;
    private int horizontalSpacing;
    private int orientation;
    private int verticalSpacing;

    public FlowLayout(Context context) {
        super(context);
        this.horizontalSpacing = 0;
        this.verticalSpacing = 0;
        this.orientation = 0;
        this.debugDraw = false;
        readStyleParameters(context, null);
    }

    public FlowLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.horizontalSpacing = 0;
        this.verticalSpacing = 0;
        this.orientation = 0;
        this.debugDraw = false;
        readStyleParameters(context, attributeSet);
    }

    public FlowLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.horizontalSpacing = 0;
        this.verticalSpacing = 0;
        this.orientation = 0;
        this.debugDraw = false;
        readStyleParameters(context, attributeSet);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int paddingLeft;
        int paddingTop;
        int size = (View.MeasureSpec.getSize(i) - getPaddingRight()) - getPaddingLeft();
        int size2 = (View.MeasureSpec.getSize(i2) - getPaddingRight()) - getPaddingLeft();
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        if (this.orientation == 0) {
            i3 = size;
            i4 = mode;
        } else {
            i3 = size2;
            i4 = mode2;
        }
        int childCount = getChildCount();
        int i8 = 0;
        int i9 = 0;
        int iMax = 0;
        int i10 = 0;
        int iMax2 = 0;
        int iMax3 = 0;
        int i11 = 0;
        while (i8 < childCount) {
            View childAt = getChildAt(i8);
            int i12 = childCount;
            if (childAt.getVisibility() == 8) {
                i5 = size;
            } else {
                childAt.measure(View.MeasureSpec.makeMeasureSpec(size, mode == 1073741824 ? Integer.MIN_VALUE : mode), View.MeasureSpec.makeMeasureSpec(size2, mode2 != 1073741824 ? mode2 : Integer.MIN_VALUE));
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                int horizontalSpacing = getHorizontalSpacing(layoutParams);
                int verticalSpacing = getVerticalSpacing(layoutParams);
                int measuredWidth = childAt.getMeasuredWidth();
                int measuredHeight = childAt.getMeasuredHeight();
                i5 = size;
                if (this.orientation == 0) {
                    i6 = measuredHeight;
                } else {
                    i6 = measuredWidth;
                    measuredWidth = measuredHeight;
                    verticalSpacing = horizontalSpacing;
                    horizontalSpacing = verticalSpacing;
                }
                int i13 = i10 + measuredWidth;
                int i14 = i13 + horizontalSpacing;
                if (layoutParams.newLine || (i4 != 0 && i13 > i3)) {
                    i11 += iMax2;
                    iMax2 = i6 + verticalSpacing;
                    i14 = measuredWidth + horizontalSpacing;
                    i13 = measuredWidth;
                    i7 = i6;
                } else {
                    i7 = iMax3;
                }
                iMax2 = Math.max(iMax2, verticalSpacing + i6);
                iMax3 = Math.max(i7, i6);
                if (this.orientation == 0) {
                    paddingLeft = (getPaddingLeft() + i13) - measuredWidth;
                    paddingTop = getPaddingTop() + i11;
                } else {
                    paddingLeft = getPaddingLeft() + i11;
                    paddingTop = (getPaddingTop() + i13) - measuredHeight;
                }
                layoutParams.setPosition(paddingLeft, paddingTop);
                iMax = Math.max(iMax, i13);
                i9 = i11 + iMax3;
                i10 = i14;
            }
            i8++;
            childCount = i12;
            size = i5;
        }
        if (this.orientation == 0) {
            setMeasuredDimension(resolveSize(iMax, i), resolveSize(i9, i2));
        } else {
            setMeasuredDimension(resolveSize(i9, i), resolveSize(iMax, i2));
        }
    }

    private int getVerticalSpacing(LayoutParams layoutParams) {
        if (!layoutParams.verticalSpacingSpecified()) {
            return this.verticalSpacing;
        }
        return layoutParams.verticalSpacing;
    }

    private int getHorizontalSpacing(LayoutParams layoutParams) {
        if (!layoutParams.horizontalSpacingSpecified()) {
            return this.horizontalSpacing;
        }
        return layoutParams.horizontalSpacing;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            childAt.layout(layoutParams.x, layoutParams.y, layoutParams.x + childAt.getMeasuredWidth(), layoutParams.y + childAt.getMeasuredHeight());
        }
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View view, long j) {
        boolean zDrawChild = super.drawChild(canvas, view, j);
        drawDebugInfo(canvas, view);
        return zDrawChild;
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    private void readStyleParameters(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FlowLayout);
        try {
            this.horizontalSpacing = typedArrayObtainStyledAttributes.getDimensionPixelSize(1, 0);
            this.verticalSpacing = typedArrayObtainStyledAttributes.getDimensionPixelSize(3, 0);
            this.orientation = typedArrayObtainStyledAttributes.getInteger(2, 0);
            this.debugDraw = typedArrayObtainStyledAttributes.getBoolean(0, false);
        } finally {
            typedArrayObtainStyledAttributes.recycle();
        }
    }

    private void drawDebugInfo(Canvas canvas, View view) {
        if (this.debugDraw) {
            Paint paintCreatePaint = createPaint(InputDeviceCompat.SOURCE_ANY);
            Paint paintCreatePaint2 = createPaint(-16711936);
            Paint paintCreatePaint3 = createPaint(SupportMenu.CATEGORY_MASK);
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            if (layoutParams.horizontalSpacing > 0) {
                float right = view.getRight();
                float top = view.getTop() + (view.getHeight() / 2.0f);
                canvas.drawLine(right, top, right + layoutParams.horizontalSpacing, top, paintCreatePaint);
                canvas.drawLine((layoutParams.horizontalSpacing + right) - 4.0f, top - 4.0f, right + layoutParams.horizontalSpacing, top, paintCreatePaint);
                canvas.drawLine((layoutParams.horizontalSpacing + right) - 4.0f, top + 4.0f, right + layoutParams.horizontalSpacing, top, paintCreatePaint);
            } else if (this.horizontalSpacing > 0) {
                float right2 = view.getRight();
                float top2 = view.getTop() + (view.getHeight() / 2.0f);
                canvas.drawLine(right2, top2, right2 + this.horizontalSpacing, top2, paintCreatePaint2);
                int i = this.horizontalSpacing;
                canvas.drawLine((i + right2) - 4.0f, top2 - 4.0f, right2 + i, top2, paintCreatePaint2);
                int i2 = this.horizontalSpacing;
                canvas.drawLine((i2 + right2) - 4.0f, top2 + 4.0f, right2 + i2, top2, paintCreatePaint2);
            }
            if (layoutParams.verticalSpacing > 0) {
                float left = view.getLeft() + (view.getWidth() / 2.0f);
                float bottom = view.getBottom();
                canvas.drawLine(left, bottom, left, bottom + layoutParams.verticalSpacing, paintCreatePaint);
                canvas.drawLine(left - 4.0f, (layoutParams.verticalSpacing + bottom) - 4.0f, left, bottom + layoutParams.verticalSpacing, paintCreatePaint);
                canvas.drawLine(left + 4.0f, (layoutParams.verticalSpacing + bottom) - 4.0f, left, bottom + layoutParams.verticalSpacing, paintCreatePaint);
            } else if (this.verticalSpacing > 0) {
                float left2 = view.getLeft() + (view.getWidth() / 2.0f);
                float bottom2 = view.getBottom();
                canvas.drawLine(left2, bottom2, left2, bottom2 + this.verticalSpacing, paintCreatePaint2);
                int i3 = this.verticalSpacing;
                canvas.drawLine(left2 - 4.0f, (i3 + bottom2) - 4.0f, left2, bottom2 + i3, paintCreatePaint2);
                int i4 = this.verticalSpacing;
                canvas.drawLine(left2 + 4.0f, (i4 + bottom2) - 4.0f, left2, bottom2 + i4, paintCreatePaint2);
            }
            if (layoutParams.newLine) {
                if (this.orientation == 0) {
                    float left3 = view.getLeft();
                    float top3 = view.getTop() + (view.getHeight() / 2.0f);
                    canvas.drawLine(left3, top3 - 6.0f, left3, top3 + 6.0f, paintCreatePaint3);
                } else {
                    float left4 = view.getLeft() + (view.getWidth() / 2.0f);
                    float top4 = view.getTop();
                    canvas.drawLine(left4 - 6.0f, top4, left4 + 6.0f, top4, paintCreatePaint3);
                }
            }
        }
    }

    private Paint createPaint(int i) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(i);
        paint.setStrokeWidth(2.0f);
        return paint;
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        private static int NO_SPACING = -1;
        private int horizontalSpacing;
        private boolean newLine;
        private int verticalSpacing;
        private int x;
        private int y;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            int i = NO_SPACING;
            this.horizontalSpacing = i;
            this.verticalSpacing = i;
            this.newLine = false;
            readStyleParameters(context, attributeSet);
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            int i3 = NO_SPACING;
            this.horizontalSpacing = i3;
            this.verticalSpacing = i3;
            this.newLine = false;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            int i = NO_SPACING;
            this.horizontalSpacing = i;
            this.verticalSpacing = i;
            this.newLine = false;
        }

        public boolean horizontalSpacingSpecified() {
            return this.horizontalSpacing != NO_SPACING;
        }

        public boolean verticalSpacingSpecified() {
            return this.verticalSpacing != NO_SPACING;
        }

        public void setPosition(int i, int i2) {
            this.x = i;
            this.y = i2;
        }

        private void readStyleParameters(Context context, AttributeSet attributeSet) {
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FlowLayout_LayoutParams);
            try {
                this.horizontalSpacing = typedArrayObtainStyledAttributes.getDimensionPixelSize(0, NO_SPACING);
                this.verticalSpacing = typedArrayObtainStyledAttributes.getDimensionPixelSize(2, NO_SPACING);
                this.newLine = typedArrayObtainStyledAttributes.getBoolean(1, false);
            } finally {
                typedArrayObtainStyledAttributes.recycle();
            }
        }
    }
}

