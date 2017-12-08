package com.example.as.mycircleindicator;

import android.content.res.Resources;

/**
 * Created by as on 2017/12/8.
 */

public class Utils {
    private static final float density= Resources.getSystem().getDisplayMetrics().density;

    public static float dpToPx(int dp)
    {
        return dp*density;
    }
}
