package com.fyp.mobilebankingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declaring TabLayout object and ViewPager Object
        tabLayout = (TabLayout) findViewById(R.id.bottomNavigationBar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        // Creating ViewPagerAdapter object
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Adding Fragments to viewPagerAdapter
        viewPagerAdapter.addFragment(new FragmentAccountSelection(), "Accounts");
        viewPagerAdapter.addFragment(new FragmentTransferMain(), "Transfer");
        viewPagerAdapter.addFragment(new FragmentBillPayment(), "Bill");
        viewPagerAdapter.addFragment(new FragmentFeedback(), "Feedback");
        viewPagerAdapter.addFragment(new FragmentSettings(), "Settings");

        // Configuring viewPagerAdapter to viewPager and linking the viewPager to tabLayout
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // Setting the icon for each option in the tabLayout
        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_account_balance_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_attach_money_white_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.baseline_list_alt_white_24dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.baseline_feedback_white_24dp);
        tabLayout.getTabAt(4).setIcon(R.drawable.baseline_settings_white_24dp);


    }

    public class MainBackground extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {

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

                        params.put("fcm_token", token);

                        return params;
                    }
                };

                FCMReceive.getFcmReceive(MainActivity.this).addToRequestQueue(stringRequest);
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
