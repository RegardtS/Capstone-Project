package com.example.regi.zass.Utils;


import android.content.Context;
import android.widget.Toast;

import com.example.regi.zass.Model.Note;
import com.firebase.client.Firebase;


public class FirebaseUtils {

    public static void createNote(Note note, Context context){
        Firebase ref = new Firebase(Constants.FIREBASE_URL);
        ref.child("activeList").child(SharedPrefsUtils.getStringPreference(context,Constants.USERID)).push().setValue(note);
        Toast.makeText(context.getApplicationContext(), "Successfully created new note", Toast.LENGTH_SHORT).show();
    }

    public static void updateNote(Note note, String key, Context context){
        Firebase ref = new Firebase(Constants.FIREBASE_URL);
        ref.child("activeList").child(SharedPrefsUtils.getStringPreference(context,Constants.USERID)).child(key).setValue(note);
        Toast.makeText(context.getApplicationContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
    }


}
