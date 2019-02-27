package com.practice.kessler.liltwitter.business.domain;

import com.practice.kessler.liltwitter.data.entity.Comment;
import com.practice.kessler.liltwitter.data.entity.Tweet;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

public class TweetWithComments {

    private long id;
    private long userId;
    private String tweet;
    private ZonedDateTime timestamp;
    private List<Comment> comments;

    public TweetWithComments(Tweet tweet, List<Comment> comments) {
        this.id = tweet.getId();
        this.userId = tweet.getUserId();
        this.tweet = tweet.getTweet();
        this.timestamp = tweet.getTimestamp();
        this.comments = comments;
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

}
