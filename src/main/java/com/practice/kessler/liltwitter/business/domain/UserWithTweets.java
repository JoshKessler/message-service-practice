package com.practice.kessler.liltwitter.business.domain;

import com.practice.kessler.liltwitter.data.entity.Comment;
import com.practice.kessler.liltwitter.data.entity.Tweet;
import com.practice.kessler.liltwitter.data.entity.User;

import java.time.ZonedDateTime;
import java.util.List;

public class UserWithTweets {
    private long id;
    private String userName;
    private String location;
    private List<TweetWithComments> tweetsWithComments;

    public UserWithTweets(User user, List<TweetWithComments> tweetsWithComments) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.location = user.getLocation();
        this.tweetsWithComments = tweetsWithComments;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<TweetWithComments> getTweetsWithComments() {
        return tweetsWithComments;
    }

    public void setContents(List<TweetWithComments> tweetsWithComments) {
        this.tweetsWithComments = tweetsWithComments;
    }

}
