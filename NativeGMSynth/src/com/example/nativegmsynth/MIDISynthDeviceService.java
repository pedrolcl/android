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

import android.media.midi.MidiDeviceService;
import android.media.midi.MidiDeviceStatus;
import android.media.midi.MidiReceiver;
import android.util.Log;

public class MIDISynthDeviceService extends MidiDeviceService {

    private static final String TAG = "MIDISynthDeviceService";
    private MIDISynth mSynthEngine = null;
    private boolean mSynthStarted = false;

    @Override
    public void onCreate() {
    	try {
    		mSynthEngine = new MIDISynth();
            super.onCreate();
    	} catch (Exception ex) {
    		Log.e(TAG, ex.getMessage());
    	}
    }
    
    @Override
    public void onClose() {
    	if (mSynthEngine != null) {
    		mSynthEngine.stop();
	        mSynthEngine.close();
	        mSynthStarted = false;
	        mSynthEngine = null;
    	}
    	super.onClose();
    }

    @Override
    public void onDestroy() {
    	if (mSynthEngine != null) {
	        mSynthEngine.stop();
	        mSynthEngine.close();
	        mSynthStarted = false;
	        mSynthEngine = null;
    	}
        super.onDestroy();
    }

    @Override
    public MidiReceiver[] onGetInputPortReceivers() {
        return new MidiReceiver[]{mSynthEngine};
    }

    /**
     * This will get called when clients connect or disconnect.
     */
    @Override
    public void onDeviceStatusChanged(MidiDeviceStatus status) {
    	try {
	        if (status.isInputPortOpen(0) && !mSynthStarted) {
	            mSynthEngine.start();
	            mSynthStarted = true;
	        } else if (!status.isInputPortOpen(0) && mSynthStarted) {
	            mSynthEngine.stop();
	            mSynthStarted = false;
	        }
    	} catch (Exception ex) {
    		Log.e(TAG, ex.getMessage());
    	}
    }

}
