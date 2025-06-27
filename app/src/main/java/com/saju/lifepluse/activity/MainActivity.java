package com.saju.lifepluse.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.saju.lifepluse.R;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private MedicineAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private Toolbar toolbar;
    private View emptyView;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Notification permission denied. Reminders may not work.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Initialize Firebase and Views ---
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance(); // <-- FIX: Initialize mAuth only once here

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab_add_medicine);
        emptyView = findViewById(R.id.emptyView);

        statusbar();


        // --- Setup UI Components ---
        setSupportActionBar(toolbar);
        fab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddMedicineActivity.class)));
        askNotificationPermission();
        setupRecyclerView();
        setupItemTouchHelper();
    }

    private void setupRecyclerView() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // This should be handled in onStart, but as a safeguard:
            startActivity(new Intent(this, SignIn.class));
            finish();
            return;
        }

        String userId = user.getUid();
        Query query = db.collection("users").document(userId)
                .collection("medicines").orderBy("time", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Medicine> options = new FirestoreRecyclerOptions.Builder<Medicine>()
                .setQuery(query, Medicine.class)
                .build();

        // --- FIX: THIS IS THE CORRECT WAY TO INITIALIZE THE ADAPTER ONCE ---
        // Create the adapter with the onDataChanged override to handle the empty view
        adapter = new MedicineAdapter(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }
        };
        // --- END OF FIX ---

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null); // Correctly disables buggy animations
        recyclerView.setAdapter(adapter);
    }

    private void setupItemTouchHelper() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return; // Safeguard against race conditions
                Medicine medicineToDelete = adapter.getItem(position);
                cancelAlarm(medicineToDelete);
                adapter.getSnapshots().getSnapshot(position).getReference().delete()
                        .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Medicine deleted", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error deleting medicine", Toast.LENGTH_SHORT).show());
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void cancelAlarm(Medicine medicine) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        int pendingIntentId = (int) medicine.getNotificationId();
        // Use FLAG_NO_CREATE to check if an alarm exists before trying to cancel it.
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, pendingIntentId, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Toast.makeText(this, "Reminder for " + medicine.getName() + " cancelled.", Toast.LENGTH_SHORT).show();
        }
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void statusbar() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Change the status bar color programmatically
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            mAuth.signOut();
            startActivity(new Intent(this, SignIn.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, SignIn.class));
            finish();
            return;
        }

        if (adapter != null) {
            adapter.startListening();
        }
    }

    // --- FIX: Use onStop() for stopListening(), which is the pair to onStart() ---
    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    // --- FIX: Remove redundant onResume and onPause for adapter listening ---
    @Override
    protected void onResume() {
        super.onResume();
        // No need to call startListening() here, it's handled in onStart()
    }

    @Override
    protected void onPause() {
        super.onPause();
        // No need to call stopListening() here, it's handled in onStop()
    }
}