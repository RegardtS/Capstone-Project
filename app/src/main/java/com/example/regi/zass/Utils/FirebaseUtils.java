package com.example.regi.zass.Utils;


import android.content.Context;
import android.widget.Toast;

import com.example.regi.zass.Model.Note;
import com.example.regi.zass.R;
import com.firebase.client.Firebase;


public class FirebaseUtils {

    public static void createNote(Note note, Context context){
        Firebase ref = new Firebase(Constants.FIREBASE_URL);
        ref.child("activeList").child(SharedPrefsUtils.getStringPreference(context,Constants.USER_ID)).push().setValue(note);
        Toast.makeText(context.getApplicationContext(), R.string.successfully_created_new_note, Toast.LENGTH_SHORT).show();
    }

    public static void updateNote(Note note, String key, Context context){
        Firebase ref = new Firebase(Constants.FIREBASE_URL);
        ref.child("activeList").child(SharedPrefsUtils.getStringPreference(context,Constants.USER_ID)).child(key).setValue(note);
        Toast.makeText(context.getApplicationContext(), R.string.successfully_updated, Toast.LENGTH_SHORT).show();
    }


}
