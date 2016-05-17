package com.example.regi.zass.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;

import com.example.regi.zass.ContentProvider.NoteDatabase;
import com.example.regi.zass.Model.Note;
import com.example.regi.zass.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.List;

public class SyncAdapter {

    private void addNote(Note note, Context context) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(Constants.PROVIDER_TAGS, note.getTags());
        contentValues.put(Constants.PROVIDER_READINGTEXT, note.getReadingText());
        contentValues.put(Constants.PROVIDER_KEY, note.getKey());
        contentValues.put(Constants.PROVIDER_READINGTITLE, note.getReadingTitle());
        contentValues.put(Constants.PROVIDER_DATECREATED, note.getDateCreated());
        contentValues.put(Constants.PROVIDER_SPEED, note.getSpeed());
        contentValues.put(Constants.PROVIDER_SIZE, note.getSize());
        contentValues.put(Constants.PROVIDER_COUNTDOWN, note.getCountdown());
        contentValues.put(Constants.PROVIDER_MARGIN, note.getMargin());
        contentValues.put(Constants.PROVIDER_SHOWMARKER, note.isShowMarker());
        contentValues.put(Constants.PROVIDER_SHOWTIMER, note.isShowTimer());
        contentValues.put(Constants.PROVIDER_SHOULDLOOP, note.isShouldLoop());
        contentValues.put(Constants.PROVIDER_LIGHT, note.isLight());

        context.getApplicationContext().getContentResolver().insert(Constants.CONTENT_URI, contentValues);
    }


    public void syncNotes(final Context context) {
        Firebase ref = new Firebase(Constants.FIREBASE_URL).child("activeList").child(SharedPrefsUtils.getStringPreference(context.getApplicationContext(), Constants.USER_ID));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot msgSnapshot : snapshot.getChildren()) {
                    Note tempNote = msgSnapshot.getValue(Note.class);
                    tempNote.setKey(msgSnapshot.getKey());
                    addNote(tempNote, context);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Chat", "The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void addMockNote(final Context context) {
        Firebase ref = new Firebase(Constants.FIREBASE_URL).child("activeList").child(SharedPrefsUtils.getStringPreference(context.getApplicationContext(), Constants.USER_ID));
        ref.getRoot().child("example").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Note temp = dataSnapshot.getValue(Note.class);
                addNote(temp, context);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }


}
