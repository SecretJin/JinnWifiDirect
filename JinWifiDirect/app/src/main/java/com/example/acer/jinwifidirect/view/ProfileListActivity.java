package com.example.acer.jinwifidirect.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.acer.jinwifidirect.R;
import com.example.acer.jinwifidirect.repository.ProfileUtil;
import java.util.ArrayList;

public class ProfileListActivity extends Activity {
    private ListView listView;
    private ProfileListAdapter adapter;
    private static ArrayList<ProfileUtil> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_list);
        listView = (ListView)findViewById(R.id.friendList);
        list = new ArrayList<ProfileUtil>();
        list= ProfileUtil.getInstance().getList();
        adapter = new ProfileListAdapter(list, this);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editBtn:
                Intent a = new Intent(ProfileListActivity.this, EditProfileActivity.class);
                startActivity(a);
                break;
            case R.id.homeBtn:
                Intent b = new Intent(ProfileListActivity.this, MenuSelectionActivity.class);
                startActivity(b);
                break;
            case R.id.friendBtn:
                Intent c = new Intent(ProfileListActivity.this, ProfileListActivity.class);
                startActivity(c);
                break;
            case R.id.profileBtn:
                Intent d = new Intent(ProfileListActivity.this, ProfileActivity.class);
                startActivity(d);
                break;
            case R.id.guideBtn:
                Intent e = new Intent(ProfileListActivity.this, GuideActivity.class);
                startActivity(e);
                break;
        }
        return true;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        list= ProfileUtil.getInstance().getList();
        adapter = new ProfileListAdapter(list, this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {}
}
