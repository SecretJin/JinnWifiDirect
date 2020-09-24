package com.example.acer.jinwifidirect.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.acer.jinwifidirect.R;
import com.example.acer.jinwifidirect.repository.ProfileUtil;

public class MenuSelectionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_selection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_selection, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.guideBtn:
                Intent i = new Intent(MenuSelectionActivity.this, GuideActivity.class);
                startActivity(i);
                break;
            case R.id.editBtn:
                Intent a = new Intent(MenuSelectionActivity.this, EditProfileActivity.class);
                startActivity(a);
                break;
            case R.id.friendBtn:
                Intent c = new Intent(MenuSelectionActivity.this, ProfileListActivity.class);
                startActivity(c);
                break;
            case R.id.profileBtn:
                Intent d = new Intent(MenuSelectionActivity.this, ProfileActivity.class);
                startActivity(d);
                break;
            case R.id.homeBtn:
                Intent b = new Intent(MenuSelectionActivity.this, MenuSelectionActivity.class);
                startActivity(b);
                break;
        }
        return true;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        updateName();
    }

    public void updateName()
    {
        TextView messageTV = (TextView)findViewById(R.id.userName);
        if(ProfileUtil.getInstance().getName()!=null) {
            messageTV.setText("Welcome " + ProfileUtil.getInstance().getName());
        }
        else
        {
            messageTV.setText("Welcome " + "Anonymous");
        }
    }

    public void createRoom (View v)
    {
        Intent i = new Intent(MenuSelectionActivity.this, HostActivity.class);
        startActivity(i);
    }

    public void joinRoom (View v)
    {
        Intent i = new Intent(MenuSelectionActivity.this, ClientActivity.class);
        startActivity(i);
    }

    public void profileDetail (View v)
    {
        Intent i = new Intent(MenuSelectionActivity.this, ProfileActivity.class);
        startActivity(i);
    }

    public void profileListBtn(View v)
    {
        Intent i = new Intent(MenuSelectionActivity.this, ProfileListActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {}
}
