package com.pasc.lib.hybrid.eh.utils;

import android.content.Context;
import android.graphics.Color;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.TMC;
import com.pasc.lib.hybrid.eh.R;
import java.util.ArrayList;
import java.util.List;

/**
 * 导航路线图层类。
 */
public class DrivingRouteOverlay extends RouteOverlay{

	private DrivePath drivePath;
    private List<LatLonPoint> throughPointList;
    private List<Marker> throughPointMarkerList = new ArrayList<Marker>();
    private boolean throughPointMarkerVisible = true;
    private List<TMC> tmcs;
    private PolylineOptions mPolylineOptions;
    private PolylineOptions mPolylineOptionscolor;
    private Context mContext;
    private boolean isColorfulline = true;
    private float mWidth = 25;
    private List<LatLng> mLatLngsOfPath;

	public void setIsColorfulline(boolean iscolorfulline) {
		this.isColorfulline = iscolorfulline;
	}

	/**
     * 根据给定的参数，构造一个导航路线图层类对象。
     *
     * @param amap      地图对象。
     * @param path 导航路线规划方案。
     * @param context   当前的activity对象。
     */
    public DrivingRouteOverlay(Context context, AMap amap, DrivePath path,
                               LatLonPoint start, LatLonPoint end, List<LatLonPoint> throughPointList) {
    	super(context);
    	mContext = context; 
        mAMap = amap; 
        this.drivePath = path;
        startPoint = AMapUtil.convertToLatLng(start);
        endPoint = AMapUtil.convertToLatLng(end);
        this.throughPointList = throughPointList;
    }

    public float getRouteWidth() {
        return mWidth;
    }

    /**
     * 设置路线宽度
     *
     * @param mWidth 路线宽度，取值范围：大于0
     */
    public void setRouteWidth(float mWidth) {
        this.mWidth = mWidth;
    }

    /**
     * 添加驾车路线添加到地图上显示。
     */
	public void addToMap() {
		initPolylineOptions();
        try {
            if (mAMap == null) {
                return;
            }

            if (mWidth == 0 || drivePath == null) {
                return;
            }
            mLatLngsOfPath = new ArrayList<LatLng>();
            tmcs = new ArrayList<TMC>();
            List<DriveStep> drivePaths = drivePath.getSteps();
            mPolylineOptions.add(startPoint);
            for (DriveStep step : drivePaths) {
                List<LatLonPoint> latlonPoints = step.getPolyline();
                List<TMC> tmclist = step.getTMCs();
                tmcs.addAll(tmclist);
                addDrivingStationMarkers(step, convertToLatLng(latlonPoints.get(0)));
                for (LatLonPoint latlonpoint : latlonPoints) {
                	mPolylineOptions.add(convertToLatLng(latlonpoint));
                	mLatLngsOfPath.add(convertToLatLng(latlonpoint));
				}
            }
            mPolylineOptions.add(endPoint);
            if (startMarker != null) {
                startMarker.remove();
                startMarker = null;
            }
            if (endMarker != null) {
                endMarker.remove();
                endMarker = null;
            }
            addStartAndEndMarker();
            addThroughPointMarker();
            if (isColorfulline && tmcs.size()>0 ) {
            	colorWayUpdate(tmcs);
            	showcolorPolyline();
			}else {
				showPolyline();
			}            
            
        } catch (Throwable e) {
        	e.printStackTrace();
        }
    }

	/**
     * 初始化线段属性
     */
    private void initPolylineOptions() {

        mPolylineOptions = null;

        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(getDriveColor()).width(getRouteWidth());
    }

    private void showPolyline() {
        addPolyLine(mPolylineOptions);
    }
    
    private void showcolorPolyline() {
    	addPolyLine(mPolylineOptionscolor);
		
	}

