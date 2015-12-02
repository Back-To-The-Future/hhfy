package com.haohaofengyun.hhfy.app.util;

/**
 * Created by 回到未来 on 2015/11/21.
 */
public interface HttpCallbackListener {
    void onFinish(String urlData);
    void onError(Exception e);
}
