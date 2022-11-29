package com.misiojab.mj.mjound;

import static android.graphics.Color.LTGRAY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.usage.UsageEvents;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.Virtualizer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.WaveVisualizer;

import java.util.ArrayList;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class MainActivity extends Activity{

    public MediaPlayer mMediaPlayer;

    public Equalizer mEqualizer;

    public BassBoost bassBoost;
    private short bassValue;

    public CircularSeekBar bassCircular;
    public TextView textBass;

    public TextView textVirtualizerTitle;
    public TextView textVirtualizer;

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
    private int id = -1;

    private int priority = 0; //było 5

    private WaveVisualizer waveVisualizer;
    BezierView bezierView;

    ConstraintLayout hiddenLayout;
    FloatingActionButton fab;
    CardView cardView;

    ConstraintLayout disabledLayout;

    ConstraintLayout cs;

    public TextView titleText;
    public TextView genreText;
    public TextView artistText;
    public TextView albumText;
    public TextView status = null;

    public int[] seekBarProgress;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cs = findViewById(R.id.cs);

        disabledLayout = findViewById(R.id.disabledLayout);
        Button b = (Button) findViewById(R.id.goToSettings);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(i);
            }
        });

        if (SavedData.readBool(SavedData.ENABLED, this)){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
            }

            disabledLayout.setVisibility(View.GONE);

            setupEqualizerCore();

            bezierView = new BezierView(this, null);
            cs.addView(bezierView);

        } else {
            disabledLayout.setVisibility(View.VISIBLE);
        }

        setupSettingsButton();

        setupFab();

        if (SavedData.readBool(SavedData.ENABLED, this)){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
            }

        } else {

            disabledLayout.setVisibility(View.VISIBLE);
        }

