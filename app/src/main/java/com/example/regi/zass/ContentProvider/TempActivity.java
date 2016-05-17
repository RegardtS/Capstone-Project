package com.example.regi.zass.ContentProvider;

import android.app.LoaderManager;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.app.Activity;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.regi.zass.R;
import com.example.regi.zass.Utils.Constants;

public class TempActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    private ListView listView;
    private SimpleCursorAdapter adapter;
    private static final int URL_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        listView = (ListView) findViewById(R.id.lstViewImages);
//
        adapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.item_layout,
                null,
                new String[] { Constants.PROVIDER_READINGTITLE, Constants.PROVIDER_DATECREATED, Constants.PROVIDER_TAGS},
                new int[] { R.id.title , R.id.date, R.id.tag }, 0);
        listView.setAdapter(adapter);

        getLoaderManager().initLoader(URL_LOADER,null,this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getBaseContext(), Constants.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}