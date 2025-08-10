package com.sysu.edu.api;

import android.app.Activity;
import android.util.DisplayMetrics;

public class Params {
    Activity context;
    public Params(Activity context){
        this.context = context;
    }
    public int getWidth(){
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        return dm.widthPixels;
    }
    public int getColumn(){
        return (getWidth()<1830)?1:(getWidth()<3050)?2:3;
    }
    public int dpToPx(int dps) {
        return Math.round(context.getResources().getDisplayMetrics().density * dps);
    }
}
