package com.example.regi.zass.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;

import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.regi.zass.Model.Note;
import com.example.regi.zass.R;
import com.example.regi.zass.Utils.Constants;
import com.example.regi.zass.View.VerticalMarqueeTextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // ReadingNote that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
//            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };




    Note currentNote = null;
    FloatingActionButton fab;
    VerticalMarqueeTextView vtv;
    FrameLayout frameLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        if( getIntent().getExtras() == null){
            // THIS SHOULD NEVER HAPPEN
            Toast.makeText(getApplicationContext(),getString(R.string.unable_to_read_note),Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        currentNote = getIntent().getParcelableExtra(Constants.CURRENT_NOTE);



        setContentView(R.layout.activity_fullscreen);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(currentNote.getReadingTitle());
        }


        init();




    }
    private void init(){
        mVisible = true;

        mContentView = findViewById(R.id.fullscreen_content);


        vtv = (VerticalMarqueeTextView) findViewById(R.id.fullscreen_content);

        setupScrollingText();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
    }

    private void setupScrollingText(){
        vtv.stopMarquee();
        vtv.setText(currentNote.getReadingText());
        vtv.stopMarquee();
//        100-500
        int speed;
        if (currentNote.getSpeed() == 5){
            speed = 500;
        }else if (currentNote.getSpeed() == 4){
            speed = 400;
        }else if (currentNote.getSpeed() == 3){
            speed = 300;
        }else if (currentNote.getSpeed() == 2){
            speed = 200;
        }else{
            speed = 100;
        }
        vtv.setMarqueeSpeed(speed);
        vtv.stopMarquee();

        float textSize;
        if (currentNote.getSize() == 5){
            textSize = 56f;
        }else if (currentNote.getSpeed() == 4){
            textSize = 48f;
        }else if (currentNote.getSpeed() == 3){
            textSize = 36f;
        }else{
            textSize = 28f;
        }

        vtv.setMarqueeSize(textSize);
        vtv.shouldLoop(currentNote.isShouldLoop());
        vtv.stopMarquee();

        final TextView countdownTimer = (TextView) findViewById(R.id.countdownTimer);
        if (currentNote.getCountdown() > 0){
            countdownTimer.setVisibility(View.VISIBLE);
            new CountDownTimer(5000, 1000) {
                public void onTick(long millisUntilFinished) {
                    //here you can have your logic to set text to edittext
                    countdownTimer.setText(String.valueOf(millisUntilFinished / 1000));
                }

                public void onFinish() {
                    countdownTimer.setVisibility(View.GONE);
                    vtv.shouldPause(false);
                }

            }.start();
        }else{
            vtv.shouldPause(false);
        }

        final TextView timer = (TextView) findViewById(R.id.totalTimer);
        if (currentNote.isShowTimer()){
            timer.setVisibility(View.VISIBLE);
            final int[] tester = {0};
            new CountDownTimer(500000, 1000) {


                
                public void onTick(long millisUntilFinished) {
                    update();
                }

                public void onFinish() {
                    update();
                    start();
                }
                private void update(){
                    tester[0]++;
                    int secs = tester[0];
                    timer.setText(getString(R.string.total_time_secs,secs));
                }

            }.start();
        }

        frameLayout = (FrameLayout) findViewById(R.id.framelayout);
        if (currentNote.isLight()){
            frameLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.background_light));
            timer.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.jet));
            countdownTimer.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.jet));
        }else{
            frameLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.background_dark));
            timer.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.base));
            countdownTimer.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.base));
        }


        if (!currentNote.isShowMarker()){
            LinearLayout marker = (LinearLayout) findViewById(R.id.marker);
            marker.setVisibility(View.GONE);
        }





    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {

        vtv.startMarquee();
        fab.setImageResource(R.drawable.ic_action_pause);


        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {

        vtv.stopMarquee();
        fab.setImageResource(R.drawable.ic_action_play_white);



        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
