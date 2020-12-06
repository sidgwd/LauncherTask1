package com.sid.jiolauncher.ApplicationFont;

import android.app.Application;

import com.sid.jiolauncher.R;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;


public class CustomApplicationFont extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // initalize Calligraphy
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("Lato-Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());


    }

}
