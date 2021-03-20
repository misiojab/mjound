package com.misiojab.mj.mjound;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaDataSource;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.Virtualizer;
import android.media.audiofx.Visualizer;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.service.notification.NotificationListenerService;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.audiovisualizer.visualizer.WaveVisualizer;

import java.util.ArrayList;
import java.util.List;

import me.tankery.lib.circularseekbar.CircularSeekBar;


public class MainActivity extends Activity{

    private MediaPlayer mMediaPlayer;

    private Equalizer mEqualizer;

    public BassBoost bassBoost;
    private short bassValue;

    public CircularSeekBar bassCircular;
    public TextView textBass;

    public Virtualizer virtualizer;
    private short virtualizerValue;
    public CircularSeekBar virtualizerSeeker;

    public LoudnessEnhancer loudnessEnhancer;
    public SeekBar loudBar;
    private short loudValue;
    public TextView textLoud;

    public Spinner equalizerPresetSpinner;
    public int selected_preset_num;
    public String selected_preset = "none";

    private LinearLayout mLinearLayout;
    private int id = 0;

    private int priority = 0; //było 5

    private WaveVisualizer waveVisualizer;

    ConstraintLayout hiddentLayout;
    FloatingActionButton fab;
    CardView cardView;

    public TextView titleText;
    public TextView genreText;
    public TextView artistText;
    public TextView status = null;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
        }

//        set the device's volume control to control the audio stream we'll be playing
        //setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // Create the MediaPlayer

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setAudioSessionId(id);

//        create the equalizer with default priority of 0 & attach to our media player
        mEqualizer = new Equalizer(priority, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);

        bassBoost = new BassBoost(priority, mMediaPlayer.getAudioSessionId());
        bassBoost.setEnabled(true);

        virtualizer = new Virtualizer(priority, mMediaPlayer.getAudioSessionId());
        virtualizer.setEnabled(true);

        loudnessEnhancer = new LoudnessEnhancer(mMediaPlayer.getAudioSessionId());
        loudnessEnhancer.setEnabled(true);

