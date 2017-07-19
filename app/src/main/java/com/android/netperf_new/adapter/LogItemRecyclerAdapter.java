package com.android.netperf_new.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.netperf_new.R;
import com.android.netperf_new.model.LogItem;
import com.android.netperf_new.utils.ColorUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Peace on 7/18/2017.
 */

public class LogItemRecyclerAdapter extends RecyclerView.Adapter<LogItemRecyclerAdapter.LogViewHolder> {

    private int numOfLogs;
    private static int viewHolderCount;

    private final String TAG = "Ping_Adapter";

    private Context mContext;
    private List<LogItem> items;

    //a member variable to store a reference to the click listener
    private LogItemClickListener mOnClickListener;

    WebView webView;

    long doneTime;
    long startTime;

    long loadTime;

    public interface LogItemClickListener{
        void onLogItemClick(View itemView, int clickedItemIndex);
    }

    public void setOnItemClickListener(LogItemClickListener listener){
        this.mOnClickListener = listener;
    }


    public LogItemRecyclerAdapter(Context context, List<LogItem> availlogs){
        items = availlogs;
        mContext = context;

        viewHolderCount = 0;
    }

    private Context getContext(){
        return mContext;
    }


    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.logitem;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        //inflate the custom layout
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        //return a new viewholder instance
        LogViewHolder viewHolder = new LogViewHolder(view);

//        viewHolder.viewHolderIndex.setText("ViewHolder index: " + viewHolderCount);


        //this to enable color banded rows
        int colorPosition = viewHolderCount % 2;
        int backgroundColorForViewHolder = ColorUtils.getViewHolderBackgroundColorFromInstance(context, colorPosition);
        viewHolder.itemView.setBackgroundColor(backgroundColorForViewHolder);

        viewHolderCount++;

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(LogViewHolder holder, int position) {
        //get the data model based on position
        LogItem item = items.get(position);

        //set item views based on the views and data model
        TextView timeView = holder.timeLogView;
        TextView delayView = holder.delayLogView;


        //DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        //Calendar calobj = Calendar.getInstance();
        //System.out.println(df.format(calobj.getTime()));

        //Long timeEntry = System.currentTimeMillis();
        //String timeEntry = df.format(calobj.getTime());

        timeView.setText(item.getLogTime());
        delayView.setText(item.getDelayLog());
    }



    //return the total count of items in the list
    @Override
    public int getItemCount() {
        return items.size();
    }


    class LogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView timeLogView;
        public TextView delayLogView;


        public LogViewHolder (View itemView){
            super(itemView);

            timeLogView = (TextView) itemView.findViewById(R.id.time_log);
            delayLogView = (TextView) itemView.findViewById(R.id.delay_log);

            itemView.setOnClickListener(this);
        }

        void bind(int listIndex){
            timeLogView.setText(String.valueOf(listIndex));
        }

        @Override
        public void onClick(View view) {
            if (mOnClickListener != null){
                int clickedPosition = getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION){
                    mOnClickListener.onLogItemClick(itemView, clickedPosition);
                }
            }

//            mOnClickListener.onDeviceItemClick(clickedPosition);
        }
    }
}
