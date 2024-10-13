package com.pasc.lib.hybrid.eh.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.pasc.lib.base.permission.Permission;
import com.pasc.lib.base.permission.RxPermissions;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.hybrid.eh.R;
import com.pasc.lib.hybrid.eh.utils.DrivingRouteOverlay;
import com.pasc.lib.hybrid.widget.WebCommonTitleView;
import com.pasc.lib.lbs.LbsManager;
import com.pasc.lib.lbs.location.LocationException;
import com.pasc.lib.lbs.location.PascLocationListener;
import com.pasc.lib.lbs.location.bean.PascLocationData;
import com.pasc.lib.smtbrowser.entity.GpsInfoBean;

import io.reactivex.functions.Consumer;

/**
 * create by wujianning385 on 2019/7/12.
 */
public class DriveRouteActivity extends Activity
        implements OnMapClickListener, OnMarkerClickListener, OnInfoWindowClickListener,
        OnRouteSearchListener, OnClickListener, AMap.OnMapLoadedListener {

  public static String KEY_CITY_NAME = "city_name_key";
  public static String KEY_TO_SITE = "to_site_key";
  public static String KEY_START_LAT = "start_lat_key";
  public static String KEY_START_LNG = "start_lng_key";
  public static String KEY_END_LAT = "end_lat_key";
  public static String KEY_END_LNG = "end_lng_key";

  private AMap aMap;
  private MapView mapView;
  private Context mContext;
  private RouteSearch mRouteSearch;
  private DriveRouteResult mDriveRouteResult;
  private LatLonPoint mStartPoint = new LatLonPoint(39.942295, 116.335891);//起点，39.942295,116.335891
  private LatLonPoint mEndPoint = new LatLonPoint(39.995576, 116.481288);//终点，39.995576,116.481288

  private final int ROUTE_TYPE_DRIVE = 2;

  private WebCommonTitleView mTitleView;
  //private RelativeLayout mBottomLayout;
  //private TextView mRotueTimeDes, mRouteDetailDes;
  //private ImageView mTrafficView;
  //private ProgressDialog progDialog = null;// 搜索时进度条

  public static void start(Context activity, String startLat, String startLng, String endLat,
          String endLng) {
    Intent intent = new Intent(activity, DriveRouteActivity.class);
    intent.putExtra(KEY_START_LAT, startLat)
            .putExtra(KEY_START_LNG, startLng)
            .putExtra(KEY_END_LAT, endLat)
            .putExtra(KEY_END_LNG, endLng);
    activity.startActivity(intent);
  }

  @Override protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.hybrideh_activity_drive_route);

    mContext = this.getApplicationContext();
    mapView = (MapView) findViewById(R.id.route_map);
    mTitleView = this.findViewById(R.id.title_view);
    mapView.onCreate(bundle);// 此方法必须重写

    //mTrafficView = (ImageView) findViewById(R.id.map_traffic);
    //mTrafficView.setOnClickListener(this);
    init();
  }

  /**
   * 初始化AMap对象
   */
  private void init() {
    if (aMap == null) {
      aMap = mapView.getMap();
      aMap.setTrafficEnabled(true);
    }
    registerListener();
    try{
      mRouteSearch = new RouteSearch(this);
      mRouteSearch.setRouteSearchListener(this);
    }catch (Exception e){
      e.printStackTrace();
    }

    mTitleView.setTitleText("路线");
    mTitleView.setOnLeftClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        onBackPressed();
      }
    });

    if (!TextUtils.isEmpty(getIntent().getStringExtra(KEY_START_LAT)) && !TextUtils.isEmpty(getIntent().getStringExtra(KEY_START_LNG))){

      double startLat = Double.valueOf(getIntent().getStringExtra(KEY_START_LAT));
      double startLng = Double.valueOf(getIntent().getStringExtra(KEY_START_LNG));
      double endLat = Double.valueOf(getIntent().getStringExtra(KEY_END_LAT));
      double endLng = Double.valueOf(getIntent().getStringExtra(KEY_END_LNG));
      mStartPoint = new LatLonPoint(startLat, startLng);
      mEndPoint = new LatLonPoint(endLat, endLng);

    }else {
      RxPermissions rxPermission = new RxPermissions(this);
      rxPermission.requestEach(Manifest.permission.ACCESS_COARSE_LOCATION,
              Manifest.permission.ACCESS_FINE_LOCATION).subscribe(new Consumer<Permission>() {
        @Override public void accept(Permission permission) throws Exception {
          if (permission.granted) {
            // 用户已经同意该权限
            LbsManager.getInstance().doLocation(0, new PascLocationListener() {
              @Override public void onLocationSuccess(PascLocationData pascLocationData) {

                double startLat = pascLocationData.getLatitude();
                double startLng = pascLocationData.getLongitude();
                double endLat = Double.valueOf(getIntent().getStringExtra(KEY_END_LAT));
                double endLng = Double.valueOf(getIntent().getStringExtra(KEY_END_LNG));
                mStartPoint = new LatLonPoint(startLat, startLng);
                mEndPoint = new LatLonPoint(endLat, endLng);
                aMap.reloadMap();
              }

              @Override public void onLocationFailure(LocationException e) {
                ToastUtils.toastMsg("用户未授权定位权限");
                finish();
              }
            });

          }  else {
            ToastUtils.toastMsg("用户未授权定位权限");
            finish();
          }
        }
      });

    }
  }


  /**
   * 注册监听
   */
  private void registerListener() {
    aMap.setOnMapLoadedListener(DriveRouteActivity.this);
    aMap.setOnMapClickListener(DriveRouteActivity.this);
    aMap.setOnMarkerClickListener(DriveRouteActivity.this);
    aMap.setOnInfoWindowClickListener(DriveRouteActivity.this);
  }

  @Override public void onInfoWindowClick(Marker arg0) {
    // TODO Auto-generated method stub

  }

  @Override public boolean onMarkerClick(Marker arg0) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override public void onMapClick(LatLng arg0) {
    // TODO Auto-generated method stub
  }

  /**
   * 开始搜索路径规划方案
   */
  public void searchRouteResult(int routeType, int mode) {
    if (mStartPoint == null) {
      ToastUtils.toastMsg("定位中，稍后再试...");
      return;
    }
    if (mEndPoint == null) {
      ToastUtils.toastMsg("终点未设置");
    }
    final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
    if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
      DriveRouteQuery query = new DriveRouteQuery(fromAndTo, mode, null, null,
              "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
      mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
    }
  }

  @Override public void onBusRouteSearched(BusRouteResult result, int errorCode) {

  }

  @Override public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
    aMap.clear();// 清理地图上的所有覆盖物
    if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
      if (result != null && result.getPaths() != null) {
        if (result.getPaths().size() > 0) {
          mDriveRouteResult = result;
          final DrivePath drivePath = mDriveRouteResult.getPaths().get(0);
          DrivingRouteOverlay drivingRouteOverlay =
                  new DrivingRouteOverlay(mContext, aMap, drivePath,
                          mDriveRouteResult.getStartPos(), mDriveRouteResult.getTargetPos(), null);
          drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
          drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
          drivingRouteOverlay.removeFromMap();
          drivingRouteOverlay.addToMap();
          drivingRouteOverlay.zoomToSpan();
          //mBottomLayout.setVisibility(View.VISIBLE);
          //int dis = (int) drivePath.getDistance();
          //int dur = (int) drivePath.getDuration();
          //String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
          //mRotueTimeDes.setText(des);
          //mRouteDetailDes.setVisibility(View.VISIBLE);
          //int taxiCost = (int) mDriveRouteResult.getTaxiCost();
          //mRouteDetailDes.setText("打车约" + taxiCost + "元");
          //mBottomLayout.setOnClickListener(new OnClickListener() {
          //  @Override
          //  public void onClick(View v) {
          //    Intent intent = new Intent(mContext,
          //            DriveRouteDetailActivity.class);
          //    intent.putExtra("drive_path", drivePath);
          //    intent.putExtra("drive_result",
          //            mDriveRouteResult);
          //    startActivity(intent);
          //  }
          //});

        } else if (result.getPaths() == null) {
          ToastUtils.toastMsg("暂无结果");
        }
      } else {
        ToastUtils.toastMsg("暂无结果");
      }
    } else {
      ToastUtils.toastMsg(errorCode);
    }
  }

  @Override public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {

  }

  /**
   * 方法必须重写
   */
  @Override protected void onResume() {
    super.onResume();
    mapView.onResume();
  }

  /**
   * 方法必须重写
   */
  @Override protected void onPause() {
    super.onPause();
    mapView.onPause();
  }

  /**
   * 方法必须重写
   */
  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }

  /**
   * 方法必须重写
   */
  @Override protected void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }

  @Override public void onRideRouteSearched(RideRouteResult arg0, int arg1) {
    // TODO Auto-generated method stub
  }

  @Override public void onClick(View view) {
    //if (view.getId() == R.id.map_traffic) {
    //  if (aMap.isTrafficEnabled()) {
    //    mTrafficView.setImageResource(R.drawable.map_traffic_white);
    //    aMap.setTrafficEnabled(false);
    //  } else {
    //    mTrafficView.setImageResource(R.drawable.map_traffic_hl_white);
    //    aMap.setTrafficEnabled(true);
    //  }
    //}
  }

  @Override public void onMapLoaded() {
    searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DRIVING_SINGLE_DEFAULT);
  }
}
