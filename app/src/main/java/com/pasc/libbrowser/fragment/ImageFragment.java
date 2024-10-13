package com.pasc.libbrowser.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.chenkun305.libbrowser.R;
import com.pasc.lib.smtbrowser.view.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import java.io.File;
import java.io.IOException;

public class ImageFragment extends Fragment {

  private Context mContext;
  private static final String IMAGE_URL = "image";
  PhotoView image;
  private String imageUrl;
  private String indicator = "";

  private Bitmap mBitmap;

  public ImageFragment() {
    // Required empty public constructor
  }

  public static ImageFragment newInstance(String... param) {
    ImageFragment fragment = new ImageFragment();
    Bundle args = new Bundle();
    args.putString(IMAGE_URL, param[0]);
    args.putString("indicator", param[1]);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = getActivity();

    if (getArguments() != null) {
      imageUrl = getArguments().getString(IMAGE_URL);
      indicator = getArguments().getString("indicator");
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_photo_preview_page, container, false);
    image = (PhotoView) view.findViewById(R.id.photo_view);
    image.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        getActivity().finish();
      }
    });

    if (!TextUtils.isEmpty(imageUrl)) {


    }
    return view;
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
  }

  @Override public void onDetach() {
    super.onDetach();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
  }

  private Transformation transformation = new Transformation() {

    @Override
    public Bitmap transform(Bitmap source) {

      int targetWidth = image.getWidth();
      Log.i("wjn","source.getHeight()="+source.getHeight()+",source.getWidth()="+source.getWidth()+",targetWidth="+targetWidth);

      if(source.getWidth()==0){
        return source;
      }

      //如果图片小于设置的宽度，则返回原图
      if(source.getWidth()<targetWidth){
        return source;
      }else{
        //如果图片大小大于等于设置的宽度，则按照设置的宽度比例来缩放
        double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
        int targetHeight = (int) (targetWidth * aspectRatio);
        if (targetHeight != 0 && targetWidth != 0) {
          Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
          if (result != source) {
            // Same bitmap is returned if sizes are the same
            source.recycle();
          }
          return result;
        } else {
          return source;
        }
      }

    }

    @Override
    public String key() {
      return "transformation" + " desiredWidth";
    }
  };


}
