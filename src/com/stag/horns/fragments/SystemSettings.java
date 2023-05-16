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
import android.util.Log;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.SearchIndexableResource;
import com.android.settings.R;
import android.provider.Settings;

// import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import com.android.internal.util.stag.udfps.UdfpsUtils;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.stag.horns.preferences.Utils;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class SystemSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String TAG = "SystemSettings";

    private static final String UDFPS_SETTINGS = "udfps_settings";

    private String MONET_ENGINE_COLOR_OVERRIDE = "monet_engine_color_override";
    private String MONET_ENGINE_BGCOLOR_OVERRIDE = "monet_engine_bgcolor_override";

    private ColorPickerPreference mMonetColor;
    private ColorPickerPreference mMonetBgColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.horns_system);
        ContentResolver resolver = getActivity().getContentResolver();
        Context mContext = getContext();

        final PreferenceScreen prefScreen = getPreferenceScreen();

		// mCustomTheme = (ListPreference) findPreference("system_custom_theme");
		// final int currentTheme = Settings.Secure.getIntForUser(resolver,
		// 		Settings.Secure.SYSTEM_CUSTOM_THEME, 0, UserHandle.USER_CURRENT);
		// mCustomTheme.setValue(String.valueOf(currentTheme));
		// mCustomTheme.setOnPreferenceChangeListener(this);
		// Log.d(TAG, "Current theme: " + String.valueOf(currentTheme));
        if (!UdfpsUtils.hasUdfpsSupport(getContext())) {
            prefScreen.removePreference(findPreference(UDFPS_SETTINGS));
        }

        mMonetColor = (ColorPickerPreference) prefScreen.findPreference(MONET_ENGINE_COLOR_OVERRIDE);
        int intColor = Settings.Secure.getInt(resolver, MONET_ENGINE_COLOR_OVERRIDE, 0xFF1B6EF3);
        String hexColor = String.format("#%08x", (0xffffff & intColor));
        mMonetColor.setNewPreviewColor(intColor);
        mMonetColor.setSummary(hexColor);
        mMonetColor.setOnPreferenceChangeListener(this);


        mMonetBgColor = (ColorPickerPreference) prefScreen.findPreference(MONET_ENGINE_BGCOLOR_OVERRIDE);
        int intBgColor = Settings.Secure.getInt(resolver, MONET_ENGINE_BGCOLOR_OVERRIDE, 0xFF1B6EF3);
        String hexBgColor = String.format("#%08x", (0xffffff & intColor));
        mMonetBgColor.setNewPreviewColor(intBgColor);
        mMonetBgColor.setSummary(hexBgColor);
        mMonetBgColor.setOnPreferenceChangeListener(this);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HORNS;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
	ContentResolver resolver = getActivity().getContentResolver();
		// if(preference == mCustomTheme) {
		// 	Log.d(TAG, "Custom theme changed");
		// 	int value = Integer.parseInt((String) newValue);
		// 	Log.d(TAG, "New theme: " + String.valueOf(value));
		// 	Settings.Secure.putIntForUser(resolver,
		// 			Settings.Secure.SYSTEM_CUSTOM_THEME, value, UserHandle.USER_CURRENT);
		// 	return true;
		// }
        if (preference == mMonetColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                .parseInt(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.Secure.putInt(resolver,
                MONET_ENGINE_COLOR_OVERRIDE, intHex);
            return true;
        }
        if (preference == mMonetBgColor) {
            String hexbg = ColorPickerPreference.convertToARGB(Integer
                .parseInt(String.valueOf(newValue)));
            preference.setSummary(hexbg);
            int intBgHex = ColorPickerPreference.convertToColorInt(hexbg);
            Settings.Secure.putInt(resolver,
                MONET_ENGINE_BGCOLOR_OVERRIDE, intBgHex);
            return true;
        }
        return false;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
		new BaseSearchIndexProvider() {
			@Override
			public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
					boolean enabled) {
				ArrayList<SearchIndexableResource> result =
						new ArrayList<SearchIndexableResource>();

				SearchIndexableResource sir = new SearchIndexableResource(context);
				sir.xmlResId = R.xml.horns_system;
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
