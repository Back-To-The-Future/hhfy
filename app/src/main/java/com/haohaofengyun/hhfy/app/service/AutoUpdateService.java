package com.haohaofengyun.hhfy.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.haohaofengyun.hhfy.app.MyApplication;
import com.haohaofengyun.hhfy.app.receiver.UpdateReceiver;
import com.haohaofengyun.hhfy.app.util.HttpCallbackListener;
import com.haohaofengyun.hhfy.app.util.HttpUtil;
import com.haohaofengyun.hhfy.app.util.Utility;

public class AutoUpdateService extends Service {
    /**
     * 构造方法
     */
    public AutoUpdateService() {
    }

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(MyApplication.getcContext(), "浩浩天气自定更新成功", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(MyApplication.getcContext(), "浩浩天气自动更新失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                upDateWeather();
                Log.d("qqqq", "run: ");
            }
        }).start();

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + 8 * 60 * 60 * 1000;
        // long triggerAtTime = SystemClock.elapsedRealtime() + 4 * 1000;
        Intent i = new Intent(this, UpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(AutoUpdateService.this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void upDateWeather() {
        Log.d("qqqq,", "updateweather");
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = prefs.getString("weather_code", "");
        Log.d("qqqq,", weatherCode);
        if (!TextUtils.isEmpty(weatherCode)) {
            String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
            HttpUtil.sendHttpResquest(address, new HttpCallbackListener() {
                @Override
                public void onFinish(String urlData) {
                    Utility.handleWeatherResponse(AutoUpdateService.this, urlData);
                    Message msg = new Message();
                    msg.what = 0;
                    myHandler.sendMessage(msg);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 1;
                    myHandler.sendMessage(msg);
                }
            });
        }
    }
}
