package com.metrorez.myspace.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.model.User;
import com.metrorez.myspace.user.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

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
    List<User> users;
    private ImageView btn_editName, btn_editLastName, btn_editEmail, btn_editStudentNo, btn_editPhone, btn_editRoomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupUI();
        editButtons();
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
        editButtonsClickListeners();
        if (mAuth.getCurrentUser() != null) {
            loadUserInfo();
            loadExtraInfo();
        }

    }

    private void saveUserInformation() {
        String displayName = editTextName.getText().toString() + " " + editTextlastName.getText().toString();
        if (!validateName() || !validateLastName()) {
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && profileImageUrl != null) {
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
                //Picasso.with(this).load(bitmap.getGenerationId()).transform(new CircleTransform()).into(profileImage);
                //Picasso.with(this).load(bitmap.)
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

            Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = profileStorageReference.putBytes(data);
            uploadTask = profileStorageReference.putFile(profileUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(view, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }

                    return profileStorageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        profileImageUrl = task.getResult().toString();
                    } else {
                        Snackbar.make(view, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    private void loadUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                String photoUrl = user.getPhotoUrl().toString();
                Picasso.with(this).load(photoUrl.toString())
                        .placeholder(R.drawable.ic_placeholder)
                        .transform(new CircleTransform())
                        .into(profileImage);
                //Glide.with(this).load(photoUrl).into(profileImage);
            }
            if (user.getDisplayName() != null) {
                String displayName = user.getDisplayName();
                TextView textView = findViewById(R.id.name);
                textView.setText(displayName);
            }
        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }

    private void editButtons() {
        btn_editName = findViewById(R.id.edit_name);
        btn_editLastName = findViewById(R.id.edit_lastName);
        btn_editEmail = findViewById(R.id.edit_email);
        btn_editPhone = findViewById(R.id.edit_phone);
        btn_editRoomName = findViewById(R.id.edit_roomName);
        btn_editStudentNo = findViewById(R.id.edit_studentNo);
    }

    private void setupUI() {
        toolbar = findViewById(R.id.toolbar);
        profileImage = findViewById(R.id.image);
        cameraImage = findViewById(R.id.profile_camera);
        editTextName = findViewById(R.id.input_name);
        editTextlastName = findViewById(R.id.input_lastName);
        editTextEmail = findViewById(R.id.input_email);
        editTextRoom = findViewById(R.id.input_roomName);
        editTextStudentNo = findViewById(R.id.input_studentNo);
        editTextPhone = findViewById(R.id.input_phone);
        spinnerCity = findViewById(R.id.spinner_city);
        spinnerResdidence = findViewById(R.id.spinner_residence);
        btnSave = findViewById(R.id.btn_update_profile);
        progressBar = findViewById(R.id.progressBar);
        view = findViewById(R.id.lyt_parent);
        users = new ArrayList<>();
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

    // TODO:
    private void loadExtraInfo() {

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                editTextName.setText(user.getUserFirstName());
                editTextlastName.setText(user.getUserLastName());
                editTextEmail.setText(user.getUserEmail());
                editTextStudentNo.setText(user.getUserStudentNo());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void editButtonsClickListeners() {
        btn_editName.setOnClickListener(this);
        btn_editStudentNo.setOnClickListener(this);
        btn_editRoomName.setOnClickListener(this);
        btn_editPhone.setOnClickListener(this);
        btn_editLastName.setOnClickListener(this);
        btn_editEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_name:
                editTextName.setEnabled(true);
                requestFocus(editTextName);
                break;
            case R.id.edit_lastName:
                editTextlastName.setEnabled(true);
                requestFocus(editTextlastName);
                break;
            case R.id.edit_email:
                editTextEmail.setEnabled(true);
                requestFocus(editTextEmail);
                break;
            case R.id.edit_phone:
                editTextPhone.setEnabled(true);
                requestFocus(editTextPhone);
                break;
            case R.id.edit_studentNo:
                editTextStudentNo.setEnabled(true);
                requestFocus(editTextStudentNo);
                break;
            case R.id.edit_roomName:
                editTextRoom.setEnabled(true);
                requestFocus(editTextRoom);
                break;
        }
    }

}
