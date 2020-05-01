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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.stag.horns.preferences.SystemSettingSeekBarPreference;

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import com.android.internal.logging.nano.MetricsProto;

import com.stag.horns.preferences.CustomSeekBarPreference;
import com.stag.horns.preferences.SystemSettingMasterSwitchPreference;
import com.stag.horns.preferences.SystemSettingSwitchPreference;

import java.util.ArrayList;
import java.util.List;

import com.android.internal.util.stag.StagUtils;

import com.stag.horns.preferences.SystemSettingEditTextPreference;

@SearchIndexable
public class QuickSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String SHOW_QS_MEDIA_PLAYER ="quick_settings_media_player";
    private static final String PREF_QSBG_NEW_TINT = "qs_panel_bg_use_new_tint";

    private SwitchPreference mShowQSMediaPlayer;
    private SystemSettingSwitchPreference mQsBgNewTint;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.horns_quicksettings);
        ContentResolver resolver = getActivity().getContentResolver();

	mShowQSMediaPlayer = (SwitchPreference) findPreference(SHOW_QS_MEDIA_PLAYER);
        mShowQSMediaPlayer.setChecked((Settings.Global.getInt(resolver,
  	      Settings.Global.SHOW_MEDIA_ON_QUICK_SETTINGS, 1) == 1));
        mShowQSMediaPlayer.setOnPreferenceChangeListener(this);

        mQsBgNewTint = (SystemSettingSwitchPreference) findPreference(PREF_QSBG_NEW_TINT);
        mQsBgNewTint.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.QS_PANEL_BG_USE_NEW_TINT, 1) == 1));
        mQsBgNewTint.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
	if (preference == mShowQSMediaPlayer){
	        final boolean isEnabled = (Boolean) newValue;
	        Settings.Global.putInt(getActivity().getContentResolver(),
			Settings.Global.SHOW_MEDIA_ON_QUICK_SETTINGS, isEnabled ? 1 : 0);
	        return true;
        } else if (preference == mQsBgNewTint) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.QS_PANEL_BG_USE_NEW_TINT, value ? 1 : 0);
            StagUtils.showSystemUiRestartDialog(getContext());
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HORNS;
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();
                     SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.horns_quicksettings;
                    result.add(sir);
                    return result;
                }
                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    return result;
                }
    };
}
