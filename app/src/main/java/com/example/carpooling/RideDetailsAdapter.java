package com.example.carpooling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RideDetailsAdapter extends RecyclerView.Adapter<RideDetailsAdapter.RideViewHolder> {

    private List<Ride> rideList;
    private Context context;

    public RideDetailsAdapter(List<Ride> rideList, Context context) {
        this.rideList = rideList;
        this.context = context;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_details_item, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);
        holder.rideDriver.setText("Driver: " + ride.getDriverName());
        holder.rideFromLocation.setText("From: " + ride.getFromLocation());
        holder.rideToLocation.setText("To: " + ride.getToLocation());
        holder.ridePhoneNumber.setText("Phone: " + ride.getPhoneNumber());
        holder.rideDateTime.setText("Date & Time: " + ride.getDateTime());
        holder.rideTotalSeats.setText("Total Seats: " + ride.getTotalSeats());
        holder.rideAvailableSeats.setText("Available Seats: " + ride.getAvailableSeats());
        holder.ridePrice.setText("Price: " + ride.getPrice());
        holder.rideCarModel.setText("Car Model: " + ride.getCarModel());

        if (ride.getCarPhotoUrl() != null && !ride.getCarPhotoUrl().isEmpty()) {
            Glide.with(context).load(ride.getCarPhotoUrl()).into(holder.rideCarPhoto);
        }


        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (ride.getUserId().equals(currentUserId)) {
            holder.deleteRideButton.setVisibility(View.VISIBLE);
            holder.deleteRideButton.setOnClickListener(v -> deleteRide(ride, position));
        } else {
            holder.leaveRideButton.setVisibility(View.VISIBLE);
            holder.leaveRideButton.setOnClickListener(v -> leaveRide(ride, position));
        }
    }

    private void deleteRide(Ride ride, int position) {
        FirebaseFirestore.getInstance().collection("journeys").document(ride.getDocumentId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    rideList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, rideList.size());
                    Toast.makeText(context, "Ride deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete ride: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void leaveRide(Ride ride, int position) {
        // Logic to leave the ride and update available seats
        // Assuming the ride has a list of user IDs representing the passengers
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (ride.getTravelersUids().remove(currentUserId)) {
            ride.setAvailableSeats(ride.getAvailableSeats() + 1);
            FirebaseFirestore.getInstance().collection("journeys").document(ride.getDocumentId())
                    .set(ride)
                    .addOnSuccessListener(aVoid -> {
                        rideList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, rideList.size());
                        Toast.makeText(context, "Ride left", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to leave ride: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView rideDriver, rideFromLocation, rideToLocation, ridePhoneNumber, rideDateTime, rideTotalSeats, rideAvailableSeats, ridePrice, rideCarModel;
        ImageView rideCarPhoto;
        Button leaveRideButton, deleteRideButton;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            rideDriver = itemView.findViewById(R.id.rideDriver);
            rideFromLocation = itemView.findViewById(R.id.rideFromLocation);
            rideToLocation = itemView.findViewById(R.id.rideToLocation);
            ridePhoneNumber = itemView.findViewById(R.id.ridePhoneNumber);
            rideDateTime = itemView.findViewById(R.id.rideDateTime);
            rideTotalSeats = itemView.findViewById(R.id.rideTotalSeats);
            rideAvailableSeats = itemView.findViewById(R.id.rideAvailableSeats);
            ridePrice = itemView.findViewById(R.id.ridePrice);
            rideCarModel = itemView.findViewById(R.id.rideCarModel);
            rideCarPhoto = itemView.findViewById(R.id.rideCarPhoto);
            leaveRideButton = itemView.findViewById(R.id.leaveRideButton);
            deleteRideButton = itemView.findViewById(R.id.deleteRideButton);
        }
    }
}