//        set the device's volume control to control the audio stream we'll be playing
        //setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // Create the MediaPlayer
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setupEqualizerCore() {

        id = SavedData.readInt(SavedData.ID, this);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setAudioSessionId(id);

//        create the equalizer with default priority of 0 & attach to our media player

        mEqualizer = new Equalizer(priority, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);

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

        setupVisualizer(mMediaPlayer.getAudioSessionId());

        setupAutomaticFit();

        startService(new Intent(this, NotificationListener.class));
    }

    private void setupFab() {
        fab = findViewById(R.id.floatingActionButton);
        hiddenLayout = findViewById(R.id.hiddenLayout);
        cardView = findViewById(R.id.cardView);

        if (SavedData.readBool(SavedData.AUTO_ENABLED, this)){
            hiddenLayout.setVisibility(View.VISIBLE);
        } else {
            hiddenLayout.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                Log.e("Przycisk: ", "pressed");
                boolean isAutoEnabled = false;

                if (hiddenLayout.getVisibility() == View.VISIBLE) {

                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    hiddenLayout.setVisibility(View.GONE);
                    fab.setBackgroundColor(R.color.colorAccent);
                    isAutoEnabled = false;
                    Log.e("AUTOENABLEC", "false");

                } else {

                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    hiddenLayout.setVisibility(View.VISIBLE);
                    fab.setBackgroundColor(R.color.colorPrimaryDark);
                    isAutoEnabled = true;
                    Log.e("AUTOENABLEC", "true");
                }

                SavedData.saveSetting(SavedData.AUTO_ENABLED, isAutoEnabled, getApplicationContext());

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
//      get list of the device's equalizer presets

        seekBarProgress = new int[mEqualizer.getNumberOfBands()];

        for (short i = 0; i < mEqualizer.getNumberOfPresets(); i++) {
            equalizerPresetNames.add(mEqualizer.getPresetName(i));
        }

        equalizerPresetNames.add( mEqualizer.getNumberOfPresets(), "Custom");

        equalizerPresetSpinner.setAdapter(equalizerPresetSpinnerAdapter);

//        handle the spinner item selections
        equalizerPresetSpinner.setOnItemSelectedListener(new AdapterView
                .OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                //first list item selected by default and sets the preset accordingly
                short numberFrequencyBands = mEqualizer.getNumberOfBands();

                if (position == mEqualizer.getNumberOfPresets()){

                    for (short i = 0; i < numberFrequencyBands; i++) {
                        short equalizerBandIndex = i;

                        SeekBar seekBar = (SeekBar) findViewById(equalizerBandIndex);
//                    get current gain setting for this equalizer band
//                    set the progress indicator of this seekBar to indicate the current gain value
                        int[] temp = equalizerStringToArray(SavedData.readString(SavedData.EQUALIZERVALUES, getApplicationContext()));
                        seekBar.setProgress(temp[i]);
                    }
                    equalizerPresetSpinner.setSelection(position);

                } else {

                    mEqualizer.usePreset((short) position);
                    selected_preset = equalizerPresetNames.get(position);
                    selected_preset_num = position;

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

                    equalizerPresetSpinner.setSelection(position);
                }

                saveSettings();
                drawBezierUI();
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
        virtualizerSeeker.setMax(Virtualizer.PARAM_STRENGTH_SUPPORTED);

        textVirtualizer = findViewById(R.id.virtualizerLevel);
        textVirtualizerTitle =findViewById(R.id.textVirtualizerTitle);
        
        Log.e("VIRTUALIZER",""+Virtualizer.PARAM_STRENGTH_SUPPORTED);

        if(isHeadphonesPlugged()) {
            virtualizerSeeker.setEnabled(true);
            virtualizerSeeker.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
                @Override
                public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                    short value = (short) progress;
                    virtualizerValue = value;
                    virtualizer.setStrength(value);

                    textVirtualizer.setText("+" + value / 10);
                }

                @Override
                public void onStopTrackingTouch(CircularSeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(CircularSeekBar seekBar) {

                }
            });
        } else {
            virtualizerSeeker.setEnabled(false);
            textVirtualizer.setTextColor(LTGRAY);
            textVirtualizerTitle.setTextColor(LTGRAY);
            textVirtualizer.setText("Disabled");
            textVirtualizer.setTextSize(16);
        }
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
        artistText = findViewById(R.id.artistText);
        genreText =  findViewById(R.id.genreText);
        titleText =  findViewById(R.id.titleText);
        hiddenLayout = findViewById(R.id.hiddenLayout);
        albumText = findViewById(R.id.albumText);

        artistText.setText(SavedData.readString(SavedData.ARTIST, this));
        genreText.setText(SavedData.readString(SavedData.GENRE, this));
        titleText.setText(SavedData.readString(SavedData.SONG, this));
        albumText.setText(SavedData.readString(SavedData.ALBUM, this));

        if (SavedData.readBool(SavedData.AUTO_ENABLED, this)){
            hiddenLayout.setVisibility(View.VISIBLE);
        } else {
            //hiddenLayout.setVisibility(View.GONE);
        }
    }

    /* displays the SeekBar sliders for the supported equalizer frequency bands
     user can move sliders to change the frequency of the bands*/
    @RequiresApi(api = 26)
    private void setupEqualizerFxAndUI() {

//        get reference to linear layout for the seekBars
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutEqual);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        ViewGroup.LayoutParams layoutParamss = mLinearLayout.getLayoutParams();
        layoutParamss.width = height- (int) convertDpToPx(this, 480); //384
        layoutParamss.height= width - (int) convertDpToPx(this, 72);

        layoutParamss.width = (int) convertDpToPx(this, 230);

        mLinearLayout.setLayoutParams(layoutParamss);

        //ConstraintLayout mConstraintLayout = findViewById(R.id.eqHolder);
//        equalizer heading
        /*
        TextView equalizerHeading = new TextView(this);
        equalizerHeading.setText("Equalizer");
        equalizerHeading.setTextSize(20);
        equalizerHeading.setGravity(Gravity.CENTER_HORIZONTAL);
        mLinearLayout.addView(equalizerHeading);
        */
//        get number frequency bands supported by the equalizer engine
        final short numberFrequencyBands = mEqualizer.getNumberOfBands();
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

            TextView frequencyHeaderTextview = new TextView(this);
            frequencyHeaderTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            frequencyHeaderTextview.setGravity(Gravity.CENTER_HORIZONTAL);
            frequencyHeaderTextview
                    .setText((mEqualizer.getCenterFreq(equalizerBandIndex) / 1000) + " Hz");
            frequencyHeaderTextview.setTextColor(getResources().getColor(R.color.textColor));

            Typeface typeface = getResources().getFont( R.font.montserrat);
            frequencyHeaderTextview.setTypeface(typeface);
            mLinearLayout.addView(frequencyHeaderTextview);

