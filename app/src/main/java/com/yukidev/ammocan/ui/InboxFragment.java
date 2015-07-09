package com.yukidev.ammocan.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yukidev.ammocan.R;
import com.yukidev.ammocan.adapters.MessageAdapter;
import com.yukidev.ammocan.utils.ParseConstants;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;



/**
 * Created by James on 5/8/2015.
 */
public class InboxFragment extends android.support.v4.app.ListFragment {

    private static final String TAG = InboxFragment.class.getSimpleName();
    protected List<ParseObject> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private Boolean mNetCheck;
    protected ParseRelation<ParseUser> mFriendRelation;
    private ParseUser mCurrentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        try {
            Bundle args = getArguments();
            mNetCheck = args.getBoolean("netCheck");
        } catch (NullPointerException e) {

        }

        mCurrentUser = ParseUser.getCurrentUser();

        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        mProgressBar = (ProgressBar)rootView.findViewById(R.id.inboxProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        return rootView;
    }

    protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh() {
            // remember getActivity() for context
            mProgressBar.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "Retrieving messages . . .", Toast.LENGTH_SHORT).show();
            retrieveMessages();
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        // set progressbar visible
        mProgressBar.setVisibility(View.VISIBLE);
        retrieveMessages();
    }

    private void retrieveMessages() {
        if (!mNetCheck) {
            Toast.makeText(getActivity(), "Network unavailable", Toast.LENGTH_LONG).show();
        } else {

            ParseQuery<ParseObject> query1 = new ParseQuery<>(ParseConstants.CLASS_MESSAGES);
            query1.whereEqualTo(ParseConstants.KEY_SUPERVISOR_ID, ParseUser.getCurrentUser().getObjectId());
            query1.whereEqualTo(ParseConstants.KEY_VIEWED, false);

            ParseQuery<ParseObject> query2 = new ParseQuery<>(ParseConstants.CLASS_MESSAGES);
            query2.whereEqualTo(ParseConstants.KEY_MESSAGE_TYPE, ParseConstants.MESSAGE_TYPE_REQUEST);
            query2.whereEqualTo(ParseConstants.KEY_TARGET_USER, mCurrentUser.getObjectId());

            List<ParseQuery<ParseObject>> bothQuerys = new ArrayList<ParseQuery<ParseObject>>();
            bothQuerys.add(query1);
            bothQuerys.add(query2);

            ParseQuery<ParseObject> mainQuery = ParseQuery.or(bothQuerys);
            mainQuery.addAscendingOrder(ParseConstants.KEY_CREATED_ON);
            mainQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> messages, ParseException e) {
                    // set progress bar invisible
                    mProgressBar.setVisibility(View.INVISIBLE);

                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    if (e == null) {
                        //success
                        mMessages = messages;
                        ParseObject.pinAllInBackground(ParseConstants.CLASS_MESSAGES, messages);
                        String[] usernames = new String[mMessages.size()];
                        int i = 0;
                        try {
                            for (ParseObject message : mMessages) {
                                try {
                                    usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                                    i++;
                                    if (getListView().getAdapter() == null) {
                                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                                        setListAdapter(adapter);
                                    } else {
                                        ((MessageAdapter) getListView().getAdapter()).refill(mMessages);
                                    }
                                } catch (IllegalStateException ise) {
                                    Log.d(TAG, "Content view not yet created" + ise.getMessage());
                                }

                            }
                        } catch (ConcurrentModificationException ccm) {
                            Log.d(TAG, "Caught exception: " + ccm.getMessage());
                        }

                    }

                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete Request/Bullet?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "Request/Bullet deleted", Toast.LENGTH_LONG).show();
                        ParseObject message = mMessages.get(position);
                        message.deleteInBackground();
                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                        view.setVisibility(View.GONE);
                        adapter.remove(message);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("CANCEL", null);
                AlertDialog dialog = builder.create();
                dialog.show();

                return false;
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        final ParseObject message = mMessages.get(position);
        final View mView = v;
        if (message.get(ParseConstants.KEY_MESSAGE_TYPE) == ParseConstants.MESSAGE_TYPE_BULLET) {
            Intent intent = new Intent(getActivity(), ViewMessageActivity.class);
            intent.putExtra(ParseConstants.KEY_OBJECT_ID, message.getObjectId());
            intent.putExtra(ParseConstants.LOCAL_STORAGE, false);
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("New request from " +
                    message.get(ParseConstants.KEY_SENDER_NAME));
            builder.setMessage(message.get(ParseConstants.KEY_SENDER_NAME) +
                    " wants to add you as their " +
                    message.get(ParseConstants.KEY_REQUEST_TYPE));
            builder.setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                    userQuery.whereEqualTo(ParseConstants.KEY_OBJECT_ID,
                            message.get(ParseConstants.KEY_SENDER_ID));
                    userQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (e == null) {
                                mFriendRelation = mCurrentUser.
                                        getRelation(ParseConstants.KEY_FRIENDS_RELATION);
                                mFriendRelation.add(parseUser);
                                if (message.get(ParseConstants.KEY_REQUEST_TYPE).equals("Supervisor")) {
                                    mCurrentUser.put(ParseConstants.KEY_SUPERVISOR_ID, message.
                                            get(ParseConstants.KEY_SENDER_ID));
                                    mCurrentUser.put(ParseConstants.KEY_SUPERVISOR_USERNAME, message.
                                            get(ParseConstants.KEY_SENDER_NAME));
                                }
                                mCurrentUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            message.deleteInBackground();

                                            MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                                            mView.setVisibility(View.GONE);
                                            adapter.remove(message);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            // don't delete user request
                                        }
                                    }
                                });


                            } else {
                                //  couldn't get user to add
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("CANCEL", null);
            builder.create().show();
        }


    }
}
