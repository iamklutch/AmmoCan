package com.yukidev.ammocan.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.yukidev.ammocan.R;
import com.yukidev.ammocan.adapters.MessageAdapter;
import com.yukidev.ammocan.utils.ParseConstants;

import java.util.List;

/**
 * Created by James on 6/30/2015.
 */
public class AirmanBulletsFragment extends android.support.v4.app.ListFragment {

    protected List<ParseObject> mMessages;
    protected String mObjectId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        Intent intent = getActivity().getIntent();
        mObjectId = intent.getStringExtra("objectId");
        retrieveMessages();
        return rootView;
    }

    private void retrieveMessages() {
        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_MESSAGES);
        query.fromLocalDatastore();
        query.whereEqualTo(ParseConstants.KEY_SENDER_ID, mObjectId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {

                if (e == null) {
                    //success
                    mMessages = messages;
                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                        if (getListView().getAdapter() == null) {
                            MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                            setListAdapter(adapter);
                        } else {
                            ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
                        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
        startActivity(intent);
    }
}