package com.pasc.lib.hybrid.eh.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.pasc.lib.base.util.DeviceUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.hybrid.eh.R;
import com.pasc.lib.hybrid.eh.utils.AMapUtil;
import com.pasc.lib.hybrid.eh.widget.HybridEHChooseOptionDialog;
import com.pasc.lib.hybrid.widget.WebCommonTitleView;

/**
 * create by wujianning385 on 2018/10/18.
 */
public class AddressNavigationActivity extends Activity {

  private Context mContext;

  private static final String LNG_KEY = "lng";
  private static final String LAT_KEY = "lat";
  private static final String NAME_KEY = "name";
  private static final String ADDRESS_KEY = "address";

  private MapView mMapView = null;
  private AMap aMap;
  private WebCommonTitleView mWebCommonTitleView;
  private LatLonPoint endPoint;
  private String addressName;

  public static void start(Context context, double lat, double lng, String name, String address) {
    Intent intent = new Intent(context, AddressNavigationActivity.class);
    intent.putExtra(LNG_KEY, lng)
            .putExtra(LAT_KEY, lat)
            .putExtra(NAME_KEY, name)
            .putExtra(ADDRESS_KEY, address);
    context.startActivity(intent);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = AddressNavigationActivity.this;
    setContentView(R.layout.activity_address_navigation);
    //获取地图控件引用
    mMapView = (MapView) this.findViewById(R.id.map_view);
    mWebCommonTitleView = (WebCommonTitleView) this.findViewById(R.id.title_view);
    mWebCommonTitleView.setOnLeftClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        AddressNavigationActivity.this.finish();
      }
    });
    mMapView.onCreate(savedInstanceState);
    //初始化地图控制器对象

    if (aMap == null) {
      aMap = mMapView.getMap();
    }

    init();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
    mMapView.onDestroy();
  }

  @Override protected void onResume() {
    super.onResume();
    //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
    mMapView.onResume();
  }

  @Override protected void onPause() {
    super.onPause();
    //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
    mMapView.onPause();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
    mMapView.onSaveInstanceState(outState);
  }

  private void init() {
    double lng = getIntent().getDoubleExtra(LNG_KEY, 0);
    double lat = getIntent().getDoubleExtra(LAT_KEY, 0);
    addressName = getIntent().getStringExtra(NAME_KEY);
    String address = getIntent().getStringExtra(ADDRESS_KEY);
    mWebCommonTitleView.setTitleText(addressName);
    endPoint = new LatLonPoint(lat, lng);
    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(AMapUtil.convertToLatLng(endPoint), 12));
    aMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
      @Override public View getInfoWindow(Marker marker) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.hybrideh_info_window_navigation, null);
        //InfoWindowData data = (InfoWindowData) marker.getObject();
        TextView poiName = (TextView) view.findViewById(R.id.temp_poi_name);
        poiName.setText(addressName);
        View navigationStart = view.findViewById(R.id.temp_navigation_start);
        navigationStart.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            setupMapSelectDialog();
          }
        });
        return view;
      }

      @Override public View getInfoContents(Marker marker) {
        return null;
      }
    });
    aMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))).showInfoWindow();
  }

  private void setupMapSelectDialog() {
    HybridEHChooseOptionDialog choosePhotoDialog =
            new HybridEHChooseOptionDialog(mContext, R.layout.hybrideh_choose_option_dialog,"使用高德地图","使用百度地图");
    choosePhotoDialog.setOnSelectedListener(new HybridEHChooseOptionDialog.OnSelectedListener() {
      @Override public void onFirst() {
        if (DeviceUtils.isAvilible(mContext, "com.autonavi.minimap")) {
          LatLng endLatLng = AMapUtil.convertToLatLng(endPoint);
          StringBuffer uri = new StringBuffer("amapuri://route/plan/?");
          uri.append("dlat=")
                  .append(endLatLng.latitude)
                  .append("&")
                  .append("dlon=")
                  .append(endLatLng.longitude)
                  .append("&")
                  .append("dname=")
                  .append(addressName);
          //.append("&")
          //.append("t=")
          //.append(NavigationActivity.Type.DRIVE);
          Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(uri.toString()));
          intent.setPackage("com.autonavi.minimap");
          choosePhotoDialog.cancel();
          try {
            startActivity(intent);
          } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.toastMsg("请先安装高德地图");
          }
        } else {
          ToastUtils.toastMsg("请先安装高德地图");
        }
      }

      @Override public void onSecond() {
        if (DeviceUtils.isAvilible(mContext, "com.baidu.BaiduMap")) {
          LatLng endLatLng = AMapUtil.convertToLatLng(endPoint);
          StringBuffer uri = new StringBuffer("baidumap://map/direction?");
          uri.append("destination=").append(addressName)
                  .append("|")
                  .append("latlng:")
                  .append(endLatLng.latitude)
                  .append(",")
                  .append(endLatLng.longitude)
                  .append("&")
                  .append("mode=")
                  .append("&")
                  .append("target")
                  .append(1);
          Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(uri.toString()));
          choosePhotoDialog.cancel();
          try {
            startActivity(intent);
          } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.toastMsg("请先安装百度地图");
          }
        } else {
          ToastUtils.toastMsg("请先安装百度地图");
        }
      }

      @Override public void onCancel() {

      }
    });
    choosePhotoDialog.show();
  }

  private void goToMarket(String packName) {
    Uri uri = Uri.parse("market://details?id=" + packName);
    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
    try {
      startActivity(goToMarket);
    } catch (ActivityNotFoundException e) {
      e.printStackTrace();
    }
  }
}
