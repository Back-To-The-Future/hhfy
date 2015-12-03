package com.haohaofengyun.hhfy.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by 回到未来911 on 2015/12/3.
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getcContext() {
        return context;
    }
}
