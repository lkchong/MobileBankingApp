package com.fyp.mobilebankingapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AccountDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);


        TabLayout tabLayout = (TabLayout ) findViewById(R.id.bottomNavigationBar);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        Intent intentAccounts = new Intent(AccountDetailsActivity.this, AccountSelectionActivity.class);
                        startActivity(intentAccounts);
                        break;

                    case 1:
                        Intent intentTransfer = new Intent(AccountDetailsActivity.this, TransferPageMain.class);
                        startActivity(intentTransfer);
                        break;

                    case 2:
                        Intent intentBill = new Intent(AccountDetailsActivity.this, BillPayment.class);
                        startActivity(intentBill);
                        break;

                    case 3:
                        Intent intentFeedback = new Intent(AccountDetailsActivity.this, CustomerFeedback.class);
                        startActivity(intentFeedback);
                        break;

                    case 4:
                        Intent intentSettings = new Intent(AccountDetailsActivity.this, SettingActivity.class);
                        startActivity(intentSettings);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
