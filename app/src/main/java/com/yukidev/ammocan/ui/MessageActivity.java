package com.yukidev.ammocan.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yukidev.ammocan.R;
import com.yukidev.ammocan.utils.Crypto;
import com.yukidev.ammocan.utils.DateHelper;
import com.yukidev.ammocan.utils.ParseConstants;
import com.yukidev.ammocan.utils.PushHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MessageActivity extends ActionBarActivity {

    private ParseObject mMessage;
    InterstitialAd mInterstitialAd;
    @InjectView(R.id.titleText) EditText mTitleText;
    @InjectView(R.id.actionText) EditText mActionText;
    @InjectView(R.id.resultText) EditText mResultText;
    @InjectView(R.id.impactText) EditText mImpactText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.inject(this);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7998491028752978/2165922641");
        requestNewInterstitial();
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
                message.pinInBackground();
                if (message == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                    builder.setMessage(getString(R.string.title_section1))
                            .setTitle(getString(R.string.sorry))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else if (!isNetworkAvailable()){
                    message.put(ParseConstants.KEY_BEEN_SENT, false);
                    message.pinInBackground();
                    Toast.makeText(this, "Network unavailable, will send message when connection is available",
                            Toast.LENGTH_LONG).show();
                } else {
                    message.put(ParseConstants.KEY_BEEN_SENT, true);
                    send(message);
                    finish();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void send(ParseObject message) {
        mMessage = message;

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            mMessage.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        //success
                        Toast.makeText(MessageActivity.this, getString(R.string.success_message),
                                Toast.LENGTH_LONG).show();
                        PushHelper push = new PushHelper();
                        push.sendPushNotification(getSupervisorIds(),
                                getString(R.string.bullet_push_message,
                                        ParseUser.getCurrentUser().getUsername()));
                    } else {
                        mMessage.put(ParseConstants.KEY_BEEN_SENT, false);
                        mMessage.pinInBackground();
                        Toast.makeText(MessageActivity.this, "Problem sending message: " +
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                mMessage.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            //success
                            Toast.makeText(MessageActivity.this, getString(R.string.success_message),
                                    Toast.LENGTH_LONG).show();
                            PushHelper push = new PushHelper();
                            push.sendPushNotification(getSupervisorIds(),
                                    getString(R.string.bullet_push_message,
                                            ParseUser.getCurrentUser().getUsername()));
                        } else {
                            Toast.makeText(MessageActivity.this, "Problem sending message: " +
                                    e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    protected ParseObject createMessage() {

        String title;
        String action;
        String result;
        String impact;
        String pass = ParseUser.getCurrentUser().getObjectId();
        DateHelper today = new DateHelper();
        String date = today.DateChangerThreeCharMonth();

        // prevents null object reference
        if (mActionText.getText().toString().equals("")){
            action = "empty";
        }else{
            action = mActionText.getText().toString();
        }

        if (mResultText.getText().toString().equals("")){
            result = "empty";
        }else{
            result = mResultText.getText().toString();
        }

        if (mImpactText.getText().toString().equals("")){
            impact = "empty";
        }else{
            impact = mImpactText.getText().toString();
        }

        if (mTitleText.getText().toString().equals("")){
            title = "empty";
        }else{
            title = mTitleText.getText().toString();
        }

        String encryptedAction = encryptThis(pass, action);
        String encryptedResult = encryptThis(pass, result);
        String encryptedImpact = encryptThis(pass, impact);

        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME,
                ParseUser.getCurrentUser().get(ParseConstants.KEY_DISPLAY_NAME));
        message.put(ParseConstants.KEY_SUPERVISOR_ID, getSupervisorIds());
        message.put(ParseConstants.KEY_BULLET_TITLE, title);
        message.put(ParseConstants.KEY_ACTION, encryptedAction);
        message.put(ParseConstants.KEY_RESULT, encryptedResult);
        message.put(ParseConstants.KEY_IMPACT, encryptedImpact);
        message.put(ParseConstants.KEY_CREATED_ON, date);
        message.put(ParseConstants.KEY_VIEWED, false);
        message.put(ParseConstants.KEY_BEEN_SENT, false);
        message.put(ParseConstants.KEY_REQUEST_TYPE, "empty");
        message.put(ParseConstants.KEY_MESSAGE_TYPE, "bullet");

            return message;
    }

    protected String getSupervisorIds() {
        String supervisorID = ParseUser.getCurrentUser().
                getString(ParseConstants.KEY_SUPERVISOR_ID);

        return supervisorID;
    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("C4A083AE232F9FB04BFA58FBF0E57A0A")
                .addTestDevice("ECE8D8561B65128D4A9A8080E2C6A51C")
                .build();

        mInterstitialAd.loadAd(adRequest);
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

    @Override
    public void onResume() {
        super.onResume();

    }

    private String encryptThis(String pass, String data) {
        String encryptedData = "";
        try {
            Crypto crypto = new Crypto(pass);
            encryptedData = crypto.encrypt(data);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return encryptedData;
    }

}
