package com.stag.horns.fragments.playintegrity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Bundle;
import android.os.AsyncTask;
import android.provider.Settings;
import android.widget.Toast;
import android.os.SystemProperties;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.util.stag.StagUtils;

import java.io.BufferedReader;
import java.util.List;
import java.lang.reflect.Method;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayIntegrity extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return 0;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.playintegrity_settings, rootKey);

        getActivity().setTitle(R.string.play_integrity_dashboard_title);

        Preference setPropertiesPreference = findPreference("play_integrity_update");
        if (setPropertiesPreference != null) {
            setPropertiesPreference.setOnPreferenceClickListener(preference -> {
                setPropertiesFromUrl();
                return true;
            });
        }

        PreferenceCategory fingerprintCategory = findPreference("play_fingeprint_category");
        String keysList = SystemProperties.get("persist.sys.pihooks.gms.list");
        String[] keys = keysList.split("\\+");

        for (String key : keys) {
            String value = SystemProperties.get("persist.sys.pihooks.gms." + key);
            Preference preference = new Preference(getPreferenceManager().getContext());
            preference.setKey(key);
            preference.setTitle(key);
            preference.setSummary(value);
            preference.setSelectable(false);
            fingerprintCategory.addPreference(preference);
        }

    }

    public HashMap<String, String> extractProperties(String input) {
        HashMap<String, String> properties = new HashMap<>();
        Pattern arrayPattern = Pattern.compile("<string-array name=\"config_certifiedBuildProperties\" translatable=\"false\">(.*?)</string-array>", Pattern.DOTALL);
        Matcher arrayMatcher = arrayPattern.matcher(input);
        if (arrayMatcher.find()) {
            String arrayContents = arrayMatcher.group(1);
            Pattern itemPattern = Pattern.compile("<item>(.*?)</item>");
            Matcher itemMatcher = itemPattern.matcher(arrayContents);
            while (itemMatcher.find()) {
                String item = itemMatcher.group(1);
                String[] parts = item.split(":");
                if (parts.length == 2) {
                    properties.put(parts[0], parts[1]);
                }
                else if (parts.length > 2) {
                    String key = parts[0];
                    String value = String.join(":", Arrays.copyOfRange(parts, 1, parts.length));
                    properties.put(key, value);
                }
            }
        }
        return properties;
    }

    public void setPropertiesFromUrl() {
        new AsyncTask<Void, Void, String>() {    

            @Override
            protected String doInBackground(Void... voids) {
                StringBuilder keysBuilder = new StringBuilder();
                String message = "";
                try {
                    String urlString = getPreferenceManager().getContext().getResources()
                            .getString(R.string.play_integrity_certification_url);
                    URL url = new URL(urlString);                    
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String content = "";
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content += line;
                    }
                    reader.close();

                    HashMap<String, String> properties = extractProperties(content);
                    for (Map.Entry<String, String> entry : properties.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        SystemProperties.set("persist.sys.pihooks.gms." + key, value);
                        if (keysBuilder.length() > 0) {
                            keysBuilder.append("+");
                        }
                        keysBuilder.append(key);
                    }
                    SystemProperties.set("persist.sys.pihooks.gms.list", keysBuilder.toString());
                    message = "Applied properties, you should pass Play Integrity.";
                } catch (Exception e) {
                    e.printStackTrace();
                    message = "Failed to download spoofing properties.";
                }
                return message;
            }

            @Override
            protected void onPostExecute(String message) {
                killGmsProcess();
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                reloadPreferences();
            }
        }.execute();
    }

    public void reloadPreferences() {
        PreferenceCategory fingerprintCategory = findPreference("play_fingeprint_category");
        fingerprintCategory.removeAll();
        String keysList = SystemProperties.get("persist.sys.pihooks.gms.list");
        String[] keys = keysList.split("\\+");

        for (String key : keys) {
            String value = SystemProperties.get("persist.sys.pihooks.gms." + key);
            Preference preference = new Preference(getPreferenceManager().getContext());
            preference.setKey(key);
            preference.setTitle(key);
            preference.setSummary(value);
            preference.setSelectable(false);
            fingerprintCategory.addPreference(preference);
        }
    }

    public void killGmsProcess() {
        StagUtils.showPackageRestartDialog(getContext(), "com.google.android.gms");
    }
}