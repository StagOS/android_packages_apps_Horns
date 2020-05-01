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
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;
import com.stag.horns.preferences.SystemSettingSeekBarPreference;

import com.android.internal.logging.nano.MetricsProto;

import com.stag.horns.preferences.CustomSeekBarPreference;
import com.stag.horns.preferences.SystemSettingMasterSwitchPreference;
import com.stag.horns.preferences.SystemSettingSwitchPreference;

import java.util.ArrayList;
import java.util.List;

import com.android.internal.util.stag.StagUtils;

import com.stag.horns.preferences.SystemSettingEditTextPreference;

public class QuickSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String KEY_QS_PANEL_ALPHA = "qs_panel_alpha";
    private static final String STATUS_BAR_CUSTOM_HEADER = "status_bar_custom_header";
    private static final String FOOTER_TEXT_STRING = "footer_text_string";
    private static final String PREF_QSBG_NEW_TINT = "qs_panel_bg_use_new_tint";

    private SystemSettingEditTextPreference mFooterString;
    private CustomSeekBarPreference mQsPanelAlpha;
    private SystemSettingMasterSwitchPreference mCustomHeader;
    private SystemSettingSwitchPreference mQsBgNewTint;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.horns_quicksettings);
        PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mQsPanelAlpha = (CustomSeekBarPreference) findPreference(KEY_QS_PANEL_ALPHA);
        int qsPanelAlpha = Settings.System.getInt(getContentResolver(),
                Settings.System.OMNI_QS_PANEL_BG_ALPHA, 221);
        mQsPanelAlpha.setValue((int)(((double) qsPanelAlpha / 255) * 100));
        mQsPanelAlpha.setOnPreferenceChangeListener(this);

        mCustomHeader = (SystemSettingMasterSwitchPreference) findPreference(STATUS_BAR_CUSTOM_HEADER);
        int qsHeader = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_CUSTOM_HEADER, 0);
        mCustomHeader.setChecked(qsHeader != 0);
        mCustomHeader.setOnPreferenceChangeListener(this);

        mFooterString = (SystemSettingEditTextPreference) findPreference(FOOTER_TEXT_STRING);
        mFooterString.setOnPreferenceChangeListener(this);
        String footerString = Settings.System.getString(getContentResolver(),
                FOOTER_TEXT_STRING);
        if (TextUtils.isEmpty(footerString) || footerString == null) {
            mFooterString.setText("#LetsAIMify");
            Settings.System.putString(getActivity().getContentResolver(),
                    Settings.System.FOOTER_TEXT_STRING, "#LetsAIMify");
        } else {
            mFooterString.setText(footerString);
        }

        mQsBgNewTint = (SystemSettingSwitchPreference) findPreference(PREF_QSBG_NEW_TINT);
        mQsBgNewTint.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.QS_PANEL_BG_USE_NEW_TINT, 1) == 1));
        mQsBgNewTint.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mQsPanelAlpha) {
            int bgAlpha = (Integer) newValue;
            int trueValue = (int) (((double) bgAlpha / 100) * 255);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.OMNI_QS_PANEL_BG_ALPHA, trueValue);
            return true;
        } else if (preference == mCustomHeader) {
            boolean header = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_CUSTOM_HEADER, header ? 1 : 0);
            return true;
	} else if (preference == mFooterString) {
            String value = (String) newValue;
            if (TextUtils.isEmpty(value) || value == null) {
                mFooterString.setText("#LetsStagify");
                Settings.System.putString(getActivity().getContentResolver(),
                        Settings.System.FOOTER_TEXT_STRING, "#LetsStagify");
            } else {
                Settings.System.putString(getActivity().getContentResolver(),
                        Settings.System.FOOTER_TEXT_STRING, value);
            }
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
