package com.example.intandem;

import static org.junit.Assert.assertEquals;

import com.example.intandem.models.Friendship;
import com.parse.ParseObject;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FriendshipUpdatesTest {

    private FriendshipUpdates fUpdates1;
    private FriendshipUpdates fUpdatesCurrZero;

    @Before
    public void init() {
        ParseObject.registerSubclass(Friendship.class);
        Set<String> currFriends = new HashSet<>();
        currFriends.add("1");
        currFriends.add("2");
        currFriends.add("3");
        currFriends.add("4");
        fUpdates1 = new FriendshipUpdates(currFriends);

        Set<String> noFriends = new HashSet<>();
        fUpdatesCurrZero = new FriendshipUpdates(noFriends);
    }

    @Test
    public void noNewFriendsTest() {
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

        assertEquals(new HashSet<String>(), fUpdates1.getNewFriendships(storedFriendsMap));
    }

    @Test
    public void someNewFriendsTest() {
        HashMap<String, Friendship> storedFriendsMap = new HashMap<>();
        Friendship f1 = new Friendship();
        f1.setUser1Id("0");
        f1.setUser1Id("1");
        Friendship f2 = new Friendship();
        f2.setUser1Id("0");
        f2.setUser1Id("2");
        storedFriendsMap.put("1", f1);
        storedFriendsMap.put("2", f2);

        Set<String> expected = new HashSet<>();
        expected.add("3");
        expected.add("4");

        assertEquals(expected, fUpdates1.getNewFriendships(storedFriendsMap));
    }

    @Test
    public void newFriendsTestCurrNone() {
        HashMap<String, Friendship> storedFriendsMap = new HashMap<>();
        Friendship f1 = new Friendship();
        f1.setUser1Id("0");
        f1.setUser1Id("1");
        Friendship f2 = ParseObject.create(Friendship.class);
        f2.setUser1Id("0");
        f2.setUser1Id("2");
        storedFriendsMap.put("1", f1);
        storedFriendsMap.put("2", f2);

        assertEquals(new HashSet<>(), fUpdatesCurrZero.getNewFriendships(storedFriendsMap));
    }
}