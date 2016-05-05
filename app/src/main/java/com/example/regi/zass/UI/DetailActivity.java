package com.example.regi.zass.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.regi.zass.Model.Note;
import com.example.regi.zass.R;
import com.example.regi.zass.Utils.Constants;
import com.example.regi.zass.Utils.DateUtils;
import com.example.regi.zass.Utils.FirebaseUtils;
import com.firebase.client.Firebase;

public class DetailActivity extends AppCompatActivity {

    TextView tvTitle, tvHashtag, tvNote;
    boolean isEditing = false;
    Note currentNote;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = (TextView) findViewById(R.id.txtDetailTitle);
        tvHashtag = (TextView) findViewById(R.id.txtDetailHashtag);
        tvNote = (TextView) findViewById(R.id.txtDetailNote);

        if (getIntent().getParcelableExtra(Constants.CURRENT_NOTE) != null) {
            currentNote = getIntent().getParcelableExtra(Constants.CURRENT_NOTE);
            key = getIntent().getStringExtra(Constants.NOTE_KEY);
            isEditing = true;

            tvHashtag.setText(currentNote.getTags());
            tvNote.setText(currentNote.getReadingText());
            tvTitle.setText(currentNote.getReadingTitle());
        }else{
            currentNote = new Note();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (currentNote.getReadingTitle() != null){
            getSupportActionBar().setTitle(currentNote.getReadingTitle());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_play:
                Intent temp2 = new Intent(DetailActivity.this, FullscreenActivity.class);
                temp2.putExtra(Constants.CURRENT_NOTE,currentNote);
                startActivity(temp2);
                return true;
            case R.id.action_settings:
                Intent temp = new Intent(DetailActivity.this, SettingsActivity.class);
                temp.putExtra(Constants.CURRENT_NOTE,currentNote);
                startActivityForResult(temp,418);
                return true;
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                save();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 418 && resultCode == RESULT_OK){
            currentNote = data.getParcelableExtra(Constants.CURRENT_NOTE);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void save(){
        if (isEditing) {
            updateNote();
        } else {
            addNote();
        }
        finish();
    }

    private void addNote() {
        currentNote.setReadingText(tvNote.getText().toString());
        currentNote.setReadingTitle(tvTitle.getText().toString());
        currentNote.setTags(tvHashtag.getText().toString());
        currentNote.setDateCreated(DateUtils.getCurrentDate());


        FirebaseUtils.createNote(currentNote, getApplicationContext());

    }

    private void updateNote() {
        currentNote.setReadingTitle(tvTitle.getText().toString());
        currentNote.setReadingText(tvNote.getText().toString());
        currentNote.setTags(tvHashtag.getText().toString());

        FirebaseUtils.updateNote(currentNote, key, getApplicationContext());
    }

}
