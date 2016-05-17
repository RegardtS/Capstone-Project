package com.example.regi.zass.UI;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


import com.example.regi.zass.BuildConfig;
import com.example.regi.zass.ContentProvider.NoteDatabase;
import com.example.regi.zass.R;
import com.example.regi.zass.Utils.Constants;
import com.example.regi.zass.Utils.DateUtils;
import com.example.regi.zass.Utils.SharedPrefsUtils;
import com.example.regi.zass.Utils.SyncAdapter;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;

import com.example.regi.zass.Model.Note;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    SyncAdapter syncAdapter;
    RecordAdapter adapter;

    private static final int URL_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.FREE_VERSION) {
            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }




        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DetailActivity.class));
            }
        });

        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerview);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rv.setItemAnimator(new DefaultItemAnimator());


        getLoaderManager().initLoader(URL_LOADER, null, this);


        adapter = new RecordAdapter(MainActivity.this, null);
        rv.setAdapter(adapter);


        syncAdapter = new SyncAdapter();


        syncAdapter.addMockNote(getApplicationContext());
        syncAdapter.syncNotes(getApplicationContext());

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initLoader();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        initLoader();
    }

    private void initLoader() {
        getLoaderManager().restartLoader(URL_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getBaseContext(), Constants.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
        Cursor dataCursor;
        Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView messageText;
            TextView nameText;
            TextView dateText;
            RelativeLayout container;

            public ViewHolder(View v) {
                super(v);
                nameText = (TextView) itemView.findViewById(R.id.title);
                messageText = (TextView) itemView.findViewById(R.id.tag);
                dateText = (TextView) itemView.findViewById(R.id.date);
                container = (RelativeLayout) itemView.findViewById(R.id.container);
            }
        }

        public RecordAdapter(Activity mContext, Cursor cursor) {
            dataCursor = cursor;
            context = mContext;
        }

        @Override
        public RecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View cardview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
            return new ViewHolder(cardview);
        }

        public Cursor swapCursor(Cursor cursor) {
            if (dataCursor == cursor) {
                return null;
            }
            Cursor oldCursor = dataCursor;
            this.dataCursor = cursor;
            if (cursor != null) {
                this.notifyDataSetChanged();
            }
            return oldCursor;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            dataCursor.moveToPosition(position);
            holder.nameText.setText(dataCursor.getString(dataCursor.getColumnIndex(Constants.PROVIDER_READINGTITLE)));
            holder.messageText.setText(dataCursor.getString(dataCursor.getColumnIndex(Constants.PROVIDER_TAGS)));
            holder.messageText.setText(DateUtils.getTimeAgo(Long.valueOf(dataCursor.getString(dataCursor.getColumnIndex(Constants.PROVIDER_DATECREATED)))));


            final Note currentNote = new Note();

            currentNote.setSpeed(dataCursor.getInt(dataCursor.getColumnIndex(Constants.PROVIDER_SPEED)));
            currentNote.setSize(dataCursor.getInt(dataCursor.getColumnIndex(Constants.PROVIDER_SIZE)));
            currentNote.setCountdown(dataCursor.getInt(dataCursor.getColumnIndex(Constants.PROVIDER_COUNTDOWN)));
            currentNote.setMargin(dataCursor.getInt(dataCursor.getColumnIndex(Constants.PROVIDER_MARGIN)));

            currentNote.setShowMarker(dataCursor.getInt(dataCursor.getColumnIndex(Constants.PROVIDER_SHOWMARKER)) == 0 ? true : false);
            currentNote.setShowTimer(dataCursor.getInt(dataCursor.getColumnIndex(Constants.PROVIDER_SHOWTIMER)) == 0 ? true : false);
            currentNote.setShouldLoop(dataCursor.getInt(dataCursor.getColumnIndex(Constants.PROVIDER_SHOULDLOOP)) == 0 ? true : false);
            currentNote.setLight(dataCursor.getInt(dataCursor.getColumnIndex(Constants.PROVIDER_LIGHT)) == 0 ? true : false);

            currentNote.setTags(dataCursor.getString(dataCursor.getColumnIndex(Constants.PROVIDER_TAGS)));
            currentNote.setReadingText(dataCursor.getString(dataCursor.getColumnIndex(Constants.PROVIDER_READINGTEXT)));
            currentNote.setReadingTitle(dataCursor.getString(dataCursor.getColumnIndex(Constants.PROVIDER_READINGTITLE)));
            currentNote.setDateCreated(dataCursor.getString(dataCursor.getColumnIndex(Constants.PROVIDER_DATECREATED)));

            currentNote.setKey(dataCursor.getString(dataCursor.getColumnIndex(Constants.PROVIDER_KEY)));


            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent temp = new Intent(MainActivity.this, DetailActivity.class);
                    temp.putExtra(Constants.CURRENT_NOTE, currentNote);
                    temp.putExtra(Constants.NOTE_KEY, dataCursor.getString(dataCursor.getColumnIndex(Constants.PROVIDER_KEY)));
                    startActivity(temp);
                }
            });
            holder.container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent temp = new Intent(MainActivity.this, FullscreenActivity.class);
                    temp.putExtra(Constants.CURRENT_NOTE, currentNote);
                    temp.putExtra(Constants.NOTE_KEY, dataCursor.getString(dataCursor.getColumnIndex(Constants.PROVIDER_KEY)));
                    startActivity(temp);
                    return false;
                }
            });

        }

        @Override
        public int getItemCount() {
            return (dataCursor == null) ? 0 : dataCursor.getCount();
        }
    }
}
