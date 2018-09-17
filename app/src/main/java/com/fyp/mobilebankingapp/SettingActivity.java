package com.fyp.mobilebankingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        TabLayout tabLayout = (TabLayout ) findViewById(R.id.bottomNavigationBar);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        Intent intentAccounts = new Intent(SettingActivity.this, AccountSelectionActivity.class);
                        startActivity(intentAccounts);
                        break;

                    case 1:
                        Intent intentTransfer = new Intent(SettingActivity.this, TransferPageMain.class);
                        startActivity(intentTransfer);
                        break;

                    case 2:
                        Intent intentBill = new Intent(SettingActivity.this, BillPayment.class);
                        startActivity(intentBill);
                        break;

                    case 3:
                        Intent intentFeedback = new Intent(SettingActivity.this, CustomerFeedback.class);
                        startActivity(intentFeedback);
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
