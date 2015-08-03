package com.yukidev.ammocan.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yukidev.ammocan.R;
import com.yukidev.ammocan.utils.Crypto;
import com.yukidev.ammocan.utils.ParseConstants;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ViewMessageActivity extends ActionBarActivity {

    @InjectView(R.id.titleTextView)TextView mTitleText;
    @InjectView(R.id.actionTextView)TextView mActionText;
    @InjectView(R.id.resultTextView)TextView mResultText;
    @InjectView(R.id.impactTextView)TextView mImpactText;
    @InjectView(R.id.createdOnTextView)TextView mCreatedOn;
    private ParseObject mMessage;
    private Boolean mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_message);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        String mMessageId = intent.getStringExtra(ParseConstants.KEY_OBJECT_ID);
        mStorage = intent.getBooleanExtra(ParseConstants.LOCAL_STORAGE, false);

        try {

            mMessage = getMessage(mMessageId);

            String messageTitle = mMessage.getString(ParseConstants.KEY_BULLET_TITLE);
            String messageAction = mMessage.getString(ParseConstants.KEY_ACTION);
            String messageResult = mMessage.getString(ParseConstants.KEY_RESULT);
            String messageImpact = mMessage.getString(ParseConstants.KEY_IMPACT);
            String messageCreated = mMessage.getString(ParseConstants.KEY_CREATED_ON);

            String decryptedAction = decryptThis(mMessage.getString(ParseConstants.KEY_SENDER_ID),
                    messageAction);
            String decryptedResult = decryptThis(mMessage.getString(ParseConstants.KEY_SENDER_ID),
                    messageResult);
            String decryptedImpact = decryptThis(mMessage.getString(ParseConstants.KEY_SENDER_ID),
                    messageImpact);


            mTitleText.setText(messageTitle);
            mActionText.setText(decryptedAction);
            mResultText.setText(decryptedResult);
            mImpactText.setText(decryptedImpact);
            mCreatedOn.setText(messageCreated);

        }catch (ParseException e) {
            Toast.makeText(ViewMessageActivity.this,
                    "Problem retrieving message: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
        try {
            mMessage.put(ParseConstants.KEY_VIEWED, true);
            mMessage.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                    }
                }
            });
        } catch (NullPointerException npe) {
            finish();
        }

    }

    private ParseObject getMessage (String objectId) throws ParseException {
        ParseObject message;
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_MESSAGES);
        if (mStorage) {
            message = query.fromLocalDatastore().get(objectId);
        } else {
            message = query.get(objectId);
        }

        return message;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete Bullet?");
                builder.setMessage("This will completely delete this bullet!  There is no Undo . . .");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMessage.deleteInBackground();
                        Toast.makeText(ViewMessageActivity.this,
                                "Bullet deleted", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private String decryptThis(String pass, String encryptedData) {
        String decryptedData = "";

        try {
            Crypto crypto = new Crypto(pass);
            decryptedData = crypto.decrypt(encryptedData);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return decryptedData;
    }
}
