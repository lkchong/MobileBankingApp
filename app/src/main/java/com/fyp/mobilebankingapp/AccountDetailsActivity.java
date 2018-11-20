package com.fyp.mobilebankingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AccountDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        Intent intent = getIntent();
        String custID = intent.getStringExtra("custID");
        String accountName = intent.getStringExtra("accountName");

        TextView accountNameText = findViewById(R.id.accountName);
        accountNameText.setText(accountName);

        // Variable declaration for transaction history
        final ListView transactionListView = (ListView) findViewById(R.id.transactionList);
        final ArrayList<TransactionItem> transactionItemList = new ArrayList<>();


        new BackgroundTask(this) {
            @Override
            protected void onPostExecute(String result) {
                // Parsing JSON result
                try {
                    JSONObject jsonAccountDetails = new JSONObject(result);
                    String balance = jsonAccountDetails.getString("balance");

                    TextView accountBalance = findViewById(R.id.accountBalance);
                    accountBalance.setText("$" + balance);

                    // Storing transaction history into JSONArray
                    JSONArray transactionList = jsonAccountDetails.getJSONArray("transaction_history");

                    // Populate Transaction List Array adapter with results
                    for(int i = 0; i < transactionList.length() && i < 10; i++) {
                        JSONObject account = transactionList.getJSONObject(i);
                        String transactionDatetime = account.getString("transc_datetime").toString();
                        String transactionReference = account.getString("transc_reference").toString();
                        String transactionAmount = account.getString("transc_amount").toString();

                        // Adding transaction details to array list
                        transactionItemList.add(new TransactionItem(transactionDatetime, transactionReference,
                                                                    "$" + transactionAmount));
                    }

                    // Array adapter for storing array list
                    TransactionListAdapter adapter = new TransactionListAdapter(AccountDetailsActivity.this,
                                                        R.layout.transc_adapter_view_layout, transactionItemList);
                    // Setting array adapter to ListView
                    transactionListView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute("accountDetails", custID, accountName);

    }
}
