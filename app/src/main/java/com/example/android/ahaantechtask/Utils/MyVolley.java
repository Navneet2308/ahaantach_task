package com.example.android.ahaantechtask.Utils;

import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class MyVolley {
    private static MyVolley mInstance = null;
    private RequestQueue mRequestQueue;

    protected MyVolley() {
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
    }

    public static MyVolley getInstance() {
        if (mInstance == null) {
            mInstance = new MyVolley();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }


}
