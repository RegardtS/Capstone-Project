package com.example.regi.zass.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.regi.zass.R;
import com.example.regi.zass.Utils.Constants;
import com.example.regi.zass.Utils.SharedPrefsUtils;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Map;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {


    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    SignInButton signInButton;
    TextView textViewBottom;
    AppCompatButton btnLogin;

    boolean isLogin = false;
    ProgressDialog progressDialog;

    Firebase ref;

    EditText etEmail,etPassword;
    TextInputLayout tlEmail,tlPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupGoogleStuff();


        etEmail = (EditText) findViewById(R.id.input_email);
        etPassword = (EditText) findViewById(R.id.input_password);
        tlEmail = (TextInputLayout) findViewById(R.id.layout_email);
        tlPassword = (TextInputLayout) findViewById(R.id.layout_password);

        textViewBottom = (TextView) findViewById(R.id.txt_bottom);
        btnLogin = (AppCompatButton) findViewById(R.id.btn_login);
        textViewBottom.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        ref = new Firebase(Constants.FIREBASE_URL);

    }

    private void setupGoogleStuff() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);

        signInButton.setStyle(SignInButton.SIZE_WIDE, SignInButton.COLOR_LIGHT);
        signInButton.setScopes(gso.getScopeArray());

        signInButton.setOnClickListener(this);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    public void onClick(View v) {
        hideKeyboard();
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.txt_bottom:
                changeToLogin(isLogin);
                break;
            case R.id.btn_login:
                if (isValid()){
                    if (!isLogin) {
                        tryLogin();
                    } else {
                        tryCreate();
                    }
                }

                break;
        }
    }

    private boolean isValid(){
        if (TextUtils.isEmpty(etEmail.getText().toString()) || !android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()){
            tlEmail.setError(getString(R.string.not_a_valid_email));
            return false;
        }

        if (TextUtils.isEmpty(etPassword.getText().toString())){
            tlPassword.setError(getString(R.string.invalid_password));
            return false;
        }
        return true;
    }

    private void tryLogin() {
        createProgressbar(getString(R.string.logging_in));
        ref.authWithPassword(etEmail.getText().toString(), etPassword.getText().toString(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                progressDialog.dismiss();
                SharedPrefsUtils.setStringPreference(getApplicationContext(),Constants.USER_ID,String.valueOf(authData.getUid()));
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                createSnackbar(firebaseError.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void tryCreate() {
        createProgressbar(getString(R.string.creating_account));
        ref.createUser(etEmail.getText().toString(), etPassword.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                progressDialog.dismiss();
                SharedPrefsUtils.setStringPreference(getApplicationContext(),Constants.USER_ID,String.valueOf(result.get("uid")));
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                progressDialog.dismiss();
                createSnackbar(firebaseError.getMessage());
            }
        });
    }

    private void createSnackbar(String msg) {
        Snackbar.make(findViewById(R.id.coordinatorLayout), msg, Snackbar.LENGTH_LONG).show();
    }

    private void createProgressbar(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(LoginActivity.this, R.style.Login_Dark_Dialog);
        }

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            SharedPrefsUtils.setStringPreference(getApplicationContext(),Constants.USER_ID,String.valueOf(acct.getId()));
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        } else {
            Toast.makeText(getApplicationContext(),getString(R.string.failed_to_sign_in) + result.getStatus(),Toast.LENGTH_LONG).show();
        }
    }

    private void changeToLogin(boolean login) {
        if (login) {
            signInButton.setVisibility(View.VISIBLE);
            textViewBottom.setText(R.string.no_account);
            btnLogin.setText(R.string.login);
        } else {
            signInButton.setVisibility(View.GONE);
            textViewBottom.setText(R.string.already_have_an_account);
            btnLogin.setText(R.string.create_account);
        }
        etPassword.setText("");
        etEmail.setText("");
        isLogin = !isLogin;
    }

}
