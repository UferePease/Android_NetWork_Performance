package com.android.netperf_new.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.telephony.CellLocation;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.netperf_new.R;
import com.android.netperf_new.dbhelper.DataBaseHelper2;
import com.android.netperf_new.model.PingerItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Peace on 7/16/2017.
 */

public class PingItemAdapter extends ArrayAdapter<PingerItem> {
    private Context context;

    final static int MAXTIME = 100000;
    final static int TIMEOUT = 3000;

    final List<PingerItem> items;

    DataBaseHelper2 myDbHelper1;

    public PingItemAdapter(Context context, List<PingerItem> items) {
        super(context, R.layout.pingitem, items);

        this.context=context;
        this.items = items;

        myDbHelper1 = new DataBaseHelper2(getContext());
    }


    private class ViewHolder{

        TextView hostip = null;
        TextView delay = null;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PingerItem getItem(int position) {

        return items.get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }


//    private class ViewWrapper {
//        View base;
//        TextView hostip=null;
//        TextView delay=null;
//
//        ViewWrapper(View base) {
//            this.base = base;
//        }
//
//        TextView getViewHostIp() {
//            if (hostip==null) {
//                hostip=(TextView)base.findViewById(R.id.hostip);
//            }
//            return hostip;
//        }
//
//        TextView getViewDelay() {
//            if (delay==null) {
//                delay=(TextView)base.findViewById(R.id.delay);
//            }
//            return delay;
//        }
//    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder = null;

        View row = convertView;
        ViewHolder wrapper = null;

        if(row==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//            LayoutInflater inflater = context.getLayoutInflater();
            row=inflater.inflate(R.layout.pingitem, null);
            wrapper = new ViewHolder();

            wrapper.hostip = (TextView) row.findViewById(R.id.hostip);
            wrapper.delay = (TextView) row.findViewById(R.id.delay);

            row.setTag(wrapper);
        }
        else {
            wrapper = (ViewHolder) row.getTag();
        }



        int textcolor;
        String textresult;
        PingerItem pi = items.get(position);

        long result;

        result = (pi.result_80 > pi.result_av) ? pi.result_av : pi.result_80;
//			TODO: this is a cleaner way to do a switch statement
//			if(pi.result_80>pi.result_av)
//				result=pi.result_av;
//			else
//				result=pi.result_80;

        if(result >= MAXTIME) {
            textcolor = Color.GRAY;
            textresult = "wait..";
        } else if (result >= TIMEOUT) {
            textcolor = Color.RED;
            textresult = "timeout";
        } else {
            textcolor = Color.WHITE;
            textresult = result + "ms";
        }


        String sIp = (pi.ia == null) ? "0.0.0.0" : pi.ia.toString().replaceFirst(".*/", "");
//			if(pi.ia==null)
//				sIp = "0.0.0.0";
//			else
//				sIp = pi.ia.toString().replaceFirst(".*/", "");


        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar calobj = Calendar.getInstance();
        System.out.println(df.format(calobj.getTime()));

        //Long timeEntry = System.currentTimeMillis();
        String timeEntry = df.format(calobj.getTime());
        String hostNameEntry = pi.hostname;
        //String ipAddressEntry = pi.ia.toString();
        String ipAddressEntry = sIp;
        Long delayEntry = result;
        Long tcp80Entry = pi.result_80;
        Long tcpAVEntry = pi.result_av;


        TelephonyManager telephonyManager;
        CellLocation cellLocation;
        int signalStrength;
        ServiceState serviceState;

        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        //GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;

        GsmCellLocation gsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();

        int cellIDEntry = gsmCellLocation.getCid();
        int locationEntry = gsmCellLocation.getLac();
        int signalEntry = telephonyManager.getDataState();
        String carrierEntry = telephonyManager.getSimOperatorName();

        final String networkOperator = telephonyManager.getNetworkOperator();
        final int mccEntry = Integer.parseInt(networkOperator.substring(0, 3));
        final int mncEntry = Integer.parseInt(networkOperator.substring(3));
        int netTypeEntry = telephonyManager.getNetworkType();
        //https://developer.android.com/reference/android/telephony/TelephonyManager.html#NETWORK_TYPE_HSPA



        SQLiteDatabase db = myDbHelper1.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_TIME, timeEntry);
        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_HOST, hostNameEntry);
        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_IP, ipAddressEntry);
        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_DELAY, delayEntry);
        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_TCP80, tcp80Entry);
        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_TCPAV, tcpAVEntry);
        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_CELLID, cellIDEntry);
        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_LOCATION, locationEntry);
        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_CARRIERNAME, carrierEntry);
        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_SIGNAL, signalEntry);
        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_MCC, mccEntry);
        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_MNC, mncEntry);
        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_TYPE, netTypeEntry);

        db.insert(DataBaseHelper2.FeedEntry.TABLE_NAME, null, values);

        db.close();

        Log.i(TAG, "Got the following data:");
        Log.i(TAG, "CellId: " + cellIDEntry);
        Log.i(TAG, "LAC: " + locationEntry);
        Log.i(TAG, "MNC: " + mncEntry);
        Log.i(TAG, "MCC: " + mccEntry);
        Log.i(TAG, "Signal strength: " + signalEntry);
        Log.i(TAG, "Carrier: " + carrierEntry);
        Log.i(TAG, "Network type: " + netTypeEntry);

        wrapper.hostip.setTextColor(textcolor);
        wrapper.hostip.setText(pi.hostname + "\n" + sIp);
        wrapper.delay.setTextColor(textcolor);
        wrapper.delay.setText(textresult);
        return row;




    }