    /**
     * 根据不同的路段拥堵情况展示不同的颜色
     *
     * @param tmcSection
     */
    private void colorWayUpdate(List<TMC> tmcSection) {
        if (mAMap == null) {
            return;
        }
        if (tmcSection == null || tmcSection.size() <= 0) {
            return;
        }
        TMC segmentTrafficStatus;
        mPolylineOptionscolor = null;
        mPolylineOptionscolor = new PolylineOptions();
        mPolylineOptionscolor.width(getRouteWidth());
        List<Integer> colorList = new ArrayList<Integer>();
        mPolylineOptionscolor.add(startPoint);
        mPolylineOptionscolor.add(AMapUtil.convertToLatLng(tmcSection.get(0).getPolyline().get(0)));
        colorList.add(getDriveColor());
        for (int i = 0; i < tmcSection.size(); i++) {
        	segmentTrafficStatus = tmcSection.get(i);
        	int color = getcolor(segmentTrafficStatus.getStatus());
        	List<LatLonPoint> mployline = segmentTrafficStatus.getPolyline();
			for (int j = 1; j < mployline.size(); j++) {
				mPolylineOptionscolor.add(AMapUtil.convertToLatLng(mployline.get(j)));
				colorList.add(color);
			}
		}
        mPolylineOptionscolor.add(endPoint); 
        colorList.add(getDriveColor());
        mPolylineOptionscolor.colorValues(colorList);
    }
    
    private int getcolor(String status) {

    	if (status.equals("畅通")) {
    		return Color.GREEN;
		} else if (status.equals("缓行")) {
			 return Color.YELLOW;
		} else if (status.equals("拥堵")) {
			return Color.RED;
		} else if (status.equals("严重拥堵")) {
			return Color.parseColor("#990033");
		} else {
			return Color.parseColor("#537edc");
		}	
	}

	public LatLng convertToLatLng(LatLonPoint point) {
        return new LatLng(point.getLatitude(),point.getLongitude());
  }
    
    /**
     * @param driveStep
     * @param latLng
     */
    private void addDrivingStationMarkers(DriveStep driveStep, LatLng latLng) {
        addStationMarker(new MarkerOptions()
                .position(latLng)
                .title("\u65B9\u5411:" + driveStep.getAction()
                        + "\n\u9053\u8DEF:" + driveStep.getRoad())
                .snippet(driveStep.getInstruction()).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(getDriveBitmapDescriptor()));
    }

    @Override
    protected LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        b.include(new LatLng(startPoint.latitude, startPoint.longitude));
        b.include(new LatLng(endPoint.latitude, endPoint.longitude));
        if (this.throughPointList != null && this.throughPointList.size() > 0) {
            for (int i = 0; i < this.throughPointList.size(); i++) {
                b.include(new LatLng(
                        this.throughPointList.get(i).getLatitude(),
                        this.throughPointList.get(i).getLongitude()));
            }
        }
        return b.build();
    }

    public void setThroughPointIconVisibility(boolean visible) {
        try {
            throughPointMarkerVisible = visible;
            if (this.throughPointMarkerList != null
                    && this.throughPointMarkerList.size() > 0) {
                for (int i = 0; i < this.throughPointMarkerList.size(); i++) {
                    this.throughPointMarkerList.get(i).setVisible(visible);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    private void addThroughPointMarker() {
        if (this.throughPointList != null && this.throughPointList.size() > 0) {
            LatLonPoint latLonPoint = null;
            for (int i = 0; i < this.throughPointList.size(); i++) {
                latLonPoint = this.throughPointList.get(i);
                if (latLonPoint != null) {
                    throughPointMarkerList.add(mAMap
                            .addMarker((new MarkerOptions())
                                    .position(
                                            new LatLng(latLonPoint
                                                    .getLatitude(), latLonPoint
                                                    .getLongitude()))
                                    .visible(throughPointMarkerVisible)
                                    .icon(getThroughPointBitDes())
                                    .title("\u9014\u7ECF\u70B9")));
                }
            }
        }
    }
    
    private BitmapDescriptor getThroughPointBitDes() {
    	return BitmapDescriptorFactory.fromResource(R.drawable.hybrideh_amap_through);
       
    }


    /**
     * 去掉DriveLineOverlay上的线段和标记。
     */
    @Override
    public void removeFromMap() {
        try {
            super.removeFromMap();
            if (this.throughPointMarkerList != null
                    && this.throughPointMarkerList.size() > 0) {
                for (int i = 0; i < this.throughPointMarkerList.size(); i++) {
                    this.throughPointMarkerList.get(i).remove();
                }
                this.throughPointMarkerList.clear();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}