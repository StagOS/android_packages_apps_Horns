/*
 * Copyright (C) 2016 The Pure Nexus Project
 * used for Stag OS
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

package com.stag.horns;

import com.android.internal.logging.nano.MetricsProto;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Surface;
import androidx.preference.Preference;
import com.android.settings.R;

import com.android.settings.SettingsPreferenceFragment;

public class Horns extends SettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.horns);

        Preference SystemCategory = findPreference("system_category");
        if (!getResources().getBoolean(R.bool.has_system_category))
            getPreferenceScreen().removePreference(SystemCategory);

	Preference StatusbarCategory = findPreference("statusbar_category");
	if (!getResources().getBoolean(R.bool.has_statusbar_category))
            getPreferenceScreen().removePreference(StatusbarCategory);

        Preference QSCategory = findPreference("quicksettings_category");
        if (!getResources().getBoolean(R.bool.has_qs_category))
            getPreferenceScreen().removePreference(QSCategory);

        Preference ButtonsCategory = findPreference("buttonsettings_category");
        if (!getResources().getBoolean(R.bool.has_buttons_category))
            getPreferenceScreen().removePreference(ButtonsCategory);

        Preference NavbarCategory = findPreference("navigationbar_category");
        if (!getResources().getBoolean(R.bool.has_navbar_category))
            getPreferenceScreen().removePreference(NavbarCategory);

        Preference AnimationsCategory = findPreference("animations_category");
        if (!getResources().getBoolean(R.bool.has_animations_category))
            getPreferenceScreen().removePreference(AnimationsCategory);

        Preference GesturesCategory = findPreference("gestures_category");
        if (!getResources().getBoolean(R.bool.has_gestures_category))
            getPreferenceScreen().removePreference(GesturesCategory);

        Preference LockscreenCategory = findPreference("lockscreen_category");
        if (!getResources().getBoolean(R.bool.has_lockscreen_category))
            getPreferenceScreen().removePreference(LockscreenCategory);

        Preference PowermenuCategory = findPreference("powermenu_category");
        if (!getResources().getBoolean(R.bool.has_powermenu_category))
            getPreferenceScreen().removePreference(PowermenuCategory);

        Preference NotificationsCategory = findPreference("notifications_category");
        if (!getResources().getBoolean(R.bool.has_notifications_category))
            getPreferenceScreen().removePreference(NotificationsCategory);

        Preference MiscCategory = findPreference("misc_category");
        if (!getResources().getBoolean(R.bool.has_misc_category))
            getPreferenceScreen().removePreference(MiscCategory);

    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HORNS;
    }

    public static void lockCurrentOrientation(Activity activity) {
        int currentRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int orientation = activity.getResources().getConfiguration().orientation;
        int frozenRotation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        switch (currentRotation) {
            case Surface.ROTATION_0:
                frozenRotation = orientation == Configuration.ORIENTATION_LANDSCAPE
                        ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Surface.ROTATION_90:
                frozenRotation = orientation == Configuration.ORIENTATION_PORTRAIT
                        ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_180:
                frozenRotation = orientation == Configuration.ORIENTATION_LANDSCAPE
                        ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            case Surface.ROTATION_270:
                frozenRotation = orientation == Configuration.ORIENTATION_PORTRAIT
                        ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
        }
        activity.setRequestedOrientation(frozenRotation);
    }
}
