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
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);

        signInButton.setStyle(SignInButton.SIZE_WIDE, SignInButton.COLOR_LIGHT);
        signInButton.setScopes(gso.getScopeArray());

        signInButton.setOnClickListener(this);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.wtf("regi", "failed " + connectionResult);
    }

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
            tlEmail.setError("Not a valid email");
            return false;
        }

        if (TextUtils.isEmpty(etPassword.getText().toString())){
            tlPassword.setError("Invalid password");
            return false;
        }
        return true;
    }

    private void tryLogin() {
        createProgressbar("Logging in...");
        ref.authWithPassword(etEmail.getText().toString(), etPassword.getText().toString(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                progressDialog.dismiss();
                SharedPrefsUtils.setStringPreference(getApplicationContext(),Constants.USERID,String.valueOf(authData.getUid()));
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
        createProgressbar("Creating account...");
        ref.createUser(etEmail.getText().toString(), etPassword.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                progressDialog.dismiss();
                SharedPrefsUtils.setStringPreference(getApplicationContext(),Constants.USERID,String.valueOf(result.get("uid")));
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
//        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        Log.wtf("regi", "result is is " + result.isSuccess());
        Log.wtf("regi", "L>>> " + result.getStatus().getStatusMessage());


        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
//            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));

            Log.wtf("regi", "name is " + acct.getDisplayName());
            Log.wtf("regi", "> " + acct.getEmail());
            Log.wtf("regi", ">> " + acct.getId()); //save this
            Log.wtf("regi", ">> " + acct.getIdToken());


//            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
//            updateUI(false);
        }
    }

    private void changeToLogin(boolean login) {
        if (login) {
            signInButton.setVisibility(View.VISIBLE);
            textViewBottom.setText("No account?");
            btnLogin.setText("Login");
        } else {
            signInButton.setVisibility(View.GONE);
            textViewBottom.setText("Already have an account?");
            btnLogin.setText("Create account");
        }
        etPassword.setText("");
        etEmail.setText("");
        isLogin = !isLogin;
    }

}
