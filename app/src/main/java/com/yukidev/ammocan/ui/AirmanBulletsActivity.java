package com.yukidev.ammocan.ui;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yukidev.ammocan.R;
import com.yukidev.ammocan.adapters.MessageAdapter;
import com.yukidev.ammocan.utils.ParseConstants;

import java.util.ConcurrentModificationException;
import java.util.List;


public class AirmanBulletsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airman_bullets);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new AirmanBulletsFragment())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_airman_bullets, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_download:
                AlertDialog.Builder builder = new AlertDialog.Builder(AirmanBulletsActivity.this);
                builder.setTitle(getString(R.string.download_bullets_title));
                builder.setMessage(getString(R.string.download_bullets_message));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AirmanBulletsActivity.this,
                                AirmanBulletsActivity.class);
                        intent.putExtra("objectId", ParseUser.getCurrentUser().getObjectId());
                        intent.putExtra("download", true);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("CANCEL", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
