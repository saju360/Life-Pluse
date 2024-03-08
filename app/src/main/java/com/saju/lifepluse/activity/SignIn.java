package com.saju.lifepluse.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.saju.lifepluse.R;

public class SignIn extends AppCompatActivity {

    TextInputEditText emailEd, passwordEd;

    ImageView phone_login_btn;
    Button loginBtn;
    ProgressBar progressBar;
    TextView createAccountBtnTextView, forgetpasswordBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailEd = findViewById(R.id.emailEd);
        passwordEd = findViewById(R.id.passwordEd);
        forgetpasswordBtn = findViewById(R.id.forgetpasswordId);
        loginBtn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progress_bar);
        phone_login_btn = findViewById(R.id.phone_login_btn);
        createAccountBtnTextView = findViewById(R.id.create_account_text_view_btn);

        statusbarcolor();

        loginBtn.setOnClickListener((v) -> loginUser());
        createAccountBtnTextView.setOnClickListener((v) -> startActivity(new Intent(SignIn.this, SignUp.class)));
        forgetpasswordBtn.setOnClickListener(v -> showForgotPasswordDialog());
        phone_login_btn.setOnClickListener(v -> startActivity(new Intent(SignIn.this, Phonenumber_SignIn.class)));

    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setCancelable(false);

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("Enter Your Registared Email");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = input.getText().toString().trim();
                Log.d("resetemail", email);
                if (!TextUtils.isEmpty(email)) {
                    sendPasswordResetEmail(email);
                } else {
                    // Handle empty email
                    Toast.makeText(SignIn.this, "Enter your email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void sendPasswordResetEmail(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignIn.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle errors
                            Toast.makeText(SignIn.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    void loginUser() {
        String email = emailEd.getText().toString();
        String password = passwordEd.getText().toString();


        boolean isValidated = validateData(email, password);
        if (!isValidated) {
            return;
        }

        loginAccountInFirebase(email, password);

    }

    void loginAccountInFirebase(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()) {
                    //login is success
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        //go to mainactivity
                        startActivity(new Intent(SignIn.this, BloodBank.class));
                        DrawerLayout.header_profile_layout.setVisibility(View.VISIBLE);
                        DrawerLayout.loginBtn.setVisibility(View.GONE);
                        DrawerLayout.createBtn.setVisibility(View.GONE);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Email not verified, Please verify your email.", Toast.LENGTH_SHORT).show();

                    }

                } else {
                    //login failed
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password) {
        //validate the data that are input by user.

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEd.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6) {
            passwordEd.setError("Password length is invalid");
            return false;
        }
        return true;
    }


    private void statusbarcolor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Change the status bar color programmatically
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.singinsignup_bg));
        }
    }


}