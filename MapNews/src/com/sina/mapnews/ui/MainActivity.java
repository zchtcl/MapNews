package com.sina.mapnews.ui;

import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.sina.mapnews.R;
import com.sina.mapnews.ui.view.NewsLayout;
import com.sina.mapnews.ui.view.TitleBar;
import com.sina.mapnews.util.Logger;
import com.sina.mapnews.util.ToastWrapper;

public class MainActivity extends BaseActivity {
    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationClient locationClient;
    private BroadcastReceiver sdkReceiver;
    private boolean isFirstLocation = true;

    private NewsLayout newsLayout;
    private LinearLayout newsHeader;
    private LinearLayout newsContent;
    private TextView newsTitle;
    private TextView newsDate;
    private TextView newsSource;
    private ImageView newsTopPic;
    private TextView newsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        configLocation();

        IntentFilter filter = new IntentFilter();
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        sdkReceiver = new SDKReceiver();
        registerReceiver(sdkReceiver, filter);
    }

    private void initView() {
        initTitleBar();
        mapView = $(R.id.vw_mapview);
        newsLayout = $(R.id.news_layout);
        newsHeader = $(R.id.news_header);
        newsContent = $(R.id.news_content);
        newsTitle = $(R.id.news_title);
        newsDate = $(R.id.news_date);
        newsSource = $(R.id.news_source);
        newsTopPic = $(R.id.news_top_pic);
        newsText = $(R.id.news_text);
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

    private void configLocation() {
        baiduMap = mapView.getMap();
        /* 设置是否允许定位图层 */
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng position = marker.getPosition();
                String toast = String.format(Locale.getDefault(), "Latitide: %f, Longitude: %f",
                        position.latitude, position.longitude);
                ToastWrapper.showToast(toast);
                return true;
            }
        });

        locationClient = new LocationClient(this);
        /* 注册定位监听函数 */
        locationClient.registerLocationListener(locationListener);
        LocationClientOption option = new LocationClientOption();
        /* 打开gps进行定位 */
        option.setOpenGps(true);
        /* 设置坐标类型 取值有3个： 返回国测局经纬度坐标系：gcj02 返回百度墨卡托坐标系 ：bd09 返回百度经纬度坐标系 ：bd09ll */
        option.setCoorType("bd09ll");
        /* 设置扫描间隔，单位是毫秒 */
        option.setScanSpan(5000);
        /* 设置为高精度模式 */
        option.setLocationMode(LocationMode.Hight_Accuracy);
        /* 设置 LocationClientOption */
        locationClient.setLocOption(option);
        locationClient.start();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        locationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
        unregisterReceiver(sdkReceiver);
    }

    private BDLocationListener locationListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null == location || null == mapView) {
                Logger.e("paramter is invalid");
                return;
            }

            Logger.d("lat: %f, lon: %f, radius: %f, direction: %f", location.getLatitude(),
                    location.getLongitude(), location.getRadius(), location.getDirection());
            MyLocationData data = new MyLocationData.Builder()
            /* 定位精度 */
            .accuracy(location.getRadius())
            /* GPS定位时方向角度 */
            .direction(location.getDirection())
            /* 百度纬度坐标 */
            .latitude(location.getLatitude())
            /* 百度经度坐标 */
            .longitude(location.getLongitude()).build();
            /* 设置定位数据, 只有先允许定位图层后设置数据才会生效 */
            baiduMap.setMyLocationData(data);

            if (isFirstLocation) {
                isFirstLocation = false;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(ll);
                /* 以动画方式更新地图状态，动画耗时 300 ms */
                baiduMap.animateMapStatus(status);
                OverlayOptions overlay = new MarkerOptions().position(ll)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_overlay))
                        .zIndex(0).draggable(false);
                baiduMap.addOverlay(overlay);
            }
        }
    };

    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            Logger.d("%s", s);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                Logger.e("key invalid");
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                ToastWrapper.showToast("网络出错");
            } else {
                Logger.d("successfully");
            }
        }
    }
}
