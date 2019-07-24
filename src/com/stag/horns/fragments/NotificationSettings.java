/*
 * Copyright (C) 2018 StagOS
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

package com.stag.horns.fragments;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.net.ConnectivityManager;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;

import com.stag.horns.R;

public class NotificationSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private Preference mChargingLeds;
    private static final String SMS_BREATH = "sms_breath";
    private static final String MISSED_CALL_BREATH = "missed_call_breath";
    private static final String VOICEMAIL_BREATH = "voicemail_breath";

    private SwitchPreference mSmsBreath;
    private SwitchPreference mMissedCallBreath;
    private SwitchPreference mVoicemailBreath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.horns_notifications);
        ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen prefScreen = getPreferenceScreen();

           // Breathing Notifications
           mSmsBreath = (SwitchPreference) findPreference(SMS_BREATH);
           mMissedCallBreath = (SwitchPreference) findPreference(MISSED_CALL_BREATH);
           mVoicemailBreath = (SwitchPreference) findPreference(VOICEMAIL_BREATH);

           ConnectivityManager cm = (ConnectivityManager)
                   getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

           if (cm.isNetworkSupported(ConnectivityManager.TYPE_MOBILE)) {
               mSmsBreath.setChecked(Settings.Global.getInt(resolver,
                       Settings.Global.KEY_SMS_BREATH, 0) == 1);
               mSmsBreath.setOnPreferenceChangeListener(this);

               mMissedCallBreath.setChecked(Settings.Global.getInt(resolver,
                       Settings.Global.KEY_MISSED_CALL_BREATH, 0) == 1);
               mMissedCallBreath.setOnPreferenceChangeListener(this);

               mVoicemailBreath.setChecked(Settings.System.getInt(resolver,
                       Settings.System.KEY_VOICEMAIL_BREATH, 0) == 1);
               mVoicemailBreath.setOnPreferenceChangeListener(this);
           } else {
               prefScreen.removePreference(mSmsBreath);
               prefScreen.removePreference(mMissedCallBreath);
               prefScreen.removePreference(mVoicemailBreath);
           }

        mChargingLeds = (Preference) findPreference("charging_light");
        if (mChargingLeds != null && !getResources().getBoolean(
                com.android.internal.R.bool.config_intrusiveBatteryLed)) {
            prefScreen.removePreference(mChargingLeds);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HORNS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
       ContentResolver resolver = getActivity().getContentResolver();
       if (preference == mSmsBreath) {
            boolean value = (Boolean) newValue;
            Settings.Global.putInt(getContentResolver(), SMS_BREATH, value ? 1 : 0);
            return true;
        } else if (preference == mMissedCallBreath) {
            boolean value = (Boolean) newValue;
            Settings.Global.putInt(getContentResolver(), MISSED_CALL_BREATH, value ? 1 : 0);
            return true;
        } else if (preference == mVoicemailBreath) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), VOICEMAIL_BREATH, value ? 1 : 0);
            return true;
		}
        return false;
    }
}
