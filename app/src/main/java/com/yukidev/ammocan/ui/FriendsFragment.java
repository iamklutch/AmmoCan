package com.yukidev.ammocan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseSession;
import com.parse.ParseUser;
import com.yukidev.ammocan.R;

import java.util.List;

import com.yukidev.ammocan.adapters.UserAdapter;
import com.yukidev.ammocan.utils.ParseConstants;

/**
 * Created by James on 5/8/2015.
 */
public class FriendsFragment extends Fragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendRelation;
    protected ParseUser mCurrentUser;
    protected GridView mGridView;
    private Boolean mNetCheck;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.user_grid, container, false);

        Bundle args = getArguments();
        mNetCheck = args.getBoolean("netCheck");

        mCurrentUser = ParseUser.getCurrentUser();

        mGridView = (GridView)rootView.findViewById(R.id.friendsGrid);

        TextView emptyTextView = (TextView)rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mFriends.get(position).getObjectId().
                        equals(mCurrentUser.get(ParseConstants.KEY_SUPERVISOR_ID))){
                    // do nothing.
                } else {
                    ParseUser clickedUser = mFriends.get(position);
                    String clickedId = clickedUser.getObjectId();
                    Intent intent = new Intent(getActivity(), AirmanBulletsActivity.class);
                    intent.putExtra("objectId", clickedId);
                    intent.putExtra("download", false);
                    startActivity(intent);
                }

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        if (!mNetCheck) {
            //Offline
            ParseQuery<ParseUser> query = mFriendRelation.getQuery();
            query.addAscendingOrder(ParseConstants.KEY_USERNAME);
            query.fromLocalDatastore();
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> friends, ParseException e) {
                    if (e == null) {
                        mFriends = friends;
                        String[] usernames = new String[mFriends.size()];
                        int i = 0;
                        for (ParseUser user : mFriends) {
                            usernames[i] = user.getUsername();
                            i++;
                        }
                        if (mGridView.getAdapter() == null) {
                            UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
                            mGridView.setAdapter(adapter);
                        } else {
                            ((UserAdapter) mGridView.getAdapter()).refill(mFriends);
                        }
                    } else {
                        Log.e(TAG, e.getMessage());
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(e.getMessage())
                                .setTitle(R.string.error_title)
                                .setPositiveButton(android.R.string.ok, null);

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                }
            });
        } else {
            //Online
            ParseSession.getCurrentSessionInBackground();
            ParseQuery<ParseUser> query = mFriendRelation.getQuery();
            query.addAscendingOrder(ParseConstants.KEY_USERNAME);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> friends, ParseException e) {
                    if (e == null) {
                        ParseUser.pinAllInBackground(friends);
                        mFriends = friends;

                        if (mFriends.size() == 0) {
                            Toast.makeText(getActivity(), getString(R.string.add_friends_toast_label), Toast.LENGTH_LONG).show();
                        }

                        String[] usernames = new String[mFriends.size()];
                        int i = 0;
                        for (ParseUser user : mFriends) {
                            usernames[i] = user.getUsername();
                            i++;
                        }
                        if (mGridView.getAdapter() == null) {
                            try{
                                UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
                                mGridView.setAdapter(adapter);
                            } catch (NullPointerException npe) {

                            }

                        } else {
                            ((UserAdapter) mGridView.getAdapter()).refill(mFriends);
                        }

                    } else if (e.getMessage().equals("java.lang.ClassCastException: " +
                            "java.lang.String cannot be cast to org.json.JSONObject")) {
                        // Do nothing because this is a parse.com error

                    } else {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }

    }

}
