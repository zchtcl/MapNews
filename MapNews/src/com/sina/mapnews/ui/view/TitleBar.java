package com.sina.mapnews.ui.view;

import com.sina.mapnews.R;
import com.sina.mapnews.util.SystemFacade;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @Description
 * @author snow
 * @version Created: 2015年3月10日 下午9:48:47
 */
public class TitleBar extends RelativeLayout {
    private TextView tvTitle;
    private LinearLayout leftContainer;
    private LinearLayout rightContainer;
    private View divider;

    /** Horizontal margin for each action */
    private static final int ACTION_MARGIN_HORIZONTAL = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 12, SystemFacade.getDisplayMetrics());

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.vw_titlebar, this);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.vw_titlebar, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvTitle = (TextView) findViewById(R.id.tv_title);
        leftContainer = (LinearLayout) findViewById(R.id.ll_left_container);
        rightContainer = (LinearLayout) findViewById(R.id.ll_right_container);
        divider = findViewById(R.id.vw_divider);
    }

    /**
     * Set title text
     * 
     * @param title
     *
     * @return This TitleBar
     * 
     * @author xuegang
     * @version Created: 2015年3月11日 上午10:14:39
     */
    public TitleBar setTitle(String title) {
        tvTitle.setText(title);
        return this;
    }

    /**
     * Set title text
     * 
     * @param resId
     * @return This TitleBar
     *
     * @author xuegang
     * @version Created: 2015年3月12日 上午10:01:12
     */
    public TitleBar setTitle(int resId) {
        tvTitle.setText(resId);
        return this;
    }

    /**
     * Whether to show the bottom divider
     * 
     * @param show
     *
     * @return This TitleBar
     * 
     * @author xuegang
     * @version Created: 2015年3月11日 上午10:14:18
     */
    public TitleBar showDivider(boolean show) {
        divider.setVisibility(show ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * Add an action to title bar
     * 
     * @param action the action to be added
     * @param position where to add this action, can be Action.Left, or Action.Right
     *
     * @return This TitleBar
     * 
     * @author xuegang
     * @version Created: 2015年3月11日 上午10:14:49
     */
    public TitleBar addAction(Action action, int position) {
        ImageView iv = new ImageView(getContext());
        iv.setImageResource(action.getDrawableRes());
        iv.setOnClickListener(action.getListener());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if (position == Action.LEFT) {
            lp.leftMargin = ACTION_MARGIN_HORIZONTAL;
            iv.setLayoutParams(lp);
            leftContainer.addView(iv);
        } else {
            lp.rightMargin = ACTION_MARGIN_HORIZONTAL;
            iv.setLayoutParams(lp);
            rightContainer.addView(iv);
        }

        return this;
    }

    /**
     * Representation of an action that can be performed
     *
     * @author xuegang
     * @version 2015年3月11日 上午10:16:25
     */
    public static class Action {
        /** Action position on left */
        public static final int LEFT = 0;
        /** Action position on right */
        public static final int RIGHT = 1;

        /** Drawable resource on this action */
        private int drawableRes;
        /** Listener on this action */
        private View.OnClickListener listener;

        public int getDrawableRes() {
            return this.drawableRes;
        }

        public Action setDrawable(int drawableRes) {
            this.drawableRes = drawableRes;
            return this;
        }

        public View.OnClickListener getListener() {
            return this.listener;
        }

        public Action setListener(View.OnClickListener listener) {
            this.listener = listener;
            return this;
        }
    }
}
