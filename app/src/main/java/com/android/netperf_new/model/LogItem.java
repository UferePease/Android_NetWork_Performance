package com.android.netperf_new.model;

/**
 * Created by Peace on 7/18/2017.
 */

public class LogItem {

    private String logTime;
    private String delayLog;

    public LogItem(String log_time, String delay_time){
        this.logTime = log_time;
        this.delayLog = delay_time;

    }

    public void setLogTime(String ltime){
        this.logTime = ltime;
    }

    public String getLogTime(){
        return logTime;
    }


    public void setDelayLog(String dlog){
        this.delayLog = dlog;
    }

    public String getDelayLog(){
        return delayLog;
    }
}
