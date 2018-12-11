package com.metrorez.myspace.user.fragment;

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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.metrorez.myspace.user.ProfileActivity;
import com.metrorez.myspace.user.SuccessActivity;
import com.metrorez.myspace.user.data.Constants;
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

public class AddComplaintFragment extends Fragment {

    DatabaseReference databaseReference;
    private Spinner priority;
    private EditText editTextComplaint;
    private Button fileComplaint;
    private CheckBox attachImage;
    private ImageView complaintImage;
    private ImageButton cameraButton;
    private Uri imageUri = null;
    private FirebaseAuth mAuth;
    private View view;
    private String imagePath;
    private String userRoom, userCity, residenceName, userName;
    private ProgressBar progressBar;
    private DatabaseReference notificationsReference;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private StorageReference storageReference;
    private DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_complaint, container, false);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("complaints");
        notificationsReference = FirebaseDatabase.getInstance().getReference().child("notifications");
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
        return view;
    }

    private void setupUI() {
        setupDropdowns();
        complaintImage = view.findViewById(R.id.imageTaken);
        complaintImage.setOnTouchListener(new ImageMatrixTouchHandler(view.getContext()));
        fileComplaint = (Button) view.findViewById(R.id.btn_send_comment);
        attachImage = (CheckBox) view.findViewById(R.id.checkbox_attach);
        cameraButton = (ImageButton) view.findViewById(R.id.camera_btn);
        progressBar = view.findViewById(R.id.progressBar);
        editTextComplaint = (EditText) view.findViewById(R.id.txtComment);
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

                        Intent intent = new Intent(getActivity(), SuccessActivity.class);
                        intent.putExtra(Constants.STRING_EXTRA, getString(R.string.str_complaint_message));
                        startActivity(intent);
                    }
                });

            } else {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(view, "Please Review the Errors", Snackbar.LENGTH_LONG).show();
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
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
        priority = (Spinner) view.findViewById(R.id.spinner_priority);
        final List<String> priorityList = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.priorities)));
        final ArrayAdapter<String> priorityArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, priorityList) {
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
            int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
        AddComplaintFragment.this.startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
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
            cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
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
                        Objects.requireNonNull(getActivity()).getContentResolver().notifyChange(selectedImage, null);
                    }
                    ContentResolver cr = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        cr = Objects.requireNonNull(getActivity()).getContentResolver();
                    }
                    Bitmap bitmap;

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImage);

                        Uri selected = getImageUri(getActivity(), bitmap);
                        String realPath = getRealPathFromURI(selected);
                        selectedImage = Uri.parse(realPath);
                        Toast.makeText(getActivity(), selectedImage.toString(), Toast.LENGTH_LONG).show();
                        if (bitmap != null) {
                            complaintImage.setVisibility(View.VISIBLE);
                            complaintImage.setImageBitmap(bitmap);
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT)
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
            ContentResolver cr = getActivity().getContentResolver();
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
                            Snackbar.make(view, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
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
                            Snackbar.make(view, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.error_message)
                .setTitle(R.string.update_profile).setIcon(R.drawable.ic_error);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(getActivity(), ProfileActivity.class));
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
}
