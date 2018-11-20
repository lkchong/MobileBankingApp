package com.fyp.mobilebankingapp.Service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fyp.mobilebankingapp.BiometricAuthorization;
import com.fyp.mobilebankingapp.FCMReceive;
import com.fyp.mobilebankingapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FirebaseInstanceService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        SharedPreferences fcmPrefs = getApplicationContext().getSharedPreferences("fcmPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor fcmeditor = fcmPrefs.edit();
        fcmeditor.putString("fcmToken", token);
        fcmeditor.commit();



        String host = getString(R.string.ip_address);    // IP use 10.0.2.2 for testing using emulator
        String insertToken_URL = host + "fcm/insert_token.php";

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, insertToken_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    SharedPreferences fcmPrefs = getApplicationContext().getSharedPreferences("fcmPrefs", Context.MODE_PRIVATE);
                    String token = fcmPrefs.getString("fcmToken", "");

                    SharedPreferences userPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                    String username = userPrefs.getString("USERNAME", null);

                    params.put("fcm_token", token);
                    params.put("fcm_username", username);

                    return params;
                }
            };

            FCMReceive.getFcmReceive(FirebaseInstanceService.this).addToRequestQueue(stringRequest);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        /**
        if(remoteMessage.getData().isEmpty())
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        else
            showNotification(remoteMessage.getData());
         **/
        showNotification(remoteMessage.getData());
    }

    /**
    private void showNotification(String title, String body) {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.notification_channel_id),
                    getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("MyNotification");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(1, notificationBuilder.build());
    }
     **/

    private void showNotification(Map<String,String> data) {

        String title = data.get("title").toString();
        String body = data.get("body").toString();

        // Payment Variable
        String custID = data.get("custID").toString();
        String transctID = data.get("transctID").toString();
        String accountNO = data.get("accountNO").toString();
        String payeeID = data.get("payeeID").toString();
        String transctAmount = data.get("transctAmount").toString();
        String payeeName = data.get("payeeName").toString();
        String transctDateTime = data.get("transctDateTime").toString();

        Intent intent = new Intent(getApplicationContext(), BiometricAuthorization.class);
        intent.putExtra("custID", custID);
        intent.putExtra("transctID", transctID);
        intent.putExtra("accountNO", accountNO);
        intent.putExtra("payeeID", payeeID);
        intent.putExtra("transctAmount", transctAmount);
        intent.putExtra("transctDetails", body);
        intent.putExtra("payeeName", payeeName);
        intent.putExtra("transctDateTime", transctDateTime);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);


        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.notification_channel_id), "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("FYP Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.notification_channel_id));
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info")
                .setContentIntent(pendingIntent);

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }
}