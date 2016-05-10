package com.example.regi.zass.Utils;

import android.net.Uri;

import com.example.regi.zass.BuildConfig;

public final class Constants {

    public static final String FIREBASE_URL = BuildConfig.UNIQUE_FIREBASE_ROOT_URL;


    public static final String USER_ID = "USER_ID";

    public static final String CURRENT_NOTE = "CURRENT_NOTE";

    public static final String NOTE_KEY = "NOTE_KEY";


    public static final String PROVIDER_TITLE = "PROVIDER_TITLE";
    public static final String PROVIDER_DATE = "PROVIDER_DATE";
    public static final String PROVIDER_TAG = "PROVIDER_TAG";

    public static final String PROVIDER_NAME = "androidcontentproviderdemo.androidcontentprovider.images";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/images");


}
