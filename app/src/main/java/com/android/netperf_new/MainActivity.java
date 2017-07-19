package com.android.netperf_new;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.netperf_new.adapter.PingItemAdapter;
import com.android.netperf_new.dbhelper.DataBaseHelper2;
import com.android.netperf_new.model.PingerItem;

import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;


    private static final String LOG_TAG = "multiping";
    private TextView myTextView = null;
    private TextView myTextView1 = null;

    DataBaseHelper2 myDbHelper1;
    // RadioData myNetworkMetric;
    //private DbxAccountManager mDbxAcctMgr;


    TextView selection;

    private final static String SAVEFILE="hosts";
    final static int MAXTIME = 100000;
    final static int TIMEOUT = 3000;
    final static long PERIOD = 5000;
    final static long UPDATE = 300000;
    final static int MAXHOSTS = 14;
    Activity content=null;
    int m_position = 0;

    Thread m_background=null;
    public static boolean isRunning=false;

    final ArrayList<PingerItem> items = new ArrayList<PingerItem>();

    PingItemAdapter pia = null;

//    public Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            String localIp = getLocalIpAddress();
//            if (localIp == null) localIp = "unknown";
//            myTextView.setText("FYI: local IP : " + localIp);
//
//            pia.notifyDataSetChanged();
//        }
//    };






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        RTT_LoadTimeFragment fragment1 = new RTT_LoadTimeFragment();
        VideoLoadFragment fragment2 = new VideoLoadFragment();

        mSectionsPagerAdapter.addFragments(fragment1);
        mSectionsPagerAdapter.addFragments(fragment2);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        myDbHelper1 = new DataBaseHelper2(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        if (id == R.id.page2) {
            Intent youTubeIntent = new Intent(this, YouTubeActivity.class);
            startActivity(youTubeIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        public PlaceholderFragment() {
//        }
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
//            return rootView;
//        }
//    }
//
//    /**
//     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
//     * one of the sections/tabs/pages.
//     */
//    public class SectionsPagerAdapter extends FragmentPagerAdapter {
//
//        public SectionsPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            // getItem is called to instantiate the fragment for the given page.
//            // Return a PlaceholderFragment (defined as a static inner class below).
//            return PlaceholderFragment.newInstance(position + 1);
//        }
//
//        @Override
//        public int getCount() {
//            // Show 3 total pages.
//            return 2;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "SECTION 1";
//                case 1:
//                    return "SECTION 2";
//            }
//            return null;
//        }
//    }



//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        content=this;
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//
//        myTextView1 = (TextView)findViewById(R.id.myTextView1);
//        myTextView = (TextView)findViewById(R.id.myTextView);
//        final ListView myListView = (ListView)findViewById(R.id.myListView);
//        final Button myButton = (Button) findViewById(R.id.myButton);
//        final Button myButton1 = (Button) findViewById(R.id.myButton1);
//
//        String localIp = getLocalIpAddress();
//        if (localIp == null) localIp = "unknown";
//        myTextView.setText("FYI: local IP = " + localIp);
//
//        pia = new PingItemAdapter(this);
//        myListView.setAdapter(pia);
//
//        saveItems();
//
//        //mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), "600vyw18i0kdkuc", "jkfqwlhgmhdpfci");
//        myDbHelper1 = new DataBaseHelper2(this);

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
//    }




//    public String getLocalIpAddress() {
//        String sLocalIpAddress="";
//        try {
//            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//                NetworkInterface intf = en.nextElement();
//                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//                    if (!inetAddress.isLoopbackAddress()) {
//                        String sIpAddress = inetAddress.getHostAddress().toString();
//                        if(sIpAddress.startsWith("fe80:")) {
//                            // Ignore IPv6 Link local address
//                        } else if(sIpAddress.startsWith("::127.") || sIpAddress.startsWith("::172.")) {
//                            // Ignore local loopback address
//                        } else {
//                            sLocalIpAddress = sLocalIpAddress + " " + sIpAddress;
//                        }
//                    }
//                }
//            }
//            return sLocalIpAddress;
//        } catch (SocketException ex) {
//            Log.e(LOG_TAG, ex.toString());
//        }
//        return null;
//    }



    public void onStart() {
        super.onStart();

//        m_background = new Thread(new Runnable() {
//            public void run() {
//                while(isRunning)
//                {
//                    for(PingerItem pingerItem: items){
//                        if(pingerItem.ia != null) {
//                            Thread t1 = new Thread(new Pinger80(pingerItem.ia));
//                            t1.setName("Pinger80 " + pingerItem.hostname);
//                            t1.start();
//                            Thread t2 = new Thread(new PingerAv(pingerItem.ia));
//                            t2.setName("PingerAv " + pingerItem.hostname);
//                            t2.start();
//                        }
//                    }
//
////    				for(int i=0; i<items.size(); i++) {
////    					PingerItem pi = items.get(i);
////    					if(pi.ia!=null) {
//////    						pi.result_80 = -1;
//////    						pi.result_av = -1;
//////    						items.set(i,pi);
////    						Thread t1 = new Thread(new Pinger80(pi.ia));
////    						t1.setName("Pinger80 " + pi.hostname);
////    						t1.start();
////    						Thread t2 = new Thread(new PingerAv(pi.ia));
////    						t2.setName("PingerAv " + pi.hostname);
////    						t2.start();
////    					}
////    				}
//
//                    try {
//                        for(int to=0; to<PERIOD; to+=UPDATE) {
//                            Thread.sleep(UPDATE);
//                            handler.sendMessage(handler.obtainMessage());
//                        }
//                    } catch (InterruptedException e) {
//                        Log.v("multiping","InterruptedException");
//                        break;
//                    }
//                } // end of while
//            }
//        });
//
//        isRunning=true;
//        m_background.start();
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

//    private void saveItems() {
//
//        List<String> hostnameArray = new ArrayList<String>();
//
//        hostnameArray.add("www.dropbox.co.uk");
//        hostnameArray.add("www.amazon.co.uk");
//        hostnameArray.add("www.youtube.com");
//        hostnameArray.add("www.facebook.com");
//        hostnameArray.add("www.google.co.uk");
//        hostnameArray.add("www.dailymail.co.uk");
//        hostnameArray.add("www.bbc.co.uk");
//        hostnameArray.add("www.highways.gov.uk");
//        hostnameArray.add("www.heathrow.co.uk");
//        hostnameArray.add("www.police.uk");
//        hostnameArray.add("www.tfl.gov.uk");
//        hostnameArray.add("www.nationarail.co.uk");
//        hostnameArray.add("www.nhs.uk");
//        hostnameArray.add("www.gov.uk");
//
//        List<NameResolver> threads = new ArrayList<NameResolver>();
//
//        for (int i = 0; i < hostnameArray.size(); i++) {
//
//            String hostname = hostnameArray.get(i);
//
//            PingerItem pi = new PingerItem();
//            pi.hostname = hostname;
//            items.add(0, pi);
//            pia.notifyDataSetChanged();
//            //m_position++;
//
//            NameResolver nameResolver = new NameResolver(hostname);
//            threads.add(nameResolver);
//
//            Thread t = new Thread(nameResolver);
//            t.start();
//            //AddHostName(hostname);
//        }
//
////		try {
////    		OutputStreamWriter out=
////    			new OutputStreamWriter(openFileOutput(SAVEFILE, 0));
////
////
////			for(int i=items.size()-1; i>=0; i--) {
////				out.write(items.get(i).hostname + "\n");
////
////    		    		}
////
////			out.close();
////
////    	}
////    	catch (Throwable t) {
////    		Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_SHORT).show();
////    	}
//
//    }

    public void onStop() {
        super.onStop();
        //saveItems();
        isRunning= false;
    }

    public void onPause() {
        super.onPause();
    }

//    public boolean onCreateOptionsMenu(Menu menu) {
//    	getMenuInflater().inflate(R.menu.quick, menu);
//    	return true;
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item) {
//    	switch (item.getItemId()) {
//    		case R.id.bugreport:
//    			String versionName = "";
//				try {
//					versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
//				} catch (NameNotFoundException e) {
//					e.printStackTrace();
//				}
//    			Intent i = new Intent(Intent.ACTION_SEND);
//    			i.setType("message/rfc822");
//    			i.putExtra(Intent.EXTRA_EMAIL, new String[]{"chukwuemeka.obiodu@kcl.ac.uk"});
//    			i.putExtra(Intent.EXTRA_SUBJECT, "BugReport " +  getString(R.string.app_name) + " " + versionName );
//    			startActivity( Intent.createChooser(i, "Select Email App"));
//    			return true;
//    		case R.id.refresh:
//    			refresh();
//    			return true;
//    	}
//    	return false;
//    }

    private void refresh() {
        try {
            OutputStreamWriter out=
                    new OutputStreamWriter(openFileOutput(SAVEFILE, 0));
            for(int i=items.size()-1; i>=0; i--) {
                out.write(items.get(i).hostname + "\n");
            }
            out.close();
        }
        catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_SHORT).show();
        }

        // Restart
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void onBackPressed()
    {
        finish();
        return;
    }
}
