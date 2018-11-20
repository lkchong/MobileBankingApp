package com.fyp.mobilebankingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;


public class FragmentSettings extends Fragment {

    View view;
    private Switch bioLoginSwitch;
    private Switch bioAuthSwitch;
    public static final String LOGIN_PREFS = "loginPrefs";
    public static final String BIOLOGINSWITCH = "bioLoginSwitch";
    private boolean loginSwitchState;
    private boolean authSwitchState;

    public FragmentSettings() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        bioLoginSwitch = (Switch) view.findViewById(R.id.bioLoginSwitch);
        bioAuthSwitch = (Switch) view.findViewById(R.id.bioAuthSwitch);

        bioLoginSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLoginMethod();
            }
        });

        bioAuthSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAuthSetting();

                if(bioAuthSwitch.isChecked()) {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            String deviceToken = instanceIdResult.getToken();

                            SharedPreferences fcmPrefs = getActivity().getSharedPreferences("fcmPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = fcmPrefs.edit();
                            editor.putString("fcmToken", deviceToken);
                            editor.commit();

                            MainBackground mainBackground = new MainBackground();
                            mainBackground.execute();
                        }
                    });
                }

                //Call async task to add token entry if switch is on
            }
        });

        loadLoginMethod();
        loadAuthSetting();
        updateViews();

        return view;
    }

    // Method for storing login settings in shared preferences
    public void saveLoginMethod() {
        // Creating SharedPreferences Object
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LOGIN_PREFS,
                                                                            Context.MODE_PRIVATE);

        // Creating shared preferences editor for storing information
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Storing information into shared preferences
        editor.putBoolean(BIOLOGINSWITCH, bioLoginSwitch.isChecked());
        editor.putString("CUSTID", getActivity().getIntent().getStringExtra("custID"));
        editor.putString("USERNAME", getActivity().getIntent().getStringExtra("username"));
        editor.apply();

        // Toast message to indicate data saved
        Toast.makeText(getActivity(), "Data Saved", Toast.LENGTH_LONG).show();
    }

    // Method for storing biometric authorization settings in shared preferences
    public void saveAuthSetting() {
        // Creating SharedPreferences Object
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("bioAuthPrefs",
                                                                            Context.MODE_PRIVATE);

        // Creating shared preferences editor for storing information
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Storing information into shared preferences
        editor.putBoolean("bioAuthSwitch", bioAuthSwitch.isChecked());
        editor.putString("USERNAME", getActivity().getIntent().getStringExtra("username"));
        editor.apply();

        // Toast message to indicate data saved
        Toast.makeText(getActivity(), "Data Saved", Toast.LENGTH_LONG).show();
    }

    // Method for loading login settings in shared preferences
    public void loadLoginMethod() {
        // Creating SharedPreferences Object
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LOGIN_PREFS,
                                                                            Context.MODE_PRIVATE);

        // Get required information from shared preferences
        loginSwitchState = sharedPreferences.getBoolean(BIOLOGINSWITCH, false);
    }

    // Method for loading biometric authorization settings in shared preferences
    public void loadAuthSetting() {
        // Creating SharedPreferences Object
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("bioAuthPrefs",
                                                                                Context.MODE_PRIVATE);

        // Get required information from shared preferences
        authSwitchState = sharedPreferences.getBoolean("bioAuthSwitch", false);
    }

    public void updateViews() {
        bioLoginSwitch.setChecked(loginSwitchState);
        bioAuthSwitch.setChecked(authSwitchState);
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

                        SharedPreferences fcmPrefs = getActivity().getSharedPreferences("fcmPrefs", Context.MODE_PRIVATE);
                        String token = fcmPrefs.getString("fcmToken", "");

                        SharedPreferences bioAuthPrefs = getActivity().getSharedPreferences("bioAuthPrefs", Context.MODE_PRIVATE);
                        String username = bioAuthPrefs.getString("USERNAME", null);

                        params.put("fcm_token", token);
                        params.put("fcm_username", username);

                        return params;
                    }
                };

                FCMReceive.getFcmReceive(getActivity()).addToRequestQueue(stringRequest);
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
