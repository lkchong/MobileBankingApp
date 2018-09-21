package com.fyp.mobilebankingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
    }


    protected void onLogin(View view){
        String user = username.getText().toString();
        String pass = password.getText().toString();
        String type = "login";

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(type, user, pass);

    }
}
