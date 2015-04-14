package com.sina.mapnews.ui.view;

import com.sina.mapnews.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * 
 *
 * @author xuegang
 * @version Created: 2015年4月8日 下午12:08:13
 */
public class NewsLayout extends ViewGroup {
    private ViewDragHelper dragHelper;
    private View headerView;
    private View contentView;
    private int headerViewtop;
    private int dragRange;
    private float dragOffset;
    private boolean isFirstLayout = true;

    public NewsLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        dragHelper = ViewDragHelper.create(this, 1.0F, new DragHelperCallback());
    }

    public NewsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewsLayout(Context context) {
        this(context, null);
    }

    @Override
    protected void onFinishInflate() {
        headerView = findViewById(R.id.news_header);
        contentView = findViewById(R.id.news_content);
    }

    public void maximum() {
        smoothSlideTo(0);
    }

    public void minimum() {
        smoothSlideTo(1.0F);
    }

    private void smoothSlideTo(float slideOffset) {
        final int topBound = getPaddingTop();
        int y = (int) (topBound + slideOffset * dragRange);
        if (dragHelper.smoothSlideViewTo(headerView, headerView.getLeft(), y)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return;
        }
    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        dragRange = getHeight() - headerView.getMeasuredHeight();
        int headerViewBottom = headerViewtop + headerView.getMeasuredHeight();
        headerView.layout(l, headerViewtop, r, headerViewBottom);
        contentView.layout(l, headerViewBottom, r,
                headerViewBottom + contentView.getMeasuredHeight());
        if (isFirstLayout) {
            isFirstLayout = false;
            getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {

                        @SuppressWarnings("deprecation")
                        @Override
                        public void onGlobalLayout() {
                            getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            minimum();
                        }
                    });
        }
    }

    private float initialX;
    private float initialY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);

        final float x = event.getX();
        final float y = event.getY();
        boolean isHeaderViewHit = isViewUnder(headerView, event);

        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN: {
                initialX = x;
                initialY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                final float dx = x - initialX;
                final float dy = y - initialY;
                final int slop = dragHelper.getTouchSlop();
                /* headerView is clicked */
                if (dx * dx + dy * dy < slop * slop && isHeaderViewHit) {
                    if (dragOffset == 0) {
                        smoothSlideTo(1f);
                    } else {
                        smoothSlideTo(0f);
                    }
                }
                break;
            }
        }

        /* If headerView or contentView is touched, return true */
        return isHeaderViewHit || isViewUnder(contentView, event);
    }

    private int[] viewLocation = new int[2];

    /**
     * 判断点击事件是否在View范围之内
     * 
     * @param view
     * @param event
     * @return
     *
     * @author xuegang
     * @version Created: 2015年4月8日 下午4:55:52
     */
    private boolean isViewUnder(View view, MotionEvent event) {
        view.getLocationOnScreen(viewLocation);
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        return rawX >= viewLocation[0] && rawX < viewLocation[0] + view.getWidth()
                && rawY >= viewLocation[1] && rawY < viewLocation[1] + view.getHeight();
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View arg0, int arg1) {
            return arg0 == headerView;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - headerView.getHeight()
                    - headerView.getPaddingBottom();

            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            return newTop;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return dragRange;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            headerViewtop = top;
            dragOffset = top / (float) dragRange;
            contentView.setAlpha(1 - dragOffset);

            requestLayout();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int finalTop = getPaddingTop();
            if (yvel > 0 || (yvel == 0 && dragOffset > 0.5)) {
                finalTop += dragRange;
            }

            dragHelper.settleCapturedViewAt(releasedChild.getLeft(), finalTop);
            invalidate();
        }

    }
}
