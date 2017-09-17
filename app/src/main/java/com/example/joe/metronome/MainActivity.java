package com.example.joe.metronome;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    // define all widgets as java objects
    private CheckBox checkBoxBeeps, checkBoxClicks, checkBoxCounting, checkBoxSnare;
    // private TextView textViewTempo, textViewTimeSig;
    private EditText editTextTempo;
    private Spinner spinnerTimeSig1, spinnerTimeSig2;
    private Button buttonTempoUp, buttonTempoDown;
    private ToggleButton toggleButton;
    private MediaPlayer mediaPlayer;

    //handler variable which will be used for the looping sound
    private Handler handler = new Handler();

    private int tempo = 120, timeSig1 = 4, timeSig2 = 4, currentBeat = 0;
    private float time;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // define all widgets in the activity
        editTextTempo = (EditText) findViewById(R.id.editTextTempo);

        spinnerTimeSig1 = (Spinner) findViewById(R.id.spinnerTimeSig1);
        spinnerTimeSig2 = (Spinner) findViewById(R.id.spinnerTimeSig2);

        checkBoxBeeps = (CheckBox) findViewById(R.id.checkBoxBeeps);
        checkBoxClicks = (CheckBox) findViewById(R.id.checkBoxClicks);
        checkBoxCounting = (CheckBox) findViewById(R.id.checkBoxCounting);
        checkBoxSnare = (CheckBox) findViewById(R.id.checkBoxSnare);

        buttonTempoDown = (Button) findViewById(R.id.buttonTempoDown);
        buttonTempoUp = (Button) findViewById(R.id.buttonTempoUp);

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);

        // edit text default values
        editTextTempo.setText(Integer.toString(tempo), TextView.BufferType.EDITABLE);
        editTextTempo.setGravity(Gravity.CENTER);

        // apply item arrays to spinners
        final ArrayAdapter<CharSequence> adapterTimeSig1 = ArrayAdapter.createFromResource(this, R.array.timeSigInts1, android.R.layout.simple_spinner_item);
        adapterTimeSig1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeSig1.setAdapter(adapterTimeSig1);

        final ArrayAdapter<CharSequence> adapterTimeSig2 = ArrayAdapter.createFromResource(this, R.array.timeSigInts2, android.R.layout.simple_spinner_item);
        adapterTimeSig2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeSig2.setAdapter(adapterTimeSig2);

        // set Beeps as the initial sound turned on
        checkBoxBeeps.setChecked(true);

        // initialize media player
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.punch_snare);

        // create action listeners for these widgets

        // going to create on on click listener for each checkbox
        // each listener will set all other checkboxes to false
        // then change the sound type for the metronome for
        checkBoxBeeps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    checkBoxClicks.setChecked(false);
                    checkBoxCounting.setChecked(false);
                    checkBoxSnare.setChecked(false);
                }
            }
        });

        checkBoxClicks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    checkBoxBeeps.setChecked(false);
                    checkBoxCounting.setChecked(false);
                    checkBoxSnare.setChecked(false);
                }
            }
        });

        checkBoxCounting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    checkBoxBeeps.setChecked(false);
                    checkBoxClicks.setChecked(false);
                    checkBoxSnare.setChecked(false);
                }
            }
        });

        checkBoxSnare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    checkBoxBeeps.setChecked(false);
                    checkBoxClicks.setChecked(false);
                    checkBoxCounting.setChecked(false);
                }
            }
        });

        // set on click listeners for the buttons next to the tempo
        // these will decrease or increase the text fields value by 1

        buttonTempoUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = editTextTempo.getText().toString();
                int i = Integer.parseInt(s);
                i++;
                editTextTempo.setText(Integer.toString(i), TextView.BufferType.EDITABLE);
                tempo = i;
            }
        });

        buttonTempoDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = editTextTempo.getText().toString();
                int i = Integer.parseInt(s);
                i--;
                editTextTempo.setText(Integer.toString(i), TextView.BufferType.EDITABLE);
                tempo = i;
            }
        });

        // for the spinners...
        // make it so that changing the value of each respective spinner
        // will cause the variables timeSig1 and timeSig2 to change
        // these variables will later be used to determine when to place downbeats
        // and the note size for the metronome
        // ex) 5/8 will give you a downbeat every 5th 8th note
        spinnerTimeSig1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String s = (String)adapterTimeSig1.getItem(i);
                timeSig1 = Integer.parseInt(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerTimeSig2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String s = (String)adapterTimeSig2.getItem(i);
                timeSig2 = Integer.parseInt(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // the onCheckedListener will contain the logic around creating the beat of the metronome
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            /*
                            if(currentBeat%timeSig1 == 0){
                                mediaPlayer.setVolume(1.0f, 1.0f);
                            }else{
                                mediaPlayer.setVolume(0.1f, 0.1f);
                            }
                            */
                            currentBeat++;
                            mediaPlayer.start();
                            handler.postDelayed(this, (long) 60000/(tempo));
                        }
                    }, 1);
                }else{
                    handler.removeCallbacksAndMessages(null);
                }
            }
        });


    }
}
