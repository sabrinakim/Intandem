package com.example.intandem;

import android.app.Application;

import com.example.intandem.models.Friendship;
import com.example.intandem.models.Post;
import com.example.intandem.models.PostToReply;
import com.example.intandem.models.Reply;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Reply.class);
        ParseObject.registerSubclass(PostToReply.class);
        ParseObject.registerSubclass(Friendship.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("xLojvhrKsyXJdPivN9v0podIw2MjQvJ9GFfmUP3i")
                .clientKey("DvTnslQVFZ9FrLKVvPDy9l94pFI0nY1EQOrQgTK6")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