//            set up linear layout to contain each seekBar
            LinearLayout seekBarRowLayout = new LinearLayout(this);
            seekBarRowLayout.setOrientation(LinearLayout.HORIZONTAL);

//            set up lower level textview for this seekBar

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


            //            **********  the seekBar  **************
//            set the layout parameters for the seekbar

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            //layoutParams.height = 100;

//            create a new seekBar
            SeekBar seekBar = new SeekBar(this);
//            give the seekBar an ID

            seekBar.setId(i);
            seekBar.setPadding( 30, 40, 30, 40);

            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);
//            set the progress for this seekBar
            seekBar.setProgress(mEqualizer.getBandLevel(equalizerBandIndex));

//            change progress as its changed by moving the sliders
            final short finalI = i;

            seekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    mEqualizer.setBandLevel(equalizerBandIndex,
                            (short) (progress + lowerEqualizerBandLevel));

                    equalizerPresetSpinner.setSelection(mEqualizer.getNumberOfPresets());

                    if (fromUser){
                        saveEqualizerValues();
                        drawBezierUI();
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    //not used
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    //not used

                    selected_preset = "Custom";
                    selected_preset_num = mEqualizer.getNumberOfPresets();

                    SavedData.saveSetting(SavedData.SELECTED_PRESET_NUM_KEY, selected_preset_num, getApplicationContext());
                    SavedData.saveSetting(SavedData.SELECTED_PRESET, "Custom", getApplicationContext());

                    Log.e(">>>>>SavedData", ": " + SavedData.readString(SavedData.EQUALIZERVALUES, getApplicationContext()));
                    Log.e(">>>>>SavedData", ": " + SavedData.readString(SavedData.SELECTED_PRESET, getApplicationContext()));
                    BackgroundService.updateNotification(getApplicationContext());
                }
            });

