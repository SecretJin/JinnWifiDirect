package com.example.acer.jinwifidirect.view;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.acer.jinwifidirect.R;

public class GuideActivity extends Activity {

    HostBroadcastReceiver receiver = new HostBroadcastReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.guide, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editBtn:
                Intent a = new Intent(GuideActivity.this, EditProfileActivity.class);
                startActivity(a);
                break;
            case R.id.homeBtn:
                Intent b = new Intent(GuideActivity.this, MenuSelectionActivity.class);
                startActivity(b);
                break;
            case R.id.friendBtn:
                Intent c = new Intent(GuideActivity.this, ProfileListActivity.class);
                startActivity(c);
                break;
            case R.id.profileBtn:
                Intent d = new Intent(GuideActivity.this, ProfileActivity.class);
                startActivity(d);
                break;
            case R.id.guideBtn:
                Intent e = new Intent(GuideActivity.this, GuideActivity.class);
                startActivity(e);
                break;

        }
        return true;
    }

    public void backButton(View v)
    {
        Intent i = new Intent(GuideActivity.this, MenuSelectionActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {}


}
