package com.misiojab.mj.mjound;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Mjound extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, AudioSessionReceiver.class));

        AudioSessionReceiver audioSessionReceiver = new AudioSessionReceiver();
        Log.e("mjound ", "onCreate: siema siema " + audioSessionReceiver.id);

        SavedData.saveSetting(SavedData.ID, audioSessionReceiver.id, this);

        startService(new Intent(this, BackgroundService.class));


    }



}
