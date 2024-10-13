package com.pasc.lib.hybrid;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;


/**
 * fragment基类
 */
public class BaseFragment extends Fragment {
    protected String TAG = BaseFragment.class.getSimpleName();
    protected Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }
}
