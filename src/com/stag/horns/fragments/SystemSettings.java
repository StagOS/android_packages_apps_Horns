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
import com.android.internal.util.stag.StagUtils;
import com.android.settings.R;

import android.os.Bundle;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import com.stag.horns.preferences.SystemSettingSwitchPreference;
import com.stag.horns.preferences.SecureSettingSwitchPreference;
import com.stag.horns.preferences.Utils;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class SystemSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String CUSTOM_UI_TOGGLE = "custom_ui_toggle";
	private static final String PI_PREF = "pintegrity_category";
	private static final String VOLUME_PANEL_ON_LEFT = "volume_panel_on_left";

	private SystemSettingSwitchPreference mCustomUIToggle;
	private SecureSettingSwitchPreference mVolumePanelOnLeft;
	private Preference mPIPref;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.horns_system);
        ContentResolver resolver = getActivity().getContentResolver();
        Context mContext = getContext();

        final PreferenceScreen prefScreen = getPreferenceScreen();

		mCustomUIToggle = (SystemSettingSwitchPreference) findPreference(CUSTOM_UI_TOGGLE);
		mCustomUIToggle.setChecked((Settings.System.getInt(resolver,
				Settings.System.CUSTOM_UI_TOGGLE, 0) == 1));
		mCustomUIToggle.setOnPreferenceChangeListener(this);

		mPIPref = (Preference) findPreference(PI_PREF);
		if (!StagUtils.isPackageInstalled(mContext, "com.google.android.gms")) {
			prefScreen.removePreference(mPIPref);
		}

		mVolumePanelOnLeft = (SecureSettingSwitchPreference) findPreference(VOLUME_PANEL_ON_LEFT);
		// check SystemUI R.bool config value for volume panel position
		mVolumePanelOnLeft.setChecked(Settings.System.getInt(resolver,
				Settings.Secure.VOLUME_PANEL_ON_LEFT,  0) == 1);
	}

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HORNS;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
		ContentResolver resolver = getActivity().getContentResolver();
		if (preference == mCustomUIToggle) {
			StagUtils.showPackageRestartDialog(getContext(), "com.android.settings");
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
