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

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.stag.horns.preferences.Utils;
import com.stag.horns.preferences.SystemSettingMasterSwitchPreference;
import com.stag.horns.preferences.CustomSeekBarPreference;
import com.stag.horns.preferences.SystemSettingSwitchPreference;

import java.util.ArrayList;
import java.util.List;

import com.stag.horns.preferences.Utils;

@SearchIndexable
public class NotificationSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {

    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";
    private static final String KEY_BATT_LIGHT = "battery_light_enabled";
    private static final String KEY_AMBIENT = "ambient_notification_light_enabled";
    private static final String KEY_CHARGING_LIGHT = "charging_light";
    private static final String LED_CATEGORY = "led";
    private static final String NOTIFICATION_HEADERS = "notification_headers";
    private static final String NOTIFICATION_PULSE_COLOR = "ambient_notification_light_color";
    private static final String NOTIFICATION_PULSE_DURATION = "notification_pulse_duration";
    private static final String NOTIFICATION_PULSE_REPEATS = "notification_pulse_repeats";
    private static final String PULSE_COLOR_MODE_PREF = "ambient_notification_light_color_mode";

    private ColorPickerPreference mEdgeLightColorPreference;
    private CustomSeekBarPreference mEdgeLightDurationPreference;
    private CustomSeekBarPreference mEdgeLightRepeatCountPreference;
    private ListPreference mColorMode;
    private SystemSettingSwitchPreference mAmbientPref;
    private SystemSettingSwitchPreference mNotificationHeader;
    private SystemSettingMasterSwitchPreference mBatteryLight;

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


        mAmbientPref = (SystemSettingSwitchPreference) findPreference(KEY_AMBIENT);
        boolean aodEnabled = Settings.Secure.getIntForUser(resolver,
                Settings.Secure.DOZE_ALWAYS_ON, 0, UserHandle.USER_CURRENT) == 1;
        if (!aodEnabled) {
            mAmbientPref.setChecked(false);
            mAmbientPref.setEnabled(false);
            mAmbientPref.setSummary(R.string.aod_disabled);
        }

        mEdgeLightRepeatCountPreference = (CustomSeekBarPreference) findPreference(NOTIFICATION_PULSE_REPEATS);
        mEdgeLightRepeatCountPreference.setOnPreferenceChangeListener(this);
        int repeats = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_REPEATS, 0);
        mEdgeLightRepeatCountPreference.setValue(repeats);

        mEdgeLightDurationPreference = (CustomSeekBarPreference) findPreference(NOTIFICATION_PULSE_DURATION);
        mEdgeLightDurationPreference.setOnPreferenceChangeListener(this);
        int duration = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_DURATION, 2);
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
        if (edgeLightColorHex.equals("#ff1a73e8")) {
            mEdgeLightColorPreference.setSummary(R.string.color_default);
        } else {
            mEdgeLightColorPreference.setSummary(edgeLightColorHex);
        }
        mEdgeLightColorPreference.setOnPreferenceChangeListener(this);
        mBatteryLight = (SystemSettingMasterSwitchPreference)
                findPreference(KEY_BATT_LIGHT);
        boolean enabled = Settings.System.getInt(resolver,
                KEY_BATT_LIGHT, 1) == 1;
        mBatteryLight.setChecked(enabled);
        mBatteryLight.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
	ContentResolver resolver = getActivity().getContentResolver();
	if (preference == mEdgeLightColorPreference) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ff1a73e8")) {
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
                    Settings.System.NOTIFICATION_PULSE_REPEATS, value);
            return true;
        } else if (preference == mEdgeLightDurationPreference) {
            int value = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NOTIFICATION_PULSE_DURATION, value);
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
                if (preference == mBatteryLight) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver, KEY_BATT_LIGHT, value ? 1 : 0);
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