//        set up visualizer and equalizer bars

        setupEqualizerFxAndUI();

        setupLoudnessEnhancer();

        setupBassCircular();

        setupVirtualizer();

        setupSettingsButton();

        setupFab();

        setupVisualizer(mMediaPlayer.getAudioSessionId());

        setupAutomaticFit();

        startService(new Intent(this, NotificationListener.class));
    }

    private void setupFab() {
        fab = findViewById(R.id.floatingActionButton);
        hiddentLayout = findViewById(R.id.hiddenLayout);
        cardView = findViewById(R.id.cardView);

        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                Log.e("Przycisk: ", "pressed");

                if (hiddentLayout.getVisibility() == View.VISIBLE) {

                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    hiddentLayout.setVisibility(View.GONE);
                    fab.setBackgroundColor(R.color.colorAccent);
                } else {

                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    hiddentLayout.setVisibility(View.VISIBLE);
                    fab.setBackgroundColor(R.color.colorPrimaryDark);
                }
            }
        });
    }

    /* shows spinner with list of equalizer presets to choose from
 - updates the seekBar progress and gain levels according
 to those of the selected preset*/
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void equalizeSound() {
//        set up the spinner
        final ArrayList<String> equalizerPresetNames = new ArrayList<String>();
        ArrayAdapter<String> equalizerPresetSpinnerAdapter
                = new ArrayAdapter<String>(this,
                R.layout.spinner_item,
                equalizerPresetNames);
        equalizerPresetSpinnerAdapter
                .setDropDownViewResource(R.layout.spinner_dropdown_item);
        equalizerPresetSpinner = (Spinner) findViewById(R.id.spinner);
//        get list of the device's equalizer presets
        for (short i = 0; i < mEqualizer.getNumberOfPresets(); i++) {
            equalizerPresetNames.add(mEqualizer.getPresetName(i));
        }
        equalizerPresetSpinner.setAdapter(equalizerPresetSpinnerAdapter);

//        handle the spinner item selections
        equalizerPresetSpinner.setOnItemSelectedListener(new AdapterView
                .OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                //first list item selected by default and sets the preset accordingly
                mEqualizer.usePreset((short) position);
                selected_preset = equalizerPresetNames.get(position);
                selected_preset_num = position;
//                get the number of frequency bands for this equalizer engine
                short numberFrequencyBands = mEqualizer.getNumberOfBands();
//                get the lower gain setting for this equalizer band
                final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];

//                set seekBar indicators according to selected preset
                for (short i = 0; i < numberFrequencyBands; i++) {
                    short equalizerBandIndex = i;



                    SeekBar seekBar = (SeekBar) findViewById(equalizerBandIndex);
//                    get current gain setting for this equalizer band
//                    set the progress indicator of this seekBar to indicate the current gain value
                    seekBar.setProgress(mEqualizer
                            .getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel);
                }
                saveSettings();
                BackgroundService.updateNotification(getApplicationContext());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                not used
            }
        });
    }

    private void setupBassCircular(){
        bassCircular = (CircularSeekBar) findViewById(R.id.BassCircular);
        bassCircular.setMax(1000);

        textBass = (TextView) findViewById(R.id.BassLevel);

        bassCircular.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                short value = (short) progress;
                bassValue = value;
                bassBoost.setStrength(value);

                textBass.setText("+" + value/100);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });
    }

    private void setupVirtualizer(){
        virtualizerSeeker = (CircularSeekBar) findViewById(R.id.circularVirtualizer);
        virtualizerSeeker.setMax(1000);

        virtualizerSeeker.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                short value = (short) progress;
                virtualizerValue = value;
                virtualizer.setStrength(value);
            }

            

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });
    }

    private void setupLoudnessEnhancer(){

        loudBar = (SeekBar) findViewById(R.id.LoudBar);
        loudBar.setMax(1000);

        textLoud = (TextView) findViewById(R.id.volumeLevel);

        loudBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                short value = (short) progress;
                loudValue = value;
                loudnessEnhancer.setTargetGain(value);

                textLoud.setText("+" + value/100);
            }


            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void setupSettingsButton(){

        ImageButton settingsButton = (ImageButton) findViewById(R.id.SettingsButton);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
    }

    private void setupVisualizer(int audioSessionId){
        waveVisualizer = findViewById(R.id.wave);

        if( audioSessionId != -1){
            waveVisualizer.setAudioSessionId(audioSessionId);
        }
    }

    // work in progress
    private void setupAutomaticFit(){


        artistText = (TextView) findViewById(R.id.artistText);
        genreText = (TextView) findViewById(R.id.genreText);
        titleText = (TextView) findViewById(R.id.titleText);

        artistText.setText(SavedData.readString(SavedData.ARTIST, this));
        genreText.setText(SavedData.readString(SavedData.GENRE, this));
        titleText.setText(SavedData.readString(SavedData.SONG, this));
    }



    /* displays the SeekBar sliders for the supported equalizer frequency bands
     user can move sliders to change the frequency of the bands*/
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void setupEqualizerFxAndUI() {

//        get reference to linear layout for the seekBars
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutEqual);

//        equalizer heading
        /*
        TextView equalizerHeading = new TextView(this);
        equalizerHeading.setText("Equalizer");
        equalizerHeading.setTextSize(20);
        equalizerHeading.setGravity(Gravity.CENTER_HORIZONTAL);
        mLinearLayout.addView(equalizerHeading);
        */


//        get number frequency bands supported by the equalizer engine
        short numberFrequencyBands = mEqualizer.getNumberOfBands();

//        get the level ranges to be used in setting the band level
//        get lower limit of the range in milliBels
        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
//        get the upper limit of the range in millibels
        final short upperEqualizerBandLevel = mEqualizer.getBandLevelRange()[1];

