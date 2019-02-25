package com.practice.kessler.liltwitter.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "USER_RELATIONSHIPS")
public class UserRelationship {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "FOLLOWER_ID")
    private long followerId;
    @Column(name = "FOLLOWED_ID")
    private long followedId;

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

    public long getFollowedId() {
        return followedId;
    }

    public void setFollowedId(long followedId) {
        this.followedId = followedId;
    }
}