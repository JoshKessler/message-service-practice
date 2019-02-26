package com.practice.kessler.liltwitter.web.service;

import com.practice.kessler.liltwitter.business.service.UserNotFoundException;
import com.practice.kessler.liltwitter.business.service.UserService;
import com.practice.kessler.liltwitter.data.entity.Tweet;
import com.practice.kessler.liltwitter.data.entity.User;
import com.practice.kessler.liltwitter.data.entity.UserRelationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(value="/liltwitter")
public class UserServiceController {

    @Autowired
    private UserService userService;

    @RequestMapping(method= GET, value="/user")
    public List<User> getAllUsers(){
        return this.userService.getAllUsers();
    }

    @RequestMapping(method= GET, value = "/user/{userName}/tweets")
    public ResponseEntity getUsersTweets(@PathVariable(value="userName")String userName){
        try {
            return new ResponseEntity(this.userService.getUsersTweets(userName), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method= GET, value = "/user/{userName}/followedby")
    public ResponseEntity getUsersFollowing(@PathVariable(value="userName")String userName){
        try {
            return new ResponseEntity(this.userService.getFollowers(userName), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method= GET, value = "/user/{userName}/following")
    public ResponseEntity getUsersFollowed(@PathVariable(value="userName")String userName){
        try {
            return new ResponseEntity(this.userService.getFollowedUsers(userName), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method= GET, value = "/user/{userName}")
    public ResponseEntity getUser(@PathVariable(value="userName")String userName){
        try {
            return new ResponseEntity(this.userService.getUser(userName), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //TODO what if wrong data passed?
    @RequestMapping(method= POST, value = "/new/user")
    public User createAccount(@RequestBody HashMap<String,String> userData){
        return userService.createAccount(userData.get("username"), userData.get("name"), userData.get("location"));
    }

    @RequestMapping(method= POST, value = "/follow")
    public ResponseEntity follow(@RequestBody HashMap<String,String> userData){
        try {
            return new ResponseEntity(this.userService.follow(userData.get("followerName"), userData.get("followedName")), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method= POST, value = "/comment")
    public ResponseEntity comment(@RequestBody HashMap<String,String> userData){
        try {
            return new ResponseEntity(this.userService.comment(userData.get("tweetId"), userData.get("message"), userData.get("commenterUserName")), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method= POST, value = "/tweet")
    public ResponseEntity tweet(@RequestBody HashMap<String,String> userData){
        try {
            return new ResponseEntity(this.userService.tweet(userData.get("userName"), userData.get("message"), userData.get("timestampe")), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
