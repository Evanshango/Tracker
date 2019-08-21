package com.evans.tracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class NameActivity extends AppCompatActivity {

    String email, password;
    EditText mName;
    Button mSubmit;
    CircleImageView mAccountImage;
    Uri resultUri;
    int GALLERY_PICK = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        mName = findViewById(R.id.sign_up_name);
        mSubmit = findViewById(R.id.btn_submit);
        mAccountImage = findViewById(R.id.account_image);

        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("email");
            password = intent.getStringExtra("password");
        }

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateCode();
            }
        });

        mAccountImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    private void generateCode() {
        String name = mName.getText().toString().trim();
        if (!name.isEmpty()) {
            Date date = new Date();
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
            String mDate = format1.format(date);
            Random r = new Random();

            int n = 100000 + r.nextInt(900000);
            String code = String.valueOf(n);

            if (resultUri != null) {
                Intent intent = new Intent(NameActivity.this, InviteCodeActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                intent.putExtra("date", mDate);
                intent.putExtra("isSharing", "false");
                intent.putExtra("code", code);
                intent.putExtra("imageUri", resultUri);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            try {
                if (resultCode == RESULT_OK && result != null) {
                    resultUri = result.getUri();
                    mAccountImage.setImageURI(resultUri);
                } else {
                    assert result != null;
                    String error = result.getError().toString();
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                String error = e.getMessage();
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
