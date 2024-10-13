package com.pasc.libbrowser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.example.chenkun305.libbrowser.R;
import com.google.gson.Gson;

import com.pasc.libbrowser.fragment.ImageFragment;
import com.pasc.libbrowser.view.NoScrollViewPager;
import java.util.ArrayList;
import java.util.List;


/**
 * 大图浏览页面
 */
public class BigPhotoActivity extends AppCompatActivity {

  NoScrollViewPager loopinVpPhoto;
  TextView	loopinTvPhotoIndicator;

  private ArrayList<String> listPhotos = new ArrayList<>();
  private int mCurIndex; //查看图片的下标

  public static void start(Context context, ArrayList<String> urls, int index) {
    Intent intent = new Intent(context, BigPhotoActivity.class);
    intent.putStringArrayListExtra("photos", urls);
    intent.putExtra("index", index);
    context.startActivity(intent);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_preview_big_photos);
    loopinVpPhoto= (NoScrollViewPager) findViewById(R.id.vp_photo);
    loopinTvPhotoIndicator= (TextView) findViewById(R.id.tv_photo_indicator);
    //        LoopinApp.getInstance().showProgressDialog(mContext, false, "");
    Intent intent = getIntent();
    String urls = "";
    Gson gson = new Gson();
    //if (intent.getData() != null) {
    //    urls = intent.getData().getQueryParameter("urls");
    //    mCurIndex = Integer.parseInt(intent.getData().getQueryParameter("index"));
    //
    //} else {
    //    mCurIndex = intent.getIntExtra("index", 0);
    //    urls = intent.getStringExtra("photos");
    //}
    mCurIndex = intent.getIntExtra("index", 0);
    listPhotos = intent.getStringArrayListExtra("photos");
    if (listPhotos == null) {
      return;
    }
    //listPhotos = gson.fromJson(urls, new TypeToken<List<String>>() {
    //}.getType());

    ImageViewPagerAdapter adapter =
        new ImageViewPagerAdapter(getSupportFragmentManager(), listPhotos);
    //        PicPlayerAdapter adapter = new PicPlayerAdapter(listPhotos);
    loopinVpPhoto.setAdapter(adapter);
    loopinVpPhoto.setCurrentItem(mCurIndex);
    loopinVpPhoto.addOnPageChangeListener(mOnPageChangeListener);
    showPicIndicator(mCurIndex);
  }

  /**
   * 显示大图预览时的图片引导
   */
  private void showPicIndicator(int index) {
    if (listPhotos.size() > 1 && index >= 0) {
      String indicator = (index + 1) + "/" + listPhotos.size();
      loopinTvPhotoIndicator.setText(indicator);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void onStop() {

    super.onStop();
  }

  @Override
  public void onDestroy() {

    super.onDestroy();
  }

  ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
      mCurIndex = position;
      showPicIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
  };

  class ImageViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final String IMAGE_URL = "image";

    List<String> mDatas;

    public ImageViewPagerAdapter(FragmentManager fm, List data) {
      super(fm);
      mDatas = data;
    }

    @Override
    public Fragment getItem(int position) {
      String url = mDatas.get(position);
      Fragment fragment = ImageFragment.newInstance(url, null);
      return fragment;
    }

    @Override
    public int getCount() {
      return mDatas == null ? 0 : mDatas.size();
    }
  }
}
