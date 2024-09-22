package com.example.carpooling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {
    private List<Ride> rideList;
    private OnJoinClickListener onJoinClickListener;

    public interface OnJoinClickListener {
        void onJoinClick(Ride ride);
    }

    public RideAdapter(List<Ride> rideList, OnJoinClickListener onJoinClickListener) {
        this.rideList = rideList;
        this.onJoinClickListener = onJoinClickListener;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_item, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);
        holder.bind(ride, onJoinClickListener);
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        private TextView driverName, fromLocation, toLocation, phoneNumber, dateTime, totalSeats, availableSeats, price, carModel;
        private ImageView carPhoto;
        private Button joinButton;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            driverName = itemView.findViewById(R.id.driver_name);
            fromLocation = itemView.findViewById(R.id.from_location);
            toLocation = itemView.findViewById(R.id.to_location);
            phoneNumber = itemView.findViewById(R.id.phone_number);
            dateTime = itemView.findViewById(R.id.date_time);
            totalSeats = itemView.findViewById(R.id.total_seats);
            availableSeats = itemView.findViewById(R.id.available_seats);
            price = itemView.findViewById(R.id.price1);
            carModel = itemView.findViewById(R.id.car_model);
            carPhoto = itemView.findViewById(R.id.car_photo);
            joinButton = itemView.findViewById(R.id.join_button);
        }

        public void bind(Ride ride, OnJoinClickListener onJoinClickListener) {
            driverName.setText(ride.getDriverName());
            fromLocation.setText(ride.getFromLocation());
            toLocation.setText(ride.getToLocation());
            phoneNumber.setText(ride.getPhoneNumber());
            dateTime.setText(ride.getDateTime());
            totalSeats.setText(String.valueOf(ride.getTotalSeats()));
            availableSeats.setText(String.valueOf(ride.getAvailableSeats()));
            price.setText(String.valueOf(ride.getPrice()));
            carModel.setText(ride.getCarModel());

            if (ride.getCarPhotoUrl() != null && !ride.getCarPhotoUrl().isEmpty()) {
                Picasso.get().load(ride.getCarPhotoUrl()).into(carPhoto);
            }

            joinButton.setOnClickListener(v -> onJoinClickListener.onJoinClick(ride));
        }
    }
}
