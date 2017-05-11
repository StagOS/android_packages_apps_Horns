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

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.ContentResolver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.SystemProperties;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.wrapper.OverlayManagerWrapper;
import com.android.settings.wrapper.OverlayManagerWrapper.OverlayInfo;
import com.stag.horns.preferences.CustomSeekBarPreference;
import com.stag.horns.preferences.SecureSettingSwitchPreference;
import com.stag.horns.preferences.SystemSettingSwitchPreference;
import com.stag.horns.utils.TelephonyUtils;

import com.stag.horns.R;


import java.util.ArrayList;
import java.util.List;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class SystemSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "System";
    private static final String ACCENT_COLOR = "accent_color";
    private static final String ACCENT_COLOR_PROP = "persist.sys.theme.accentcolor";
    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";

    private Handler mHandler;

    private ColorPickerPreference mThemeColor;
    private Fragment mCurrentFragment = this;
    private OverlayManagerWrapper mOverlayService;
    private PackageManager mPackageManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.horns_system);
        mOverlayService = ServiceManager.getService(Context.OVERLAY_SERVICE) != null ? new OverlayManagerWrapper()
                : null;
        mPackageManager = getActivity().getPackageManager();
	mHandler = new Handler();
        ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen prefSet = getPreferenceScreen();

        final PreferenceScreen prefScreen = getPreferenceScreen();

        setupAccentPref();

        boolean enableSmartPixels = getContext().getResources().
                getBoolean(com.android.internal.R.bool.config_enableSmartPixels);
        Preference SmartPixels = findPreference("smart_pixels");

        if (!enableSmartPixels){
            prefSet.removePreference(SmartPixels);
        }

        PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS);
        if (!TelephonyUtils.isVoiceCapable(getActivity())) {
                prefSet.removePreference(incallVibCategory);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HORNS;
    }

    private void setupAccentPref() {
        mThemeColor = (ColorPickerPreference) findPreference(ACCENT_COLOR);
        String colorVal = SystemProperties.get(ACCENT_COLOR_PROP, "-1");
        int color = "-1".equals(colorVal)
                ? Color.WHITE
                : Color.parseColor("#" + colorVal);
        mThemeColor.setNewPreviewColor(color);
        mThemeColor.setOnPreferenceChangeListener(this);
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
	if (preference == mThemeColor) {
            int color = (Integer) newValue;
            String hexColor = String.format("%08X", (0xFFFFFFFF & color));
            SystemProperties.set(ACCENT_COLOR_PROP, hexColor);
            mOverlayService.reloadAndroidAssets(UserHandle.USER_CURRENT);
            mOverlayService.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
            mOverlayService.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT); 
       return true;
	}
       return false;
    }

    private boolean isTheme(OverlayInfo oi) {
        if (!OverlayInfo.CATEGORY_THEME.equals(oi.category)) {
            return false;
        }
        try {
            PackageInfo pi = mPackageManager.getPackageInfo(oi.packageName, 0);
            return pi != null && !pi.isStaticOverlayPackage();
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
