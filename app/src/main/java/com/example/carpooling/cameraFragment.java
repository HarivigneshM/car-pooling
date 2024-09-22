package com.example.carpooling;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class cameraFragment extends Fragment {

    private RecyclerView ridesRecyclerView;
    private RideDetailsAdapter rideDetailsAdapter;
    private List<Ride> rideList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser currentUser;

    public cameraFragment() {
        // Required empty public constructor
    }

    public static cameraFragment newInstance(String param1, String param2) {
        cameraFragment fragment = new cameraFragment();
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        ridesRecyclerView = view.findViewById(R.id.ridesRecyclerView);
        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        rideDetailsAdapter = new RideDetailsAdapter(rideList, getContext());
        ridesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ridesRecyclerView.setAdapter(rideDetailsAdapter);
        fetchRides();
    }

    private void fetchRides() {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            CollectionReference ridesRef = fStore.collection("journeys");

            // Query for rides where the current user is the driver
            Query driverQuery = ridesRef.whereEqualTo("userId", uid);

            // Query for rides where the current user is a traveler
            Query travelerQuery = ridesRef.whereArrayContains("travelersUids", uid);

            // Fetch rides where the user is the driver
            driverQuery.get().addOnSuccessListener(driverSnapshots -> {
                rideList.clear();
                for (QueryDocumentSnapshot document : driverSnapshots) {
                    Ride ride = document.toObject(Ride.class);
                    ride.setDocumentId(document.getId());
                    rideList.add(ride);
                }

                // Fetch rides where the user is a traveler
                travelerQuery.get().addOnSuccessListener(travelerSnapshots -> {
                    for (QueryDocumentSnapshot document : travelerSnapshots) {
                        Ride ride = document.toObject(Ride.class);
                        ride.setDocumentId(document.getId());
                        // Ensure the ride is not already added
                        if (!rideList.contains(ride)) {
                            rideList.add(ride);
                        }
                    }
                    rideDetailsAdapter.notifyDataSetChanged();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch rides as traveler: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to fetch rides as driver: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
