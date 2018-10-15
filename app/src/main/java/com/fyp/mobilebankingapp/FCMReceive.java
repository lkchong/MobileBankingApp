package com.fyp.mobilebankingapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class FCMReceive {
    private static FCMReceive fcmReceive;
    private static Context context;
    private RequestQueue requestQueue;

    private FCMReceive(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue() {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        return requestQueue;
    }

    public static synchronized FCMReceive getFcmReceive(Context context) {
        if(fcmReceive == null) {
            fcmReceive = new FCMReceive(context);
        }

        return fcmReceive;
    }

    public<T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }
}
