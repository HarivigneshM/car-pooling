# Carpooling Android App

## Overview

This is an Android application that simulates carpooling functionalities. It is built using Java and integrates Firebase Firestore, Firebase Filestore, Firebase Authentication, and Google Maps for a seamless user experience. The app allows users to register, log in, and create or join carpool groups, with real-time location tracking using Google Maps.

## Features

- **User Authentication**: Sign up and log in using Firebase Authentication.
- **Carpool Management**: Users can create, join, and manage carpool rides.
- **Real-time Database**: Firebase Firestore is used for storing and retrieving carpool-related data in real-time.
- **File Storage**: Firebase Filestore is utilized for managing and storing user-related documents (e.g., vehicle registration, profile pictures, etc.).
- **Google Maps Integration**: Visualize and track routes and carpool locations in real-time.

## Technology Stack

- **Programming Language**: Java
- **Android SDK**: Android API 21+
- **Firebase Services**:
  - Firestore
  - Filestore
  - Authentication
- **Google Maps API**

## Installation

1. Clone the repository to your local machine:
   ```bash
   git clone https://github.com/HarivigneshM/car-pooling.git
   ```

2. **Open the project in Android Studio**.

3. **Make sure to have Google Play services installed** for using Google Maps.

4. **Set up Firebase**:
   - Go to [Firebase Console](https://console.firebase.google.com/).
   - Create a new project and add Android app details.
   - Download the `google-services.json` file and place it in the `app/` directory.
   - Enable Firebase Firestore, Filestore, and Authentication from Firebase Console.

5. **Configure Google Maps API**:
   - Visit the [Google Cloud Console](https://console.cloud.google.com/).
   - Enable the Maps SDK for Android.
   - Generate an API Key and add it to your `AndroidManifest.xml` file:
     ```xml
     <meta-data
         android:name="com.google.android.geo.API_KEY"
         android:value="YOUR_API_KEY_HERE" />
     ```
6. **Build and run the project**.

## Usage

1. **Sign Up / Login**: Create a new account or log in with an existing account using Firebase Authentication.
2. **Create a Carpool**: As a logged-in user, you can create a new carpool by providing necessary ride details.
3. **Join a Carpool**: Search for available carpool rides and join one that fits your route and schedule.
4. **Map View**: Use Google Maps to visualize your location and see the route of the carpool.


## Future Improvements

- Add push notifications to alert users of carpool changes.
- Implement a rating system for drivers and passengers.
- Improve route optimization using Google Maps Directions API.
- Add payment gateway integration for ride payments.

