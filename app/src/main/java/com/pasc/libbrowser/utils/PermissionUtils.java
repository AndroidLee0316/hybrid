package com.pasc.libbrowser.utils;

import android.Manifest;
import android.app.Activity;
import android.support.annotation.NonNull;
import com.example.chenkun305.libbrowser.BuildConfig;
import com.tbruyelle.rxpermissions2.RxPermissions;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by huanglihou519 on 2017/11/15.
 * 适配大部分国内机型，支持魅族摄像头权限
 */

public class PermissionUtils {
    private PermissionUtils() {
        throw new AssertionError("no instances");
    }

    // 官方定义的需要动态请求的权限组
    public static final class Groups {
        public final static String[] LOCATION = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        public final static String[] CALENDAR = new String[]{
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
        };

        public final static String[] CAMERA = new String[]{
                Manifest.permission.CAMERA
        };

        public final static String[] CONTACTS = new String[]{
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.GET_ACCOUNTS
        };

        public final static String[] MICROPHONE = new String[]{
                Manifest.permission.RECORD_AUDIO
        };

        public final static String[] PHONE = new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.USE_SIP,
                Manifest.permission.PROCESS_OUTGOING_CALLS
        };

        public final static String[] SENSORS = new String[]{
                Manifest.permission.BODY_SENSORS
        };

        public final static String[] SMS = new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_WAP_PUSH,
                Manifest.permission.RECEIVE_MMS
        };

        public final static String[] STORAGE = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

    }

    private static RxPermissions createRxPermissions(@NonNull Activity activity) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.setLogging(BuildConfig.DEBUG);
        return rxPermissions;
    }

    public static Observable<Boolean> request(@NonNull Activity activity, final String... permissions) {
        return createRxPermissions(activity).request(permissions);
    }

    public static Observable<Permission> requestEach(@NonNull Activity activity, final String... permissions) {
        return createRxPermissions(activity).requestEach(permissions)
                .map(new Function<com.tbruyelle.rxpermissions2.Permission, Permission>() {
                    @Override
                    public Permission apply(com.tbruyelle.rxpermissions2.Permission rxPermission) throws Exception {
                        return new Permission(rxPermission.name,
                                rxPermission.granted,
                                rxPermission.shouldShowRequestPermissionRationale);
                    }
                });
    }

    public static Observable<Boolean> shouldShowRequestPermissionRationale(final Activity activity, final String... permissions) {
        return createRxPermissions(activity)
                .shouldShowRequestPermissionRationale(activity, permissions);
    }

    public static class Permission {
        public final String name;
        public final boolean granted;
        public final boolean shouldShowRequestPermissionRationale;

        public Permission(String name, boolean granted) {
            this(name, granted, false);
        }

        public Permission(String name, boolean granted, boolean shouldShowRequestPermissionRationale) {
            this.name = name;
            this.granted = granted;
            this.shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale;
        }

        @Override
        @SuppressWarnings("SimplifiableIfStatement")
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final com.tbruyelle.rxpermissions2.Permission that = (com.tbruyelle.rxpermissions2.Permission) o;

            if (granted != that.granted) return false;
            if (shouldShowRequestPermissionRationale != that.shouldShowRequestPermissionRationale)
                return false;
            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + (granted ? 1 : 0);
            result = 31 * result + (shouldShowRequestPermissionRationale ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Permission{" +
                    "name='" + name + '\'' +
                    ", granted=" + granted +
                    ", shouldShowRequestPermissionRationale=" + shouldShowRequestPermissionRationale +
                    '}';
        }
    }
}
