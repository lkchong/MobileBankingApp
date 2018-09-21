package com.fyp.mobilebankingapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AccountDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        Intent intent = getIntent();
        TextView accountName = (TextView) findViewById(R.id.accountName);
        accountName.setText(intent.getStringExtra("accountName"));
    }
}
