package com.saju.lifepluse.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saju.lifepluse.R;
import com.saju.lifepluse.modelclass.EmailUser;

public class SignUp extends AppCompatActivity {

    TextInputEditText nameEd, emailEd, passwordEd, confirempasswordEd;

    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;
    FirebaseFirestore database;
    EmailUser user;
    CheckBox termscondition;

    ImageView phone_signup_btn;
    String uid;
    String name;

    boolean isform_filled = false;
    boolean isform_notfilled = true;

    boolean isorgadd = false;
    boolean isorgnotadd = true;
    boolean isfindbbankadd = false;
    boolean isfindbbanknotadd = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        nameEd = findViewById(R.id.nameEd);
        emailEd = findViewById(R.id.emailEd);
        passwordEd = findViewById(R.id.passwordEd);
        confirempasswordEd = findViewById(R.id.confirempasswordEd);
        progressBar = findViewById(R.id.progress_bar);
        createAccountBtn = findViewById(R.id.create_account_btn);
        loginBtnTextView = findViewById(R.id.login_text_view_btn);
        phone_signup_btn = findViewById(R.id.phone_signup_btn);
        termscondition = findViewById(R.id.termconditionCheckboxId);

        database = FirebaseFirestore.getInstance();

        statusbarcolor();

        createAccountBtn.setOnClickListener(v -> createAccount());
        loginBtnTextView.setOnClickListener(v -> startActivity(new Intent(SignUp.this, SignIn.class)));

        phone_signup_btn.setOnClickListener(v -> startActivity(new Intent(SignUp.this, Phonenumber_SignIn.class)));

    }

    void createAccount() {

        name = nameEd.getText().toString();
        String email = emailEd.getText().toString();
        String password = passwordEd.getText().toString();
        String confirmPassword = confirempasswordEd.getText().toString();

        if (!termscondition.isChecked()) {
            Toast.makeText(this, "Please agree to the terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isValidated = validateData(name, email, password, confirmPassword);
        if (!isValidated) {
            return;
        }

        createAccountInFirebase(email, password);





    }

    void createAccountInFirebase(String email, String password) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUp.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if (task.isSuccessful()) {



                            uid = task.getResult().getUser().getUid();

                            user = new EmailUser(name, email, password, uid, Timestamp.now(), isform_filled, isform_notfilled, isorgadd,isorgnotadd, isfindbbankadd, isfindbbanknotadd);
                            database.collection("users").document(uid).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    if (task.isSuccessful()) {
                                        //creating acc is done
                                        changeInProgress(false);
                                        Toast.makeText(SignUp.this, "Successfully create account,Check email to verify", Toast.LENGTH_SHORT).show();
                                        firebaseAuth.getCurrentUser().sendEmailVerification();
                                        firebaseAuth.signOut();
                                        finish();
                                    } else {
                                        //failure
                                        Toast.makeText(SignUp.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        } else {
                            changeInProgress(false);
                            Toast.makeText(SignUp.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                }
        );


    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }


    private void statusbarcolor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Change the status bar color programmatically
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.singinsignup_bg));
        }
    }


    boolean validateData(String name, String email, String password, String confirmPassword) {
        //validate the data that are input by user.

        if (name.isEmpty()) {
            nameEd.setError("Please Enter Your Name");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEd.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6) {
            passwordEd.setError("Password length is invalid");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirempasswordEd.setError("Password not matched");
            return false;
        }
        return true;
    }

}