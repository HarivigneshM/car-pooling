package com.example.carpooling;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class JoinJourneyFragment extends Fragment {
    private FirebaseFirestore fstore3;
    private FirebaseAuth fAuth3;
    private RecyclerView ridesRecyclerView;
    private String travelerUid;
    private EditText searchFromLocation, searchToLocation, searchDate;
    private Button searchButton;
    private RideAdapter rideAdapter;
    private List<Ride> rideList = new ArrayList<>();
    private Calendar calendar;

    public JoinJourneyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_journey, container, false);

        // Initialize Firestore
        fstore3 = FirebaseFirestore.getInstance();
        fAuth3 = FirebaseAuth.getInstance();

        // Initialize UI elements
        ridesRecyclerView = view.findViewById(R.id.ridesRecyclerView);
        searchFromLocation = view.findViewById(R.id.searchFromLocation);
        searchToLocation = view.findViewById(R.id.searchToLocation);
        searchDate = view.findViewById(R.id.searchDate);
        searchButton = view.findViewById(R.id.searchButton);

        travelerUid = fAuth3.getCurrentUser().getUid();

        // Setup RecyclerView
        ridesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rideAdapter = new RideAdapter(rideList, this::joinRide);
        ridesRecyclerView.setAdapter(rideAdapter);

        // Fetch all rides initially
        fetchRides(null, null, null);

        // Setup date picker for searchDate
        calendar = Calendar.getInstance();
        searchDate.setInputType(InputType.TYPE_NULL);
        searchDate.setOnClickListener(v -> showDateTimePicker());

        // Setup search button click listener
        searchButton.setOnClickListener(v -> {
            String fromLocation = searchFromLocation.getText().toString();
            String toLocation = searchToLocation.getText().toString();
            String date = searchDate.getText().toString();
            fetchRides(fromLocation, toLocation, date);
        });

        return view;
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute1) -> {
                String selectedDateTime = dayOfMonth + "/" + (month1 + 1) + "/" + year1 + " " + hourOfDay + ":" + minute1;
                searchDate.setText(selectedDateTime);
            }, hour, minute, true);
            timePickerDialog.show();
        }, year, month, day);
        datePickerDialog.show();
    }

    private void fetchRides(String fromLocation, String toLocation, String date) {
        CollectionReference journeysRef = fstore3.collection("journeys");
        Query query = journeysRef;

        if (fromLocation != null && !fromLocation.isEmpty()) {
            query = query.whereEqualTo("fromLocation", fromLocation);
        }
        if (toLocation != null && !toLocation.isEmpty()) {
            query = query.whereEqualTo("toLocation", toLocation);
        }
        if (date != null && !date.isEmpty()) {
            query = query.whereEqualTo("dateTime", date);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            rideList.clear();
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                Ride ride = document.toObject(Ride.class);
                ride.setDocumentId(document.getId());

                // Initialize lists if they are null
                if (ride.getTravelersUids() == null) {
                    ride.setTravelersUids(new ArrayList<>());
                }
                if (ride.getTravelersNames() == null) {
                    ride.setTravelersNames(new ArrayList<>());
                }
                if (ride.getTravelersPhones() == null) {
                    ride.setTravelersPhones(new ArrayList<>());
                }
                if (!ride.getUserId().equals(travelerUid) && !ride.getTravelersUids().contains(travelerUid)) {
                    rideList.add(ride);
                }
            }
            rideAdapter.notifyDataSetChanged();
        });
    }

    private void joinRide(Ride ride) {
        if (ride.getAvailableSeats() > 0) {
            // Show dialog for seat input
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_join, null);
            EditText seatInput = dialogView.findViewById(R.id.seatInput);
            Button joinConfirmButton = dialogView.findViewById(R.id.joinConfirmButton);

            // Build the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            joinConfirmButton.setOnClickListener(v -> {
                String seatsNeededStr = seatInput.getText().toString().trim();
                if (!seatsNeededStr.isEmpty()) {
                    int seatsNeeded = Integer.parseInt(seatsNeededStr);
                    if (seatsNeeded > 0 && seatsNeeded <= ride.getAvailableSeats()) {
                        // Reduce available seats
                        ride.setAvailableSeats(ride.getAvailableSeats() - seatsNeeded);

                        // Fetch user data from the users collection
                        fstore3.collection("users").document(travelerUid).get().addOnSuccessListener(userDocument -> {
                            if (userDocument.exists()) {
                                String travelerName = userDocument.getString("name");
                                String travelerPhone = userDocument.getString("phone");

                                List<String> travelersNames = ride.getTravelersNames();
                                List<String> travelersPhones = ride.getTravelersPhones();
                                List<String> travelersUids = ride.getTravelersUids();

                                for (int i = 0; i < seatsNeeded; i++) {
                                    travelersNames.add(travelerName);
                                    travelersPhones.add(travelerPhone);
                                    travelersUids.add(travelerUid);
                                }

                                // Update Firestore document
                                fstore3.collection("journeys").document(ride.getDocumentId())
                                        .update("availableSeats", ride.getAvailableSeats(),
                                                "travelersNames", travelersNames,
                                                "travelersPhones", travelersPhones,
                                                "travelersUids", travelersUids)
                                        .addOnSuccessListener(aVoid -> {
                                            // Successfully joined the ride
                                            rideAdapter.notifyDataSetChanged();
                                            Toast.makeText(getContext(), "Joined ride successfully!", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle the error
                                            Toast.makeText(getContext(), "Failed to join ride!", Toast.LENGTH_SHORT).show();
                                        });

                                dialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), "User data not found!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to fetch user data!", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        Toast.makeText(getContext(), "Please enter a valid number of seats within available range!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please enter the number of seats you need!", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.show();
        } else {
            Toast.makeText(getContext(), "No available seats for this ride!", Toast.LENGTH_SHORT).show();
        }
    }
}
