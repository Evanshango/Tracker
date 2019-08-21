package com.evans.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends AppCompatActivity {

    EditText mPassword;
    Button mNext;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        mPassword = findViewById(R.id.sign_up_password);
        mNext = findViewById(R.id.next_btn);

        Intent intent = getIntent();
        if (intent != null){
            email = intent.getStringExtra("email");
        }

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNamePickActivity();
            }
        });
    }

    private void goToNamePickActivity() {
        String password = mPassword.getText().toString().trim();
        if (!password.isEmpty()){
            if (password.length() >= 6){
                nameActivity(email, password);
            } else {
                Toast.makeText(this, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void nameActivity(String email, String password) {
        Intent intent = new Intent(PasswordActivity.this, NameActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        startActivity(intent);
        finish();
    }
}
