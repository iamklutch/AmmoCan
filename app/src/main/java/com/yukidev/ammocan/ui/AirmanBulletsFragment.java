package com.yukidev.ammocan.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.yukidev.ammocan.R;
import com.yukidev.ammocan.adapters.MessageAdapter;
import com.yukidev.ammocan.utils.ParseConstants;

import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by James on 6/30/2015.
 */
public class AirmanBulletsFragment extends android.support.v4.app.ListFragment {

    private static final String TAG = AirmanBulletsFragment.class.getSimpleName();

    private ProgressBar mProgressBar;
    protected List<ParseObject> mMessages;
    protected String mObjectId;
    private Boolean mDownload;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        Intent intent = getActivity().getIntent();
        mObjectId = intent.getStringExtra("objectId");
        mDownload = intent.getBooleanExtra("download", false);
        retrieveMessages();
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.inboxProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        return rootView;
    }

    protected void retrieveMessages() {

        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_SENDER_ID, mObjectId);
        query.whereEqualTo(ParseConstants.KEY_MESSAGE_TYPE, ParseConstants.MESSAGE_TYPE_BULLET);
        //if statement gets owners bullets from online
        if (!mDownload){
            query.fromPin(ParseConstants.CLASS_MESSAGES);
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {

                if (e == null) {
                    //success
                    mMessages = messages;
                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    try {
                        for (ParseObject message : mMessages) {
                            usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                            i++;
//                            if (getListView().getAdapter() == null) {
                            MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                            setListAdapter(adapter);
//                            } else {
//                                ((MessageAdapter) getListView().getAdapter()).refill(mMessages);
//                            }
                        }
                    } catch (ConcurrentModificationException ccm) {
                        Log.e(TAG, "ConcurrentMod Exception: " + ccm.getMessage());
                    }

                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_download:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.download_bullets_title));
                builder.setMessage(getString(R.string.download_bullets_message));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadMessages();
                    }
                });
                builder.setNegativeButton("CANCEL", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete Bullet?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "Bullet deleted", Toast.LENGTH_LONG).show();
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

        ParseObject message = mMessages.get(position);

        Intent intent = new Intent(getActivity(), ViewMessageActivity.class);
        intent.putExtra(ParseConstants.KEY_OBJECT_ID, message.getObjectId());
        intent.putExtra(ParseConstants.LOCAL_STORAGE, true);
        startActivity(intent);
    }

    public void downloadMessages(){

        mProgressBar.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_SENDER_ID, mObjectId);
        query.whereEqualTo(ParseConstants.KEY_MESSAGE_TYPE, ParseConstants.MESSAGE_TYPE_BULLET);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                if (e == null) {
                    //success
                    ParseObject.pinAllInBackground(ParseConstants.CLASS_MESSAGES, messages);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    mMessages = messages;
                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    try {
                        for (ParseObject message : mMessages) {
                            usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                            i++;
                            if (getListView().getAdapter() == null) {
                                adapter = new MessageAdapter(getListView().getContext(), mMessages);
                                setListAdapter(adapter);
                            } else {
                                ((MessageAdapter) getListView().getAdapter()).refill(mMessages);
                            }
                        }
                    } catch (ConcurrentModificationException ccm) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Log.e(TAG, "ConcurrentMod Exception: " + ccm.getMessage());
                    }

                }
            }
        });
    }
}