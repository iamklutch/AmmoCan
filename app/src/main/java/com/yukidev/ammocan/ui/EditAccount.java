package com.yukidev.ammocan.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yukidev.ammocan.R;
import com.yukidev.ammocan.utils.ParseConstants;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EditAccount extends ActionBarActivity {

    @InjectView(R.id.currentUsernameField)TextView mUsername;
    @InjectView(R.id.currentUserLastName)EditText mLastName;
    @InjectView(R.id.currentUserSquadron)EditText mSquadron;
    @InjectView(R.id.currentEmail)EditText mCurrentEmail;
    @InjectView(R.id.newPasswordField)EditText mNewPassA;
    @InjectView(R.id.newPasswordField2)EditText mNewPassB;
    @InjectView(R.id.changePasswordButton)Button mPassButton;
    private ParseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        ButterKnife.inject(this);
        mCurrentUser = ParseUser.getCurrentUser();

        mUsername.setText(mCurrentUser.get(ParseConstants.KEY_DISPLAY_NAME).toString());
        mLastName.setText(mCurrentUser.get(ParseConstants.KEY_LASTNAME).toString());
        mSquadron.setText(mCurrentUser.get(ParseConstants.KEY_SQUADRON).toString());
        mCurrentEmail.setText(mCurrentUser.getEmail());

        mPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPass = mNewPassA.getText().toString().trim();
                String newPassB = mNewPassB.getText().toString().trim();

                if (newPass.equals("") || newPassB.equals("")) {
                    Toast.makeText(EditAccount.this,
                            getString(R.string.password_change_empty_toast),
                            Toast.LENGTH_LONG).show();
                } else if (newPass.length() < 8 || newPassB.length() < 8) {
                    Toast.makeText(EditAccount.this,
                            getString(R.string.new_password_length_error_toast),
                            Toast.LENGTH_LONG).show();
                } else if (!newPass.equals(newPassB)) {
                    Toast.makeText(EditAccount.this,
                            getString(R.string.password_error_message),
                            Toast.LENGTH_LONG).show();
                } else {
                    mCurrentUser.setPassword(newPass);
                    mCurrentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                Toast.makeText(EditAccount.this,
                                        getString(R.string.password_changed_toast),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(EditAccount.this,
                                        "There was an error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_edit_account_save_changes:
                mCurrentUser.put(ParseConstants.KEY_LASTNAME, mLastName.getText().toString());
                mCurrentUser.put(ParseConstants.KEY_SQUADRON, mSquadron.getText().toString().toUpperCase());
                mCurrentUser.setEmail(mCurrentEmail.getText().toString());
                mCurrentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(EditAccount.this,
                                    "Changes Saved!",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(EditAccount.this,
                                    "There was a problem: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
