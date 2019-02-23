package com.practice.kessler.liltwitter.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "COMMENT")
public class Comment {
    @Id
    @Column(name = "COMMENT_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "ORIGINAL_TWEET_ID")
    private long originalTweetId;
    @Column(name = "WRITTEN_BY")
    private long writtenById;
    @Column(name = "MESSAGE_TEXT")
    private String comment;
    @Column(name = "TIMESTAMP")
    private Date timestamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOriginalTweetId() {
        return originalTweetId;
    }

    public void setOriginalTweetId(long originalTweetId) {
        this.originalTweetId = originalTweetId;
    }

    public long getWrittenById() {
        return writtenById;
    }

    public void setWrittenById(long writtenById) {
        this.writtenById = writtenById;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
//TODO figure out what to do about date format

}
