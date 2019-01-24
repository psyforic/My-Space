package com.metrorez.myspace.user;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Complaint;
import com.metrorez.myspace.user.model.Notification;
import com.metrorez.myspace.user.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddComplaintActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private Spinner priority;
    private EditText editTextComplaint;
    private Button fileComplaint;
    private CheckBox attachImage;
    private ImageView complaintImage;
    private ImageButton cameraButton;
    private Uri imageUri = null;
    private FirebaseAuth mAuth;
    private String imagePath;
    private String userRoom, userCity, residenceName, userName;
    private ProgressBar progressBar;
    private DatabaseReference notificationsReference;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private LinearLayout imageLinear;
    private View parent_view;
    private StorageReference storageReference;
    private DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_complaint);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("complaints");
        notificationsReference = FirebaseDatabase.getInstance().getReference().child("notifications");
        initToolbar();
        setupUI();
        getUserInfo();
        fileComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageToFirebaseStorage();
            }
        });
        attachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraButton.setVisibility(View.GONE);
                if (attachImage.isChecked()) {
                    cameraButton.setVisibility(View.VISIBLE);
                } else {
                    cameraButton.setVisibility(View.GONE);
                }
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImage();
            }
        });
        Tools.systemBarLolipop(this);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("File New Complaint");
    }

    private void setupUI() {
        parent_view = findViewById(android.R.id.content);
        setupDropdowns();
        complaintImage = findViewById(R.id.imageTaken);
        complaintImage.setOnTouchListener(new ImageMatrixTouchHandler(this));
        fileComplaint = (Button) findViewById(R.id.btn_send_comment);
        attachImage = (CheckBox) findViewById(R.id.checkbox_attach);
        cameraButton = (ImageButton) findViewById(R.id.camera_btn);
        progressBar = findViewById(R.id.progressBar);
        editTextComplaint = (EditText) findViewById(R.id.txtComment);
        imageLinear = findViewById(R.id.lyt_image);
    }

    private void addComplaint() {
        String category = priority.getSelectedItem().toString();
        String complainTxt = editTextComplaint.getText().toString().trim();
        String id = databaseReference.push().getKey();
        String userId = mAuth.getCurrentUser().getUid();
        if (validateResidence() && validateRoomNo()) {

            if (validateComplaint()) {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date today = Calendar.getInstance().getTime();
                String date = dateFormat.format(today);
                Complaint complaint;
                if (imageUri != null && attachImage.isChecked()) {
                    complaint = new Complaint(mAuth.getCurrentUser().getUid(), id, category, complainTxt, date, userCity, residenceName, userRoom, imagePath, userName);
                } else {
                    complaint = new Complaint(mAuth.getCurrentUser().getUid(), id, category, complainTxt, date, userCity, residenceName, userRoom, userName);
                }
                progressBar.setVisibility(View.VISIBLE);
                databaseReference.child(userId).child(id).setValue(complaint).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        sendNotification("Complaint", Constants.COMPLAINT_TYPE);
                        progressBar.setVisibility(View.GONE);

                        Intent intent = new Intent(AddComplaintActivity.this, SuccessActivity.class);
                        intent.putExtra(Constants.STRING_EXTRA, getString(R.string.str_complaint_message));
                        startActivity(intent);
                    }
                });

            } else {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(parent_view, "Please Review the Errors", Snackbar.LENGTH_LONG).show();
            }
        } else {
            showDialog();
        }
    }

    private boolean validateComplaint() {
        if (editTextComplaint.getText().toString().trim().isEmpty()) {
            requestFocus(editTextComplaint);
            editTextComplaint.setError("this field cannot be empty");
            return false;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void sendNotification(String content, String type) {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String date = dateFormat.format(today);
        String id = notificationsReference.push().getKey();
        String typeId = databaseReference.push().getKey();
        String userId = mAuth.getCurrentUser().getUid();
        Notification notification = new Notification(id, mAuth.getCurrentUser().getUid(), date, content, mAuth.getCurrentUser().getDisplayName(), type, typeId, userId, Constants.ADMIN_USER_ID, false);
        notificationsReference.child(id).setValue(notification);
    }

    private void setupDropdowns() {
        priority = (Spinner) findViewById(R.id.spinner_priority);
        final List<String> priorityList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.priorities)));
        final ArrayAdapter<String> priorityArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, priorityList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View spinnerPriorityView = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) spinnerPriorityView;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return spinnerPriorityView;
            }
        };
        priorityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priority.setAdapter(priorityArrayAdapter);
    }

    private void takeImage() {

        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String imageFilename = "JPEG_" + timeStamp + "_";
        File photo = new File(Environment.getExternalStorageDirectory(), imageFilename + ".jpg");
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Objects.requireNonNull(this).getContentResolver().notifyChange(selectedImage, null);
                    }
                    ContentResolver cr = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        cr = Objects.requireNonNull(this).getContentResolver();
                    }
                    Bitmap bitmap;

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImage);

                        Uri selected = getImageUri(this, bitmap);
                        String realPath = getRealPathFromURI(selected);
                        selectedImage = Uri.parse(realPath);
                        Toast.makeText(this, selectedImage.toString(), Toast.LENGTH_LONG).show();
                        if (bitmap != null) {
                            imageLinear.setVisibility(View.VISIBLE);
                            complaintImage.setVisibility(View.VISIBLE);

                            complaintImage.setImageBitmap(bitmap);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }
        }
    }

    private void uploadImageToFirebaseStorage() {
        storageReference = FirebaseStorage.getInstance().getReference("complaintPics/" + System.currentTimeMillis() + ".jpg");
        if (imageUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            ContentResolver cr = getContentResolver();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr, imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = storageReference.putBytes(data);
                final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (!task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(parent_view, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            imagePath = task.getResult().toString();
                            addComplaint();
                        } else {
                            Snackbar.make(parent_view, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

            } catch (Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        } else {
            addComplaint();
        }
    }

    private void getUserInfo() {
        usersReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                residenceName = user != null ? user.getUserResidence() : null;
                userRoom = user != null ? user.getUserRoom() : null;
                userCity = user != null ? user.getUserCity() : null;
                userName = user != null ? user.getUserFirstName() + " " + user.getUserLastName() : null;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean validateResidence() {
        if (residenceName == null) {
            return false;
        }
        return true;
    }

    private boolean validateRoomNo() {
        if (userRoom == null) {
            return false;
        }
        return true;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.error_message)
                .setTitle(R.string.update_profile).setIcon(R.drawable.ic_error);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(AddComplaintActivity.this, ProfileActivity.class));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
