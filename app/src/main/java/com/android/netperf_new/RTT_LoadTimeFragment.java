package com.android.netperf_new;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.netperf_new.adapter.PingItemRecyclerAdapter;
import com.android.netperf_new.dbhelper.DataBaseHelper2;
import com.android.netperf_new.model.PingerItem;
import com.android.netperf_new.tasks.NameResolver;
import com.android.netperf_new.tasks.Pinger80;
import com.android.netperf_new.tasks.PingerAv;
import com.android.netperf_new.utils.PageLoader;

import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class RTT_LoadTimeFragment extends Fragment implements PingItemRecyclerAdapter.PingerItemClickListener {

    private static final String LOG_TAG = "multiping";
    private final static String SAVEFILE="hosts";

    final static long PERIOD = 5000;
    final static long UPDATE = 300000;

    Thread m_background=null;
    public static boolean isRunning=false;

//    PingItemAdapter pia;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView myTextView;
    private TextView myTextView1;

    public static ArrayList<PingerItem> items;

    private OnFragmentInteractionListener mListener;

    DataBaseHelper2 myDbHelper1;

    public static WebView webView;
    private ProgressDialog progDailog;

    private PingItemRecyclerAdapter pingAdapter;
    private RecyclerView pingersRecyclerView;




    public android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            String localIp = getLocalIpAddress();
            if (localIp == null) localIp = "unknown";
            myTextView.setText("FYI: local IP : " + localIp);

//            pia.notifyDataSetChanged();
            pingAdapter.notifyDataSetChanged();
        }
    };

    public RTT_LoadTimeFragment() {
        // Required empty public constructor
    }



    public static RTT_LoadTimeFragment newInstance(String param1, String param2) {
        RTT_LoadTimeFragment fragment = new RTT_LoadTimeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment1_content_scrolling, container, false);


        //USING LISTVIEW//////////////////////////////////////////////
//        myTextView1 = (TextView) view.findViewById(R.id.myTextView1);
        myTextView = (TextView) view.findViewById(R.id.myTextView);

        webView = (WebView) view.findViewById(R.id.myWebView);
//        final ListView myListView = (ListView) view.findViewById(R.id.myListView);
//        final NonScrollListView myListView = (NonScrollListView) view.findViewById(R.id.myListView);
        final Button myButton = (Button) view.findViewById(R.id.myButton);
//        final Button myButton1 = (Button) view.findViewById(R.id.myButton1);

        String localIp = getLocalIpAddress();
        if (localIp == null) localIp = "unknown";
        myTextView.setText("FYI: local IP = " + localIp);

        items = new ArrayList<PingerItem>();

        //////////////
//        pia = new PingItemAdapter(getContext(), items);
//        myListView.setAdapter(pia);

        //saveItems();

        //mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), "600vyw18i0kdkuc", "jkfqwlhgmhdpfci");
        myDbHelper1 = new DataBaseHelper2(getContext());

        //myNetworkMetric = new RadioData(this);

//		try {
//			myDbHelper.createDataBase();
//		} catch (IOException ioe) {
//			throw new Error("Unable to create database");
//		}
//
//		try {
//			myDbHelper.openDataBase();
//		} catch (SQLException sqle) {
//			throw sqle;
//		}
        //////////////////////////////////////////////////////

        pingersRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        pingersRecyclerView.setHasFixedSize(true);

        //initialize camapign devices
//        availableDevices = CampaignDevice.createCampaignDevicesList(NUM_LIST_ITEMS);
        //create adapter passing in the sample user data
        pingAdapter = new PingItemRecyclerAdapter(getContext(), items, webView, this);
        //attach the adapter to the recyclerview to populate items
        pingersRecyclerView.setAdapter(pingAdapter);

        //set layout manager to position the items
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        pingersRecyclerView.setLayoutManager(layoutManager);

//        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        devicesRecyclerView.addItemDecoration(itemDecoration);

