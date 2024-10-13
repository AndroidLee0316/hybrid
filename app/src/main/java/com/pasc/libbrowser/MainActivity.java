package com.pasc.libbrowser;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chenkun305.libbrowser.R;
import com.pasc.lib.hybrid.PascHybridInterface;
import com.pasc.lib.hybrid.PascWebFragment;
import com.pasc.lib.hybrid.nativeability.WebStrategy;
import com.pasc.lib.hybrid.nativeability.WebStrategyType;
import com.pasc.lib.hybrid.webview.PascWebView;
import com.pasc.lib.net.resp.BaseRespThrowableObserver;
import com.pasc.lib.openplatform.network.OpenBiz;
import com.pasc.lib.openplatform.resp.CheckInitCodeResp;
import com.pasc.lib.openplatform.resp.OpenIdResp;
import com.pasc.lib.openplatform.resp.RequestCodeResp;
import com.pasc.lib.openplatform.resp.ServiceInfoResp;
import com.pasc.lib.openplatform.resp.ServiceStatusResp;
import com.pasc.libbrowser.fragment.Fragment1;
import com.pasc.libbrowser.fragment.Fragment2;
import com.pasc.libbrowser.utils.Locator;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PascHybridInterface {

    private static final String TO_TAB_INDEX = "to_index";

    private Context mContext;

    private ViewPager viewPager;
    private TextView tvTab1;
    private TextView tvTab2;
    public CompositeDisposable disposables = new CompositeDisposable();
    private List<Fragment> fragmentContainer;
    private final static String EXTRA_URL_FLG = "extra_url";
    PascWebFragment fragment3;


    public static void start(Context context, int tabIndex) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(TO_TAB_INDEX, tabIndex);
        context.startActivity(intent);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = MainActivity.this;
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) this.findViewById(R.id.viewpager);
        tvTab1 = (TextView) this.findViewById(R.id.tv_tab_1);
        tvTab2 = (TextView) this.findViewById(R.id.tv_tab_2);
        findViewById(R.id.tv1).setOnClickListener(this);
        findViewById(R.id.tv2).setOnClickListener(this);
        findViewById(R.id.tv3).setOnClickListener(this);
        findViewById(R.id.tv4).setOnClickListener(this);
        findViewById(R.id.tv5).setOnClickListener(this);
        tvTab1.setOnClickListener(this);
        tvTab2.setOnClickListener(this);
        fragmentContainer = new ArrayList<Fragment>();
        Fragment1 fragment1 = new Fragment1();
        Fragment2 fragment2 = new Fragment2();
        fragment3 = new PascWebFragment();
        Bundle bundle = new Bundle();
        WebStrategy webStrategy = new WebStrategy();
        webStrategy.url="https://www.sina.com.cn";
        webStrategy.toolBarVisibility = WebStrategyType.TOOLBAR_GONE;
        webStrategy.isHideProgressBar = WebStrategyType.PROGRESS_HIDE;
        webStrategy.statusBarVisibility = WebStrategyType.STATUSBAR_GONE;
        webStrategy.mainPageModule = WebStrategyType.MAINPAGE;
        bundle.putSerializable("extra_strategy", webStrategy);
        fragment3.setArguments(bundle);
        fragmentContainer.add(fragment1);
        fragmentContainer.add(fragment2);
        fragmentContainer.add(fragment3);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {

                return fragmentContainer.size();
            }

            @Override
            public Fragment getItem(int position) {
                return fragmentContainer.get(position);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position);
                if (position == 0) {
                    tvTab1.setTextColor(getResources().getColor(R.color.colorAccent));
                    tvTab2.setTextColor(getResources().getColor(R.color.black_333333));
                } else {
                    tvTab1.setTextColor(getResources().getColor(R.color.black_333333));
                    tvTab2.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission.requestEach(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(Permission permission) throws Exception {
                if (permission.granted) {
                    // 用户已经同意该权限
                    Locator.doLocation(mContext, false);
                } else if (permission.shouldShowRequestPermissionRationale) {
                    // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                } else {
                    // 用户拒绝了该权限，并且选中『不再询问』
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        Disposable disposable = null;
        switch (v.getId()) {
            case R.id.tv_tab_1:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_tab_2:
                viewPager.setCurrentItem(1);
                break;
            case R.id.tv1:
                disposable = OpenBiz.getServiceInfo("av8eZ4pK81cTQxsO72ZcRR1vMxwaZxc0").subscribe(new Consumer<ServiceInfoResp>() {
                    @Override
                    public void accept(ServiceInfoResp infoResp) throws Exception {
                        Log.e("", "");
                        toast(" appKey " + infoResp.appKey + " token =" + infoResp.token, 200);
                    }
                }, new BaseRespThrowableObserver() {
                    @Override
                    public void onError(int code, String msg) {
                        Log.e("", "");
                        toast(msg, code);
                    }
                });
                break;
            case R.id.tv2:
                disposable = OpenBiz.checkCode("av8eZ4pK81cTQxsO72ZcRR1vMxwaZxc0", "353c849047cf433e8d1bca5a49582e6c").subscribe(new Consumer<CheckInitCodeResp>() {
                    @Override
                    public void accept(CheckInitCodeResp infoResp) throws Exception {
                        Log.e("", "");
                        toast(" verifyResult= " + infoResp.verifyResult, 200);
                    }
                }, new BaseRespThrowableObserver() {
                    @Override
                    public void onError(int code, String msg) {
                        Log.e("", "");
                        toast(msg, code);
                    }
                });
                break;
            case R.id.tv3:
                disposable = OpenBiz.getOpenId("av8eZ4pK81cTQxsO72ZcRR1vMxwaZxc0", "").subscribe(new Consumer<OpenIdResp>() {
                    @Override
                    public void accept(OpenIdResp infoResp) throws Exception {
                        toast(" openId= " + infoResp.openId, 200);
                    }
                }, new BaseRespThrowableObserver() {
                    @Override
                    public void onError(int code, String msg) {
                        toast(msg, code);
                    }
                });
                break;
            case R.id.tv4:
                disposable = OpenBiz.getResquestCode("av8eZ4pK81cTQxsO72ZcRR1vMxwaZxc0", "").subscribe(new Consumer<RequestCodeResp>() {
                    @Override
                    public void accept(RequestCodeResp infoResp) throws Exception {
                        Log.e("", "");
                        toast(" requestCode= " + infoResp.requestCode, 200);
                    }
                }, new BaseRespThrowableObserver() {
                    @Override
                    public void onError(int code, String msg) {
                        toast(msg, code);

                    }
                });

                break;
            case R.id.tv5:
                disposable = OpenBiz.getServiceStatus("av8eZ4pK81cTQxsO72ZcRR1vMxwaZxc0", "").subscribe(new Consumer<ServiceStatusResp>() {
                    @Override
                    public void accept(ServiceStatusResp infoResp) throws Exception {
                        Log.e("", "");
                        toast(" authorizationStatus= " + infoResp.authorizationStatus, 200);
                    }
                }, new BaseRespThrowableObserver() {
                    @Override
                    public void onError(int code, String msg) {
                        toast(msg, code);

                    }
                });

                break;
        }
        if (disposable != null) {
            disposables.add(disposable);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int index = intent.getIntExtra(TO_TAB_INDEX, 0);
        switch (index) {
            case 0:
                viewPager.setCurrentItem(0);
                break;
            case 1:
                viewPager.setCurrentItem(1);
                break;
            default:
                viewPager.setCurrentItem(0);
                break;
        }
    }

    private void toast(final String msg, final int code) {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, " code " + code + " msg =" + msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    @Override
    public PascWebFragment getPascWebFragment() {
        return fragment3;
    }

    @Override
    public PascWebView getPascWebView() {
        if (fragment3 instanceof PascWebFragment) {
            return ((PascWebFragment) fragment3).mWebView;
        }
        return null;
    }
}
