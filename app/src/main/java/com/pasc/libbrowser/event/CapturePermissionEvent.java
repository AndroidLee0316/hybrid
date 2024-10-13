package com.pasc.libbrowser.event;

/**
 * create by wujianning385 on 2018/8/11.
 */
public class CapturePermissionEvent {

    public boolean isHasPermission;

    public CapturePermissionEvent(boolean isHasPermission) {
        this.isHasPermission = isHasPermission;
    }
}
