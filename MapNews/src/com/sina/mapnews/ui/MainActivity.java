package com.sina.mapnews.ui;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.sina.mapnews.R;
import com.sina.mapnews.ui.view.TitleBar;
import com.sina.mapnews.util.Logger;
import com.sina.mapnews.util.ToastWrapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends BaseActivity {
    private MapView mapView;
    private BroadcastReceiver sdkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        IntentFilter filter = new IntentFilter();
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        sdkReceiver = new SDKReceiver();
        registerReceiver(sdkReceiver, filter);
    }

    private void initView() {
        initTitleBar();
        mapView = $(R.id.vw_mapview);
    }

    private void initTitleBar() {
        TitleBar titleBar = $(R.id.vw_titlebar);
        titleBar.setTitle(R.string.main_title);
        titleBar.addAction(new TitleBar.Action().setDrawable(R.drawable.ic_back_selector)
                .setListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }), TitleBar.Action.LEFT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sdkReceiver);
    }

    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            Logger.d("%s", s);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                ToastWrapper.showToast("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                ToastWrapper.showToast("网络出错");
            } else {
                ToastWrapper.showToast("key注册成功");
            }
        }
    }
}
