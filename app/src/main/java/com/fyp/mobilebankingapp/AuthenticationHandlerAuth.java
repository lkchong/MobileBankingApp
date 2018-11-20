package com.fyp.mobilebankingapp;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.TextView;

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

public class AuthenticationHandlerAuth extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private FingerprintDialog fingerprintDialog;
    private String username;

    private String custID;
    private String transctID;
    private String accountNO;
    private String payeeID;
    private String transctAmount;
    TextView transcResult;

    public AuthenticationHandlerAuth (Context context, FingerprintDialog fingerprintDialog, String username, String custID,
                                        String transctID, String accountNO, String payeeID, String transctAmount, TextView transcResult) {
        this.context = context;
        this.fingerprintDialog = fingerprintDialog;
        this.username = username;

        this.custID = custID;
        this.transctID = transctID;
        this.accountNO = accountNO;
        this.payeeID = payeeID;
        this.transctAmount = transctAmount;
        this.transcResult = transcResult;
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

        AuthorizeTransaction authorizeTransaction = new AuthorizeTransaction();
        authorizeTransaction.execute();
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        fingerprintDialog.setTextView("Authentication Failed");
    }


    public class AuthorizeTransaction extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String host = context.getString(R.string.ip_address);
            String conn_URL = host + "fcm/authorize_payment.php";
            String result = "";

            try {
                URL url = new URL(conn_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("custID","UTF-8")+"="+URLEncoder.encode(custID,"UTF-8")+"&"
                        +URLEncoder.encode("transctID","UTF-8")+"="+URLEncoder.encode(transctID,"UTF-8")+"&"
                        +URLEncoder.encode("accountNO","UTF-8")+"="+URLEncoder.encode(accountNO,"UTF-8")+"&"
                        +URLEncoder.encode("payeeID","UTF-8")+"="+URLEncoder.encode(payeeID,"UTF-8")+"&"
                        +URLEncoder.encode("transctAmount","UTF-8")+"="+URLEncoder.encode(transctAmount,"UTF-8");

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
            return result;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Success")) {
                fingerprintDialog.setTextView("Authentication Succeeded");
                fingerprintDialog.setImageView(R.mipmap.fingerprint_success);

                transcResult.setText("Payment Successful");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fingerprintDialog.dismiss();
                        fingerprintDialog.setTextView("Payment Successful");

                    }
                }, 1000);
            } else {
                fingerprintDialog.setTextView("Failed");
                fingerprintDialog.setCancelable(true);

                transcResult.setText("Payment Unsuccessful");
            }
        }
    }
}
