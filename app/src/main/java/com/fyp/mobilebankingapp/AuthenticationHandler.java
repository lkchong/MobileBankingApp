package com.fyp.mobilebankingapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AuthenticationHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private FingerprintDialog fingerprintDialog;
    private String custID;

    public AuthenticationHandler (Context context, FingerprintDialog fingerprintDialog, String custID) {
        this.context = context;
        this.fingerprintDialog = fingerprintDialog;
        this.custID = custID;
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        fingerprintDialog.setTextView(errString.toString());
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
        fingerprintDialog.setTextView(helpString.toString());
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);

        ConnectivityTest connectivityTest = new ConnectivityTest();
        connectivityTest.execute();
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        fingerprintDialog.setTextView("Authentication Failed");
    }


    public class ConnectivityTest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String host = context.getString(R.string.ip_address);
            String conn_URL = host + "conntest.php";
            String result = "";

            try {
                URL url = new URL(conn_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String line;
                result = "";

                while ((line = bufferedReader.readLine()) != null) {
                    result = result + line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Connection Success")) {
                fingerprintDialog.setTextView("Authentication Succeeded");
                fingerprintDialog.setImageView(R.mipmap.fingerprint_success);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fingerprintDialog.dismiss();

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("custID", custID);
                        context.startActivity(intent);
                    }
                }, 1000);
            } else {
                fingerprintDialog.setTextView("Server Issue, Try Later");
                fingerprintDialog.setCancelable(true);
            }
        }
    }
}
