package com.fyp.mobilebankingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

public class AuthenticationHandler extends FingerprintManager.AuthenticationCallback {

    private  Context context;
    private FingerprintDialog fingerprintDialog;

    public AuthenticationHandler (Context context, FingerprintDialog fingerprintDialog) {
        this.context = context;
        this.fingerprintDialog = fingerprintDialog;
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        //Toast.makeText(context, "Authentication Error." + errString, Toast.LENGTH_LONG).show();
        fingerprintDialog.setTextView(errString.toString());
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
        //Toast.makeText(context, "Authentication Help:" + helpString, Toast.LENGTH_LONG).show();
        fingerprintDialog.setTextView(helpString.toString());
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        //Toast.makeText(context, "Authentication Succeeded.", Toast.LENGTH_LONG).show();

        fingerprintDialog.setTextView("Authentication Succeeded");
        fingerprintDialog.setImageView(R.mipmap.fingerprint_success);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fingerprintDialog.dismiss();

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("custID", "1");
                intent.putExtra("username", "marcus");
                context.startActivity(intent);
            }
        }, 1000);


    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        //Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_LONG).show();
        fingerprintDialog.setTextView("Authentication Failed");
    }
}
