package com.epicodus.backtalkr.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.epicodus.backtalkr.BackTalkrApplication;
import com.epicodus.backtalkr.R;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.emailEditText) EditText mEmailEditText;
    @Bind(R.id.passwordEditText) EditText mPasswordEditText;
    @Bind(R.id.passwordLoginButton) Button mPasswordLoginButton;
    @Bind(R.id.registerButton) Button mRegisterButton;

    private ProgressDialog mAuthProgressDialog;
    private Firebase mFirebaseRef;
    private Firebase.AuthResultHandler mAuthResultHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mFirebaseRef = BackTalkrApplication.getAppInstance().getFirebaseRef();

        initializeAuthResultHandler();
        initializeProgressDialog();

        mPasswordLoginButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.passwordLoginButton:
                loginWithPassword();
                break;
            case R.id.registerButton:
                registerNewUser();
                break;
        }
    }

    public void loginWithPassword() {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        mFirebaseRef.authWithPassword(email, password, mAuthResultHandler);
    }

    public void registerNewUser() {
        final String email = mEmailEditText.getText().toString();
        final String password = mPasswordEditText.getText().toString();

        mFirebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {
                mFirebaseRef.authWithPassword(email, password, mAuthResultHandler);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Log.d("Registration error", firebaseError.toString());
            }
        });
    }

    private  void initializeProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog .setTitle("Loading");
        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
        mAuthProgressDialog.setCancelable(true);
    }

    private void initializeAuthResultHandler() {
        mAuthResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                goToMainActivity();
                mAuthProgressDialog.hide();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Log.d("Firebase auth error", firebaseError.toString());
                showErrorDialog(firebaseError.toString());
            }
        };
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
