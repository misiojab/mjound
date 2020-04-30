package com.misiojab.mj.mjound;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import static com.misiojab.mj.mjound.BackgroundService.NOTIF_ID;
import static com.misiojab.mj.mjound.SavedData.SELECTED_PRESET;
import static java.lang.Integer.valueOf;

public class MainActivity extends Activity {

    private static final float VISUALIZER_HEIGHT_DIP = 50f;


    private MediaPlayer mMediaPlayer;

    private Equalizer mEqualizer;

    private ImageButton rButton;

    public BassBoost bassBoost;
    private short bassValue;
    public SeekBar bassSeeker;

    public LoudnessEnhancer loudnessEnhancer;
    public SeekBar loudBar;
    private short loudValue;

    public Spinner equalizerPresetSpinner;
    public int selected_preset_num;
    public String selected_preset = "none";


    private LinearLayout mLinearLayout;
    private int id = 0;

    private VisualizerView mVisualizerView;
    private Visualizer mVisualizer;




//    private TextView mStatusTextView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        set the device's volume control to control the audio stream we'll be playing
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Create the MediaPlayer
        //you need to put your audio file in the res/raw folder
        //- the filename must be test_audio_file or
        //change it below to match your filename

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setAudioSessionId(id);

/**
        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        mVisualizer.setEnabled(true);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
//                disable the visualizer as it's no longer needed
                mVisualizer.setEnabled(false);
            }
        });

*/
//        create the equalizer with default priority of 0 & attach to our media player
        mEqualizer = new Equalizer(5, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);

        bassBoost = new BassBoost(5, mMediaPlayer.getAudioSessionId());
        bassBoost.setEnabled(true);

        loudnessEnhancer = new LoudnessEnhancer(mMediaPlayer.getAudioSessionId());
        loudnessEnhancer.setEnabled(true);


//        set up visualizer and equalizer bars

//        setupVisualizerFxAndUI();

        setupEqualizerFxAndUI();
        setupBassBoost();
        setupLoudnessEnhancer();

        // enable the visualizer


        // listen for when the music stream ends playing

        rButton = (ImageButton) findViewById(R.id.OnButton);
        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rButton.setEnabled(true);

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
                android.R.layout.simple_spinner_item,
                equalizerPresetNames);
        equalizerPresetSpinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

    private void setupBassBoost(){

        bassSeeker = (SeekBar) findViewById(R.id.BassSeeker);
        bassSeeker.setMax(1000);
        bassSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                short value = (short) progress;
                bassValue = value;
                bassBoost.setStrength(value);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setupLoudnessEnhancer(){

        loudBar = (SeekBar) findViewById(R.id.LoudBar);
        loudBar.setMax(1500);
        loudBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                loudValue = (short) progress;
                loudnessEnhancer.setTargetGain(progress);
            }


            public void onStartTrackingTouch(SeekBar seekBar) {

            }


            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



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
            seekBar.setPadding( 0, 36, 0, 36);

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

    /*displays the audio waveform*/
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void setupVisualizerFxAndUI() {
        mLinearLayout = findViewById(R.id.linearLayoutVisual);
        // Create a VisualizerView to display the audio waveform for the current settings
        mVisualizerView = new VisualizerView(this);
        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));
        mLinearLayout.addView(mVisualizerView);

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                mVisualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
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

        bassSeeker.setProgress(SavedData.readInt(SavedData.BASS_VALUE_KEY, this));
        loudBar.setProgress(SavedData.readInt(SavedData.LOUD_VALUE_KEY, this));
        equalizerPresetSpinner.setSelection(SavedData.readInt(SavedData.SELECTED_PRESET_NUM_KEY, this));


    }

    private void saveSettings(){
        SavedData.saveSetting(SavedData.BASS_VALUE_KEY, bassValue, this);
        SavedData.saveSetting(SavedData.LOUD_VALUE_KEY, loudValue, this);
        SavedData.saveSetting(SavedData.SELECTED_PRESET_NUM_KEY, selected_preset_num, this);
        SavedData.saveSetting(SavedData.SELECTED_PRESET, selected_preset, this);


    }

}

