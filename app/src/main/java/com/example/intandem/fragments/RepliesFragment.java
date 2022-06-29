package com.example.intandem.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.intandem.Post;
import com.example.intandem.R;
import com.example.intandem.RepliesAdapter;
import com.example.intandem.Reply;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RepliesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RepliesFragment extends Fragment {

    public static final int LIMIT = 20;
    public static final String TAG = "RepliesFragment";
    private static final String ARG_CURR_USER = "param1";
    private RecyclerView rvReplies;
    private List<Reply> allReplies;
    private RepliesAdapter adapter;

    // TODO: Rename and change types of parameters
    private ParseUser mCurrUser;

    public RepliesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1
     * @return A new instance of fragment RepliesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RepliesFragment newInstance(ParseUser param1) {
        RepliesFragment fragment = new RepliesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CURR_USER, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrUser = getArguments().getParcelable(ARG_CURR_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_replies, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvReplies = view.findViewById(R.id.rvReplies);
        allReplies = new ArrayList<>();
        adapter = new RepliesAdapter(getContext(), allReplies);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvReplies.setAdapter(adapter);
        rvReplies.setLayoutManager(linearLayoutManager);
        queryReplies();
    }

    private void queryReplies() {
        ParseQuery<Reply> query = ParseQuery.getQuery(Reply.class);
        // we only want to query for replies for that specific post we clicked on.
        query.setLimit(LIMIT);
        query.findInBackground(new FindCallback<Reply>() {
            @Override
            public void done(List<Reply> replies, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issue getting replies");
                }
//                for (Reply reply : replies) {
//                    // do something
//                }
                allReplies.addAll(replies);
                adapter.notifyDataSetChanged();
            }
        });
    }
}

