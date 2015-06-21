package com.yukidev.ammocan.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yukidev.ammocan.R;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by James on 5/8/2015.
 */
public class InboxFragment extends android.support.v4.app.ListFragment {

    protected List<ParseObject> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        return rootView;
    }

    protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh() {
            // remember getActivity() for context
            Toast.makeText(getActivity(), "reeeeeefresh", Toast.LENGTH_LONG).show();
//            retrieveMessages();
        }
    };


    @Override
    public void onResume() {
        super.onResume();

//         set progressbar visible

//        retrieveMessages();
    }

//    private void retrieveMessages() {
//        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_MESASGES);
//        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
//        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> messages, ParseException e) {
//                // set progress bar invisible
//
//                if (mSwipeRefreshLayout.isRefreshing()) {
//                    mSwipeRefreshLayout.setRefreshing(false);
//                }
//
//                if (e == null) {
//                    //success
//                    mMessages = messages;
//                    String[] usernames = new String[mMessages.size()];
//                    int i = 0;
//                    for (ParseObject message : mMessages) {
//                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
//                        i++;
//                        if (getListView().getAdapter() == null) {
//                            MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
//                            setListAdapter(adapter);
//                        } else {
//                            ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
//                        }
//                    }
//
//                }
//
//            }
//        });
//    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

//        ParseObject message = mMessages.get(position);
//        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
//        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
//        Uri fileUri = Uri.parse(file.getUrl());
//
//        if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
//            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
//            intent.setData(fileUri);
//            startActivity(intent);
//        }
//        else {
//            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
//            intent.setDataAndType(fileUri, "video/*");
//            startActivity(intent);
//        }
//        // delete message
//        List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);
//
//        if (ids.size() == 1) {
//            // last recipient delete message
//            message.deleteInBackground();
//        }
//        else {
//            //remove recipient
//            ids.remove(ParseUser.getCurrentUser().getObjectId());
//
//            ArrayList<String> idsToRemove = new ArrayList<String>();
//            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());
//
//            message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
//            message.saveInBackground();
//        }
    }
}
