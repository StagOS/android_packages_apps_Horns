/*
 *  Copyright (C) 2015 The OmniROM Project
 *	Copyright (C) 2020 StagOS
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

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.media.AudioAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.SearchIndexableResource;
import com.android.settings.R;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.ListPreference;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.widget.RadioButtonPreference;

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.stag.horns.preferences.HornsUtils;
import com.stag.horns.preferences.CustomSeekBarPreference;
import com.stag.horns.preferences.SystemSettingSwitchPreference;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@SearchIndexable
public class NotificationSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable, Preference.OnPreferenceClickListener {

    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";

    private static final String ALERT_SLIDER_PREF = "alert_slider_notifications";

    private Context mContext;
    private Vibrator mVibrator;

    private final AudioAttributes mAudioAttributesRingtone = new AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
        .build();

    private static final String RINGTONE_VIBRATION_PATTERN = "ringtone_vibration_pattern";
    private static final String[] mKeys = {"pattern_dzzz_dzzz", "pattern_dzzz_da", "pattern_mm_mm_mm",
        "pattern_da_da_dzzz", "pattern_da_dzzz_da"};

    private final Map<String, RadioButtonPreference> mStringToPreferenceMap = new HashMap<>();

    private RadioButtonPreference[] mRadioPreferences = new RadioButtonPreference[5];

    private static final long[] DZZZ_DZZZ_VIBRATION_PATTERN = {
        0, // No delay before starting
        800, // How long to vibrate
        800, // How long to wait before vibrating again
        800, // How long to vibrate
        800, // How long to wait before vibrating again
    };

    private static final long[] DZZZ_DA_VIBRATION_PATTERN = {
        0, // No delay before starting
        500, // How long to vibrate
        200, // Delay
        20, // How long to vibrate
        720, // How long to wait before vibrating again
    };

    private static final long[] MM_MM_MM_VIBRATION_PATTERN = {
        0, // No delay before starting
        300, // How long to vibrate
        400, // Delay
        300, // How long to vibrate
        400, // Delay
        300, // How long to vibrate
        1700, // How long to wait before vibrating again
    };

    private static final long[] DA_DA_DZZZ_VIBRATION_PATTERN = {
        0, // No delay before starting
        30, // How long to vibrate
        80, // Delay
        30, // How long to vibrate
        80, // Delay
        50,  // How long to vibrate
        180, // Delay
        600,  // How long to vibrate
        1050, // How long to wait before vibrating again
    };

    private static final long[] DA_DZZZ_DA_VIBRATION_PATTERN = {
        0, // No delay before starting
        80, // How long to vibrate
        200, // Delay
        600, // How long to vibrate
        150, // Delay
        20,  // How long to vibrate
        1050, // How long to wait before vibrating again
    };

    private static final int[] NINE_ELEMENTS_VIBRATION_AMPLITUDE = {
        0, // No delay before starting
        255, // Vibrate full amplitude
        0, // No amplitude while waiting
        255,
        0,
        255,
        0,
        255,
        0,
    };

    private static final int[] SEVEN_ELEMENTS_VIBRATION_AMPLITUDE = {
        0, // No delay before starting
        255, // Vibrate full amplitude
        0, // No amplitude while waiting
        255,
        0,
        255,
        0,
    };

    private static final int[] FIVE_ELEMENTS_VIBRATION_AMPLITUDE = {
        0, // No delay before starting
        255, // Vibrate full amplitude
        0, // No amplitude while waiting
        255,
        0,
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);


        mContext = getActivity().getApplicationContext();
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        if (mVibrator != null && !mVibrator.hasVibrator()) {
            mVibrator = null;
        }

        addPreferencesFromResource(R.xml.horns_notifications);
	ContentResolver resolver = getActivity().getContentResolver();
	final PreferenceScreen prefScreen = getPreferenceScreen();

        PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS);
        if (!HornsUtils.isVoiceCapable(getActivity())) {
            prefScreen.removePreference(incallVibCategory);
        }

        for (int i = 0; i < 5; i++) {
            mRadioPreferences[i] = (RadioButtonPreference) findPreference(mKeys[i]);
            mStringToPreferenceMap.put(mKeys[i], mRadioPreferences[i]);
            mRadioPreferences[i].setOnPreferenceClickListener(this);
        }

        final int currentPattern = Settings.System.getIntForUser(resolver, RINGTONE_VIBRATION_PATTERN, 0, UserHandle.USER_CURRENT);

        updateVibrationPattern(currentPattern);

        boolean alertSliderAvailable = getActivity().getResources().getBoolean(
                 com.android.internal.R.bool.config_hasAlertSlider);
         if (!alertSliderAvailable)
             getPreferenceScreen().removePreference(findPreference(ALERT_SLIDER_PREF));
    }

    private void updateVibrationPattern(int val) {
        for (int i = 0; i < 5; i++) {
            ((RadioButtonPreference) mStringToPreferenceMap.get(mKeys[i])).setChecked((val == i) ? true:false);
        }
        Settings.System.putIntForUser(getContentResolver(), RINGTONE_VIBRATION_PATTERN,
                val, UserHandle.USER_CURRENT);
    }

    private void performVibrationDemo(int val) {
        VibrationEffect mDefaultVibrationEffect;
        switch(val) {
            case 1:
                mDefaultVibrationEffect = VibrationEffect.createWaveform(DZZZ_DA_VIBRATION_PATTERN,
                    FIVE_ELEMENTS_VIBRATION_AMPLITUDE, -1);
                break;
            case 2:
                mDefaultVibrationEffect = VibrationEffect.createWaveform(MM_MM_MM_VIBRATION_PATTERN,
                    SEVEN_ELEMENTS_VIBRATION_AMPLITUDE, -1);
                break;
            case 3:
                mDefaultVibrationEffect = VibrationEffect.createWaveform(DA_DA_DZZZ_VIBRATION_PATTERN,
                    NINE_ELEMENTS_VIBRATION_AMPLITUDE, -1);
                break;
            case 4:
                mDefaultVibrationEffect = VibrationEffect.createWaveform(DA_DZZZ_DA_VIBRATION_PATTERN,
                    SEVEN_ELEMENTS_VIBRATION_AMPLITUDE, -1);
                break;
            default:
                mDefaultVibrationEffect = VibrationEffect.createWaveform(DZZZ_DZZZ_VIBRATION_PATTERN,
                    FIVE_ELEMENTS_VIBRATION_AMPLITUDE, -1);
                break;
        }
        if (mVibrator != null && mVibrator.hasVibrator()) {
            mVibrator.vibrate(mDefaultVibrationEffect, mAudioAttributesRingtone);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        final String key = preference.getKey();
        if (preference instanceof RadioButtonPreference) {
            int val = Arrays.asList(mKeys).indexOf(key);
            updateVibrationPattern(val);
            performVibrationDemo(val);
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
	ContentResolver resolver = getActivity().getContentResolver();
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HORNS;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
		new BaseSearchIndexProvider() {
			@Override
			public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
					boolean enabled) {
				ArrayList<SearchIndexableResource> result =
						new ArrayList<SearchIndexableResource>();

				SearchIndexableResource sir = new SearchIndexableResource(context);
				sir.xmlResId = R.xml.horns_notifications;
				result.add(sir);
				return result;
			}

			@Override
			public List<String> getNonIndexableKeys(Context context) {
				List<String> keys = super.getNonIndexableKeys(context);
				return keys;
			}
    };
}
