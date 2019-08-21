package com.evans.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.evans.tracker.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class InviteCodeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference userRef;
    StorageReference mStorageReference;

    ProgressDialog mDialog;
    TextView mInviteCode;
    Button mRegister;

    String name, email, password, date, isSharing, code, userId;
    Uri imageUri;
    UploadTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        mStorageReference = FirebaseStorage.getInstance().getReference().child("user_images");

        mDialog = new ProgressDialog(this);

        mInviteCode = findViewById(R.id.code);
        mRegister = findViewById(R.id.btn_register);

        fetchDetails();

        mInviteCode.setText(code);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRegister();
            }
        });
    }

    //perform user registration
    private void doRegister() {
        mDialog.setTitle("Creating Account");
        mDialog.setMessage("Please wait...");
        mDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addUserDetails();
                        } else {
                            mDialog.dismiss();
                            String exception = Objects.requireNonNull(task.getException()).toString();
                            Toast.makeText(InviteCodeActivity.this, exception, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mDialog.dismiss();
                String error = e.getMessage();
                Toast.makeText(InviteCodeActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //send user details to db
    private void addUserDetails() {
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        final User user = new User(userId, name, email, code, "false", "na", "na", "na");
        userRef.child(userId).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mDialog.dismiss();
                            final StorageReference ref = mStorageReference.child(userId + ".jpg");
                            ref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                if (task.isSuccessful()) {
                                                    mDialog.dismiss();
                                                    String downloadUrl = uri.toString();
                                                    userRef.child(userId).child("imageUrl").setValue(downloadUrl)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        mDialog.dismiss();
                                                                        Toast.makeText(InviteCodeActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                                                        finish();
                                                                        startActivity(new Intent(InviteCodeActivity.this, HomeActivity.class));
                                                                    } else {
                                                                        Toast.makeText(InviteCodeActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(InviteCodeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(InviteCodeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog.dismiss();
                        String error = e.getMessage();
                        Toast.makeText(InviteCodeActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //get info from previous intent
    private void fetchDetails() {
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
            password = intent.getStringExtra("password");
            date = intent.getStringExtra("date");
            isSharing = intent.getStringExtra("isSharing");
            code = intent.getStringExtra("code");
            imageUri = intent.getParcelableExtra("imageUri");
        }
    }
}
