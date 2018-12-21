package com.fyp.mobilebankingapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.KeyguardManager;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;

    private SharedPreferences sharedPreferences;
    private String custID;
    private String username;
    private Button loginBtn;

    private String KEY_NAME = "fingerprintkey";
    private FingerprintDialog fingerprintDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText)findViewById(R.id.username);
        passwordEditText = (EditText)findViewById(R.id.password);

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("bioLoginSwitch", false)) {
            custID = sharedPreferences.getString("CUSTID", null);
            username = sharedPreferences.getString("USERNAME", null);

            usernameEditText.setVisibility(View.INVISIBLE);
            passwordEditText.setVisibility(View.INVISIBLE);

            loginBtn = (Button) findViewById(R.id.loginButton);
            loginBtn.setVisibility(View.INVISIBLE);

            biometricLogin();
        }
    }

    protected void biometricLogin() {
        fingerprintDialog = new FingerprintDialog();
        fingerprintDialog.setCancelable(false);

        Bundle dialogBundle = new Bundle();
        dialogBundle.putString("username", username);
        dialogBundle.putString("type", "biometricLogin");

        fingerprintDialog.setArguments(dialogBundle);
        fingerprintDialog.show(getSupportFragmentManager(), "fingerprint dialog");

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

        // Keystore act as a container for securely storing cryptographic key
        KeyStore keyStore;

        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch(Exception e) {
            Log.e("KeyStore", e.getMessage());
            return;
        }

        // Key generator to generate encryption key
        KeyGenerator keyGenerator;

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (Exception e) {
            Log.e("KeyGenerator", e.getMessage());
            return;
        }

        // Generate encrption key and storing in keystore
        try {
            keyStore.load(null);
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT |
                                                                        KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setUserAuthenticationRequired(true)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .build());

            keyGenerator.generateKey();
        } catch (Exception e) {
            Log.e("Generating keys", e.getMessage());
            return;
        }

        // Create encryption and decryption algorithm
        Cipher cipher;

        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_CBC + "/" +
                    KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (Exception e) {
            Log.e("Cipher", e.getMessage());
            return;
        }

        //Create secret key object from keystore
        try {
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (Exception e) {
            Log.e("Secret key", e.getMessage());
            return;
        }

        // CryptoObject allow for knowing if a new fingerprint was added
        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);

        // Cancellationsignal allows for operation in progress to be canceled
        CancellationSignal cancellationSignal = new CancellationSignal();

        // Activate the fingerprint scanner
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0,
                new AuthenticationHandler(this, fingerprintDialog, custID, username), null);
    }

    protected void onLogin(View view){
        String user = usernameEditText.getText().toString();
        String pass = passwordEditText.getText().toString();
        String type = "login";

        if(!user.equals("") && !pass.equals("")) {
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute(type, user, pass);
        } else {
            AlertDialog.Builder alertDialogBuilder;
            alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setCancelable(true).setTitle("Login Status");
            alertDialogBuilder.setMessage("Please fill in both username and password");
            alertDialogBuilder.create().show();
        }
    }
}
