package com.metrorez.myspace.user.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
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
import android.widget.AdapterView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.SuccessActivity;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.model.Complaint;
import com.metrorez.myspace.user.model.Notification;

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
    private Spinner residences, cities, priority;
    private EditText roomNo, editTextComplaint;
    private Button fileComplaint;
    private CheckBox attachImage;
    private ImageView complaintImage;
    private ImageButton cameraButton;
    private Uri imageUri = null;
    private FirebaseAuth mAuth;
    private View view;
    String imagePath;
    private ProgressBar progressBar;
    private DatabaseReference notificationsReference;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_complaint, container, false);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("complaints");
        notificationsReference = FirebaseDatabase.getInstance().getReference().child("notifications");
        setupUI();

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
        roomNo = (EditText) view.findViewById(R.id.txtRoomName);
        editTextComplaint = (EditText) view.findViewById(R.id.txtComment);

    }

    private void addComplaint() {
        String residence = residences.getSelectedItem().toString();
        String city = cities.getSelectedItem().toString();
        String room = roomNo.getText().toString().trim();
        String category = priority.getSelectedItem().toString();
        String complainTxt = editTextComplaint.getText().toString().trim();
        String id = databaseReference.push().getKey();
        String userId = mAuth.getCurrentUser().getUid();


        if (validateRoom() && validateComplaint()) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date today = Calendar.getInstance().getTime();
            String date = dateFormat.format(today);
            Complaint complaint;
            if (imageUri != null && attachImage.isChecked()) {
                complaint = new Complaint(mAuth.getCurrentUser().getUid(), id, category, complainTxt, date, city, residence, room, imagePath);
            } else {
                complaint = new Complaint(mAuth.getCurrentUser().getUid(), id, category, complainTxt, date, city, residence, room);
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
    }

    private boolean validateRoom() {
        if (roomNo.getText().toString().trim().isEmpty()) {
            //inputLayoutName.setError(getString(R.string.err_msg_name));
            Snackbar.make(view, getString(R.string.err_msg_room), Snackbar.LENGTH_SHORT).show();
            requestFocus(roomNo);
            roomNo.setError("this field cannot be empty");
            return false;
        }
        return true;
    }

    private boolean validateComplaint() {
        if (editTextComplaint.getText().toString().trim().isEmpty()) {
            //inputLayoutName.setError(getString(R.string.err_msg_name));
            Snackbar.make(view, getString(R.string.err_msg_complaint), Snackbar.LENGTH_SHORT).show();
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
        Notification notification = new Notification(userId, id, mAuth.getCurrentUser().getUid(), date, content, mAuth.getCurrentUser().getDisplayName(), type, typeId, false);
        notificationsReference.child(userId).child(id).setValue(notification);
    }

    private void setupDropdowns() {
        /**
         * Cities Dropdown
         */
        cities = (Spinner) view.findViewById(R.id.spinner_city);
        final List<String> cityList = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.city)));
        final ArrayAdapter<String> cityArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cityList) {
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
                View spinnerCityView = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) spinnerCityView;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return spinnerCityView;
            }
        };

        cityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cities.setAdapter(cityArrayAdapter);

        /**
         * Residences Dynamic Dropdown
         */
        residences = (Spinner) view.findViewById(R.id.spinner_residence);
        final int[] array = new int[1];
        array[0] = R.array.PortElizabethResidences;
        final ArrayAdapter<String> spinnerArrayAdapter;
        cities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (cities.getSelectedItemPosition()) {
                    case 1:
                        array[0] = R.array.PortElizabethResidences;
                        break;

                    case 2:
                        array[0] = R.array.EastLondonResidences;
                        break;
                    case 3:
                        array[0] = R.array.QueenstownResidences;
                        break;
                    default:
                        array[0] = R.array.PortElizabethResidences;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final List<String> residenceList = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(array[0])));
        spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, residenceList) {
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
                View spinnerView = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) spinnerView;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return spinnerView;
            }
        };
        spinnerArrayAdapter.notifyDataSetChanged();
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        residences.setAdapter(spinnerArrayAdapter);
        /**
         * Priority List Dropdown
         */
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
                progressBar.setVisibility(View.GONE);
            } catch (Exception e) {

            }
        }
        else{
            addComplaint();
        }
    }
}
