package com.metrorez.myspace;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.metrorez.myspace.data.Tools;
import com.metrorez.myspace.model.User;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "LOGGED";
    //FIREBASE AUTHENTICATION FIELDS
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    private EditText inputName, inputEmail, inputLastName, inputStudentNo,
            inputPassword, inputConfirmPassword;
    private Button btnSignUp, btnLogin;
    private View parent_view;
    DatabaseReference usersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupUI();
        mAuth = FirebaseAuth.getInstance();
        usersDatabase = FirebaseDatabase.getInstance().getReference("users");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {

                }
            }
        };

        submitForm();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        //Snackbar.make(parent_view, "Registration Success", Snackbar.LENGTH_SHORT).show();
        hideKeyboard();
        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }


    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setupUI() {

        parent_view = findViewById(android.R.id.content);
        inputName = (EditText) findViewById(R.id.input_name);
        inputLastName = (EditText) findViewById(R.id.input_lastname);
        inputStudentNo = (EditText) findViewById(R.id.input_student_no);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputConfirmPassword = (EditText) findViewById(R.id.input_confirm_password);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_sign_in);
    }

    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validateLastName()) {
            return;
        }

        if (!validateStudentNo()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        if (!validateConfirmPassword()) {
            return;
        }

        register();
    }

    private void register() {
        String userEmail, userPassword;
        userEmail = inputEmail.getText().toString().trim();
        userPassword = inputPassword.getText().toString().trim();
        addUser();
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");

                    Snackbar.make(parent_view, "User Account Created", Snackbar.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Snackbar.make(parent_view, "User Account Registration Failed", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            //inputLayoutName.setError(getString(R.string.err_msg_name));
            Snackbar.make(parent_view, getString(R.string.err_msg_name), Snackbar.LENGTH_SHORT).show();
            requestFocus(inputName);
            return false;
        }
        return true;
    }

    private boolean validateLastName() {
        if (inputLastName.getText().toString().trim().isEmpty()) {
            Snackbar.make(parent_view, getString(R.string.err_msg_lastname), Snackbar.LENGTH_SHORT).show();
            requestFocus(inputLastName);
            return false;
        }
        return true;
    }

    private boolean validateStudentNo() {
        if (inputStudentNo.getText().toString().trim().isEmpty()) {
            Snackbar.make(parent_view, getString(R.string.err_msg_studentNo), Snackbar.LENGTH_SHORT).show();
            requestFocus(inputStudentNo);
            return false;
        }
        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            Snackbar.make(parent_view, getString(R.string.err_msg_email), Snackbar.LENGTH_SHORT).show();
            requestFocus(inputEmail);
            return false;
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePassword() {
        String password = inputPassword.getText().toString().trim();
        if (password.isEmpty() || password.length() < 8) {
            Snackbar.make(parent_view, getString(R.string.err_msg_password), Snackbar.LENGTH_SHORT).show();
            requestFocus(inputPassword);

            return false;
        }
        return true;
    }

    private boolean validateConfirmPassword() {
        String confirmPassword = inputConfirmPassword.getText().toString().trim();
        int passwordLength = inputPassword.getText().toString().trim().length();
        if (confirmPassword.isEmpty() || confirmPassword.length() != passwordLength) {
            Snackbar.make(parent_view, getString(R.string.err_msg_confirm_password), Snackbar.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void addUser() {
        String firstName = inputName.getText().toString().trim();
        String lastName = inputLastName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String studentNo = inputStudentNo.getText().toString().trim();


        if (validateName() && validateEmail() && validateLastName() && validateStudentNo()) {
            String userId = usersDatabase.push().getKey();
            User user = new User(userId, firstName, lastName, email, studentNo);
            usersDatabase.child(userId).setValue(user);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
