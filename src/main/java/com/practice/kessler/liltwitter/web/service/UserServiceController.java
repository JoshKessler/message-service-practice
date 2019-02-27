package com.practice.kessler.liltwitter.web.service;

import com.practice.kessler.liltwitter.business.service.*;
import com.practice.kessler.liltwitter.data.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
    //Should also change the message in the RelationshipNotFoundException so it differentiates between an attempt to make comments and view them
    @RequestMapping(method= POST, value = "/tweet/comments")
    public ResponseEntity getCommentsAssociatedWithTweet(@RequestBody HashMap<String, String> userData){
        ArrayList expectedFields = new ArrayList<String>(List.of("requesterName", "tweetId"));
        ArrayList numericFields = new ArrayList<String>(List.of("tweetId"));
        try {
            checkInputs(userData, expectedFields, numericFields);
        } catch (InvalidRequestDataException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity(this.userService.getCommentsAssociatedWithTweetIfFollowing(userData.get("requesterName"), userData.get("tweetId")), HttpStatus.OK);
        } catch (RelationshipNotFoundException e){
            return new ResponseEntity("You can't view comments on this tweet because you're not following the original tweeter.", HttpStatus.BAD_REQUEST);
        }

        catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //helper method to check for bad inputs
    private void checkInputs(HashMap<String, String> inputs, List<String> expectedFieldNames, List<String> expectedNumericFieldNames) throws InvalidRequestDataException {
        for (String f : expectedFieldNames) {
            if ((inputs.get(f) == null || inputs.get(f) == "")){
                throwInputError(expectedFieldNames, expectedNumericFieldNames);
            }
        }
        for (String f : expectedNumericFieldNames) {
            try {
                Long.parseLong(inputs.get(f));
            } catch (NumberFormatException e){
                throwInputError(expectedFieldNames, expectedNumericFieldNames);
            }
        }
    }

    //helper method to format error message and throw error
    private void throwInputError(List<String> expectedFieldNames, List<String> expectedNumericFieldNames) throws InvalidRequestDataException {
        StringBuilder sb = new StringBuilder("Invalid input. The following fields must all be present and non-empty: ");
        sb.append(expectedFieldNames.toString());
        if (expectedNumericFieldNames.size() > 0){
            sb.append(". The following fields must also be integers: ");
            sb.append(expectedNumericFieldNames.toString());
        }
        throw new InvalidRequestDataException(sb.toString());
    }


    //TODO should differentiate between returning a tweet list because requester not found and because requester found but not following
    @RequestMapping(method= POST, value = "/allContentFromUser")
    public ResponseEntity getAllUserContentWithRequester(@RequestBody HashMap<String, String> userData){
        ArrayList expectedFields = new ArrayList<String>(List.of("requesterName", "posterUserName"));
        ArrayList numericFields = new ArrayList<String>();
        try {
            checkInputs(userData, expectedFields, numericFields);
        } catch (InvalidRequestDataException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
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

    //helper method
    private ResponseEntity returnTweetListEntity(String userName){
        try {
            return new ResponseEntity(this.userService.getUsersTweets(userName), HttpStatus.OK);
        } catch (UserNotFoundException err) {
            return new ResponseEntity(err.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method= POST, value = "/new/user")
    public ResponseEntity createAccount(@RequestBody HashMap<String,String> userData){
        ArrayList expectedFields = new ArrayList<String>(List.of("username"));
        //these fields are allowed to be null but should not be empty
        if ((userData.get("name")) != null){
            expectedFields.add("name");
        }
        if ((userData.get("location")) != null){
            expectedFields.add("location");
        }

        ArrayList numericFields = new ArrayList<String>();
        try {
            checkInputs(userData, expectedFields, numericFields);
        } catch (InvalidRequestDataException e) {
            return new ResponseEntity("Invalid input. Username is a mandatory field, and name and location, if present, must not be empty.", HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity(userService.createAccount(userData.get("username"), userData.get("name"), userData.get("location")), HttpStatus.OK);
        } catch (Exception e){
            if (e.getMessage().contains("constraint")){
                return new ResponseEntity("It looks like this username is already taken.", HttpStatus.BAD_REQUEST);
            }
            else return new ResponseEntity("Uh-oh, something went wrong. Instead of spending all your time on social media, why don't you go explore nature?", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method= POST, value = "/new/follow")
    public ResponseEntity follow(@RequestBody HashMap<String,String> userData){
        ArrayList expectedFields = new ArrayList<String>(List.of("followerName", "followedName"));
        ArrayList numericFields = new ArrayList<String>();
        try {
            checkInputs(userData, expectedFields, numericFields);
        } catch (InvalidRequestDataException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity(this.userService.follow(userData.get("followerName"), userData.get("followedName")), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RelationshipAlreadyExistsException e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method= POST, value = "/new/comment")
    public ResponseEntity comment(@RequestBody HashMap<String,String> userData){
        ArrayList expectedFields = new ArrayList<String>(List.of("tweetId", "message", "commenterUserName"));
        ArrayList numericFields = new ArrayList<String>(List.of("tweetId"));
        try {
            checkInputs(userData, expectedFields, numericFields);
        } catch (InvalidRequestDataException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity(this.userService.comment(userData.get("tweetId"), userData.get("message"), userData.get("commenterUserName")), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method= POST, value = "new/tweet")
    public ResponseEntity tweet(@RequestBody HashMap<String,String> userData){
        ArrayList expectedFields = new ArrayList<String>(List.of("userName", "message"));
        ArrayList numericFields = new ArrayList<String>();
        try {
            checkInputs(userData, expectedFields, numericFields);
        } catch (InvalidRequestDataException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity(this.userService.tweet(userData.get("userName"), userData.get("message")), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method= GET, value = "/user/{userName}/allContentFromAllFollowedUsers")
    public ResponseEntity getAllContentFromAllFollowedUsers(@PathVariable(value="userName")String userName){
        try {
            return new ResponseEntity(this.userService.getAllContentFromAllFollowedUsers(userName), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method= POST, value = "/new/unfollow")
    public ResponseEntity unfollow(@RequestBody HashMap<String,String> userData){
        ArrayList expectedFields = new ArrayList<String>(List.of("followerName", "followedName"));
        ArrayList numericFields = new ArrayList<String>();
        try {
            checkInputs(userData, expectedFields, numericFields);
        } catch (InvalidRequestDataException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        try {
            this.userService.unfollow(userData.get("followerName"), userData.get("followedName"));
            return new ResponseEntity("It should be a crime to say such things about someone's mother! You're no longer following them", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RelationshipNotFoundException e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}