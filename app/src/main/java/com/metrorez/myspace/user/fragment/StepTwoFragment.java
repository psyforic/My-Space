package com.metrorez.myspace.user.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.metrorez.myspace.user.activity.SuccessActivity;
import com.metrorez.myspace.user.adapter.MoveInItemsGridAdapter;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Inventory;
import com.metrorez.myspace.user.model.MoveIn;
import com.metrorez.myspace.user.model.MoveInItem;
import com.metrorez.myspace.user.model.Notification;
import com.metrorez.myspace.user.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class StepTwoFragment extends BaseFragment {

    private View view;
    private Uri imageUri = null;
    private ProgressBar progressBar;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String profileImageUrl;
    private ListView listView;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    public final static int ALL_PERMISSIONS_RESULT = 107;
    public final static int IMAGE_RESULT = 200;

    private RecyclerView recyclerView;
    private List<Inventory> inventoryList = new ArrayList<>();
    private List<MoveInItem> items = new ArrayList<>();


    private String userResidence;
    private String userRoom;
    private String userCity;
    private String userName;

    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ActionBar actionBar;
    int position;
    List<String> urls = new ArrayList<>();
    public MoveInItemsGridAdapter mAdapter;

    private DatabaseReference notificationsReference;
    private DatabaseReference checkinReference = FirebaseDatabase.getInstance().getReference("moveIns");
    private DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");
    private StorageReference imageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_step_two, container, false);
        mAuth = FirebaseAuth.getInstance();
        getUserInfo();
        initToolbar();
        notificationsReference = FirebaseDatabase.getInstance().getReference().child("notifications");

        permissions.add(CAMERA);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        return view;
    }

    public Intent getPickImageChooserIntent(int pos) {
        position = pos;
        Uri outputFileUri = getCaptureImageOutputUri();
        imageUri = getCaptureImageOutputUri();
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getActivity().getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        //Uri outputFileUri = null;
        File getImage = getActivity().getExternalFilesDir("");
        if (getImage != null) {
            //outputFileUri = Uri.fromFile(new File(getImage.getPath(), "image.png"));
            imageUri = Uri.fromFile(new File(getImage.getPath(), "image.png"));
        }
        return imageUri;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
    }

    private void initToolbar() {
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getString(R.string.move_in_images));
    }

    public void setupRecycler(List<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
        setupItems();
        mAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mAdapter);
        //mAdapter.notifyDataSetChanged();
    }

    private void setupUI() {
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), Tools.getGridSpanCount(getActivity()));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setupItems() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String date = dateFormat.format(today);
        for (Inventory inventory : inventoryList) {
            MoveInItem item = new MoveInItem(inventory.getItemName(), mAuth.getCurrentUser().getUid(), date);
            if (!items.contains(item.getItemName())) {
                items.add(item);
            }
        }
        mAdapter = new MoveInItemsGridAdapter(getActivity(), items, StepTwoFragment.this);
        mAdapter.notifyDataSetChanged();
    }

    /* public void takePhoto(int pos) {
         position = pos;
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
         StepTwoFragment.this.startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);

     }*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == Activity.RESULT_OK) {


            if (requestCode == IMAGE_RESULT) {

                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                    try {
                        uploadImageToFirebaseStorage();
                    }catch (Exception e){
                        Toast.makeText(getActivity(),"Failed To Load", Toast.LENGTH_LONG).show();
                    }
                    mAdapter.setImageInView(position, imageUri);
                }
            }else{
                Toast.makeText(getActivity(),"Error Request Code", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getActivity(),"Error Result Code", Toast.LENGTH_LONG).show();
        }

    }

    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;

        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());

    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("imageUri", imageUri);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (imageUri != null)
            imageUri = savedInstanceState.getParcelable("imageUri");

    }


    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }
   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(selectedImage, null);
                    }
                    ContentResolver cr = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        cr = Objects.requireNonNull(getActivity()).getContentResolver();
                    }
                    Bitmap bitmap;

                    try {
                        bitmap = MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);

                        Uri selected = getImageUri(getActivity(), bitmap);
                        String realPath = getRealPathFromURI(selected);
                        selectedImage = Uri.parse(realPath);
                        Toast.makeText(getActivity(), selectedImage.toString(),
                                Toast.LENGTH_LONG).show();
                        uploadImageToFirebaseStorage();
                        mAdapter.setImageInView(position, imageUri);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }
        }
    }*/

  /*  public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }*/

    public void upload() {
        if (urls.size() != 0) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date today = Calendar.getInstance().getTime();
            String date = dateFormat.format(today);
            saveCheckinInfo(urls, mAuth.getCurrentUser().getUid(), date);
            Intent intent = new Intent(getActivity(), SuccessActivity.class);
            intent.putExtra(Constants.STRING_EXTRA, getString(R.string.str_checkin_message));
            getActivity().startActivity(intent);
            getActivity().finish();

        } else {
            Snackbar.make(view, "No Images taken", Snackbar.LENGTH_LONG).show();
        }
    }

    private void uploadImageToFirebaseStorage() {
        imageReference = FirebaseStorage.getInstance().getReference("checkinPics/" + System.currentTimeMillis() + ".jpg");

        if (imageUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            ContentResolver cr = getActivity().getContentResolver();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr, imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = imageReference.putBytes(data);
                final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
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
                            urls.add(profileImageUrl);

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
            Snackbar.make(view, "Image URI Null", Snackbar.LENGTH_LONG).show();
        }
    }

    private void saveCheckinInfo(List<String> urls, String userId, String date) {
        String Id = mAuth.getCurrentUser().getUid();
        String key = checkinReference.push().getKey();
        MoveIn moveIn = new MoveIn(userId, key, date, urls, userCity, userResidence, userRoom, items, userName);
        checkinReference.child(Id).child(key).setValue(moveIn).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendNotification("New MoveIn", Constants.MOVEIN_TYPE);
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
        Notification notification = new Notification(id, mAuth.getCurrentUser().getUid(), date, content, mAuth.getCurrentUser().getDisplayName(), type, typeId, userId, Constants.ADMIN_USER_ID, false);
        notificationsReference.child(userId).child(id).setValue(notification);
    }

    public static StepTwoFragment newInstance() {
        StepTwoFragment fragment = new StepTwoFragment();
        return fragment;
    }

    private void getUserInfo() {

        usersReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userResidence = user != null ? user.getUserResidence() : "";
                userRoom = user != null ? user.getUserRoom() : "";
                userCity = user != null ? user.getUserCity() : "";
                userName = user != null ? user.getUserFirstName() + " " + user.getUserLastName() : "";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
