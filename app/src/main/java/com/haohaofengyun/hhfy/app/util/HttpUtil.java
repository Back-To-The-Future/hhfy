package com.haohaofengyun.hhfy.app.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 回到未来 on 2015/11/21.
 * App HHFY中涉及http请求的工具类
 */
public class HttpUtil {
    private static final String TAG = "HttpUtil";

    /**
     * @param address
     * @param listener
     */
    public static void sendHttpResquest(final String address, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    //Log.d(TAG, "connect: "+connection.getResponseMessage().toString());
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    // connection.setDoInput(true);
                    //connection.setDoOutput(true);
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    if (listener != null) {
                        Log.d(TAG, "-----------------" + stringBuilder.toString());
                        listener.onFinish(stringBuilder.toString());
                    }
                } catch (IOException e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                    // e.printStackTrace();
                } finally {
                    if (connection != null)
                        connection.disconnect();
                }
            }
        }).start();
    }
}
