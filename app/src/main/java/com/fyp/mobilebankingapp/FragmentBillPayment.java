package com.fyp.mobilebankingapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class FragmentBillPayment extends Fragment {
    View view;

    String custID;
    Spinner accountSpinner;
    Spinner payeeSpinner;
    TextView billAccountNOTextview;
    EditText amountEdittext;

    String billAccountNO;
    String billOrganization;
    String billAmount;
    String accountNO;
    String accountName;
    String paymentReference;
    String transferAmount;
    String transctID;
    String transctDateTime;
    String senderAccNO;
    String receiverAccNO;


    public FragmentBillPayment() {
    }

    @Override
    public void onResume() {
        super.onResume();

        custID = getActivity().getIntent().getStringExtra("custID");

        // Generates Payee Bill List
        PayeeBillSelection payeeBillSelection = new PayeeBillSelection();
        payeeBillSelection.execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bill_payment, container, false);

        custID = getActivity().getIntent().getStringExtra("custID");
        accountSpinner = view.findViewById(R.id.accountSpinner);
        payeeSpinner = view.findViewById(R.id.payeeSpinner);

        billAccountNOTextview = view.findViewById(R.id.billAccountNO);

        // Generate account list
        new BackgroundTask(getActivity()) {
            @Override
            protected void onPostExecute(String result) {
                // Creating ArrayAdapter object
                ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item);

                accountAdapter.setDropDownViewResource(R.layout.spinner_textview);

                accountSpinner.setAdapter(accountAdapter);

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

        // Generates Payee Bill List
        PayeeBillSelection payeeBillSelection = new PayeeBillSelection();
        payeeBillSelection.execute();

        // Display bill account no on payee selection
        payeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String payeeMultilines = payeeSpinner.getSelectedItem().toString();
                String[] lines;
                String[] columns;

                String nextLineDelimiter = "\n";
                String spaceDelimiter = " ";

                lines = payeeMultilines.split(nextLineDelimiter);
                columns = lines[1].split(spaceDelimiter);

                billOrganization = lines[0].trim();
                billAccountNO = columns[2].trim();
                billAccountNOTextview.setText(billAccountNO);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Add new bill payee
        ImageButton addNewBillPayeeBtn = view.findViewById(R.id.addNewBillPayeeBtn);
        addNewBillPayeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewBillPaymentPayee.class);
                intent.putExtra("custID", custID);
                startActivity(intent);
            }
        });

        // Confirm Button Listener
        Button confirmBtn = view.findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountMultilines = accountSpinner.getSelectedItem().toString();
                String[] lines;

                String nextLineDelimiter = "\n";

                lines = accountMultilines.split(nextLineDelimiter);

                accountNO = lines[1].trim();
                accountName = lines[0].trim();

                amountEdittext = view.findViewById(R.id.amountEdittext);
                billAmount = amountEdittext.getText().toString();

                if(!billOrganization.equals("") && !billAccountNO.equals("") && !billAmount.equals("")) {
                    BillTask billTask = new BillTask();
                    billTask.execute();
                }
                else {
                    AlertDialog.Builder alertDialogBuilder;
                    alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setCancelable(true).setTitle("Status");
                    alertDialogBuilder.setMessage("Please fill in all details.");
                    alertDialogBuilder.create().show();
                }
            }
        });



        return view;
    }

    // AsyncTask class for returning payee list
    public class PayeeBillSelection extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... params) {

            String host = getString(R.string.ip_address);    // IP use 10.0.2.2 for testing using emulator
            String payeeBillSelectionURL = host + "payeebillselection.php";

            try {
                URL url = new URL(payeeBillSelectionURL);
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
            ArrayAdapter<String> payeeAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_dropdown_item);

            payeeAdapter.setDropDownViewResource(R.layout.spinner_textview);

            payeeSpinner.setAdapter(payeeAdapter);

            // Parsing JSON result
            try {
                JSONObject jsonPayee = new JSONObject(result);
                JSONArray payeeList = jsonPayee.getJSONArray("payeeList");

                for (int i = 0; i < payeeList.length(); i++) {
                    JSONObject payee = payeeList.getJSONObject(i);
                    String payeeOrganization = payee.getString("payee_organization");
                    String billAccountNO = payee.getString("bill_account_no");

                    payeeAdapter.add(payeeOrganization + "\n" + "Bill NO: " + billAccountNO);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class BillTask extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... params) {

            String host = getString(R.string.ip_address);    // IP use 10.0.2.2 for testing using emulator
            String billingURL = host + "billing.php";

            try {
                URL url = new URL(billingURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("custID","UTF-8")+"="+URLEncoder.encode(custID,"UTF-8")+"&"
                        +URLEncoder.encode("accountNO","UTF-8")+"="+URLEncoder.encode(accountNO,"UTF-8")+"&"
                        +URLEncoder.encode("billOrganization","UTF-8")+"="+URLEncoder.encode(billOrganization,"UTF-8")+"&"
                        +URLEncoder.encode("billAccountNO","UTF-8")+"="+URLEncoder.encode("Bill NO: " + billAccountNO,"UTF-8")+"&"
                        +URLEncoder.encode("billAmount","UTF-8")+"="+URLEncoder.encode(billAmount,"UTF-8")+"&"
                        +URLEncoder.encode("transferType","UTF-8")+"="+URLEncoder.encode("Bill Payment","UTF-8");

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

                    Intent intent = new Intent(getActivity(), BillPaymentSummary.class);
                    intent.putExtra("custID", custID);
                    intent.putExtra("accountNO", accountNO);
                    intent.putExtra("accountName", accountName);
                    intent.putExtra("billOrganization", billOrganization);
                    intent.putExtra("billAccountNO", billAccountNO);
                    intent.putExtra("billAmount", billAmount);
                    intent.putExtra("transctID", transctID);
                    intent.putExtra("transctDateTime", transctDateTime);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder alertDialogBuilder;
                    alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
