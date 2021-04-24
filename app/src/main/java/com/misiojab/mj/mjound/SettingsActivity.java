package com.misiojab.mj.mjound;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

public class SettingsActivity extends Activity {

    Switch enableSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupEnableSwitch();
        setupReturnButton();
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveSettings();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void setupReturnButton(){
        ImageButton returnButton = findViewById(R.id.returnButton);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateUpTo(new Intent(SettingsActivity.this, MainActivity.class));

            }
        });
    }

    private void setupEnableSwitch (){
        enableSwitch = findViewById(R.id.switchEnable);

        enableSwitch.setChecked(SavedData.readBool(SavedData.ENABLED, this));

        enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Log.e("Enable Mjound", String.valueOf(isChecked));
                    SavedData.saveSetting(SavedData.ENABLED, isChecked, getApplicationContext());
                    Log.e("Enable Mjound w save", String.valueOf(SavedData.readBool(SavedData.ENABLED, getApplicationContext())));

                    getApplicationContext().startService(new Intent(getApplicationContext(), BackgroundService.class));

                } else if (!isChecked) {
                    Log.e("Enable Mjound", String.valueOf(isChecked));
                    SavedData.saveSetting(SavedData.ENABLED, isChecked, getApplicationContext());
                    Log.e("Enable Mjound w save", String.valueOf(SavedData.readBool(SavedData.ENABLED, getApplicationContext())));

                    getApplicationContext().stopService(new Intent(getApplicationContext(), BackgroundService.class));
                }
            }
        });
    }

    public void saveSettings() {

    }

    public void stopMediaPlayer(MediaPlayer mediaPlayer){
        mediaPlayer.stop();
    }
}