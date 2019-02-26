package com.practice.kessler.liltwitter.web.service;

import com.practice.kessler.liltwitter.business.service.UserService;
import com.practice.kessler.liltwitter.data.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(UserServiceController.class)
public class UserServiceControllerTest {

    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllUsers(){
        List<User> users = userService.getAllUsers();
    }

    @Test
    public void testGetSpecificUser(){
        //user exists
        //user doesn't exist
    }

    @Test
    public void testGetUsersFollowers(){
        //user doesn't exist
        //0 followers
        //1 follower
        //1+ folowers
    }

    @Test
    public void testGetUsersFollowed(){
        //user doesn't exist
        //0 followed
        //1 followed
        //1+ followed
    }

    @Test
    public void testGetUsersTweets(){
        //user doesn't exist
        //0 tweets
        //1 tweet
        //1+ tweets
    }

    @Test
    public void testGetCommentsAssociatedWithTweet(){
        //tweet doesn't exist
        //tweet exists and user doesn't follow original tweeter
        //tweet exists, user follows, 0 comments
        //1+ comments
    }

    @Test
    public void testGetAllUserContent(){
        //user doesn't exist
        //follows user, no tweets
        //follows user, tweets but no comments
        //doesn't follow user, tweets and comments
    }

    @Test
    public void testCreateUser(){
        //badly formatted data
        //username already exists
        //only supply username
        //supply username and name
        //supply username and location
        //supply username and name and location
    }

    @Test
    public void testNewTweet(){
        //user doesn't exists
        //user exists
        //maybe timestamp provided or not?
    }

    @Test
    public void testNewFollow(){
        //both exist
        //follower doesn't exist
        //followed user doesn't exist
    }

    @Test
    public void testNewComment(){
        //tweet doesn't exist
        //commenter doesn't follow original tweeter
        //tweet exists and commenter follows tweeter
    }

}
