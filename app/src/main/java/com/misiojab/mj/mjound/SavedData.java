package com.misiojab.mj.mjound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.SeekBar;

public class SavedData {
    public static final String APP = "com.misiojab.mj.mjound";
    public static final String BASS_VALUE_KEY = "com.misiojab.mj.mjound.bassvalue";
    public static final String LOUD_VALUE_KEY = "com.misiojab.mj.mjound.loudvalue";
    public static final String VIRTUALIZER_VALUE_KEY = "com.misiojab.mj.mjound.virtualizervalue";

    public static final String ARTIST = "com.misiojab.mj.mjound.artist";
    public static final String SONG = "com.misiojab.mj.mjound.song";
    public static final String ALBUM = "com.misiojab.mj.mjound.album";
    public static final String GENRE =  "com.misiojab.mj.mjound.genre";

    public static final String EQUALIZERVALUES = "com.misiojab.mj.mjound.equalizervalue";

    public static final String ID = "com.misiojab.mj.mjound.id";

    public static final String ENABLED = "com.misiojab.mj.mjound.enabled";
    public static final String AUTO_ENABLED = "com.misiojab.mj.mjound.autoenabled";

    public static final String SELECTED_PRESET = "com.misiojab.mj.mjound.selected_preset";
    public static final String SELECTED_PRESET_NUM_KEY = "com.misiojab.mj.mjound.loudvaluepreset";


    public static int readInt(String KEY, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(
                APP, Context.MODE_PRIVATE);

        return prefs.getInt(KEY, 0);
    }

    public static String readString(String KEY, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(
                APP, Context.MODE_PRIVATE);

        return prefs.getString(KEY, "");
    }

    public static boolean readBool(String KEY, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(
                APP, Context.MODE_PRIVATE);

        return prefs.getBoolean(KEY, false);
    }


    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public static void saveSetting(String KEY, int value, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(
                APP, Context.MODE_PRIVATE);

        prefs.edit().putInt(KEY, value).apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public static void saveSetting(String KEY, String value, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences (
                APP, Context.MODE_PRIVATE);

        prefs.edit().putString(KEY, value).apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public static void saveSetting(String KEY, boolean value, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(
                APP, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY, value).apply();
    }

}