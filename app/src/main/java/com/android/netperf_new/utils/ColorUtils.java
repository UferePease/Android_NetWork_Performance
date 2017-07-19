package com.android.netperf_new.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.android.netperf_new.R;

/**
 * Created by Peace on 7/17/2017.
 */

public class ColorUtils {

    public static int getViewHolderBackgroundColorFromInstance(Context context, int instanceNum){

        switch (instanceNum){
            case 0:
                return ContextCompat.getColor(context, R.color.recycle0);
            case 1:
                return ContextCompat.getColor(context, R.color.recycle1);

            default:
                return ContextCompat.getColor(context, R.color.recycle0);
        }
    }
}