//            add the lower and upper band level textviews and the seekBar to the row layout
            //seekBarRowLayout.addView(lowerEqualizerBandLevelTextview);
            seekBarRowLayout.addView(seekBar);
            //seekBarRowLayout.addView(upperEqualizerBandLevelTextview);

            mLinearLayout.addView(seekBarRowLayout);

            equalizeSound();
        }
    }

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

    public void CalculatePoints(){
        //int[][] location = new int[mEqualizer.getNumberOfBands()][2];

        int[] x = new int[mEqualizer.getNumberOfBands()];
        int[] y = new int[mEqualizer.getNumberOfBands()];

        for (int i = 0; i < mEqualizer.getNumberOfBands(); i++){
            //seekbarWidth = seekBar.getHeight() - seekBar.getPaddingTop() - seekBar.getPaddingBottom();
            //thumbPos[finalI] = seekBar.getPaddingBottom() + width * seekBar.getProgress() / seekBar.getMax();
            SeekBar seekBar = findViewById(i);
            //seekBar.getLocationOnScreen(location[i]);

            int[] location = new int[2];

            seekBar.getLocationOnScreen(location);

            int width = seekBar.getWidth();

            x[i] = location[0] + seekBar.getHeight()/2;
            y[i] = location[1] - seekBar.getPaddingLeft()
                    - width
                    * seekBar.getProgress()
                    / seekBar.getMax()
            -seekBar.getPaddingRight();

        }
        SavedData.saveSetting(SavedData.X_CORD, equalizerToString(x), this);
        SavedData.saveSetting(SavedData.Y_CORD, equalizerToString(y), this);
        Log.e("Calculatepoints", SavedData.readString(SavedData.X_CORD, this)+ " " +SavedData.readString(SavedData.Y_CORD, this) );

    }

    public void saveEqualizerValues(){
        for (int i = 0; i < mEqualizer.getNumberOfBands(); i++){

            SeekBar seekBar = findViewById(i);
            seekBarProgress[i] = seekBar.getProgress();

            SavedData.saveSetting(SavedData.EQUALIZERVALUES, equalizerToString(seekBarProgress), getApplicationContext());
        }
    }

    @Override
    protected  void onResume() {
        super.onResume();

        if (SavedData.readBool(SavedData.ENABLED, getApplicationContext())) {
            if (SavedData.readInt(SavedData.SELECTED_PRESET_NUM_KEY, this) == mEqualizer.getNumberOfPresets()){
                equalizerPresetSpinner.setSelection(SavedData.readInt(SavedData.SELECTED_PRESET_NUM_KEY, this));
                for (short i = 0; i < mEqualizer.getNumberOfBands(); i++) {
                    short equalizerBandIndex = i;

                    SeekBar seekBar = (SeekBar) findViewById(equalizerBandIndex);
//                    get current gain setting for this equalizer band
//                    set the progress indicator of this seekBar to indicate the current gain value
                    int[] temp = equalizerStringToArray(SavedData.readString(SavedData.EQUALIZERVALUES, getApplicationContext()));
                    seekBar.setProgress(temp[i]);
                }

                equalizerPresetSpinner.setSelection(mEqualizer.getNumberOfPresets());
            } else {
                equalizerPresetSpinner.setSelection(SavedData.readInt(SavedData.SELECTED_PRESET_NUM_KEY, this));
            }

            if (SavedData.readBool(SavedData.AUTO_ENABLED, this)){
                hiddenLayout.setVisibility(View.VISIBLE);
            } else {
                hiddenLayout.setVisibility(View.GONE);
            }

            if (!SavedData.readBool(SavedData.ENABLED, this)){
                    mEqualizer.release();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
            }

            loudBar.setProgress(SavedData.readInt(SavedData.LOUD_VALUE_KEY, this));

            virtualizerSeeker.setProgress(SavedData.readInt(SavedData.VIRTUALIZER_VALUE_KEY, this));
            bassCircular.setProgress(SavedData.readInt(SavedData.BASS_VALUE_KEY, this));

            artistText.setText(SavedData.readString(SavedData.ARTIST, this));
            genreText.setText(SavedData.readString(SavedData.GENRE, this));
            titleText.setText(SavedData.readString(SavedData.SONG, this));
        }

    }

    @Override
    protected void onStart(){
        super.onStart();
        if (SavedData.readBool(SavedData.ENABLED, this)){

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if ( SavedData.readBool(SavedData.ENABLED, this)){
            saveSettings();
        }


        if (isFinishing() && mMediaPlayer != null && !SavedData.readBool(SavedData.ENABLED, this)) {

            mEqualizer.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        if(waveVisualizer != null){
            waveVisualizer.release();
        }
    }

    private void saveSettings(){

        SavedData.saveSetting(SavedData.BASS_VALUE_KEY, bassValue, this);
        SavedData.saveSetting(SavedData.LOUD_VALUE_KEY, loudValue, this);
        SavedData.saveSetting(SavedData.SELECTED_PRESET_NUM_KEY, selected_preset_num, this);
        SavedData.saveSetting(SavedData.SELECTED_PRESET, selected_preset, this);

        if (SavedData.readBool(SavedData.ENABLED, this)) {
           // SavedData.saveSetting(SavedData.EQUALIZERVALUES, equalizerToString(seekBarProgress), getApplicationContext());
        }

        SavedData.saveSetting(SavedData.VIRTUALIZER_VALUE_KEY, virtualizerValue, this);
    }

    public void drawBezierUI(){

        if (SavedData.readBool(SavedData.ENABLED, this)) {

            CalculatePoints();
            bezierView.init();
            bezierView.update();

            bezierView.invalidate();
        }
    }

    public float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public String equalizerToString (int[] values){
        String equalizerString = "";

        for (int i = 0; i < values.length; i++){
            equalizerString += values[i] + ";";
        }

        return equalizerString;
    }

    public int[] equalizerStringToArray (String string){
        String[] stringArray = string.split(";");
        int[] equalizerIntArray = new int[stringArray.length];

        for (int i = 0; i < stringArray.length; i++){
            equalizerIntArray[i] = Integer.parseInt(stringArray[i]);
        }

        return equalizerIntArray;
    }

    private boolean isHeadphonesPlugged(){
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
        for(AudioDeviceInfo deviceInfo : audioDevices){
            if(deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADSET){
                return true;
            }
        }
        return false;
    }
}