//        pingersRecyclerView.setItemAnimator(new SlideInUpAnimator());


        saveItems();

        pingAdapter.setOnItemClickListener(this);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        m_background = new Thread(new Runnable() {
            public void run() {
                while(isRunning)
                {
                    for(PingerItem pingerItem: items){
                        if(pingerItem.ia != null) {
                            Thread t1 = new Thread(new Pinger80(pingerItem.ia, items));
                            t1.setName("Pinger80 " + pingerItem.hostname);
                            t1.start();
                            Thread t2 = new Thread(new PingerAv(pingerItem.ia, items));
                            t2.setName("PingerAv " + pingerItem.hostname);
                            t2.start();
                        }
                    }

//    				for(int i=0; i<items.size(); i++) {
//    					PingerItem pi = items.get(i);
//    					if(pi.ia!=null) {
////    						pi.result_80 = -1;
////    						pi.result_av = -1;
////    						items.set(i,pi);
//    						Thread t1 = new Thread(new Pinger80(pi.ia));
//    						t1.setName("Pinger80 " + pi.hostname);
//    						t1.start();
//    						Thread t2 = new Thread(new PingerAv(pi.ia));
//    						t2.setName("PingerAv " + pi.hostname);
//    						t2.start();
//    					}
//    				}

                    try {
                        for(int to = 0; to < PERIOD; to+=UPDATE) {
                            Thread.sleep(UPDATE);
                            handler.sendMessage(handler.obtainMessage());
                        }
                    } catch (InterruptedException e) {
                        Log.v("multiping", "InterruptedException");
                        break;
                    }
                } // end of while
            }
        });

        isRunning = true;
        m_background.start();



        loadWebPage(items, pingAdapter);

