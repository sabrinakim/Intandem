package com.example.intandem;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.intandem.models.Friendship;
import com.parse.Parse;
import com.parse.ParseObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private FriendshipUpdates fUpdates1;

    @Before
    public void init() {
        Set<String> currFriends = new HashSet<>();
        currFriends.add("1");
        currFriends.add("2");
        currFriends.add("3");
        currFriends.add("4");
        fUpdates1 = new FriendshipUpdates(currFriends);
    }

//    @Test
//    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);
//    }

    @Test
    public void NoNewFriendsTest() {
        HashMap<String, Friendship> storedFriendsMap = new HashMap<>();
        ParseObject.registerSubclass(Friendship.class);
        Friendship f1 = new Friendship();
        f1.setUser1Id("0");
        f1.setUser1Id("1");
        Friendship f2 = ParseObject.create(Friendship.class);
        f2.setUser1Id("0");
        f2.setUser1Id("2");
        Friendship f3 =ParseObject.create(Friendship.class);
        f3.setUser1Id("0");
        f3.setUser1Id("3");
        Friendship f4 = ParseObject.create(Friendship.class);
        f4.setUser1Id("0");
        f4.setUser1Id("4");
        storedFriendsMap.put("1", f1);
        storedFriendsMap.put("2", f2);
        storedFriendsMap.put("3", f3);
        storedFriendsMap.put("4", f4);

        assertEquals(new HashSet<String>(), fUpdates1.getNewFriendships(storedFriendsMap));
    }
}