package com.example.intandem.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.intandem.R;
import com.parse.ParseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RepliesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RepliesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CURR_USER = "param1";
    private static final String ARG_PARAM2 = "param2";

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
}