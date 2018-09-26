package com.fyp.mobilebankingapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity {
    EditText username;
    EditText password;

    private String KEY_NAME = "fingerprintkey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);


        FingerprintDialog fingerprintDialog = new FingerprintDialog();
        fingerprintDialog.setCancelable(false);
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
                new AuthenticationHandler(this, fingerprintDialog), null);

    }

/**
    protected void onLogin(View view){
        String user = username.getText().toString();
        String pass = password.getText().toString();
        String type = "login";

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(type, user, pass);

         // Testing for asynctask inner class
        BackgroundTask2 backgroundTask = new BackgroundTask2();
        backgroundTask.execute(user, pass);

    }

    public class BackgroundTask2 extends AsyncTask<String, Void, String> {
        AlertDialog.Builder alertDialogBuilder;
        String user;

        @Override
        protected String doInBackground(String... params) {

            String host = "http://192.168.1.18/";   // IP use 10.0.2.2 for testing using emulator
            String login_URL = host + "login.php";
            String result = "";

                try {
                    user = params[0];
                    String pass = params[1];
                    URL url = new URL(login_URL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                    String post_data = URLEncoder.encode("user","UTF-8")+"="+URLEncoder.encode(user,"UTF-8")+"&"
                            +URLEncoder.encode("pass","UTF-8")+"="+URLEncoder.encode(pass,"UTF-8");

                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                    String line;
                    result = "";

                    while((line = bufferedReader.readLine()) != null) {
                        result = result + line;
                    }

                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
        }

        @Override
        protected void onPreExecute() {
            alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
            alertDialogBuilder.setCancelable(true).setTitle("Login Status");
        }

        @Override
        protected void onPostExecute(String result) {
                if (result.equals("Failed")) {
                    alertDialogBuilder.setMessage("Login Unsuccessful");
                    alertDialogBuilder.create().show();
                } else {
                    Toast loginSuccessToast = Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT);
                    loginSuccessToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    loginSuccessToast.show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("custID", result);
                    intent.putExtra("username", user);
                    LoginActivity.this.startActivity(intent);
                }
        }
    }**/
}
