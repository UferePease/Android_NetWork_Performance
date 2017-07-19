package com.android.netperf_new.tasks;

import android.util.Log;

import com.android.netperf_new.model.PingerItem;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Peace on 7/16/2017.
 */

public class Pinger80 implements Runnable {

    final static int TIMEOUT = 3000;

    final ArrayList<PingerItem> items;

    private InetAddress ia;

    public Pinger80(InetAddress ia, ArrayList<PingerItem> items) {
        this.ia = ia;
        this.items = items;
    }

    public void run() {
        Log.v("multiping", "Pinger80 " + ia + "start" );
        long t1 = System.nanoTime();
        // Try port 80
        try {
            long dt = TIMEOUT;
            try {
                Socket socket = new Socket(ia, 80);
                long t2 = System.nanoTime();
                dt = (t2 - t1) / 1000000;
                socket.close();
            } catch (IOException e) {
                Log.v("multiping", "Pinger80 " + e.toString());
            }

            for(PingerItem pingerItem: items)
            {
                if(pingerItem.ia.equals(ia))
                {
                    pingerItem.result_80 = dt;
//						items.set(i,pi);
                    Log.v("multiping", "Pinger80 " + pingerItem.hostname + " " + dt);
                }
            }

        } catch (Exception e) {
            Log.v("multiping", "Pinger80 " + e.toString());
        }
        Log.v("multiping", "Pinger80 " + ia + "end" );
    }
}
