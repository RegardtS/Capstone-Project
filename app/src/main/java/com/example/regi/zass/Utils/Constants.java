package com.example.regi.zass.Utils;

import android.net.Uri;

import com.example.regi.zass.BuildConfig;

public final class Constants {

    public static final String FIREBASE_URL = BuildConfig.UNIQUE_FIREBASE_ROOT_URL;


    public static final String USER_ID = "USER_ID";

    public static final String CURRENT_NOTE = "CURRENT_NOTE";

    public static final String NOTE_KEY = "NOTE_KEY";


    public static final String PROVIDER_TAGS = "PROVIDER_TITLE";
    public static final String PROVIDER_READINGTEXT = "PROVIDER_DATE";
    public static final String PROVIDER_READINGTITLE = "PROVIDER_TAG";
    public static final String PROVIDER_DATECREATED = "PROVIDER_DATECREATED";
    public static final String PROVIDER_KEY = "PROVIDER_KEY";

    public static final String PROVIDER_SPEED = "PROVIDER_SPEED";
    public static final String PROVIDER_SIZE = "PROVIDER_SIZE";
    public static final String PROVIDER_COUNTDOWN = "PROVIDER_COUNTDOWN";
    public static final String PROVIDER_MARGIN = "PROVIDER_MARGIN";

    public static final String PROVIDER_SHOWMARKER = "PROVIDER_SHOWMARKER";
    public static final String PROVIDER_SHOWTIMER = "PROVIDER_SHOWTIMER";
    public static final String PROVIDER_SHOULDLOOP = "PROVIDER_SHOULDLOOP";
    public static final String PROVIDER_LIGHT = "PROVIDER_LIGHT";


    public static final String PROVIDER_NAME = "com.example.regi.zass.contentprovider.notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/notes");


}
