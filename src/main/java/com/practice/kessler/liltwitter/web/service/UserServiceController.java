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

    //Ideally this would be a GET with session info used to confirm user is following tweeter
    @RequestMapping(method= POST, value = "/tweet/comments")
    public ResponseEntity getCommentsAssociatedWithTweet(@RequestBody HashMap<String, String> userData){
        try {
            return new ResponseEntity(this.userService.getCommentsAssociatedWithTweetIfFollowing(userData.get("requesterName"), userData.get("tweetId")), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //TODO should differentiate between returning a tweet list because requester not found and because requester found but not following
    public ResponseEntity getAllUserContentWithRequester(@RequestBody HashMap<String, String> userData){
        try {
            this.userService.getUser(userData.get("requesterName"));
            if (this.userService.validateUserRelationship(userData.get("requesterName"), userData.get("posterUserName"))){
                return new ResponseEntity(this.userService.getAllUserContentWithRequester(userData.get("requesterName"), userData.get("posterUserName")), HttpStatus.OK);
            } else{
                //requester not following, so return list of tweets without comments
                return returnTweetListEntity(userData.get("posterUserName"));
            }
        } catch (UserNotFoundException e) {
            //requester not found, so return list of requested user's tweets without comments
            return returnTweetListEntity(userData.get("posterUserName")); //
        }
    }

    private ResponseEntity returnTweetListEntity(String userName){
        try {

            return new ResponseEntity(this.userService.getUsersTweets(userName), HttpStatus.OK);
        } catch (UserNotFoundException err) {
            return new ResponseEntity(err.getMessage(), HttpStatus.BAD_REQUEST);
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