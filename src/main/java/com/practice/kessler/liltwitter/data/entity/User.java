package com.practice.kessler.liltwitter.data.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "USER")
public class User {
    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "USERNAME")
    private String userName;
    @Column(name = "NAME")
    private String name;
    @Column(name = "LOCATION")
    private String location;
    @ManyToMany
    @JoinTable(name="USER_RELATIONSHIPS", joinColumns = @JoinColumn(name="FOLLOWER_ID"),
            inverseJoinColumns = @JoinColumn(name="FOLLOWED_ID"))
    private Set<User> followedUsers;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<User> getFollowedUsers() {
        return followedUsers;
    }

    public void setFollowedUsers(Set<User> followedUsers) {
        this.followedUsers = followedUsers;
    }

}
