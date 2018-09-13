package com.fyp.mobilebankingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        /**BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigationMenu);

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.account_tab:
                        Intent intentAccount = new Intent(SettingActivity.this, AccountSelectionActivity.class);
                        startActivity(intentAccount);
                        break;

                    case R.id.transfer_tab:
                        Intent intentTransfer = new Intent(SettingActivity.this, TransferPageMain.class);
                        startActivity(intentTransfer);
                        break;

                    case R.id.bill_tab:
                        Intent intentBill = new Intent(SettingActivity.this, BillPayment.class);
                        startActivity(intentBill);
                        break;

                    case R.id.feedback_tab:
                        Intent intentFeedback = new Intent(SettingActivity.this, CustomerFeedback.class);
                        startActivity(intentFeedback);
                        break;

                    case R.id.settings_tab:
                        Intent intentSettings = new Intent(SettingActivity.this, SettingActivity.class);
                        startActivity(intentSettings);
                        break;
                }
            }
        });**/
    }
}
