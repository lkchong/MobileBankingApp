package com.fyp.mobilebankingapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declaring TabLayout object and ViewPager Object
        tabLayout = (TabLayout) findViewById(R.id.bottomNavigationBar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        // Creating ViewPagerAdapter object
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Adding Fragments to viewPagerAdapter
        viewPagerAdapter.addFragment(new FragmentAccountSelection(), "Accounts");
        viewPagerAdapter.addFragment(new FragmentTransferMain(), "Transfer");
        viewPagerAdapter.addFragment(new FragmentBillPayment(), "Bill");
        viewPagerAdapter.addFragment(new FragmentFeedback(), "Feedback");
        viewPagerAdapter.addFragment(new FragmentSettings(), "Settings");

        // Configuring viewPagerAdapter to viewPager and linking the viewPager to tabLayout
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // Setting the icon for each option in the tabLayout
        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_account_balance_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_attach_money_white_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.baseline_list_alt_white_24dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.baseline_feedback_white_24dp);
        tabLayout.getTabAt(4).setIcon(R.drawable.baseline_settings_white_24dp);
    }
}
