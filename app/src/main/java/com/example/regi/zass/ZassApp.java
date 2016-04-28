package com.example.regi.zass;

import com.firebase.client.Firebase;

public class ZassApp extends android.app.Application {



    @Override
    public void onCreate() {
        super.onCreate();
        /* Initialize Firebase */
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

    }

}
