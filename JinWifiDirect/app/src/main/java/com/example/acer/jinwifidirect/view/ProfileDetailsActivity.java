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

public class ProfileDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.guideBtn:
                Intent i = new Intent(ProfileDetailsActivity.this, GuideActivity.class);
                startActivity(i);
                break;
            case R.id.editBtn:
                Intent a = new Intent(ProfileDetailsActivity.this, EditProfileActivity.class);
                startActivity(a);
                break;
            case R.id.homeBtn:
                Intent b = new Intent(ProfileDetailsActivity.this, MenuSelectionActivity.class);
                startActivity(b);
                break;
            case R.id.friendBtn:
                Intent c = new Intent(ProfileDetailsActivity.this, ProfileListActivity.class);
                startActivity(c);
                break;
            case R.id.profileBtn:
                Intent d = new Intent(ProfileDetailsActivity.this, ProfileActivity.class);
                startActivity(d);
                break;
        }
        return true;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        setProfile();
    }

    public void setProfile()
    {
        Intent intent = getIntent();
        ProfileUtil profile = (ProfileUtil) intent.getExtras().getSerializable("MyClass");
        TextView messageN = (TextView)findViewById(R.id.profileName);
        messageN.setText(profile.getName());
        TextView messageE = (TextView)findViewById(R.id.profileEmail);
        messageE.setText(profile.getEmail());
        TextView messageG = (TextView)findViewById(R.id.profileGender);
        messageG.setText(profile.getGender());
        TextView messageD = (TextView)findViewById(R.id.profileDescription);
        messageD.setText(profile.getDescription());
        TextView messageUN = (TextView)findViewById(R.id.userProfile);
        messageUN.setText(profile.getName()+"'s Profile");

    }

    public void backBtn(View v)
    {
        Intent i = new Intent(ProfileDetailsActivity.this, ProfileListActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {}
}
