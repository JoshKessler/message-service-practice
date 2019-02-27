package com.practice.kessler.liltwitter.web.service;

import com.practice.kessler.liltwitter.business.service.TweetNotFoundException;
import com.practice.kessler.liltwitter.business.service.UserNotFoundException;
import com.practice.kessler.liltwitter.business.service.UserService;
import com.practice.kessler.liltwitter.data.entity.Comment;
import com.practice.kessler.liltwitter.data.entity.Tweet;
import com.practice.kessler.liltwitter.data.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(UserServiceController.class)
public class UserServiceControllerTest {

    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllUsers() throws Exception {
        User user = new User();
        user.setUserName("josh");
        User user2 = new User();
        user.setUserName("josh2");
        List<User> mockUserList = new ArrayList<>();
        mockUserList.add(user);
        mockUserList.add(user2);
        given(userService.getAllUsers()).willReturn(mockUserList);
        this.mockMvc.perform(get("/user")).andExpect(status().isOk()).andExpect(content().json("josh"));
        List<User> users = userService.getAllUsers();
    }

    @Test
    public void testGetSpecificUser() throws UserNotFoundException {
        User userExpected = userService.getUser("Jkessl204");
        assertEquals("Jkessl204", userExpected.getUserName());

        try {
            User noUserExpected = userService.getUser("nobody");
            fail();
        } catch(UserNotFoundException e){
            assertTrue(e.getMessage().contains("User not found"));
        }
    }

    @Test
    public void testGetUsersFollowers() throws UserNotFoundException {

        try {
            List<User> noUserExpected = userService.getFollowers("nobody");
            fail();
        } catch(UserNotFoundException e){
            assertTrue(e.getMessage().contains("User not found"));
        }

        List<User> zeroFollowers = userService.getFollowers("");
        assertEquals(0, zeroFollowers.size());

        List<User> userWithFollowers = userService.getFollowers("");
        assertEquals(3, userWithFollowers.size());
        boolean foundfirstFollower = false;
        boolean foundSecondFollower = false;
        for (User user : userWithFollowers) {
            if (user.getUserName().equals("firstfollower")) {
                foundfirstFollower = true;
            }
            else if (user.getUserName().equals("secondfoll")) {
                foundSecondFollower = true;
            }
        }
        assertTrue(foundfirstFollower && foundSecondFollower);
    }

    @Test
    public void testGetUsersFollowed() throws UserNotFoundException {
        try {
            List<User> noUserExpected = userService.getFollowedUsers("nobody");
            fail();
        } catch(UserNotFoundException e){
            assertTrue(e.getMessage().contains("User not found"));
        }

        List<User> zeroFollowed = userService.getFollowedUsers("");
        assertEquals(0, zeroFollowed.size());

        List<User> userWithFollowed = userService.getFollowedUsers("");
        assertEquals(3, userWithFollowed.size());
        boolean foundfirstFollowed = false;
        boolean foundSecondFollowed = false;
        for (User user : userWithFollowed) {
            if (user.getUserName().equals("firstfollower")) {
                foundfirstFollowed = true;
            }
            else if (user.getUserName().equals("secondfoll")) {
                foundSecondFollowed = true;
            }
        }
        assertTrue(foundfirstFollowed && foundSecondFollowed);
    }

    @Test
    public void testGetUsersTweets() throws UserNotFoundException {
        try {
            List<Tweet> noUserExpected = userService.getUsersTweets("nobody");
            fail();
        } catch(UserNotFoundException e){
            assertTrue(e.getMessage().contains("User not found"));
        }

        List<Tweet> zeroTweets = userService.getUsersTweets("");
        assertEquals(0, zeroTweets.size());

        List<Tweet> someTweets = userService.getUsersTweets("");
        assertEquals(3, someTweets.size());
        boolean foundfirstTweet = false;
        boolean foundSecondTweet = false;
        for (Tweet tweet : someTweets) {
            if (tweet.getTweet().contains("firstfollower")) {
                foundfirstTweet = true;
            }
            if (tweet.getTweet().contains("firstfollower")) {
                foundSecondTweet = true;
            }
        }
        assertTrue(foundfirstTweet && foundSecondTweet);
    }

    @Test
    public void testGetCommentsAssociatedWithTweet(){
        try {
            List<Comment> noTweet = userService.getCommentsAssociatedWithTweet("000");
            fail();
        } catch(TweetNotFoundException e){
            assertTrue(e.getMessage().contains("User not found"));
        }
        try {
            List<Comment> noTweet = userService.getCommentsAssociatedWithTweet("000");
            fail();
        } catch(TweetNotFoundException e){
            assertTrue(e.getMessage().contains("User not found"));
        }


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
