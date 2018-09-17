package com.fyp.mobilebankingapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TransferToOwnAcc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_to_own_acc);

        TabLayout tabLayout = (TabLayout ) findViewById(R.id.bottomNavigationBar);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        Intent intentAccounts = new Intent(TransferToOwnAcc.this, AccountSelectionActivity.class);
                        startActivity(intentAccounts);
                        break;

                    case 1:
                        Intent intentTransfer = new Intent(TransferToOwnAcc.this, TransferPageMain.class);
                        startActivity(intentTransfer);
                        break;

                    case 2:
                        Intent intentBill = new Intent(TransferToOwnAcc.this, BillPayment.class);
                        startActivity(intentBill);
                        break;

                    case 3:
                        Intent intentFeedback = new Intent(TransferToOwnAcc.this, CustomerFeedback.class);
                        startActivity(intentFeedback);
                        break;

                    case 4:
                        Intent intentSettings = new Intent(TransferToOwnAcc.this, SettingActivity.class);
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
