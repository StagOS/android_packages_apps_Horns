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

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.stag.horns.preferences.Utils;
import com.stag.horns.preferences.SystemSettingSeekBarPreference;
import com.stag.horns.preferences.SystemSettingMasterSwitchPreference;

import java.util.ArrayList;
import java.util.List;

import com.stag.horns.preferences.Utils;

@SearchIndexable
public class NotificationSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {

    private static final String AMBIENT_LIGHT_DURATION = "ambient_light_duration";
    private static final String AMBIENT_LIGHT_REPEAT_COUNT = "ambient_light_repeat_count";
    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";
    private static final String NOTIFICATION_PULSE_COLOR = "ambient_notification_light_color";
    private static final String PULSE_COLOR_MODE_PREF = "ambient_notification_light_color_mode";

    private ColorPickerPreference mEdgeLightColorPreference;
    private ListPreference mColorMode;
    private SystemSettingSeekBarPreference mEdgeLightDurationPreference;
    private SystemSettingSeekBarPreference mEdgeLightRepeatCountPreference;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.horns_notifications);
	ContentResolver resolver = getActivity().getContentResolver();
	final PreferenceScreen prefScreen = getPreferenceScreen();

        PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS);
        if (!Utils.isVoiceCapable(getActivity())) {
            prefScreen.removePreference(incallVibCategory);
        }


        mEdgeLightRepeatCountPreference = (SystemSettingSeekBarPreference) findPreference(AMBIENT_LIGHT_REPEAT_COUNT);
        mEdgeLightRepeatCountPreference.setOnPreferenceChangeListener(this);
        int rCount = Settings.System.getInt(getContentResolver(),
                Settings.System.AMBIENT_LIGHT_REPEAT_COUNT, 0);
        mEdgeLightRepeatCountPreference.setValue(rCount);

        mEdgeLightDurationPreference = (SystemSettingSeekBarPreference) findPreference(AMBIENT_LIGHT_DURATION);
        mEdgeLightDurationPreference.setOnPreferenceChangeListener(this);
        int duration = Settings.System.getInt(getContentResolver(),
                Settings.System.AMBIENT_LIGHT_DURATION, 2);
        mEdgeLightDurationPreference.setValue(duration);

        mColorMode = (ListPreference) findPreference(PULSE_COLOR_MODE_PREF);
        int value;
        boolean colorModeAutomatic = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_COLOR_AUTOMATIC, 0) != 0;
        boolean colorModeAccent = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_ACCENT, 0) != 0;
        if (colorModeAutomatic) {
            value = 0;
        } else if (colorModeAccent) {
            value = 1;
        } else {
            value = 2;
        }

        mColorMode.setValue(Integer.toString(value));
        mColorMode.setSummary(mColorMode.getEntry());
        mColorMode.setOnPreferenceChangeListener(this);

        mEdgeLightColorPreference = (ColorPickerPreference) findPreference(NOTIFICATION_PULSE_COLOR);
        int edgeLightColor = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_COLOR, 0xFF1A73E8);
        mEdgeLightColorPreference.setNewPreviewColor(edgeLightColor);
        mEdgeLightColorPreference.setAlphaSliderEnabled(false);
        String edgeLightColorHex = String.format("#%08x", (0xFF1A73E8 & edgeLightColor));
        if (edgeLightColorHex.equals("#ff3980ff")) {
            mEdgeLightColorPreference.setSummary(R.string.color_default);
        } else {
            mEdgeLightColorPreference.setSummary(edgeLightColorHex);
        }
        mEdgeLightColorPreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
	ContentResolver resolver = getActivity().getContentResolver();
	if (preference == mEdgeLightColorPreference) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ff3980ff")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NOTIFICATION_PULSE_COLOR, intHex);
            return true;
        } else if (preference == mEdgeLightRepeatCountPreference) {
            int value = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.AMBIENT_LIGHT_REPEAT_COUNT, value);
            return true;
        } else if (preference == mEdgeLightDurationPreference) {
            int value = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.AMBIENT_LIGHT_DURATION, value);
            return true;
        } else if (preference == mColorMode) {
            int value = Integer.valueOf((String) newValue);
            int index = mColorMode.findIndexOfValue((String) newValue);
            mColorMode.setSummary(mColorMode.getEntries()[index]);
            if (value == 0) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_COLOR_AUTOMATIC, 1);
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_ACCENT, 0);
            } else if (value == 1) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_COLOR_AUTOMATIC, 0);
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_ACCENT, 1);
            } else {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_COLOR_AUTOMATIC, 0);
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NOTIFICATION_PULSE_ACCENT, 0);
            }
            return true;
	}
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
