package com.example.intandem;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("xLojvhrKsyXJdPivN9v0podIw2MjQvJ9GFfmUP3i")
                .clientKey("DvTnslQVFZ9FrLKVvPDy9l94pFI0nY1EQOrQgTK6")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