//    public View getView(int position, View convertView, ViewGroup parent) {
//        View row=convertView;
//        ViewWrapper wrapper=null;
//
//        if(row==null) {
//            LayoutInflater inflater = context.getLayoutInflater();
//            row=inflater.inflate(R.layout.pingitem, null);
//            wrapper=new ViewWrapper(row);
//            row.setTag(wrapper);
//        }
//        else {
//            wrapper=(ViewWrapper)row.getTag();
//        }
//
//        int textcolor;
//        String textresult;
//        PingerItem pi = items.get(position);
//
//        long result;
//
//        result = (pi.result_80>pi.result_av)? pi.result_av : pi.result_80;
////			TODO: this is a cleaner way to do a switch statement
////			if(pi.result_80>pi.result_av)
////				result=pi.result_av;
////			else
////				result=pi.result_80;
//
//        if(result>=MAXTIME) {
//            textcolor = Color.GRAY;
//            textresult = "wait..";
//        } else if (result>=TIMEOUT) {
//            textcolor = Color.RED;
//            textresult = "timeout";
//        } else {
//            textcolor = Color.WHITE;
//            textresult = result + "ms";
//        }
//
//
//        String sIp = (pi.ia==null)? "0.0.0.0": pi.ia.toString().replaceFirst(".*/", "");
////			if(pi.ia==null)
////				sIp = "0.0.0.0";
////			else
////				sIp = pi.ia.toString().replaceFirst(".*/", "");
//
//
//        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
//        Calendar calobj = Calendar.getInstance();
//        System.out.println(df.format(calobj.getTime()));
//
//        //Long timeEntry = System.currentTimeMillis();
//        String timeEntry = df.format(calobj.getTime());
//        String hostNameEntry = pi.hostname;
//        //String ipAddressEntry = pi.ia.toString();
//        String ipAddressEntry = sIp;
//        Long delayEntry = result;
//        Long tcp80Entry = pi.result_80;
//        Long tcpAVEntry = pi.result_av;
//
//
//        TelephonyManager telephonyManager;
//        CellLocation cellLocation;
//        int signalStrength;
//        ServiceState serviceState;
//
//        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//
//        //GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
//
//        GsmCellLocation gsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
//
//        int cellIDEntry = gsmCellLocation.getCid();
//        int locationEntry = gsmCellLocation.getLac();
//        int signalEntry = telephonyManager.getDataState();
//        String carrierEntry = telephonyManager.getSimOperatorName();
//
//        final String networkOperator = telephonyManager.getNetworkOperator();
//        final int mccEntry = Integer.parseInt(networkOperator.substring(0, 3));
//        final int mncEntry = Integer.parseInt(networkOperator.substring(3));
//        int netTypeEntry = telephonyManager.getNetworkType();
//        //https://developer.android.com/reference/android/telephony/TelephonyManager.html#NETWORK_TYPE_HSPA
//
//
//
//        SQLiteDatabase db = myDbHelper1.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//
//        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_TIME, timeEntry);
//        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_HOST, hostNameEntry);
//        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_IP, ipAddressEntry);
//        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_DELAY, delayEntry);
//        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_TCP80, tcp80Entry);
//        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_TCPAV, tcpAVEntry);
//        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_CELLID, cellIDEntry);
//        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_LOCATION, locationEntry);
//        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_CARRIERNAME, carrierEntry);
//        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_SIGNAL, signalEntry);
//        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_MCC, mccEntry);
//        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_MNC, mncEntry);
//        values.put(DataBaseHelper2.FeedEntry.COLUMN_NAME_TYPE, netTypeEntry);
//
//        db.insert(DataBaseHelper2.FeedEntry.TABLE_NAME, null, values);
//
//        db.close();
//
//        Log.i(TAG, "Got the following data:");
//        Log.i(TAG, "CellId: " + cellIDEntry);
//        Log.i(TAG, "LAC: " + locationEntry);
//        Log.i(TAG, "MNC: " + mncEntry);
//        Log.i(TAG, "MCC: " + mccEntry);
//        Log.i(TAG, "Signal strength: " + signalEntry);
//        Log.i(TAG, "Carrier: " + carrierEntry);
//        Log.i(TAG, "Network type: " + netTypeEntry);
//
//        wrapper.getViewHostIp().setTextColor(textcolor);
//        wrapper.getViewHostIp().setText(pi.hostname + "\n" + sIp);
//        wrapper.getViewDelay().setTextColor(textcolor);
//        wrapper.getViewDelay().setText(textresult);
//        return row;
//    }







    @Override
    public void add(PingerItem item) {
        items.add(item);
        notifyDataSetChanged();
        super.add(item);
    }

    @Override
    public void remove(PingerItem item) {
        items.remove(item);
        notifyDataSetChanged();
        super.remove(item);
    }



}
