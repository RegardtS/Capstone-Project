package com.example.regi.zass.ContentProvider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class NotesProvider extends ContentProvider {
    private static final String PROVIDER_NAME = "com.example.regi.zass.contentprovider.notes";
    private static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/notes");
    private static final int IMAGES = 1;
    private static final int IMAGE_ID = 2;
    private static final UriMatcher uriMatcher = getUriMatcher();

    private static UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "notes", IMAGES);
        uriMatcher.addURI(PROVIDER_NAME, "notes/#", IMAGE_ID);
        return uriMatcher;
    }

    private NoteDatabase noteDatabase = null;

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case IMAGES:
                return "com.example.regi.zass.contentprovider.notes.dir/com.example.regi.zass.contentprovider.notes";
            case IMAGE_ID:
                return "com.example.regi.zass.contentprovider.notes.item/com.example.regi.zass.contentprovider.notes";

        }
        return "";
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        noteDatabase = NoteDatabase.getInstance(context.getApplicationContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String id = null;
        if (uriMatcher.match(uri) == IMAGE_ID) {
            //Query is for one single image. Get the ID from the URI.
            id = uri.getPathSegments().get(1);
        }
        return noteDatabase.getImages(id, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        try {
            long id = noteDatabase.addNewNote(values);
            Uri returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
            return returnUri;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String id = null;
        if (uriMatcher.match(uri) == IMAGE_ID) {
            //Delete is for one single image. Get the ID from the URI.
            id = uri.getPathSegments().get(1);
        }

        return noteDatabase.deleteNotes(id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String id = null;
        if (uriMatcher.match(uri) == IMAGE_ID) {
            //Update is for one single image. Get the ID from the URI.
            id = uri.getPathSegments().get(1);
        }

        return noteDatabase.updateNotes(id, values);
    }
}