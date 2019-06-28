package com.example.sorter2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import models.User;
import models.UserManager;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LogInActivity";

    private UserManager um;
    private EditText username;
    private EditText password;

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        username = (EditText) findViewById(R.id.username_input);
        password = (EditText) findViewById(R.id.password_input);

        btn = findViewById(R.id.main_button);
        btn.setOnClickListener(this);

        um = UserManager.getInstance();
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: Called");
        String name = username.getText().toString();
        User user = um.getUserByName(name);
        if (user != null && user.getPassword().equals(password.getText().toString())){
            um.setCurrentUser(user);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Wrong username or password", Toast.LENGTH_SHORT).show();
        }
    }
}
