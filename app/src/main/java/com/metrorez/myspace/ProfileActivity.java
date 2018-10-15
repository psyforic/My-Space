package com.metrorez.myspace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;
    private ImageView profileImage;
    private ImageView cameraImage;
    private EditText editTextName, editTextlastName, editTextEmail, editTextRoom, editTextStudentNo, editTextPhone;
    private Spinner spinnerCity, spinnerResdidence;
    private Button btnSave;
    private Uri profileUri;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private String profileImageUrl;
    private View view;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupUI();

        mAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });

    }

    private void saveUserInformation() {
        String displayName = editTextName.getText().toString() + " " + editTextlastName.getText().toString();
        if (!validateName() || !validateLastName()) {
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && profileUri != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Snackbar.make(view, "Profile updated", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profileUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profileUri);
                profileImage.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        final StorageReference profileStorageReference = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
        if (profileUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            profileStorageReference.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    profileImageUrl = profileStorageReference.getDownloadUrl().toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }

    private void setupUI() {
        toolbar = findViewById(R.id.toolbar);
        profileImage = findViewById(R.id.image);
        cameraImage = findViewById(R.id.profile_camera);
        editTextName = findViewById(R.id.input_name);
        editTextlastName = findViewById(R.id.input_lastname);
        editTextEmail = findViewById(R.id.input_email);
        editTextRoom = findViewById(R.id.input_roomName);
        editTextStudentNo = findViewById(R.id.input_studentNo);
        editTextPhone = findViewById(R.id.input_phone);
        spinnerCity = findViewById(R.id.spinner_city);
        spinnerResdidence = findViewById(R.id.spinner_residence);
        btnSave = findViewById(R.id.btn_update_profile);
        progressBar = findViewById(R.id.progressBar);
        view = findViewById(R.id.lyt_parent);
    }

    private boolean validateName() {
        if (editTextName.getText().toString().trim().isEmpty()) {
            //inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(editTextName);
            return false;
        }
        return true;
    }

    private boolean validateLastName() {
        if (editTextlastName.getText().toString().trim().isEmpty()) {

            requestFocus(editTextlastName);
            return false;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
