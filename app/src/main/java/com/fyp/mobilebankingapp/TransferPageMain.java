package com.fyp.mobilebankingapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



public class TransferPageMain extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_page_main);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.bottomNavigationBar);
        tabLayout.getTabAt(1).select();


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        Intent intentAccounts = new Intent(TransferPageMain.this, AccountSelectionActivity.class);
                        startActivity(intentAccounts);
                        break;
                    case 2:
                        Intent intentBill = new Intent(TransferPageMain.this, BillPayment.class);
                        startActivity(intentBill);
                        break;

                    case 3:
                        Intent intentFeedback = new Intent(TransferPageMain.this, CustomerFeedback.class);
                        startActivity(intentFeedback);
                        break;

                    case 4:
                        Intent intentSettings = new Intent(TransferPageMain.this, SettingActivity.class);
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

    @Override
    protected void onResume() {
        super.onResume();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.bottomNavigationBar);
        tabLayout.getTabAt(1).select();

    }
}