//        for (int i = 0; i < PageLoader.loadTimes.size(); i++){
//            items.get(i).loadTime = PageLoader.loadTimes.get(i);
//            pingAdapter.notifyDataSetChanged();
//        }


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    @Override
    public void onPingerItemClick(View itemView, int clickedItemIndex) {
        PingerItem clickedPinger = items.get(clickedItemIndex);
//        OnPingerListFragInteractionListener pingListFragListener = (OnPingerListFragInteractionListener) getActivity();
//        pingListFragListener.onPingeItemClickListener(clickedPinger);
    }


    public interface OnPingerListFragInteractionListener {
        void onPingeItemClickListener(PingerItem clickedIem);
    }



    public String getLocalIpAddress() {
        String sLocalIpAddress="";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String sIpAddress = inetAddress.getHostAddress().toString();
                        if(sIpAddress.startsWith("fe80:")) {
                            // Ignore IPv6 Link local address
                        } else if(sIpAddress.startsWith("::127.") || sIpAddress.startsWith("::172.")) {
                            // Ignore local loopback address
                        } else {
                            sLocalIpAddress = sLocalIpAddress + " " + sIpAddress;
                        }
                    }
                }
            }
            return sLocalIpAddress;
        } catch (SocketException ex) {
            Log.e(LOG_TAG, ex.toString());
        }
        return null;
    }


    private void saveItems() {

        List<String> hostnameArray = new ArrayList<String>();

        hostnameArray.add("www.dropbox.co.uk");
        hostnameArray.add("www.amazon.co.uk");
        hostnameArray.add("www.youtube.com");
        hostnameArray.add("www.facebook.com");
        hostnameArray.add("www.google.co.uk");
        hostnameArray.add("www.dailymail.co.uk");
        hostnameArray.add("www.bbc.co.uk");
        hostnameArray.add("www.highways.gov.uk");
        hostnameArray.add("www.heathrow.co.uk");
        hostnameArray.add("www.police.uk");
        hostnameArray.add("www.tfl.gov.uk");
        hostnameArray.add("www.nationarail.co.uk");
        hostnameArray.add("www.nhs.uk");
        hostnameArray.add("www.gov.uk");

        List<NameResolver> threads = new ArrayList<NameResolver>();

        for (int i = 0; i < hostnameArray.size(); i++) {

            String hostname = hostnameArray.get(i);

            PingerItem pi = new PingerItem();
            pi.hostname = hostname;
            items.add(0, pi);
//            pia.notifyDataSetChanged();
            pingAdapter.notifyDataSetChanged();
            //m_position++;

            NameResolver nameResolver = new NameResolver(hostname, items);
            threads.add(nameResolver);

            Thread t = new Thread(nameResolver);
            t.start();
            //AddHostName(hostname);
        }

//		try {
//    		OutputStreamWriter out=
//    			new OutputStreamWriter(openFileOutput(SAVEFILE, 0));
//
//
//			for(int i=items.size()-1; i>=0; i--) {
//				out.write(items.get(i).hostname + "\n");
//
//    		    		}
//
//			out.close();
//
//    	}
//    	catch (Throwable t) {
//    		Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_SHORT).show();
//    	}

    }



    private void refresh() {
        try {
            OutputStreamWriter out=
                    new OutputStreamWriter(getContext().openFileOutput(SAVEFILE, 0));
            for(int i = items.size()-1; i >= 0; i--) {
                out.write(items.get(i).hostname + "\n");
            }
            out.close();
        }
        catch (Throwable t) {
            Toast.makeText(getActivity(), "Exception: " + t.toString(), Toast.LENGTH_SHORT).show();
        }

        // Restart
        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
    }



    private void startPing() {
        isRunning=true;
        for (int i = 0; i < 14; i++) {
            PingerItem pi = new PingerItem();

            //Thread t = new Thread(new NameResolver(pi.hostname));
            //t.start();
        }
    }

    private void stopPing() {
        isRunning=false;
        for (int i = 0; i < 14; i++) {
            PingerItem pi = new PingerItem();

            //Thread t = new Thread(new NameResolver(pi.hostname));
            //t.stop();
        }
    }


    public void loadWebPage(final ArrayList<PingerItem> items, final PingItemRecyclerAdapter pingAdapter){

//        progDailog = ProgressDialog.show(getContext(), "Loading","Please wait...", true);
//        progDailog.setCancelable(false);

        webView.clearCache(true);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.getSettings().setDomStorageEnabled(true);

        // Set WebView client


        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient(){

            long doneTime;
            long startTime;

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
                Log.d("WebView", "onPageStarted " + url);
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                doneTime = System.currentTimeMillis();

//                progDailog.dismiss();

                long loadTime = doneTime - startTime;

                Log.d("WebView", "onPageFinished " + url + " Started: " + startTime + " ended: " + doneTime + " Load Time: " + loadTime);

                int currentIndex = PageLoader.currentUrl;

                // we are checking to ensure that the url being recorded corresponds to the index on the items arraylist
                String[] checkMatchUrl = url.split("/");

                Log.d("webview", "PageLoaderURL: " + PageLoader.urlsToLoad.get(currentIndex).split("/")[2]);
                Log.d("webview", "PingerURL: " + items.get(PageLoader.currentUrl).hostname);

                Log.d("webview", "checkMatchUrl: " + checkMatchUrl[0]);
                Log.d("webview", "checkMatchUrl2: " + checkMatchUrl[2]);

//                if (checkMatchUrl[2].equals(items.get(PageLoader.currentUrl).hostname) && PageLoader.urlsToLoad.get(currentIndex).split("/")[2].equals(checkMatchUrl[2]))

                for (PingerItem pinger: items) {
                    if (pinger.hostname.contains(checkMatchUrl[2])){
                        pinger.loadTime += loadTime;
                        pingAdapter.notifyDataSetChanged();

                        //update the pageloader loadTimes list
                        int supposedIndex = items.indexOf(pinger);

                        long existingLoadTime = PageLoader.loadTimes.get(supposedIndex);
                        long newLoadTime = existingLoadTime + loadTime;

                        PageLoader.loadTimes.add(supposedIndex, newLoadTime);

                        // reset the currentIndex pointer so that the correct url is loaded next
                        PageLoader.currentUrl = supposedIndex;
                        break;
                    }
                }

//                if (checkMatchUrl[2].equals(items.get(PageLoader.currentUrl).hostname)){
//
//                    PageLoader.loadTimes.add(loadTime);
//
//                    items.get(currentIndex).loadTime = loadTime;
//
//                    pingAdapter.notifyDataSetChanged();
//
//
//                }else {
//                    for (PingerItem pinger: items) {
//                        if (pinger.hostname.equals(checkMatchUrl[2])){
//                            pinger.loadTime += loadTime;
//                            pingAdapter.notifyDataSetChanged();
//
//                            //update the pageloader loadTimes list
//                            int supposedIndex = items.indexOf(pinger);
//
//                            long existingLoadTime = PageLoader.loadTimes.get(supposedIndex);
//                            long newLoadTime = existingLoadTime + loadTime;
//
//                            PageLoader.loadTimes.add(supposedIndex, newLoadTime);
//
//                            // reset the currentIndex pointer so that the correct url is loaded next
//                            PageLoader.currentUrl = supposedIndex;
//                            break;
//                        }
//                    }
//
//                }

                //wait for sometime before loading another page
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isRunning){
                            PageLoader.loadNextUrl();
                        }
                    }
                }, 5000);





//                Toast.makeText(getContext(), "Page loaded in: " + loadTime + " milliseconds", Toast.LENGTH_SHORT).show();
            }
        });

        new PageLoader();

    }


}
