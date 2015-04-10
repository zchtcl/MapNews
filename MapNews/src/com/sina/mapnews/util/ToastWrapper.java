package com.sina.mapnews.util;

import com.sina.mapnews.App;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public final class ToastWrapper {
    private static TextView mContentView = null;
    private static Toast mToast = null;

    static {
        mToast = Toast.makeText(App.getCtx(), "", Toast.LENGTH_SHORT);
        View rootView = mToast.getView();
        int contentId = App.getCtx().getResources().getIdentifier("message", "id", "android");
        mContentView = (TextView) rootView.findViewById(contentId);
        mContentView.setGravity(Gravity.CENTER);
    }

    public static void showToast(String str) {
        try {
            mContentView.setText(str);
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast(int resId) {
        try {
            mContentView.setText(resId);
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
