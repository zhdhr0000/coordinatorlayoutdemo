package com.github.zhdhr0000.demoapp;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by zhangyh on 2017/1/16.
 */

public class DimenUtils {

    public static int getScreenWidth(Context context) {
        DisplayMetrics metric =
                context.getResources().getDisplayMetrics();
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        return width;

    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metric =
                context.getResources().getDisplayMetrics();
        int height = metric.heightPixels;   // 屏幕高度（像素）
        return height;
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


}
