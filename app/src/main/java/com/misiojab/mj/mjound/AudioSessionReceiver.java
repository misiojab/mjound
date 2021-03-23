package com.misiojab.mj.mjound;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.Virtualizer;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class AudioSessionReceiver extends BroadcastReceiver {
    public int id;

    public MediaPlayer mMediaPlayer;

    public Equalizer mEqualizer;

    public BassBoost bassBoost;

    public Virtualizer virtualizer;

    public LoudnessEnhancer loudnessEnhancer;

    @Override
    public void onReceive(Context context, Intent intent) {
        id = intent.getIntExtra(Equalizer.EXTRA_AUDIO_SESSION, -1);
        String packageName = intent.getStringExtra(Equalizer.EXTRA_PACKAGE_NAME);
        Toast.makeText(context, id, Toast.LENGTH_LONG).show();
        Log.e("ASR: ", "onReceive: " + id);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setAudioSessionId(id);

        mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);

        mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);


        bassBoost = new BassBoost(0, mMediaPlayer.getAudioSessionId());
        bassBoost.setEnabled(true);

        virtualizer = new Virtualizer(0,mMediaPlayer.getAudioSessionId());
        virtualizer.setEnabled(true);

        loudnessEnhancer = new LoudnessEnhancer(mMediaPlayer.getAudioSessionId());
        loudnessEnhancer.setEnabled(true);
    }


}
