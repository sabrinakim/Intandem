# Intandem

===

## Friend Finding App

## Table of Contents
1. [Overview]
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Through pictures & swipes, users can easily find people to go somewhere/do something with so they don't have to go alone (rather, **in tandem** with their friends).

### App Evaluation
- **Category:** Social
- **Mobile:** Easy access
- **Story:** Allows users to easily find buddies to go somewhere/do something with so they don't have to go alone.
- **Market:** Everyone!
- **Habit:** Users can use app anytime they're bored or want to find another person to do something with.
- **Scope:** All places

## Product Spec

### 1. User Stories

**Required Must-have Stories**

* User can login, signup, and logout through Facebook Authentication
* Users can create & post event invitations so that their friends can join them if they want
  * These posts include:
    * Location: e.g. Fondren Library
    * Duration: e.g. "the next 3 hours"
    * Picture: e.g. (picture of homework at the library)
    * Caption: e.g. "please join me at the library i'm working on comp182 hw rn T_T"
* Users can view posts created by their FB friends in their home page
* Users can reply to posts
  * Replies will consist of:
    * Picture: e.g. (selfie showing excitement)
    * Message: e.g. "omg i'm doing comp182 hw rn too i will join u"
  * Everyone who is invited to the event can see the replies
* Users can view people's replies to the post
* FILTERING (1st technically ambiguous problem)
  * Users can filter posts through a series of parameters, like location.
  * "expired" posts will disappear from feed
  * Users can only see posts from their friends in their home feed, and users posts will only be shown to their friends
* MERGING 2 APIS (2nd technically ambiguous problem)
  * App merges API responses from Google and Yelp into one response to make a custom "place" object
  * Users will be able to see Google and Yelp review for locations
  

### 2. Screen Archetypes

* Login/Signup Screen
   * user can login/signup
* Home Screen
   * Users can view posts
* Invitation Detail Screen
   * Users can see a map view of the location with markers
   * Users can see Yelp and Google reviews for that location
* Invitation Creation screen
   * Users can create their event invitation here to share with their friends
* Profile screen
   * Users can logout and view their profile picture here

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home Screen
* Profile Screen

**Flow Navigation** (Screen to Screen)

* Invitation (on Home screen) -> (swipe left) invitation details
* Invitation (on Home screen) -> (double tap) reply to your friend's invitation 

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="intandem_wireframe.jpeg" width=1000>

## Schema
### Models

Place:

| Property | Type | Description |
| -------- | ------ | --------------------------- |
| objectId | String | unique id for the place |
| name | String | name for the location |
| gPlaceId | String | Google Place ID for the location |
| address | String | address |
| rating | double | averaged rating across Google & Yelp |
| price | String | price level (e.g. "$$") |

Post:

| Property | Type | Description |
| -------- | ------ | --------------------------- |
| objectId | String | unique id for the post |
| author | Pointer to User | invitation author |
| picture | File | picture accompanying the invitation |
| caption | String | invitation caption by author |
| place | Pointer to Place| location that this event will take place |
| time | DateTime | date/time that this post will expire |

Reply:

| Property | Type | Description |
| -------- | ------ | --------------------------- |
| objectId | String | unique id for the reply |
| author | Pointer to User | author of this reply |
| picture | File | picture for this reply |
| caption | String | message for this reply |
| post | Pointer to Post | post that this reply is replying to |

Review: 

| Property | Type | Description |
| -------- | ------ | --------------------------- |
| objectId | String | unique id for the reviews |
| post | Pointer to Place | place this review corresponds to |
| source | String | "Yelp"/"Google" |

User:

| Property | Type | Description |
| -------- | ------ | --------------------------- |
| objectId | String | unique id for the user |
| username | String | user's username |
| password | String | user's password |
| profilePicUrl | String | URL of user's FB profile picture |

Friendships:

| Property | Type | Description |
| -------- | ------ | --------------------------- |
| user1 | Pointer to User | one side of friendship |
| user2 | Pointer to User | other end of friendship |

