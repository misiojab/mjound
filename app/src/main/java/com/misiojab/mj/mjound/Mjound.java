package com.misiojab.mj.mjound;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class Mjound extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, BackgroundService.class));


    }


}
