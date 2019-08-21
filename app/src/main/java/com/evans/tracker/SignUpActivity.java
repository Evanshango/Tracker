package com.evans.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressDialog mDialog;

    EditText mEmail;
    Button mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        mEmail = findViewById(R.id.sign_up_email);
        mNext = findViewById(R.id.btn_next);

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPassword();
            }
        });
    }

    private void goToPassword() {
        mDialog.setMessage("Checking email address...");
        mDialog.show();
        //check if email is already in use or not
        String email = mEmail.getText().toString().trim();
        if (!email.isEmpty()){
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                doGoToPassword(email);
            } else {
                mDialog.dismiss();
                Toast.makeText(this, "Enter a valid Email", Toast.LENGTH_SHORT).show();
            }
        } else {
            mDialog.dismiss();
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
        }
    }

    private void doGoToPassword(final String email) {
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            mDialog.dismiss();
                            boolean check = !Objects.requireNonNull(Objects.requireNonNull(task.getResult())
                                    .getSignInMethods())
                                    .isEmpty();
                            if (!check) {
                                Intent intent = new Intent(SignUpActivity.this, PasswordActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                finish();
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Email already in use", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mDialog.dismiss();
                String err = e.getMessage();
                Toast.makeText(SignUpActivity.this, err, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
