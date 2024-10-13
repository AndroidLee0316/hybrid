package com.pasc.libbrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chenkun305.libbrowser.R;
import com.pasc.lib.net.resp.BaseRespThrowableObserver;
import com.pasc.lib.openplatform.network.TransformUtil;
import com.pasc.libbrowser.net.AppApi;
import com.pasc.libbrowser.net.LoginBean;
import com.pasc.libbrowser.net.UserInfoBean;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class LoginActivity extends Activity {

    TextView tv_login;
    EditText et_account;
    EditText et_password;
    public static String TOKEN = "csToken";
    CompositeDisposable disposables = new CompositeDisposable ();

    public static String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);
        initView ();
    }

    private void initView() {
        tv_login = findViewById (R.id.tv_login);
        et_account = findViewById (R.id.et_account);
        et_password = findViewById (R.id.et_password);


        tv_login.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                login ();
            }
        });

    }

    void login() {
        LoginBean loginBean = new LoginBean ();
        loginBean.mobile = et_account.getText ().toString ().trim ();
        loginBean.verificationCode = et_password.getText ().toString ().trim ();
        String url = "http://cssc-smt-stg1.yun.city.pingan.com/zuul-service/platform/user/mobileLogin";
        Disposable disposable = ApiGenerator.createApi (AppApi.class).login (url, loginBean)
                .compose (TransformUtil.getTransformer ())
                .subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ())
                .subscribe (new Consumer<UserInfoBean> () {
                    @Override
                    public void accept(UserInfoBean userInfoBeanBaseResp) throws Exception {
                        token = userInfoBeanBaseResp.token;
                        startActivity (new Intent (LoginActivity.this, MainActivity.class));
                        finish ();
                        Toast.makeText (LoginActivity.this, "登陆成功，可以测试开放平台", Toast.LENGTH_SHORT).show ();
                    }
                }, new BaseRespThrowableObserver () {
                    @Override
                    public void onError(int code, String msg) {
                        Toast.makeText (LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show ();
                    }
                });
        disposables.add (disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();
        disposables.clear ();
    }
}
