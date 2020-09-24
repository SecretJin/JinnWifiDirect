package com.example.acer.jinwifidirect.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.acer.jinwifidirect.R;
import com.example.acer.jinwifidirect.repository.ProfileUtil;

import java.io.ByteArrayOutputStream;

public class EditProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.guideBtn:
                Intent i = new Intent(EditProfileActivity.this, GuideActivity.class);
                startActivity(i);
                break;
            case R.id.homeBtn:
                Intent b = new Intent(EditProfileActivity.this, MenuSelectionActivity.class);
                startActivity(b);
                break;
            case R.id.friendBtn:
                Intent c = new Intent(EditProfileActivity.this, ProfileListActivity.class);
                startActivity(c);
                break;
            case R.id.profileBtn:
                Intent d = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(d);
                break;
            case R.id.editBtn:
                Intent a = new Intent(EditProfileActivity.this, EditProfileActivity.class);
                startActivity(a);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {}

    @Override
    public void onStart()
    {
        super.onStart();
        setName();
        setEmail();
        setGender();
        setDescr();
    }

    public void setName()
    {
        EditText messageET = (EditText)findViewById(R.id.nameEditText);
        messageET.setText(ProfileUtil.getInstance().getName(), TextView.BufferType.EDITABLE);
    }

    public void setEmail()
    {
        EditText messageET = (EditText)findViewById(R.id.emailEditText);
        messageET.setText(ProfileUtil.getInstance().getEmail(), TextView.BufferType.EDITABLE);
    }

    public void setGender()
    {
        EditText messageET = (EditText)findViewById(R.id.genderEditText);
        messageET.setText(ProfileUtil.getInstance().getGender(), TextView.BufferType.EDITABLE);
    }

    public void setDescr()
    {
        EditText messageET = (EditText)findViewById(R.id.descEditText);
        messageET.setText(ProfileUtil.getInstance().getDescription(), TextView.BufferType.EDITABLE);
    }

    public void confirmUpdate(View v)
    {
        EditText messageName = (EditText)findViewById(R.id.nameEditText);
        String message = messageName.getText().toString();
        if(message.matches(""))
        {
            ProfileUtil.getInstance().setName("Anonymous");
        }
        else
        {
            ProfileUtil.getInstance().setName((messageName.getText().toString()));
        }

        EditText messageGender = (EditText)findViewById(R.id.genderEditText);
        ProfileUtil.getInstance().setGender((messageGender.getText().toString()));

        EditText messageEmail = (EditText)findViewById(R.id.emailEditText);
        ProfileUtil.getInstance().setEmail((messageEmail.getText().toString()));

        EditText messageDescr = (EditText)findViewById(R.id.descEditText);
        ProfileUtil.getInstance().setDescription((messageDescr.getText().toString()));

        Intent i = new Intent(EditProfileActivity.this, ProfileActivity.class);
        startActivity(i);
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    /*
    public void uploadPicture(View v)
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,PICK_IMAGE);
    }*/
    /*

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode==RESULT_OK && data!=null)
        {
            Uri selectedImage = data.getData();
            image.setImageURI(selectedImage);

        }
    }
    */
}
