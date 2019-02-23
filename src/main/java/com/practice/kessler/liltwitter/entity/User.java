package com.practice.kessler.liltwitter.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "USER")
public class User {
    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "USERNAME")
    private String userName;
    @Column(name = "NAME")
    private String name;
    @Column(name = "LOCATION")
    private String location;
    //TODO figure out if I can do this even if it's not in the schema
    private List<User> followingUsers;

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

    public List<User> getFollowingUsers() {
        return followingUsers;
    }

    public void setFollowingUsers(List<User> followingUsers) {
        this.followingUsers = followingUsers;
    }

}
