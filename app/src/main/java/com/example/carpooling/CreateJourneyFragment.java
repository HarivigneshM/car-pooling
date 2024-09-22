package com.example.carpooling;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateJourneyFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private FirebaseAuth fauth3;
    private FirebaseFirestore fstore3;
    private FirebaseStorage fstorage3;
    private EditText driverName, fromLocation, toLocation, phoneNumber, dateTime, totalSeats, availableSeats, carModel,price;
    private String uid;
    private Button carPhotoButton, createButton;
    private ImageView carPhoto;
    private String currentPhotoPath;
    Bitmap photo;
    private static final int pic_id = 123;

    public CreateJourneyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_journey, container, false);

        // Initialize Firebase
        fauth3 = FirebaseAuth.getInstance();
        fstore3 = FirebaseFirestore.getInstance();
        fstorage3 = FirebaseStorage.getInstance();
        uid = fauth3.getCurrentUser().getUid();

        // Initialize UI elements
        driverName = view.findViewById(R.id.DriverName);
        fromLocation = view.findViewById(R.id.fromLocation);
        toLocation = view.findViewById(R.id.toLocation);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        dateTime = view.findViewById(R.id.dot);
        totalSeats = view.findViewById(R.id.totalSeats);
        availableSeats = view.findViewById(R.id.availableSeats);
        carModel = view.findViewById(R.id.carModel);
        price=view.findViewById(R.id.price);
        carPhotoButton = view.findViewById(R.id.carPhotoButton);
        carPhoto = view.findViewById(R.id.carPhoto);
        createButton = view.findViewById(R.id.create);

        // Set up date and time picker dialog
        dateTime.setOnClickListener(v -> showDateTimePicker());

        // Set up button click listener
        createButton.setOnClickListener(v -> createJourney());

        // Set up car photo button listener
        carPhotoButton.setOnClickListener(v -> dispatchTakePictureIntent());

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
                dateTime.setText(selectedDateTime);
            }, hour, minute, true);
            timePickerDialog.show();
        }, year, month, day);
        datePickerDialog.show();
    }

    private void createJourney() {
        // Collect data from input fields
        String driverNameStr1 = driverName.getText().toString();
        String fromLocationStr1 = fromLocation.getText().toString();
        String toLocationStr1 = toLocation.getText().toString();
        String phoneNumberStr1 = phoneNumber.getText().toString();
        String dateTimeStr1 = dateTime.getText().toString();
        String totalSeatsStr1 = totalSeats.getText().toString();
        String availableSeatsStr1 = availableSeats.getText().toString();
        String carModelStr1 = carModel.getText().toString();
        String priceStr1= price.getText().toString();

        if (driverNameStr1.isEmpty() || fromLocationStr1.isEmpty() || toLocationStr1.isEmpty() ||
                phoneNumberStr1.isEmpty() || dateTimeStr1.isEmpty() || totalSeatsStr1.isEmpty()||
                availableSeatsStr1.isEmpty()|| carModelStr1.isEmpty()|| priceStr1.isEmpty() || photo == null || photo.getWidth() == 0 || photo.getHeight() == 0) {
            Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the image to Firebase Storage
        if (photo != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference storageRef = fstorage3.getReference().child("car_photos/" + System.currentTimeMillis() + ".jpg");
            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnFailureListener(exception -> {
                Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error uploading image: " + exception.getMessage());
            }).addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Create a new journey map with image URL
                Map<String, Object> journey = new HashMap<>();
                journey.put("driverName", driverNameStr1);
                journey.put("fromLocation", fromLocationStr1);
                journey.put("toLocation", toLocationStr1);
                journey.put("phoneNumber", phoneNumberStr1);
                journey.put("dateTime", dateTimeStr1);
                journey.put("totalSeats", Integer.parseInt(totalSeatsStr1));
                journey.put("availableSeats", Integer.parseInt(availableSeatsStr1));
                journey.put("carModel", carModelStr1);
                journey.put("price",Double.parseDouble(priceStr1));
                journey.put("carPhotoUrl", uri.toString());
                journey.put("userId", uid);

                // Save the journey data to Firestore
                fstore3.collection("journeys").add(journey).addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Journey Created Successfully", Toast.LENGTH_SHORT).show();
                    saveImage(photo);
                    // Optionally, navigate to another fragment or activity
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to Create Journey", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error adding document: " + e.getMessage());
                });
            }));
        } else {
            Toast.makeText(getContext(), "Please take a photo of your car", Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera_intent, pic_id);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Match the request 'pic id with requestCode
        if (requestCode == pic_id && resultCode == RESULT_OK) {
            // BitMap is data structure of image file which store the image in memory
            photo = (Bitmap) data.getExtras().get("data");
            // Set the image in imageview for display
            carPhoto.setImageBitmap(photo);
        }
    }
    private void saveImage(Bitmap bitmap) {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            OutputStream fos;
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "Carpooling");
                    values.put(MediaStore.Images.Media.IS_PENDING, true);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");

                    Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    fos = getContext().getContentResolver().openOutputStream(uri);
                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    getContext().getContentResolver().update(uri, values, null, null);
                } else {
                    String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + File.separator + "Carpooling";
                    File file = new File(imagesDir);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    String fileName = System.currentTimeMillis() + ".jpg";
                    File image = new File(imagesDir, fileName);
                    fos = new FileOutputStream(image);
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File externalStorageDir = new File(Environment.getExternalStorageDirectory(), "Carpooling");
        if (!externalStorageDir.exists()) {
            externalStorageDir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(externalStorageDir, fileName);

        try (OutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            Toast.makeText(getContext(), "Image Saved to Memory Card", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Failed to Save Image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
