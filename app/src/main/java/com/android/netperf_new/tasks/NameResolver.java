package com.android.netperf_new.tasks;

import android.util.Log;

import com.android.netperf_new.model.PingerItem;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Peace on 7/16/2017.
 */

public class NameResolver implements Runnable {

    private String hostname;
    final ArrayList<PingerItem> items;

    public NameResolver(String hostname, ArrayList<PingerItem> items) {
        this.hostname = hostname;
        this.items = items;
    }

    public void run() {
        Log.v("multiping", "NameResolver " + hostname);
//			InetAddress ia;
        try {
            InetAddress inetAddress = InetAddress.getByName(hostname);

            for (PingerItem pingerItem: items) {
                if(pingerItem.hostname.equals(hostname)){
                    Log.v("multiping", "NameResolver " + hostname + " resolved:" + inetAddress);
                    pingerItem.ia = inetAddress;
                    break;
                }
            }
//				for(int i=0; i<items.size(); i++)
//				{
//					PingerItem pi = items.get(i);
//					if(pi.hostname.equals(hostname)) {
//						Log.v("multiping","NameResolver "+hostname + " resolved:" + ia);
//						pi.ia = ia;
//						items.set(i,pi);
//					}
//				}
        } catch (UnknownHostException e) {
            //FIXME: We should not hide exceptions, either print or handle ;-)
        } catch (Exception e) {
            //FIXME: We should not hide exceptions, either print or handle ;-)
        }
    }
}
