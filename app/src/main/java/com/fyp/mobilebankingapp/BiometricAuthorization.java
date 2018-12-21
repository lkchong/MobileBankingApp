package com.fyp.mobilebankingapp;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class BiometricAuthorization extends AppCompatActivity {

    private String custID;
    private String transctID;
    private String accountNO;
    private String payeeID;
    private String transctAmount;
    private String transctDetails;
    private String payeeName;
    private String transctDateTime;
    TextView transcResult;

    private String username;
    private SharedPreferences bioAuthPrefs;
    private FingerprintDialog fingerprintDialog;
    private String KEY_NAME = "fingerprintkey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric_authorization);

        bioAuthPrefs = getSharedPreferences("bioAuthPrefs", Context.MODE_PRIVATE);
        username = bioAuthPrefs.getString("USERNAME", null);
        custID = getIntent().getStringExtra("custID");
        transctID = getIntent().getStringExtra("transctID");
        accountNO = getIntent().getStringExtra("accountNO");
        payeeID = getIntent().getStringExtra("payeeID");
        transctAmount = getIntent().getStringExtra("transctAmount");
        transctDetails = getIntent().getStringExtra("transctDetails");
        payeeName = getIntent().getStringExtra("payeeName");
        transctDateTime = getIntent().getStringExtra("transctDateTime");
        transcResult = findViewById(R.id.transcResults);

        biometricLogin();
    }


    protected void biometricLogin() {
        fingerprintDialog = new FingerprintDialog();
        fingerprintDialog.setCancelable(false);

        Bundle dialogBundle = new Bundle();
        dialogBundle.putString("username", username);
        dialogBundle.putString("type", "biometricAuthorization");
        dialogBundle.putString("transctID", transctID);
        dialogBundle.putString("transctDetails",transctDetails);
        dialogBundle.putString("payeeName", payeeName);
        dialogBundle.putString("transctAmount", transctAmount);
        dialogBundle.putString("payeeName", payeeName);
        dialogBundle.putString("transctDateTime", transctDateTime);

        fingerprintDialog.setArguments(dialogBundle);
        fingerprintDialog.show(getSupportFragmentManager(), "fingerprint dialog");

        // Creating keyguard manager object
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        // Check if keyguard security is enabled
        if(!fingerprintManager.isHardwareDetected()) {
            Log.e("Hardware", "Fingerprint hardware not detected.");
            return;
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("Permission", "Fingerprint permission is rejected.");
            return;
        }

        if(!keyguardManager.isKeyguardSecure()) {
            Log.e("Keyguard", "Keyguard is not enabled.");
            return;
        }

        KeyStore keyStore;

        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch(Exception e) {
            Log.e("KeyStore", e.getMessage());
            return;
        }

        KeyGenerator keyGenerator;

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (Exception e) {
            Log.e("KeyGenerator", e.getMessage());
            return;
        }

        try {
            keyStore.load(null);
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setUserAuthenticationRequired(true)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .build());

            keyGenerator.generateKey();
        } catch (Exception e) {
            Log.e("Generating keys", e.getMessage());
            return;
        }

        // Encryption and decryption algorithm
        Cipher cipher;

        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_CBC + "/" +
                    KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (Exception e) {
            Log.e("Cipher", e.getMessage());
            return;
        }

        //Create secret key object
        try {
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (Exception e) {
            Log.e("Secret key", e.getMessage());
            return;
        }

        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0,
                new AuthenticationHandlerAuth(this, fingerprintDialog, username, custID, transctID
                                                , accountNO, payeeID, transctAmount, transcResult), null);
    }

}
