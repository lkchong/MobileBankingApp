package com.fyp.mobilebankingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FragmentTransferMain extends Fragment {
    View view;
    String custID;

    public FragmentTransferMain() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_transfer_main, container, false);

        custID = getActivity().getIntent().getStringExtra("custID");

        Button buttonOwn = view.findViewById(R.id.transferToOwnAccBtn);
        buttonOwn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TransferToOwnAcc.class);
                intent.putExtra("custID", custID);
                startActivity(intent);
            }
        });

        Button buttonOther = view.findViewById(R.id.transferToOtherAccBtn);
        buttonOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TransferToOtherAcc.class);
                intent.putExtra("custID", custID);
                startActivity(intent);
            }
        });

        return view;
    }
}