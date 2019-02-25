package com.practice.kessler.liltwitter.data.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "TWEET")
public class Tweet {
    @Id
    @Column(name = "TWEET_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "WRITTEN_BY")
    private long userId;
    @Column(name = "MESSAGE_TEXT")
    private String tweet;
    @Column(name = "TIMESTAMP")
    private Date timestamp;

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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
//TODO figure out what to do about date format

}
