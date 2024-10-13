package com.pasc.offline.zip;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class ZipTest extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zip();
    }

    public void zip(){
        try {
            String[] permissions = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
            new RxPermissions(ZipTest.this)
                    .request(permissions)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                ZipUtil.zipFolder(Environment.getExternalStorageDirectory() + "/libOffline/ntsafe.docx"
                                        , Environment.getExternalStorageDirectory() + "/libOffline/1.zip");
                                ZipUtil.unZip(Environment.getExternalStorageDirectory() + "/libOffline/ntsafe.zip"
                                        , Environment.getExternalStorageDirectory() + "/libOffline/");
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
