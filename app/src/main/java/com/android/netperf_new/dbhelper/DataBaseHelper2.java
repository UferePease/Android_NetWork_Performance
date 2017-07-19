package com.android.netperf_new.dbhelper;

import android.database.sqlite.SQLiteOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by Peace on 7/16/2017.
 */

public class DataBaseHelper2 extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.abegoo.emeka.netperf_kcl/databases/";

    private static String DB_NAME = "NetPerf_KCL.sqlite";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    //private String searchTable = LanguageSelection.searchTable;

    public static class FeedEntry implements BaseColumns {

        public static final String TABLE_NAME = "tcpTest";
        public static final String COLUMN_NAME_TIME = "timeEntry";
        public static final String COLUMN_NAME_HOST = "hostnameEntry";
        public static final String COLUMN_NAME_IP = "ipAddressEntry";
        public static final String COLUMN_NAME_DELAY = "delayEntry";
        public static final String COLUMN_NAME_TCP80 = "tcp80Entry";
        public static final String COLUMN_NAME_TCPAV = "tcpAVEntry";
        public static final String COLUMN_NAME_CELLID = "cellIDEntry";
        public static final String COLUMN_NAME_LOCATION = "locationEntry";
        public static final String COLUMN_NAME_CARRIERNAME = "carrierEntry";
        public static final String COLUMN_NAME_SIGNAL = "signalEntry";
        public static final String COLUMN_NAME_MCC = "mccEntry";
        public static final String COLUMN_NAME_MNC = "mncEntry";
        public static final String COLUMN_NAME_TYPE = "netTypeEntry";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_TIME + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_HOST + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_IP + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_DELAY + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_TCP80 + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_TCPAV + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_CELLID + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_CARRIERNAME + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_SIGNAL + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_MCC + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_MNC + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_TYPE + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;


    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */
    public DataBaseHelper2(Context context) {

        super(context, "/sdcard/" + DB_NAME, null, 1);
        this.myContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }
}
