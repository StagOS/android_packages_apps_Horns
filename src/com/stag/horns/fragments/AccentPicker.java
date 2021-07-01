/*
 * Copyright (C) 2018 The Dirty Unicorns Project
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
import android.app.AlertDialog;
import android.app.Dialog;
import androidx.fragment.app.Fragment;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccentPicker extends InstrumentedDialogFragment implements OnClickListener {

    private static final String TAG_ACCENT_PICKER = "accent_color";

    private View mView;

    private static final String KEY_THEME = "theme";

    private MetricsFeatureProvider mMetricsFeatureProvider;
    private PackageManager mPackageManager;
    private IOverlayManager mOverlayManager;
    private int mCurrentUserId;

    Map<String, String> ACCENT_PRESETS = new HashMap<String, String>() {{
		 put("14", "FFFFC107");
		 put("20", "FF000000");
		 put("6", "FF448AFF");
		 put("19", "FF607D8B");
		 put("17", "FF795548");
		 put("37", "ffe52c5f");
		 put("33", "fff21a48");
		 put("34", "FFFF1744");
		 put("47", "ff2e86cd");
		 put("30", "ffefe3a8");
		 put("8", "FF00BCD4");
		 put("55", "ff97f456");
		 put("16", "FFFF5722");
		 put("4", "FF7C4DFF");
		 put("43", "ff7278e5");
		 put("39", "ffb7095a");
		 put("54", "FF21ef8b");
		 put("44", "ff3c5ae0");
		 put("46", "ff2856ff");
		 put("10", "FF4CAF50");
		 put("18", "FF9E9E9E");
		 put("36", "ffc83c64");
		 put("48", "ff327ccb");
		 put("50", "ff0ac8ff");
		 put("40", "ffa02963");
		 put("5", "FF536DFE");
		 put("56", "FF9ABC98");
		 put("7", "FF03A9F4");
		 put("11", "FF8BC34A");
		 put("12", "FFCDDC39");
		 put("29", "fff6ca11");
		 put("53", "ff46ff99");
		 put("31", "fff25555");
		 put("42", "ff8522ff");
		 put("45", "ff0042ba");
		 put("15", "FFFF9800");
		 put("49", "FFA1B6ED");
		 put("35", "FFF05361");
		 put("2", "FFFF4081");
		 put("3", "FFE040FB");
		 put("1", "FFFF5252");
		 put("52", "ff01c18c");
		 put("41", "ff822b6b");
		 put("51", "ff009688");
		 put("9", "FF009688");
		 put("38", "ffae2463");
		 put("26", "FF0EE898");
		 put("25", "FF42f4aa");
		 put("22", "FF900000");
		 put("28", "FFFFCC00");
		 put("27", "FFFFC27B");
		 put("24", "FF1976D2");
		 put("23", "FF42069a");
		 put("32", "ffff6f20");
		 put("white", "FFFFFFFF");
		 put("13", "FFFFEB3B");
    }};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOverlayManager = IOverlayManager.Stub.asInterface(
                ServiceManager.getService(Context.OVERLAY_SERVICE));
        mCurrentUserId = ActivityManager.getCurrentUser();
		mPackageManager = getContext().getPackageManager();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.accent_picker, null);

        if (mView != null) {
            initView();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mView)
                .setNegativeButton(R.string.cancel, this)
                .setNeutralButton(R.string.theme_accent_picker_default, this)
                .setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    private void initView() {

        Button redAccent = mView.findViewById(R.id.redAccent);
        setAccent("1", redAccent);

        Button pinkAccent = mView.findViewById(R.id.pinkAccent);
        setAccent("2", pinkAccent);

        Button purpleAccent = mView.findViewById(R.id.purpleAccent);
        setAccent("3", purpleAccent);

        Button deeppurpleAccent = mView.findViewById(R.id.deeppurpleAccent);
        setAccent("4", deeppurpleAccent);

        Button indigoAccent = mView.findViewById(R.id.indigoAccent);
        setAccent("5", indigoAccent);

        Button blueAccent = mView.findViewById(R.id.blueAccent);
        setAccent("6", blueAccent);

        Button lightblueAccent = mView.findViewById(R.id.lightblueAccent);
        setAccent("7", lightblueAccent);

        Button cyanAccent = mView.findViewById(R.id.cyanAccent);
        setAccent("8", cyanAccent);

        Button tealAccent = mView.findViewById(R.id.tealAccent);
        setAccent("9", tealAccent);

        Button greenAccent = mView.findViewById(R.id.greenAccent);
        setAccent("10", greenAccent);

        Button lightgreenAccent = mView.findViewById(R.id.lightgreenAccent);
        setAccent("11", lightgreenAccent);

        Button limeAccent = mView.findViewById(R.id.limeAccent);
        setAccent("12", limeAccent);

        Button yellowAccent = mView.findViewById(R.id.yellowAccent);
        setAccent("13", yellowAccent);

        Button amberAccent = mView.findViewById(R.id.amberAccent);
        setAccent("14", amberAccent);

        Button orangeAccent = mView.findViewById(R.id.orangeAccent);
        setAccent("15", orangeAccent);

        Button deeporangeAccent = mView.findViewById(R.id.deeporangeAccent);
        setAccent("16", deeporangeAccent);

        Button brownAccent = mView.findViewById(R.id.brownAccent);
        setAccent("17", brownAccent);

        Button greyAccent = mView.findViewById(R.id.greyAccent);
        setAccent("18", greyAccent);

        Button bluegreyAccent = mView.findViewById(R.id.bluegreyAccent);
        setAccent("19", bluegreyAccent);

        Button blackAccent = mView.findViewById(R.id.blackAccent);

        int currentNightMode = getActivity().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                blackAccent.setBackgroundColor(getResources().getColor(R.color.accent_picker_dark_accent));
                blackAccent.setBackgroundTintList(getResources().getColorStateList(R.color.accent_picker_dark_accent));
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                blackAccent.setBackgroundColor(getResources().getColor(R.color.accent_picker_white_accent));
                blackAccent.setBackgroundTintList(getResources().getColorStateList(R.color.accent_picker_white_accent));
                break;
        }

        setAccent("20", blackAccent);

        Button userAccentOne = mView.findViewById(R.id.userAccentOne);
        setAccent("22", userAccentOne);

        Button userAccentTwo = mView.findViewById(R.id.userAccentTwo);
        setAccent("23", userAccentTwo);

        Button userAccentThree = mView.findViewById(R.id.userAccentThree);
        setAccent("24", userAccentThree);

        Button userAccentFour = mView.findViewById(R.id.userAccentFour);
        setAccent("25", userAccentFour);

        Button userAccentFive = mView.findViewById(R.id.userAccentFive);
        setAccent("26", userAccentFive);

        Button userAccentSix = mView.findViewById(R.id.userAccentSix);
        setAccent("27", userAccentSix);

        Button userAccentSeven = mView.findViewById(R.id.userAccentSeven);
        setAccent("28", userAccentSeven);

        Button maniaamberAccent = mView.findViewById(R.id.maniaamberAccent);
        setAccent("29", maniaamberAccent);

        Button coldyellowAccent = mView.findViewById(R.id.coldyellowAccent);
        setAccent("30", coldyellowAccent);

        Button newhouseorangeAccent = mView.findViewById(R.id.newhouseorangeAccent);
        setAccent("31", newhouseorangeAccent);

        Button warmthorangeAccent = mView.findViewById(R.id.warmthorangeAccent);
        setAccent("32", warmthorangeAccent);

        Button burningredAccent = mView.findViewById(R.id.burningredAccent);
        setAccent("33", burningredAccent);

        Button candyredAccent = mView.findViewById(R.id.candyredAccent);
        setAccent("34", candyredAccent);

        Button paleredAccent = mView.findViewById(R.id.paleredAccent);
        setAccent("35", paleredAccent);

        Button hazedpinkAccent = mView.findViewById(R.id.hazedpinkAccent);
        setAccent("36", hazedpinkAccent);

        Button bubblegumpinkAccent = mView.findViewById(R.id.bubblegumpinkAccent);
        setAccent("37", bubblegumpinkAccent);

        Button trufilpinkAccent = mView.findViewById(R.id.trufilpinkAccent);
        setAccent("38", trufilpinkAccent);

        Button duskpurpleAccent = mView.findViewById(R.id.duskpurpleAccent);
        setAccent("39", duskpurpleAccent);

        Button illusionspurpleAccent = mView.findViewById(R.id.illusionspurpleAccent);
        setAccent("40", illusionspurpleAccent);

        Button spookedpurpleAccent = mView.findViewById(R.id.spookedpurpleAccent);
        setAccent("41", spookedpurpleAccent);

        Button notimppurpleAccent = mView.findViewById(R.id.notimppurpleAccent);
        setAccent("42", notimppurpleAccent);

        Button dreamypurpleAccent = mView.findViewById(R.id.dreamypurpleAccent);
        setAccent("43", dreamypurpleAccent);

        Button footprintpurpleAccent = mView.findViewById(R.id.footprintpurpleAccent);
        setAccent("44", footprintpurpleAccent);

        Button obfusbleuAccent = mView.findViewById(R.id.obfusbleuAccent);
        setAccent("45", obfusbleuAccent);

        Button frenchbleuAccent = mView.findViewById(R.id.frenchbleuAccent);
        setAccent("46", frenchbleuAccent);

        Button coldbleuAccent = mView.findViewById(R.id.coldbleuAccent);
        setAccent("47", coldbleuAccent);

        Button heirloombleuAccent = mView.findViewById(R.id.heirloombleuAccent);
        setAccent("48", heirloombleuAccent);

        Button paleblueAccent = mView.findViewById(R.id.paleblueAccent);
        setAccent("49", paleblueAccent);

        Button holillusionAccent = mView.findViewById(R.id.holillusionAccent);
        setAccent("50", holillusionAccent);

        Button stockAccent = mView.findViewById(R.id.stockAccent);
        setAccent("51", stockAccent);

        Button seasidemintAccent = mView.findViewById(R.id.seasidemintAccent);
        setAccent("52", seasidemintAccent);

        Button movemintAccent = mView.findViewById(R.id.movemintAccent);
        setAccent("53", movemintAccent);

        Button extendedgreenAccent = mView.findViewById(R.id.extendedgreenAccent);
        setAccent("54", extendedgreenAccent);

        Button diffdaygreenAccent = mView.findViewById(R.id.diffdaygreenAccent);
        setAccent("55", diffdaygreenAccent);

        Button jadegreenAccent = mView.findViewById(R.id.jadegreenAccent);
        setAccent("56", jadegreenAccent);
		
		Button bluesky = mView.findViewById(R.id.bluesky);
    	setGradient("com.revengeos.gradient.bluesky", bluesky);

		Button cherry = mView.findViewById(R.id.cherry);
    	setGradient("com.revengeos.gradient.cherry", cherry);

		Button deepskyline = mView.findViewById(R.id.deepskyline);
    	setGradient("com.revengeos.gradient.deepskyline", deepskyline);

		Button deepsunset = mView.findViewById(R.id.deepsunset);
    	setGradient("com.revengeos.gradient.deepsunset", deepsunset);

		Button gradientcolor_default = mView.findViewById(R.id.gradientcolor_default);
    	setGradient("com.revengeos.gradient.default", gradientcolor_default);

		Button flare = mView.findViewById(R.id.flare);
    	setGradient("com.revengeos.gradient.flare", flare);

		Button grapevine = mView.findViewById(R.id.grapevine);
    	setGradient("com.revengeos.gradient.grapevine", grapevine);

		Button hyakkimaru = mView.findViewById(R.id.hyakkimaru);
    	setGradient("com.revengeos.gradient.hyakkimaru", hyakkimaru);

		Button kyemeh = mView.findViewById(R.id.kyemeh);
    	setGradient("com.revengeos.gradient.kyemeh", kyemeh);

		Button lavender = mView.findViewById(R.id.lavender);
    	setGradient("com.revengeos.gradient.lavender", lavender);

		Button lightseastorm = mView.findViewById(R.id.lightseastorm);
    	setGradient("com.revengeos.gradient.lightss", lightseastorm);

		Button orangecoral = mView.findViewById(R.id.orangecoral);
    	setGradient("com.revengeos.gradient.orangecoral", orangecoral);

		Button peachy = mView.findViewById(R.id.peachy);
    	setGradient("com.revengeos.gradient.peachy", peachy);

		Button polargreen = mView.findViewById(R.id.polargreen);
    	setGradient("com.revengeos.gradient.polargreen", polargreen);

		Button purelust = mView.findViewById(R.id.purelust);
    	setGradient("com.revengeos.gradient.purelust", purelust);

		Button quepal = mView.findViewById(R.id.quepal);
    	setGradient("com.revengeos.gradient.quepal", quepal);

		Button rea = mView.findViewById(R.id.rea);
    	setGradient("com.revengeos.gradient.rea", rea);

		Button seastorm = mView.findViewById(R.id.seastorm);
    	setGradient("com.revengeos.gradient.seastorm", seastorm);

		Button shadesofgrey = mView.findViewById(R.id.shadesofgrey);
    	setGradient("com.revengeos.gradient.shadesofgrey", shadesofgrey);

		Button sincityred = mView.findViewById(R.id.sincityred);
    	setGradient("com.revengeos.gradient.sincityred", sincityred);

		Button skyline = mView.findViewById(R.id.skyline);
    	setGradient("com.revengeos.gradient.skyline", skyline);

		Button sublime = mView.findViewById(R.id.sublime);
    	setGradient("com.revengeos.gradient.sublime", sublime);

		Button timber = mView.findViewById(R.id.timber);
    	setGradient("com.revengeos.gradient.timber", timber);

		Button yoda = mView.findViewById(R.id.yoda);
    	setGradient("com.revengeos.gradient.yoda", yoda);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (which == AlertDialog.BUTTON_NEGATIVE) {
            dismiss();
        }
        if (which == AlertDialog.BUTTON_NEUTRAL) {
            Settings.System.putIntForUser(resolver,
                    Settings.System.ACCENT_COLOR, 0, mCurrentUserId);
            dismiss();
        }
    }

    public static void show(Fragment parent) {
        if (!parent.isAdded()) return;

        final AccentPicker dialog = new AccentPicker();
        dialog.setTargetFragment(parent, 0);
        dialog.show(parent.getFragmentManager(), TAG_ACCENT_PICKER);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HORNS;
    }


    public static int convertToColorInt(String argb) throws NumberFormatException {

        if (argb.startsWith("#")) {
            argb = argb.replace("#", "");
        }

        int alpha = -1, red = -1, green = -1, blue = -1;

        if (argb.length() == 8) {
            alpha = Integer.parseInt(argb.substring(0, 2), 16);
            red = Integer.parseInt(argb.substring(2, 4), 16);
            green = Integer.parseInt(argb.substring(4, 6), 16);
            blue = Integer.parseInt(argb.substring(6, 8), 16);
        }
        else if (argb.length() == 6) {
            alpha = 255;
            red = Integer.parseInt(argb.substring(0, 2), 16);
            green = Integer.parseInt(argb.substring(2, 4), 16);
            blue = Integer.parseInt(argb.substring(4, 6), 16);
        }

        return Color.argb(alpha, red, green, blue);
    }

    private void setAccent(final String accent, final Button buttonAccent) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (buttonAccent != null) {
            buttonAccent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
		    int intHex = convertToColorInt(ACCENT_PRESETS.get(accent));
                    Settings.System.putIntForUser(resolver,
                            Settings.System.ACCENT_COLOR, intHex, mCurrentUserId);
                    dismiss();
                }
            });
        }
    }
	
	private void setGradient(final String gradient, final Button buttonGradient) {
        final ContentResolver resolver = getActivity().getContentResolver();
		if (buttonGradient != null) {
            buttonGradient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
					String current = getCurrentTheme();
					if (Objects.equals(gradient, current)) {
						dismiss();
					}
					try {
						mOverlayManager.setEnabledExclusiveInCategory((String) gradient, UserHandle.myUserId());
					} catch (RemoteException re) {
						throw re.rethrowFromSystemServer();
					}
					dismiss();
				}
			});
		}
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
	
    String getCurrentTheme() {
        String[] themePackages = getAvailableThemes(true /* currentThemeOnly */);
        return themePackages.length < 1 ? null : themePackages[0];
    }

	String[] getAvailableThemes(boolean currentThemeOnly) {
        List<OverlayInfo> infos;
        List<String> pkgs;
        try {
            infos = mOverlayManager.getOverlayInfosForTarget("android", UserHandle.myUserId());
            pkgs = new ArrayList<>(infos.size());
            for (int i = 0, size = infos.size(); i < size; i++) {
                if (isTheme(infos.get(i))) {
                    if (infos.get(i).isEnabled() && currentThemeOnly) {
                        return new String[] {infos.get(i).packageName};
                    } else {
                        pkgs.add(infos.get(i).packageName);
                    }
                }
            }
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }

        // Current enabled theme is not found.
        if (currentThemeOnly) {
            return new String[0];
        }
        return pkgs.toArray(new String[pkgs.size()]);
    }
}
