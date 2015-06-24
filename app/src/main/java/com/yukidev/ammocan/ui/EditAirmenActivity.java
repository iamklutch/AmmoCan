package com.yukidev.ammocan.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yukidev.ammocan.R;
import com.yukidev.ammocan.utils.ParseConstants;
import com.yukidev.ammocan.adapters.UserAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class EditAirmenActivity extends Activity {

    public static final String TAG = EditAirmenActivity.class.getSimpleName();

    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendRelation;
    protected ParseUser mCurrentUser;
    protected GridView mGridView;
    protected ImageButton mSendButton;
    protected String mSearchUsername;

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

        mSendButton = (ImageButton)findViewById(R.id.userGridImageButton);
        mSendButton.setVisibility(View.INVISIBLE);


    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText searchUsername = new EditText(this);
        searchUsername.setHint("Airman's username");
        builder.setTitle(getString(R.string.search_title));
        builder.setMessage("Enter your airman's username");
        builder.setView(searchUsername);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSearchUsername = searchUsername.getText().toString().trim();
                mProgressBar.setVisibility(View.VISIBLE);

                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereStartsWith(ParseConstants.KEY_USERNAME, mSearchUsername);
                query.orderByAscending(ParseConstants.KEY_USERNAME);
                query.setLimit(100);
                query.findInBackground(new FindCallback<ParseUser>() {

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
        builder.setCancelable(false);
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
            ImageView checkImageView = (ImageView)view.findViewById(R.id.checkImageView);

            if (mGridView.isItemChecked(position)){
                //  add friend
                mFriendRelation.add(mUsers.get(position));
                checkImageView.setVisibility(View.VISIBLE);
            }
            else {
                // remove friend
                mFriendRelation.remove(mUsers.get(position));
                checkImageView.setVisibility(View.INVISIBLE);
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