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

public class BillPaymentSummary extends AppCompatActivity {

    String custID;
    String accountNO;
    String accountName;
    String billOrganization;
    String billAccountNO;
    String billAmount;
    String transctID;
    String transctDateTime;

    TextView bankAccountTv;
    TextView payeeOrganizationTv;
    TextView billAccountNOTv;
    TextView dateTimeTv;
    TextView amountTv;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        TransferCancel transferCancel = new TransferCancel();
        transferCancel.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_payment_summary);

        // Getting data from intent
        Intent intent = getIntent();
        custID = intent.getStringExtra("custID");
        accountNO = intent.getStringExtra("accountNO");
        accountName = intent.getStringExtra("accountName");
        billOrganization = intent.getStringExtra("billOrganization");
        billAccountNO = intent.getStringExtra("billAccountNO");
        billAmount = intent.getStringExtra("billAmount");
        transctID = intent.getStringExtra("transctID");
        transctDateTime = intent.getStringExtra("transctDateTime");

        // Initializing textview
        bankAccountTv = findViewById(R.id.bankAccNOTextview);
        payeeOrganizationTv = findViewById(R.id.payeeTextview);
        billAccountNOTv = findViewById(R.id.billAccNOTextview);
        dateTimeTv = findViewById(R.id.dateTimeTextview);
        amountTv = findViewById(R.id.amountTextview);

        // Set value to textview
        bankAccountTv.setText(accountName + "\n" + accountNO);
        payeeOrganizationTv.setText(billOrganization);
        billAccountNOTv.setText(billAccountNO);
        dateTimeTv.setText(transctDateTime);
        amountTv.setText(billAmount);


        // Confirm button listener
        Button finalConfirm = findViewById(R.id.confirmBtn);
        finalConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BillTaskFinal transferTaskFinal = new BillTaskFinal();
                transferTaskFinal.execute();
            }
        });

        // Cancel button listener
        Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransferCancel transferCancel = new TransferCancel();
                transferCancel.execute();
            }
        });
    }


    // Async Task for Billing final
    public class BillTaskFinal extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... params) {

            String host = getString(R.string.ip_address);    // IP use 10.0.2.2 for testing using emulator
            String transferOwnFinal = host + "billing_final.php";

            try {
                URL url = new URL(transferOwnFinal);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("accountNO","UTF-8")+"="+URLEncoder.encode(accountNO,"UTF-8")+"&"
                        +URLEncoder.encode("billOrganization","UTF-8")+"="+URLEncoder.encode(billOrganization,"UTF-8")+"&"
                        +URLEncoder.encode("billAmount","UTF-8")+"="+URLEncoder.encode(billAmount,"UTF-8")+"&"
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
                alertDialogBuilder = new AlertDialog.Builder(BillPaymentSummary.this);
                alertDialogBuilder.setCancelable(true).setTitle("Status");
                alertDialogBuilder.setMessage("Payment Successful");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialogBuilder.create().show();
            } else {
                AlertDialog.Builder alertDialogBuilder;
                alertDialogBuilder = new AlertDialog.Builder(BillPaymentSummary.this);
                alertDialogBuilder.setCancelable(true).setTitle("Status");
                alertDialogBuilder.setMessage("Payment Unsuccessful");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
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
                AlertDialog.Builder alertDialogBuilder;
                alertDialogBuilder = new AlertDialog.Builder(BillPaymentSummary.this);
                alertDialogBuilder.setCancelable(true).setTitle("Status");
                alertDialogBuilder.setMessage("Transfer Canceled");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialogBuilder.create().show();
            }
        }
    }
}
