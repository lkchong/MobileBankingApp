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

public class NewTransferPayee extends AppCompatActivity {

    String custID;
    Spinner bankNameSpinner;
    EditText payeeAccountNOEdittext;

    String bankName;
    String payeeAccountNO;
    String payeeName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transfer_payee);

        bankNameSpinner = findViewById(R.id.bankList);
        payeeAccountNOEdittext = findViewById(R.id.payeeAccNOEdittext);

        custID = getIntent().getStringExtra("custID");

        // Generates Bank List
        ArrayAdapter<CharSequence> bankListAdapter = ArrayAdapter.createFromResource(this,
                R.array.bank_list, android.R.layout.simple_spinner_item);
        bankListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bankNameSpinner.setAdapter(bankListAdapter);

        // Action when confirm button pressed
        Button confirmBtn = findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bankName = bankNameSpinner.getSelectedItem().toString();
                payeeAccountNO = payeeAccountNOEdittext.getText().toString();

                // Check required information
                if (!bankName.equals("") && !payeeAccountNO.equals("")) {
                    AddNewPayee addNewPayee = new AddNewPayee();
                    addNewPayee.execute();
                }
                else {
                    AlertDialog.Builder alertDialogBuilder;
                    alertDialogBuilder = new AlertDialog.Builder(NewTransferPayee.this);
                    alertDialogBuilder.setCancelable(true).setTitle("Status");
                    alertDialogBuilder.setMessage("Please fill in all details.");
                    alertDialogBuilder.create().show();
                }
            }
        });
    }


    // AsyncTask Class for adding new payee
    public class AddNewPayee extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... params) {

            String host = getString(R.string.ip_address);    // IP use 10.0.2.2 for testing using emulator
            String addNewTransferPayeeURL = host + "addnewtransferpayee.php";

            try {
                URL url = new URL(addNewTransferPayeeURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("custID","UTF-8")+"="+URLEncoder.encode(custID,"UTF-8")+"&"
                        +URLEncoder.encode("bankName","UTF-8")+"="+URLEncoder.encode(bankName,"UTF-8")+"&"
                        +URLEncoder.encode("payeeAccountNO","UTF-8")+"="+URLEncoder.encode(payeeAccountNO,"UTF-8");

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
            try {
                JSONObject jsonObject = new JSONObject(result);
                String results = jsonObject.getString("result");

                if(results.equals("Exist")){
                    payeeName = jsonObject.getString("payeeName");

                    AlertDialog.Builder alertDialogBuilder;
                    alertDialogBuilder = new AlertDialog.Builder(NewTransferPayee.this);
                    alertDialogBuilder.setCancelable(true).setTitle("Status");
                    alertDialogBuilder.setMessage("Payee already added." + "\n"
                                                    + "Payee Name: " + payeeName);
                    alertDialogBuilder.create().show();
                } else if (results.equals("Added")){
                    payeeName = jsonObject.getString("payeeName");

                    AlertDialog.Builder alertDialogBuilder;
                    alertDialogBuilder = new AlertDialog.Builder(NewTransferPayee.this);
                    alertDialogBuilder.setCancelable(true).setTitle("Status");
                    alertDialogBuilder.setMessage("Payee added." + "\n"
                                                    + "Payee Name: " + payeeName);
                    alertDialogBuilder.create().show();
                } else if (results.equals("Failed")){
                    AlertDialog.Builder alertDialogBuilder;
                    alertDialogBuilder = new AlertDialog.Builder(NewTransferPayee.this);
                    alertDialogBuilder.setCancelable(true).setTitle("Status");
                    alertDialogBuilder.setMessage("Payee does not exist");
                    alertDialogBuilder.create().show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}