package com.android.netperf_new.utils;

import android.nfc.Tag;
import android.util.Log;
import android.webkit.WebChromeClient;

import com.android.netperf_new.RTT_LoadTimeFragment;
import com.android.netperf_new.model.PingerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.android.netperf_new.RTT_LoadTimeFragment.items;
import static com.android.netperf_new.RTT_LoadTimeFragment.webView;

/**
 * Created by Peace on 7/18/2017.
 */

public class PageLoader {
    static final String TAG = "PageLoader_Log";

    public static int currentUrl;

    public static List<String> urlsToLoad;
    public static ArrayList<PingerItem> pingitems = items;

    public static List<Long> loadTimes;

    long initialLoadTime = 0;

    public PageLoader(){
        populate();
        loadFirstUrl();
    }


    public void populate() {
        currentUrl = 0;
        urlsToLoad = new ArrayList<String>();
        loadTimes = new ArrayList<Long>();

        for (PingerItem pingerItem: pingitems) {
            urlsToLoad.add("http://" + pingerItem.hostname);
        }
    }

    public static void loadNextUrl(){
        if (currentUrl <= urlsToLoad.size() - 2){
            currentUrl++;
            Log.d(TAG, "We are currently loading item number: " + currentUrl);

            webView.clearCache(true);

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);

            webView.getSettings().setDomStorageEnabled(true);

            webView.loadUrl("http://" + pingitems.get(currentUrl).hostname);

        }

//        class SayHello extends TimerTask {
//
//            public void run() {
//                if (currentUrl <= urlsToLoad.size() - 2){
//                    currentUrl++;
//                    webView.loadUrl("http://" + pingitems.get(currentUrl).hostname);
//
//                }
//            }
//        }
//        Timer timer = new Timer();
//        timer.schedule(new SayHello(), 0, 5000);

    }

    public void loadFirstUrl(){

        // Set WebView client

        loadTimes.add(0, initialLoadTime);

        webView.setWebChromeClient(new WebChromeClient());

        webView.loadUrl("http://" + pingitems.get(currentUrl).hostname);
    }

}
