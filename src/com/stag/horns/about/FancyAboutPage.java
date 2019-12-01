/*
 * Copyright (C) 2018 StagOS Project
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

package com.stag.horns.about;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stag.horns.about.DiagonalView;

import com.android.settings.R;

public class FancyAboutPage extends RelativeLayout {
    private TextView l1,l2,phenomname,vjsname,pritishname,shekhawatname,abhimanyuname,vinothname,phenomdescription,vjsdescription,pritishdescription,shekhawatdescription,abhimanyudescription,vinothdescription;
    DiagonalView diagonalView;
    ImageView phenom,vjs,pritish,shekhawat,abhimanyu,vinoth,gg,tl,tw,git;
    String twitterurl,ggurl,tlurl,githuburl;
    private void init(Context context) {
        //do stuff that was in your original constructor...
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_fancy_about_page, this, true);
        l1=(TextView) findViewById(R.id.name);
        l2=(TextView) findViewById(R.id.description);
        phenomname=(TextView) findViewById(R.id.phenomname);
        vjsname=(TextView) findViewById(R.id.vjsname);
        pritishname=(TextView) findViewById(R.id.pritishname);
        shekhawatname=(TextView) findViewById(R.id.shekhawatname);
        abhimanyuname=(TextView) findViewById(R.id.abhimanyuname);
        vinothname=(TextView) findViewById(R.id.vinothname);
        phenomdescription=(TextView) findViewById(R.id.phenomdescription);
        vjsdescription=(TextView) findViewById(R.id.vjsdescription);
        pritishdescription=(TextView) findViewById(R.id.pritishdescription);
        shekhawatdescription=(TextView) findViewById(R.id.shekhawatdescription);
        abhimanyudescription=(TextView) findViewById(R.id.abhimanyudescription);
        vinothdescription=(TextView) findViewById(R.id.vinothdescription);
        phenom=(ImageView) findViewById(R.id.phenom);
        vjs=(ImageView) findViewById(R.id.vjs);
        pritish=(ImageView) findViewById(R.id.pritish);
        shekhawat=(ImageView) findViewById(R.id.shekhawat);
        abhimanyu=(ImageView) findViewById(R.id.abhimanyu);
        vinoth=(ImageView) findViewById(R.id.vinoth);
        tw=(ImageView) findViewById(R.id.twitter);
        gg=(ImageView) findViewById(R.id.google);
        tl=(ImageView) findViewById(R.id.telegram);
        git=(ImageView) findViewById(R.id.github);
        diagonalView = (DiagonalView) findViewById(R.id.background);
    }
    public FancyAboutPage(Context context) {
        super(context);
        init(context);
    }
    public FancyAboutPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public FancyAboutPage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs,defStyle);
        init(context);
    }
    public void setName(String name){
       l1.setText(name);
    }
    public void setDescription(String description){
       l2.setText(description);
    }
    public void setCoverTintColor(int color){
        diagonalView.setTintColor(color);
    }
    public void setCover(int drawable){
        diagonalView.setImageResource(drawable);
    }

    public void addTwitterLink(String twitterAddress){
        tw.setVisibility(VISIBLE);
        twitterurl=twitterAddress;
        tw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!twitterurl.startsWith("http://") && !twitterurl.startsWith("https://"))
                    twitterurl = "http://" + twitterurl;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterurl));
                getContext().startActivity(browserIntent);
            }
        });
    }
    public void addGoogleLink(String googleAddress){
        gg.setVisibility(VISIBLE);
        ggurl=googleAddress;
        gg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ggurl.startsWith("http://") && !ggurl.startsWith("https://")) {
                    ggurl = "http://" + ggurl;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ggurl));
                getContext().startActivity(browserIntent);
            }
        });

    }
    public void addTelegramLink(String telegramAddress){
        tl.setVisibility(VISIBLE);
        tlurl=telegramAddress;
        tl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tlurl.startsWith("http://") && !tlurl.startsWith("https://")) {
                    tlurl = "http://" + tlurl;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tlurl));
                getContext().startActivity(browserIntent);
            }
        });

    }
    public void addGitHubLink(String githubAddress){
        git.setVisibility(VISIBLE);
        githuburl=githubAddress;
        git.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!githuburl.startsWith("http://") && !githuburl.startsWith("https://"))
                    githuburl = "http://" + githuburl;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(githuburl));
                getContext().startActivity(browserIntent);
            }
        });

    }
    public void setAppIcon(int Icon){
       phenom.setImageResource(Icon);
       vjs.setImageResource(Icon);
       pritish.setImageResource(Icon);
       shekhawat.setImageResource(Icon);
       abhimanyu.setImageResource(Icon);
       vinoth.setImageResource(Icon);
    }
    public void setAppName(String AppName){
       phenomname.setText(AppName);
       vjsname.setText(AppName);
       pritishname.setText(AppName);
       shekhawatname.setText(AppName);
       abhimanyuname.setText(AppName);
       vinothname.setText(AppName);
    }
    public void setAppDescription(String AppDescription){
        phenomdescription.setText(AppDescription);
        vjsdescription.setText(AppDescription);
        pritishdescription.setText(AppDescription);
        shekhawatdescription.setText(AppDescription);
        abhimanyudescription.setText(AppDescription);
        vinothdescription.setText(AppDescription);
    }

}
