package com.android.netperf_new;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.netperf_new.model.PingerItem;

/**
 * Created by Peace on 7/17/2017.
 */

public class MyWebViewClient extends WebViewClient {

    private Context mContext;

    public long startTime;
    public long doneTime;
    public long loadTime;

    public PingerItem thisItem;

    public interface OnPageLoadComplete{
        void onPageLoaded(PingerItem item);

    }

    public MyWebViewClient(Context contxt, PingerItem p_item) {
        this.mContext = contxt;
        this.thisItem = p_item;
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        view.loadUrl(url);

        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
//                progDailog.show(getContext(), "Loading","Please wait...", true);
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPageFinished(WebView view, final String url) {
        doneTime = System.currentTimeMillis();
//                progDailog.dismiss();

        loadTime = doneTime - startTime;
        thisItem.loadTime = loadTime;


//        OnPageLoadComplete pageCompleteListener = (OnPageLoadComplete) getClass();
//        deviceListFragListener.onDeviceItemClickListener(clickedDevice);
//                Toast.makeText(getContext(), "Page loaded in: " + loadTime + " milliseconds", Toast.LENGTH_SHORT).show();
    }
}
