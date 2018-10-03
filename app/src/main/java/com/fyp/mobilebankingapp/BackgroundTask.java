package com.fyp.mobilebankingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Gravity;
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

    String user;
    String type;
    String result;

    @Override
    protected String doInBackground(String... params) {
        type = params[0];

        String host = context.getString(R.string.ip_address);    // IP use 10.0.2.2 for testing using emulator
        String login_URL = host + "login.php";
        String accountSelectionURL = host + "accountselection.php";
        String accountDetailsURL = host + "accountdetails.php";

        if(type.equals("login")) {
            try {
                user = params[1];
                String pass = params[2];
                URL url = new URL(login_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

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
                String custID = params[1];
                URL url = new URL(accountSelectionURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("custID","UTF-8")+"="+URLEncoder.encode(custID,"UTF-8");

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
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // For account details
        if(type.equals("accountDetails")) {
            try {
                String custID = params[1];
                String accountName = params[2];
                URL url = new URL(accountDetailsURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("custID","UTF-8")+"="+URLEncoder.encode(custID,"UTF-8")
                                    +"&"+URLEncoder.encode("accountName","UTF-8")+"="+URLEncoder.encode(accountName,"UTF-8");

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
        alertDialogBuilder.setCancelable(true).setTitle("Login Status");
    }

    @Override
    protected void onPostExecute(String result) {
            if (result.equals("Failed")) {
                alertDialogBuilder.setMessage("Login Unsuccessful");
                alertDialogBuilder.create().show();
            } else {
                Toast loginSuccessToast = Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT);
                loginSuccessToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                loginSuccessToast.show();

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("custID", result);
                intent.putExtra("username", user);
                context.startActivity(intent);
            }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
