package com.fyp.mobilebankingapp;

import android.app.AlertDialog;
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

public class NewBillPaymentPayee extends AppCompatActivity {

    String custID;
    Spinner payeeSpinner;
    EditText billAccountNOTv;

    String billPayeeName;
    String billAccountNO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bill_payment_payee);

        custID = getIntent().getStringExtra("custID");
        payeeSpinner = findViewById(R.id.payeeSpinner);
        billAccountNOTv = findViewById(R.id.billAccNOEdittext);

        // Generate Bill Payee List
        BillPayeeSelection billPayeeSelection = new BillPayeeSelection();
        billPayeeSelection.execute();

        // Actin when add button pressed
        Button addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billPayeeName = payeeSpinner.getSelectedItem().toString();
                billAccountNO = billAccountNOTv.getText().toString();

                // Check required information
                if(!billPayeeName.equals("") && !billAccountNO.equals("")) {
                    AddNewBillPayee addNewBillPayee = new AddNewBillPayee();
                    addNewBillPayee.execute();
                }
                else {
                    AlertDialog.Builder alertDialogBuilder;
                    alertDialogBuilder = new AlertDialog.Builder(NewBillPaymentPayee.this);
                    alertDialogBuilder.setCancelable(true).setTitle("Status");
                    alertDialogBuilder.setMessage("Please fill in all details.");
                    alertDialogBuilder.create().show();
                }
            }
        });
    }


    // AsyncTask class for returning payee list
    public class BillPayeeSelection extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... params) {

            String host = getString(R.string.ip_address);    // IP use 10.0.2.2 for testing using emulator
            String payeeSelectionURL = host + "addnewbillpayeeselection.php";

            try {
                URL url = new URL(payeeSelectionURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(false);
                httpURLConnection.setDoInput(true);

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
        protected void onPostExecute(String result) {
            // Creating ArrayAdapter object
            ArrayAdapter<String> payeeAdapter = new ArrayAdapter<String>(NewBillPaymentPayee.this,
                    android.R.layout.simple_spinner_dropdown_item);

            payeeSpinner.setAdapter(payeeAdapter);

            // Parsing JSON result
            try {
                JSONObject jsonPayee = new JSONObject(result);
                JSONArray payeeList = jsonPayee.getJSONArray("payees");

                for (int i = 0; i < payeeList.length(); i++) {
                    JSONObject payee = payeeList.getJSONObject(i);
                    String payeeName = payee.getString("payee_organization");

                    payeeAdapter.add(payeeName);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    // AsyncTask Class for adding new payee
    public class AddNewBillPayee extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... params) {

            String host = getString(R.string.ip_address);    // IP use 10.0.2.2 for testing using emulator
            String addNewTransferPayeeURL = host + "addnewbillpayee.php";

            try {
                URL url = new URL(addNewTransferPayeeURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("custID","UTF-8")+"="+URLEncoder.encode(custID,"UTF-8")+"&"
                        +URLEncoder.encode("billPayeeName","UTF-8")+"="+URLEncoder.encode(billPayeeName,"UTF-8")+"&"
                        +URLEncoder.encode("billAccountNO","UTF-8")+"="+URLEncoder.encode(billAccountNO,"UTF-8");

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
            if(result.equals("Exist")){
                AlertDialog.Builder alertDialogBuilder;
                alertDialogBuilder = new AlertDialog.Builder(NewBillPaymentPayee.this);
                alertDialogBuilder.setCancelable(true).setTitle("Status");
                alertDialogBuilder.setMessage("Payee already added." + "\n"
                        + "Payee Name: " + billPayeeName + "\n" + "Bill NO: " + billAccountNO);
                alertDialogBuilder.create().show();
            } else if (result.equals("Added")){
                AlertDialog.Builder alertDialogBuilder;
                alertDialogBuilder = new AlertDialog.Builder(NewBillPaymentPayee.this);
                alertDialogBuilder.setCancelable(true).setTitle("Status");
                alertDialogBuilder.setMessage("Payee added." + "\n"
                        + "Payee Name: " + billPayeeName + "\n" + "Bill NO: " + billAccountNO);
                alertDialogBuilder.create().show();
            } else if (result.equals("Failed")){
                AlertDialog.Builder alertDialogBuilder;
                alertDialogBuilder = new AlertDialog.Builder(NewBillPaymentPayee.this);
                alertDialogBuilder.setCancelable(true).setTitle("Status");
                alertDialogBuilder.setMessage("Payee does not exist");
                alertDialogBuilder.create().show();
            }
        }
    }
}
