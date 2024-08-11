package com.saju.lifepluse.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.saju.lifepluse.R;
import com.saju.lifepluse.fragment.BloodNeed_Fragment;
import com.saju.lifepluse.fragment.DashBoard;
import com.saju.lifepluse.fragment.Profile_Fragment;
import com.saju.lifepluse.utils.FirebaseUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class DrawerLayout extends AppCompatActivity {

    MaterialToolbar materialToolbar;
    androidx.drawerlayout.widget.DrawerLayout drawerLayout;
    NavigationView navigationView;
    Drawable navigationIcon;
    public static Button loginBtn, createBtn;
    public static TextView headernameId, headeremailTv;
    public static LinearLayout header_profile_layout;

    private AppUpdateManager appUpdateManager;
    private static final int REQUEST_CODE_UPDATE = 123; // You can use any integer value
    private static final String APP_UPDATE_PREFERENCES = "AppUpdatePreferences";
    private static final String IS_UPDATED_KEY = "isUpdated";


    //===============================================for user location retrive=========================//

    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final long UPDATE_INTERVAL = 5000; // 5 seconds

    private FusedLocationProviderClient fusedLocationClient;

    public static String locationText;
    public static String userDivision;
    private LocationCallback locationCallback;

    FirebaseFirestore db;


    @SuppressLint({"MissingInflatedId", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocal();
        setContentView(R.layout.drawerlayout);


        materialToolbar = findViewById(R.id.toolbarId);
        drawerLayout = findViewById(R.id.drwerLayout);
        appUpdateManager = AppUpdateManagerFactory.create(this);
        navigationView = findViewById(R.id.navigationviewId);
        setSupportActionBar(materialToolbar);
        db = FirebaseFirestore.getInstance();

        materialToolbar.setTitle(getResources().getString(R.string.app_name));


        headerlayout(navigationView);
        statusbar();
        inappupdate();
        askNotificationPermission();
        initfirebaseNotification();


        //========================Location data get=========================//
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }

        //========================Location data get end=========================//


        //init Mobile Ads





        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, new DashBoard());
        fragmentTransaction.commit();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, materialToolbar, R.string.Drawer_Close, R.string.Drawer_Open);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.profileId) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, new DashBoard());
                    fragmentTransaction.commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (item.getItemId() == R.id.logout_Id) {

                    if (FirebaseUtil.currentuser()!=null){
                        FirebaseAuth.getInstance().signOut();
                        updateUIAfterLogout();
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }else {
                        Toast.makeText(DrawerLayout.this, "Please SignIn First", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DrawerLayout.this, SignIn.class ));
                    }




                } else if (item.getItemId() == R.id.rateBtnId) {

                    Context context = getApplicationContext();
                    if (context != null) {
                        String packageName = context.getPackageName();
                        Uri uri = Uri.parse("market://details?id=" + packageName);
                        Intent rateIntent = new Intent(Intent.ACTION_VIEW, uri);
                        rateIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);

                        try {
                            startActivity(rateIntent);
                        } catch (ActivityNotFoundException e) {
                            // Handle the case where Play Store app is not installed
                            // Open the Play Store website instead
                            Uri playStoreUri = Uri.parse("http://play.google.com/store/apps/details?id=" + packageName);
                            Intent webIntent = new Intent(Intent.ACTION_VIEW, playStoreUri);
                            startActivity(webIntent);
                        }
                    }
                    // Your rate button logic here
                    return true;
                }

                // Other menu item handling goes here...

                return false;
            }
        });


        navigationIcon = materialToolbar.getNavigationIcon();
        if (navigationIcon != null) {
            navigationIcon.mutate().setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        }


    } //==========================================Oncreate End================================//


    private void updateUIAfterLogout() {
        header_profile_layout.setVisibility(View.GONE);
        loginBtn.setVisibility(View.VISIBLE);
        createBtn.setVisibility(View.VISIBLE);
        Profile_Fragment.profile_design_layout.setVisibility(View.GONE);
        Profile_Fragment.singin_layoutforprofile.setVisibility(View.VISIBLE);
        Profile_Fragment.logout_anim_btn.setVisibility(View.GONE);


    }

    private void headerlayout(NavigationView navigationView) {

        View headerview = navigationView.getHeaderView(0);
        loginBtn = headerview.findViewById(R.id.headerlogintBtn);
        createBtn = headerview.findViewById(R.id.headercreataccountBtn);
        headernameId = headerview.findViewById(R.id.headernameId);
        headeremailTv = headerview.findViewById(R.id.headeremailTv);
        header_profile_layout  = headerview.findViewById(R.id.header_profile_layout);



        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null){

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DrawerLayout.this, SignIn.class));
                    Log.d("btn", "Login Button Clicked");
                }
            });
            createBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DrawerLayout.this, SignUp.class));
                    Log.d("btn", "SignUp Button Clicked");
                }
            });


        }else{

            loginBtn.setVisibility(View.GONE);
            createBtn.setVisibility(View.GONE);
            header_profile_layout.setVisibility(View.VISIBLE);



        }



    }

    private void inappupdate() {

        //======================in app update code start here==============//


        // Check for update availability
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                // Check if the user already updated the app
                SharedPreferences sharedPreferences = getSharedPreferences(APP_UPDATE_PREFERENCES, Context.MODE_PRIVATE);
                boolean isUpdated = sharedPreferences.getBoolean(IS_UPDATED_KEY, false);

                if (!isUpdated) {
                    // Request an immediate update
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                this, // Your activity reference
                                REQUEST_CODE_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //======================in app update code end here==============//
    }

    private void statusbar() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Change the status bar color programmatically
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar));
        }

    }


    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        updateLocation(location);
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        getAddressFromLocation(latitude, longitude);
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String district = address.getSubAdminArea();
                 userDivision = address.getAdminArea();

                locationText = district + ", " + userDivision;
            } else {
                Log.d(TAG, "No address found");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error getting address: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    //========================Location data get method end=========================//

    // Check for update availability
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE) {
            if (resultCode != RESULT_OK) {
                // Handle update failure
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            } else {
                // Update completed successfully, save the flag indicating update completion
                SharedPreferences sharedPreferences = getSharedPreferences(APP_UPDATE_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(IS_UPDATED_KEY, true);
                editor.apply();
            }
        }
    }


    private boolean isFirstBackPressed = true;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            int backStackEntryCount = fragmentManager.getBackStackEntryCount();

            if (backStackEntryCount > 0) {
                // If there are fragments in the back stack, pop the back stack
                fragmentManager.popBackStackImmediate();
            } else {
                Fragment currentFragment = getCurrentFragment();

                if (currentFragment instanceof DashBoard) {
                    if (isFirstBackPressed) {
                        // If it's the first back press in DashboardFragment, show a toast or perform any other action
                        Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
                        isFirstBackPressed = false;
                    } else {
                        // Show the exit dialog box
                        new AlertDialog.Builder(this)
                                .setTitle("Exit App")
                                .setIcon(R.drawable.alert_icon)
                                .setMessage("Are you sure you want to exit?")
                                .setNeutralButton("Rate Us", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Context context = getApplicationContext();
                                        if (context != null) {
                                            String packageName = context.getPackageName();
                                            Uri uri = Uri.parse("market://details?id=" + packageName);
                                            Intent rateIntent = new Intent(Intent.ACTION_VIEW, uri);
                                            rateIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);

                                            try {
                                                startActivity(rateIntent);
                                            } catch (ActivityNotFoundException e) {
                                                // Handle the case where Play Store app is not installed
                                                // Open the Play Store website instead
                                                Uri playStoreUri = Uri.parse("http://play.google.com/store/apps/details?id=" + packageName);
                                                Intent webIntent = new Intent(Intent.ACTION_VIEW, playStoreUri);
                                                startActivity(webIntent);
                                            }
                                        }
                                    }
                                })
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                } else {
                    // Replace the current fragment with DashboardFragment
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, new DashBoard());
                    fragmentTransaction.commit();
                }
            }
        }
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.framelayout);
    }


    //==================================facebook fullscreen ads===================


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_language) {
            showChangLanguageDialog();


            return true;
        } else if (id == R.id.action_notificaton) {

        }

        return super.onOptionsItemSelected(item);
    }

    private void showChangLanguageDialog() {
        final String[] listItems = {"Bangla", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DrawerLayout.this);
        mBuilder.setTitle("Choose Language...");

        // Get the previously selected language
        String currentLanguage = getCurrentLanguage(); // Replace this with your method to get the current language

        // Set a default selected item based on the current language
        int checkedItem = currentLanguage.equals("bn") ? 0 : 1;

        // Create the single-choice items list
        mBuilder.setSingleChoiceItems(listItems, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle selection as before
                if (which == 0) {
                    setLocale("bn");
                    recreate();

                } else if (which == 1) {
                    setLocale("en");
                    recreate();
                }

                dialog.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }


    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());


        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();


    }


    public void loadLocal() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }

    private String getCurrentLanguage() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        return prefs.getString("My_Lang", ""); // Returns the saved language or empty string if not found
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });
    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                AlertDialog.Builder alerdialog = new AlertDialog.Builder(DrawerLayout.this);
                alerdialog.setTitle("Permission Needed");
                alerdialog.setMessage("This is permission is required for better performance");
                alerdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);

                    }
                });
                alerdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                alerdialog.show();


            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void initfirebaseNotification() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Firebasetoken", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d("Firebasetoken", token);

                        sendTokentoFirebase(token);


                    }
                });
    }

    private void sendTokentoFirebase(String token) {
        // Get the Firebase installation ID
        FirebaseInstallations.getInstance().getId()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String installationId) {
                        // Create a reference to the Firestore collection where you want to store the token
                        DocumentReference userRef = db.collection("devices").document(installationId);

                        // Create a map with the token data
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("fcmToken", token);

                        // Set the data for the document
                        userRef.set(userData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "FCM token stored successfully for device: " + installationId);
                                        // Handle success if needed
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Error storing FCM token for device: " + installationId + ", Error: " + e.getMessage());
                                        // Handle failure if needed
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting Firebase installation ID: " + e.getMessage());
                        // Handle failure if needed
                    }
                });
    }



}