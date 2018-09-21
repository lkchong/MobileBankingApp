package com.fyp.mobilebankingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class BackgroundTask extends AsyncTask<String, Void, String> {
    Context context;
    AlertDialog.Builder alertDialogBuilder;

    BackgroundTask(Context context) {
        this.context = context;
    }



    String result;
    String type;

    @Override
    protected String doInBackground(String... params) {
        type = params[0];

        String login_URL = "http://192.168.1.18/login.php";
        // IP use 10.0.2.2 for testing using emulator

        String accountSelectionURL = "http://192.168.1.18/accountselection.php";

        if(type.equals("login")) {
            try {
                String user = params[1];
                String pass = params[2];
                URL url = new URL(login_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                //httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("user","UTF-8")+"="+URLEncoder.encode(user,"UTF-8")+"&"
                        +URLEncoder.encode("pass","UTF-8")+"="+URLEncoder.encode(pass,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String line;
                result = "";

                while((line = bufferedReader.readLine()) != null) {
                    result = result + line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                //return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // For account selection
        if(type.equals("accountSelection")) {
            try {

                URL url = new URL(accountSelectionURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String line;
                result = "";

                while((line = bufferedReader.readLine()) != null) {
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
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(true).setTitle("Login" +
                " Status");
    }

    @Override
    protected void onPostExecute(String result) {
        if(type.equals("login")) {
            if (result.equals("Success")) {
                Toast loginSuccessToast = Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT);
                loginSuccessToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                loginSuccessToast.show();

                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else {
                //alertDialogBuilder.setMessage(result);
                alertDialogBuilder.setMessage("Login Unsuccessful");
                alertDialogBuilder.create().show();
            }
        }

        if(type.equals("accountSelection")) {
            Toast loginSuccessToast = Toast.makeText(context, "Account Selection", Toast.LENGTH_SHORT);
            loginSuccessToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            loginSuccessToast.show();

        }

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
