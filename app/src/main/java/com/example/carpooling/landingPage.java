package com.example.carpooling;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.carpooling.databinding.ActivityLandingPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class landingPage extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityLandingPageBinding binding;

    TextView name;
    FloatingActionButton mapFragmentStart;
    FirebaseAuth fauth3;
    FirebaseFirestore fstore3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLandingPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new CreateJourneyFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.createJourney) {
                replaceFragment(new CreateJourneyFragment());
            } else if (itemId == R.id.joinJourney) {
                replaceFragment(new JoinJourneyFragment());
            } else if (itemId == R.id.camera) {
                replaceFragment(new cameraFragment());
            } else if (itemId == R.id.settings) {
                replaceFragment(new settingsFragment());
            }
            return true;
        });
        mapFragmentStart=findViewById(R.id.map);
        mapFragmentStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new mapFragment());
            }
        });

    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame,fragment);
        fragmentTransaction.commit();
    }

}