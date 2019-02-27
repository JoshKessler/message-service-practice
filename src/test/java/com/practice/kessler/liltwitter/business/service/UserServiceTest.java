package com.practice.kessler.liltwitter.business.service;

import com.practice.kessler.liltwitter.data.entity.Comment;
import com.practice.kessler.liltwitter.data.entity.Tweet;
import com.practice.kessler.liltwitter.data.entity.User;
import com.practice.kessler.liltwitter.data.entity.UserRelationship;
import com.practice.kessler.liltwitter.web.service.UserServiceController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(UserServiceController.class)
public class UserServiceTest {

    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllUsers(){
        List<User> users = userService.getAllUsers();
        System.out.println(users);
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
    public void testGetCommentsAssociatedWithTweet() throws TweetNotFoundException {
        try {
            List<Comment> noTweet = userService.getCommentsAssociatedWithTweet("000");
            fail();
        } catch (TweetNotFoundException e) {
            assertTrue(e.getMessage().contains("User not found"));
        }

        List<Comment> zeroComments = userService.getCommentsAssociatedWithTweet("000");
        assertEquals(0, zeroComments.size());

        List<Comment> someComments = userService.getCommentsAssociatedWithTweet("");
        assertEquals(3, someComments.size());
        boolean foundfirstComment = false;
        boolean foundSecondComment = false;
        for (Comment comment : someComments) {
            if (comment.getComment().contains("firstfollower")) {
                foundfirstComment = true;
            }
            if (comment.getComment().contains("firstfollower")) {
                foundSecondComment = true;
            }
        }
        assertTrue(foundfirstComment && foundSecondComment);
    }

    @Test
    public void testGetCommentsAssociatedWithTweetIfFollowing() throws Exception {
        try {
            List<Comment> noCommenter = userService.getCommentsAssociatedWithTweetIfFollowing("nobody", "");
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("User not found"));
        }

        try {
            List<Comment> notFollowing = userService.getCommentsAssociatedWithTweetIfFollowing("", "");
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("not following"));
        }

        List<Comment> following = userService.getCommentsAssociatedWithTweetIfFollowing("", "");
        assertEquals(3, following.size());
    }

    @Test
    public void testCreateUser() throws UserNotFoundException {
        //TODO error
        User user0 = userService.createAccount("exists", null, null);

        User user1 = userService.createAccount("new1", null, null);
        assertEquals(user1.getUserName(), userService.getUser("new1").getUserName());

        User user2 = userService.createAccount("new2", "name", null);
        assertEquals("new2", userService.getUser("new2").getUserName());
        assertEquals("name", userService.getUser("new2").getName());
        assertEquals(null, userService.getUser("new2").getLocation());

        User user3 = userService.createAccount("new3", null, "location");
        assertEquals("new3", userService.getUser("new3").getUserName());
        assertEquals(null, userService.getUser("new3").getName());
        assertEquals("location", userService.getUser("new3").getLocation());

        User user4 = userService.createAccount("new4", "name", "location");
        assertEquals("new4", userService.getUser("new4").getUserName());
        assertEquals("name", userService.getUser("new4").getName());
        assertEquals("location", userService.getUser("new4").getLocation());

        User user5 = userService.createAccount("new5", "", "");
        assertEquals(null, userService.getUser("new5").getName());

        //badly formatted data
    }
    /*
        @Test
        public void testNewTweet() throws UserNotFoundException, TweetNotFoundException {
            //error
            Tweet tweet0 = userService.tweet("nobody", "Hi", null);

            //error
            Tweet tweet1 = userService.tweet("exists", "", null);

            Date before = Date.();
            Tweet tweet2 = userService.tweet("exists", "Hi", null);
            double after = System.currentTimeMillis();
            Tweet actualTweet = userService.getSpecificTweet(tweet2.getId());

            assertTrue(actualTweet.getTimestamp() >= before && actualTweet.getTimestamp() <= after);


            Tweet tweet3 = userService.tweet("exists", "Hi", "something specific");
        }
    */
    @Test
    public void testNewFollow() throws UserNotFoundException, RelationshipAlreadyExistsException {
        UserRelationship rel0 = userService.follow("nobody", "nobody");
        UserRelationship rel1 = userService.follow("exists", "nobody");
        UserRelationship rel2 = userService.follow("exists", "exists2");
        boolean updatedFollowing = false;
        boolean updatedFollowers = false;
        for (User user : userService.getFollowedUsers("exists")) {
            if (user.getUserName().equals("exists2")) {
                updatedFollowing = true;
            }
        }
        for (User user : userService.getFollowers("exists2")) {
            if (user.getUserName().equals("exists1")) {
                updatedFollowers = true;
            }
        }
        assertTrue(updatedFollowers && updatedFollowing);

    }

    @Test
    public void testNewComment() throws RelationshipNotFoundException, UserNotFoundException, TweetNotFoundException {
        Comment comm0 = userService.comment("exists", "hi", "nobody");
        Comment comm1 = userService.comment("none", "hi", "exists");
        Comment comm2 = userService.comment("exists", "hi", "doesn'tfollow");
        Comment comm3 = userService.comment("exists", "hi", "follows");

    }

    @Test
    public void testGetAllUserContentWithRequester() throws UserNotFoundException {
        userService.getAllUserContentWithRequester("exists", "none");
        userService.getAllUserContentWithRequester("none", "exists");
        userService.getAllUserContentWithRequester("exists", "nofollow");
        userService.getAllUserContentWithRequester("exists", "followsNoTweets");
        userService.getAllUserContentWithRequester("exists", "followsTweetsnoCOmments");
        userService.getAllUserContentWithRequester("exists", "followsTweetsAndComments");
    }

}
