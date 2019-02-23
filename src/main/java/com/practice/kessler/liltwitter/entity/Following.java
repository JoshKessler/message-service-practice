package com.practice.kessler.liltwitter.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "FOLLOWING")
public class Following {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "FOLLOWER_ID")
    private long followerId;
    @Column(name = "FOLLOWEE_ID")
    private long followeeId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(long followerId) {
        this.followerId = followerId;
    }

    public long getFolloweeId() {
        return followeeId;
    }

    public void setFolloweeId(long followeeId) {
        this.followeeId = followeeId;
    }
}
