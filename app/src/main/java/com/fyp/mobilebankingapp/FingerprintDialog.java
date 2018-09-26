package com.fyp.mobilebankingapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class FingerprintDialog extends AppCompatDialogFragment {
    private TextView textView;
    private ImageView imageView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fingerprint_dialog, null);

        builder.setView(view).setTitle("");

        textView = view.findViewById(R.id.fingerprintText);
        imageView = view.findViewById(R.id.fingerprintImage);

        return builder.create();
    }

    public void setTextView(String textView) {
        this.textView.setText(textView);
    }

    public void setImageView(int imageView) {
        this.imageView.setImageResource(imageView);
    }
}
