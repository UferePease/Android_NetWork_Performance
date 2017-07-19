package com.android.netperf_new.model;

import java.net.InetAddress;

/**
 * Created by Peace on 7/16/2017.
 */

public class PingerItem {

    final static int MAXTIME = 100000;

    public String hostname;
    public InetAddress ia;
    public long result_80; // connect 80
    public long result_av; // isAvailable
    public long loadTime;

    public PingerItem(){

        this.hostname = null;
        this.ia = null;
        this.loadTime = 0;

        this.result_80 = MAXTIME;
        this.result_av = MAXTIME;

    }
}
