package com.yukidev.ammocan.ui;

import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yukidev.ammocan.R;
import com.yukidev.ammocan.adapters.UserAdapter;
import com.yukidev.ammocan.utils.ParseConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MessageActivity extends ActionBarActivity {

    private static final String TAG = MessageActivity.class.getSimpleName();

    @InjectView(R.id.actionText) EditText mActionText;
    @InjectView(R.id.resultText) EditText mResultText;
    @InjectView(R.id.impactText) EditText mImpactText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.inject(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_message_send:
                ParseObject message = createMessage();
                if (message == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                    builder.setMessage(getString(R.string.title_section1))
                            .setTitle(getString(R.string.sorry))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    send(message);
                    finish();
                }
                send(message);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //success
                    Toast.makeText(MessageActivity.this, getString(R.string.success_message),
                            Toast.LENGTH_LONG).show();
                    sendPushNotifications();
                }
                else {
                    Log.e(TAG, e.getMessage());

                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                    builder.setMessage(getString(R.string.error_sending_message))
                            .setTitle(getString(R.string.sorry))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    protected ParseObject createMessage() {

        ParseObject message = new ParseObject(ParseConstants.CLASS_MESASGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_SUPERVISOR_ID, getSupervisorIds());
        message.put(ParseConstants.KEY_ACTION, mActionText.getText().toString());
        message.put(ParseConstants.KEY_RESULT, mResultText.getText().toString());
        message.put(ParseConstants.KEY_IMPACT, mImpactText.getText().toString());


//        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
//
//        if (fileBytes == null) {
//            return null;
//        }
//        else {
//            if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
//                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
//            }
//            String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
//            ParseFile file = new ParseFile(fileName, fileBytes);
//            message.put(ParseConstants.KEY_FILE, file);
            return message;
//        }
    }

    protected ArrayList<String> getSupervisorIds() {
        ArrayList<String> recipientIds = new ArrayList<String>();
//        // for loops through all ids, getCount() sets how many there are in the gotten list.
//        for (int i = 0 ; i < mGridView.getCount() ; i++) {
//            if (mGridView.isItemChecked(i)) {
//                recipientIds.add(mFriends.get(i).getObjectId());
//            }
//        }
        return recipientIds;
    }

    protected void sendPushNotifications() {
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.KEY_USER_ID, getSupervisorIds());

        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(getString(R.string.push_message, ParseUser.getCurrentUser().getUsername()));
        push.sendInBackground();

    }

    @Override
    public void onResume() {
        super.onResume();
//
//        mCurrentUser = ParseUser.getCurrentUser();
//        mFriendRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
//
//        ParseQuery<ParseUser> query =  mFriendRelation.getQuery();
//        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
//        query.findInBackground(new FindCallback<ParseUser>() {
//
//            @Override
//            public void done(List<ParseUser> friends, ParseException e) {
//                if (e == null) {
//
//                    // gets user friends list
//                    mFriends = friends;
//
//                    String[] usernames = new String[mFriends.size()];
//                    int i = 0;
//                    for (ParseUser user : mFriends) {
//                        usernames[i] = user.getUsername();
//                        i++;
//                    }
//                    if (mGridView.getAdapter() == null) {
//                        UserAdapter adapter = new UserAdapter(MessageActivity.this, mFriends);
//                        mGridView.setAdapter(adapter);
//                    } else {
//                        ((UserAdapter) mGridView.getAdapter()).refill(mFriends);
//                    }
//
//                } else {
//                    Log.e(TAG, e.getMessage());
//                    AlertDialog.Builder builder = new AlertDialog.Builder(mGridView.getContext());
//                    builder.setMessage(e.getMessage())
//                            .setTitle(R.string.error_title)
//                            .setPositiveButton(android.R.string.ok, null);
//
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//
//                }
//            }
//        });
//
    }


}
