package com.fyp.mobilebankingapp;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.bottomNavigationBar);
        appBarLayout = (AppBarLayout) findViewById(R.id.bottomNavigationAppBar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Adding Fragments to viewPagerAdapter
        viewPagerAdapter.addFragment(new AccountSelectionFragment(), "Accounts");
        viewPagerAdapter.addFragment(new TransferMainFragment(), "Transfer");

        // Configuring viewPager
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_account_balance_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_attach_money_white_24dp);
    }
}
