package com.fyp.mobilebankingapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class TransferToOwnAcc extends AppCompatActivity {

    String custID;
    Spinner accountSpinner;
    Spinner receiverSpinner;

    String senderMultilines;
    String senderName;
    String receiverMultilines;
    String receiverName;
    String paymentReference;
    String transferAmount;
    String transctID;
    String transctDateTime;
    String senderAccNO;
    String receiverAccNO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_to_own_acc);

        accountSpinner = findViewById(R.id.ownAccountList);
        receiverSpinner = findViewById(R.id.receiverAccountList);

        custID = getIntent().getStringExtra("custID");

        // Generate account and receiver list
        new BackgroundTask(this) {
            @Override
            protected void onPostExecute(String result) {
                // Creating ArrayAdapter object
                ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(TransferToOwnAcc.this,
                        android.R.layout.simple_spinner_dropdown_item);

                accountAdapter.setDropDownViewResource(R.layout.spinner_textview);

                accountSpinner.setAdapter(accountAdapter);
                receiverSpinner.setAdapter(accountAdapter);

                // Parsing JSON result
                try {
                    JSONObject jsonAccount = new JSONObject(result);
                    JSONArray accountList = jsonAccount.getJSONArray("accounts");

                    for(int i = 0; i < accountList.length(); i++) {
                        JSONObject account = accountList.getJSONObject(i);
                        String accountName = account.getString("account_name");
                        String accountNO = account.getString("account_no");
                        accountAdapter.add(accountName + "\n" + accountNO);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute("accountSelection", custID);

        Button transferConfirm = findViewById(R.id.transferConfirmBtn);
        transferConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText paymentRef = findViewById(R.id.paymentRefEdittext);
                EditText amount = findViewById(R.id.amountEdittext);

                String spaceDelimiter = "\n";

                senderMultilines = accountSpinner.getSelectedItem().toString();
                String[] senderLines = senderMultilines.split(spaceDelimiter);
                senderName = senderLines[0].trim();
                senderAccNO = senderLines[1].trim();

                receiverMultilines = receiverSpinner.getSelectedItem().toString();
                String[] receiverLines = receiverMultilines.split(spaceDelimiter);
                receiverName = receiverLines[0].trim();
                receiverAccNO = receiverLines[1].trim();

                paymentReference = paymentRef.getText().toString();
                transferAmount = amount.getText().toString();

                if (!paymentReference.equals("") && !transferAmount.equals("")) {
                    if(senderName.equals(receiverName)) {
                        AlertDialog.Builder alertDialogBuilder;
                        alertDialogBuilder = new AlertDialog.Builder(TransferToOwnAcc.this);
                        alertDialogBuilder.setCancelable(true).setTitle("Status");
                        alertDialogBuilder.setMessage("Please choose different sender and receiver");
                        alertDialogBuilder.create().show();
                    } else {
                        TransferTask transferTask = new TransferTask();
                        transferTask.execute();
                    }
                }
                else {
                    AlertDialog.Builder alertDialogBuilder;
                    alertDialogBuilder = new AlertDialog.Builder(TransferToOwnAcc.this);
                    alertDialogBuilder.setCancelable(true).setTitle("Status");
                    alertDialogBuilder.setMessage("Please fill in all details.");
                    alertDialogBuilder.create().show();
                }
            }
        });
    }

    public class TransferTask extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... params) {

            String host = getString(R.string.ip_address);    // IP use 10.0.2.2 for testing using emulator
            String accountSelectionURL = host + "transfer_own.php";


            try {
                URL url = new URL(accountSelectionURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("custID","UTF-8")+"="+URLEncoder.encode(custID,"UTF-8")+"&"
                        +URLEncoder.encode("senderAccNO","UTF-8")+"="+URLEncoder.encode(senderAccNO,"UTF-8")+"&"
                        +URLEncoder.encode("receiverAccNO","UTF-8")+"="+URLEncoder.encode(receiverAccNO,"UTF-8")+"&"
                        +URLEncoder.encode("paymentReference","UTF-8")+"="+URLEncoder.encode(paymentReference,"UTF-8")+"&"
                        +URLEncoder.encode("transctAmount","UTF-8")+"="+URLEncoder.encode(transferAmount,"UTF-8");

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
            // Parsing JSON result
            try {
                JSONObject jsonTransactionResults = new JSONObject(result);
                String results = jsonTransactionResults.getString("result");

                if(results.equals("Success")){
                    transctID = jsonTransactionResults.getString("transctID");
                    transctDateTime = jsonTransactionResults.getString("transctDateTime");

                    Intent intent = new Intent(TransferToOwnAcc.this, TransferSummaryOwn.class);

                    intent.putExtra("custID", custID);
                    intent.putExtra("sender", senderName);
                    intent.putExtra("receiver", receiverName);
                    intent.putExtra("paymentReference", paymentReference);
                    intent.putExtra("transferAmount", transferAmount);
                    intent.putExtra("senderAccNO", senderAccNO);
                    intent.putExtra("receiverAccNO", receiverAccNO);
                    intent.putExtra("transctID", transctID);
                    intent.putExtra("transctDateTime", transctDateTime);

                    startActivity(intent);
                    finish();
                } else {
                    AlertDialog.Builder alertDialogBuilder;
                    alertDialogBuilder = new AlertDialog.Builder(TransferToOwnAcc.this);
                    alertDialogBuilder.setCancelable(true).setTitle("Status");
                    alertDialogBuilder.setMessage("Insufficient Balance");
                    alertDialogBuilder.create().show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
