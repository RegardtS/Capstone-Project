package com.example.regi.zass.UI;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.regi.zass.Model.Note;
import com.example.regi.zass.R;
import com.example.regi.zass.Utils.Constants;

public class SettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    Note currentNote;

    TextView tvSize, tvSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        if (getIntent().getParcelableExtra(Constants.CURRENT_NOTE) != null) {
            currentNote = getIntent().getParcelableExtra(Constants.CURRENT_NOTE);
        } else {
            Toast.makeText(getApplicationContext(), R.string.unable_to_read_note, Toast.LENGTH_LONG).show();
            finish();
        }

        tvSize = (TextView) findViewById(R.id.tvSize);
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);

        SeekBar sbSize = (SeekBar) findViewById(R.id.sbSize);
        SeekBar sbSpeed = (SeekBar) findViewById(R.id.sbSpeed);

        sbSpeed.setOnSeekBarChangeListener(this);
        sbSize.setOnSeekBarChangeListener(this);

        sbSize.setProgress(currentNote.getSize() + 1);
        sbSpeed.setProgress(currentNote.getSpeed() + 1);


        CheckBox cbLight, cbCountdown, cbLoop, cbMarker, cbTimer;
        cbLight = (CheckBox) findViewById(R.id.cbLight);
        cbCountdown = (CheckBox) findViewById(R.id.cbCountdown);
        cbLoop = (CheckBox) findViewById(R.id.cbLoop);
        cbMarker = (CheckBox) findViewById(R.id.cbMarker);
        cbTimer = (CheckBox) findViewById(R.id.cbTimer);

        cbLight.setOnCheckedChangeListener(this);
        cbCountdown.setOnCheckedChangeListener(this);
        cbLoop.setOnCheckedChangeListener(this);
        cbMarker.setOnCheckedChangeListener(this);
        cbTimer.setOnCheckedChangeListener(this);

        cbCountdown.setChecked(currentNote.getCountdown() > 0 ? true : false);
        cbLight.setChecked(currentNote.isLight());
        cbLoop.setChecked(currentNote.isShouldLoop());
        cbMarker.setChecked(currentNote.isShowMarker());
        cbTimer.setChecked(currentNote.isShowTimer());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                updateNote();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.sbSize) {
            tvSize.setText(String.valueOf(progress + 1));
            currentNote.setSize(Integer.parseInt(tvSize.getText().toString() + 1));
        } else {
            tvSpeed.setText(String.valueOf(progress + 1));
            currentNote.setSpeed(Integer.parseInt(tvSpeed.getText().toString() + 1));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cbCountdown:
                currentNote.setCountdown(isChecked ? 1 : 0);
                break;
            case R.id.cbLight:
                currentNote.setLight(isChecked);
                break;
            case R.id.cbLoop:
                currentNote.setShouldLoop(isChecked);
                break;
            case R.id.cbMarker:
                currentNote.setShowMarker(isChecked);
                break;
            case R.id.cbTimer:
                currentNote.setShowTimer(isChecked);
                break;
        }
    }


    private void updateNote() {
        Intent data = new Intent();
        data.putExtra(Constants.CURRENT_NOTE, currentNote);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

}
