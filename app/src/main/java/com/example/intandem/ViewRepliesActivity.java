package com.example.intandem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.example.intandem.models.Post;
import com.example.intandem.models.Reply;
import com.example.intandem.models.Review;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ViewRepliesActivity extends AppCompatActivity {

    public static final String TAG = "ViewRepliesActivity";
    public static final int LIMIT = 20;
    private List<Reply> allReplies;
    private RecyclerView rvReplies;
    private RepliesAdapter adapter;
    private Post currPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_replies);

        rvReplies = findViewById(R.id.rvReplies);

        allReplies = new ArrayList<>();
        adapter = new RepliesAdapter(this, allReplies);

        Bundle extras = getIntent().getExtras();
        currPost = extras.getParcelable("currPost");
        rvReplies.setAdapter(adapter);
        rvReplies.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));

        queryReplies();
    }

    private void queryReplies() {
        ParseQuery<Reply> query = ParseQuery.getQuery(Reply.class);
        query.setLimit(LIMIT);
        // only grab replies for that specific post
        query.whereEqualTo(Reply.KEY_POST, currPost);
        query.findInBackground(new FindCallback<Reply>() {
            @Override
            public void done(List<Reply> replies, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issue getting replies");
                }
                for (Reply reply : replies) {
                    Log.i(TAG, reply.getCaption());
                }
                allReplies.addAll(replies);
                adapter.notifyDataSetChanged();
            }
        });
    }
}