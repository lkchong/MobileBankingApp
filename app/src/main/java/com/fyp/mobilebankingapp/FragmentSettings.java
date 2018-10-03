package com.fyp.mobilebankingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;


public class FragmentSettings extends Fragment {
    View view;
    private Switch bioLoginSwitch;

    public static final String LOGIN_PREFS = "loginPrefs";
    public static final String BIOLOGINSWITCH = "bioLoginSwitch";

    private boolean loginSwitchState;

    public FragmentSettings() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);


        bioLoginSwitch = (Switch) view.findViewById(R.id.bioLoginSwitch);

        bioLoginSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLoginMethod();
            }
        });

        loadLoginMethod();
        updateViews();


        return view;
    }

    public void saveLoginMethod() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(BIOLOGINSWITCH, bioLoginSwitch.isChecked());
        editor.putString("CUSTID", getActivity().getIntent().getStringExtra("custID"));
        editor.putString("USERNAME", getActivity().getIntent().getStringExtra("username"));
        editor.apply();

        Toast.makeText(getActivity(), "Data Saved", Toast.LENGTH_LONG).show();
    }

    public void loadLoginMethod() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        loginSwitchState = sharedPreferences.getBoolean(BIOLOGINSWITCH, false);
    }

    public void updateViews() {
        bioLoginSwitch.setChecked(loginSwitchState);
    }

}
