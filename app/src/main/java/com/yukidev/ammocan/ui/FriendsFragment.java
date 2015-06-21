package com.yukidev.ammocan.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
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
    protected ImageButton mSendButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.user_grid, container, false);

        mGridView = (GridView)rootView.findViewById(R.id.friendsGrid);

        TextView emptyTextView = (TextView)rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

        mSendButton = (ImageButton)rootView.findViewById(R.id.userGridImageButton);
        mSendButton.setVisibility(View.INVISIBLE);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseSession.getCurrentSessionInBackground();

        ParseQuery<ParseUser> query =  mFriendRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {

                if (e == null) {

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
                        UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
                        mGridView.setAdapter(adapter);
                    }else {
                        ((UserAdapter)mGridView.getAdapter()).refill(mFriends);
                    }


                } else {
                    Log.e(TAG, e.getMessage());
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setMessage(e.getMessage())
//                            .setTitle(R.string.error_title)
//                            .setPositiveButton(android.R.string.ok, null);
//
//                    AlertDialog dialog = builder.create();
//                    dialog.show();

                }
            }
        });

    }
}
