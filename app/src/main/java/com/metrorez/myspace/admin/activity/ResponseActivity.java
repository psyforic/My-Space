package com.metrorez.myspace.admin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.adapter.ResponseDetailsListAdapter;
import com.metrorez.myspace.admin.model.ResponseDetails;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Notification;
import com.metrorez.myspace.user.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ResponseActivity extends AppCompatActivity {
    public static final String KEY_USER = "USER";
    public static final String KEY_DATA = "DATA";
    public static final String KEY_DATE = "DATE";
    public static final String KEY_IMAGE = "IMAGE";
    private ImageView imageView;
    private FirebaseAuth mAuth;
    private DatabaseReference notificationsReference = FirebaseDatabase.getInstance().getReference("notifications");

    public static void navigate(AppCompatActivity activity, View transitionImage, User obj, String snippet, String date) {
        Intent intent = new Intent(activity, ResponseActivity.class);
        intent.putExtra(KEY_USER, obj);
        intent.putExtra(KEY_DATA, snippet);
        intent.putExtra(KEY_DATE, date);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, KEY_USER);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void navigate(AppCompatActivity activity, View transitionImage, User obj, String snippet, String date, String imageUrl) {
        Intent intent = new Intent(activity, ResponseActivity.class);
        intent.putExtra(KEY_USER, obj);
        intent.putExtra(KEY_DATA, snippet);
        intent.putExtra(KEY_DATE, date);
        intent.putExtra(KEY_IMAGE, imageUrl);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, KEY_USER);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private Button btn_send;
    private EditText et_content;
    public static ResponseDetailsListAdapter adapter;

    private ListView listview;
    private ActionBar actionBar;
    private User user;
    private List<ResponseDetails> items = new ArrayList<>();
    private View parent_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AdminTheme);
        setContentView(R.layout.activity_response);
        parent_view = findViewById(android.R.id.content);
        mAuth = FirebaseAuth.getInstance();
        // animation transition
        ViewCompat.setTransitionName(parent_view, KEY_USER);
        // initialize conversation data
        Intent intent = getIntent();
        user = (User) intent.getExtras().getSerializable(KEY_USER);
        String snippet = intent.getStringExtra(KEY_DATA);
        //String snippet = Html.fromHtml(data).toString();
        String date = intent.getStringExtra(KEY_DATE);
        String imageUrl = intent.getStringExtra(KEY_IMAGE);
        initToolbar();
        iniComponent();
        if (imageUrl != null && snippet != null) {
            items.add(new ResponseDetails(999, date, user, snippet, imageUrl, false));
        } else if (snippet != null && imageUrl == null) {
            items.add(new ResponseDetails(999, date, user, snippet, false));
        }
        //items.addAll(Constants.getChatDetailsData(this, user));
        adapter = new ResponseDetailsListAdapter(this, items);
        listview.setAdapter(adapter);
        listview.setSelectionFromTop(adapter.getCount(), 0);
        listview.requestFocus();
        registerForContextMenu(listview);
        // for system bar in lollipop
        Tools.adminSystemBarLollipop(this);
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(user.getUserFirstName() + " " + user.getUserLastName());
    }

    public void bindView() {
        try {
            adapter.notifyDataSetChanged();
            listview.setSelectionFromTop(adapter.getCount(), 0);
        } catch (Exception e) {

        }
    }

    public void iniComponent() {
        listview = (ListView) findViewById(R.id.listview);
        btn_send = (Button) findViewById(R.id.btn_send);
        et_content = (EditText) findViewById(R.id.text_content);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                items.add(items.size(), new ResponseDetails(items.size(), Constants.formatTime(System.currentTimeMillis()), user, et_content.getText().toString(), true));
                //items.add(items.size(), new ResponseDetails(items.size(), Constants.formatTime(System.currentTimeMillis()), user, et_content.getText().toString(), false));
                sendNotification(et_content.getText().toString(), Constants.ADMIN_COMPLAINT_RESPONSE, user.getUserId());
                et_content.setText("");
                bindView();
                hideKeyboard();
            }
        });
        et_content.addTextChangedListener(contentWatcher);
        if (et_content.length() == 0) {
            btn_send.setEnabled(false);
        }

        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private TextWatcher contentWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable etd) {
            if (etd.toString().trim().length() == 0) {
                btn_send.setEnabled(false);
            } else {
                btn_send.setEnabled(true);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_response_details, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Handle click on action bar
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                //onBackPressed();
                finish();
                return true;
            case R.id.admin_notification:
                Snackbar.make(parent_view, item.getTitle() + " Clicked ", Snackbar.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendNotification(String content, String type, String userId) {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String date = dateFormat.format(today);
        String id = notificationsReference.push().getKey();
        String typeId = notificationsReference.push().getKey();

        Notification notification = new Notification(id, mAuth.getCurrentUser().getUid(), date, content, "ADMIN", type, typeId, userId, userId, false);
        notificationsReference.child(id).setValue(notification);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(user.getUserFirstName() + " " + user.getUserLastName());

    }
}
