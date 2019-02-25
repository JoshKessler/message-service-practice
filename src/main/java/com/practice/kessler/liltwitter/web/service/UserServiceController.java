package com.practice.kessler.liltwitter.web.service;

import com.practice.kessler.liltwitter.business.service.UserService;
import com.practice.kessler.liltwitter.data.entity.Tweet;
import com.practice.kessler.liltwitter.data.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(value="/liltwitter")
public class UserServiceController {

    @Autowired
    private UserService userService;

    @RequestMapping(method= GET, value="/user/")
    public List<User> getAllUsers(){
        return this.userService.getAllUsers();
    }

    @RequestMapping(method= GET, value = "/user/{userName}/tweets")
    public List<Tweet> getUsersTweets(@PathVariable(value="userName")String userName){
        return this.userService.getUsersTweets(userName);
    }

    @RequestMapping(method= GET, value = "/user/{userName}/followedby")
    public List<User> getUsersFollowing(@PathVariable(value="userName")String userName){
        return this.userService.getFollowers(userName);
    }

    @RequestMapping(method= GET, value = "/user/{userName}/following")
    public List<User> getUsersFollowed(@PathVariable(value="userName")String userName){
        return this.userService.getFollowedUsers(userName);
    }
}
