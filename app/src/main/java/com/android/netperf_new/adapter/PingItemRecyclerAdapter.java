package com.android.netperf_new.adapter;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.telephony.CellLocation;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.android.netperf_new.MyWebViewClient;
import com.android.netperf_new.R;
import com.android.netperf_new.dbhelper.DataBaseHelper2;
import com.android.netperf_new.model.PingerItem;
import com.android.netperf_new.utils.ColorUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Peace on 7/17/2017.
 */

public class PingItemRecyclerAdapter extends RecyclerView.Adapter<PingItemRecyclerAdapter.PingViewHolder> {

    final static int MAXTIME = 100000;
    final static int TIMEOUT = 3000;

    DataBaseHelper2 myDbHelper1;

    private int numOfPings;
    private static int viewHolderCount;

    private final String TAG = "Ping_Adapter";

    private Context mContext;
    private List<PingerItem> items;

    //a member variable to store a reference to the click listener
    private PingerItemClickListener mOnClickListener;

    WebView webView;
    private ProgressDialog progDailog;

    long doneTime;
    long startTime;

    long loadTime;


    public interface PingerItemClickListener{
        void onPingerItemClick(View itemView, int clickedItemIndex);
    }

    public void setOnItemClickListener(PingerItemClickListener listener){
        this.mOnClickListener = listener;
    }


    public PingItemRecyclerAdapter(Context context, List<PingerItem> availitems, WebView web, PingerItemClickListener listener){
        items = availitems;
        mContext = context;

        mOnClickListener = listener;
        viewHolderCount = 0;
        myDbHelper1 = new DataBaseHelper2(getContext());
        webView = web;
    }

    private Context getContext(){
        return mContext;
    }

    @Override
    public PingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.pingitem;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        //inflate the custom layout
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        //return a new viewholder instance
        PingViewHolder viewHolder = new PingViewHolder(view);

//        viewHolder.viewHolderIndex.setText("ViewHolder index: " + viewHolderCount);


        //this to enable color banded rows
        int colorPosition = viewHolderCount % 2;
        int backgroundColorForViewHolder = ColorUtils.getViewHolderBackgroundColorFromInstance(context, colorPosition);
        viewHolder.itemView.setBackgroundColor(backgroundColorForViewHolder);

        viewHolderCount++;

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(PingViewHolder holder, int position) {
        //get the data model based on position
        PingerItem item = items.get(position);

        //set item views based on the views and data model
        TextView hostipView = holder.hostip;
        TextView delayView = holder.delay;
        TextView pageloadtimeview = holder.pageloadview;


//        hostipView.setText(item.hostname);
//        delayView.setText(item.);

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

        telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

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

        hostipView.setTextColor(textcolor);
        hostipView.setText(pi.hostname + "\n" + sIp);
        delayView.setTextColor(textcolor);
        delayView.setText(textresult);





//        webView.clearCache(false);
//
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//
//        // Set WebView client
//        webView.setWebChromeClient(new WebChromeClient());
//
//        MyWebViewClient webViewClient = new MyWebViewClient(getContext(), pi);
//
//        webView.setWebViewClient(webViewClient);


//        loadWebPage(pi);

        pageloadtimeview.setText((pi.loadTime) + " ms");
//        Toast.makeText(mContext, "load time: " + (pi.loadTime / 1000) + " secs", Toast.LENGTH_SHORT).show();

//        holder.bind(position);
    }

    //return the total count of items in the list
    @Override
    public int getItemCount() {
        return items.size();
    }



    public void loadWebPage(final PingerItem ping){

//        progDailog = ProgressDialog.show(getContext(), "Loading","Please wait...", true);
//        progDailog.setCancelable(false);

        webView.clearCache(false);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        // Set WebView client
        webView.setWebChromeClient(new WebChromeClient(){});

        webView.setWebViewClient(new WebViewClient(){

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
                ping.loadTime = loadTime;


//                Toast.makeText(getContext(), "Page loaded in: " + loadTime + " milliseconds", Toast.LENGTH_SHORT).show();
            }
        });

        webView.loadUrl("http://" + ping.hostname);


    }







    class PingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView hostip;
        public TextView delay;
        public TextView pageloadview;


        public PingViewHolder (View itemView){
            super(itemView);

            hostip = (TextView) itemView.findViewById(R.id.hostip);
            delay = (TextView) itemView.findViewById(R.id.delay);
            pageloadview = (TextView) itemView.findViewById(R.id.page_load_time);


            itemView.setOnClickListener(this);
        }

        void bind(int listIndex){
            hostip.setText(String.valueOf(listIndex));
        }

        @Override
        public void onClick(View view) {
            if (mOnClickListener != null){
                int clickedPosition = getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION){
                    mOnClickListener.onPingerItemClick(itemView, clickedPosition);
                }
            }

//            mOnClickListener.onDeviceItemClick(clickedPosition);
        }
    }
}
