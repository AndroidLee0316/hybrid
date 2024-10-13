package com.example.chenkun305.libbrowser;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import android.util.Log;
import com.pasc.lib.hybrid.util.Utils;
import java.net.URISyntaxException;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
  @Test public void useAppContext() throws URISyntaxException {
        // Context of the app under test.
    String url =
            "http://tyrztest.gdbs.gov.cn/am/oauth2/authorize?service=initService&response_type=code&scope=all&openweb=paschybrid&client_id=gdbs_66&client_secret=123qwe&redirect_uri=http%3A%2F%2Fisz-cloud.yun.city.pingan.com%2Fsmtapp%2Findex.jsp";
    String result = Utils.getDeleteParamUri(url);
    //System.out.print(result);
    Log.i("wjn", "result: " + result);
  }


}
