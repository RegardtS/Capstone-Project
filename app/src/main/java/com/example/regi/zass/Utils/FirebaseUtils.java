package com.example.regi.zass.Utils;


import com.example.regi.zass.Model.Note;
import com.firebase.client.Firebase;

public class FirebaseUtils {

    public static void createNote(Note note){
        Firebase ref = new Firebase(Constants.FIREBASE_URL);
        ref.child("activeList").push().setValue(note);
    }

    public static void updateNote(Note note, String key){
        Firebase ref = new Firebase(Constants.FIREBASE_URL);
        ref.child("activeList").child(key).setValue(note);
    }


}