//        loop through all the equalizer bands to display the band headings, lower
//        & upper levels and the seek bars
        for (short i = 0; i < numberFrequencyBands; i++) {
            final short equalizerBandIndex = i;

//            frequency header for each seekBar
            /*
            TextView frequencyHeaderTextview = new TextView(this);
            frequencyHeaderTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            frequencyHeaderTextview.setGravity(Gravity.CENTER_HORIZONTAL);
            frequencyHeaderTextview
                    .setText((mEqualizer.getCenterFreq(equalizerBandIndex) / 1000) + " Hz");
            mLinearLayout.addView(frequencyHeaderTextview);
            */

//            set up linear layout to contain each seekBar
            LinearLayout seekBarRowLayout = new LinearLayout(this);
            seekBarRowLayout.setOrientation(LinearLayout.HORIZONTAL);


//            set up lower level textview for this seekBar
            /*
            TextView lowerEqualizerBandLevelTextview = new TextView(this);
            lowerEqualizerBandLevelTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            lowerEqualizerBandLevelTextview.setText((lowerEqualizerBandLevel / 100) + " dB");
//            set up upper level textview for this seekBar
            TextView upperEqualizerBandLevelTextview = new TextView(this);
            upperEqualizerBandLevelTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            upperEqualizerBandLevelTextview.setText((upperEqualizerBandLevel / 100) + " dB");
            */

            //            **********  the seekBar  **************
//            set the layout parameters for the seekbar
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 0;

//            create a new seekBar
            SeekBar seekBar = new SeekBar(this);
//            give the seekBar an ID

            seekBar.setId(i);
            seekBar.setPadding( 0, 40, 0, 40);


            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);
//            set the progress for this seekBar
            seekBar.setProgress(mEqualizer.getBandLevel(equalizerBandIndex));


//            change progress as its changed by moving the sliders
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    mEqualizer.setBandLevel(equalizerBandIndex,
                            (short) (progress + lowerEqualizerBandLevel));
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    //not used
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    //not used
                }
            });

//            add the lower and upper band level textviews and the seekBar to the row layout
            //seekBarRowLayout.addView(lowerEqualizerBandLevelTextview);
            seekBarRowLayout.addView(seekBar);
            //seekBarRowLayout.addView(upperEqualizerBandLevelTextview);

            mLinearLayout.addView(seekBarRowLayout);

            //        show the spinner

            equalizeSound();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onPause() {
        super.onPause();

        saveSettings();

        if (isFinishing() && mMediaPlayer != null) {

            mEqualizer.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    protected  void onResume() {
        super.onResume();

        loudBar.setProgress(SavedData.readInt(SavedData.LOUD_VALUE_KEY, this));
        equalizerPresetSpinner.setSelection(SavedData.readInt(SavedData.SELECTED_PRESET_NUM_KEY, this));
        virtualizerSeeker.setProgress(SavedData.readInt(SavedData.VIRTUALIZER_VALUE_KEY, this));
        bassCircular.setProgress(SavedData.readInt(SavedData.BASS_VALUE_KEY, this));

        artistText.setText(SavedData.readString(SavedData.ARTIST, this));
        genreText.setText(SavedData.readString(SavedData.GENRE, this));
        titleText.setText(SavedData.readString(SavedData.SONG, this));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(waveVisualizer != null){
            waveVisualizer.release();
        }
    }

    private void saveSettings(){

        SavedData.saveSetting(SavedData.BASS_VALUE_KEY, bassValue, this);
        SavedData.saveSetting(SavedData.LOUD_VALUE_KEY, loudValue, this);
        SavedData.saveSetting(SavedData.SELECTED_PRESET_NUM_KEY, selected_preset_num, this);
        SavedData.saveSetting(SavedData.SELECTED_PRESET, selected_preset, this);

        SavedData.saveSetting(SavedData.VIRTUALIZER_VALUE_KEY, virtualizerValue, this);
    }

}

