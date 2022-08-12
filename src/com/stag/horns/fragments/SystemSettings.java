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
import android.widget.Toast;
import android.os.UserHandle;
import android.os.Process;
import android.util.Log;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import com.android.internal.util.stag.udfps.UdfpsUtils;
import com.android.internal.util.stag.StagUtils;

import com.stag.horns.fragments.DozeSettings;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.stag.horns.preferences.HornsUtils;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class SystemSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String TAG = "SystemSettings";
    private static final String UDFPS_SETTINGS = "udfps_settings";
    private static final String SCREEN_RESOLUTION_SETTINGS = "screen_resolution";

    private ListPreference mScreenResolution;

    // Create a map of screen resolutions to their respective values
    // 0 - "auto"
    // 1 - "720"
    // 2 - "1080"
    // 3 - "1440"
    // 4 - "2160"
    private static final String[] SCREEN_RESOLUTION_VALUES = new String[] {
            "auto", "720", "1080", "1440", "2160"
    };
    // List of generic dpis
    private static final String[] SCREEN_RESOLUTION_DPIS = new String[] {
            "auto", "320", "420", "540", "640"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.horns_system);
        ContentResolver resolver = getActivity().getContentResolver();
        Context mContext = getContext();

        final PreferenceScreen prefScreen = getPreferenceScreen();

        if (!UdfpsUtils.hasUdfpsSupport(getContext())) {
            prefScreen.removePreference(findPreference(UDFPS_SETTINGS));
        }

        //  Check if config_screen_resolution_changing_supported is set to true from resources
        mScreenResolution = (ListPreference) findPreference(SCREEN_RESOLUTION_SETTINGS);
        final boolean isScreenResolutionChangingSupported = mContext.getResources().getBoolean(
                R.bool.config_screen_resolution_changing_supported);
        if (!isScreenResolutionChangingSupported) {
            prefScreen.removePreference(mScreenResolution);
        }else{
            // Get value of config_screen_resolution_max
            final int screenResolutionMax = mContext.getResources().getInteger(
                    R.integer.config_screen_resolution_max);
            // Set entries and values for screen resolution list preference
            // 1: "screen_resolution_entries_hd"
            // 2: "screen_resolution_entries_fhd"
            // 3: "screen_resolution_entries_qhd"
            // 4: "screen_resolution_entries_wqhd"
            // And the respective values
            switch (screenResolutionMax) {
                case 1:
                    mScreenResolution.setEntries(R.array.screen_resolution_entries_hd);
                    mScreenResolution.setEntryValues(R.array.screen_resolution_values_hd);
                    break;
                case 2:
                    mScreenResolution.setEntries(R.array.screen_resolution_entries_fhd);
                    mScreenResolution.setEntryValues(R.array.screen_resolution_values_fhd);
                    break;
                case 3:
                    mScreenResolution.setEntries(R.array.screen_resolution_entries_qhd);
                    mScreenResolution.setEntryValues(R.array.screen_resolution_values_qhd);
                    break;
                case 4:
                    mScreenResolution.setEntries(R.array.screen_resolution_entries_wqhd);
                    mScreenResolution.setEntryValues(R.array.screen_resolution_values_wqhd);
                    break;
                default:
                    mScreenResolution.setEntries(R.array.screen_resolution_entries_fhd);
                    mScreenResolution.setEntryValues(R.array.screen_resolution_values_fhd);
                    break;
            }
            mScreenResolution.setOnPreferenceChangeListener(this);
            int screen_resolution_value = Settings.System.getInt(resolver,
                    Settings.System.CUSTOM_SCREEN_RESOLUTION, 0);
            mScreenResolution.setValue(String.valueOf(screen_resolution_value));
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HORNS;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "onPreferenceChange: " + preference.getKey());
        Log.d(TAG, "onPreferenceChange: " + newValue);
		ContentResolver resolver = getActivity().getContentResolver();
        if(preference == mScreenResolution){
            Log.d(TAG, "Preference is screen resolution");
            // If new value is "auto", raise a toast not supported
            if (newValue.toString().equals("auto")) {
                Toast.makeText(getContext(), getString(R.string.screen_resolution_not_supported), Toast.LENGTH_LONG).show();
                return false;
            }
            int value = Integer.parseInt((String) newValue);
            Settings.System.putInt(resolver, Settings.System.CUSTOM_SCREEN_RESOLUTION, value);
            // Get value config_screen_diagonal_length if it is set
            final String screenDiagonalLength = getResources().getString(
                    R.string.config_screen_diagonal_length);
	    WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
	    DisplayMetrics displayMetrics = new DisplayMetrics();
	    wm.getDefaultDisplay().getRealMetrics(displayMetrics);
	    int screenHeight = displayMetrics.heightPixels;
	    int screenWidth = displayMetrics.widthPixels;
	    float ratio = (float) ((float) screenWidth/screenHeight);
            Log.d(TAG, "Detected h,w new: " + Integer.toString(screenHeight) + "," + Integer.toString(screenWidth));
            Log.d(TAG, "Detected ratio new: " + Float.toString(ratio));
            // Convert to float if config_screen_diagonal_length is set
            if (screenDiagonalLength != null) {
                float screenDiagonalLengthFloat = Float.parseFloat(screenDiagonalLength);
                Log.d(TAG, "Calling changeScreenResolution with screenDiagonalLengthFloat: " + screenDiagonalLengthFloat);
                StagUtils.changeScreenResolution(SCREEN_RESOLUTION_VALUES[value], screenDiagonalLengthFloat, ratio);
            }else{
                Log.d(TAG, "Calling changeScreenResolution with value: " + value);
                StagUtils.changeScreenResolution(SCREEN_RESOLUTION_VALUES[value], SCREEN_RESOLUTION_DPIS[value], ratio);
            }
	    restartSystemUI(getContext());
            return true;
        }
        return false;
    }

    public static void restartSystemUI(Context ctx) {
        Process.killProcess(Process.myPid());
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
