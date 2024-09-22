package com.example.carpooling;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class settingsFragment extends Fragment {

    private TextView userNameTextView;
    private EditText editNameEditText, editPhoneEditText;
    private Button submitButton, signOutButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser currentUser;

    public settingsFragment() {
        // Required empty public constructor
    }

    public static settingsFragment newInstance() {
        return new settingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        userNameTextView = view.findViewById(R.id.userNameTextView);
        editNameEditText = view.findViewById(R.id.editNameEditText);
        editPhoneEditText = view.findViewById(R.id.editPhoneEditText);
        submitButton = view.findViewById(R.id.submitButton);
        signOutButton = view.findViewById(R.id.signOutButton);

        // Set current user's name to userNameTextView
        if (currentUser != null) {
            userNameTextView.setText(currentUser.getDisplayName());
        }

        // Fetch user's phone number from Firestore and set it to editPhoneEditText
        fetchUserDetails();

        // Handle submit button click
        submitButton.setOnClickListener(v -> updateUserDetails());

        // Handle sign out button click
        signOutButton.setOnClickListener(v -> signOut());

        return view;
    }

    private void fetchUserDetails() {
        if (currentUser != null) {
            fStore.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String phoneNumber = documentSnapshot.getString("mobile");
                            userNameTextView.setText(documentSnapshot.getString("name"));
                            editNameEditText.setText(documentSnapshot.getString("name"));
                            editPhoneEditText.setText(phoneNumber);
                        } else {
                            Toast.makeText(getContext(), "User details not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to fetch user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateUserDetails() {
        String newName = editNameEditText.getText().toString().trim();
        String newPhone = editPhoneEditText.getText().toString().trim();

        // Update display name
        if (!newName.isEmpty()) {
            currentUser.updateProfile(new UserProfileChangeRequest.Builder()
                            .setDisplayName(newName)
                            .build())
                    .addOnSuccessListener(aVoid -> {
                        userNameTextView.setText(newName);
                        Toast.makeText(getContext(), "Name updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to update name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        // Update phone number in Firestore
        if (!newPhone.isEmpty()) {
            fStore.collection("users").document(currentUser.getUid())
                    .update("phoneNumber", newPhone)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Phone number updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to update phone number: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent=new Intent(getActivity().getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}
