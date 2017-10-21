package com.example.smile.whereareyou.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by Ryan on 2017/10/12.
 */

public class Utility {
    // Not available on >= api23
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService((Context.CONNECTIVITY_SERVICE));
        if (connectivity != null) {
            NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            // 获取到当前网络信息，且有连接
            if (networkInfo != null && networkInfo.isConnected()) {
                // 网络状态已连接
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED)
                    return true;
            }
        }
        return false;
    }

    public static JSONObject parseResponse(Response response) throws IOException {
        try {
            String jsonString = response.body().string();
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
