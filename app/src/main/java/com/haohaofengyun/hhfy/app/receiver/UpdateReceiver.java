package com.haohaofengyun.hhfy.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.haohaofengyun.hhfy.app.service.AutoUpdateService;

/**
 * Created by 回到未来911 on 2015/12/3.
 */
public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoUpdateService.class);
        Log.d("qqqq", "onReceive: ");
        context.startService(i);
    }
}
