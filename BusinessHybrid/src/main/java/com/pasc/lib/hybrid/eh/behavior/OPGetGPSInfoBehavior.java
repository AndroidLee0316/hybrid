 package com.pasc.lib.hybrid.eh.behavior;

 import android.Manifest;
 import android.app.Activity;
 import android.content.Context;
 import com.google.gson.Gson;
 import com.pasc.lib.base.permission.Permission;
 import com.pasc.lib.base.permission.RxPermissions;
 import com.pasc.lib.base.util.ToastUtils;
 import com.pasc.lib.hybrid.PascWebviewActivity;
 import com.pasc.lib.hybrid.behavior.BehaviorHandler;
 import com.pasc.lib.hybrid.callback.CallBackFunction;
 import com.pasc.lib.lbs.LbsManager;
 import com.pasc.lib.lbs.location.LocationException;
 import com.pasc.lib.lbs.location.PascLocationListener;
 import com.pasc.lib.lbs.location.bean.PascLocationData;
 import com.pasc.lib.smtbrowser.entity.GpsInfoBean;
 import com.pasc.lib.smtbrowser.entity.NativeResponse;
 import io.reactivex.functions.Consumer;
 import java.io.Serializable;
 import org.xml.sax.Locator;

 /**
 * create by wujianning385 on 2018/8/1.
 */
public class OPGetGPSInfoBehavior implements BehaviorHandler,Serializable {
    @Override
    public void handler(Context context, String data, CallBackFunction function,
                        NativeResponse response) {
      if(!(context instanceof Activity)){
          return;
      }
      RxPermissions rxPermission = new RxPermissions((Activity)context);
      Gson gson = new Gson();
      rxPermission.requestEach(Manifest.permission.ACCESS_COARSE_LOCATION,
              Manifest.permission.ACCESS_FINE_LOCATION).subscribe(new Consumer<Permission>() {
        @Override public void accept(Permission permission) throws Exception {
          if (permission.granted) {
            // 用户已经同意该权限
            LbsManager.getInstance().doLocation(0, new PascLocationListener() {
              @Override public void onLocationSuccess(PascLocationData pascLocationData) {
                GpsInfoBean gpsInfoBean = new GpsInfoBean(pascLocationData.getLongitude()
                        ,pascLocationData.getLatitude(),pascLocationData.getDistrict()
                        ,pascLocationData.getProvince(),pascLocationData.getCity()
                        ,pascLocationData.getDistrict(),pascLocationData.getAddress()
                        ,pascLocationData.getCityCode(),pascLocationData.getAdCode());

                response.data = gpsInfoBean;
                function.onCallBack(gson.toJson(response));
              }

              @Override public void onLocationFailure(LocationException e) {
                response.code = -1;
                response.message = "用户未打开定位";
                function.onCallBack(gson.toJson(response));
              }
            });
            //PascLocationData pascLocationData = LbsManager.getInstance().getLastLocationData();
            //if (pascLocationData==null){
            //  response.code = -1;
            //  response.message = "用户未打开定位";
            //  function.onCallBack(gson.toJson(response));
            //  ToastUtils.toastMsg("请打开GPS开关");
            //  return;
            //}

          } else if (permission.shouldShowRequestPermissionRationale) {
            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
            response.code = -1;
            response.message = "用户拒绝权限";
            function.onCallBack(gson.toJson(response));
          } else {
            // 用户拒绝了该权限，并且选中『不再询问』
            response.code = -1;
            response.message = "用户拒绝权限";
            function.onCallBack(gson.toJson(response));
          }
        }
      });

    }
}
