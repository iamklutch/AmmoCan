package com.yukidev.ammocan.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.yukidev.ammocan.R;
import com.yukidev.ammocan.utils.ExceptionHandler;
import com.yukidev.ammocan.utils.ParseConstants;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SignUpActivity extends ActionBarActivity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mPassword2;
    protected EditText mLastName;
    protected EditText mSquadron;
    protected EditText mEmail;
    protected Button mSignUpButton;
    @InjectView(R.id.signUpProgressBar)ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        setContentView(R.layout.activity_sign_up);
        ButterKnife.inject(this);
        mProgressBar.setVisibility(View.INVISIBLE);

        mUsername = (EditText)findViewById(R.id.usernameField);
        mPassword = (EditText)findViewById(R.id.passwordField);
        mPassword2 = (EditText)findViewById(R.id.passwordField2);
        mLastName = (EditText)findViewById(R.id.lastNameText);
        mSquadron = (EditText)findViewById(R.id.squadronText);
        mEmail = (EditText)findViewById(R.id.emailField);
        mSignUpButton = (Button)findViewById(R.id.signupButton);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()) {

                    String username = mUsername.getText().toString().trim().toLowerCase();
                    String password = mPassword.getText().toString().trim();
                    String password2 = mPassword2.getText().toString().trim();
                    String lastName = mLastName.getText().toString().trim();
                    String squadron = mSquadron.getText().toString().toUpperCase();
                    String email = mEmail.getText().toString().trim().toLowerCase();

                    if (username.isEmpty() || password.isEmpty() || email.isEmpty() ||
                            username.length() < 5 || password.length() < 8) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                        builder.setMessage(R.string.signup_error_message)
                                .setTitle(R.string.signup_error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else if (!password.equals(password2)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                        builder.setMessage(R.string.password_error_message)
                                .setTitle(R.string.signup_error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else {
                        // create new user
                        mProgressBar.setVisibility(View.VISIBLE);

                        ParseUser newUser = new ParseUser();
                        newUser.setUsername(username);
                        newUser.setPassword(password);
                        newUser.setEmail(email);
                        newUser.put(ParseConstants.KEY_LASTNAME, lastName);
                        newUser.put(ParseConstants.KEY_SQUADRON, squadron);
                        newUser.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    // success creating user
                                    mProgressBar.setVisibility(View.INVISIBLE);

                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                    builder.setMessage(e.getMessage())
                                            .setTitle(R.string.signup_error_title)
                                            .setPositiveButton(android.R.string.ok, null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Internet connection unavailable", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }
}
