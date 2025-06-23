package com.example.messengar;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // ✅ Initialize Firebase once for the entire app
        FirebaseApp.initializeApp(this);
    }


}
