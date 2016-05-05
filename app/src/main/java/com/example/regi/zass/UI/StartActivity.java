package com.example.regi.zass.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.regi.zass.Utils.Constants;
import com.example.regi.zass.Utils.SharedPrefsUtils;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String id = SharedPrefsUtils.getStringPreference(getApplicationContext(), Constants.USER_ID);

        if (id == null || id.isEmpty()){
            startActivity(new Intent(StartActivity.this,LoginActivity.class));
            finish();
        }else{
            startActivity(new Intent(StartActivity.this,MainActivity.class));
            finish();
        }
    }
}
