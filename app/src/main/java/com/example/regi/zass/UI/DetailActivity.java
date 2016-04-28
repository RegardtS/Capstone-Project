package com.example.regi.zass.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.regi.zass.Model.Note;
import com.example.regi.zass.R;
import com.example.regi.zass.Utils.Constants;
import com.example.regi.zass.Utils.FirebaseUtils;
import com.firebase.client.Firebase;

public class DetailActivity extends AppCompatActivity {

    TextView tvTitle,tvHashtag,tvNote;
    boolean isEditing = false;
    Note currentNote;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvTitle = (TextView) findViewById(R.id.txtDetailTitle);
        tvHashtag = (TextView) findViewById(R.id.txtDetailHashtag);
        tvNote = (TextView) findViewById(R.id.txtDetailNote);

//        emp.putExtra("currentNote",currentNote);
//        temp.putExtra("key",mAdapter.getRef(position).getKey());

        if (getIntent().getParcelableExtra("currentNote")!=null){
            currentNote = getIntent().getParcelableExtra("currentNote");
            key = getIntent().getStringExtra("key");
            isEditing = true;

            tvHashtag.setText(currentNote.getTags());
            tvNote.setText(currentNote.getReadingText());
            tvTitle.setText(currentNote.getReadingTitle());
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
                startActivity(new Intent(DetailActivity.this, FullscreenActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(DetailActivity.this, SettingsActivity.class));
                return true;
            case android.R.id.home:
                if (shouldSave()) {
                    if (isEditing){
                        updateNote();
                    }else{
                        addNote();
                    }

                }
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean shouldSave() {
        return true;
//        check if any fields are empty
    }


    private void addNote() {

        String title = tvTitle.getText().toString();
        String note = tvNote.getText().toString();
        String hashtag = tvHashtag.getText().toString();

        FirebaseUtils.createNote(new Note(1, 1, 1, 1, false, false, false, false, hashtag,note,title,"dateHere"));
    }

    private void updateNote(){
        currentNote.setReadingTitle(tvTitle.getText().toString());
        currentNote.setReadingText(tvNote.getText().toString());
        currentNote.setTags(tvHashtag.getText().toString());

        FirebaseUtils.updateNote(currentNote,key);
    }

}
