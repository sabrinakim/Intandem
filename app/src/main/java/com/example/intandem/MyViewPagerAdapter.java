package com.example.intandem;

import android.location.Location;
import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.intandem.fragments.LocationFragment;
import com.example.intandem.fragments.PictureFragment;
import com.example.intandem.models.Post;
import com.parse.ParseUser;

public class MyViewPagerAdapter extends FragmentStateAdapter {

    public static final String TAG = "MyViewPagerAdapter";
    private Post currPost;
    private Location currLocation;
    private ParseUser currUser;

    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Post currPost, Location currLocation, ParseUser currUser) {
        super(fragmentActivity);
        this.currPost = currPost;
        this.currLocation = currLocation;
        this.currUser = currUser;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return PictureFragment.newInstance(currPost, currLocation, currUser);
        }
        return LocationFragment.newInstance(currPost, currLocation);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
