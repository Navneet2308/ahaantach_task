package com.example.android.ahaantechtask.Utils;

import android.app.Application;
import android.content.Context;

import com.android.volley.BuildConfig;
import com.android.volley.RequestQueue;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static MyApplication mInstance;
    public static RequestQueue mRequestQue;
    public static MySharedPreferences mSp;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mSp = MySharedPreferences.getInstance(this);
        mRequestQue = MyVolley.getInstance().getRequestQueue();
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
