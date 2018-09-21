package com.fyp.mobilebankingapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter{

    // Constants declaration
    private final List<Fragment> FRAGMENT_LIST = new ArrayList<>();
    private final List<String> FRAGMENT_LIST_TITLES = new ArrayList<>();

    // Constructor for ViewPagerAdapter Class
    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    //
    @Override
    public Fragment getItem(int position) {
        return FRAGMENT_LIST.get(position);
    }

    @Override
    public int getCount() {
        return FRAGMENT_LIST_TITLES.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return FRAGMENT_LIST_TITLES.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        FRAGMENT_LIST.add(fragment);
        FRAGMENT_LIST_TITLES.add(title);
    }
}
