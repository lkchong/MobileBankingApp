package com.fyp.mobilebankingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FragmentAccountSelection extends Fragment {
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Variables Declaration
        view = inflater.inflate(R.layout.fragment_account_selection, container, false);
        final Spinner accountSpinner = view.findViewById(R.id.accountDropdown);

        final String custID = getActivity().getIntent().getStringExtra("custID");

        new BackgroundTask(getActivity()) {
            @Override
            protected void onPostExecute(String result) {
                // Creating ArrayAdapter object
                ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item);

                accountSpinner.setAdapter(accountAdapter);

                // Parsing JSON result
                try {
                    JSONObject jsonAccount = new JSONObject(result);
                    JSONArray accountList = jsonAccount.getJSONArray("accounts");

                    for(int i = 0; i < accountList.length(); i++) {
                        JSONObject account = accountList.getJSONObject(i);
                        String accountName = account.getString("account_name");
                        accountAdapter.add(accountName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute("accountSelection", custID);

        Button accountSelected = view.findViewById(R.id.accountSelectedButton);
        accountSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountDetailsActivity.class);
                intent.putExtra("custID", custID);
                intent.putExtra("accountName", accountSpinner.getSelectedItem().toString());
                startActivity(intent);
            }
        });

        return view;
    }
}
