package com.sina.mapnews.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * 所有Activity的基类。封装了Activity的通用操作，如统计。
 * 其他所有Activity必须继承此基类。
 *
 * @author xuegang
 * @version 2014年11月24日 下午6:57:19
 */
public class BaseActivity extends FragmentActivity {
    /**
     * Enum to indicate the status of a page
     *
     * @author snow
     * @version 2014年12月13日 下午9:48:31
     */
    public static enum Status {
        /** Page is displayed normally */
        NORMAL,
        /** Page is in loading status */
        LOADING,
        /** Page load failed, and can be touched to reload */
        RELOAD
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 结果与findViewById(R.id....)相同，采用$作为参数名，借鉴自jQuery
     * 
     * @param id
     * @return
     *
     * @author xuegang
     * @version Created: 2015年1月7日 下午3:00:57
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T $(int id) {
        return (T) super.findViewById(id);
    }
}
