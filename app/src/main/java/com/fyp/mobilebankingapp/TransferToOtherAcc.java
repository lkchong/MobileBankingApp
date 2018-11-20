package com.fyp.mobilebankingapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

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

public class TransferToOtherAcc extends AppCompatActivity {

    String custID;
    Spinner accountSpinner;
    Spinner receiverSpinner;
    Spinner transferTypeSpinner;
    TextView transferTypeTextview;

    String sender;
    String receiver;
    String payeeBankName;
    String receiverAccNO;
    String paymentReference;
    String transferType;
    String transferAmount;
    String transctID;
    String transctDateTime;
    String senderAccNO;

    public void setDisplay() {
        // Generate Sender List
        new BackgroundTask(this) {
            @Override
            protected void onPostExecute(String result) {
                // Creating ArrayAdapter object
                ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(TransferToOtherAcc.this,
                        android.R.layout.simple_spinner_dropdown_item);

                accountSpinner.setAdapter(accountAdapter);

                // Parsing JSON result
                try {
                    JSONObject jsonAccount = new JSONObject(result);
                    JSONArray accountList = jsonAccount.getJSONArray("accounts");

                    for(int i = 0; i < accountList.length(); i++) {
                        JSONObject account = accountList.getJSONObject(i);
                        String accountName = account.getString("account_name");
                        accountAdapter.add(accountName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute("accountSelection", custID);

        // Generates Payee List
        PayeeSelection payeeSelection = new PayeeSelection();
        payeeSelection.execute();

        // Generates Transfer Type
        final ArrayAdapter<CharSequence> transferTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.transfer_type, android.R.layout.simple_spinner_item);
        transferTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transferTypeSpinner.setAdapter(transferTypeAdapter);

        // Checking selected payee bank account if same as application's
        receiverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String receiverMultiLines = receiverSpinner.getSelectedItem().toString();
                String[] lines;
                String spaceDelimiter = "\n";
                String hypenDelimiter = "-";

                lines = receiverMultiLines.split(spaceDelimiter);

                String column[] = lines[1].split(hypenDelimiter);

                receiver = lines[0].trim();
                payeeBankName = column[0].trim();
                receiverAccNO = column[1].trim();

                switch (payeeBankName) {
                    case "ABC Bank":
                        transferTypeTextview.setVisibility(View.INVISIBLE);
                        transferTypeSpinner.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        transferTypeTextview.setVisibility(View.VISIBLE);
                        transferTypeSpinner.setVisibility(View.VISIBLE);
                        break;
                }

                //CheckPayeeBank checkPayeeBank = new CheckPayeeBank();
                //checkPayeeBank.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_to_other_acc);

        accountSpinner = findViewById(R.id.ownAccountList);
        receiverSpinner = findViewById(R.id.receiverAccountList);
        transferTypeSpinner = findViewById(R.id.transferTypeList);

        transferTypeTextview = findViewById(R.id.transferTypeTextview);

        custID = getIntent().getStringExtra("custID");

        // Initialize display with data from database
        setDisplay();

        // Add new payee
        ImageButton addNewPayeeBtn = findViewById(R.id.addNewPayeeBtn);
        addNewPayeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransferToOtherAcc.this, NewTransferPayee.class);
                intent.putExtra("custID", custID);
                startActivity(intent);
                finish();
            }
        });

        // Confirm Button Listener
        Button transferConfirm = findViewById(R.id.transferConfirmBtn);
        transferConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText paymentRef = findViewById(R.id.paymentRefEdittext);
                EditText amount = findViewById(R.id.amountEdittext);

                String senderMultilines = accountSpinner.getSelectedItem().toString();
                String[] lines;
                String spaceDelimiter = "\n";

                lines = senderMultilines.split(spaceDelimiter);

                sender = lines[0].trim();
                paymentReference = paymentRef.getText().toString();
                transferAmount = amount.getText().toString();

                if(payeeBankName.equals("ABC Bank")) {
                    transferType = "Instant Transfer";
                }
                else {
                    transferType = transferTypeSpinner.getSelectedItem().toString();
                }

                if (!sender.equals("") && !receiver.equals("") && !paymentReference.equals("") &&
                        !transferAmount.equals("") && !transferType.equals("")) {
                    TransferTask transferTask = new TransferTask();
                    transferTask.execute();
                }
                else {
                    AlertDialog.Builder alertDialogBuilder;
                    alertDialogBuilder = new AlertDialog.Builder(TransferToOtherAcc.this);
                    alertDialogBuilder.setCancelable(true).setTitle("Status");
                    alertDialogBuilder.setMessage("Please fill in all details.");
                    alertDialogBuilder.create().show();
                }

            }
        });
    }

    // AsyncTask Class for validating transfer
    public class TransferTask extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... params) {

            String host = getString(R.string.ip_address);    // IP use 10.0.2.2 for testing using emulator
            String transferOtherURL = host + "transfer_other.php";

            try {
                URL url = new URL(transferOtherURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("custID","UTF-8")+"="+URLEncoder.encode(custID,"UTF-8")+"&"
                        +URLEncoder.encode("payeeBankName","UTF-8")+"="+URLEncoder.encode(payeeBankName,"UTF-8")+"&"
                        +URLEncoder.encode("receiverAccNO","UTF-8")+"="+URLEncoder.encode(receiverAccNO,"UTF-8")+"&"
                        +URLEncoder.encode("senderAccName","UTF-8")+"="+URLEncoder.encode(sender,"UTF-8")+"&"
                        +URLEncoder.encode("paymentReference","UTF-8")+"="+URLEncoder.encode(paymentReference,"UTF-8")+"&"
                        +URLEncoder.encode("transferType","UTF-8")+"="+URLEncoder.encode(transferType,"UTF-8")+"&"
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
                    senderAccNO = jsonTransactionResults.getString("senderAccNO");
                    //receiverAccNO = jsonTransactionResults.getString("receiverAccNO");

                    Intent intent = new Intent(TransferToOtherAcc.this, TransferSummaryOther.class);
                    intent.putExtra("custID", custID);
                    intent.putExtra("receiverBank", payeeBankName);
                    intent.putExtra("sender", sender);
                    intent.putExtra("receiver", receiver);
                    intent.putExtra("paymentReference", paymentReference);
                    intent.putExtra("transferType", transferType);
                    intent.putExtra("transferAmount", transferAmount);
                    intent.putExtra("senderAccNO", senderAccNO);
                    intent.putExtra("receiverAccNO", receiverAccNO);
                    intent.putExtra("transctID", transctID);
                    intent.putExtra("transctDateTime", transctDateTime);
                    startActivity(intent);
                    finish();
                } else {
                    AlertDialog.Builder alertDialogBuilder;
                    alertDialogBuilder = new AlertDialog.Builder(TransferToOtherAcc.this);
                    alertDialogBuilder.setCancelable(true).setTitle("Status");
                    alertDialogBuilder.setMessage("Insufficient Balance");
                    alertDialogBuilder.create().show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // AsyncTask class for returning payee list
    public class PayeeSelection extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... params) {

            String host = getString(R.string.ip_address);    // IP use 10.0.2.2 for testing using emulator
            String payeeSelectionURL = host + "payeeselection.php";

            try {
                URL url = new URL(payeeSelectionURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("custID", "UTF-8") + "=" + URLEncoder.encode(custID, "UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

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
            ArrayAdapter<String> payeeAdapter = new ArrayAdapter<String>(TransferToOtherAcc.this,
                    android.R.layout.simple_spinner_dropdown_item);

            payeeAdapter.setDropDownViewResource(R.layout.spinner_textview);

            receiverSpinner.setAdapter(payeeAdapter);

            // Parsing JSON result
            try {
                JSONObject jsonPayee = new JSONObject(result);
                JSONArray payeeList = jsonPayee.getJSONArray("payees_others");

                for (int i = 0; i < payeeList.length(); i++) {
                    JSONObject payee = payeeList.getJSONObject(i);
                    String payeeName = payee.getString("payee_name");
                    String payeeBankName = payee.getString("payee_bank");
                    String payeeAccountNO = payee.getString("payee_account_no");

                    payeeAdapter.add(payeeName + "\n" + payeeBankName + " - " + payeeAccountNO);
                }

                payeeList = jsonPayee.getJSONArray("payees_same_bank");

                for (int i = 0; i < payeeList.length(); i++) {
                    JSONObject payee = payeeList.getJSONObject(i);
                    String payeeFirstName = payee.getString("first_name");
                    String payeeLastName = payee.getString("last_name");
                    String payeeAccountNO = payee.getString("payee_account_no");

                    payeeAdapter.add(payeeFirstName + " " + payeeLastName + "\n" + "ABC Bank"
                                        + " - " + payeeAccountNO);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // AsyncTask Class for retrieving payee bank account
    public class CheckPayeeBank extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... params) {

            String host = getString(R.string.ip_address);    // IP use 10.0.2.2 for testing using emulator
            String checkPayeeBankURL = host + "checkpayeebank.php";
            receiver = receiverSpinner.getSelectedItem().toString();

            try {
                URL url = new URL(checkPayeeBankURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("custID","UTF-8")+"="+URLEncoder.encode(custID,"UTF-8")+"&"
                        +URLEncoder.encode("receiverAccName","UTF-8")+"="+URLEncoder.encode(receiver,"UTF-8");

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
            switch (result) {
                case "Public Bank":
                    transferTypeTextview.setVisibility(View.INVISIBLE);
                    transferTypeSpinner.setVisibility(View.INVISIBLE);
                    transferTypeSpinner.setSelection(1);
                    break;
                default:
                    transferTypeTextview.setVisibility(View.VISIBLE);
                    transferTypeSpinner.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}
