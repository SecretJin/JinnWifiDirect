package com.example.acer.jinwifidirect.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acer.jinwifidirect.R;
import com.example.acer.jinwifidirect.repository.ProfileUtil;

public class ProfileActivity extends Activity {

    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.guideBtn:
                Intent i = new Intent(ProfileActivity.this, GuideActivity.class);
                startActivity(i);
                break;
            case R.id.editBtn:
                Intent a = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(a);
                break;
            case R.id.homeBtn:
                Intent b = new Intent(ProfileActivity.this, MenuSelectionActivity.class);
                startActivity(b);
                break;
            case R.id.friendBtn:
                Intent c = new Intent(ProfileActivity.this, ProfileListActivity.class);
                startActivity(c);
                break;
            case R.id.profileBtn:
                Intent d = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(d);
                break;
        }
        return true;
    }
    @Override
    public void onStart()
    {
        super.onStart();
        setName();
        setDescr();
        setEmail();
        setGender();
    }

    public void backMenu(View v)
    {
        Intent i = new Intent(ProfileActivity.this, MenuSelectionActivity.class);
        startActivity(i);
    }

    public void setName()
    {
        TextView messageTV = (TextView)findViewById(R.id.nameTextView);
        messageTV.setText(ProfileUtil.getInstance().getName());
    }

    public void setEmail()
    {
        TextView messageTV = (TextView)findViewById(R.id.emailTextView);
        messageTV.setText(ProfileUtil.getInstance().getEmail());

    }

    public void setGender()
    {
        TextView messageTV = (TextView)findViewById(R.id.genderTextView);
        messageTV.setText(ProfileUtil.getInstance().getGender());
    }

    public void setDescr()
    {
        TextView messageTV = (TextView)findViewById(R.id.descTextView);
        messageTV.setText(ProfileUtil.getInstance().getDescription());
    }

    @Override
    public void onBackPressed() {}

}
