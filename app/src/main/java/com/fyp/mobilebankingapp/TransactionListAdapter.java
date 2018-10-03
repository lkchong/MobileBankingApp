package com.fyp.mobilebankingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TransactionListAdapter extends ArrayAdapter<TransactionItem>{
    private static final String TAG = "TransactionListAdapter";

    private Context context;
    int resource;

    public TransactionListAdapter(Context context, int resource, ArrayList<TransactionItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String transactionDatetime = getItem(position).getTransactionDatetime();
        String transactionReference = getItem(position).getTransactionReference();
        String transactionAmount = getItem(position).getTransactionAmount();

        //TransactionItem transactionItem = new TransactionItem(transactionDatetime,transactionReference, transactionAmount);

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        TextView textDatetime = (TextView) convertView.findViewById(R.id.transactionDatetime);
        TextView textReference = (TextView) convertView.findViewById(R.id.transactionReference);
        TextView textAmount = (TextView) convertView.findViewById(R.id.transactionAmount);

        textDatetime.setText(transactionDatetime);
        textReference.setText(transactionReference);
        textAmount.setText(transactionAmount);

        return convertView;
    }
}
