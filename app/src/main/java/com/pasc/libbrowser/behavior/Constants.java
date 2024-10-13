package com.pasc.libbrowser.behavior;

/**
 * Created by lanshaomin
 * Date: 2019/8/26 下午4:10
 * Desc:
 */
public class Constants {
  public static final String WEB_BEHAVIOR_NAME_NATIVE_ROUTE = "PASC.app.nativeRoute";
  public static final String WEB_BEHAVIOR_NAME_OPEN_ADDRESS = "PASC.app.OpenAddress";
  public static final String WEB_BEHAVIOR_NAME_PAY = "PASC.app.Pay";
  public static final String WEB_BEHAVIOR_NAME_NAVIGATION = "PASC.app.Navigation";
  /**
   * 打开设置页面
   */
  public static final String WEB_BEHAVIOR_NAME_OPEN_SETTING = "PASC.app.openSetting";
  //web 业务跳转 跳转登录
  public static final String WEB_ROUTE_LOGIN_PATH = "/user/login/main";
  /**
   * 实名认证
   */
  public static final String WEB_ROUTER_CERTIFICATION_PATH="/user/auth/verify";
  /**
   * 实名认证：人脸认证
   */
  public static final String WEB_ROUTER_CERTIFICATION_FACE_PATH="/user/auth/face";
  /**
   * 人脸核验
   */
  public static final String WEB_ROUTER_FACE_CHECK_PATH="/user/auth/facecheck";
  //web 业务跳转 检查登录状态
  public static final String WEB_ROUTE_LOGIN_STATUS = "/user/login/status";

  public static final String WEB_ROUTE_CERT_STATUS = "PASC.app.authStatus";
  //web 业务 App更新
  public static final String WEB_ROUTER_UPDATE_APP = "/base/update/appUpdate";
  //ota 升级
  public static final String  SERVICE_OTA_UPDATE = "/ota/update/service";

  //验证指纹手势、faceID
  public static final String ROUTER_TO_AUTH_FINGER = "/user/service/authenticate";


  //语音、askbob
  public static final String WEB_BEHAVIOR_OP_RECORDER_MANAGER = "PASC.app.RecorderManager";
  public static final String WEB_BEHAVIOR_RECORDER_MANAGER = "PASC.app.recorderManager";
  public static final String WEB_BEHAVIOR_PERMISSION = "PASC.app.permission";
  public static final String WEB_BEHAVIOR_TTS_MANAGER = "PASC.app.TTSManager";
  //设置和获取屏幕亮度
  public static final String WEB_BEHAVIOR_SCREEN_BRIGHTNESS = "PASC.app.screenBrightness";
  //设置是否能截屏
  public static final String WEB_BEHAVIOR_USER_CAPTURE_SCREEN = "PASC.app.userCaptureScreen";

  //撤回隐私政策
  public static final String WEB_BEHAVIOR_WITHDRAW_PRIVACY_PROTOCOL = "PASC.app.withdrawPrivacyProtocol";

  //获取设备信息
  public static final String WEB_BEHAVIOR_GET_COUNTLY_INFO = "PASC.app.getCountlyInfo";


  //法人功能

  public static final String COMPANY_TO_AUTH = "PASC.app.legalPeopleAuth";

  public static final String COMPANY_IS_LOGIN = "PASC.app.isLegalPeopleLogin";

  public static final String COMPANY_TO_LOGIN = "PASC.app.legalPeopleLogin";

  public static final String COMPANY_GET_CERT = "PASC.app.legalPeopleAuthStatus";

  public static final String COMPANY_OPERTOR_STATUE = "PASC.app.authStatus";

}
