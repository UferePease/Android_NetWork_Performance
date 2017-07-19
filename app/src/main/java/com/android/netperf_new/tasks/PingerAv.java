package com.android.netperf_new.tasks;

import android.util.Log;

import com.android.netperf_new.model.PingerItem;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by Peace on 7/16/2017.
 */

public class PingerAv implements Runnable {

    final static int TIMEOUT = 3000;

    final ArrayList<PingerItem> items;

    private InetAddress ia;

    public PingerAv(InetAddress ia, ArrayList<PingerItem> items) {
        this.ia = ia;
        this.items = items;
    }

    public void run() {
        Log.v("multiping", "PingerAv " + ia + "start" );
        // Try port 7
        for(PingerItem pingerItem: items)
        {
            try {
                if(pingerItem.ia.equals(ia))
                {
                    long t1 = System.nanoTime();
                    try {
                        if(pingerItem.ia.isReachable(TIMEOUT))
                        {
                            long t2 = System.nanoTime();
                            long dt = (t2 - t1) / 1000000;

//								pi = items.get(i);
                            pingerItem.result_av = dt;
//								items.set(i, pi);
                            Log.v("multiping", "PingerAv "+pingerItem.hostname + " " + pingerItem.result_av);
                        }
                        else
                        {
//								pi = items.get(i);
                            pingerItem.result_av = TIMEOUT;
//								items.set(i, pi);
                            Log.v("multiping", "PingerAv TIMEOUT "+pingerItem.hostname + " " + pingerItem.result_av);
                        }
                    } catch (IOException e) {
//							pi = items.get(i);
                        pingerItem.result_av = TIMEOUT;
//							items.set(i, pi);
                        Log.v("multiping","PingerAv " + e.toString());
                    }
                }
            } catch (Exception e) {
                Log.v("multiping","PingerAv " + e.toString());
            }
        }
        Log.v("multiping", "PingerAv " + ia + "end" );
    }
}
