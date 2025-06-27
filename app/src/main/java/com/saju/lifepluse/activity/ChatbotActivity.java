package com.saju.lifepluse.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.saju.lifepluse.R; // <<<--- THIS IS THE MISSING IMPORT

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatbotActivity extends AppCompatActivity {

    private static final String TAG = "ChatbotActivity";

    private LinearLayout chatLayout;
    private FirebaseFirestore db;
    private NestedScrollView scrollView;
    private ProgressBar progressBar;
    private String currentLocation;
    private int itemMargin;
    private MaterialButton btnHospitals, btnDonors, btnBanks;

    // TTS variables
    private TextToSpeech textToSpeech;
    private boolean isTtsInitialized = false;

    // Language management
    private String currentLanguage = "en"; // Default: English
    private Map<String, Map<String, String>> languageResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        // NOTE: The standard Android approach is to use XML string resources
        // (e.g., res/values/strings.xml and res/values-bn/strings.xml).
        // This is more scalable and integrates better with the Android system.
        initializeLanguageResources();
        initializeViews();
        setupFirestore();
        showLanguageSelection();
    }

    private void initializeLanguageResources() {
        languageResources = new HashMap<>();

        // English resources
        Map<String, String> enResources = new HashMap<>();
        enResources.put("health_assistant_title", "Health Assistant");
        enResources.put("welcome", "Hi there! I'm your Health Assistant. How can I help you today?");
        enResources.put("quick_options", "You can tap the buttons below or tell me what you need:");
        enResources.put("select_blood", "Which blood type are you looking for?");
        enResources.put("hospitals", "🏥 Nearby Hospitals");
        enResources.put("donors", "💉 Blood Donors");
        enResources.put("banks", "🩸 Blood Banks");
        enResources.put("showing_hospitals", "We are showing nearby hospitals for you");
        enResources.put("showing_donors", "Please select a blood type");
        enResources.put("showing_banks", "We are showing blood banks near you");
        enResources.put("found_hospitals", "We found %d hospitals near you");
        enResources.put("no_hospitals", "Sorry, no hospitals found in your location");
        enResources.put("found_donors", "We found %d %s donors near you");
        enResources.put("no_donors", "Sorry, no %s donors found in your location");
        enResources.put("found_banks", "We found %d blood banks near you");
        enResources.put("no_banks", "Sorry, no blood banks found in your location");
        enResources.put("error", "Failed to load data. Please try again.");
        enResources.put("english", "English");
        enResources.put("bangla", "Bangla");
        enResources.put("select_lang", "Please select your language / দয়া করে আপনার ভাষা নির্বাচন করুন");
        languageResources.put("en", enResources);

        // Bangla resources
        Map<String, String> bnResources = new HashMap<>();
        bnResources.put("health_assistant_title", "স্বাস্থ্য সহায়ক");
        bnResources.put("welcome", "হাই! আমি আপনার স্বাস্থ্য সহায়ক। আজকে আমি আপনাকে কিভাবে সাহায্য করতে পারি?");
        bnResources.put("quick_options", "আপনি নিচের বাটন টেপ করতে পারেন বা আমাকে বলতে পারেন আপনার কী দরকার:");
        bnResources.put("select_blood", "আপনি কোন রক্তের গ্রুপ খুঁজছেন?");
        bnResources.put("hospitals", "🏥 কাছাকাছি হাসপাতাল");
        bnResources.put("donors", "💉 রক্ত দাতা");
        bnResources.put("banks", "🩸 ব্লাড ব্যাংক");
        bnResources.put("showing_hospitals", "আমরা আপনার কাছাকাছি হাসপাতাল দেখাচ্ছি");
        bnResources.put("showing_donors", "দয়া করে একটি রক্তের গ্রুপ নির্বাচন করুন");
        bnResources.put("showing_banks", "আমরা আপনার কাছাকাছি ব্লাড ব্যাংক দেখাচ্ছি");
        bnResources.put("found_hospitals", "আমরা আপনার কাছাকাছি %d টি হাসপাতাল পেয়েছি");
        bnResources.put("no_hospitals", "দুঃখিত, আপনার এলাকায় কোন হাসপাতাল পাওয়া যায়নি");
        bnResources.put("found_donors", "আমরা আপনার কাছাকাছি %d জন %s রক্ত দাতা পেয়েছি");
        bnResources.put("no_donors", "দুঃখিত, আপনার এলাকায় %s রক্ত দাতা পাওয়া যায়নি");
        bnResources.put("found_banks", "আমরা আপনার কাছাকাছি %d টি ব্লাড ব্যাংক পেয়েছি");
        bnResources.put("no_banks", "দুঃখিত, আপনার এলাকায় কোন ব্লাড ব্যাংক পাওয়া যায়নি");
        bnResources.put("error", "ডেটা লোড করতে ব্যর্থ হয়েছে। দয়া করে আবার চেষ্টা করুন।");
        bnResources.put("english", "ইংরেজি");
        bnResources.put("bangla", "বাংলা");
        bnResources.put("select_lang", "Please select your language / দয়া করে আপনার ভাষা নির্বাচন করুন");
        languageResources.put("bn", bnResources);
    }

    private String getString(String key) {
        if (languageResources.containsKey(currentLanguage) && languageResources.get(currentLanguage).containsKey(key)) {
            return languageResources.get(currentLanguage).get(key);
        }
        // Fallback to English if the key is missing in the selected language to prevent crashes.
        Log.w(TAG, "String key '" + key + "' not found for language '" + currentLanguage + "'. Falling back to English.");
        return languageResources.get("en").get(key);
    }

    private String getString(String key, Object... args) {
        String format = getString(key);
        return String.format(format, args);
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        chatLayout = findViewById(R.id.chat_layout);
        scrollView = findViewById(R.id.chat_scroll);
        progressBar = findViewById(R.id.progress_bar);
        btnHospitals = findViewById(R.id.btn_hospitals);
        btnDonors = findViewById(R.id.btn_donors);
        btnBanks = findViewById(R.id.btn_banks);

        itemMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()
        );
    }

    private void setupFirestore() {
        db = FirebaseFirestore.getInstance();
        // WARNING: Using a static variable is a fragile pattern. Data should be passed
        // between Activities using Intent extras for better stability and testability.
        // currentLocation = DrawerLayout.locationText;

        // Adding a robustness check to prevent crashes if location is not available.
        if (TextUtils.isEmpty(currentLocation)) {
            Log.e(TAG, "Current location is NULL or EMPTY. Location-based queries will not work as expected.");
            currentLocation = ""; // Prevent NullPointerException in queries
        }
        Log.d(TAG, "Current location set to: " + currentLocation);
    }

    private void showLanguageSelection() {
        runOnUiThread(() -> {
            addBotMessage(getString("select_lang"));

            LinearLayout langLayout = new LinearLayout(this);
            langLayout.setOrientation(LinearLayout.HORIZONTAL);
            langLayout.setGravity(Gravity.CENTER);

            langLayout.addView(createLanguageButton(getString("english"), "en"));
            langLayout.addView(createLanguageButton(getString("bangla"), "bn"));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 16, 0, 16);
            langLayout.setLayoutParams(params);

            chatLayout.addView(langLayout);
            scrollToBottom();
        });
    }

    private MaterialButton createLanguageButton(String text, String langCode) {
        MaterialButton button = new MaterialButton(this);
        button.setText(text);
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        button.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        button.setCornerRadius(16);
        button.setPadding(32, 16, 32, 16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 0, 16, 0);
        button.setLayoutParams(params);

        button.setOnClickListener(v -> {
            currentLanguage = langCode;
            startMainChatFlow();
        });

        return button;
    }

    /**
     * Sets up the main chat interface after a language is selected.
     * This decouples the UI from the TTS system, making the app more responsive.
     */
    private void startMainChatFlow() {
        // 1. Clear the language selection UI
        chatLayout.removeAllViews();
        // 2. Update all UI text to the selected language
        updateUiTexts();
        // 3. Make the main action buttons functional
        setupOptionsBar();
        // 4. Display the initial messages on the screen
        addBotMessage(getString("welcome"));
        addBotMessage(getString("quick_options"));
        // 5. Initialize TTS as an enhancement to speak the greeting. The app is already functional.
        initializeTTS();
    }

    private void updateUiTexts() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString("health_assistant_title"));
        }
        btnHospitals.setText(getString("hospitals"));
        btnDonors.setText(getString("donors"));
        btnBanks.setText(getString("banks"));
    }

    private void setupOptionsBar() {
        btnHospitals.setOnClickListener(v -> handleOptionClick(getString("hospitals")));
        btnDonors.setOnClickListener(v -> handleOptionClick(getString("donors")));
        btnBanks.setOnClickListener(v -> handleOptionClick(getString("banks")));
    }

    private void initializeTTS() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Locale locale = currentLanguage.equals("bn") ? new Locale("bn", "BD") : Locale.US;
                int result = textToSpeech.setLanguage(locale);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Language not supported, falling back to English");
                    textToSpeech.setLanguage(Locale.US);
                }
                isTtsInitialized = true;
                textToSpeech.setPitch(currentLanguage.equals("bn") ? 1.0f : 1.1f);
                textToSpeech.setSpeechRate(0.9f);
                // Speak the welcome message which is already on the screen
                speakOut(getString("welcome"));
            } else {
                Log.e(TAG, "TTS Initialization failed");
            }
        });
    }

    private void handleOptionClick(String option) {
        addUserMessage(option);

        String speechResponse = "";
        if (option.equals(getString("hospitals"))) {
            speechResponse = getString("showing_hospitals");
            fetchHospitals();
        } else if (option.equals(getString("donors"))) {
            speechResponse = getString("showing_donors");
            showBloodTypeOptions();
        } else if (option.equals(getString("banks"))) {
            speechResponse = getString("showing_banks");
            fetchBloodBanks();
        }
        speakOut(speechResponse);
    }

    private void addBotMessage(String message) {
        runOnUiThread(() -> {
            View view = getLayoutInflater().inflate(R.layout.chatbot_item_bot, null);
            TextView msg = view.findViewById(R.id.bot_message);
            msg.setText(message);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, itemMargin, 0, itemMargin);
            params.gravity = Gravity.START;
            view.setLayoutParams(params);

            chatLayout.addView(view);
            scrollToBottom();
        });
    }

    private void addUserMessage(String message) {
        runOnUiThread(() -> {
            View view = getLayoutInflater().inflate(R.layout.chatbot_item_user, null);
            TextView msg = view.findViewById(R.id.user_message);
            msg.setText(message);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, itemMargin, 0, itemMargin);
            params.gravity = Gravity.END;
            view.setLayoutParams(params);

            chatLayout.addView(view);
            scrollToBottom();
        });
    }

    private void showBloodTypeOptions() {
        runOnUiThread(() -> {
            addBotMessage(getString("select_blood"));

            GridLayout gridLayout = new GridLayout(this);
            gridLayout.setColumnCount(2);
            gridLayout.setPadding(16, 16, 16, 16);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 16, 0, 16);
            gridLayout.setLayoutParams(layoutParams);

            List<String> bloodTypes = Arrays.asList(
                    "A Positive", "B Positive", "O Positive", "AB Positive",
                    "A Negative", "B Negative", "O Negative", "AB Negative"
            );

            for (String bloodType : bloodTypes) {
                MaterialButton chip = new MaterialButton(this, null,
                        com.google.android.material.R.attr.materialButtonOutlinedStyle);
                chip.setText(bloodType);
                chip.setBackgroundColor(ContextCompat.getColor(this, R.color.blood_red_light));
                chip.setTextColor(ContextCompat.getColor(this, R.color.blood_red));
                chip.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blood_red)));
                chip.setStrokeWidth(2);
                chip.setCornerRadius(16);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.setMargins(8, 8, 8, 8);
                chip.setLayoutParams(params);

                if (bloodType.contains("Positive")) {
                    chip.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_blood_plus));
                } else {
                    chip.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_blood_minus));
                }
                chip.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blood_red)));
                chip.setIconPadding(4);

                chip.setOnClickListener(v -> {
                    addUserMessage(bloodType);
                    speakOut(currentLanguage.equals("bn") ?
                            bloodType + " রক্ত দাতা খুঁজছি" :
                            "Searching " + bloodType + " donors");
                    fetchBloodDonors(bloodType);
                });
                gridLayout.addView(chip);
            }
            chatLayout.addView(gridLayout);
            scrollToBottom();
        });
    }

    private void fetchHospitals() {
        showLoading();
        // NOTE: Client-side filtering is inefficient. The database schema should include
        // a normalized location field (e.g., 'district') for server-side querying.
        // Fetching a larger limit to increase chances of finding a match.
        db.collectionGroup("hospital_list")
                .limit(50)
                .get()
                .addOnCompleteListener(task -> {
                    hideLoading();
                    int foundCount = 0;
                    StringBuilder builder = new StringBuilder();

                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String name = doc.getString("hpname_eng");
                            String address = doc.getString("hp_address");
                            String mobile = doc.getString("hpmobile");

                            if (isLocationMatch(address, currentLocation)) {
                                foundCount++;
                                builder.append("• ").append(TextUtils.isEmpty(name) ? "N/A" : name)
                                        .append("\n📍 ").append(TextUtils.isEmpty(address) ? "Address not available" : address)
                                        .append("\n📞 ").append(TextUtils.isEmpty(mobile) ? "N/A" : mobile)
                                        .append("\n\n");
                            }
                        }
                    }

                    String message;
                    if (foundCount > 0) {
                        message = getString("hospitals") + ":\n\n" + builder.toString();
                        speakOut(getString("found_hospitals", foundCount));
                    } else {
                        message = getString("no_hospitals");
                        speakOut(getString("no_hospitals"));
                    }
                    addBotMessage(message);
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    addBotMessage(getString("error"));
                    speakOut(getString("error"));
                    Log.e(TAG, "Error fetching hospitals", e);
                });
    }

    private void fetchBloodDonors(String bloodType) {
        showLoading();
        // IMPROVEMENT: Use server-side filtering for better performance and accuracy.
        // This assumes a 'district' field exists and matches the 'currentLocation' string.
        // A composite index on ('district', 'bloodType') may be required in Firestore.
        Query query = db.collectionGroup("bloodDoner").whereEqualTo("bloodType", bloodType);
        if (!TextUtils.isEmpty(currentLocation)) {
            query = query.whereEqualTo("district", currentLocation);
        }

        query.limit(20).get().addOnCompleteListener(task -> {
            hideLoading();
            int foundCount = 0;
            StringBuilder builder = new StringBuilder();

            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                foundCount = task.getResult().size();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String name = doc.getString("name");
                    String district = doc.getString("district");
                    String mobile = doc.getString("mobile");

                    builder.append("• ").append(TextUtils.isEmpty(name) ? "Anonymous" : name)
                            .append("\n📍 ").append(TextUtils.isEmpty(district) ? "Location not available" : district)
                            .append("\n📞 ").append(TextUtils.isEmpty(mobile) ? "Contact not available" : mobile)
                            .append("\n\n");
                }
            }

            String message;
            if (foundCount > 0) {
                message = getString("donors") + " (" + bloodType + "):\n\n" + builder.toString();
                speakOut(getString("found_donors", foundCount, bloodType));
            } else {
                message = getString("no_donors", bloodType);
                speakOut(getString("no_donors", bloodType));
            }
            addBotMessage(message);
        }).addOnFailureListener(e -> {
            hideLoading();
            addBotMessage(getString("error"));
            speakOut(getString("error"));
            Log.e(TAG, "Error fetching donors", e);
        });
    }

    private void fetchBloodBanks() {
        showLoading();
        // NOTE: Same inefficiency as fetchHospitals. A normalized location field is needed
        // in the 'BloodOrganization' collection for server-side filtering.
        db.collectionGroup("BloodOrganization")
                .limit(50)
                .get()
                .addOnCompleteListener(task -> {
                    hideLoading();
                    int foundCount = 0;
                    StringBuilder builder = new StringBuilder();

                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String name = doc.getString("orgname_Ed");
                            String address = doc.getString("orgaddress_Ed");
                            String phone = doc.getString("orgphone_Ed");

                            if (isLocationMatch(address, currentLocation)) {
                                foundCount++;
                                builder.append("• ").append(TextUtils.isEmpty(name) ? "N/A" : name)
                                        .append("\n📍 ").append(TextUtils.isEmpty(address) ? "Address not available" : address)
                                        .append("\n📞 ").append(TextUtils.isEmpty(phone) ? "N/A" : phone)
                                        .append("\n\n");
                            }
                        }
                    }

                    String message;
                    if (foundCount > 0) {
                        message = getString("banks") + ":\n\n" + builder.toString();
                        speakOut(getString("found_banks", foundCount));
                    } else {
                        message = getString("no_banks");
                        speakOut(getString("no_banks"));
                    }
                    addBotMessage(message);
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    addBotMessage(getString("error"));
                    speakOut(getString("error"));
                    Log.e(TAG, "Error fetching blood banks", e);
                });
    }

    private void showLoading() {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            scrollToBottom();
        });
    }

    private void hideLoading() {
        runOnUiThread(() -> progressBar.setVisibility(View.GONE));
    }

    private void scrollToBottom() {
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private boolean isLocationMatch(String address, String location) {
        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(location)) {
            return false;
        }

        String addrLower = address.toLowerCase().trim();
        String locLower = location.toLowerCase().trim();

        // Check for direct substring match first
        return addrLower.contains(locLower);
    }

    private void speakOut(String text) {
        if (isTtsInitialized && !TextUtils.isEmpty(text)) {
            // Speak only if the message is reasonably short to avoid long, unskippable narrations.
            if (text.split("\\s+").length <= 30) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Log.d(TAG, "Skipping TTS for long message: " + text.substring(0, 50) + "...");
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}