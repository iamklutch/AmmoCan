package com.yukidev.ammocan.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yukidev.ammocan.R;
import com.yukidev.ammocan.adapters.SectionsPagerAdapter;
import com.yukidev.ammocan.utils.ParseConstants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener{


    private ParseUser mCurrentUser;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = new Bundle();
        bundle.putBoolean("netCheck", isNetworkAvailable());

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), bundle);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        mCurrentUser = ParseUser.getCurrentUser();

        if(mCurrentUser == null) {
            navigateToLogin();
        }
        else {
            messageUpdater();
        }


        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {

            actionBar.addTab(
                    actionBar.newTab()
                            // This is to set the Icon when you have some.
                            .setIcon(mSectionsPagerAdapter.getIcon(i)).setTabListener(this));
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                break;
            case R.id.action_edit_account:
                Intent intent = new Intent(this, EditAccount.class);
                startActivity(intent);
                break;

            case R.id.action_see_my_bullets:
                String clickedId = mCurrentUser.getObjectId().toString();
                intent = new Intent(MainActivity.this, AirmanBulletsActivity.class);
                intent.putExtra("objectId", clickedId);
                intent.putExtra("download", true);
                startActivity(intent);
                break;

            case R.id.action_clear_list:
                clearList();
                break;

            case R.id.action_edit_friends:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.supervisor_airmen, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.action_message:
//                if (mCurrentUser.get(ParseConstants.KEY_SUPERVISOR_ID).equals("none")){
//                    Toast.makeText(this, "Please add a supervisor first!", Toast.LENGTH_LONG).show();
//                } else {
                    intent = new Intent(this, MessageActivity.class);
                    startActivity(intent);
//                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent intent;
            switch (which) {
                case 0:
                    intent = new Intent(MainActivity.this, EditSupervisorActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(MainActivity.this, EditAirmenActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void messageUpdater() {
        ParseQuery<ParseObject> query1 = new ParseQuery<>(ParseConstants.CLASS_MESSAGES);
        query1.fromLocalDatastore();
        query1.whereEqualTo(ParseConstants.KEY_VIEWED, false);

        ParseQuery<ParseObject> query2 = new ParseQuery<>(ParseConstants.CLASS_MESSAGES);
        query2.fromLocalDatastore();
        query2.whereEqualTo(ParseConstants.KEY_BEEN_SENT, false);

        List<ParseQuery<ParseObject>> bothQuerys = new ArrayList<>();
        bothQuerys.add(query1);
        bothQuerys.add(query2);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(bothQuerys);
        mainQuery.fromLocalDatastore();
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (!isNetworkAvailable()) {
                        // no network so don't try to send message.
                        Log.e("MainActivity: ", "MessageUpdater: No network");
                    } else {
                        for (int i = 0; i < list.size(); i++) {
                            ParseObject currentMessage = list.get(i);
                            currentMessage.put(ParseConstants.KEY_BEEN_SENT, true);
                            currentMessage.saveEventually(new SaveCallback() {
                                @Override
                                public void done(ParseException f) {
                                    if (f == null) {

                                    } else {
                                        Log.e("MainActivity: ", "KEY_BEEN_SENT failed");
                                    }
                                }
                            });
                        }
                    }

                } else {
                    Log.e("MainActivity: ", "MessageUpdater: failed retrieving local messages");
                }

            }
        });
    }

    private void clearList(){
        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_MESSAGES);
        query.fromLocalDatastore();
        query.whereEqualTo(ParseConstants.KEY_VIEWED, false);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (!isNetworkAvailable()) {

                        // no network so don't try to send message.
                        Toast.makeText(MainActivity.this,
                                getString(R.string.internet_connection_unavailable),
                                Toast.LENGTH_LONG).show();
                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(getString(R.string.clear_inbox_title));
                        builder.setMessage(getString(R.string.clear_inbox_message));
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i = 0; i < list.size(); i++) {
                                    ParseObject currentMessage = list.get(i);
                                    currentMessage.put(ParseConstants.KEY_VIEWED, true);
                                    currentMessage.saveEventually();
                                }
                            }
                        });
                        builder.setNegativeButton("CANCEL", null);
                        builder.create().show();

                    }
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }



}
