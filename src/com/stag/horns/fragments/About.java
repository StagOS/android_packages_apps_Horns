/*
 * Copyright (C) 2019 Stag-OS Project
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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.preference.ListPreference;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.graphics.Color;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.Utils;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.stag.horns.about.CircularImageView;
import com.stag.horns.about.FancyAboutPage;

public class About extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "About";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ContentResolver resolver = getActivity().getContentResolver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about, container, false);
        FancyAboutPage fancyAboutPage = (FancyAboutPage)view.findViewById(R.id.fancyaboutpage);
	CircularImageView circularImageView = view.findViewById(R.id.circularImageView);

        int currentNightMode = getActivity().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
		circularImageView.setImageResource(R.drawable.logo_white);
                fancyAboutPage.setCover(R.drawable.banner_white);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
		circularImageView.setImageResource(R.drawable.logo_dark);
                fancyAboutPage.setCover(R.drawable.banner_dark);
                break;
        }

        fancyAboutPage.addTwitterLink("https://twitter.com/");
        fancyAboutPage.addGoogleLink("https://plus.google.com/");
        fancyAboutPage.addTelegramLink("https://t.me/HornsOfficial");
        fancyAboutPage.addGitHubLink("https://github.com/StagOS");
        
        
        // TextView textView = (TextView) view.findViewById(R.id.maintainers);
        CardView cardView = (CardView) view.findViewById(R.id.maintainers);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Meet the maintainers", Toast.LENGTH_SHORT).show();
                DeviceMaintainersFragment nextFrag = new DeviceMaintainersFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup)getView().getParent()).getId(), nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
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
        final String key = preference.getKey();
        return false;
    }

}
