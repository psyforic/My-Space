package com.metrorez.myspace.user.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.metrorez.myspace.user.model.MoveIn;
import com.metrorez.myspace.user.model.Inventory;
import com.metrorez.myspace.user.model.Notification;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StepThreeFragment extends Fragment implements StepOneFragment.OnInventoryDataListener {
    private View view;
    private Uri imageUri = null;
    private FloatingActionButton fab_camera;
    private ImageView imageView;
    private ProgressBar progressBar;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String profileImageUrl;
    private List<Inventory> inventoryList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private ListView listView;

    private ArrayAdapter<String> arrayAdapter;
    private List<String> items = new ArrayList<>();

    private DatabaseReference notificationsReference;

    private DatabaseReference checkinReference = FirebaseDatabase.getInstance().getReference("checkins");

    private StorageReference imageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_step_three, container, false);
        setupUI();
        mAuth = FirebaseAuth.getInstance();
        notificationsReference = FirebaseDatabase.getInstance().getReference().child("notifications");
//        items = getArguments().getStringArrayList(Constants.MOVEIN_EXTRA);

        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(arrayAdapter);
        cameraButton();
        return view;
    }

    @Override
    public void onInventoryDataReceived(ArrayList<Inventory> inventoryData) {
        inventoryList = inventoryData;
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupUI() {
        listView = view.findViewById(R.id.listView);
        fab_camera = view.findViewById(R.id.fab_camera_intent);
        imageView = view.findViewById(R.id.imageView);
        imageView.setOnTouchListener(new ImageMatrixTouchHandler(view.getContext()));
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void cameraButton() {
        fab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });
    }

    public void takePhoto() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String imageFilename = "JPEG_" + timeStamp + "_";
        File photo = new File(Environment.getExternalStorageDirectory(), imageFilename + ".jpg");
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        StepThreeFragment.this.startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    getActivity().getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getActivity().getContentResolver();
                    Bitmap bitmap;

                    try {
                        bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);

                        imageView.setImageBitmap(bitmap);
                        Uri selected = getImageUri(getActivity(), bitmap);
                        String realPath = getRealPathFromURI(selected);
                        selectedImage = Uri.parse(realPath);
                        Toast.makeText(getActivity(), selectedImage.toString(),
                                Toast.LENGTH_LONG).show();

                        //uploadImageToFirebaseStorage("image");
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }
        }
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

    public void upload() {
        if (imageUri != null) {
            uploadImageToFirebaseStorage("image");
            Intent intent = new Intent(getActivity(), SuccessActivity.class);
            intent.putExtra(Constants.STRING_EXTRA, getString(R.string.str_checkin_message));
            getActivity().startActivity(intent);
            getActivity().finish();

        } else {
            Snackbar.make(view, "No Image taken", Snackbar.LENGTH_LONG).show();
        }
    }

    private void uploadImageToFirebaseStorage(final String name) {
        imageReference = FirebaseStorage.getInstance().getReference("checkinPics/" + System.currentTimeMillis() + ".jpg");

        if (imageUri != null) {
            progressBar.setVisibility(View.VISIBLE);

            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = imageReference.putFile(imageUri);

            final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(view, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }

                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        profileImageUrl = task.getResult().toString();
                        List<String> urls = new ArrayList<>();
                        urls.add(profileImageUrl);
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date today = Calendar.getInstance().getTime();
                        String date = dateFormat.format(today);

                        saveCheckinInfo(urls, mAuth.getCurrentUser().getUid(), date);


                    } else {
                        Snackbar.make(view, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    public void getItems(List<Inventory> inventoryList) {

        if (inventoryList.size() != 0) {
            this.inventoryList = inventoryList;
        }

        for (Inventory item : inventoryList) {
            items.add(item.getItemName());
        }
        arrayAdapter.notifyDataSetChanged();
    }

    private void saveCheckinInfo(List<String> url, String userId, String date) {
        String Id = mAuth.getCurrentUser().getUid();
        String key = checkinReference.push().getKey();
        MoveIn moveIn = new MoveIn(userId, key, date, url, inventoryList);
        checkinReference.child(Id).child(key).setValue(moveIn).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendNotification("MoveIn", Constants.MOVEIN_TYPE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void sendNotification(String content, String type) {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String date = dateFormat.format(today);
        String id = notificationsReference.push().getKey();
        String typeId = checkinReference.push().getKey();
        String userId = mAuth.getCurrentUser().getUid();
        Notification notification = new Notification(userId, id, mAuth.getCurrentUser().getUid(), date, content, mAuth.getCurrentUser().getDisplayName(), type, typeId, false);
        notificationsReference.child(userId).child(id).setValue(notification);
    }
}
