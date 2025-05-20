package com.saju.lifepluse.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.saju.lifepluse.R;

import java.util.Objects;
import java.util.regex.Pattern;

public class Contact_US extends AppCompatActivity {

    private TextInputEditText nameInput, emailInput, subjectInput, messageInput;
    private View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        initializeViews();
        setupToolbar();
        setupStatusBar();
        setupSubmitButton();
    }

    private void initializeViews() {
        parentLayout = findViewById(android.R.id.content);
        nameInput = findViewById(R.id.input_name);
        emailInput = findViewById(R.id.input_email);
        subjectInput = findViewById(R.id.input_subject);
        messageInput = findViewById(R.id.input_message);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.primary_dark));

            // Set light status bar icons
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                );
            }
        }
    }

    private void setupSubmitButton() {
        findViewById(R.id.submit_button).setOnClickListener(v -> {
            String name = Objects.toString(nameInput.getText(), "").trim();
            String email = Objects.toString(emailInput.getText(), "").trim();
            String subject = Objects.toString(subjectInput.getText(), "").trim();
            String message = Objects.toString(messageInput.getText(), "").trim();

            if (validateInputs(name, email, message)) {
                sendEmail(name, email, subject, message);
            }
        });
    }

    private boolean validateInputs(String name, String email, String message) {
        if (name.isEmpty()) {
            showError("Please enter your name", nameInput);
            return false;
        }

        if (email.isEmpty() || !isValidEmail(email)) {
            showError("Please enter a valid email address", emailInput);
            return false;
        }

        if (message.isEmpty()) {
            showError("Please enter your message", messageInput);
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        return Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
                .matcher(email)
                .matches();
    }

    private void showError(String message, View view) {
        view.requestFocus();
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void sendEmail(String name, String email, String subject, String message) {
        String fullMessage = String.format(
                "Name: %s\nEmail: %s\nSubject: %s\n\nMessage:\n%s",
                name, email, subject, message
        );

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:developer.bdboy@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact Us: " + subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, fullMessage);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        } catch (Exception e) {
            Snackbar.make(parentLayout, "No email app found", Snackbar.LENGTH_LONG)
                    .setAction("Install", v -> openEmailClientStore())
                    .show();
        }
    }

    private void openEmailClientStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.google.android.gm")));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gm")));
        }
    }
}