package com.fyp.mobilebankingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

public class TransferSummaryOwn extends AppCompatActivity {

    String custID;
    String sender;
    String receiver;
    String paymentReference;
    String transferAmount;
    String transctID;
    String transctDateTime;
    String senderAccNO;
    String receiverAccNO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_summary_own);

        custID = getIntent().getStringExtra("custID");
        sender = getIntent().getStringExtra("sender");
        receiver = getIntent().getStringExtra("receiver");
        paymentReference = getIntent().getStringExtra("paymentReference");
        transferAmount = getIntent().getStringExtra("transferAmount");
        transctID = getIntent().getStringExtra("transctID");
        transctDateTime = getIntent().getStringExtra("transctDateTime");
        senderAccNO = getIntent().getStringExtra("senderAccNO");
        receiverAccNO = getIntent().getStringExtra("receiverAccNO");

        final TextView senderTextview = findViewById(R.id.senderTextview);
        final TextView receiverTextview = findViewById(R.id.receiverTextview);
        final TextView paymentRefTextview = findViewById(R.id.paymentRefTextview);
        final TextView dateTimeTextview = findViewById(R.id.dateTimeTextview);
        final TextView amountTextview = findViewById(R.id.amountTextview);

        senderTextview.setText(sender + "\n" + senderAccNO);
        receiverTextview.setText(receiver + "\n" + receiverAccNO);
        paymentRefTextview.setText(paymentReference);
        dateTimeTextview.setText(transctDateTime);
        amountTextview.setText("RM " + transferAmount);

        Button finalConfirm = findViewById(R.id.finalConfirmBtn);
        finalConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransferTaskFinal transferTaskFinal = new TransferTaskFinal();
                transferTaskFinal.execute();
            }
        });

        Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransferCancel transferCancel = new TransferCancel();
                transferCancel.execute();
            }
        });
    }

    public class TransferTaskFinal extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... params) {

            String host = getString(R.string.ip_address);    // IP use 10.0.2.2 for testing using emulator
            String transferOwnFinal = host + "transfer_own_final.php";

            try {
                URL url = new URL(transferOwnFinal);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("senderAccNO","UTF-8")+"="+URLEncoder.encode(senderAccNO,"UTF-8")+"&"
                        +URLEncoder.encode("receiverAccNO","UTF-8")+"="+URLEncoder.encode(receiverAccNO,"UTF-8")+"&"
                        +URLEncoder.encode("transferAmount","UTF-8")+"="+URLEncoder.encode(transferAmount,"UTF-8")+"&"
                        +URLEncoder.encode("transctID","UTF-8")+"="+URLEncoder.encode(transctID,"UTF-8");

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
        protected void onPostExecute(String result) {
            if(result.equals("Success")) {
                AlertDialog.Builder alertDialogBuilder;
                alertDialogBuilder = new AlertDialog.Builder(TransferSummaryOwn.this);
                alertDialogBuilder.setCancelable(true).setTitle("Status");
                alertDialogBuilder.setMessage("Payment Successful");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TransferSummaryOwn.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialogBuilder.create().show();
            } else {
                AlertDialog.Builder alertDialogBuilder;
                alertDialogBuilder = new AlertDialog.Builder(TransferSummaryOwn.this);
                alertDialogBuilder.setCancelable(true).setTitle("Status");
                alertDialogBuilder.setMessage("Payment Unsuccessful");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TransferSummaryOwn.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialogBuilder.create().show();
            }
        }
    }

    public class TransferCancel extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... params) {

            String host = getString(R.string.ip_address);    // IP use 10.0.2.2 for testing using emulator
            String transferCancelURL = host + "transfercancel.php";

            try {
                URL url = new URL(transferCancelURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("transctID","UTF-8")+"="+URLEncoder.encode(transctID,"UTF-8");

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
        protected void onPostExecute(String result) {
            if(result.equals("Success")) {
                Intent intent = new Intent(TransferSummaryOwn.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                AlertDialog.Builder alertDialogBuilder;
                alertDialogBuilder = new AlertDialog.Builder(TransferSummaryOwn.this);
                alertDialogBuilder.setCancelable(true).setTitle("Status");
                alertDialogBuilder.setMessage("Payment Unsuccessful");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TransferSummaryOwn.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                alertDialogBuilder.create().show();
            }
        }
    }

}
