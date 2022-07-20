package com.example.intandem;

import com.example.intandem.models.Friendship;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendshipUpdates {

    private Set<String> currFriendsList;

    public FriendshipUpdates(Set<String> currFriendsList) {
        this.currFriendsList = currFriendsList;
    }

    public Set<String> getNewFriendships(HashMap<String, Friendship> storedFriendsMap) {
        Set<String> newFriends = new HashSet<>();
        newFriends.addAll(this.currFriendsList);
        newFriends.removeAll(storedFriendsMap.keySet());
        return newFriends;
    }

    public Set<Friendship> getFriendsToDeleteInverse(HashMap<String, Friendship> deletedFriendsMap, List<Friendship> friendships) {
        Set<Friendship> toDelete = new HashSet<>();
        for (Friendship friendship : friendships) {
            if (deletedFriendsMap.containsKey(friendship.getUser1Id())) {
                toDelete.add(friendship);
            }
        }
        return toDelete;
    }

    public HashMap<String, Friendship> getFriendsToDelete(HashMap<String, Friendship> storedFriendsMap) {
        HashMap<String, Friendship> deletedFriendsMap = new HashMap<>();
        deletedFriendsMap.putAll(storedFriendsMap);
        for (String currFriend : this.currFriendsList) {
            deletedFriendsMap.remove(currFriend);
        } // friendships that didn't get removed are the friendships that are no longer in database
        // these friendships need to be deleted
        return deletedFriendsMap;
    }
}
