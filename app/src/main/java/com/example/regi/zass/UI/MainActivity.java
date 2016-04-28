package com.example.regi.zass.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.regi.zass.BuildConfig;
import com.example.regi.zass.R;
import com.example.regi.zass.Utils.Constants;
import com.example.regi.zass.Utils.DateUtils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;

import com.example.regi.zass.Model.Note;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {

    FirebaseRecyclerAdapter mAdapter;
    Context context;
    Firebase ref;
    List<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if(BuildConfig.FREE_VERSION){
            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }


        noteList = new ArrayList<>();


        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Calendar cal = Calendar.getInstance();


        Date dateTime = null;
        try {
            dateTime = sdf.parse(sdf.format(cal.getTime()));
            String temp = dateTime.toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }


        context = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DetailActivity.class));
            }
        });

        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerview);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));

        rv.setItemAnimator(new DefaultItemAnimator());

        ref = new Firebase(Constants.FIREBASE_URL).child("activeList");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                noteList.clear();
                for (DataSnapshot msgSnapshot: snapshot.getChildren()) {
                    Note msg = msgSnapshot.getValue(Note.class);
                    noteList.add(msg);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Chat", "The read failed: " + firebaseError.getMessage());
            }
        });




        mAdapter = new FirebaseRecyclerAdapter<Note, NoteViewHolder>(Note.class, R.layout.item_layout, NoteViewHolder.class, ref)  {
            @Override
            public void populateViewHolder(NoteViewHolder chatMessageViewHolder, final Note currentNote, final int position) {
                chatMessageViewHolder.nameText.setText(currentNote.getReadingTitle());
                chatMessageViewHolder.messageText.setText(currentNote.getTags());
                chatMessageViewHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent temp = new Intent(MainActivity.this, FullscreenActivity.class);
//                        Note currentNote = noteList.get(position);
                        temp.putExtra("currentNote",currentNote);
                        temp.putExtra("key",mAdapter.getRef(position).getKey());
                        startActivity(temp);
                    }
                });
                chatMessageViewHolder.container.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showDialogFromTap(position);
                        return false;
                    }
                });
            }
        };
        rv.setAdapter(mAdapter);




        ref.createUser("bobtony@firebase.com", "correcthorsebatterystaple", new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                System.out.println("Successfully created user account with uid: " + result.get("uid"));
            }
            @Override
            public void onError(FirebaseError firebaseError) {
                // there was an error
            }
        });


    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public void showDialogFromTap(int position){
//        Toast.makeText(getApplicationContext(),"tester " + position,Toast.LENGTH_LONG).show();

        //TODO ADD POPUP REMOVE/PLAY


        startActivity(new Intent(MainActivity.this, FullscreenActivity.class));

    }





    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView nameText;
        RelativeLayout container;

        public NoteViewHolder(View itemView) {
            super(itemView);
            nameText = (TextView)itemView.findViewById(R.id.title);
            messageText = (TextView) itemView.findViewById(R.id.tag);
            container = (RelativeLayout) itemView.findViewById(R.id.container);

        }






    }


    private void fillWithData(){
        Firebase ref = new Firebase(Constants.FIREBASE_URL);


        Note tester = new Note(1,1,1,1,false,false,false,false,"important,starter","dokdsosksdo","tester", DateUtils.getCurrentDate());
        ref.child("activeList").push().setValue(tester);
    }

    

}
