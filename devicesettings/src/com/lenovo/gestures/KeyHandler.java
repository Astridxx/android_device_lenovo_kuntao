/*
 * Copyright (C) 2019 The LineageOS Project
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

package com.lenovo.gestures;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.view.KeyEvent;

import com.android.internal.os.DeviceKeyHandler;

public class KeyHandler implements DeviceKeyHandler {

    private final Context mContext;
    private boolean mFingerprintGesturesEnabled = false;
    private static long time = 0;
    private static long time1 = 0;
    private static final String TAG = "keyLogs";
    private static String str = "KeyCode ScanCode";

    public KeyHandler(Context context) {
        mContext = context;

        IntentFilter fingerprintGesturesFilter =
                new IntentFilter(Constants.FINGERPRINT_GESTURES_INTENT);
        mContext.registerReceiver(mFingerprintGesturesReceiver, fingerprintGesturesFilter);
    }

    @Override
    public KeyEvent handleKeyEvent(KeyEvent event) {

        int keyCode;
        int scanCode;
        int action;

        keyCode = event.getKeyCode();
        scanCode = event.getScanCode();

        if(keyCode == KeyEvent.KEYCODE_HOME) {
                action = event.getAction();
                if(action == KeyEvent.ACTION_DOWN)
                        time1 = System.currentTimeMillis();
                else if(action == KeyEvent.ACTION_UP)
                        time = System.currentTimeMillis();
            return event;
        }

        if ((keyCode == KeyEvent.KEYCODE_BACK && scanCode == 562) ||
                (keyCode == KeyEvent.KEYCODE_APP_SWITCH && scanCode == 563)) {

           if((System.currentTimeMillis() - time1) < 700) return null;
           if((System.currentTimeMillis() - time) < 500) return null;

           /* Consume the fingerprint gestures key events if not enabled */
            return !mFingerprintGesturesEnabled ? null : event;
        }

        return event;
    }


    private BroadcastReceiver mFingerprintGesturesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mFingerprintGesturesEnabled =
                    intent.getBooleanExtra(Constants.FINGERPRINT_GESTURES_INTENT_ENABLED, false);
        }
    };
}
