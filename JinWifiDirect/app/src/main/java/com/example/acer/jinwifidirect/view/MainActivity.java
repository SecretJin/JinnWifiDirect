package com.example.acer.jinwifidirect.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.example.acer.jinwifidirect.R;
import com.example.acer.jinwifidirect.repository.ProfileUtil;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        Thread loadingThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(3000);
                } catch (Exception e) {

                } finally {
                    ProfileUtil.getInstance().setName("Anonymous");
                    ProfileUtil.getInstance().setEmail("anonymous@email.com");
                    ProfileUtil.getInstance().setDescription("Hello, I am anonymous");
                    ProfileUtil.getInstance().setGender("Unknown");
                    Intent i = new Intent(MainActivity.this, MenuSelectionActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        loadingThread.start();

    }

    @Override
    public void onBackPressed() {}

}
