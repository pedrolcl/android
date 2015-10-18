/*
 * OpenSL ES audio output for Sonivox EAS synthesizer in real time
 * Copyright (C) 2015 Pedro López-Cabanillas <plcl@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.nativegmsynth;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.media.AudioManager;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;


public class MainActivity extends Activity implements OnTouchListener, OnCheckedChangeListener {

	static final String TAG = "MainActivity";
    MidiManager mMidiManager;
	MidiInputPort mInputPort;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		boolean claimsFeature = false;
		String sampleRate = null;
		String framesPerBuffer = null; 
		
		try {
			PackageManager pm = getPackageManager();
	        if (pm.hasSystemFeature(PackageManager.FEATURE_MIDI)) {
	            setupMidi();
	        }
			claimsFeature = pm.hasSystemFeature(PackageManager.FEATURE_AUDIO_LOW_LATENCY);
			Log.d(TAG, "PackageManager says: hasLowLatency=" + claimsFeature );
			AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			sampleRate = am.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
			framesPerBuffer = am.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
			Log.d(TAG, "AudioManager says: SampleRate=" + sampleRate + " bufferSize=" + framesPerBuffer);
		} catch (Exception ex) {
			Log.e(TAG, "Error", ex);
		}
		
		TextView tv2 = (TextView) findViewById(R.id.textView2);
		tv2.setText(String.format("AudioManager configuration:\nHas low latency: %b\nsample rate: %s\nbuffer size:%s", claimsFeature, sampleRate, framesPerBuffer));
	
		Button btnC = (Button) findViewById(R.id.buttonC);
		btnC.setOnTouchListener(this);
		Button btnD = (Button) findViewById(R.id.buttonD);
		btnD.setOnTouchListener(this);
		Button btnE = (Button) findViewById(R.id.buttonE);
		btnE.setOnTouchListener(this);
		Button btnF = (Button) findViewById(R.id.buttonF);
		btnF.setOnTouchListener(this);
		Button btnG = (Button) findViewById(R.id.buttonG);
		btnG.setOnTouchListener(this);
		Button btnA = (Button) findViewById(R.id.buttonA);
		btnA.setOnTouchListener(this);
		Button btnB = (Button) findViewById(R.id.buttonB);
		btnB.setOnTouchListener(this);
		Button btnC2 = (Button) findViewById(R.id.buttonC2);
		btnC2.setOnTouchListener(this);
		
		RadioButton rb1 = (RadioButton) findViewById(R.id.rb1);
		rb1.setOnCheckedChangeListener(this);
		RadioButton rb2 = (RadioButton) findViewById(R.id.rb2);
		rb2.setOnCheckedChangeListener(this);
		RadioButton rb3 = (RadioButton) findViewById(R.id.rb3);
		rb3.setOnCheckedChangeListener(this);
		RadioButton rb4 = (RadioButton) findViewById(R.id.rb4);
		rb4.setOnCheckedChangeListener(this);
		RadioButton rb5 = (RadioButton) findViewById(R.id.rb5);
		rb5.setOnCheckedChangeListener(this);
	}

    /**
     * @return a device that matches the manufacturer and product or null
     */
    private MidiDeviceInfo findDevice(MidiManager midiManager,
            String manufacturer, String product) {
        for (MidiDeviceInfo info : midiManager.getDevices()) {
            String deviceManufacturer = info.getProperties()
                    .getString(MidiDeviceInfo.PROPERTY_MANUFACTURER);
            if ((manufacturer != null)
                    && manufacturer.equals(deviceManufacturer)) {
                String deviceProduct = info.getProperties()
                        .getString(MidiDeviceInfo.PROPERTY_PRODUCT);
                if ((product != null) && product.equals(deviceProduct)) {
                    return info;
                }
            }
        }
        return null;
    }
	
    @Override
    public void onDestroy() {
    	if (mInputPort != null) {
    		try {
				mInputPort.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
        super.onDestroy();  
    }    
    
    private void setupMidi() {
        mMidiManager = (MidiManager) getSystemService(MIDI_SERVICE);
        final MidiDeviceInfo synthInfo = findDevice(mMidiManager, "AndroidTest", "NativeGMSynth");
        if (synthInfo == null) {
        	Log.e(TAG, "could not find the device info for NativeGMSynth");
        } else  {
	        mMidiManager.openDevice(synthInfo, new MidiManager.OnDeviceOpenedListener() {
	            @Override
	            public void onDeviceOpened(MidiDevice device) {
	                if (device == null) {
	                    Log.e(TAG, "could not open the device " + synthInfo);
	                } else {
	                	mInputPort = device.openInputPort(0);
	                	if (mInputPort == null) {
                            Log.e(TAG, "could not open input port on " + synthInfo);
                        }
	                }
	            }}, new Handler(Looper.getMainLooper()));
        }
    }
	
	@Override
	public boolean onTouch(View v, MotionEvent ev) {
		byte midiNote = 0;
		byte midiStatus = 0;
		byte midiVelocity = 100; // loud enough
		
		switch (v.getId()) {
		case R.id.buttonC:
			midiNote = 60; // middle C
			break;
		case R.id.buttonD:
			midiNote = 62;
			break;
		case R.id.buttonE:
			midiNote = 64;
			break;
		case R.id.buttonF:
			midiNote = 65;
			break;
		case R.id.buttonG:
			midiNote = 67;
			break;
		case R.id.buttonA:
			midiNote = 69;
			break;
		case R.id.buttonB:
			midiNote = 71;
			break;
		case R.id.buttonC2:
			midiNote = 72;
			break;
		}
		
		switch (ev.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			midiStatus = (byte) 0x90; // note on, channel 1
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			midiStatus = (byte) 0x80; // note off, channel 1
			break;
		}	
		
		if (midiStatus != 0 && midiNote != 0 && mInputPort != null) {
			try {
				mInputPort.send(new byte[] {midiStatus, midiNote, midiVelocity}, 0, 3);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			byte midiPgm = 0;
			byte midiStatus = (byte) 0xC0;
			switch (buttonView.getId()) {
				case R.id.rb1:
					midiPgm = 1;
					break;
				case R.id.rb2:
					midiPgm = 18;
					break;
				case R.id.rb3:
					midiPgm = 50;
					break;
				case R.id.rb4:
					midiPgm = 56;
					break;
				case R.id.rb5:
					midiPgm = 65;
					break;
			}
			
			if (midiPgm != 0 && mInputPort != null) {
				try {
					mInputPort.send(new byte[] {midiStatus, midiPgm}, 0, 2);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
}
