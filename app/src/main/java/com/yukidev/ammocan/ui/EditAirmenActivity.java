package com.yukidev.ammocan.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yukidev.ammocan.R;
import com.yukidev.ammocan.utils.ParseConstants;
import com.yukidev.ammocan.adapters.UserAdapter;
import com.yukidev.ammocan.utils.PushHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class EditAirmenActivity extends ActionBarActivity {

    public static final String TAG = EditAirmenActivity.class.getSimpleName();

    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendRelation;
    protected ParseUser mCurrentUser;
    protected int mPosition;
    private View mView;
    protected GridView mGridView;
    protected String mUnitSearchVariable;
    protected String mLastNameSearchVariable;
    protected String mUsernameSearchVariable;

    @InjectView(R.id.userGridProgressBar)ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid);
        ButterKnife.inject(this);


        mGridView = (GridView)findViewById(R.id.friendsGrid);
        // allows the check boxes to be checked (multiple)
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        mGridView.setOnItemClickListener(mOnItemClickListener);

        TextView emptyTextView = (TextView)findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText searchVariable = new EditText(this);
        searchVariable.setHint("Username, last name or unit");
        builder.setTitle(getString(R.string.search_title));
        builder.setMessage("Enter your airman's username, last name, or unit");
        builder.setView(searchVariable);
        builder.setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUsernameSearchVariable = searchVariable.getText().toString().trim().toLowerCase();
                mLastNameSearchVariable = searchVariable.getText().toString();
                mUnitSearchVariable = searchVariable.getText().toString().trim().toUpperCase();
                mProgressBar.setVisibility(View.VISIBLE);

                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereContains(ParseConstants.KEY_USERNAME, mUsernameSearchVariable);
                query.whereNotEqualTo(ParseConstants.KEY_OBJECT_ID, mCurrentUser.getObjectId());

                ParseQuery<ParseUser> query2 = ParseUser.getQuery();
                query2.whereEqualTo(ParseConstants.KEY_LASTNAME, mLastNameSearchVariable);
                query2.whereNotEqualTo(ParseConstants.KEY_OBJECT_ID, mCurrentUser.getObjectId());

                ParseQuery<ParseUser> query3 = ParseUser.getQuery();
                query3.whereContains(ParseConstants.KEY_SQUADRON, mUnitSearchVariable);
                query3.whereNotEqualTo(ParseConstants.KEY_OBJECT_ID, mCurrentUser.getObjectId());

                List<ParseQuery<ParseUser>> allQuerys = new ArrayList<ParseQuery<ParseUser>>();
                allQuerys.add(query);
                allQuerys.add(query2);
                allQuerys.add(query3);

                ParseQuery<ParseUser> mainQuery = ParseQuery.or(allQuerys);
                mainQuery.orderByAscending(ParseConstants.KEY_LASTNAME);
                mainQuery.setLimit(100);
                mainQuery.findInBackground(new FindCallback<ParseUser>() {

                    @Override
                    public void done(List<ParseUser> list, ParseException e) {
                        if (e == null) {
                            // find user
                            mUsers = list;
                            String[] usernames = new String[mUsers.size()];
                            int i = 0;
                            for (ParseUser user : mUsers) {
                                usernames[i] = user.getUsername();
                                i++;
                            }
                            if (mGridView.getAdapter() == null) {
                                UserAdapter adapter = new UserAdapter(EditAirmenActivity.this, mUsers);
                                mGridView.setAdapter(adapter);


                                addFriendCheckmarks();

                                mProgressBar.setVisibility(View.INVISIBLE);
                            } else {
                                mProgressBar.setVisibility(View.INVISIBLE);

                                Log.e(TAG, e.getMessage());
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditAirmenActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle(R.string.error_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    }

                });
            }
        });
        builder.setNegativeButton("CANCEL", null);
        builder.create().show();
    }
    private void addFriendCheckmarks() {

        mFriendRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    // list returned, look for match
                    for (int i = 0; i < mUsers.size(); i++) {
                        ParseUser user = mUsers.get(i);

                        for (ParseUser friend : friends) {
                            if (friend.getObjectId().equals(user.getObjectId())) {
                                //  sets checkmark
                                mGridView.setItemChecked(i, true);
                            }
                        }
                    }
                }
                else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_supervisor_and_airmen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.action_done:
                intent = new Intent(EditAirmenActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.action_another:
                intent = new Intent(EditAirmenActivity.this, EditAirmenActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final ImageView checkImageView = (ImageView)view.findViewById(R.id.checkImageView);
            checkImageView.setImageResource(R.drawable.avatar_request_pending);
            checkImageView.setVisibility(View.VISIBLE);
            mPosition = position;
            mView = view;

            if (mGridView.isItemChecked(position)){
                ParseObject request = new ParseObject(ParseConstants.CLASS_MESSAGES);
                request.put(ParseConstants.KEY_SENDER_ID, mCurrentUser.getObjectId());
                request.put(ParseConstants.KEY_SENDER_NAME, mCurrentUser.getUsername());
                request.put(ParseConstants.KEY_TARGET_USER, mUsers.get(position).getObjectId());
                request.put(ParseConstants.KEY_REQUEST_TYPE, "Airman");
                request.put(ParseConstants.KEY_ACTION, "Airman");
                request.put(ParseConstants.KEY_SUPERVISOR_ID, mCurrentUser.getObjectId());
                request.put(ParseConstants.KEY_MESSAGE_TYPE, ParseConstants.MESSAGE_TYPE_REQUEST);
                request.put(ParseConstants.KEY_BULLET_TITLE, mCurrentUser.getUsername() +
                        " wants to add you!");
                request.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            PushHelper push = new PushHelper();
                            push.sendPushNotification(mUsers.get(mPosition).getObjectId(),
                                    getString(R.string.request_push_message,
                                            ParseUser.getCurrentUser().getUsername()));
                            Toast.makeText(EditAirmenActivity.this, "Request sent",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(EditAirmenActivity.this,
                                    "Problem sending request, please try later",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
//                }
            }
            else {
                // remove friend
                AlertDialog.Builder builder = new AlertDialog.Builder(EditAirmenActivity.this);
                builder.setTitle("Remove this Airman?");
                builder.setMessage("Click OK to remove this person.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUsers.get(mPosition).unpinInBackground();
                        mFriendRelation.remove(mUsers.get(mPosition));
                        checkImageView.setVisibility(View.INVISIBLE);
                        mCurrentUser.saveInBackground();
                    }
                });
                builder.setNegativeButton("CANCEL", null);
                builder.create().show();
            }

            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {

                    } else {

                        Toast.makeText(EditAirmenActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

    };
}
