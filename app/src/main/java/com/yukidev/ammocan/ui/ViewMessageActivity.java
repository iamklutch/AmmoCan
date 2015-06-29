package com.yukidev.ammocan.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yukidev.ammocan.R;
import com.yukidev.ammocan.utils.DateHelper;
import com.yukidev.ammocan.utils.ExceptionHandler;
import com.yukidev.ammocan.utils.ParseConstants;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ViewMessageActivity extends ActionBarActivity {

    @InjectView(R.id.titleTextView)TextView mTitleText;
    @InjectView(R.id.actionTextView)TextView mActionText;
    @InjectView(R.id.resultTextView)TextView mResultText;
    @InjectView(R.id.impactTextView)TextView mImpactText;
    @InjectView(R.id.createdOnTextView)TextView mCreatedOn;
    private ParseObject mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        setContentView(R.layout.activity_view_message);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        String mMessageId = intent.getStringExtra(ParseConstants.KEY_OBJECT_ID);

        mActionText.setMovementMethod(new ScrollingMovementMethod());
        mResultText.setMovementMethod(new ScrollingMovementMethod());
        mImpactText.setMovementMethod(new ScrollingMovementMethod());

        try {

            mMessage = getMessage(mMessageId);

            String messageTitle = mMessage.getString(ParseConstants.KEY_BULLET_TITLE);
            String messageAction = mMessage.getString(ParseConstants.KEY_ACTION);
            String messageResult = mMessage.getString(ParseConstants.KEY_RESULT);
            String messageImpact = mMessage.getString(ParseConstants.KEY_IMPACT);
            String messageCreated = mMessage.getString(ParseConstants.KEY_CREATED_ON);

            mTitleText.setText(messageTitle);
            mActionText.setText(messageAction);
            mResultText.setText(messageResult);
            mImpactText.setText(messageImpact);
            mCreatedOn.setText(messageCreated);

        }catch (ParseException e) {
            Toast.makeText(ViewMessageActivity.this,
                    "Problem retrieveing message: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private ParseObject getMessage (String objectId) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_MESSAGES);
        ParseObject message = query.get(objectId);
        return message;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete:
                mMessage.deleteInBackground();
                Toast.makeText(this, "Bullet deleted", Toast.LENGTH_LONG).show();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
