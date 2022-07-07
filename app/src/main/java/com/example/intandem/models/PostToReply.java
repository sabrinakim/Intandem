package com.example.intandem.models;

import com.example.intandem.models.Post;
import com.example.intandem.models.Reply;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("PostToReply")
public class PostToReply extends ParseObject {

    public static final String KEY_POST = "post";
    public static final String KEY_REPLY = "reply";

    public PostToReply() {}

    public Post getPost() {
        return (Post) getParseObject(KEY_POST);
    }

    public void setPost(Post post) {
        put(KEY_POST, post);
    }

    public Reply getReply() {
        return (Reply) getParseObject(KEY_REPLY);
    }

    public void setReply(Reply reply) {
        put(KEY_REPLY, reply);
    }
}
