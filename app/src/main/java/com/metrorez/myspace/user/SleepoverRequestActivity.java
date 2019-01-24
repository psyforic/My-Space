package com.metrorez.myspace.user;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrorez.myspace.R;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.data.Tools;
import com.metrorez.myspace.user.model.Notification;
import com.metrorez.myspace.user.model.User;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SleepoverRequestActivity extends AppCompatActivity {
    private EditText friendName, friendLastName, fromDate, toDate;
    private Spinner friendGender;
    private Button submitButton;
    private final Calendar myCalendar = Calendar.getInstance();

    private String userResidence, userRoom, userCity, userName, userEmail;
    private DatabaseReference extrasReference = FirebaseDatabase.getInstance().getReference().child("extras");
    private DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");
    private DatabaseReference notificationsReference = FirebaseDatabase.getInstance().getReference().child("notifications");
    private FirebaseAuth mAuth;

    private static final String SENDGRID_USERNAME = "myspaceMailer";
    private static final String SENDGRID_PASSWORD = "#MySpace@2018";
    private static final String TAG = "GymAccess";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleepover_request);
        initToolbar();

        mAuth = FirebaseAuth.getInstance();
        setupUI();
        setupDropdowns();
        getUserInfo();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateName() && validateDate()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SleepoverRequestActivity.this);
                    // LayoutInflater inflater = getActivity().getLayoutInflater();

                    builder.setMessage(R.string.request_gym_message)
                            .setTitle(R.string.request_gym_title);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //requestAccess();
                            Toast.makeText(SleepoverRequestActivity.this, R.string.request_success, Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Snackbar.make(view, "Fill all the required information", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        Tools.systemBarLolipop(this);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Sleepover Request");
    }

    private void setupUI() {
        friendName = findViewById(R.id.input_friend_name);
        friendLastName = findViewById(R.id.input_friend_lastname);
        fromDate = findViewById(R.id.fromDate);
        toDate = findViewById(R.id.toDate);
        friendGender = findViewById(R.id.spinnerGender);
        submitButton = findViewById(R.id.btn_request);
    }

    private void submit() {
        //TODO: implement method
    }

    private void dateFromClicked() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateFromLabel();
            }

        };
        fromDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                hideKeyboard();
                new DatePickerDialog(SleepoverRequestActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void dateToClicked() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateToLabel();
            }

        };

        toDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                hideKeyboard();
                new DatePickerDialog(SleepoverRequestActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateFromLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        fromDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateToLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        toDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void sendNotification(String content, String type) {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String date = dateFormat.format(today);
        String id = notificationsReference.push().getKey();
        String typeId = extrasReference.push().getKey();
        String userId = mAuth.getCurrentUser().getUid();
        Notification notification = new Notification(id, mAuth.getCurrentUser().getUid(), date, content, mAuth.getCurrentUser().getDisplayName(), type, typeId, userId, Constants.ADMIN_USER_ID, false);
        notificationsReference.child(id).setValue(notification);
    }

    private void getUserInfo() {
        usersReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userResidence = user != null ? user.getUserResidence() : "";
                userRoom = user != null ? user.getUserRoom() : "";
                userCity = user != null ? user.getUserCity() : "";
                userName = user != null ? user.getUserFirstName() + " " + user.getUserLastName() : null;
                userEmail = user != null ? user.getUserEmail() : null;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupDropdowns() {
        /**
         * gender dropdown
         */
        final List<String> genderList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.gender)));
        final ArrayAdapter<String> genderArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderList) {
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
                View spinnerGenderView = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) spinnerGenderView;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return spinnerGenderView;
            }
        };
        genderArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        friendGender.setAdapter(genderArrayAdapter);
    }

    private void sendEmail() {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        if (validateName() && validateDate()) {
            String toEmail = Constants.SENDGRID_TO_EMAIL;
            String fromMail = userEmail;
            String subject = Constants.EMAIL_SUBJECT;
            String gender = friendGender.getSelectedItem().toString();
            String date = dateFormat.format(today);
            String messageBody = getString(R.string.str_sleepover_msg_body, userName, gender,
                    userCity, userResidence, userRoom, userCity, fromDate, toDate);
            SendEmailAsyncTask task = new SendEmailAsyncTask(this, toEmail, fromMail, subject, messageBody);
            task.execute();
        }

    }

    private boolean validateDate() {
        return false;
    }

    private boolean validateName() {
        return false;
    }

    private class SendEmailAsyncTask extends AsyncTask<Void, Void, Void> {
        private Context mContext;
        private String mMsgResponse;

        private String mTo;
        private String mFrom;
        private String mSubject;
        private String mBody;

        public SendEmailAsyncTask(Context context, String to, String from, String subject, String body) {
            mContext = context.getApplicationContext();
            mTo = to;
            mFrom = from;
            mSubject = subject;
            mBody = body;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                SendGrid sendGrid = new SendGrid(SENDGRID_USERNAME, SENDGRID_PASSWORD);
                SendGrid.Email email = new SendGrid.Email();

                email.addTo(mTo);
                email.setFrom(mFrom);
                email.setSubject(mSubject);
                email.setText(mBody);

                SendGrid.Response response = sendGrid.send(email);
                mMsgResponse = response.getMessage();

                Log.d(TAG, mMsgResponse);
            } catch (SendGridException e) {

                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
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
