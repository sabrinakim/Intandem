package com.example.intandem;

import com.example.intandem.models.Friendship;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LoginActivityTest extends TestCase {

    @Before
    public void init() {


        HashMap<String, Friendship> storedFriendsMap = new HashMap<>();
        Friendship f1 = new Friendship();
        f1.setUser1Id("0");
        f1.setUser1Id("1");
        Friendship f2 = new Friendship();
        f2.setUser1Id("0");
        f2.setUser1Id("2");
        Friendship f3 = new Friendship();
        f3.setUser1Id("0");
        f3.setUser1Id("3");
        Friendship f4 = new Friendship();
        f4.setUser1Id("0");
        f4.setUser1Id("4");
        storedFriendsMap.put("1", f1);
        storedFriendsMap.put("2", f2);
        storedFriendsMap.put("3", f3);
        storedFriendsMap.put("4", f4);
    }

    @Test
    public void NoNewFriendsTest() {
        Set<String> currFriends = new HashSet<>();
        currFriends.add("1");
        currFriends.add("2");
        currFriends.add("3");
        currFriends.add("4");
    }

}