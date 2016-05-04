package com.example.regi.zass.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.example.regi.zass.Utils.SharedPrefsUtils;
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

public class MainActivity extends AppCompatActivity  {

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

        context = getApplicationContext();

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DetailActivity.class));
            }
        });

        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerview);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        rv.setItemAnimator(new DefaultItemAnimator());

        ref = new Firebase(Constants.FIREBASE_URL).child("activeList").child(SharedPrefsUtils.getStringPreference(getApplicationContext(),Constants.USERID));

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
                        Intent temp = new Intent(MainActivity.this, DetailActivity.class);
                        temp.putExtra("currentNote",currentNote);
                        temp.putExtra("key",mAdapter.getRef(position).getKey());
                        startActivity(temp);
                    }
                });
                chatMessageViewHolder.container.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent temp = new Intent(MainActivity.this, FullscreenActivity.class);
                        temp.putExtra("currentNote",currentNote);
                        temp.putExtra("key",mAdapter.getRef(position).getKey());
                        startActivity(temp);
                        return false;
                    }
                });
            }
        };
        rv.setAdapter(mAdapter);

    }





    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView nameText;
        RelativeLayout container;

        public NoteViewHolder(View itemView) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.title);
            messageText = (TextView) itemView.findViewById(R.id.tag);
            container = (RelativeLayout) itemView.findViewById(R.id.container);

        }
    }
}
